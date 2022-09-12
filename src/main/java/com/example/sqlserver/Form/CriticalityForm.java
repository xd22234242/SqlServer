package com.example.sqlserver.Form;

import com.example.sqlserver.ControlConfig.NumberTextField;
import com.example.sqlserver.Unit.SqlConUnit;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CriticalityForm extends JFrame {

    public CriticalityForm() throws SQLException {
        this.setBounds(150, 150, 600, 300);
        this.setResizable(false);
        this.setTitle("调整多条数据");

        JComboBox addSubControl = new JComboBox();
        JLabel channelListLabel = new JLabel("通道", JLabel.CENTER);
        JLabel addSubControlLabel = new JLabel("方式", JLabel.CENTER);
        JLabel dateLabel = new JLabel("日期", JLabel.CENTER);
        JTextField beginDate = new JTextField();
        JButton beginDateButton = new JButton("...");
        JTextField endDate = new JTextField();
        JButton endDateButton = new JButton("...");
        JLabel lineLabel = new JLabel("~", JLabel.CENTER);
        JComboBox region = new JComboBox(); //库房区域

        addSubControl.setBounds(350, 125, 150, 35);
        channelListLabel.setBounds(50, 125, 50, 35);
        addSubControlLabel.setBounds(300, 125, 50, 35);
        dateLabel.setBounds(50, 75, 50, 35);
        beginDate.setBounds(100, 75, 125, 35);
        beginDateButton.setBounds(225, 75, 50, 35);
        lineLabel.setBounds(275, 75, 50, 35);
        endDate.setBounds(325, 75, 125, 35);
        endDateButton.setBounds(450, 75, 50, 35);
        region.setBounds(100, 25, 175, 35);

        addSubControl.addItem("整体上调");
        addSubControl.addItem("整体下调");

        getRegion(region);      //获取库区列表

        JComboBox storeRoomList = new JComboBox();
        JComboBox storeRoomIdList = new JComboBox();

        getWarehouseList(region, storeRoomList, storeRoomIdList);

        String[] number = {"通道一", "通道二"};
        JComboBox channelList = new JComboBox();

        for (int i = 0; i < getChannelNumber((Integer) storeRoomIdList.getSelectedItem()); i++) {
            channelList.addItem(number[i]);
        }
        channelList.setSelectedIndex(0);

        storeRoomList.setBounds(325, 25, 175, 35);
        channelList.setBounds(100, 125, 150, 35);

        JTextField value = new JTextField();

        //value.setBounds(150, 50, 50, 35);
        value.setVisible(true);
        value.setText("0");
        value.setDocument(new NumberTextField());

        JLabel criticalityValueLabel = new JLabel("临界值", JLabel.CENTER);

        criticalityValueLabel.setBounds(50, 175, 50, 35);
        criticalityValueLabel.setVisible(true);

        JTextField criticalityValue = new JTextField();

        criticalityValue.setBounds(100, 175, 150, 35);
        criticalityValue.setDocument(new NumberTextField());
        criticalityValue.setVisible(true);

        JLabel adjustValueLabel = new JLabel("调整值", JLabel.CENTER);

        adjustValueLabel.setBounds(300, 175, 50, 35);
        adjustValueLabel.setVisible(true);

        JTextField adjustValue = new JTextField();

        adjustValue.setBounds(350, 175, 150, 35);
        adjustValue.setDocument(new NumberTextField());
        adjustValue.setVisible(true);

        JButton updateButton = new JButton("修改");

        updateButton.setBounds(250, 225, 100, 35);
        updateButton.setVisible(true);

        JPanel panel = new JPanel();

        panel.add(storeRoomList);
        panel.add(channelList);
        panel.add(value);
        panel.add(criticalityValueLabel);
        panel.add(criticalityValue);
        panel.add(updateButton);
        panel.add(addSubControl);
        panel.add(adjustValueLabel);
        panel.add(adjustValue);
        panel.add(channelListLabel);
        panel.add(dateLabel);
        panel.add(beginDateButton);
        panel.add(beginDate);
        panel.add(endDateButton);
        panel.add(endDate);
        panel.add(lineLabel);
        panel.add(addSubControlLabel);
        panel.add(region);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        storeRoomList.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (storeRoomList.getItemCount() == 0 || storeRoomIdList.getItemCount() == 0) {
                    return ;
                }
                int index = storeRoomList.getSelectedIndex();
                storeRoomIdList.setSelectedIndex(index);
                channelList.removeAllItems();
                for (int i = 0; i < getChannelNumber((Integer) storeRoomIdList.getSelectedItem()); i++) {
                    channelList.addItem(number[i]);
                }
                channelList.setSelectedIndex(0);
            }
        });

        //修改按钮
        updateButton.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!checkTime(beginDate.getText(), endDate.getText())) {
                    return ;
                }

                SwingArea swingArea = new SwingArea();

                //临界值
                double criticalityValueText = Double.parseDouble(criticalityValue.getText());
                //修改值
                double adjustValueText = Double.parseDouble(adjustValue.getText());
                //通道口
                int channelPort = channelList.getSelectedIndex() + 1;
                //调整方式 0:上调 1:下调
                int adjustManner = addSubControl.getSelectedIndex();
                //开始时间
                String beginTime = beginDate.getText();
                //结束时间
                String endTime = endDate.getText();
                //获取InfoId的值
                int infoId = getInfoId(storeRoomList.getSelectedItem().toString());

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        updateButton.setEnabled(false);
                        //调整数据
                        adjustData(criticalityValueText, infoId, channelPort, beginTime, endTime,
                                adjustManner, adjustValueText);
                        updateButton.setEnabled(true);
                    }
                }).start();
            }
        });

        beginDateButton.addActionListener(actionEvent -> {
            DateForm df = new DateForm();
            df.setModal(true);
            beginDate.setText(df.getDate());
        });

        endDateButton.addActionListener(actionEvent -> {
            DateForm df = new DateForm();
            df.setModal(true);
            endDate.setText(df.getDate());
        });

        region.addActionListener(actionEvent -> {
            try {
                storeRoomIdList.removeAllItems();
                storeRoomList.removeAllItems();
                getWarehouseList(region, storeRoomList, storeRoomIdList);
                for (int i = 0; i < getChannelNumber((Integer) storeRoomIdList.getSelectedItem()); i++) {
                    channelList.addItem(number[i]);
                }
                channelList.setSelectedIndex(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //获取通道数
    public int getChannelNumber(int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select ChannelNum from Dev_Info where Id = " + infoId;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        return res.getInt("ChannelNum");
    }

    //获取库区列表
    public void getWarehouseList(JComboBox region,
                                         JComboBox storeRoomList,
                                         JComboBox storeRoomIdList) throws SQLException {
        List<String> warehouseList = new ArrayList<>();
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "";
        ResultSet res = null;

        sql = (region.getSelectedIndex() == 0)
            ? "select Id, Name from Dev_Info"
            : "select Id, Name from Dev_Info " +
                    "where Id in " +
                    "(select DevId from WareHouse_Dev " +
                    "where WareHouseId = " +
                    "(select Id from WareHouse_Info " +
                    "where Name = '" + region.getSelectedItem() + "'))";

        res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            storeRoomList.addItem(res.getString("Name"));
            storeRoomIdList.addItem(res.getInt("Id"));
        }

        storeRoomIdList.setSelectedIndex(0);
        storeRoomList.setSelectedIndex(0);
    }

    //检查时间格式是否正确
    public boolean checkTime(String beginTime, String endTime) throws ParseException {
        if (beginTime.length() == 0 || endTime.length() == 0) {
            JOptionPane.showMessageDialog(null, "时间不得为空");
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date beginDate = dateFormat.parse(beginTime);
        Date endDate = dateFormat.parse(endTime);

        if (endDate.before(beginDate)) {
            JOptionPane.showMessageDialog(null,
                    "起始日期不得大于结束日期");
            return false;
        }

        return true;
    }

    //获取InfoId的值
    public int getInfoId(String value) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        ResultSet res;

        res = sqlConUnit.executeQuery("select Id from Dev_Info where Name = '" + value + "'");
        res.next();
        return res.getInt("Id");
    }

    //调整数据
    public void adjustData(double criticalityValue, int infoId, int channelPort,
                           String beginTime, String endTime,
                           int adjustManner, double adjustValue) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        ResultSet res;
        String sql = "";
        DecimalFormat dft = new DecimalFormat("0.0");

        sql = "update Record_Channel set Value = (Value " + (adjustManner == 0 ? "+" : "-") + " " + adjustValue + " ) " +
                "where InfoId = " + infoId + " " +
                "and channelPort = " + channelPort + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and Value " + (adjustManner == 0 ? "<" : ">") + " " + criticalityValue;

        sqlConUnit.executeUpdate(sql);

        //修改条数
        sql = "select @@RowCount AS NUM";
        res = sqlConUnit.executeQuery(sql);
        res.next();
        int updateNum = res.getInt("NUM");

        checkData(infoId, beginTime, endTime);
        deleteErrRuleData(beginTime, endTime, infoId);

        JOptionPane.showMessageDialog(null, "修改了 " + updateNum + " 条数据");
    }

    //删除不符合规则的数据
    public void deleteErrRuleData(String beginTime, String endTime,
                                  int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql;


        sql = "delete from Record_Channel " +
                "where DATEPART(mi, DevTime) % " + getRecordInterval(infoId) + " != 0 " +
                "and InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and DevTime not in ( " +
                "select DevTime from Record_Channel " +
                "where InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and IsOver = 1 )";

        sqlConUnit.executeUpdate(sql);

        sql = "delete from Record_Data " +
                "where DATEPART(mi, DevTime) % " + getRecordInterval(infoId) + " != 0 " +
                "and InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' " +
                "and IsDevOver = 0 ";

        sqlConUnit.executeUpdate(sql);
    }
    
    //获取记录间隔(s)
    public int getRecordInterval(int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select RecordInterval from Dev_Info " +
                "where Id = " + infoId;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        return (res.getInt("RecordInterval") / 60000);
    }

    //获取各个通道温度上下限
    public int[] getChannelUpperLower(int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql;
        ResultSet res;

        sql = "select ChannelNum from Dev_Info where Id = " + infoId;
        res = sqlConUnit.executeQuery(sql);
        res.next();

        int channelNum = res.getInt("ChannelNum");
        int[] channelUpperLower = new int[channelNum * 2];

        sql = "select Upper, Lower from Dev_Channel " +
                "where InfoId = " + infoId + " ";
        res = sqlConUnit.executeQuery(sql);

        int count = 0;
        while (res.next()) {
            channelUpperLower[count++] = res.getInt("Upper");
            channelUpperLower[count++] = res.getInt("Lower");
        }

        return channelUpperLower;
    }

    //检查数据异常
    public void checkData(int infoId,
                          String beginTime, String endTime) throws SQLException{
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "";
        ResultSet res;
        String temporarySql = "";

        int[] channelUpperLower = getChannelUpperLower(infoId);

        for (int i = 0; i < channelUpperLower.length / 2; i++) {
            sql = "update Record_Channel set IsOver = 0 " +
                    "where infoId = " + infoId + " " +
                    "and ChannelPort = " + (i + 1) + " " +
                    "and Value >= " + channelUpperLower[i * 2 + 1] + " " +
                    "and Value <= " + channelUpperLower[i * 2] + " " +
                    "and DevTime >= '" + beginTime + "' " +
                    "and DevTime <= '" + endTime + "' ";
            sqlConUnit.executeUpdate(sql);

            sql = "update Record_Channel set IsOver = 1 " +
                    "where infoId = " + infoId + " " +
                    "and ChannelPort = " + (i + 1) + " " +
                    "and (Value < " + channelUpperLower[i * 2 + 1] + " " +
                    "or Value > " + channelUpperLower[i * 2] + ") " +
                    "and DevTime >= '" + beginTime + "' " +
                    "and DevTime <= '" + endTime + "' ";
            sqlConUnit.executeUpdate(sql);
        }

        sql = "update Record_Data set IsDevOver = 1 " +
                "where DevTime in ( " +
                "select DevTime from Record_Channel " +
                "where IsOver = 1 " +
                "and InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' ) " +
                "and InfoId = " + infoId;
        sqlConUnit.executeUpdate(sql);

        sql = "update Record_Data set IsDevOver = 0 " +
                "where DevTime not in ( " +
                "select DevTime from Record_Channel " +
                "where IsOver = 1 " +
                "and InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' ) " +
                "and InfoId = " + infoId + " " +
                "and DevTime >= '" + beginTime + "' " +
                "and DevTime <= '" + endTime + "' ";
        sqlConUnit.executeUpdate(sql);
    }

    public void getRegion(JComboBox region) throws SQLException{
        region.addItem("全选");
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select Name from WareHouse_Info";
        res = sqlConUnit.executeQuery(str);

        while (res.next()) {
            region.addItem(res.getString("Name"));
        }

        region.setSelectedIndex(0);
    }
}
