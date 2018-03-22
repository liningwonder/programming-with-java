package com.lining.connection.pool;

import com.lining.common.config.PropUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * description:
 * date 2018-03-21
 *
 * @author lining1
 * @version 1.0.0
 */
public class DbcpHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbcpHelper.class);

    /**
     * 由于DataSource是静态变量，类共享的，所以当不同的线程去取的时候，会出现并发问题
     * 除非使用加锁.
     * 这种情况不适用于多数据源的情况,应为静态初始化只能是用一份配置
     */
    private static Properties PROPERTIES = null;

    /**
     * 这种情况不适用于多数据源的情况
     */
    private static final DataSource DATA_SOURCE;

    /**
     * ThreadLocal 是一个容器，用于存放线程的局部变量,为了解决多线程并发问题
     * 在同步机制中，使用同步保证同一时间只有一个线程访问，不能同时访问共享资源，否则就是出现错误。
     * ThreadLocal则隔离了相关的资源，并在同一个线程中可以共享这个资源。彼此独立，修改不会影响到对方。
     * 对于多线程资源共享问题，同步机制采用了“以时间换空间”的方式，而ThreadLocal采用了“以空间换时间”的方式。
     * 前者仅提供一份变量，让不同的线程排队访问，而后者为每一个线程都提供了一份变量，因此可以同时访问而互不影响。
     * 保证线程里的多个dao操作，用的是同一个connection，以保证事务。
     */
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    /**
     * 修改配置需要重启才能生效
     */
    static {
        PROPERTIES = PropUtil.getProperties("dbcp.properties");
        DATA_SOURCE = initDataSource(PROPERTIES);
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
    }

    /**
     * 初始化数据源
     * @param properties
     * @return
     */
    private static DataSource initDataSource(Properties properties) {
        DataSource dataSource = null;
        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    /**
     * 获取数据源
     * @return
     */
    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /**
     * 设计成 static 的主要是为了让 DbcpHelper 的 static 方法访问起来更加方便。
     * @return
     */
    public static Connection getDbcpConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            try {
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOG.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CONNECTION_HOLDER.remove();
        }
    }

}
