package com.example.sqlserver.Unit;

import com.example.sqlserver.Form.LoginServerSql;
import com.example.sqlserver.Form.SwingArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.sql.*;

//TODO 登录失败后增加一个连接数据库的选项

@Data
public class SqlConUnit {
    private static Connection dbConn = null;
    private static String databaseName = "LuGe_Monitoring";
    private static String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=" + databaseName;
    private static int mode = 0; //默认为Window 身份验证连接方式

    public void init() throws SQLException {
        try {
            //1.加载驱动
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //2.连接
            if (mode == 1) {
                dbConn = DriverManager.getConnection(dbURL, "sa", "jxxl123456");
            } else {
                dbConn =  DriverManager.getConnection("jdbc:sqlserver://localhost:1433;integratedSecurity=true;DatabaseName=" + databaseName + ";useUnicode=true&characterEncoding=UTF-8");

            }

//            dbConn.createStatement();
        } catch (Exception e) {

        }
    }

    public SqlConUnit() {

    }

    public ResultSet executeQuery(String sql) {
        try {
//            PreparedStatement statement=null;
//            statement=dbConn.prepareStatement(sql);
//            ResultSet res=statement.executeQuery();;

            Statement statement = dbConn.createStatement();
            ResultSet res = statement.executeQuery(sql);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement statement=null;
        statement=dbConn.prepareStatement(sql);
        return statement.executeUpdate();
    }

    public void setDbURL(String databaseName) throws SQLException {
        this.databaseName = databaseName;
        if (mode == 1) {
            this.dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=" + databaseName;
        } else {
            this.dbURL = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;DatabaseName=" + databaseName+ ";useUnicode=true&characterEncoding=UTF-8";
        }

        init();
        JOptionPane.showMessageDialog(null,
                "已切换至 " + databaseName + " 数据库",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void setDbURL(JComboBox databaseNames) throws SQLException {
        this.databaseName = databaseNames.getSelectedItem().toString();
        if (mode == 1) {
            this.dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=" + databaseName;
        } else {
            this.dbURL = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;DatabaseName=" + databaseName + ";useUnicode=true&characterEncoding=UTF-8";
        }
        init();
    }

    public void setDbURL() throws SQLException {
        this.dbURL = "jdbc:sqlserver://localhost:1433;";
        init();
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getDbURL() {
        return this.dbURL;
    }

    public void setDbConn(Connection connection) throws SQLException {
        this.dbConn = connection;
        dbConn.createStatement();
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
