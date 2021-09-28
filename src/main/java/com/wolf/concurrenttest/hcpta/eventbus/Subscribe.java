package com.wolf.concurrenttest.hcpta.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 用于订阅者的方法上
 * Created on 2021/9/27 9:06 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    String topic() default "default-topic";
}
