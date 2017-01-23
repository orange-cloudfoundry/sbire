package com.orange.ops.sbire.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "PaaS ops blacklists a service broker",
        description = "In order to prevent some service broker service bindings from being listed or rebound,<br>" +
                "As a PaaS ops,<br>" +
                "I want to blacklist any service broker.")
@Retention(RetentionPolicy.RUNTIME)
public @interface BlacklistServiceBrokers {

}
