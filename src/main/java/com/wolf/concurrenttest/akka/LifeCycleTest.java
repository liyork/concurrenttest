package com.wolf.concurrenttest.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Description:
 * <br/> Created on 25/03/2018 8:17 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LifeCycleTest {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("lifeCycleSystem",
                ConfigFactory.load("samplehello.conf"));
        ActorRef worker = system.actorOf(Props.create(LifeCycleWorker.class), "worker");
        system.actorOf(Props.create(WatchActor.class, worker), "watcher");
        worker.tell(Msg.WORKING,ActorRef.noSender());
        worker.tell(Msg.DONE,ActorRef.noSender());
        worker.tell(PoisonPill.getInstance(),ActorRef.noSender());
    }
}
