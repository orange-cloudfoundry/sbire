package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.ImmutableListServiceBindingsRequest;
import com.orange.ops.sbire.domain.ImmutableRebindServiceBindingsRequest;
import com.orange.ops.sbire.domain.ServiceBindingDetail;
import com.orange.ops.sbire.domain.ServiceBindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    @GetMapping(value = "/v1/service_brokers/{brokerName}/service_bindings")
    public List<ServiceBindingDetail> listServiceBindings(@PathVariable("brokerName") String brokerName,
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

    /**
     * rebind service broker service bindings.
     * Results can be filtered using following optional query parameters :
     * <p>
     * <ul>
     * <li> org_name : POST /v1/service_brokers/p-redis/service_bindings?org_name=aaa</li>
     * <li> space_name : POST /v1/service_brokers/p-redis/service_bindings?org_name=aaa&space_name=bbb</li>
     * <li> service_label : POST /v1/service_brokers/p-redis/service_bindings?service_label=aaa</li>
     * <li> plan_name : POST /v1/service_brokers/p-redis/service_bindings?plan_name=aaa</li>
     * <li> instance_name : POST /v1/service_brokers/p-redis/service_bindings?instance_name=aaa</li>
     * </ul>
     * <p>
     **/
    @PostMapping(value = "/v1/service_brokers/{brokerName}/service_bindings")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ServiceBindingDetail> rebindServiceBindings(@PathVariable("brokerName") String brokerName,
                                                            @RequestParam("org_name") Optional<String> orgName,
                                                            @RequestParam("space_name") Optional<String> spaceName,
                                                            @RequestParam("service_label") Optional<String> serviceLabel,
                                                            @RequestParam("plan_name") Optional<String> planName,
                                                            @RequestParam("instance_name") Optional<String> instanceName) {
        return serviceBindingsService.rebind(ImmutableRebindServiceBindingsRequest.builder()
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
