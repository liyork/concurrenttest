package com.wolf.concurrenttest.mtadp.activeobject;

import com.wolf.concurrenttest.mtadp.future.MyFuture;
import com.wolf.concurrenttest.mtadp.future.TaskExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Description: 真实OrderService实现
 *
 * @author 李超
 * @date 2019/02/06
 */
public class OrderServiceImpl implements OrderService {

    @Override
    public MyFuture<String> getDetail(long orderId) {

        return TaskExecutor.<Long, String>newExecutor().submit(input -> {

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("process the orderId:" + input);

            return "the order detail information:" + input;
        }, orderId);
    }

    @Override
    public void placeOrder(String account, long orderId) {

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("process the order for account " + account + ",orderId:" + orderId);
    }
}
