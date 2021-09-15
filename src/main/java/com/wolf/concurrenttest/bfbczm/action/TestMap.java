package com.wolf.concurrenttest.bfbczm.action;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: ConcurrentHashMap的使用问题
 * Created on 2021/9/13 1:31 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestMap {
    // <topic, List<device>>
    static ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 线程1进入直播间topic1
        Thread threadOne = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device1");
            list1.add("device2");

            map.put("topic1", list1);
            System.out.println(JSON.toJSONString(map));
        });

        // 线程2进入直播间topic1
        Thread threadTwo = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device11");
            list1.add("device22");

            // 会丢失，因为覆盖了
            map.put("topic1", list1);
            System.out.println(JSON.toJSONString(map));
        });

        // 线程3进入直播间topic2
        Thread threadThree = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device111");
            list1.add("device222");

            map.put("topic2", list1);
            System.out.println(JSON.toJSONString(map));
        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();
    }
}
