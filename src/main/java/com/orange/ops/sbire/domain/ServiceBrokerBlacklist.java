package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ServiceBrokerBlacklist {

    public abstract List<String> getServiceBrokers();

}
