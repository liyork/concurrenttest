package com.wolf.concurrenttest.program.dataselect;

import com.wolf.concurrenttest.program.dataselect.concurrent.ConcurrentSearch;
import com.wolf.concurrenttest.program.dataselect.serial.SerialSearch;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/06
 */
public class DataSelectTest {

    public static void main(String[] args) {
        testSerial();
        testConcurrent();
    }

    //按照如下方式测试其他用例:
    //使用 findAny()方法查找出现在数组中最后一个位置的对象。
    //使用 findAny()方法尝试查找某个并不存在的对象。
    //在错误情境中使用 findAny()方法。
    //使用 findAll()方法获取满足筛选器列表条件的所有对象。
    //在错误情境中使用 findAll()方法
    private static void testSerial() {
        Path path = Paths.get("data", "census-income.data");
        CensusData data[] = CensusDataLoader.load(path);
        System.out.println("Number of items: " + data.length);

        ArrayList<FilterData> filters = new ArrayList<>();
        FilterData filter = new FilterData();
        filter.setIdField(32);
        filter.setValue("Dominican-Republic");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(31);
        filter.setValue("Dominican-Republic");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(1);
        filter.setValue("Not in universe");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(14);
        filter.setValue("Not in universe");
        filters.add(filter);

        Date start = new Date();
        CensusData result = SerialSearch.findAny(data, filters);
        System.out.println("Test 1 - Result: " + result.getReasonForUnemployment());
        Date end = new Date();
        System.out.println("Test 1- Execution Time: " + (end.getTime() - start.getTime()));
    }

    private static void testConcurrent() {

        Path path = Paths.get("data", "census-income.data");
        CensusData data[] = CensusDataLoader.load(path);
        System.out.println("Number of items: " + data.length);

        ArrayList<FilterData> filters = new ArrayList<>();
        FilterData filter = new FilterData();
        filter.setIdField(32);
        filter.setValue("Dominican-Republic");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(31);
        filter.setValue("Dominican-Republic");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(1);
        filter.setValue("Not in universe");
        filters.add(filter);

        filter = new FilterData();
        filter.setIdField(14);
        filter.setValue("Not in universe");
        filters.add(filter);

        Date start = new Date();
        CensusData result = ConcurrentSearch.findAny(data, filters,10);
        System.out.println("Test 1 - Result: " + result.getReasonForUnemployment());
        Date end = new Date();
        System.out.println("Test 1- Execution Time: " + (end.getTime() - start.getTime()));
    }
}
