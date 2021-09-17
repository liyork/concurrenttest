package com.wolf.concurrenttest.hcpta.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description:
 * Created on 2021/9/16 6:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RecordQuery {
    private final Connection connection;

    public RecordQuery(Connection connection) {
        this.connection = connection;
    }

    // 通过接口交互，让类隔离
    // 查询数据，然后交给handler对数据封装
    public <T> T query(RowHandler<T> handler, String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            ResultSet resultSet = stmt.executeQuery();
            // 结果交给实现RowHandler的类对象
            return handler.handle(resultSet);
        }
    }
}
