package com.orange.ops.sbire.domain;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.client.v2.servicebindings.*;
import org.cloudfoundry.client.v2.servicebrokers.*;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.*;
import org.cloudfoundry.client.v2.services.*;
import org.cloudfoundry.client.v2.spaces.*;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@Service
public class DefaultServiceBindingsService implements ServiceBindingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceBindingsService.class);

    private static final int CF_SERVICE_ALREADY_BOUND = 90003;

    private final CloudFoundryClient cloudFoundryClient;

    private final ServiceBrokerBlacklist serviceBrokerBlacklist;

    @Autowired
    public DefaultServiceBindingsService(CloudFoundryClient cloudFoundryClient, ServiceBrokerBlacklist serviceBrokerBlacklist) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.serviceBrokerBlacklist = serviceBrokerBlacklist;
    }

    protected static Flux<ServiceInstanceResource> getSpaceServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceBroker, Optional<String> serviceLabel, Optional<String> planName, Optional<String> serviceInstanceName) {
        return getServiceBrokerByName(cloudFoundryClient, serviceBroker)
                .flatMap(resource -> getServiceBrokerServicePlans(cloudFoundryClient, ResourceUtils.getId(resource), serviceLabel, planName))
                .flatMap(plan -> getServicePlanServiceInstances(cloudFoundryClient, ResourceUtils.getId(plan), spaceId, serviceInstanceName));
    }

    private static Mono<ServiceEntity> getService(CloudFoundryClient cloudFoundryClient, Optional<String> serviceId) {
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

    private static Flux<ServicePlanResource> getServiceBrokerServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, Optional<String> serviceLabel, Optional<String> planName) {
        // return requestListServicePlans(cloudFoundryClient, serviceBrokerId)
        return requestListServices(cloudFoundryClient, serviceBrokerId)
                .filter(hasService(serviceLabel))
                .flatMap(service -> requestListServiceServicePlans(cloudFoundryClient, ResourceUtils.getId(service)))
                .filter(hasServicePlan(planName));
    }

    private static Predicate<ServicePlanResource> hasServicePlan(Optional<String> planName) {
        return servicePlan -> planName.map(ResourceUtils.getEntity(servicePlan).getName()::equals).orElse(true);
    }

    private static Predicate<ServiceResource> hasService(Optional<String> serviceLabel) {
        return service -> serviceLabel.map(ResourceUtils.getEntity(service).getLabel()::equals).orElse(true);
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

    private static Mono<GetSpaceResponse> requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
                .get(GetSpaceRequest.builder()
                        .spaceId(spaceId)
                        .build());
    }

    private static Mono<SpaceEntity> getSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return Mono
                .justOrEmpty(spaceId)
                .then(spaceId1 -> requestSpace(cloudFoundryClient, spaceId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(SpaceEntity.builder().build()));
    }

    private static Mono<ServiceBrokerResource> getServiceBrokerByName(CloudFoundryClient cloudFoundryClient, String serviceBroker) {
        return requestListServiceBrokers(cloudFoundryClient, serviceBroker)
                .single()
                .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service Broker %s does not exist", serviceBroker));
    }

    private static Mono<ServiceBrokerEntity> getServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestServiceBroker(cloudFoundryClient, serviceBrokerId)
                .map(ResourceUtils::getEntity)
                .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service Broker %s does not exist", serviceBrokerId));
    }

    private static Flux<ServiceBrokerResource> requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.serviceBrokers()
                        .list(ListServiceBrokersRequest.builder()
                                .name(serviceBrokerName)
                                .page(page)
                                .build()));
    }

    private static Mono<OrganizationResource> getOrganizationByName(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
                .singleOrEmpty();
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganizationByName(cloudFoundryClient, organization)
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

    private static ServiceBindingDetailResource toServiceBindingDetailResource(ServiceBindingResource serviceBinding, ServiceInstanceResource serviceInstance, ServicePlanEntity servicePlan, ServiceEntity service, SpaceResource space, ApplicationEntity application, OrganizationEntity organization, ServiceBrokerEntity serviceBroker) {
        return ImmutableServiceBindingDetailResource.builder()
                .entity(ImmutableServiceBindingDetail.builder()
                        .id(ResourceUtils.getId(serviceBinding))
                        .serviceInstanceId(ResourceUtils.getId(serviceInstance))
                        .serviceInstanceName(ResourceUtils.getEntity(serviceInstance).getName())
                        .service(service.getLabel())
                        .servicePlan(servicePlan.getName())
                        .applicationId(ResourceUtils.getEntity(serviceBinding).getApplicationId())
                        .applicationName(application.getName())
                        .organization(organization.getName())
                        .space(ResourceUtils.getEntity(space).getName())
                        .serviceBroker(serviceBroker.getName())
                        .build()
                )
                .metadata(Metadata.builder()
                        .from(serviceBinding.getMetadata())
                        .build()
                )
                .build();
    }

    private static ServiceBindingDetailResource toServiceBindingDetail2(CreateServiceBindingResponse serviceBinding, ServiceInstanceEntity serviceInstance, ServicePlanEntity servicePlan, ServiceEntity service, SpaceEntity space, ApplicationEntity application, OrganizationEntity organization, ServiceBrokerEntity serviceBroker) {
        return ImmutableServiceBindingDetailResource.builder()
                .entity(
                        ImmutableServiceBindingDetail.builder()
                                .id(ResourceUtils.getId(serviceBinding))
                                .serviceInstanceId(ResourceUtils.getEntity(serviceBinding).getServiceInstanceId())
                                .serviceInstanceName(serviceInstance.getName())
                                .service(service.getLabel())
                                .servicePlan(servicePlan.getName())
                                .applicationId(ResourceUtils.getEntity(serviceBinding).getApplicationId())
                                .applicationName(application.getName())
                                .organization(organization.getName())
                                .space(space.getName())
                                .serviceBroker(serviceBroker.getName())
                                .build()
                )
                .metadata(Metadata.builder()
                        .from(serviceBinding.getMetadata())
                        .build()
                )
                .build();
    }

    private static Mono<CreateServiceBindingResponse> createServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId, parameters)
                .otherwise(ExceptionUtils.statusCode(CF_SERVICE_ALREADY_BOUND), t -> Mono.empty());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId,
                                                                                  Map<String, Object> parameters) {
        return cloudFoundryClient.serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder()
                        .applicationId(applicationId)
                        .parameters(parameters)
                        .serviceInstanceId(serviceInstanceId)
                        .build());
    }

    private static Mono<Void> deleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        return requestDeleteServiceBinding(cloudFoundryClient, serviceBindingId)
                .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<DeleteServiceBindingResponse> requestDeleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        return cloudFoundryClient.serviceBindingsV2()
                .delete(DeleteServiceBindingRequest.builder()
                        .serviceBindingId(serviceBindingId)
                        .async(true)
                        .build());
    }

    private static Mono<ServiceInstanceEntity> getServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return Mono
                .justOrEmpty(serviceInstanceId)
                .then(serviceInstanceId1 -> requestGetServiceInstance(cloudFoundryClient, serviceInstanceId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(ServiceInstanceEntity.builder().build()));
    }

    private static Mono<GetServiceInstanceResponse> requestGetServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstances()
                .get(org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest.builder()
                        .serviceInstanceId(serviceInstanceId)
                        .build());
    }

    private static Mono<GetServicePlanResponse> requestGetServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return cloudFoundryClient.servicePlans()
                .get(GetServicePlanRequest.builder()
                        .servicePlanId(servicePlanId)
                        .build());
    }

    private static Mono<ServicePlanEntity> getServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return Mono
                .justOrEmpty(servicePlanId)
                .then(servicePlanId1 -> requestGetServicePlan(cloudFoundryClient, servicePlanId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(ServicePlanEntity.builder().build()));
    }

    private static Mono<OrganizationEntity> getOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return Mono
                .justOrEmpty(organizationId)
                .then(organizationId1 -> requestGetOrganization(cloudFoundryClient, organizationId1))
                .map(ResourceUtils::getEntity)
                .otherwiseIfEmpty(Mono.just(OrganizationEntity.builder().build()));
    }

    private static Mono<GetOrganizationResponse> requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
                .get(GetOrganizationRequest.builder()
                        .organizationId(organizationId)
                        .build());
    }

    private static Mono<GetServiceBrokerResponse> requestServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return cloudFoundryClient.serviceBrokers()
                .get(GetServiceBrokerRequest.builder()
                        .serviceBrokerId(serviceBrokerId)
                        .build());
    }

    protected Flux<SpaceResource> getSpaces(Optional<String> organization, Optional<String> space) {
        return organization.map(orgName -> space.map(spaceName -> getSpace(cloudFoundryClient, orgName, spaceName).flux())
                .orElse(DefaultServiceBindingsService.getOrganizationSpaces(cloudFoundryClient, orgName)))
                .orElse(DefaultServiceBindingsService.getSpaces(cloudFoundryClient));
    }

    private Mono<ApplicationEntity> getApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
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

    public Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request) {
        AtomicInteger countResources = new AtomicInteger();
        AtomicInteger countPages = new AtomicInteger();

        return serviceBrokerBlacklist
                .getServiceBrokers()
                .stream()
                .filter(request.getServiceBrokerName()::equals)
                .findFirst()
                .map(serviceBroker -> Mono.just(ImmutableListServiceBindingsResponse.builder()
                        .totalPages(0)
                        .totalResults(0)
                        .build()).cast(ListServiceBindingsResponse.class))
                .orElse(getServiceBindings(request.getServiceBrokerName(),
                        request.getOrgName(),
                        request.getSpaceName(),
                        request.getServiceLabel(),
                        request.getServicePlanName(),
                        request.getServiceInstanceName()
                ).doOnNext(binding -> {
                    countResources.incrementAndGet();
                    countPages.set(1);
                })
                        .collect(ImmutableListServiceBindingsResponse::builder, ImmutableListServiceBindingsResponse.Builder::addResources)
                        .map(builder -> builder.totalResults(countResources.get()))
                        .map(builder -> builder.totalPages(countPages.get()))
                        .map(ImmutableListServiceBindingsResponse.Builder::build));
    }

    private Flux<ServiceBindingDetailResource> getServiceBindings(String serviceBrokerName, Optional<String> orgName, Optional<String> spaceName, Optional<String> serviceLabel, Optional<String> planName, Optional<String> instanceName) {
        return getSpaces(orgName, spaceName)
                .flatMap(space -> getOrganization(cloudFoundryClient, ResourceUtils.getEntity(space).getOrganizationId())
                        .map(organization -> Tuples.of(space, organization)))
                .flatMap(function((space, organization) -> getSpaceServiceInstances(cloudFoundryClient, ResourceUtils.getId(space), serviceBrokerName, serviceLabel, planName, instanceName)
                        .map(serviceInstance -> Tuples.of(space, organization, serviceInstance))))
                .flatMap(function((space, organization, serviceInstance) -> getServicePlan(cloudFoundryClient, ResourceUtils.getEntity(serviceInstance).getServicePlanId())
                        .map(servicePlan -> Tuples.of(space, organization, serviceInstance, servicePlan))))
                .flatMap(function((space, organization, serviceInstance, servicePlan) ->
                        getServiceInstanceServiceBindings(cloudFoundryClient, ResourceUtils.getId(serviceInstance))
                                .map(serviceBinding -> Tuples.of(space, organization, serviceInstance, servicePlan, serviceBinding))))
                .flatMap(function((space, organization, serviceInstance, servicePlan, serviceBinding) ->
                        getService(cloudFoundryClient, Optional.ofNullable(servicePlan.getServiceId()))
                                .map(service -> Tuples.of(space, organization, serviceInstance, servicePlan, serviceBinding, service))))
                .flatMap(function((space, organization, serviceInstance, servicePlan, serviceBinding, service) ->
                        getApplication(cloudFoundryClient, ResourceUtils.getEntity(serviceBinding).getApplicationId())
                                .map(application -> Tuples.of(serviceBinding, serviceInstance, servicePlan, service, space, application, organization))))
                .flatMap(function((serviceBinding, serviceInstance, servicePlan, service, space, application, organization) ->
                        getServiceBroker(cloudFoundryClient, service.getServiceBrokerId())
                                .map(serviceBroker -> Tuples.of(serviceBinding, serviceInstance, servicePlan, service, space, application, organization, serviceBroker))))
                .map(function(DefaultServiceBindingsService::toServiceBindingDetailResource));

    }

    protected Flux<ServiceBindingResource> getServiceInstanceServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return requestListServiceBindings(cloudFoundryClient, serviceInstanceId);
    }

    @Override
    public Mono<RebindServiceBindingsResponse> rebind(RebindServiceBindingsRequest request) {
        AtomicInteger countResources = new AtomicInteger();
        AtomicInteger countPages = new AtomicInteger();

        return getServiceBindings(
                request.getServiceBrokerName(),
                request.getOrgName(),
                request.getSpaceName(),
                request.getServiceLabel(),
                request.getServicePlanName(),
                request.getServiceInstanceName()
        ).doOnNext(binding -> LOGGER.debug("Deleting service binding {}", binding))
                .flatMap(binding -> deleteServiceBinding(this.cloudFoundryClient, ResourceUtils.getId(binding))
                        .then(createServiceBinding(this.cloudFoundryClient, ResourceUtils.getEntity(binding).getApplicationId(), ResourceUtils.getEntity(binding).getServiceInstanceId(), null))
                        .doOnNext(newBinding -> LOGGER.debug("New service binding {} created", newBinding)))
                .flatMap(serviceBinding -> Mono.when
                        (
                                Mono.just(serviceBinding),
                                getServiceInstance(cloudFoundryClient, ResourceUtils.getEntity(serviceBinding).getServiceInstanceId()),
                                getApplication(cloudFoundryClient, ResourceUtils.getEntity(serviceBinding).getApplicationId())
                        ))
                .flatMap(function((serviceBinding, serviceInstance, application) -> Mono.when
                        (
                                Mono.just(serviceBinding),
                                Mono.just(serviceInstance),
                                getServicePlan(cloudFoundryClient, serviceInstance.getServicePlanId()),
                                getSpace(cloudFoundryClient, serviceInstance.getSpaceId()),
                                Mono.just(application)
                        )))
                .flatMap(function((serviceBinding, serviceInstance, servicePlan, space, application) ->
                        getOrganization(cloudFoundryClient, space.getOrganizationId())
                                .map(organization -> Tuples.of(serviceBinding, serviceInstance, servicePlan, application, space, organization))
                ))
                .flatMap(function((serviceBinding, serviceInstance, servicePlan, application, space, organization) ->
                        getService(cloudFoundryClient, Optional.ofNullable(servicePlan.getServiceId()))
                                .map(service -> Tuples.of(serviceBinding, serviceInstance, servicePlan, service, space, application, organization))
                ))
                .flatMap(function((serviceBinding, serviceInstance, servicePlan, service, space, application, organization) ->
                        getServiceBroker(cloudFoundryClient, service.getServiceBrokerId())
                                .map(serviceBroker -> Tuples.of(serviceBinding, serviceInstance, servicePlan, service, space, application, organization, serviceBroker))
                ))
                .map(function(DefaultServiceBindingsService::toServiceBindingDetail2))
                .doOnNext(binding -> {
                    countResources.incrementAndGet();
                    countPages.set(1);
                })
                .collect(ImmutableRebindServiceBindingsResponse::builder, ImmutableRebindServiceBindingsResponse.Builder::addResources)
                .map(builder -> builder.totalResults(countResources.get()))
                .map(builder -> builder.totalPages(countPages.get()))
                .map(ImmutableRebindServiceBindingsResponse.Builder::build);
    }


}