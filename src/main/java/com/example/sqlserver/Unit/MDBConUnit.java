package com.example.sqlserver.Unit;

import java.io.File;
import java.sql.*;
import java.util.Properties;

public class MDBConUnit {
    public MDBConUnit(File mdbFile) {
        Properties prop = new Properties();
        prop.put("charSet", "gb2312"); // 这里是解决中文乱码
        prop.put("user", "admin");
        prop.put("password", "zs");
        String url = "jdbc:ucanaccess://"
                + mdbFile.getAbsolutePath();
        Statement stmt = null;
        ResultSet rs = null;
        String tableName = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            // 连接到mdb文件
            Connection conn = DriverManager.getConnection(url, prop);
            ResultSet tables = conn.getMetaData().getTables(
                    mdbFile.getAbsolutePath(), null, null,
                    new String[] { "TABLE" });
            // 获取第一个表名
            if (tables.next()) {
                tableName = tables.getString(3);// getXXX can only be used once
            } else {
                return;
            }
            stmt = (Statement) conn.createStatement();
            // 读取第一个表的内容
            rs = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "    ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
