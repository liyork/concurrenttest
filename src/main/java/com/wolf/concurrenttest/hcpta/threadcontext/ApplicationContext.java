package com.wolf.concurrenttest.hcpta.threadcontext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 应用上下文
 * 若runtimeInfo、configuration生命周期会随着被创建一直到系统运行结束，就可以将ApplicationContext称为系统上下文
 * runtimeInfo、configuration称为系统上下文成员
 * Created on 2021/9/25 7:29 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class ApplicationContext {
    private ApplicationConfiguration configuration;
    private RuntimeInfo runtimeInfo;

    private ConcurrentHashMap<Thread, ActionContext1> contexts = new ConcurrentHashMap<Thread, ActionContext1>();

    private static class Holder {
        private static ApplicationContext instance = new ApplicationContext();
    }

    public static ApplicationContext getContext() {
        return Holder.instance;
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public ApplicationConfiguration getConfiguration() {
        return this.configuration;
    }

    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    public ActionContext1 getActionContext() {
        ActionContext1 actionContext = contexts.get(Thread.currentThread());
        if (actionContext == null) {
            actionContext = new ActionContext1();
            contexts.put(Thread.currentThread(), actionContext);
        }
        return actionContext;
    }
}

class ApplicationConfiguration {

}

class RuntimeInfo {

}

class ActionContext1 {

}