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
public class UserChatEventChannel extends AsyncChannel {
    @Override
    protected void handle(Event message) {
        UserChatEvent event = (UserChatEvent) message;
        System.out.println("The User[" + event.getUser().getName() + "] say: " + event.getMessage());
    }
}
