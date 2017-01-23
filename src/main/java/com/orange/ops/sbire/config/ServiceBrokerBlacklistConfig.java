package com.orange.ops.sbire.config;

import com.orange.ops.sbire.domain.ImmutableServiceBrokerBlacklist;
import com.orange.ops.sbire.domain.ServiceBrokerBlacklist;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sebastien Bortolussi
 */
public class ServiceBrokerBlacklistConfig {

    @Configuration
    protected static class DefaultEmtpyConfig {

        @Bean
        @ConditionalOnMissingBean
        ServiceBrokerBlacklist serviceBrokerBlacklist() {
            return ImmutableServiceBrokerBlacklist.builder().build();
        }

    }

    @Configuration
    @ConditionalOnProperty(value = "sbire.blacklist")
    @EnableConfigurationProperties(ServiceBrokerBlacklistProperties.class)
    protected static class WithServiceBrokerBlacklistConfig {

        @Bean
        ServiceBrokerBlacklist serviceBrokerBlacklist(ServiceBrokerBlacklistProperties properties) {
            return ImmutableServiceBrokerBlacklist.builder().addAllServiceBrokers(properties.getBlacklist()).build();
        }
    }

}
