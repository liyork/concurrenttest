package com.wolf.concurrenttest.hcpta.activeobject;

import com.wolf.concurrenttest.hcpta.future.FutureTask;

/**
 * Description:
 * Created on 2021/9/26 10:01 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveFuture<T> extends FutureTask<T> {
    @Override
    public void finish(T result) {  // 改成public
        super.finish(result);
    }
}
