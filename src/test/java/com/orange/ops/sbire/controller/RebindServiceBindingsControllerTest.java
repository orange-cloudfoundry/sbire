package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.ImmutableServiceBindingDetail;
import com.orange.ops.sbire.tags.HttpAPI;
import com.orange.ops.sbire.tags.RebindServiceBindings;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.cloudfoundry.util.test.TestObjects.fill;

/**
 * @author Sebastien Bortolussi
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = ServiceBindingsController.class, secure = false)
@EnableJGiven
@ComponentScan
@RebindServiceBindings
@HttpAPI
public class RebindServiceBindingsControllerTest extends SimpleSpringScenarioTest<RebindServiceBindingsControllerStage> {

    @Test
    public void rebind_no_filter() throws Exception {
        given().service_broker_service_bindings(fill(ImmutableServiceBindingDetail.builder())
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"test-serviceInstanceName\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}}]");
    }

    @Test
    public void rebind_by_organization_name() throws Exception {
        given().service_broker_service_bindings_for_org1(fill(ImmutableServiceBindingDetail.builder())
                .organization("org1")
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings?org_name=org1");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"test-serviceInstanceName\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"org1\"}}]");
    }

    @Test
    public void rebind_by_space_name() throws Exception {
        given().service_broker_service_bindings_for_org1_space12(fill(ImmutableServiceBindingDetail.builder())
                .organization("org1")
                .space("space12")
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings?org_name=org1&space_name=space12");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"test-serviceInstanceName\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"space12\",\"organization\": \"org1\"}}]");

    }

    @Test
    public void rebind_by_service_label() throws Exception {
        given().service_broker_service_bindings_for_service_label_p_redis(fill(ImmutableServiceBindingDetail.builder())
                .service("p-redis")
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings?service_label=p-redis");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"test-serviceInstanceName\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"test-servicePlan\",\"service\": \"p-redis\",\"space\": \"test-space\",\"organization\": \"test-organization\"}}]");
    }

    @Test
    public void rebind_by_service_plan() throws Exception {
        given().service_broker_service_bindings_for_service_plan_dedicated_vm(fill(ImmutableServiceBindingDetail.builder())
                .servicePlan("dedicated-vm")
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings?plan_name=dedicated-vm");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"test-serviceInstanceName\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"dedicated-vm\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}}]");

    }

    @Test
    public void rebind_by_service_instance() throws Exception {
        given().service_broker_service_bindings_for_service_instance_my_redis_112(fill(ImmutableServiceBindingDetail.builder())
                .serviceInstanceName("my-redis-112")
                .build());
        when().paas_ops_POST_$_to_rebind_service_bindings("/v1/service_brokers/p-redis/service_bindings?instance_name=my-redis-112");
        then().she_should_get_HTTP_$_response(HttpStatus.CREATED)
                .she_should_get_json_response("{\"next_url\":\"test-nextUrl\",\"prev_url\":\"test-previousUrl\",\"total_pages\":1,\"total_results\":1}\"resources\":[{\"entity\":{\"id\": \"test-id\",\"serviceInstanceName\": \"my-redis-112\",\"applicationName\": \"test-applicationName\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}}]");
    }

}