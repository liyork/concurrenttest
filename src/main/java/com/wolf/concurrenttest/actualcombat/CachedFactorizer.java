package com.wolf.concurrenttest.actualcombat;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * CachedFactorizer
 * <p/>
 * Servlet that caches its last request and result
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class CachedFactorizer extends GenericServlet implements Servlet {
    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;
    @GuardedBy("this")
    private long hits;//统计访问数

    //由于++操作涉及三步，都有可能乱序，所以加锁。
    public synchronized long getHits() {
        return hits;
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;
        synchronized(this) {
            ++hits;
            //两个操作是原子的，不然有可能cpu调度乱序，导致b线程仅放入了lastNumber，而a线程判断ok则返回lastFactors，就失败了。
            if(i.equals(lastNumber)) {
                factors = lastFactors.clone();
            }
        }
        if(factors == null) {
            //费时计算最好不要放在锁中
            factors = factor(i);
            //两个操作在一起是原子的
            synchronized(this) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse(resp, factors);
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
