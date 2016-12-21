package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class ListServiceBindingsRequest {

    public abstract String getServiceBrokerName();

    public abstract Optional<String> getOrgName();

    public abstract Optional<String> getSpaceName();

    public abstract Optional<String> getServiceLabel();

    public abstract Optional<String> getServicePlanName();

    public abstract Optional<String> getServiceInstanceName();
}
