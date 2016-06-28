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
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-28 09:53:09
 */
public class ControllerComponent {

    private String buildPath = BuildPath.getPath(); //生成路径;
    private String authorName = null;//作者名字
    private String email = null;//作者邮箱
    private String packageAndOutPath = null;//指定实体生成所在包的路径
    private Set<String> imports = new HashSet<String>();
    private String tablename = null;

    private String classType = "Controller";

    private String extendsClass = "SupportController";

    private String businessModulePackage = null;

    private String mainPackage = null;

    private String[] classAnnotation = {"@Controller", "@RequestMapping(\"%s\")"};

    private void init(String tablename) {

        this.tablename = tablename;

        mainPackage = Config.getMainPackage();
        authorName = Config.getAuthorName();
        email = Config.getEmail();
        businessModulePackage =  Config.getBusinessModulePackage();

        imports.add("org.springframework.web.bind.annotation.*");
        imports.add("org.springframework.stereotype.Controller");
//        imports.add("org.springframework.transaction.annotation.Transactional");
        imports.add("javax.inject.Inject");
        imports.add("java.util.Map");
        imports.add("net.zz.sql.filter.SqlFilter");
        imports.add("javax.servlet.http.HttpServletRequest");
        imports.add(mainPackage + ".infrastructure.web.support.SupportController");

        if (!"".equals(businessModulePackage)){
            imports.add(mainPackage + "."+ businessModulePackage + ".dao.*");
            imports.add(mainPackage + "."+ businessModulePackage + ".dao.entity.*");
            imports.add(mainPackage + "."+ businessModulePackage +".service.*");
            packageAndOutPath = mainPackage + "." + businessModulePackage + ".controller" ;
        }else {
            imports.add(mainPackage  +".service.*");
            packageAndOutPath = mainPackage  + ".controller" ;
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
        sb.append(String.format("* %s 控制器\r\n", tablename));
        sb.append("* \r\n");
        sb.append(String.format("* @author %s\r\n", this.authorName));
        sb.append(String.format("* @email %s\r\n", this.email));
        sb.append(String.format("* @date %s\r\n", new Date().toLocaleString()));
        sb.append("*/ \r\n");

        sb.append("\r\n\r\n");
        for (String _at : classAnnotation) {
            sb.append(String.format(_at,tablename) + "\r\n");
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
        sb.append(String.format("\tprivate %sService service;  \r\n", entityName));
        sb.append("\t//@Inject\r\n");
        sb.append(String.format("\t//private %sDao dao; ", entityName));
        sb.append(String.format("\t//  %sDaoParams  \r\n\r\n", entityName));

    }

    /**
     * 方法
     * @return
     */
    private void method(StringBuffer sb) {
        sb.append("\t//@RequestMapping(\"test\")\r\n");
        sb.append("\tpublic Map<String, Object> test(){\r\n");
        sb.append("\t\tMap<String,Object> data = successData();\r\n");
        sb.append("\t\treturn data;\r\n");
        sb.append("\t}\r\n\r\n");
        sb.append("\t/**\r\n");
        sb.append("\t*根据请求过滤转化为查询参数\r\n");
        sb.append("\t*\r\n");
        sb.append("\t* @param request\r\n");
        sb.append(String.format("\t* @author %s\r\n", this.authorName));
        sb.append(String.format("\t* @email %s\r\n", this.email));
        sb.append(String.format("\t* @date %s\r\n", new Date().toLocaleString()));
        sb.append("\t*/\r\n");
        sb.append("\t@RequestMapping(\"list\")\r\n");
        sb.append("\tpublic Map<String, Object> list(HttpServletRequest request){\r\n");
        sb.append("\t\tSqlFilter sqlFilter = new SqlFilter(request);\r\n");
        sb.append("\t\treturn assemblyPageData(service.findByFilter(sqlFilter));\r\n");
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
