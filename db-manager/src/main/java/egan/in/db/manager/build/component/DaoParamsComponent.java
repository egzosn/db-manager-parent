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
public class DaoParamsComponent {

    private String buildPath = BuildPath.getPath(); //生成路径;
    private String authorName = null;//作者名字
    private String email = null;//作者邮箱
    private String packageAndOutPath = null;//指定实体生成所在包的路径
    private List<Column> columnJavaclass = new ArrayList<Column>(); //
    private Set<String> imports = new HashSet<String>();
    private String tablename = null;
    private String businessModulePackage = null;

    private String classType = "DaoParams";

    private String extendsClass = "Where";

    private String mainPackage;

    private boolean addConstrucotr = false;
    enum type{
        Short,Integer,Long,Float,Double,BigDecimal,String,Blod,Date,Time,Timestamp,Boolean
    }
    private void init(String tablename, List<Column> columnJavaclass) {

        this.tablename = tablename;
        mainPackage = Config.getMainPackage();
        businessModulePackage =  Config.getBusinessModulePackage();
        this.authorName = Config.getAuthorName();
        email = Config.getEmail();
        imports.add("net.zz.dao.params.*");
        imports.add("net.zz.dao.params.enums.*");
        imports.add("java.util.List");
        imports.add("java.util.Arrays");

        if (!"".equals(businessModulePackage)){
            packageAndOutPath =   mainPackage + "." + businessModulePackage  + ".dao.params";
        }else {
            packageAndOutPath = mainPackage  + ".dao.params" ;
        }

        this.columnJavaclass = columnJavaclass;
    }

    private String parse() {

        StringBuffer sb = new StringBuffer();
        sb.append(String.format("package %s;\r\n", packageAndOutPath));
        for (String _import : imports) {
            sb.append("import " + _import + ";\r\n");
        }
        Set<String> javaClass = new HashSet<String>();
        for (Column column : columnJavaclass) {
            javaClass.add(column.getType());
        }
        for (String _import : javaClass) {
            sb.append("import " + _import + ";\r\n");
        }

        sb.append("\r\n");
        //注释部分
        sb.append("/**\r\n");
        sb.append(String.format("* %s 请求参数封装类\r\n", tablename));
        sb.append("* \r\n");
        sb.append(String.format("* @author %s\r\n", this.authorName));
        sb.append(String.format("* @email %s\r\n", this.email));
        sb.append(String.format("* @date %s\r\n", new Date().toLocaleString()));
        sb.append("*/ \r\n");
        //实体部分
        sb.append("\r\n\r\n");
//        sb.append("public class " + CommonUtils.UnderlineToCap(tablename) + classType + " extends " + extendsClass + " {\r\n");
        sb.append(String.format("public class %s%s extends %s {\r\n", CommonUtils.UnderlineToCap(tablename), classType, extendsClass));
        processAllConstant(sb);
        processAllConstructor(sb);
        processEnum(sb);
        //  processAllAttrs(sb);//属性
        processAllMethods(sb); // 方法
        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * 功能：生成所有方法
     * @param sb
     */
    private void processAllMethods(StringBuffer sb) {

        String javaClassShortName = null;
        String table = CommonUtils.UnderlineToCap(tablename);
        for (Column column : columnJavaclass) {
            String attr =  CommonUtils.fieldConvert(column.getField().toLowerCase());

            javaClassShortName = column.getType().substring(column.getType().lastIndexOf(".") + 1);
//            sb.append(String.format("\tpublic %s set%s(%s %s){\r\n", table + classType, CommonUtils.UnderlineToCap(column), javaClassShortName, attr));
//            sb.append(String.format("\t\tand(Field.%s.name(), %s, alias);\r\n", attr, attr));
            sb.append(String.format("\tpublic %s set%s(%s %s, boolean isHQL){\r\n", table + classType, CommonUtils.UnderlineToCap(column.getField().toLowerCase()), javaClassShortName, attr));
            sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), %s, alias);\r\n", attr, attr, attr));
            sb.append("\t\treturn this;\r\n");
            sb.append("\t}\r\n");

//            sb.append("\tpublic " + attrTypes[i] + " get" + xhxConvert(colnames[i].toLowerCase()) + "(){\r\n");
            sb.append(String.format("\tpublic %s get%s(){\r\n",javaClassShortName, CommonUtils.UnderlineToCap(column.getField().toLowerCase())));
//            sb.append("\t\treturn " + fieldConvert(colnames[i].toLowerCase()) + ";\r\n");
            sb.append(String.format("\t\treturn (%s) attrs.get(Field.%s.name());\r\n", javaClassShortName, attr));
            sb.append("\t}\r\n");

            switch (type.valueOf(javaClassShortName)){
                case String:
                    processLKMethod(sb, attr, javaClassShortName,table);
                    break;
                case Blod:
                    break;
                case Short:
                case Integer:
                case Long:
                case Float:
                case Double:
                case BigDecimal:
                case Date:
                case Time:
                case Timestamp:
                    processBetweenMethod(sb, attr, javaClassShortName ,table);
                    processCompareMethod(sb, attr, javaClassShortName,table);
                    break;

            }
            processInMethod(sb, attr, javaClassShortName,table);
            processNulMethod(sb, attr, javaClassShortName,table);


        }
    }

    public void processLKMethod(StringBuffer sb, String attr, String attrType, String table)
    {
        String[] r = new String[]{"LLk","RLK","LK"};
        for (int i = 0 ; i < r.length; i++) {
//            sb.append(String.format("\tpublic %s set%s%s(%s %s) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attrType, attr));
//            sb.append(String.format("\t\tand(Field.%s.name() , %s, Restriction.%s, alias);\r\n", attr, attr, r[i].toUpperCase()));
            sb.append(String.format("\tpublic %s set%s%s(%s %s, boolean isHQL) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attrType, attr));
            sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), %s, Restriction.%s, alias);\r\n", attr, attr, attr, r[i].toUpperCase()));
            sb.append("\t\treturn this;\r\n");
            sb.append("\t}\r\n");
        }

    }

    public void processCompareMethod(StringBuffer sb, String attr, String attrType, String table)
    {
        String[] r = new String[]{"NE","GE","GT","LT","LE"};
        for (int i = 0 ; i < r.length; i++)
        {
//            sb.append(String.format("\tpublic %s set%s%s(%s %s) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attrType, attr));
//            sb.append(String.format("\t\tand(Field.%s.name(), %s, Restriction.%s, alias);\r\n", attr, attr, r[i]));
            sb.append(String.format("\tpublic %s set%s%s(%s %s, boolean isHQL) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attrType, attr));
            sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), %s, Restriction.%s, alias);\r\n", attr, attr, attr, r[i]));
            sb.append("\t\treturn this;\r\n");
            sb.append("\t}\r\n");
        }

    }
    public void processBetweenMethod(StringBuffer sb, String attr, String attrType, String table)
    {
//        sb.append(String.format("\tpublic %s set%sBW(%s[] %s) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), attrType, attr));
//        sb.append(String.format("\t\tand(Field.%s.name(), %s, Restriction.BW, alias);\r\n", attr, attr));
        sb.append(String.format("\tpublic %s set%sBetween(%s[] %s, boolean isHQL) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), attrType, attr));
        sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), %s, Restriction.BW, alias);\r\n",attr, attr, attr));
        sb.append("\t\treturn this;\r\n");
        sb.append("\t}\r\n");

    }
    public void processInMethod(StringBuffer sb, String attr, String attrType, String table)
    {
        String[] r = new String[]{"In","NIn"};
        for (int i = 0 ; i < r.length; i++) {
//            sb.append(String.format("\tpublic %s set%s%s(List<Object> %s) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attr));
//            sb.append(String.format("\t\tand(Field.%s.name(), %s, Restriction.%s, alias);\r\n", attr, attr, r[i].toUpperCase()));
            sb.append(String.format("\tpublic %s set%s%s(List<Object> %s, boolean isHQL) {\r\n",table + classType, CommonUtils.UnderlineToCap(attr), r[i], attr));
            sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), %s, Restriction.%s, alias);\r\n", attr, attr, attr, r[i].toUpperCase()));
            sb.append("\t\treturn this;\r\n");
            sb.append("\t}\r\n");
        }
    }
    public void processNulMethod(StringBuffer sb, String attr, String attrType, String table)
    {
        String[] r = new String[]{"Nul","NNul"};
        for (int i = 0 ; i < r.length; i++) {

//            sb.append(String.format("\tpublic %s set%s%s() {\r\n", table + classType, CommonUtils.UnderlineToCap(attr), r[i]));
//            sb.append(String.format("\t\tand(Field.%s.name(), null, Restriction.%s, alias);\r\n", attr, r[i].toUpperCase()));
            sb.append(String.format("\tpublic %s set%s%s( boolean isHQL) {\r\n", table + classType, CommonUtils.UnderlineToCap(attr), r[i]));
            sb.append(String.format("\t\tand(isHQL ? Field.%s.name() : Field.%s.getColumn(), null, Restriction.%s, alias);\r\n", attr, attr, r[i].toUpperCase()));
            sb.append("\t\treturn this;\r\n");
            sb.append("\t}\r\n");
        }
    }


    private void processEnum(StringBuffer sb) {
        sb.append( "\tpublic enum "  + "Field {\r\n");
        sb.append("\t\t");

        for (Column column : columnJavaclass) {
            sb.append(String.format("%s(\"%s\"),", CommonUtils.fieldConvert(column.getField().toLowerCase()), column.getField()));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";\r\n");
        sb.append("\tprivate String column;\n" +
                "\n" +
                "\t\tprivate Field(String column) {\n" +
                "\t\t\tthis.column = column;\n" +
                "\t\t}\n" +
                "\t\tpublic String getColumn() {\n" +
                "\t\t\treturn column;\n" +
                "\t\t\t\n" +
                "\t\t}\n" +
                "\t\tpublic static String getSelects(Field...ignoreFields)\n" +
                "\t\t{\n" +
                "\t\t\treturn getSelects(ALIAS,ignoreFields);\n" +
                "\t\t}\n" +
                "\t\tpublic static String getSelects(String prefix, Field...ignoreFields){\n" +
                "\t\t\tStringBuilder sb = new StringBuilder();\n" +
                "            if (null != ignoreFields){\n" +
                "                Arrays.sort(ignoreFields);\n" +
                "            }\n" +
                "            for ( Field field : Field.values()) {\n" +
                "                if (null != ignoreFields && Arrays.binarySearch(ignoreFields, field) >= 0){\n" +
                "                    continue;\n" +
                "                }\n" +
                "\t\t\t\tsb.append(String.format(\" %s.`%s` %s, \", prefix, field.getColumn(), field.name()));\n" +
                "\t\t\t}\n" +
                "\t\t\tsb.deleteCharAt(sb.length() - 2);\n" +
                "\t\t\treturn sb.toString();\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tpublic String getSelect()\n" +
                "\t\t{\n" +
                "\t\t\treturn getSelect(ALIAS);\n" +
                "\t\t}\n" +
                "\t\tpublic String getSelect(String prefix)\n" +
                "\t\t{\n" +
                "\t\t\treturn String.format(\" %s.`%s` %s \", prefix, this.getColumn(), this.name());\n" +
                "\t\t}\n");
        sb.append("\n" +
                "\t\tpublic static String getSelect(Field...fields){\n" +
                "\t\t\treturn getSelect(ALIAS, fields);\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tpublic static String getSelect(String prefix,  Field...fields){\n" +
                "            if (null == fields || fields.length == 0){\n" +
                "                return \"\";\n" +
                "            }\n" +
                "\t\t\tStringBuilder sb = new StringBuilder();\n" +
                "            for ( Field field : fields) {\n" +
                "\t\t\t\tsb.append(String.format(\" %s.`%s` %s, \", prefix, field.getColumn(), field.name()));\n" +
                "\t\t\t}\n" +
                "\t\t\tsb.deleteCharAt(sb.length() - 2);\n" +
                "\t\t\treturn sb.toString();\n" +
                "\t\t}\r\n");
        sb.append("\t}\r\n");

    }
    private void processAllConstructor(StringBuffer sb) {
        sb.append(String.format("\tpublic %s%s() {\n", CommonUtils.UnderlineToCap(tablename),"DaoParams"));
        sb.append("\t\talias = ALIAS;\n" );
        sb.append("\t\twhere();\n" );
        if (addConstrucotr) {
            sb.append("\t\tsetIsDel(false, true);\n");
        }
        sb.append("\t}\r\n");
    }

    private void processAllConstant(StringBuffer sb) {
        sb.append(String.format("\tpublic static final String ALIAS = \"%s\";\n", CommonUtils.fieldConvert(tablename)));
        sb.append(String.format("\tpublic static final String TABLE = \"%s\";\n", tablename));
    }


    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {

        String javaClassShortName = null;
        for (Column column : columnJavaclass) {
            String columnName = column.getField().toLowerCase();
            javaClassShortName = column.getType().substring(column.getType().lastIndexOf(".") + 1);
            sb.append("\tprivate " + javaClassShortName + " " + CommonUtils.fieldConvert(columnName) + ";\r\n");
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
            String columnName = column.getField().toLowerCase();
            javaClassShortName = column.getType().substring(column.getType().lastIndexOf(".") + 1);

            sb.append("\tpublic void set" + CommonUtils.UnderlineToCap(columnName) + "(" + javaClassShortName + " " +
                    CommonUtils.fieldConvert(columnName) + "){\r\n");
            sb.append("\tthis." + CommonUtils.fieldConvert(columnName) + "=" + CommonUtils.fieldConvert(columnName) + ";\r\n");
            sb.append("\tadd(\"" + CommonUtils.fieldConvert(columnName) + "\"," + CommonUtils.fieldConvert(columnName) + ");\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic " + javaClassShortName + " get" + CommonUtils.UnderlineToCap(columnName) + "(){\r\n");
            sb.append("\t\treturn " + CommonUtils.fieldConvert(columnName) + ";\r\n");
            sb.append("\t}\r\n");
        }
    }


/*
    private void processConstructor(StringBuffer sb) {

        String str = "   public "+ CommonUtils.UnderlineToCap(tablename) + classType +"() {\n" +
                "        setDelState(false,true);\n" +
                "    }\r\n";
        sb.append(str);
    }
*/

    public void gen(String tablename, List<Column> columnJavaclass) {
        init(tablename, columnJavaclass);
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
