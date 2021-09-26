package com.wolf.concurrenttest.hcpta.twophasetermination;

/**
 * Description: 内存占用对象
 * Created on 2021/9/26 7:26 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Reference {
    // 1M
    private final byte[] data = new byte[2 << 19];

    // 会在gc的标记阶段被调用
    @Override
    protected void finalize() throws Throwable {
        System.out.println("the reference will be gc");
    }
}
