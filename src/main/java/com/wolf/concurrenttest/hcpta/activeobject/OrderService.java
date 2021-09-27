package com.wolf.concurrenttest.hcpta.activeobject;

import com.wolf.concurrenttest.hcpta.future.Future;

/**
 * Description: 对外接口
 * Created on 2021/9/26 9:52 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface OrderService {
    //@ActiveMethod
    // 依据订单号查询订单明细
    Future<String> findOrderDetails(long orderId);

    //@ActiveMethod
    // 提交订单
    void order(String account, long orderId);
}
