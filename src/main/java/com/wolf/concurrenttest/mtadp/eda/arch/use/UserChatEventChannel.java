package com.wolf.concurrenttest.mtadp.eda.arch.use;

import com.wolf.concurrenttest.mtadp.eda.arch.async.AsyncChannel;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class UserChatEventChannel extends AsyncChannel {

    @Override
    protected void handle(Event event) {

        UserChatEvent userChatEvent = (UserChatEvent) event;
        System.out.println("the user[" + userChatEvent.getUser().getName() +
                "] say: " + userChatEvent.getMessage());
    }
}
