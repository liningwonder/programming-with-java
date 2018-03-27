package com.lining.connection.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.lining.common.config.PropUtil;
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
public class DruidHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DruidHelper.class);

    private Properties properties = null;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public DataSource getDatasource() {
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return dataSource;
    }

    public Connection getConnection(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return connection;
    }

}
