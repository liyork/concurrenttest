package com.wolf.concurrenttest.hcpta.eda.use;

/**
 * Description:
 * Created on 2021/9/28 12:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UserChatEvent extends UserOnlineEvent {
    private final String message;

    public UserChatEvent(User user, String message) {
        super(user);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
