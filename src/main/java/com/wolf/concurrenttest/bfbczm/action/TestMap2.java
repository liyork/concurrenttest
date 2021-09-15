package com.wolf.concurrenttest.bfbczm.action;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: putIfAbsent判断是否存在和放入两个操作是原子
 * Created on 2021/9/13 1:31 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestMap2 {
    // <topic, List<device>>
    static ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<String, List<String>>();

    public static void main(String[] args) {
        // 线程1进入直播间topic1
        Thread threadOne = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device1");
            list1.add("device2");

            List<String> oldList = map.putIfAbsent("topic1", list1);
            if (null != oldList) {
                oldList.addAll(list1);
            }
            System.out.println(JSON.toJSONString(map));
        });

        // 线程2进入直播间topic1
        Thread threadTwo = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device11");
            list1.add("device22");

            List<String> oldList = map.putIfAbsent("topic1", list1);
            if (null != oldList) {
                oldList.addAll(list1);
            }
            System.out.println(JSON.toJSONString(map));
        });

        // 线程3进入直播间topic2
        Thread threadThree = new Thread(() -> {
            ArrayList<String> list1 = new ArrayList<>();
            list1.add("device111");
            list1.add("device222");

            List<String> oldList = map.putIfAbsent("topic2", list1);
            if (null != oldList) {
                oldList.addAll(list1);
            }
            System.out.println(JSON.toJSONString(map));
        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();
    }
}
