package egan.in.db.manager.build;

import egan.in.db.manager.build.bean.Column;
import egan.in.db.manager.build.component.*;
import egan.in.db.manager.build.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-23 15:29:42
 */
public class GenCodeForMysql {
    
    private void genTable(String tableName, DatabaseMetaData metaData) {

        List<Column> columnJavaclass = new ArrayList<Column>();

        try {
            ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName);
            String pk = null;
            while (pkRs.next()){
               if ("PRIMARY".equals(pkRs.getString("PK_NAME"))){
                   pk = pkRs.getString("COLUMN_NAME");
                   break;
               }
            }

            ResultSet rs = metaData.getColumns(null, null, tableName, null);
            while (rs.next()){
                columnJavaclass.add( new Column(rs.getString("COLUMN_NAME"), rs.getString("COLUMN_NAME").equals(pk),  rs.getBoolean("IS_AUTOINCREMENT"), rs.getBoolean("IS_NULLABLE"), rs.getString("REMARKS"),  rs.getInt("ORDINAL_POSITION") ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //创建连接
        Connection con = null;
        //查要生成实体类的表
        String sql = String.format("select * from  %s where 1=2", tableName);
        PreparedStatement ps = null;
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ResultSetMetaData rsmd = ps.getMetaData();
            for (Column column :columnJavaclass){
                column.setType(getClassName(rsmd, column));
            }
            //生成组件
            new ControllerComponent().gen(tableName);
            new DaoComponent().gen(tableName, columnJavaclass);
            new DaoParamsComponent().gen(tableName, columnJavaclass);
            new EntityComponent().gen(tableName, columnJavaclass);
            new ServiceComponent().gen(tableName);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(con, null, ps);
        }
    }

    public void gen() {
        //创建连接
        Connection con = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();

            DatabaseMetaData metaData = con.getMetaData();
            rs = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});

            String tableName = null;
            while (rs.next()) {
                tableName = rs.getString(3);
//                System.out.println("表名：" + tableName);
                if ("DATABASECHANGELOG".equals(tableName) || "DATABASECHANGELOGLOCK".equals(tableName)) {
                    continue;
                }
                genTable(tableName, metaData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(con, rs, null);
        }
    }

    private String sqlType2JavaType(int javaType, String comment) {
        switch (javaType) {
            case Types.BIT:
            case Types.BOOLEAN:
            case Types.TINYINT:
                if (null == comment || !comment.matches(".*[0-9]+[\\:|\\：|\\,|\\，]+.*")){
                    return Boolean.class.getName();
                }
                return Integer.class.getName(); //$NON-NLS-1$
        }
        return null;
    }

    private String getClassName(ResultSetMetaData rsmd, Column column) throws SQLException {
        String className = sqlType2JavaType(rsmd.getColumnType(column.getOrdinalPosition()), column.getComment());
        if (className == null) {
            className = rsmd.getColumnClassName(column.getOrdinalPosition());
        }
        return className;
    }

}
