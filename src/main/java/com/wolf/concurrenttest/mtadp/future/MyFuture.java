package com.wolf.concurrenttest.mtadp.future;

/**
 * Description: 相当于凭证,不必一直等待，有时间再来取
 * <p>
 * 场景：
 * 当某个任务运行需要较长时间，调用线程在提交任务之后一直等待还占用cpu，对CPU资源是一种浪费，这时可以做其他任务，有结果
 * 时自己再取或者通过回调被动知道。
 * <p>
 * CompletableFuture
 *
 * @author 李超
 * @date 2019/02/01
 */
public interface MyFuture<T> {

    //阻塞直到有结果才能返回
    T get() throws InterruptedException;

    //todo 可以带有超时的get

    //todo cancel功能

    boolean isDone();

    void finish(T result);
}
