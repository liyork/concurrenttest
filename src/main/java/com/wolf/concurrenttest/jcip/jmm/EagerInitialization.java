package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: Using eager initialization, eliminates the synchronization cost incurred on each call getInstance
 * Created on 2021/7/13 12:30 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class EagerInitialization {
    private static Resource resource = new Resource();

    public static Resource getResource() {
        return resource;
    }
}
