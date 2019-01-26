package com.wolf.concurrenttest.akka;

import akka.actor.UntypedAbstractActor;

/**
 * Description:
 * <br/> Created on 25/03/2018 8:07 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LifeCycleWorker extends UntypedAbstractActor {

    //初始化资源
    @Override
    public void preStart() throws Exception {
        System.out.println("LifeCycleWorker is starting");
    }

    //释放资源
    @Override
    public void postStop() throws Exception {
        System.out.println("LifeCycleWorker is stopping");
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message == Msg.WORKING) {
            System.out.println("I am working");
        } else if (message == Msg.DONE) {
            System.out.println("Stop working");
        } else if (message == Msg.CLOSE) {
            System.out.println("I will shudown");
            getSender().tell(Msg.CLOSE, getSelf());
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
