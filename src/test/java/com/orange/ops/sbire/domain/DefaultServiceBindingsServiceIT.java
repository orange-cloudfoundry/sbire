package com.orange.ops.sbire.domain;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

/**
 * @author Sebastien Bortolussi
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Ignore
public class DefaultServiceBindingsServiceIT {

    public static final String SERVICE_BROKER_NAME = "p-redis";

    @Autowired
    private ServiceBindingsService serviceBindingsService;

    @Value("${test.org}")
    private String testOrgName;

    @Value("${test.space}")
    private String testSpace;

    @Test
    public void list_all_service_instances_for_service_broker_p_redis() {

        serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(SERVICE_BROKER_NAME)
                .build())
                .doOnNext(System.out::println)
                .then()
                .block(Duration.ofMinutes(5L));
    }

    @Test
    public void list_all_service_bindings_for_service_broker_p_redis_in_a_specific_org() {

        serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(SERVICE_BROKER_NAME)
                .orgName(testOrgName)
                .build())
                .doOnNext(System.out::println)
                .block(Duration.ofMinutes(5));
    }

    @Test
    public void list_all_service_bindings_for_service_broker_p_redis_in_a_specific_space() {

        serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(SERVICE_BROKER_NAME)
                .orgName(testOrgName)
                .spaceName(testSpace)
                .build())
                .doOnNext(System.out::println)
                .block(Duration.ofMinutes(5));
    }

}
