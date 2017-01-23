package com.orange.ops.sbire.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author Sebastien Bortolussi
 */
@ConfigurationProperties(prefix = "sbire")
public class ServiceBrokerBlacklistProperties {

    /**
     * The service brokers to blacklist.
     * When a service broker has been blacklisted, any request to list or rebind service broker will result in an empty response.
     */
    public List<String> blacklist = Collections.emptyList();

    public List<String> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

}
