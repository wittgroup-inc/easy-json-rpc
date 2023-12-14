package com.gowittgroup.easyjsonrpc.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcParam {
    String value() default "";
}
