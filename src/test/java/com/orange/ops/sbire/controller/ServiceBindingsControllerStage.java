package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.ImmutableListServiceBindingsRequest;
import com.orange.ops.sbire.domain.ImmutableServiceBindingSummary;
import com.orange.ops.sbire.domain.ServiceBindingsService;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import reactor.core.publisher.Flux;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Bortolussi
 */
@JGivenStage
public class ServiceBindingsControllerStage extends Stage<ServiceBindingsControllerStage> {

    @Autowired
    private MockMvc mvc;

    @MockBean(answer = Answers.RETURNS_SMART_NULLS)
    private ServiceBindingsService serviceBindingsService;

    private ResultActions perform;

    public ServiceBindingsControllerStage service_broker_service_bindings(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage service_broker_service_bindings_for_org1(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage service_broker_service_bindings_for_org1_space12(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .orgName("org1")
                .spaceName("space12")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage service_broker_service_bindings_for_service_label_p_redis(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceLabel("p-redis")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage service_broker_service_bindings_for_service_plan_dedicated_vm(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .servicePlanName("dedicated-vm")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage service_broker_service_bindings_for_service_instance_my_redis_112(@Table ImmutableServiceBindingSummary... serviceBindings) {
        BDDMockito.given(this.serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName("p-redis")
                .serviceInstanceName("my-redis-112")
                .build()))
                .willReturn(Flux.just(serviceBindings));
        return self();
    }

    public ServiceBindingsControllerStage paas_ops_lists_service_broker_service_bindings(String query) throws Exception {
        perform = this.mvc.perform(get(query).accept(MediaType.APPLICATION_JSON));
        return self();
    }

    public ServiceBindingsControllerStage she_should_get_HTTP_$_response(HttpStatus httpStatus) throws Exception {
        perform.andExpect(status().is(httpStatus.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        return self();
    }

    public ServiceBindingsControllerStage she_should_get_json_response(String expectedJson) throws Exception {
        perform.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        return self();
    }
}
