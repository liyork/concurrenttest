package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: Thread-safe lazy initialization
 * 使用synchronized保证线程安全
 * because the code path through getInstance is fairly short(a test and a predicted branch), if getInstance is not called
 * frequently by many threads, there is a little enough contention for the SafeLazyInitialization lock that
 * this approach offers adequate performance
 * Created on 2021/7/13 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class SafeLazyInitialization {
    private static Resource resource;

    public synchronized static Resource getInstance() {
        if (resource == null) {
            resource = new Resource();
        }
        return resource;
    }
}
