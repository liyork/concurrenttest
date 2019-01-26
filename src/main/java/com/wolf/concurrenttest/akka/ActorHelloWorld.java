package com.wolf.concurrenttest.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

/**
 * Description:
 * <br/> Created on 24/03/2018 8:45 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ActorHelloWorld extends UntypedAbstractActor {

    ActorRef greeter;

    @Override
    public void preStart() throws Exception {
        greeter = getContext()//子actor
                .actorOf(Props.create(Greeter.class), "greeter");
        System.out.println("Greeter Actor Path:" + greeter.path());
        greeter.tell(Msg.GREET, getSelf());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message == Msg.DONE) {
            greeter.tell(Msg.GREET, getSelf());
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
