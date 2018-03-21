package com.lining.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * date 2018-03-21
 *
 * @author lining1
 * @version 1.0.0
 */
public class JdbcExample {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcExample.class);

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        DRIVER = "com.mysql.jdbc.Driver";
        URL = "jdbc:mysql://localhost:3306/lining?useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=TRUE";
        USERNAME = "root";
        PASSWORD = "root";
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return connection;
    }

    public static void closeResource(Connection conn, Statement stat, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {rs.close();}
            if (stat != null && !stat.isClosed()) {stat.close();}
            if (conn != null && !conn.isClosed()) {conn.close();}
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    public static User getUser(int id) {
        Connection conn = getConnection();
        PreparedStatement stat = null;
        ResultSet rs = null;
        User user = new User();
        try {
            String sql = "select id, email, name from t_user where id=?";
            stat = conn.prepareStatement(sql);
            stat.setInt(1, id);
            rs = stat.executeQuery();
            while (rs.next()) {
                int roleId = rs.getInt("id");
                String email = rs.getString("email");
                String name = rs.getString("name");
                user.setId(id);
                user.setEmail(email);
                user.setName(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        } finally {
            closeResource(conn, stat, rs);
        }
        return user;
    }

    public static List<User> getAll() {
        List<User> userList = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            String sql = "select * from t_user";
            stat = conn.prepareStatement(sql);
            rs = stat.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("email"), rs.getString("name"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        } finally {
            closeResource(conn, stat, rs);
        }
        return userList;
    }

    public static List<User> getUserList(String name) {
        List<User> userList = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            String sql = "select * from t_user where name=?";
            stat = conn.prepareStatement(sql);
            stat.setString(1,name);
            rs = stat.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("email"), rs.getString("name"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return userList;
    }

    public static boolean insertUser(User user) {
        boolean result = false;
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement("insert into t_user(id , email, name) VALUES(?,?,?) ");
            statement.setInt(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getName());
            int i = statement.executeUpdate();
            if (i != 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("insert error");
        } finally {
            closeResource(conn, statement, rs);
        }
        return result;
    }

    public static boolean updateUser(User user) {
        boolean result = false;
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement("update t_user set email=?,name = ? where id=? ");
            statement.setInt(3, user.getId());
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getName());
            int row = statement.executeUpdate();
            if (row != 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("update error");
        } finally {
            closeResource(conn, statement, rs);
        }
        return result;
    }

    public static boolean deleteUser(int id) {
        boolean result = false;
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement("delete from t_user where id=? ");
            statement.setInt(1, id);
            int row = statement.executeUpdate();
            if (row != 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("delete error");
        } finally {
           closeResource(conn, statement, rs);
        }
        return result;
    }

    public static void main(String[] args) {
/*        User user = new User(4, "liningwonder@163.com", "lining");
        insertUser(user);*/
/*        User user2 = new User(2, "lining", "liningwonder@163.com");
        updateUser(user2);*/
/*        List<User> all = getAll();
        for (User user : all) {
            LOG.info("all:{}", user.toString());
        }*/

/*        User user3 = getUser(1);
        LOG.info("single:{}", user3.toString());*/
        List<User> user4 = getUserList("lining");
        for (User user : user4) {
            LOG.info("user4:{}", user.toString());
        }
    }

}
