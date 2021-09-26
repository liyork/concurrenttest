package com.wolf.concurrenttest.hcpta.twophasetermination;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * Description: 虚引用测试
 * Created on 2021/9/26 12:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PhantomReferenceTest {
    //public static void main(String[] args) throws InterruptedException {
    //    ReferenceQueue<Reference> queue = new ReferenceQueue<Reference>();
    //    PhantomReference<Reference> reference = new PhantomReference<>(new Reference(), queue);
    //    System.out.println(reference.get());  // 始终null
    //
    //    System.gc();
    //    Utils.slowly(2);
    //    // todo 这里会卡住，和ReferenceQueueTest中不一样，不能获取到
    //    java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
    //    System.out.println("gcedRef==>" + gcedRef);
    //}

    // todo 和ReferenceQueueTest一样的代码，不过改成了PhantomReference，但是remove还是阻塞。。
    public static void main(String[] args) throws InterruptedException {
        // 被回收掉的Reference，会被加入与之关联的queue中
        ReferenceQueue<Reference> queue = new ReferenceQueue<Reference>();
        Reference ref = new Reference();
        PhantomReference<Reference> reference = new PhantomReference<Reference>(ref, queue);
        ref = null;
        System.out.println("reference.get()==>" + reference.get());

        System.gc();
        Utils.slowly(1);  // make sure gc thread triggered
        // 阻塞方法
        java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
        // 被垃圾回收后，会从队列中获取到WeakReference
        System.out.println("gcedRef==>" + gcedRef);
    }
}
