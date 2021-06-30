package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Description: 将结果缓存，不断优化
 * Created on 2021/6/30 9:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CacheResult {
    interface Computable<A, V> {
        V compute(A arg) throws InterruptedException;
    }

    class ExpensiveFunction implements Computable<String, BigInteger> {
        @Override
        public BigInteger compute(String arg) throws InterruptedException {
            // after deep thought..
            return new BigInteger(arg);
        }
    }

    // using hashMap and Synchronization
    class Memorizer1<A, V> implements Computable<A, V> {
        @GuardedBy("this")
        private final Map<A, V> cache = new HashMap<>();
        private final Computable<A, V> c;

        public Memorizer1(Computable<A, V> c) {
            this.c = c;
        }

        // hashmap不安全需要用synchronized同步，但是有明显的scalability问题，只有单线程同一时间执行
        @Override
        public synchronized V compute(A arg) throws InterruptedException {
            V result = cache.get(arg);
            if (result == null) {
                result = c.compute(arg);
                cache.put(arg, result);
            }
            return result;
        }
    }

    // using ConcurrentHashMap，不过可能会有计算相同值多次的可能，就是因为对于同值，一个线程计算时可能另一线程并不能感知
    class Memorizer2<A, V> implements Computable<A, V> {
        private final Map<A, V> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        public Memorizer2(Computable<A, V> c) {
            this.c = c;
        }

        // ConcurrentHashMap本身线程安全，不用synchronized
        @Override
        public V compute(A arg) throws InterruptedException {
            V result = cache.get(arg);
            if (result == null) {
                result = c.compute(arg);
                cache.put(arg, result);
            }
            return result;
        }
    }

    // using FutureTask，可减少很大部分的对同值相同计算问题，不过还是有可能会产生，因为check-then-act sequence 并不是原子的
    class Memorizer3<A, V> implements Computable<A, V> {
        private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        public Memorizer3(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public V compute(final A arg) throws InterruptedException {
            Future<V> result = cache.get(arg);
            if (result == null) {
                Callable<V> eval = (Callable) () -> c.compute(arg);
                FutureTask<V> ft = new FutureTask<>(eval);
                result = ft;
                cache.put(arg, result);
                ft.run();// call to c.compute happens here
            }
            try {
                return result.get();
            } catch (ExecutionException e) {
                throw FutureTaskDemo.launderThrowable(e.getCause());
            }
        }
    }

    // final implementation of Memorizer
    // 不过没有处理缓存过期问题，可以通过FutureTask子类对每个实体关联过期时间，并周期扫描。
    // 也没有处理cache eviction
    class Memorizer<A, V> implements Computable<A, V> {
        private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
        private final Computable<A, V> c;

        public Memorizer(Computable<A, V> c) {
            this.c = c;
        }

        @Override
        public V compute(final A arg) throws InterruptedException {
            while (true) {
                Future<V> result = cache.get(arg);
                if (result == null) {
                    Callable<V> eval = (Callable) () -> c.compute(arg);
                    FutureTask<V> ft = new FutureTask<>(eval);
                    result = cache.putIfAbsent(arg, ft);
                    if (result == null) {  // 首次放入成功
                        result = ft;
                        ft.run();// 执行callable
                    }
                }
                try {
                    return result.get();
                } catch (CancellationException e) {// if a computation is cancelled or fails, remove it
                    cache.remove(arg, result);
                } catch (ExecutionException e) {
                    throw FutureTaskDemo.launderThrowable(e.getCause());
                }
            }
        }
    }

    // caches results using Memorizer
    @ThreadSafe
    class Factorizer extends AbstractServlet {
        private final Computable<BigInteger, BigInteger[]> c = arg -> factor(arg);
        private final Computable<BigInteger, BigInteger[]> cache = new Memorizer<>(c);

        public void service(ServletRequest req, ServletResponse resp) {
            try {
                BigInteger i = extractFromRequest(req);
                encodeIntoResponse(resp, cache.compute(i));
            } catch (InterruptedException e) {
                encodeError(resp, "factorization interrupted");
            }
        }

        private void encodeError(ServletResponse resp, String factorization_interrupted) {
        }
    }
}
