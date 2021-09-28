package com.wolf.concurrenttest.hcpta.eda.use;

import com.wolf.concurrenttest.hcpta.eda.async.AsyncEventDispatcher;

/**
 * Description:
 * Created on 2021/9/28 12:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserChatApplication {
    public static void main(String[] args) throws InterruptedException {
        final AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
        // 三个处理器
        dispatcher.registerChannel(UserOnlineEvent.class, new UserOnlineEventChannel());
        dispatcher.registerChannel(UserOfflineEvent.class, new UserOfflineEventChannel());
        dispatcher.registerChannel(UserChatEvent.class, new UserChatEventChannel());

        // 三个发送者，产生各种事件，让上面处理器处理
        UserChatThread thread1 = new UserChatThread(new User("leo"), dispatcher);
        thread1.start();
        UserChatThread thread2 = new UserChatThread(new User("alex"), dispatcher);
        thread2.start();
        UserChatThread thread3 = new UserChatThread(new User("tina"), dispatcher);
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
        dispatcher.shutdown();
    }
}
