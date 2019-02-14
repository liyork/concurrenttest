package com.wolf.concurrenttest.mtadp.future;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
@FunctionalInterface
public interface Task<I, O> {

    O exe(I input);
}
