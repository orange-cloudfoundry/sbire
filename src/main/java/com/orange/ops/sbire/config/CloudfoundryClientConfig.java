/*
 * <!--
 *
 *     Copyright (C) 2015 Orange
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 * -->
 */

package com.orange.ops.sbire.config;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * @author Sebastien Bortolussi
 */
@Configuration

public class CloudfoundryClientConfig {

    @Autowired
    CloudFoundryClientSettings cloudFoundryClientSettings;

    @Bean
    DefaultConnectionContext connectionContext(CloudFoundryClientSettings settings) {

        DefaultConnectionContext.Builder connectionContext = DefaultConnectionContext.builder()
                .apiHost(settings.getHost())
                .sslHandshakeTimeout(Duration.ofSeconds(30))
                .skipSslValidation(settings.getSkipSslValidation());

        if (StringUtils.hasText(settings.getProxyHost())) {
            ProxyConfiguration.Builder proxyConfiguration = ProxyConfiguration.builder()
                    .host(settings.getProxyHost())
                    .port(settings.getProxyPort());

            if (StringUtils.hasText(settings.getProxyUsername())) {
                proxyConfiguration
                        .password(settings.getProxyPassword())
                        .username(settings.getProxyUsername());
            }

            connectionContext.proxyConfiguration(proxyConfiguration.build());
        }
        return connectionContext.build();
    }

    @Bean
    PasswordGrantTokenProvider tokenProvider(CloudFoundryClientSettings cloudFoundryClientSettings) {
        return PasswordGrantTokenProvider.builder()
                .password(cloudFoundryClientSettings.getPassword())
                .username(cloudFoundryClientSettings.getUser())
                .build();
    }

    @Bean
    CloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();
    }

    @Bean
    CloudFoundryOperations cloudFoundryOperations(CloudFoundryClientSettings settings, CloudFoundryClient cloudFoundryClient) {
        return DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .organization(settings.getOrganization())
                .space(settings.getSpace())
                .build();
    }


}
