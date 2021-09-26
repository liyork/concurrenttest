package com.wolf.concurrenttest.hcpta.threadpermessage;

/**
 * Description: 客户提交的业务请求会被封装成Request
 * Created on 2021/9/25 4:24 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Request {
    private final String business;

    public Request(String business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return business;
    }
}
