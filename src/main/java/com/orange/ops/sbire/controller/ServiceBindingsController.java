package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.ImmutableListServiceBindingsRequest;
import com.orange.ops.sbire.domain.ServiceBindingSummary;
import com.orange.ops.sbire.domain.ServiceBindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * @author Sebastien Bortolussi
 */
@RestController
public class ServiceBindingsController {

    @Value("${service.bindings.timeout:5}")
    public int timeout;

    private ServiceBindingsService serviceBindingsService;

    @Autowired
    public ServiceBindingsController(ServiceBindingsService serviceBindingsService) {
        this.serviceBindingsService = serviceBindingsService;
    }

    /**
     * List service broker service bindings.
     * Results can be filtered using following optional query parameters :
     * <p>
     * <ul>
     * <li> org_name : GET /v1/service_brokers/p-redis/service_bindings?org_name=aaa</li>
     * <li> space_name : GET /v1/service_brokers/p-redis/service_bindings?org_name=aaa&space_name=bbb</li>
     * <li> service_label : GET /v1/service_brokers/p-redis/service_bindings?service_label=aaa</li>
     * <li> plan_name : GET /v1/service_brokers/p-redis/service_bindings?plan_name=aaa</li>
     * <li> instance_name : GET /v1/service_brokers/p-redis/service_bindings?instance_name=aaa</li>
     * </ul>
     * <p>
     **/
    @RequestMapping(value = "/v1/service_brokers/{brokerName}/service_bindings", method = RequestMethod.GET)
    public List<ServiceBindingSummary> listServiceBindings(@PathVariable("brokerName") String brokerName,
                                                           @RequestParam("org_name") Optional<String> orgName,
                                                           @RequestParam("space_name") Optional<String> spaceName,
                                                           @RequestParam("service_label") Optional<String> serviceLabel,
                                                           @RequestParam("plan_name") Optional<String> planName,
                                                           @RequestParam("instance_name") Optional<String> instanceName) {
        return serviceBindingsService.list(ImmutableListServiceBindingsRequest.builder()
                .serviceBrokerName(brokerName)
                .orgName(orgName)
                .spaceName(spaceName)
                .serviceLabel(serviceLabel)
                .servicePlanName(planName)
                .serviceInstanceName(instanceName)
                .build())
                .collectList()
                .block(Duration.ofMinutes(timeout));
    }

}
