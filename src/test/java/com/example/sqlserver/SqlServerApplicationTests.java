package com.example.sqlserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SqlServerApplicationTests {
    private static Connection dbConn = null;

    @Test
    void contextLoads() {
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=LuGe_Monitoring";

        try {
            //1.加载驱动
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("加载驱动成功！");
            //2.连接
            dbConn = DriverManager.getConnection(dbURL, "sa", "jxxl123456");
            System.out.println("连接数据库成功！");
            String sql="select distinct top 100 RC.Id, RC.Value, RC.TypeUnit, RD.SN, RC.DevTime, RC.TypeName," +
                    "DI.Name, DI.PortName " +
                    "from Record_Channel as RC " +
                    "left join Record_Data as RD " +
                    "on RD.InfoId = RC.InfoId " +
                    " " +
                    "left join Dev_Info as DI " +
                    "on DI.SN = RD.SN " +
                    "where DI.SN != '210316028N' " +
                    "and RC.DevTime >= '2022-03-05 15:00:00' " +
                    "and RC.DevTime <= '2022-03-05 16:00:00' " +
                    "order by RC.DevTime ";
            PreparedStatement statement=null;
            statement=dbConn.prepareStatement(sql);
            ResultSet res=null;
            res=statement.executeQuery();
//
//            System.out.println(res);
//            System.out.println(res.getMetaData().getColumnCount());
//            System.out.println(res.getMetaData().getColumnName(1));
//
//            List<String> columnName = new ArrayList<>();
//
//            for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
//                columnName.add(res.getMetaData().getColumnName(i));
//            }
//
//            System.out.println(columnName);
//            String str = "";
//            while(res.next()) {
//                for (int i = 0; i < columnName.size(); i++) {
//                    str += res.getString(columnName.get(i)) + " ";
//                }
//                System.out.println(str);
//                str = "";
//            }
            String tValue = ""; //温度
            String hValue = ""; //湿度
            String tTypeUnit = ""; //温度单位
            String hTypeUnit = ""; //湿度单位

            while(res.next()){
                int id = res.getInt("Id");
                if (id % 2 != 0) {
                    tValue = res.getString("Value");
                    tTypeUnit = res.getString("TypeUnit");
                    continue;
                }

                String name = res.getString("Name");
                String sn = res.getString("SN");
                hValue = res.getString("Value");
                hTypeUnit = res.getString("TypeUnit");
                String devTime = res.getString("DevTime");
                String typeName = res.getString("TypeName");
                devTime = devTime.substring(0, devTime.length() - 2);


                System.out.println(String.format("%-16s %10s %5s %-4s %5s %-4s %-23s",name, sn, tValue, tTypeUnit, hValue, hTypeUnit, devTime));
            }
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("连接数据库失败！");
        }
    }

}
