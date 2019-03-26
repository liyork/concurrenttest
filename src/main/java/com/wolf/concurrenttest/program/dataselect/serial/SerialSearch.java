package com.wolf.concurrenttest.program.dataselect.serial;

import com.wolf.concurrenttest.program.dataselect.CensusData;
import com.wolf.concurrenttest.program.dataselect.Filter;
import com.wolf.concurrenttest.program.dataselect.FilterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/06
 */
public class SerialSearch {

    //返回第一个符合筛选条件的
    public static CensusData findAny(CensusData[] data, List<FilterData> filters) {

        int index = 0;
        for (CensusData censusData : data) {
            if (Filter.filter(censusData, filters)) {
                System.out.println("Found: " + index);
                return censusData;
            }
            index++;
        }

        return null;
    }

    //返回符合筛选条件的所有数据
    public static List<CensusData> findAll(CensusData[] data, List<FilterData> filters) {

        ArrayList<CensusData> results = new ArrayList<>();

        for (CensusData censusData : data) {
            if (Filter.filter(censusData, filters)) {
                results.add(censusData);
            }
        }

        return results;
    }
}
