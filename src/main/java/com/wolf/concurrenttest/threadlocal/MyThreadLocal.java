package com.wolf.concurrenttest.threadlocal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:简单猜想实现
 * 缺点:
 * ThreadLocal本意是避免并发，用一个全局Map显然违背了这一初衷，还需要同步map；
 * 用Thread当key，除非手动调用remove，否则即使线程退出了会导致：
 * 1)该Thread对象无法回收；2)该线程在所有ThreadLocal中对应的value也无法回收。
 * jdk实现刚好反过来，每个线程方放入threadloca的内部map，然后线程不用时携带的变量也一起被回收
 * <br/> Created on 2017/2/24 9:03
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyThreadLocal {

    private Map values = Collections.synchronizedMap(new HashMap());

    public Object get() {
        Thread curThread = Thread.currentThread();
        Object o = values.get(curThread);
        if(o == null && !values.containsKey(curThread)) {
            o = initialValue();
            values.put(curThread, o);
        }
        return o;
    }

    private Object initialValue() {
        return null;
    }

    public void set(Object newValue) {
        values.put(Thread.currentThread(), newValue);
    }
}
