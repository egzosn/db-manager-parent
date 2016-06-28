package egan.in.db.manager.build.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author egan
 * @email egzosn@gmail.com
 * @date 2016-6-23 14:50:08
 */
public class Config {

    private static String authorName = null;
    private static String email = null;
    private static String mainPackage = null;
    private static String businessModulePackage = null;

    public static void init(String path) {
        try {
            Properties properties = new Properties();
            properties.load(Config.class.getResourceAsStream(path));
            System.out.println("------------读取配置文件-----------");
            for (String key : properties.stringPropertyNames()) {
                if (key.equals("main_package")) {
                    mainPackage = properties.getProperty(key);
                } else if (key.equals("author_name")) {
                    authorName = properties.getProperty(key, "");
                } else if (key.equals("email")) {
                    email = properties.getProperty(key, "");
                } else if (key.equals("business_module_package")) {
                    businessModulePackage = properties.getProperty(key);
                }
            }
        } catch (IOException e) {
            System.out.println("没有找到配置文件：" + path);
        }

    }

    public static String getAuthorName() {
        return authorName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getMainPackage() {
        return mainPackage;
    }

    public static String getBusinessModulePackage() {
        return businessModulePackage;
    }
}
