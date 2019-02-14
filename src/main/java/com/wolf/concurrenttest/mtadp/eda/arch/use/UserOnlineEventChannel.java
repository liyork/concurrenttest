package com.wolf.concurrenttest.mtadp.eda.arch.use;

import com.wolf.concurrenttest.mtadp.eda.arch.async.AsyncChannel;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class UserOnlineEventChannel extends AsyncChannel {

    @Override
    protected void handle(Event event) {

        UserOnlineEvent userOnlineEvent = (UserOnlineEvent) event;
        System.out.println("the user[" + userOnlineEvent.getUser().getName() + "] is online.");
    }
}
