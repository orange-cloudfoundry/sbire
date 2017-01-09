package com.orange.ops.sbire.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "HTTP API")
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpAPI {

}
