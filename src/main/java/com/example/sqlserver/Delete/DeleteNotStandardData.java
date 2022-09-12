package com.example.sqlserver.Delete;

import com.example.sqlserver.ControlConfig.MultiComboBox;
import com.example.sqlserver.Form.DateForm;
import com.example.sqlserver.Unit.SqlConUnit;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//删除非标准间隔内的数据
public class DeleteNotStandardData extends JFrame {
    public DeleteNotStandardData() throws SQLException {
        this.setTitle("删除非标准间隔内的数据");
        this.setBounds(300, 400, 550, 300);
        this.setResizable(false);

        JComboBox interval = new JComboBox();
        JButton delete = new JButton("删除");
        JTextField startTime = new JTextField();
        JTextField endTime = new JTextField();
        JButton startTimeB = new JButton("...");
        JButton endTimeB = new JButton("...");
        JLabel symbol = new JLabel("~", JLabel.CENTER);
        JComboBox region = new JComboBox(); //库区
        MultiComboBox measurePoints = new MultiComboBox(new String[] {"全选"});  //测点

        interval.setBounds(50, 50, 100, 35);
        delete.setBounds(50, 150, 100, 35);
        startTime.setBounds(50, 100, 150, 35);
        startTimeB.setBounds(200, 100, 50, 35);
        endTime.setBounds(300, 100, 150, 35);
        endTimeB.setBounds(450, 100, 50, 35);
        symbol.setBounds(250, 100, 50, 35);
        region.setBounds(175, 50, 150, 35);
        measurePoints.setBounds(350, 50, 150, 35);

        interval.addItem(5);
        interval.addItem(30);

        interval.setSelectedIndex(1);

        measurePoints.setEnabled(true);

        regionInit(region);
        measurePointsInit(region, measurePoints);

        JPanel panel = new JPanel();

        panel.setLayout(null);

        panel.add(interval);
        panel.add(delete);
        panel.add(startTime);
        panel.add(startTimeB);
        panel.add(endTime);
        panel.add(endTimeB);
        panel.add(symbol);
        panel.add(region);
        panel.add(measurePoints);

        this.setContentPane(panel);
        this.setVisible(true);

        delete.addActionListener((actionEvent) -> {
            delete.setEnabled(false);

            try {
                deleteData(Integer.valueOf(interval.getSelectedItem().toString()),
                        startTime.getText(),
                        endTime.getText(),
                        getInfoIds(getMultiComboBox(measurePoints).split(",")));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            delete.setEnabled(true);
        });

        startTimeB.addActionListener((actionEvent -> {
            DateForm df = new DateForm();
            df.setModal(true);
            startTime.setText(df.getDate());
        }));

        endTimeB.addActionListener((actionEvent -> {
            DateForm df = new DateForm();
            df.setModal(true);
            endTime.setText(df.getDate());
        }));

        region.addActionListener(actionEvent -> {
            measurePointsInit(region, measurePoints);
        });
    }

    public void deleteData(int interval, String startTime, String endTime, String infoIds) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "delete from Record_Data " +
                "where DATEPART(mi, DevTime) % " + interval + " != 0 " +
                "and DevTime >= '" + startTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and (" + infoIds + ") ";
        sqlConUnit.executeUpdate(sql);

        sql = "delete from Record_Channel " +
                "where DATEPART(mi, DevTime) % " + interval + " != 0 " +
                "and DevTime >= '" + startTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and (" + infoIds + ") ";
        sqlConUnit.executeUpdate(sql);
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

    //库房值初始化
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
