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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Bortolussi
 */
@JGivenStage
public class RebindServiceBindingsControllerStage extends Stage<RebindServiceBindingsControllerStage> {

    @Autowired
    private MockMvc mvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ServiceBindingsService serviceBindingsService;

    private ResultActions perform;

    private static ImmutableRebindServiceBindingsResponse toRebindServiceBindingsResponse(ImmutableServiceBindingDetail... serviceBindings) {

        final ImmutableRebindServiceBindingsResponse.Builder responseBuilder = fill(ImmutableRebindServiceBindingsResponse.builder());

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

    public RebindServiceBindingsControllerStage service_broker_service_bindings(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage service_broker_service_bindings_for_org1(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage service_broker_service_bindings_for_org1_space12(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .spaceName("space12")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage service_broker_service_bindings_for_service_label_p_redis(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceLabel("p-redis")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage service_broker_service_bindings_for_service_plan_dedicated_vm(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .servicePlanName("dedicated-vm")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage service_broker_service_bindings_for_service_instance_my_redis_112(@Table ImmutableServiceBindingDetail... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceInstanceName("my-redis-112")
                .build()))
                .willReturn(Mono.just(toRebindServiceBindingsResponse(serviceBindings)));
        return self();
    }

    public RebindServiceBindingsControllerStage paas_ops_POST_$_to_rebind_service_bindings(String query) throws Exception {
        perform = this.mvc.perform(post(query).accept(MediaType.APPLICATION_JSON));
        return self();
    }

    public RebindServiceBindingsControllerStage she_should_get_HTTP_$_response(HttpStatus httpStatus) throws Exception {
        perform.andExpect(status().is(httpStatus.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        return self();
    }

    public RebindServiceBindingsControllerStage she_should_get_json_response(String expectedJson) throws Exception {
        perform.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        return self();
    }


}
