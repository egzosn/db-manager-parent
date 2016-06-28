package egan.in.db.manager.build.component;

import egan.in.db.manager.build.bean.Column;
import egan.in.db.manager.build.utils.BuildPath;
import egan.in.db.manager.build.utils.CommonUtils;
import egan.in.db.manager.build.utils.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by ZaoSheng on 15-5-26.
 */
public class DaoComponent {

    private String buildPath = BuildPath.getPath(); //生成路径;
    private String authorName = null;//作者名字
    private String email = null;//作者邮箱
    private String packageAndOutPath = null;//指定实体生成所在包的路径
    private Set<String> imports = new HashSet<String>();
    private String tablename = null;
    private String businessModulePackage = null;
    private String classType = "Dao";

    private String extendsClass = "BaseDao";

    private String mainPackage = null;

    private String[] classAnnotation = { "@Repository"};

    private String pkIdType = "Object";

    private void init(String tablename, List<Column> columnJavaclass) {

        this.tablename = tablename;
        this.mainPackage = Config.getMainPackage();
        mainPackage = Config.getMainPackage();
        authorName = Config.getAuthorName();
        email = Config.getEmail();
        businessModulePackage =  Config.getBusinessModulePackage();
        imports.add("org.springframework.stereotype.Repository");
        imports.add(mainPackage + ".infrastructure.hibernate." + extendsClass);

        if (!"".equals(businessModulePackage)){
            imports.add(mainPackage + "."+ businessModulePackage +".dao.entity.*");
            packageAndOutPath = mainPackage + "." + businessModulePackage + ".dao" ;
        }else {
            imports.add(mainPackage  +".dao.entity.*");
            packageAndOutPath = mainPackage  + ".dao" ;
        }

        for (Column column : columnJavaclass) {
            if (column.isPrimary()) {
                imports.add(column.getType());
                pkIdType = column.getType().substring(column.getType().lastIndexOf(".") + 1);
                break;
            }
        }


    }

    private String parse() {

        StringBuffer sb = new StringBuffer();
        sb.append(String.format("package %s;\r\n", packageAndOutPath));
        for (String _import : imports) {
            sb.append("import " + _import + ";\r\n");
        }

        sb.append("\r\n");
        //注释部分
        sb.append("/**\r\n");
        sb.append(String.format("* %s 数据处理\r\n", tablename));
        sb.append("* \r\n");
        sb.append(String.format("* @author %s\r\n", this.authorName));
        sb.append(String.format("* @email %s\r\n", this.email));
        sb.append(String.format("* @date %s\r\n", new Date().toLocaleString()));
        sb.append("*/ \r\n");
        for (String _a : classAnnotation) {
            sb.append(_a + "\r\n");
        }
//        sb.append("public class " + CommonUtils.UnderlineToCap(tablename) + classType + " extends " + extendsClass + " <"+ CommonUtils.UnderlineToCap(tablename)+","+pkIdType+"> {\r\n");
        sb.append(String.format("public class %s%s extends %s<%s, %s> {\r\n", CommonUtils.UnderlineToCap(tablename), classType, extendsClass, CommonUtils.UnderlineToCap(tablename), pkIdType));

        sb.append("}\r\n");
        return sb.toString();
    }


    public void gen(String tablename, List<Column> columnJavaclass) {
        init(tablename,columnJavaclass);
        try {
            String outputDir = buildPath + this.packageAndOutPath.replace(".", "/");
            String outputPath = outputDir + "/" + CommonUtils.UnderlineToCap(tablename) + classType + ".java";
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileWriter fw = new FileWriter(outputPath);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(parse());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
