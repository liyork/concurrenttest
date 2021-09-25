package com.wolf.concurrenttest.hcpta.future;

/**
 * Description: 凭据
 * Created on 2021/9/24 10:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Future<T> {
    // 返回计算后的结果，若此时没有则陷入阻塞
    T get() throws InterruptedException;

    // 是否完成
    boolean done();
}
