package egan.in.db.manager.build;

import egan.in.db.manager.build.utils.Config;
import egan.in.db.manager.build.utils.DBConnection;

/**
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-23 14:37:01
 */
public class BuildMain {
    public static void main(String[] args) {
        if (args.length <= 0)
        {
            args = new String[]{"/liquibase/local.properties","/liquibase/webconfig.properties"};
        }
        System.out.println("---------------start----------");
        DBConnection.init(args[0]);
        Config.init(args[1]);
        new GenCodeForMysql().gen();
        System.out.println("---------------end-----------------");
    }


}
