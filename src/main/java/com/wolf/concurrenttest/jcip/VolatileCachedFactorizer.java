package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * Description: Caching the last result using a volatile reference to an immutable holder object
 * 一个线程修改后，对以前的线程无影响，只影响之后的使用此对象的线程
 * 外界仅仅用一个锁(防止多线程重复更新)+volatile即可保证这种线程安全(多值一起原子变动)
 * Created on 2021/6/27 3:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class VolatileCachedFactorizer extends AbstractServlet {
    //多个可变的需要原子操作的变量被封装
    //多线程获取由于volatile能保证写操作对于读操作的可见性
    private volatile OneValueCache cache = new OneValueCache(null, null);

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        // 这里没有用锁，可能会有数据出现多次，不过确实没有并发问题，因为各自线程操作的是各自缓存了，不过后来的线程确实可以利用缓存
        BigInteger[] factors = cache.getFactors(i);
        if (null == factors) {
            factors = factor(i);
            //保证对于读进程可见性
            //todo 可能暂时没有考虑到多个线程重复写入volatile变量问题
            cache = new OneValueCache(i, factors);
        }
        encodeIntoResponse(resp, factors);
    }
}
