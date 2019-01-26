package com.wolf.concurrenttest.actualcombat.testmaplist;

/**
 * Description:
 * <br/> Created on 2017/4/26 13:52
 *
 * @author 李超
 * @since 1.0.0
 */
public interface IInterceptor {


    boolean preHandle();

    boolean postHandle();

    void afterCompletion();
}
