package com.wolf.concurrenttest.hcpta.thread;

import java.sql.ResultSet;

/**
 * Description: 此接口负责对从数据查询出来的结果集进行操作，最终返回的数据结构由实现决定
 * Created on 2021/9/16 6:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface RowHandler<T>{
    T handle(ResultSet rs);
}
