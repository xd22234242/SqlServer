package com.example.sqlserver.Form;

import com.example.sqlserver.Unit.SqlConUnit;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class InsertAloneData extends JFrame {
    public InsertAloneData() throws SQLException {
        this.setBounds(200, 300, 500, 400);
        this.setResizable(false);
        this.setTitle("添加单条数据");

        JComboBox region = new JComboBox();
        JComboBox wareHouse = new JComboBox();
        JTextField date = new JTextField();
        JButton dateButton = new JButton("...");
        JLabel channelOneLabel = new JLabel("通道一", JLabel.CENTER);
        JLabel channelTwoLabel = new JLabel("通道二", JLabel.CENTER);
        JTextField channelOne = new JTextField();
        JTextField channelTwo = new JTextField();
        JButton insert = new JButton("添加");

        region.setBounds(50, 50, 150, 35);
        wareHouse.setBounds(250, 50, 150, 35);
        date.setBounds(50, 100, 200, 35);
        dateButton.setBounds(250, 100, 50, 35);
        channelOneLabel.setBounds(50, 150, 50, 35);
        channelOne.setBounds(100, 150, 50, 35);
        channelTwoLabel.setBounds(200, 150, 50, 35);
        channelTwo.setBounds(250, 150, 50, 35);
        insert.setBounds(150, 200, 100, 35);

        addRegion(region);
        addWareHouse(region, wareHouse);

        JPanel panel = new JPanel();

        panel.add(region);
        panel.add(wareHouse);
        panel.add(date);
        panel.add(dateButton);
        panel.add(channelOne);
        panel.add(channelTwoLabel);
        panel.add(channelOneLabel);
        panel.add(channelTwo);
        panel.add(insert);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        region.addActionListener(actionEvent -> {
            try {
                wareHouse.removeAllItems();
                addWareHouse(region, wareHouse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        dateButton.addActionListener(actionEvent -> {
            DateForm df = new DateForm();
            df.setModal(true);

            date.setText(df.getDate());
        });

        wareHouse.addActionListener(actionEvent -> {
            try {
                setChannelNum(wareHouse, channelOne, channelTwo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //插入按钮
        insert.addActionListener(actionEvent -> {
            try {
                double[] channelValue = new double[getChannelNum(wareHouse)];

                switch (getChannelNum(wareHouse)) {
                    case 2:
                        channelValue[1] = Double.parseDouble(channelTwo.getText());
                    case 1:
                        channelValue[0] = Double.parseDouble(channelOne.getText());
                }

                insertData(getSN(wareHouse), date.getText(), getInfoId(wareHouse),
                        getChannelNum(wareHouse), channelValue);
            } catch(Exception e) {
                e.printStackTrace();
            }

        });
    }

    //获取通道口数量
    public int getChannelNum(JComboBox wareHouse) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select ChannelNum from Dev_Info " +
                "where Name = '" + wareHouse.getSelectedItem() + "'";
        res = sqlConUnit.executeQuery(str);
        res.next();
        return res.getInt("ChannelNum");
    }

    //添加库区列表
    public void addRegion(JComboBox region) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        region.addItem("全选");

        str = "select Name from WareHouse_Info";
        res = sqlConUnit.executeQuery(str);
        while (res.next()) {
            region.addItem(res.getString("Name"));
        }

        region.setSelectedIndex(0);
    }

    //添加库房列表
    public void addWareHouse(JComboBox region, JComboBox wareHouse) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = region.getSelectedIndex() == 0
                ? "select Name from Dev_Info"
                : "select Name from Dev_Info " +
                "where Id in " +
                "(select DevId from WareHouse_Dev " +
                "where WareHouseId = " +
                "(select Id from WareHouse_Info " +
                "where Name = '" + region.getSelectedItem() + "'))";
        res = sqlConUnit.executeQuery(str);

        wareHouse.removeAllItems();

        while (res.next()) {
            wareHouse.addItem(res.getString("Name"));
        }
    }

    //设置通道数量
    public void setChannelNum(JComboBox wareHouse, JTextField channelOne, JTextField channelTwo) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select ChannelNum from Dev_Info " +
                "where Name = '" + wareHouse.getSelectedItem() + "'";
        res = sqlConUnit.executeQuery(str);
        res.next();

        switch (res.getInt("ChannelNum")) {
            case 2:
                channelTwo.setEditable(true);
                break;
            case 1:
                channelTwo.setEditable(false);
                break;
        }
    }

    //获取sn号
    public String getSN(JComboBox wareHouse) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select SN from Dev_Info " +
                "where Name = '" + wareHouse.getSelectedItem() + "' ";
        res = sqlConUnit.executeQuery(str);
        res.next();
        return res.getString("SN");
    }

    //获取InfoId
    public int getInfoId(JComboBox wareHouse) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select Id from Dev_Info " +
                "where Name = '" + wareHouse.getSelectedItem() + "' ";
        res = sqlConUnit.executeQuery(str);
        res.next();
        return res.getInt("Id");
    }

    //插入数据
    public void insertData(String sn, String devTime,
                           int infoId, int channelNum, double[] channelValue) throws SQLException {

        int[] isOver = getChannelIsOver(sn, channelNum, channelValue);
        int isDevOver = 0;
        int dataId;
        String[] typeName = getTypeName(sn, channelNum);
        String[] typeUnit = getTypeUnit(sn, channelNum);

        for (int i : isOver) {
            if (i == 1) {
                isDevOver = 1;
            }
        }

        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql;
        ResultSet res;

        sql = "select DevTime from Record_Data where DevTime = '" + devTime + "' " +
                "and InfoId = " + infoId;
        res = sqlConUnit.executeQuery(sql);
        res.next();
        try {
            res.getString("devTime");
            JOptionPane.showMessageDialog(null, "数据已存在，不得重复插入");
            return ;
        } catch (Exception e) {

        }

        sql = "insert into Record_Data " +
                "(SN, IsDevOver, InfoId, HexStr, Battery, [External], OverReason, Type, DevTime, AddTime, DataTime)" +
                "Values ('" + sn + "', " + isDevOver + ", " + infoId + ", '', 0, 255, '', 0, '" + devTime + "','" + devTime + "','" + devTime + "')";
        sqlConUnit.executeUpdate(sql);

        //数据插入
        for (int count = 0; count < channelNum; count++) {
            sql = "insert into Record_Channel " +
                    "(InfoId, ChannelPort, Value, IsOver, TypeName, TypeUnit, Format, DevTime) " +
                    "VALUES " +
                    "(" + infoId + "," + (count + 1) +" , " + channelValue[count] + ", " + isOver[count] + ", '" + typeName[count] + "', '" + typeUnit[count] + "', 0.1, '" + devTime + "')";
            sqlConUnit.executeUpdate(sql);

            sql = "select Id from Record_Data where DevTime = '" + devTime + "' " +
                    "and InfoId = " + infoId;
            res = sqlConUnit.executeQuery(sql);

            res.next();
            dataId = res.getInt("Id");

            sql = "Update Record_Channel set DataId = " + dataId + " " +
                    "where devTime = '" + devTime + "' " +
                    "and InfoId = " + infoId + " " +
                    "and ChannelPort = " + (count + 1);

            sqlConUnit.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "成功插入一条");
        }
    }

    //判断通道数据是否超标
    public int[] getChannelIsOver(String sn, int channelNum, double[] channelValue) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;
        int count = 0;
        int[] isOver = new int[channelNum];

        str = "select Upper, Lower from Dev_Channel " +
                "where SN = '" + sn + "' ";
        res = sqlConUnit.executeQuery(str);

        while(res.next()) {
            if (channelValue[count] > res.getDouble("Upper") ||
            channelValue[count] < res.getDouble("Lower")) {
                isOver[count] = 1;
            } else {
                isOver[count] = 0;
            }

            count++;
        }

        return isOver;
    }

    //获取类型名称
    public String[] getTypeName(String sn, int channelNum) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;
        int count = 0;
        String[] typeName = new String[channelNum];

        str = "select TypeName from Dev_Channel " +
                "where sn = '" + sn + "' ";
        res = sqlConUnit.executeQuery(str);

        while(res.next()) {
            typeName[count++] = res.getString("TypeName");
        }

        return typeName;
    }

    //获取类型后缀
    public String[] getTypeUnit(String sn, int channelNum) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;
        int count = 0;
        String[] typeUnit = new String[channelNum];

        str = "select TypeUnit from Dev_Channel " +
                "where sn = '" + sn + "' ";
        res = sqlConUnit.executeQuery(str);

        while(res.next()) {
            typeUnit[count++] = res.getString("TypeUnit");
        }

        return typeUnit;
    }
}
