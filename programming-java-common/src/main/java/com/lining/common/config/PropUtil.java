package com.lining.common.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * description:
 * date 2018-03-21
 *
 * @author lining1
 * @version 1.0.0
 */
public class PropUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropUtil.class);

    /**
     * 加载外部资源文件的路径
     * D:\1aaaaCode\programming-with-java\config\config.properties
     * @param fileName
     * @return
     */
    public static String getFilePath(String fileName) {
        String PATH = null;
        if (StringUtils.isBlank(PATH)) {
            PATH = System.getProperty("user.dir")  + File.separator + "config" + File.separator + fileName;
        }
        return PATH;
    }

    public static Properties getProperties(String file) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(getFilePath(file));
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return properties;
    }

    /**
     * 只能加载类路径下的资源文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + " file is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
            LOG.error("load properties file failure", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error("close input stream failure", e);
                }
            }
        }
        return props;
    }

    public static String getString(Properties props, String key, String defaultValue) {
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    public static int getInt(Properties props, String key, int defaultValue) {
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = Integer.parseInt(props.getProperty(key));
        }
        return value;
    }

    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (props.containsKey(key)) {
            value = Boolean.valueOf(props.getProperty(key));
        }
        return value;
    }

    public static void main(String[] args) {
        Properties properties = loadProps("config.properties");
        String password = getString(properties, "jdbc.password", "root");
        LOG.info(password);


        Properties properties1 = getProperties("config.properties");
        String driver = getString(properties1, "jdbc.driver", "driver");
        LOG.info(driver);

        LOG.info(getFilePath("config.properties"));

        LOG.info(String.valueOf(getInt(properties1, "jdbc.test.port", 0) + 1));
        LOG.info(String.valueOf(getBoolean(properties1, "jdbc.test.open", false)));
    }
}
