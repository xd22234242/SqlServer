package com.example.sqlserver.Form;

import com.example.sqlserver.Unit.SqlConUnit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServerSql extends JFrame {
    public LoginServerSql() throws SQLException {
        this.setBounds(500, 300, 300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        JComboBox mode = new JComboBox();
        JButton getDataBases = new JButton("...");

        mode.setBounds(50, 0, 200, 35);
        getDataBases.setBounds(200, 100, 50, 35);

        mode.addItem("Windows 身份验证");
        mode.addItem("SQL Server 身份验证");
        mode.setSelectedIndex(0);

        JTextField password = new JTextField();

        password.setBounds(50, 50, 150, 35);
        password.setEditable(false);

        JLabel passwordLabel = new JLabel("密码", JLabel.CENTER);

        passwordLabel.setBounds(0, 50, 50, 35);

        JButton login = new JButton("登录");

        login.setBounds(100, 150, 100,35);

        JComboBox databaseNames = new JComboBox();

        databaseNames.setBounds(50, 100, 150, 35);

        JPanel panel = new JPanel();

        panel.add(password);
        panel.add(mode);
        panel.add(passwordLabel);
        panel.add(login);
        panel.add(databaseNames);
        panel.add(getDataBases);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        SqlConUnit sqlConUnit = new SqlConUnit();

        getDataBases.addActionListener(actionEvent -> {
            try {
                sqlConUnit.setDbURL();
                String sql = "SELECT name " +
                        "FROM  master..sysdatabases " +
                        "WHERE name " +
                        "NOT IN ( 'master', 'model', 'msdb', 'tempdb', 'ReportServerTempDB' )";
                ResultSet res = sqlConUnit.executeQuery(sql);
                if (res == null) {
                    databaseNames.addItem(JOptionPane.showInputDialog("数据库名"));
                } else {
                    while (res.next()) {
                        databaseNames.addItem(res.getString("name"));
                    }
                }
                databaseNames.setSelectedIndex(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mode.addActionListener(actionEvent -> {
            if (mode.getSelectedIndex() == 0) {
                password.setEditable(false);
                sqlConUnit.setMode(0);
            } else {
                password.setEditable(true);
                sqlConUnit.setMode(1);
            }
        });

        login.addActionListener(actionEvent -> {
//            if (password.getText() == "") {
//                new ToolList();
//                dispose();
//            }
            SqlConUnit sqlConUnit1 = new SqlConUnit();
            if (mode.getSelectedIndex() == 1) {
                sqlConUnit1.setMode(1);
                Connection connection = null;
                try {
                    connection = DriverManager.getConnection(sqlConUnit1.getDbURL(),
                            "sa",
                            password.getText());
                    sqlConUnit1.setDbConn(connection);
                    sqlConUnit1.setDbURL(databaseNames);
                    JOptionPane.showMessageDialog(null,
                            "登录成功");
                    SwingArea.getInstance().initUI();
                    dispose();
                } catch (Exception e) {
                    System.out.println(e);
                    JOptionPane.showMessageDialog(null,
                            "登录失败，密码不正确");
                }
            } else {
                try {
                    sqlConUnit1.setMode(0);
                    sqlConUnit1.setDbURL(databaseNames);
                    sqlConUnit1.init();
                    SwingArea.getInstance().initUI();
                    dispose();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
