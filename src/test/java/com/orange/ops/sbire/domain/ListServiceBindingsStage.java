package com.orange.ops.sbire.domain;


import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.Table;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.client.v2.servicebrokers.*;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.*;
import org.cloudfoundry.client.v2.services.*;
import org.cloudfoundry.client.v2.spaces.*;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;

/**
 * @author Sebastien Bortolussi
 */
public class ListServiceBindingsStage extends Stage<ListServiceBindingsStage> {

    public static final int TIMEOUT_SECONDS = 5;

    protected final Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);
    protected final ServiceBrokers serviceBrokers = mock(ServiceBrokers.class, RETURNS_SMART_NULLS);
    protected final Services services = mock(Services.class, RETURNS_SMART_NULLS);
    protected final ServicePlans servicePlans = mock(ServicePlans.class, RETURNS_SMART_NULLS);
    protected final ServiceBindingsV2 serviceBindingsV2 = mock(ServiceBindingsV2.class, RETURNS_SMART_NULLS);
    protected final ApplicationsV2 applicationsV2 = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);
    private final Organizations organizations = mock(Organizations.class, RETURNS_SMART_NULLS);
    private final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);
    private final DefaultServiceBindingsService serviceBindingsService = new DefaultServiceBindingsService(cloudFoundryClient);
    private Flux<ServiceBindingSummary> serviceBindingSummaryFlux;

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization, String organizationId) {
        Mockito.when(cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                        .name(organization)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListOrganizationsResponse.builder())
                                .resource(OrganizationResource.builder()
                                        .metadata(fill(Metadata.builder())
                                                .id(organizationId)
                                                .build())
                                        .entity(fill(OrganizationEntity.builder())
                                                .name(organization)
                                                .build())
                                        .build())
                                .totalPages(1)
                                .build()));
    }

    private static void requestUnknownOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        Mockito.when(cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                        .name(organization)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListOrganizationsResponse.builder())
                                .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, SpaceSummary... spaces) {
        final ListSpacesResponse.Builder responseBuilder = fill(ListSpacesResponse.builder());

        Stream.of(spaces).map(space -> fill(SpaceResource.builder())
                .metadata(fill(Metadata.builder())
                        .id(space.getId())
                        .build())
                .entity(fill(SpaceEntity.builder())
                        .organizationId(space.getOrganizationId())
                        .name(space.getName())
                        .build())
                .build())
                .forEach(responseBuilder::resource);

        Mockito.when(cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(responseBuilder.build()));
    }

    private static void requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, SpaceSummary... spaces) {
        final ListOrganizationSpacesResponse.Builder responseBuilder = fill(ListOrganizationSpacesResponse.builder());

        Stream.of(spaces).map(space -> fill(SpaceResource.builder())
                .metadata(fill(Metadata.builder())
                        .id(space.getId())
                        .build())
                .entity(fill(SpaceEntity.builder())
                        .organizationId(organizationId)
                        .name(space.getName())
                        .build())
                .build())
                .forEach(responseBuilder::resource);

        Mockito.when(cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(responseBuilder.build()));
    }

    private static void requestOrganizationSpace(CloudFoundryClient cloudFoundryClient, SpaceSummary space) {
        Mockito.when(cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                        .name(space.getName())
                        .organizationId(space.getOrganizationId())
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListOrganizationSpacesResponse.builder())
                                .resource(fill(SpaceResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id(space.getId())
                                                .build())
                                        .entity(fill(SpaceEntity.builder())
                                                .organizationId(space.getOrganizationId())
                                                .name(space.getName())
                                                .build())
                                        .build())
                                .build()));
    }

    private static void requestUnknownOrganizationSpace(CloudFoundryClient cloudFoundryClient, SpaceSummary space) {
        Mockito.when(cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                        .name(space.getName())
                        .organizationId(space.getOrganizationId())
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListOrganizationSpacesResponse.builder())
                                .build()));
    }

    private static void requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName, String serviceBrokerId) {
        Mockito.when(cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServiceBrokersResponse.builder())
                                .resource(fill(ServiceBrokerResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id(serviceBrokerId)
                                                .build())
                                        .entity(fill(ServiceBrokerEntity.builder())
                                                .build())
                                        .build())
                                .build()));
    }

    private static void requestUnknownListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        Mockito.when(cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServiceBrokersResponse.builder())
                                .build()));
    }

    private static void requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceId) {
        Mockito.when(cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                        .serviceBrokerId(serviceBrokerId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServicesResponse.builder())
                                .resource(fill(ServiceResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id(serviceId)
                                                .build())
                                        .entity(fill(ServiceEntity.builder())
                                                .build())
                                        .build())
                                .build()));
    }

    private static void requestListServiceServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        Mockito.when(cloudFoundryClient.services()
                .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServiceServicePlansResponse.builder())
                                .resource(fill(ServicePlanResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id("shared-vm")
                                                .build())
                                        .entity(fill(ServicePlanEntity.builder())
                                                .name("shared-vm")
                                                .serviceId(serviceId)
                                                .build())
                                        .build())
                                .resource(fill(ServicePlanResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id("dedicated-vm")
                                                .build())
                                        .entity(fill(ServicePlanEntity.builder())
                                                .name("dedicated-vm")
                                                .serviceId(serviceId)
                                                .build())
                                        .build())
                                .build()));
    }

    private static void requestGetService(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        Mockito.when(cloudFoundryClient.services()
                .get(GetServiceRequest.builder()
                        .serviceId(serviceId)
                        .build()))
                .thenReturn(Mono
                        .just(fill(GetServiceResponse.builder())
                                .metadata(fill(Metadata.builder())
                                        .id(serviceId)
                                        .build())
                                .entity(fill(ServiceEntity.builder())
                                        .extra("{\"displayName\":\"test-value\",\"longDescription\":\"value\",\"documentationUrl\":\"documentation-url\",\"supportUrl\":\"value\"}")
                                        .label(service)
                                        .build())
                                .build()));
    }

    private static void requestListServicePlanServiceInstances(CloudFoundryClient cloudFoundryClient, String servicePlanId, String spaceId, String serviceInstanceId, String serviceInstanceName) {
        Mockito.when(cloudFoundryClient.servicePlans()
                .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .servicePlanId(servicePlanId)
                        .spaceId(spaceId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServicePlanServiceInstancesResponse.builder())
                                .resource(fill(ServiceInstanceResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id(serviceInstanceId)
                                                .build())
                                        .entity(fill(ServiceInstanceEntity.builder())
                                                .name(serviceInstanceName)
                                                .build())
                                        .build())
                                .build()));
    }

    private static void requestListServicePlanServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String servicePlanId, String spaceId) {
        Mockito.when(cloudFoundryClient.servicePlans()
                .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .servicePlanId(servicePlanId)
                        .spaceId(spaceId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServicePlanServiceInstancesResponse.builder())
                                .build()));
    }

    private static void requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceBindingId, String serviceInstanceId, String applicationId) {
        Mockito.when(cloudFoundryClient.serviceBindingsV2()
                .list(org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest.builder()
                        .serviceInstanceId(serviceInstanceId)
                        .page(1)
                        .build()))
                .thenReturn(Mono
                        .just(fill(ListServiceBindingsResponse.builder())
                                .resource(fill(ServiceBindingResource.builder())
                                        .metadata(fill(Metadata.builder())
                                                .id(serviceBindingId)
                                                .build())
                                        .entity(fill(ServiceBindingEntity.builder())
                                                .serviceInstanceId(serviceInstanceId)
                                                .applicationId(applicationId)
                                                .build())
                                        .build())
                                .build()));
    }

    private void requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String organizationName) {
        Mockito.when(cloudFoundryClient.organizations()
                .get(GetOrganizationRequest.builder()
                        .organizationId(organizationId)
                        .build()))
                .thenReturn(Mono
                        .just(fill(GetOrganizationResponse.builder())
                                .metadata(fill(Metadata.builder())
                                        .id(organizationId)
                                        .build())
                                .entity(fill(OrganizationEntity.builder())
                                        .name(organizationName)
                                        .build())
                                .build()));
    }

    private void requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String applicationName) {
        Mockito.when(cloudFoundryClient.applicationsV2()
                .get(GetApplicationRequest.builder()
                        .applicationId(applicationId)
                        .build()))
                .thenReturn(Mono
                        .just(fill(GetApplicationResponse.builder())
                                .metadata(fill(Metadata.builder())
                                        .id(applicationId)
                                        .build())
                                .entity(fill(ApplicationEntity.builder())
                                        .name(applicationName)
                                        .build())
                                .build()));
    }

    @BeforeScenario
    public final void mockClient() {
        Mockito.when(this.cloudFoundryClient.organizations()).thenReturn(this.organizations);
        Mockito.when(this.cloudFoundryClient.serviceBindingsV2()).thenReturn(this.serviceBindingsV2);
        Mockito.when(this.cloudFoundryClient.serviceBrokers()).thenReturn(this.serviceBrokers);
        Mockito.when(this.cloudFoundryClient.servicePlans()).thenReturn(this.servicePlans);
        Mockito.when(this.cloudFoundryClient.services()).thenReturn(this.services);
        Mockito.when(this.cloudFoundryClient.spaces()).thenReturn(this.spaces);
        Mockito.when(this.cloudFoundryClient.applicationsV2()).thenReturn(this.applicationsV2);
    }

    public ListServiceBindingsStage service_bindings(@Table ImmutableServiceBindingSummary... serviceBindings) {
        requestSpaces(this.cloudFoundryClient,
                ImmutableSpaceSummary.builder().id("space11").name("space11").organizationId("org1").organizationName("org1").build(),
                ImmutableSpaceSummary.builder().id("space12").name("space12").organizationId("org2").organizationName("org2").build(),
                ImmutableSpaceSummary.builder().id("space22").name("space22").organizationId("org2").organizationName("org2").build());
        requestOrganizationSpaces(this.cloudFoundryClient, "org1",
                ImmutableSpaceSummary.builder().id("space11").name("space11").organizationId("org1").organizationName("org1").build());
        requestOrganizationSpaces(this.cloudFoundryClient, "org2",
                ImmutableSpaceSummary.builder().id("space12").name("space12").organizationId("org2").organizationName("org2").build(),
                ImmutableSpaceSummary.builder().id("space22").name("space22").organizationId("org2").organizationName("org2").build()
        );
        requestOrganizationSpaces(this.cloudFoundryClient, "org2",
                ImmutableSpaceSummary.builder().id("space12").name("space12").organizationId("org2").organizationName("org2").build(),
                ImmutableSpaceSummary.builder().id("space22").name("space22").organizationId("org2").organizationName("org2").build()
        );

        requestOrganizationSpace(this.cloudFoundryClient, ImmutableSpaceSummary.builder().id("space11").name("space11").organizationId("org1").organizationName("org1").build());
        requestOrganizationSpace(this.cloudFoundryClient, ImmutableSpaceSummary.builder().id("space12").name("space12").organizationId("org2").organizationName("org2").build());
        requestOrganizationSpace(this.cloudFoundryClient, ImmutableSpaceSummary.builder().id("space22").name("space22").organizationId("org2").organizationName("org2").build());

        requestUnknownOrganizationSpace(this.cloudFoundryClient, ImmutableSpaceSummary.builder().id("dummy").name("dummy").organizationId("org2").organizationName("org2").build());

        requestGetOrganization(this.cloudFoundryClient, "org1", "org1");
        requestGetOrganization(this.cloudFoundryClient, "org2", "org2");

        requestOrganizations(this.cloudFoundryClient, "org1", "org1");
        requestOrganizations(this.cloudFoundryClient, "org2", "org2");
        requestUnknownOrganizations(this.cloudFoundryClient, "dummy");


        requestListServiceBrokers(this.cloudFoundryClient, "p-redis", "p-redis");
        requestUnknownListServiceBrokers(this.cloudFoundryClient, "dummy");

        requestListServices(this.cloudFoundryClient, "p-redis", "p-redis");
        requestListServiceServicePlans(this.cloudFoundryClient, "p-redis");
        requestGetService(this.cloudFoundryClient, "p-redis", "p-redis");
        requestGetApplication(this.cloudFoundryClient, "my-app", "my-app");
        requestListServicePlanServiceInstances(this.cloudFoundryClient, "shared-vm", "space11", "my-redis-111", "my-redis-111");
        requestListServicePlanServiceInstances(this.cloudFoundryClient, "dedicated-vm", "space11", "my-redis-211", "my-redis-211");
        requestListServicePlanServiceInstances(this.cloudFoundryClient, "shared-vm", "space12", "my-redis-112", "my-redis-112");
        requestListServicePlanServiceInstances(this.cloudFoundryClient, "shared-vm", "space22", "my-redis-122", "my-redis-122");
        requestListServicePlanServiceInstancesEmpty(this.cloudFoundryClient, "dedicated-vm", "space12");
        requestListServicePlanServiceInstancesEmpty(this.cloudFoundryClient, "dedicated-vm", "space22");
        requestListServiceBindings(cloudFoundryClient, "aaa", "my-redis-111", "my-app");
        requestListServiceBindings(cloudFoundryClient, "bbb", "my-redis-211", "my-app");
        requestListServiceBindings(cloudFoundryClient, "ccc", "my-redis-112", "my-app");
        requestListServiceBindings(cloudFoundryClient, "ddd", "my-redis-122", "my-app");

        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings(String serviceBroker) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .build());
        return self();
    }

    public ListServiceBindingsStage she_should_get_service_bindings(@Table ImmutableServiceBindingSummary... serviceBindings) {
        serviceBindingSummaryFlux
                .collectList()
                .as(StepVerifier::create)
                .expectNext(Arrays.asList(serviceBindings))
                .expectComplete()
                .verify(Duration.ofSeconds(TIMEOUT_SECONDS));
        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings_for_org(String serviceBroker, String org) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .orgName(org)
                .build());
        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings_for_org_$_and_space(String serviceBroker, String org, String space) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .orgName(org)
                .spaceName(space)
                .build());
        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings_for_service_label(String serviceBroker, String serviceLabel) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .serviceLabel(serviceLabel)
                .build());
        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings_for_service_plan_name(String serviceBroker, String servicePlanName) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .servicePlanName(servicePlanName)
                .build());
        return self();
    }

    public ListServiceBindingsStage paas_ops_lists_service_broker_$_service_bindings_for_service_instance(String serviceBroker, String serviceInstanceName) {
        serviceBindingSummaryFlux = serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(serviceBroker)
                .serviceInstanceName(serviceInstanceName)
                .build());
        return self();
    }

    public ListServiceBindingsStage she_should_get_no_service_binding() {
        serviceBindingSummaryFlux
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(TIMEOUT_SECONDS));
        return self();
    }

    public ListServiceBindingsStage she_should_get_error(String message) {
        serviceBindingSummaryFlux
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage(message))
                .verify(Duration.ofSeconds(TIMEOUT_SECONDS));
        return self();
    }
}
