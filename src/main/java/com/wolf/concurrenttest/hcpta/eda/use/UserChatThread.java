package com.wolf.concurrenttest.hcpta.eda.use;

import com.wolf.concurrenttest.hcpta.eda.async.AsyncEventDispatcher;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/28 12:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserChatThread extends Thread {
    private final User user;
    private final AsyncEventDispatcher dispatcher;

    public UserChatThread(User user, AsyncEventDispatcher dispatcher) {
        super(user.getName());
        this.user = user;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            dispatcher.dispatch(new UserOnlineEvent(user));
            for (int i = 0; i < 5; i++) {
                dispatcher.dispatch(new UserChatEvent(user, getName() + "-hello-" + i));
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dispatcher.dispatch(new UserOfflineEvent(user));
        }
    }
}
