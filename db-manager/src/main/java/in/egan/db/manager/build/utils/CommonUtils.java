package in.egan.db.manager.build.utils;

import com.mysql.jdbc.MysqlDefs;

import java.sql.Types;

/**
 * Created by egan on 15-5-26.
 */
public class CommonUtils {

    /**
     * 功能：将输入字符串的首字母改成大写
     * @param str
     * @return
     */
    public static String toCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 去除下划线 并将跟在下划线的第一个字母转为大写：如 net_mzzo 转为：netMzzo
     * @param name
     * @return
     */
    public static String UnderlineToCap(String name) {
        String[] split = name.split("_");
        String _name = "";
        if (split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                _name += toCap(split[i]);
            }
        }
        return _name;
    }

    /**
     * 首字母小写
     * @param name
     * @return
     */
    public static String fieldConvert(String name) {
        name = UnderlineToCap(name);
        String first = name.substring(0, 1).toLowerCase();
        String rest = name.substring(1, name.length());
        String newStr = new StringBuffer(first).append(rest).toString();

        return newStr;
    }
    
   public static String getClassNameForJavaType(int javaType,
                                          boolean isUnsigned, int mysqlTypeIfKnown,
                                          boolean isBinaryOrBlob,
                                          boolean isOpaqueBinary) {
        switch (javaType) {
            case Types.BIT:
            case Types.BOOLEAN:
                return "java.lang.Boolean"; //$NON-NLS-1$

            case Types.TINYINT:

                if (isUnsigned) {
                    return "java.lang.Integer"; //$NON-NLS-1$
                }

                return "java.lang.Integer"; //$NON-NLS-1$

            case Types.SMALLINT:

                if (isUnsigned) {
                    return "java.lang.Integer"; //$NON-NLS-1$
                }

                return "java.lang.Integer"; //$NON-NLS-1$

            case Types.INTEGER:

                if (!isUnsigned ||
                        mysqlTypeIfKnown == 9) {
                    return "java.lang.Integer"; //$NON-NLS-1$
                }

                return "java.lang.Long"; //$NON-NLS-1$

            case Types.BIGINT:

                if (!isUnsigned) {
                    return "java.lang.Long"; //$NON-NLS-1$
                }

                return "java.math.BigInteger"; //$NON-NLS-1$

            case Types.DECIMAL:
            case Types.NUMERIC:
                return "java.math.BigDecimal"; //$NON-NLS-1$

            case Types.REAL:
                return "java.lang.Float"; //$NON-NLS-1$

            case Types.FLOAT:
            case Types.DOUBLE:
                return "java.lang.Double"; //$NON-NLS-1$

            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                if (!isOpaqueBinary) {
                    return "java.lang.String"; //$NON-NLS-1$
                }

                return "[B";

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:

                if (mysqlTypeIfKnown == 255) {
                    return "[B";
                } else if (isBinaryOrBlob) {
                    return "[B";
                } else {
                    return "java.lang.String";
                }

            case Types.DATE:
                return "java.sql.Date"; //$NON-NLS-1$

            case Types.TIME:
                return "java.sql.Time"; //$NON-NLS-1$

            case Types.TIMESTAMP:
                return "java.sql.Timestamp"; //$NON-NLS-1$

            default:
                return "java.lang.Object"; //$NON-NLS-1$
        }
    }


}
