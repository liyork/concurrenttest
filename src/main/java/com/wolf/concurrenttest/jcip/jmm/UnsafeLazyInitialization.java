package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.NotThreadSafe;

/**
 * Description: Unsafe lazy initialization
 * another thread could observe a reference to a partially constructed Resource
 * 假设A先执行getInstance然后设定，B获取用, but there is no happens-before ordering between the writing of resource in A and the reading of resource in B
 * the Resource constructor changes the fields of the freshly allocated Resource from their default values to their initial values.由于没有同步可能b看到构造一半的对象
 * Created on 2021/7/13 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@NotThreadSafe
public class UnsafeLazyInitialization {
    private static Resource resource;

    public static Resource getInstance() {  // race
        if (resource == null) {
            resource = new Resource();  // unsafe publication
        }
        return resource;
    }
}
