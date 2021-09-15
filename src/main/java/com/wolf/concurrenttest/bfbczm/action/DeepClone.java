package com.wolf.concurrenttest.bfbczm.action;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Description:
 * Created on 2021/9/13 10:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DeepClone {
    static Map<Integer, StrategyService> serviceMap = new HashMap<>();

    static {
        // 给不同的appkey注册对应的处理策略
        serviceMap.put(111, new StrategyOneService());
        serviceMap.put(222, new StrategyTwoService());
    }

    public static void main(String[] args) {
        // <appKey, List<deviceId>>
        Map<Integer, List<String>> appKeyMap = new HashMap<>();

        // 创建appkey=111的设备列表
        List<String> oneList = new ArrayList<>();
        oneList.add("device_id1");
        appKeyMap.put(111, oneList);

        // 创建appkey=222的设备列表
        List<String> twoList = new ArrayList<>();
        twoList.add("device_id2");
        appKeyMap.put(222, twoList);

        // 创建消息
        List<Msg> msgList = new ArrayList<>();
        Msg msg = new Msg();
        msg.setDataId("abc");  // 每个消息对应一个DataId，唯一标识
        msg.setBody("hello");
        msgList.add(msg);

        // 根据不同的appKey使用不同的策略进行处理
        Iterator<Integer> appKeyItr = appKeyMap.keySet().iterator();
        while (appKeyItr.hasNext()) {
            int appKey = appKeyItr.next();
            System.out.println("appkey:" + appKey);
            StrategyService strategyService = serviceMap.get(appKey);
            if (null != strategyService) {
                //strategyService.sendMsg(msgList, appKeyMap.get(appKey));
                // 想到，不同的appkey应该有自己的一份List<Msg>，这样，不同的服务只会修改自己的消息的DataId而不会相互影响。也不行，因为实际引用是Msg而不是List
                //strategyService.sendMsg(new ArrayList<>(msgList), appKeyMap.get(appKey));
                // 深克隆
                strategyService.sendMsg(deepClone(msgList), appKeyMap.get(appKey));
            } else {
                System.out.println(String.format("appkey:%s, is not registered service", appKey));
            }
        }
    }

    private static List<Msg> deepClone(List<Msg> msgList) {
        ArrayList<Msg> out = new ArrayList<>();
        for (Msg msg : msgList) {
            out.add(new Msg(msg.getDataId(), msg.getBody()));
        }
        return out;
    }
}

interface StrategyService {
    public void sendMsg(List<Msg> msgList, List<String> deviceIdList);
}

class StrategyOneService implements StrategyService {

    @Override
    public void sendMsg(List<Msg> msgList, List<String> deviceIdList) {
        for (Msg msg : msgList) {
            msg.setDataId("oneService_" + msg.getDataId());
            System.out.println(msg.getDataId() + " " + JSON.toJSONString(deviceIdList));
        }
    }
}

class StrategyTwoService implements StrategyService {

    @Override
    public void sendMsg(List<Msg> msgList, List<String> deviceIdList) {
        for (Msg msg : msgList) {
            msg.setDataId("twoService_" + msg.getDataId());
            System.out.println(msg.getDataId() + " " + JSON.toJSONString(deviceIdList));
        }
    }
}

class Msg {
    private String dataId;
    private String body;

    public Msg() {
    }

    public Msg(String dataId, String body) {
        this.dataId = dataId;
        this.body = body;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
