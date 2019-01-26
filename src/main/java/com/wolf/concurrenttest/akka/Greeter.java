package com.wolf.concurrenttest.akka;

import akka.actor.UntypedAbstractActor;

/**
 * Description:
 * <br/> Created on 24/03/2018 8:43 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class Greeter extends UntypedAbstractActor{

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message == Msg.GREET) {
            System.out.println("Greeter onReceive hello world!");
            getSender().tell(Msg.DONE, getSelf());
        } else {
            unhandled(message);
        }
    }



}
