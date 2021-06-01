package com.wolf.concurrenttest.netty.inaction.asyncdemo.future;

import com.wolf.concurrenttest.netty.inaction.asyncdemo.callback.Data;

import java.util.concurrent.Future;

/**
 * Description: 用future的fetcher
 * Created on 2021/6/1 10:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Fetcher {
    Future<Data> fetchData();
}
