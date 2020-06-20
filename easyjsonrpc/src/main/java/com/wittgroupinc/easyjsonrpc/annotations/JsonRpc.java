package com.wittgroupinc.easyjsonrpc.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpc {
     String value() default "";
}
