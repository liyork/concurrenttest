package com.wolf.concurrenttest.netty.inaction.asyncdemo.callback;

/**
 * Description:
 * Created on 2021/6/1 10:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Fetcher {
    void fetchData(FetchCallback callback);
}
