package egan.in.db.manager.build.component;

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
public class ServiceComponent {

    private String buildPath = BuildPath.getPath(); //生成路径;
    private String authorName = null;//作者名字
    private String email = null;//作者邮箱
    private String packageAndOutPath = null;//指定实体生成所在包的路径
    private String businessModulePackage = null;

    private Set<String> imports = new HashSet<String>();
    private String tablename = null;

    private String classType = "Service";

    private String extendsClass = "SupportService";

    private String mainPackage = null;

    private String[] classAnnotation = {"@Service", "@Transactional"};

    private void init(String tablename) {

        this.tablename = tablename;

        mainPackage = Config.getMainPackage();
        authorName = Config.getAuthorName();
        email = Config.getEmail();

        businessModulePackage =  Config.getBusinessModulePackage();
        imports.add("org.springframework.stereotype.Service");
        imports.add("org.springframework.transaction.annotation.Transactional");
        imports.add("javax.inject.Inject");
        imports.add("net.zz.sql.filter.SqlFilter");
        imports.add("net.zz.dao.params.*");
        imports.add(mainPackage + ".infrastructure.hibernate.Page");
        imports.add(mainPackage + ".infrastructure.web.support.service.SupportService");
        if (!"".equals(businessModulePackage)){
            imports.add(mainPackage + "."+ businessModulePackage +".dao.entity." +  CommonUtils.UnderlineToCap(tablename));
            imports.add(mainPackage + "."+ businessModulePackage +".dao.*");
            packageAndOutPath = mainPackage + "." + businessModulePackage + ".service" ;
        }else {
            imports.add(mainPackage  +".entity.*");
            imports.add(mainPackage  +".dao.*");
            packageAndOutPath = mainPackage  + ".service" ;
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
        sb.append(String.format("* %s 服务\r\n", tablename));
        sb.append("* \r\n");
        sb.append(String.format("* @author %s\r\n", this.authorName));
        sb.append(String.format("* @email %s\r\n", this.email));
        sb.append(String.format("* @date %s\r\n", new Date().toLocaleString()));
        sb.append("*/ \r\n");

        sb.append("\r\n\r\n");
        for (String _a : classAnnotation) {
            sb.append(String.format(_a,tablename) + "\r\n");
        }
        sb.append(String.format("public class %s%s extends %s {\r\n", CommonUtils.UnderlineToCap(tablename), classType, extendsClass));

        field(sb);
        method(sb);

        sb.append("}\r\n");
        return sb.toString();
    }


    /**
     * 字段
     * @return
     */
    private void field(StringBuffer sb) {
        String entityName = CommonUtils.UnderlineToCap(tablename);
        sb.append("\t@Inject\r\n");
        sb.append(String.format("\tprivate %sDao dao; ", entityName));
        sb.append(String.format("\t//  %sDaoParams  \r\n", entityName));

    }

    /**
     * 方法
     * @return
     */
    private void method(StringBuffer sb) {
        sb.append("\r\n");
        sb.append("\t/**\r\n");
        sb.append("\t* @param sqlFilter 过滤对象\r\n");
        sb.append(String.format("\t* @author %s\r\n", this.authorName));
        sb.append(String.format("\t* @email %s\r\n", this.email));
        sb.append(String.format("\t* @date %s\r\n", new Date().toLocaleString()));
        sb.append("\t*/\r\n");
        sb.append("\t@Transactional(readOnly = true)\r\n");
        sb.append(String.format("\tpublic Page<%s> findByFilter(SqlFilter sqlFilter){\r\n",  CommonUtils.UnderlineToCap(tablename)));
        sb.append("\t\t//设置排序\r\n");
        sb.append("\t\tsqlFilter.setOrder();\r\n");
        sb.append(String.format("\t\treturn dao.queryPageUseHQL(sqlFilter.setAlias(\"%s\").getQueryParams(), true);\r\n", CommonUtils.fieldConvert(tablename)));
        sb.append("\t}\r\n\r\n");
    }


    public void gen(String tablename) {
        init(tablename);
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
