package com.orange.ops.sbire.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "PaaS ops rebinds service broker service bindings",
        description = "In order to broadcast a service broker update to existing service instance bindings,<br>" +
                "As a PaaS ops,<br>" +
                "I want to rebind service broker service bindings.")
@Retention(RetentionPolicy.RUNTIME)
public @interface RebindServiceBindings {

}
