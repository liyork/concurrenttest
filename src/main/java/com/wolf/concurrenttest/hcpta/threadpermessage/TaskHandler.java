package com.wolf.concurrenttest.hcpta.threadpermessage;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: 处理每一个提交的Request请求
 * Created on 2021/9/25 9:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TaskHandler implements Runnable {

    private final Request request;

    public TaskHandler(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        System.out.println("Begin handle " + request);
        Utils.slowly(ThreadLocalRandom.current().nextInt(10));
        System.out.println("End handle " + request);
    }
}
