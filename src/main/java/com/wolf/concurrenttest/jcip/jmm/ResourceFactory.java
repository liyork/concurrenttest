package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: Lazy initialization holder class idiom
 * 用静态初始化方式实现懒加载，推荐
 * uses a class whose only purpose is to initial the Resource.
 * the jvm defers initializing the ResourceHolder class until it is actually used,
 * and because the Resource is initialized with a static initializer, no additional synchronization is needed.
 * Created on 2021/7/13 12:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class ResourceFactory {
    private static class ResourceHolder {
        public static Resource resource = new Resource();
    }

    public static Resource getResource() {
        return ResourceHolder.resource;
    }
}
