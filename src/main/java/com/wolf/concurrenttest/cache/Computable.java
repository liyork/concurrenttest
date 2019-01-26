package com.wolf.concurrenttest.cache;

/**
 * Description:
 * <br/> Created on 2017/6/25 9:53
 *
 * @author 李超
 * @since 1.0.0
 */
interface Computable <A, V> {
    V compute(A arg) throws InterruptedException;
}
