package com.wolf.concurrenttest.mtadp.activeobject;

import com.wolf.concurrenttest.mtadp.future.MyFuture;
import com.wolf.concurrenttest.mtadp.future.MyFutureImpl;

import java.util.HashMap;

/**
 * Description: 静态代理，放入队列，其他线程执行调用
 *
 * @author 李超
 * @date 2019/02/06
 */
public class OrderServiceProxy implements OrderService {

    private final OrderService orderService;

    private final ActiveMessageQueue activeMessageQueue;

    public OrderServiceProxy(OrderService orderService) {

        this.orderService = orderService;

        this.activeMessageQueue = new ActiveMessageQueue();

        Thread thread = new Thread(new ActiveDaemonTask(), "ActiveDaemonTask");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public MyFuture<String> getDetail(long orderId) {

        //需要立即返回，所以构造新的future
        MyFuture<String> activeFuture = new MyFutureImpl<>();

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("activeFuture", activeFuture);

        GetDetailsMessage message = new GetDetailsMessage(params, orderService);

        activeMessageQueue.offer(message);

        return activeFuture;
    }

    @Override
    public void placeOrder(String account, long orderId) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("orderId", orderId);

        PlaceOrderMessage message = new PlaceOrderMessage(params, orderService);

        activeMessageQueue.offer(message);
    }

    class ActiveDaemonTask implements Runnable {

        @Override
        public void run() {

            for (; ; ) {
                MethodMessage methodMessage = activeMessageQueue.take();
                methodMessage.execute();
            }
        }
    }
}
