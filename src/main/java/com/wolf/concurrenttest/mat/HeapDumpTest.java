package com.wolf.concurrenttest.mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created on 2021/9/14 6:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HeapDumpTest {
    public static void main(String[] args) throws InterruptedException {
        List<School> schoolList = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            School school = new School();
            for (int j = 0; j < 5; ++j) {
                school.studentList.add(new Student());
            }
            schoolList.add(school);
        }
        Thread.sleep(1000000000);
    }
}

class School {
    final List<Student> studentList = new ArrayList<>();
}

class Student {

}