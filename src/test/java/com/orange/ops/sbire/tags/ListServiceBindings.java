package com.orange.ops.sbire.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "PaaS ops lists all service broker service bindings",
        description = "In order to have a comprehensive status of a service broker update impacts on existing related service instance bindings,<br>" +
                "As a PaaS ops,<br>" +
                "I want to list service broker service bindings.")
@Retention(RetentionPolicy.RUNTIME)
public @interface ListServiceBindings {

}
