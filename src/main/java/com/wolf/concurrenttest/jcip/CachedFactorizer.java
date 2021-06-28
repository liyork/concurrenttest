package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * Description: 将锁范围减小
 * Servlet that caches its last request and result
 * provides a balance between simplicity and concurrency.
 * Created on 2021/6/26 3:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class CachedFactorizer extends AbstractServlet {

    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;
    @GuardedBy("this")
    private long hits;//统计访问数
    @GuardedBy("this")
    private long cacheHits;

    //由于++操作涉及三步，都有可能乱序，所以加锁。
    public synchronized long getHits() {
        return hits;
    }

    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits;
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;
        synchronized (this) {
            ++hits;
            //两个操作是原子的，不然有可能cpu调度乱序，导致b线程仅放入了lastNumber，而a线程判断ok则返回lastFactors，就失败了。
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);// 长时间执行(费时计算等)，最好不要放在锁中
            synchronized (this) {//两个操作在一起是原子的，避免race condition
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse(resp, factors);
    }
}
