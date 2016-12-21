package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

/**
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ServiceInstanceSummary {

    public abstract String getId();

    public abstract String getName();

    public abstract String getServicePlan();

    public abstract String getService();

}
