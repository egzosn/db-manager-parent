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
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-28 09:51:01
 */
public class EntityComponent {

    private String buildPath = BuildPath.getPath(); //生成路径;
    private String authorName = null;//作者名字
    private String email = null;//作者邮箱
    private String packageAndOutPath = null;//指定实体生成所在包的路径
    private String businessModulePackage = null;
    private List<Column> columnJavaclass = new ArrayList<Column>(); //
    private Set<String> imports = new HashSet<String>();
    private String tablename = null;

//    private String extendsClass = "BaseEntity";

    private String mainPackage;

    /**
     * 初始化
     */
    private void init(String tablename, List<Column> columnJavaclass) {

        this.tablename = tablename;
        this.mainPackage = Config.getMainPackage();
        businessModulePackage =  Config.getBusinessModulePackage();
        this.authorName = Config.getAuthorName();
        this.email = Config.getEmail();

        imports.add("com.fasterxml.jackson.annotation.JsonIgnore");
        imports.add("javax.persistence.*");
        imports.add("org.hibernate.annotations.GenericGenerator");
        this.columnJavaclass = columnJavaclass;
        if (!"".equals(businessModulePackage)){
            packageAndOutPath =   mainPackage + "." + businessModulePackage  + ".dao.entity";
        }else {
            packageAndOutPath = mainPackage  + ".dao.entity" ;
        }

    }

    private String parse() {

        StringBuffer sb = new StringBuffer();
        sb.append(String.format("package %s;\r\n", packageAndOutPath));
        for (Column column : columnJavaclass) {
            imports.add(column.getType());
        }
        for (String _import : imports) {
            sb.append(String.format("import %s;\r\n", _import));
        }

        sb.append("\r\n");
        //注释部分
        sb.append("/**\r\n");
        sb.append(String.format("* %s 实体类\r\n", tablename));
        sb.append("* \r\n");
        sb.append(String.format("* @author %s\r\n", this.authorName));
        sb.append(String.format("* @email %s\r\n", this.email));
        sb.append(String.format("* @date %s\r\n", new Date().toLocaleString()));
        sb.append("*/ \r\n");
        //实体部分
        sb.append(String.format("@Table(name = \"%s\") \r\n", tablename));
        sb.append("@Entity \r\n");
        sb.append(String.format("public class %s {\r\n", CommonUtils.UnderlineToCap(tablename)));
        processAllAttrs(sb);//属性
        processAllMethod(sb); // 方法
        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {

        String javaClassShortName = null;
        String  attrsDefaultValue = "";
        sb.append("\r\n");
        for (Column column : columnJavaclass) {
            if (null == column.getComment() || "".equals(column.getComment())){ }
            else {
                sb.append(String.format("\t //%s\r\n", column.getComment()));
            }
            javaClassShortName =  column.getType().substring(column.getType().lastIndexOf(".") + 1);
            if (column.isPrimary()) {
                sb.append("\t@Id\r\n");
                if (column.getType().equals(String.class.getName())) {
                    sb.append("\t@GenericGenerator(name=\"generator\", strategy=\"uuid.hex\")\r\n");
                    sb.append("\t@GeneratedValue(generator=\"generator\")\r\n");
                }else if (column.isAutoincrement()){
                    sb.append("\t@GeneratedValue\r\n");
                }
            }
            if (column.equals("version")) {
                sb.append("\t@Version\r\n");
                sb.append("\t@JsonIgnore\r\n");
            }

            sb.append(String.format("\t@Column(name = \"%s\") \r\n", column.getField()));
            sb.append("\tprivate " + javaClassShortName + " " + CommonUtils.fieldConvert(column.getField()) + attrsDefaultValue + ";\r\n");
            attrsDefaultValue = "";
        }

    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {

        String javaClassShortName = null;
        for (Column column : columnJavaclass) {
            sb.append("\r\n");
            String fieldConvert = CommonUtils.fieldConvert(column.getField());
            String methodField = CommonUtils.UnderlineToCap(column.getField());
            javaClassShortName =  column.getType().substring(column.getType().lastIndexOf(".") + 1);
            sb.append(String.format("\tpublic void set%s(%s %s){\r\n", methodField, javaClassShortName, fieldConvert));
            sb.append(String.format("\t\tthis.%s = %s;\r\n", fieldConvert, fieldConvert));
            sb.append("\t}\r\n\r\n");
            sb.append(String.format("\tpublic %s get%s(){\r\n", javaClassShortName, methodField));
            sb.append(String.format("\t\treturn %s;\r\n", fieldConvert));
            sb.append("\t}\r\n");
        }
    }



    public void gen(String tablename, List<Column> columnJavaclass) {
        init(tablename, columnJavaclass);
        try {
            String outputDir = buildPath + this.packageAndOutPath.replace(".", "/");
            String outputPath = outputDir + "/" + CommonUtils.UnderlineToCap(tablename) + ".java";
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
