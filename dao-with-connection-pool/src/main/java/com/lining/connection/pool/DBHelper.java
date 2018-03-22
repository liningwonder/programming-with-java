package com.lining.connection.pool;

import com.lining.common.config.PropUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * description:将 Connection 放到 ThreadLocal 容器中了，
 * 这样每个线程之间对 Connection 的访问就是隔离的了（不会共享），保证了线程安全。
 * date 2018-03-22
 *
 * @author lining1
 * @version 1.0.0
 */
public class DBHelper {

    private static final BasicDataSource ds = new BasicDataSource();
    private static final QueryRunner runner = new QueryRunner(ds);

    // 定义一个局部线程变量（使每个线程都拥有自己的连接）
    private static ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

    static {
        System.out.println("Init DBHelper...");

        Properties properties = PropUtil.getProperties("dbcp.properties");

        // 初始化数据源
        ds.setDriverClassName(PropUtil.getString(properties, "jdbc.driver", "0"));
        ds.setUrl(PropUtil.getString(properties, "jdbc.url", "0"));
        ds.setUsername(PropUtil.getString(properties,"jdbc.username", "0"));
        ds.setPassword(PropUtil.getString(properties, "jdbc.password", "0"));
        ds.setMaxTotal(PropUtil.getInt(properties, "jdbc.max.active", 50));
        ds.setMaxIdle(PropUtil.getInt(properties, "jdbc.max.idle", 5));
    }

    // 获取数据源
    public static DataSource getDataSource() {
        return ds;
    }

    // 从数据源中获取数据库连接
    public static Connection getConnectionFromDataSource() {
        Connection conn = null;
        try {
            conn = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 从线程局部变量中获取数据库连接
    public static Connection getConnectionFromThreadLocal() {
        return connContainer.get();
    }

    // 开启事务
    public static void beginTransaction() {
        Connection conn = connContainer.get();
        if (conn == null) {
            try {
                conn = ds.getConnection();
                conn.setAutoCommit(false);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connContainer.set(conn);
            }
        }
    }

    // 提交事务
    public static void commitTransaction() {
        Connection conn = connContainer.get();
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connContainer.remove();
            }
        }
    }

    // 回滚事务
    public static void rollbackTransaction() {
        Connection conn = connContainer.get();
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connContainer.remove();
            }
        }
    }

    // 获取数据库默认事务隔离级别
    public static int getDefaultIsolationLevel() {
        int level = 0;
        try {
            level = getConnectionFromThreadLocal().getMetaData().getDefaultTransactionIsolation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    /**
     * 获取 Connection 分两种情况，若自动从 DataSource 中获取，则为非事务情况；
     * 反之，从关闭 Connection 自动提交功能后，强制传入 Connection 时，则为事务情况。
     * @param runner
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public static int update(QueryRunner runner, Connection conn, String sql, Object... params) {
        int result = 0;
        try {
            if (conn != null) {
                result = runner.update(conn, sql, params);
            } else {
                result = runner.update(sql, params);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
