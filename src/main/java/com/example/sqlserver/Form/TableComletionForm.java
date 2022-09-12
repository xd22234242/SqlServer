package com.example.sqlserver.Form;

import com.example.sqlserver.ControlConfig.MultiComboBox;
import com.example.sqlserver.Unit.SqlConUnit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableComletionForm extends JFrame {
    public TableComletionForm() {
        this.setBounds(200, 200, 550, 200);
        this.setTitle("数据补全");
        this.setResizable(false);

        JLabel text = new JLabel("", JLabel.CENTER);
        JTextField beginTime = new JTextField();
        JTextField endTime = new JTextField();
        JButton beginTimeButton = new JButton("...");
        JButton endTimeButton = new JButton("...");
        JLabel symbol = new JLabel("~", JLabel.CENTER);
        JComboBox region = new JComboBox(); //库区
        MultiComboBox measurePoints = new MultiComboBox(new String[] {"全选"});  //测点

        text.setBounds(50, 0, 450, 35);
        beginTime.setBounds(50, 50, 150, 35);
        beginTimeButton.setBounds(200, 50, 50, 35);
        symbol.setBounds(250, 50, 50, 35);
        endTime.setBounds(300, 50, 150, 35);
        endTimeButton.setBounds(450, 50, 50, 35);
        region.setBounds(50, 100, 200, 35);
        measurePoints.setBounds(300, 100, 200, 35);

        text.setText("补全方式为采取前N天的数据补全");

        JPanel panel = new JPanel();

        panel.add(text);
        panel.add(beginTime);
        panel.add(beginTimeButton);
        panel.add(symbol);
        panel.add(endTime);
        panel.add(endTimeButton);
        panel.add(region);
        panel.add(measurePoints);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        beginTimeButton.addActionListener(actionEvent -> {
            DateForm dateForm = new DateForm();
            beginTime.setText(dateForm.getDate());
        });

        endTimeButton.addActionListener(actionEvent -> {
            DateForm dateForm = new DateForm();
            endTime.setText(dateForm.getDate());
        });

        region.addActionListener(actionEvent -> {
            try {
                regionInit((region));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            measurePointsInit(region, measurePoints);
        });
    }

    //库区值初始化
    public void regionInit(JComboBox region) throws SQLException {
        region.addItem("全选");
        region.setSelectedIndex(0);
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select * from WareHouse_Info";
        ResultSet res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            region.addItem(res.getString("Name"));
        }
    }

    //测点值初始化
    public void measurePointsInit(JComboBox region, MultiComboBox measurePoints) {
        List<String> warehouseAreas = new ArrayList<>();

        try {
            SqlConUnit sqlConUnit = new SqlConUnit();
            String str;
            str = region.getSelectedIndex() != 0
                    ? "select Name from Dev_Info where Id in " +
                    "(select DevId from WareHouse_Dev " +
                    "where WareHouseId = " +
                    "(select Id from WareHouse_Info " +
                    "where name = '" + region.getSelectedItem() + "')) "
                    : "select Name from Dev_Info";
            ResultSet res = sqlConUnit.executeQuery(str);
            while (res.next()) {
                warehouseAreas.add(res.getString("Name"));
            }

            Object[] value = new String[warehouseAreas.size() + 1];
            value[0] = "全选";
            for (int i = 0; i < warehouseAreas.size(); i++) {
                value[i + 1] = warehouseAreas.get(i);
            }

            measurePoints.setValues(value);

            measurePoints.setSelectValues(new Object[] { value[1] });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //通过Dev_Info的Name值获取InfoId的值
    public String getInfoIds(String[] value) throws SQLException {
        String infoIds = "";
        SqlConUnit sqlConUnit = new SqlConUnit();
        ResultSet res;

        for (int i = 0; i < value.length; i++) {
            System.out.println(value[i]);
            res = sqlConUnit.executeQuery("select Id from Dev_Info where Name = '" + value[i] + "'");
            res.next();
            infoIds += i == (value.length - 1) ? "InfoId = '" + res.getInt("Id") + "'" : "InfoId = '" + res.getInt("Id") + "' or ";
        }

        return infoIds;
    }

    //获取MultiComboBox值
    public String getMultiComboBox(MultiComboBox warehouseArea) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < warehouseArea.getSelectedValues().length; i++) {
            value.append(i == warehouseArea.getSelectedValues().length - 1 ? (warehouseArea.getSelectedValues()[i]) : (warehouseArea.getSelectedValues()[i] + ","));
        }

        return value.toString();
    }
}
