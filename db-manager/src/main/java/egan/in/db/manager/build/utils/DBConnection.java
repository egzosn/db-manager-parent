package egan.in.db.manager.build.utils;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-23 15:40:26
 */
public class DBConnection {

    //数据库连接
    private static  String URL = null;
    private static  String NAME = null;
    private static  String PASS = null;
    private static  String DRIVER = null;

    public static void init(String path) {
        try {
            Properties properties = new Properties();
            properties.load(Config.class.getResourceAsStream(path));
            System.out.println("------------读取配置文件-----------");
            for (String key : properties.stringPropertyNames()) {
                if (key.equals("driver")) {
                    DRIVER = properties.getProperty(key);
                } else if (key.equals("username")) {
                    NAME = properties.getProperty(key);
                } else if (key.equals("url")) {
                    URL = properties.getProperty(key);
                } else if (key.equals("password")) {
                    PASS = properties.getProperty(key);
                }
            }
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("没有找到配置文件：" + path);
        }
    }

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL, NAME, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Connection conn,ResultSet rs,PreparedStatement ps) {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


    }



}
