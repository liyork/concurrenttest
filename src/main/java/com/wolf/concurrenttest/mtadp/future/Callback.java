package com.wolf.concurrenttest.mtadp.future;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/02
 */
@FunctionalInterface
public interface Callback<O> {

    void call(O o);
}
