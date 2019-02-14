package com.wolf.concurrenttest.mtadp.eda.arch.use;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
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
