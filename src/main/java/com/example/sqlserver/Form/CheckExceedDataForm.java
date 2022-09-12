package com.example.sqlserver.Form;

import com.example.sqlserver.Unit.SqlConUnit;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckExceedDataForm extends JFrame {
    List<Integer> exceedCount = new ArrayList<>();
    List<Integer> errCount = new ArrayList<>();

    public CheckExceedDataForm(JTable table) {
        this.setBounds(300, 200, 350, 400);
        this.setResizable(false);
        this.setTitle("超标检查");

        JProgressBar process = new JProgressBar();

        process.setBounds(50, 50, 250, 35);
        process.setStringPainted(true);

        JLabel scanTable = new JLabel();

        scanTable.setBounds(50, 100, 250, 35);

        JLabel exceedData = new JLabel();

        exceedData.setBounds(50, 150, 250, 35);

        JLabel errData = new JLabel();

        errData.setBounds(50, 200, 250, 35);

        JButton errButton = new JButton("异常数据查询");

        errButton.setBounds(50, 250, 150, 35);

        JButton exceedButton = new JButton("超标数据查询");

        exceedButton.setBounds(50, 300, 150, 35);

        JPanel panel = new JPanel();

        panel.add(process);
        panel.add(scanTable);
        panel.add(exceedData);
        panel.add(errData);
        panel.add(errButton);
        panel.add(exceedButton);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        try {
            process.setMaximum(table.getRowCount());
            process.setMinimum(0);

            scanTableData(table, process, scanTable, exceedData, errData);
        } catch (Exception e) {
            System.out.println(e);
        }

        errButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new errDataSearchForm(table, errCount, 0);
            }
        });

        exceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new errDataSearchForm(table, exceedCount, 1);
            }
        });
    }

    public void scanTableData(JTable table,
                              JProgressBar process,
                              JLabel scanContext,
                              JLabel exceedContext,
                              JLabel errContext) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                exceedContext.setText("超标数据：0");
                errContext.setText("异常数据：0");

                for (int i = 0; i < table.getRowCount(); i++) {
                    process.setValue(i + 1);
                    process.updateUI();
                    scanContext.setText("正在检查数据（" + (i + 1) + "/" + table.getRowCount() + ")");
                    boolean recordDataOver = databaseCheck(table.getValueAt(i, 2).toString(), table.getValueAt(i, 5).toString());
                    int passageOne = passageCheck(Integer.valueOf(table.getValueAt(i, 6).toString()), table.getValueAt(i, 5).toString(), 1);
                    int passageTwo = passageCheck(Integer.valueOf(table.getValueAt(i, 6).toString()), table.getValueAt(i, 5).toString(), 2);

                    if (passageOne == 2 || passageTwo == 2) {
                        errCount.add(i);
                        exceedContext.setText("异常数据：" + errCount.size());
                        continue;
                    }

                    if (!recordDataOver && passageOne == 0 && passageTwo == 0) {
                        continue;
                    } else if (recordDataOver && (passageOne == 1 || passageTwo == 1)) {
                        exceedCount.add(i);
                        exceedContext.setText("超标数据：" + exceedCount.size());
                        continue;
                    } else {
                        errCount.add(i);
                        errContext.setText("异常数据：" + errCount.size());
                        continue;
                    }
                }
            }
        }).start();
    }

    //检查Record_Data IsDevOver是否为1（超标）
    public boolean databaseCheck(String sn, String devTime) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select IsDevOver from Record_Data " +
                "where SN = '" + sn + "' " +
                "and DevTime = '" + devTime + "' ";
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();

        if (res.getInt("IsDevOver") == 1) {
            return true;
        }

        return false;
    }

    //检查Record_Channel 通道是否符合规范 (0表示正常，1表示超标，2表示异常)
    public int passageCheck(int infoId, String devTime, int channelPort) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select * from Record_Channel " +
                "where InfoId = " + infoId + " " +
                "and DevTime = '" + devTime + "' " +
                "and ChannelPort = " + channelPort;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        DecimalFormat to = new DecimalFormat("0.0");
        double value = res.getDouble("Value");
        int isOver = res.getInt("IsOver");

        sql = "select * from Dev_Channel where InfoId = " + infoId + " " +
                "and [Index] = " + channelPort;
        res = sqlConUnit.executeQuery(sql);
        res.next();
        double upperValue = res.getDouble("Upper");
        double lowerValue = res.getDouble("Lower");

        if (value <= upperValue && value >= lowerValue && isOver == 0) {
            return 0;
        } else if ((value > upperValue || value < lowerValue) && isOver == 1) {
            return 1;
        }
        return 2;
    }
}
