package com.wolf.concurrenttest.hcpta.threadpermessage;

/**
 * Description: 操作员
 * Created on 2021/9/25 9:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Operator {
    public void call(String business) {
        // 每个请求，创建一个线程处理
        TaskHandler taskHandler = new TaskHandler(new Request(business));
        new Thread(taskHandler).start();
    }
}
