package com.wolf.concurrenttest.hcpta.singlethreadexec;

/**
 * Description: Single Thread Execution
 * Created on 2021/9/23 6:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FlightSecurity {
    private int count = 0;
    // 登机牌
    private String boardingPass = "null";
    // 身份证
    private String idCard = "null";

    //public void pass(String boardingPass, String idCard) {  // 不安全
    public synchronized void pass(String boardingPass, String idCard) {  // 安全
        this.boardingPass = boardingPass;
        this.idCard = idCard;
        this.count++;
        check();
    }

    private void check() {
        // 简单测试，首字母不同时表示检查不通过
        if (boardingPass.charAt(0) != idCard.charAt(0)) {
            throw new RuntimeException("====Exception====" + toString());
        }
    }

    @Override
    public String toString() {
        return "The " + count + " passengers, boardingPass [" + boardingPass + "], idCard [" + idCard + "]";
    }
}
