package com.wolf.concurrenttest.hcpta.eda.use;

/**
 * Description:
 * Created on 2021/9/28 12:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserOfflineEvent extends UserOnlineEvent {
    public UserOfflineEvent(User user) {
        super(user);
    }
}
