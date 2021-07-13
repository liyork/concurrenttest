package com.wolf.concurrenttest.jcip.atomic;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 使用原子类解决race condition，只能用于单值
 * Created on 2021/6/26 2:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class CountingFactorizer {
    private final AtomicLong count = new AtomicLong(0);

    public long getCount() {
        return count.get();
    }

    public void service(ServletRequest req, ServletResponse response) {
        count.incrementAndGet();
    }
}
