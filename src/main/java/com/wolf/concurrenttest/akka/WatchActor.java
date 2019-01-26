package com.wolf.concurrenttest.akka;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;

/**
 * Description:
 * <br/> Created on 25/03/2018 8:11 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class WatchActor extends UntypedAbstractActor {

    public WatchActor(ActorRef actorRef) {
        getContext().watch(actorRef);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Terminated) {
            System.out.println(String.format("%s has terminated, shutting down system",
                    ((Terminated) message).getActor().path()));
            getContext().system().terminate();
        } else {
            unhandled(message);
        }
    }
}
