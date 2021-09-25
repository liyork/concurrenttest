package com.wolf.concurrenttest.hcpta.threadcontext;

/**
 * Description: 将所有需要被线程访问和操作的数据封装在了Context中，每个线程拥有不一样的上下文数据
 * Created on 2021/9/25 3:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActionContext {
    private static final ThreadLocal<Context> context = ThreadLocal.withInitial(Context::new);

    public static Context get() {
        return context.get();
    }

    // 每个线程都有一个独立的Context实例
    static class Context {
        private Configuration configuration;
        private OtherResource otherResource;

        public Configuration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }

        public OtherResource getOtherResource() {
            return otherResource;
        }

        public void setOtherResource(OtherResource otherResource) {
            this.otherResource = otherResource;
        }
    }
}

class Configuration {

}

class OtherResource {
}
