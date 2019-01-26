package com.wolf.concurrenttest.cache;

import java.util.*;
import java.util.concurrent.*;

/**
 * Memoizer3
 * <p/>
 * Memoizing wrapper using FutureTask
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if(f == null) {//虽然保证线程来之前，能先看看是否有人已经开始计算了，但是还是不能保证
            Callable<V> eval = new Callable<V>() {
                public V call() throws InterruptedException {
                    return c.compute(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<V>(eval);//使用FutureTask可以得到阻塞语义，内部用aqs
            f = ft;//这个的语义是???---哦原来是让外层调用get的。。。
            cache.put(arg, ft);
            ft.run(); //由于本方法供多线程使用，所以没有再开线程，而是直接调用run
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }
}