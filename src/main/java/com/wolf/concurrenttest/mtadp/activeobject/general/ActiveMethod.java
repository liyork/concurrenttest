package com.wolf.concurrenttest.mtadp.activeobject.general;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 被标记的方法，异步另起线程执行。
 *
 * @author 李超
 * @date 2019/02/06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActiveMethod {
}
