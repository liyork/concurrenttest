package com.wolf.concurrenttest.hcpta.eda.use;

import com.wolf.concurrenttest.hcpta.eda.Event;
import com.wolf.concurrenttest.hcpta.eda.async.AsyncChannel;

/**
 * Description:
 * Created on 2021/9/28 12:23 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserOfflineEventChannel extends AsyncChannel {
    @Override
    protected void handle(Event message) {
        UserOfflineEvent event = (UserOfflineEvent) message;
        System.out.println("The User[" + event.getUser().getName() + "] is offline");
    }
}
