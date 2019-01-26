package com.wolf.concurrenttest.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Description:actor由线程池执行，不要在actor中执行耗时代码，否则可能导致其他actor调度问题。
 * 由消息驱动。推荐使用不可变对象
 * <br/> Created on 24/03/2018 8:48 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ActorHelloWorldMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("Hello1", ConfigFactory.load("samplehello.conf"));
        //创建顶级actor(ActorHelloWorld)
        ActorRef actorRef = system.actorOf(Props.create(ActorHelloWorld.class), "helloworld1");
        System.out.println("helloworld actor path:"+ actorRef.path());
    }
}
