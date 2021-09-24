package com.wolf.concurrenttest.hcpta.singlethreadexec;

/**
 * Description:
 * Created on 2021/9/23 6:07 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FlightSecurityTest {
    // 旅客线程
    static class Passengers extends Thread {
        private final FlightSecurity flightSecurity;
        private final String idCard;
        private final String boardingPass;

        public Passengers(FlightSecurity flightSecurity, String idCard, String boardingPass) {
            this.flightSecurity = flightSecurity;
            this.idCard = idCard;
            this.boardingPass = boardingPass;
        }

        @Override
        public void run() {
            // 旅客不断过安检
            while (true) {
                flightSecurity.pass(boardingPass, idCard);
            }
        }
    }

    public static void main(String[] args) {
        final FlightSecurity flightSecurity = new FlightSecurity();
        new Passengers(flightSecurity, "A123456", "AF123456").start();
        new Passengers(flightSecurity, "B123456", "BF123456").start();
        new Passengers(flightSecurity, "C123456", "CF123456").start();
    }
}
