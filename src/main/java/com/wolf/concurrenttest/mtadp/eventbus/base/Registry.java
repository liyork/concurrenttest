package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 注册方法
 *
 * @author 李超
 * @date 2019/02/10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Registry {

    String topic() default "default-topic";
}
