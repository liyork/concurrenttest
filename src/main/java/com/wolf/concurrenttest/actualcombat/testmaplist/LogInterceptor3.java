package com.wolf.concurrenttest.actualcombat.testmaplist;

/**
 * Description:
 * <br/> Created on 2017/4/26 13:53
 *
 * @author 李超
 * @since 1.0.0
 */
@InterceptorDesc(regex = "^.*$", order = 3)
public class LogInterceptor3 implements IInterceptor {
    @Override
    public boolean preHandle() {
        System.out.println("LogInterceptor3 preHandle");
        return true;
    }

    @Override
    public boolean postHandle() {
        System.out.println("LogInterceptor3 postHandle");
        return true;
    }

    @Override
    public void afterCompletion() {
        System.out.println("LogInterceptor3 afterCompletion");
    }
}
