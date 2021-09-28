package com.wolf.concurrenttest.hcpta.eda.use;

import com.wolf.concurrenttest.hcpta.eda.Event;

/**
 * Description:
 * Created on 2021/9/28 12:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserOnlineEvent extends Event {
    private final User user;

    public UserOnlineEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
