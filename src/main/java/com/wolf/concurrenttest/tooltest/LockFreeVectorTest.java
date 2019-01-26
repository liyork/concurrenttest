package com.wolf.concurrenttest.tooltest;

import com.wolf.concurrenttest.actualcombat.LockFreeVector;

/**
 * Description:
 * <br/> Created on 2018/3/22 9:37
 *
 * @author 李超
 * @since 1.0.0
 */
public class LockFreeVectorTest {

    public static void main(String[] args) {
//        testNumberOfLeadingZeros();

        LockFreeVector<Integer> lockFreeVector = new LockFreeVector<Integer>();
        for (int i = 0; i < 100; i++) {
            lockFreeVector.add(i);
        }


    }

    private static void testNumberOfLeadingZeros() {
        System.out.println(Integer.numberOfLeadingZeros(8));

        //00001000 00000000 00000000 00000000
        System.out.println(0x80000000);
        System.out.println(Integer.MAX_VALUE+1);
    }
}
