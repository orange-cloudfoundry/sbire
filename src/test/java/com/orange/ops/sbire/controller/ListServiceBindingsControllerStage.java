package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.*;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.cloudfoundry.client.v2.Metadata;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Bortolussi
 */
@JGivenStage
public class ListServiceBindingsControllerStage extends Stage<ListServiceBindingsControllerStage> {

    @Autowired
    private MockMvc mvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS, name = "list")
    private ServiceBindingsService serviceBindingsService;

    private ResultActions perform;

    private static ImmutableListServiceBindingsResponse toListServiceBindingsResponse(ImmutableServiceBindingDetail... serviceBindings) {

        final ImmutableListServiceBindingsResponse.Builder responseBuilder = fill(ImmutableListServiceBindingsResponse.builder());

        Stream.of(serviceBindings)
                .map(serviceBinding -> ImmutableServiceBindingDetailResource.builder()
                        .metadata(fill(Metadata.builder())
                                .id(serviceBinding.getId())
                                .build())
                        .entity(fill(ImmutableServiceBindingDetail.builder()).build())
                        .build()
                )
                .forEach(responseBuilder::addResources);

        return responseBuilder.totalResults(1).build();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings_for_org1(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings_for_org1_space12(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .spaceName("space12")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings_for_service_label_p_redis(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceLabel("p-redis")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings_for_service_plan_dedicated_vm(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .servicePlanName("dedicated-vm")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage service_broker_service_bindings_for_service_instance_my_redis_112(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceInstanceName("my-redis-112")
                .build()))
                .willReturn(Mono.just(toListServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public ListServiceBindingsControllerStage paas_ops_lists_service_broker_service_bindings(String query) throws Exception {
        perform = this.mvc.perform(get(query).accept(MediaType.APPLICATION_JSON));
        return self();
    }

    public ListServiceBindingsControllerStage she_should_get_HTTP_$_response(HttpStatus httpStatus) throws Exception {
        perform.andExpect(status().is(httpStatus.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        return self();
    }

    public ListServiceBindingsControllerStage she_should_get_json_response(String expectedJson) throws Exception {
        perform.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        return self();
    }


}
