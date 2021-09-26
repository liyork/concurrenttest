package com.wolf.concurrenttest.hcpta.threadpermessage;

import com.wolf.concurrenttest.hcpta.threadpool.BasicThreadPool;
import com.wolf.concurrenttest.hcpta.threadpool.ThreadPool;

/**
 * Description: 操作员，线程池处理
 * Created on 2021/9/25 9:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Operator1 {
    private final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);

    public void call(String business) {
        TaskHandler taskHandler = new TaskHandler(new Request(business));
        threadPool.execute(taskHandler);
    }
}
