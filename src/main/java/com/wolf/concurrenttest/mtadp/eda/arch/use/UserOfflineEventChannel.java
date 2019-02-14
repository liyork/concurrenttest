package com.wolf.concurrenttest.mtadp.eda.arch.use;

import com.wolf.concurrenttest.mtadp.eda.arch.async.AsyncChannel;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class UserOfflineEventChannel extends AsyncChannel {

    @Override
    protected void handle(Event event) {

        UserOfflineEvent userOfflineEvent = (UserOfflineEvent) event;
        System.out.println("the user[" + userOfflineEvent.getUser().getName() + "] is offline.");
    }
}
