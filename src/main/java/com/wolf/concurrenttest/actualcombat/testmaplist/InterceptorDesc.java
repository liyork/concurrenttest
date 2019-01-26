package com.wolf.concurrenttest.actualcombat.testmaplist;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorDesc {

    String regex();

    int order() default Integer.MAX_VALUE;

}
