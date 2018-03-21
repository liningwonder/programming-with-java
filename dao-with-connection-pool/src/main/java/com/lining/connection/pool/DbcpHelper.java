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

    private static Properties PROPERTIES = null;
    private static final DataSource DATA_SOURCE;

    /**
     * 保证线程里的多个dao操作，用的是同一个connection，以保证事务。
     */
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    /**
     * 修改配置需要重启才能生效
     */
    static {
        PROPERTIES = PropUtil.getProperties("dbcp.properties");
        DATA_SOURCE = getDataSource(PROPERTIES);
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
    }

    public static DataSource getDataSource(Properties properties) {
        DataSource dataSource = null;
        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

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

}
