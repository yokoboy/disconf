package com.baidu.disconf.web.service.user.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Kail on 2017/6/16.
 */
@Target(value = {ElementType.METHOD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Auth {

    String value() default "";

    String optId() default "";

}
