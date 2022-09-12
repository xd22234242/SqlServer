package com.example.sqlserver.MDBTool;

import com.example.sqlserver.Unit.SqlConUnit;

import javax.swing.*;
import java.sql.ResultSet;

public class MDBLogin extends JFrame {
    public MDBLogin() {
        this.setBounds(500, 300, 300, 200);
        this.setResizable(false);

        JLabel accountLabel = new JLabel("登录名");

        accountLabel.setBounds(50, 0, 50, 35);

        JTextField account = new JTextField("admin");

        account.setBounds(100, 0, 150, 35);

        JTextField password = new JTextField();

        password.setBounds(100, 50, 150, 35);

        JLabel passwordLabel = new JLabel("密码");

        passwordLabel.setBounds(50, 50, 50, 35);

        JButton login = new JButton("登录");

        login.setBounds(100, 100, 100,35);

        JPanel panel = new JPanel();

        panel.add(account);
        panel.add(password);
        panel.add(accountLabel);
        panel.add(passwordLabel);
        panel.add(login);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);
    }
}
