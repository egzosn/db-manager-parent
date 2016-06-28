package egan.in.db.manager.build.utils;

import java.io.File;

/**
 * Created by egan on 15-5-5.
 */
public class BuildPath {

    public static String path ;

    static {
        path = BuildPath.class.getResource("/").getPath();
        File file = new File( new File(path).getParent() + "/build" );
        path = file.getPath();
        file.mkdirs();
    }

    public static String getPath() {
        return path + "/";
    }

    public static void main(String[] args) {
        System.out.println(path);
    }

}
