package com.wolf.concurrenttest.mtadp.eventbus.base;

import java.lang.reflect.Method;

/**
 * Description: 内部使用
 *
 * @author 李超
 * @date 2019/02/11
 */
class Register {

    private final Object registerObject;

    private final Method registerMethod;

    private boolean disable = false;

    Register(Object registerObject, Method registerMethod) {
        this.registerObject = registerObject;
        this.registerMethod = registerMethod;
    }

    Object getRegisterObject() {
        return registerObject;
    }

    Method getRegisterMethod() {
        return registerMethod;
    }

    boolean isDisable() {
        return disable;
    }

    void setDisable(boolean disable) {
        this.disable = disable;
    }
}
