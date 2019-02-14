package com.wolf.concurrenttest.mtadp.activeobject;

import com.wolf.concurrenttest.mtadp.activeobject.general.ActiveMethod;
import com.wolf.concurrenttest.mtadp.future.MyFuture;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/06
 */
public interface OrderService {

    //异步得到结果
    @ActiveMethod
    MyFuture<String> getDetail(long orderId);

    @ActiveMethod
    void placeOrder(String account, long orderId);
}
