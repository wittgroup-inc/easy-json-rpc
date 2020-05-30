package com.wittgroupinc.easyjsonrpc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpc {
     String value() default "";
}
