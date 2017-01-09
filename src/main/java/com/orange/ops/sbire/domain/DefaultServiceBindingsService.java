package com.orange.ops.sbire.domain;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.*;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@Service
public class DefaultServiceBindingsService implements ServiceBindingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceBindingsService.class);

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    public DefaultServiceBindingsService(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    protected static Flux<ServiceInstanceSummary> getSpaceServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceBroker, Optional<String> serviceLabel, Optional<String> planName, Optional<String> serviceInstanceName) {
        return getServiceBroker(cloudFoundryClient, serviceBroker)
                .flatMap(resource -> getServiceBrokerServicePlans(cloudFoundryClient, ResourceUtils.getId(resource), serviceLabel, planName))
                .flatMap(plan -> getServicePlanServiceInstances(cloudFoundryClient, plan.getId(), spaceId, serviceInstanceName)
                        .map(serviceInstance -> toServiceInstanceSummary(serviceInstance, plan)));
    }

    private static ServiceInstanceSummary toServiceInstanceSummary(ServiceInstanceResource serviceInstance, ServicePlanSummary plan) {
        return ImmutableServiceInstanceSummary.builder()
                .id(ResourceUtils.getId(serviceInstance))
                .service(plan.getService())
                .servicePlan(plan.getName())
                .name(ResourceUtils.getEntity(serviceInstance).getName())
                .build();
    }

    private static Mono<ServiceEntity> getServiceEntity(CloudFoundryClient cloudFoundryClient, Optional<String> serviceId) {
        return Mono
                .justOrEmpty(serviceId)
                .then(serviceId1 -> requestGetService(cloudFoundryClient, serviceId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(ServiceEntity.builder().build()));
    }

    private static Mono<GetServiceResponse> requestGetService(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return cloudFoundryClient.services()
                .get(GetServiceRequest.builder()
                        .serviceId(serviceId)
                        .build());
    }

    private static Flux<ServiceInstanceResource> getServicePlanServiceInstances(CloudFoundryClient cloudFoundryClient, String servicePlanId, String spaceId, Optional<String> serviceInstanceName) {
        return requestListServicePlanServiceInstances(cloudFoundryClient, servicePlanId, spaceId)
                .filter(serviceInstance -> serviceInstanceName
                        .map(ResourceUtils.getEntity(serviceInstance).getName()::equals)
                        .orElse(true));
    }

    private static Flux<ServiceInstanceResource> requestListServicePlanServiceInstances(CloudFoundryClient cloudFoundryClient, String servicePlanId, String spaceId) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                        .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                                .page(page)
                                .servicePlanId(servicePlanId)
                                .spaceId(spaceId)
                                .build()));
    }

    private static Flux<ServicePlanSummary> getServiceBrokerServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, Optional<String> serviceLabel, Optional<String> planName) {
        // return requestListServicePlans(cloudFoundryClient, serviceBrokerId)
        return requestListServices(cloudFoundryClient, serviceBrokerId)
                .flatMap(service -> requestListServiceServicePlans(cloudFoundryClient, ResourceUtils.getId(service)))
                .flatMap(resource -> Mono
                        .when(
                                Mono.just(resource),
                                getServiceEntity(cloudFoundryClient, Optional.ofNullable(ResourceUtils.getEntity(resource).getServiceId()))))
                .map(function(DefaultServiceBindingsService::toServicePlanSummary))
                .filter(hasService(serviceLabel))
                .filter(hasServicePlan(planName));
    }

    private static Predicate<ServicePlanSummary> hasServicePlan(Optional<String> planName) {
        return servicePlanSummary -> planName.map(servicePlanSummary.getName()::equals).orElse(true);
    }

    private static Predicate<ServicePlanSummary> hasService(Optional<String> serviceLabel) {
        return servicePlanSummary -> serviceLabel.map(servicePlanSummary.getService()::equals).orElse(true);
    }

    private static ServicePlanSummary toServicePlanSummary(ServicePlanResource resource, ServiceEntity serviceEntity) {
        return ImmutableServicePlanSummary.builder()
                .id(ResourceUtils.getId(resource))
                .name(ResourceUtils.getEntity(resource).getName())
                .service(serviceEntity.getLabel())
                .build();
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils.
                requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                        .list(ListServicePlansRequest.builder()
                                .serviceBrokerId(serviceBrokerId)
                                .page(page)
                                .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils.
                requestClientV2Resources(page -> cloudFoundryClient.services()
                        .list(ListServicesRequest.builder()
                                .serviceBrokerId(serviceBrokerId)
                                .page(page)
                                .build()));
    }

    private static Flux<ServicePlanResource> requestListServiceServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils.
                requestClientV2Resources(page -> cloudFoundryClient.services()
                        .listServicePlans(ListServiceServicePlansRequest.builder()
                                .serviceId(serviceId)
                                .page(page)
                                .build()));
    }

    static Flux<SpaceResource> getSpaces(CloudFoundryClient cloudFoundryClient) {
        return requestSpaces(cloudFoundryClient);
    }

    static Flux<SpaceResource> getOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organization) {
        return Mono.when(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, organization)
        ).flatMap(function(DefaultServiceBindingsService::requestOrganizationSpaces));
    }

    static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organization, String space) {
        return Mono.when(Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, organization),
                Mono.just(space)
        ).flatMap(function(DefaultServiceBindingsService::requestOrganizationSpace))
                .singleOrEmpty();
    }

    private static Flux<SpaceResource> requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                                .organizationId(organizationId)
                                .page(page)
                                .build()));
    }

    private static Flux<SpaceResource> requestOrganizationSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                                .name(space)
                                .organizationId(organizationId)
                                .page(page)
                                .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                                .page(page)
                                .build()));
    }

    private static Mono<ServiceBrokerResource> getServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBroker) {
        return requestListServiceBrokers(cloudFoundryClient, serviceBroker)
                .single()
                .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service Broker %s does not exist", serviceBroker));
    }

    private static Flux<ServiceBrokerResource> requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
                        .list(ListServiceBrokersRequest.builder()
                                .name(serviceBrokerName)
                                .page(page)
                                .build()));
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
                .singleOrEmpty();
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
                .map(ResourceUtils::getId);
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                        .list(ListOrganizationsRequest.builder()
                                .name(organizationName)
                                .page(page)
                                .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils.
                requestClientV2Resources(page -> cloudFoundryClient.serviceBindingsV2()
                        .list(org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest.builder()
                                .serviceInstanceId(serviceInstanceId)
                                .page(page)
                                .build()));
    }

    private static SpaceSummary toSpaceSummary(SpaceResource spaceResource, OrganizationEntity organization) {
        return ImmutableSpaceSummary.builder()
                .id(ResourceUtils.getId(spaceResource))
                .organizationName(organization.getName())
                .organizationId(ResourceUtils.getEntity(spaceResource).getOrganizationId())
                .name(ResourceUtils.getEntity(spaceResource).getName())
                .build();
    }

    private static ServiceBindingSummary toServiceBindingSummary(ServiceBindingResource serviceBinding, ServiceInstanceSummary serviceInstance, SpaceSummary space, ApplicationEntity application) {
        return ImmutableServiceBindingSummary.builder()
                .id(ResourceUtils.getId(serviceBinding))
                .serviceInstance(serviceInstance.getName())
                .service(serviceInstance.getService())
                .servicePlan(serviceInstance.getServicePlan())
                .application(application.getName())
                .organization(space.getOrganizationName())
                .space(space.getName())
                .build();
    }

    protected Flux<SpaceSummary> getSpaces(Optional<String> organization, Optional<String> space) {
        return organization.map(orgName -> space.map(spaceName -> getSpace(cloudFoundryClient, orgName, spaceName).flux())
                .orElse(DefaultServiceBindingsService.getOrganizationSpaces(cloudFoundryClient, orgName)))
                .orElse(DefaultServiceBindingsService.getSpaces(cloudFoundryClient))
                .flatMap(spaceResource -> Mono.when
                        (
                                Mono.just(spaceResource),
                                getOrganizationEntity(cloudFoundryClient, ResourceUtils.getEntity(spaceResource).getOrganizationId())
                        ))
                .map(function(DefaultServiceBindingsService::toSpaceSummary));
    }

    private Mono<OrganizationEntity> getOrganizationEntity(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return Mono
                .justOrEmpty(organizationId)
                .then(organizationId1 -> requestGetOrganization(cloudFoundryClient, organizationId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(OrganizationEntity.builder().build()));
    }

    private Mono<GetOrganizationResponse> requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
                .get(GetOrganizationRequest.builder()
                        .organizationId(organizationId)
                        .build());
    }

    private Mono<ApplicationEntity> getApplicationEntity(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return Mono
                .justOrEmpty(applicationId)
                .then(applicationId1 -> requestGetApplication(cloudFoundryClient, applicationId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(ApplicationEntity.builder().build()));
    }

    private Mono<GetApplicationResponse> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
                .get(GetApplicationRequest.builder()
                        .applicationId(applicationId)
                        .build());
    }

    public Flux<ServiceBindingSummary> list(ListServiceBindingsRequest request) {
        return getServiceBindings(request.getServiceBrokerName(),
                request.getOrgName(),
                request.getSpaceName(),
                request.getServiceLabel(),
                request.getServicePlanName(),
                request.getServiceInstanceName());
    }

    private Flux<ServiceBindingSummary> getServiceBindings(String serviceBrokerName, Optional<String> orgName, Optional<String> spaceName, Optional<String> serviceLabel, Optional<String> planName, Optional<String> instanceName) {
        return getSpaces(orgName, spaceName)
                .flatMap(space -> getSpaceServiceInstances(cloudFoundryClient, space.getId(), serviceBrokerName, serviceLabel, planName, instanceName)
                        .flatMap(serviceInstance -> getServiceInstanceServiceBindings(cloudFoundryClient, serviceInstance.getId())
                                .flatMap(serviceBinding -> getApplicationEntity(cloudFoundryClient, ResourceUtils.getEntity(serviceBinding).getApplicationId())
                                        .map(application -> toServiceBindingSummary(serviceBinding, serviceInstance, space, application)))));
    }

    protected Flux<ServiceBindingResource> getServiceInstanceServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return requestListServiceBindings(cloudFoundryClient, serviceInstanceId);
    }

}