package com.example.sqlserver.Form;

import com.example.sqlserver.ControlConfig.NumberTextField;
import com.example.sqlserver.Unit.SqlConUnit;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class InsertDataForm extends JFrame {
    //左下角显示
    public static JLabel message = new JLabel();
    public static SwingArea swingArea = new SwingArea();
    public static int infoId;

    public InsertDataForm() throws SQLException {
        this.setBounds(300, 300, 550, 400);
        this.setResizable(false);
        this.setTitle("添加多条数据");

        //库区
        JComboBox warehouseArea = new JComboBox();

        warehouseArea.setBounds(50, 0, 150, 35);

        List<String> snList = new ArrayList<>();
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select Name, SN from Dev_Info";
        ResultSet res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            warehouseArea.addItem(res.getString("Name"));
            snList.add(res.getString("SN"));
        }

        //SN
        JTextField jTextField = new JTextField(snList.get(warehouseArea.getSelectedIndex()));

        jTextField.setBounds(250, 0, 150, 35);
        jTextField.setEditable(false);

        JTextField infoIdText = new JTextField();

        //InfoId
        infoIdText.setBounds(450, 0, 50, 35);
        infoIdText.setEditable(false);
        infoIdText.setText(String.valueOf(getInfoId(snList.get(warehouseArea.getSelectedIndex()))));

        infoId = Integer.parseInt(infoIdText.getText());

        //日期标签
        JLabel dateLabel = new JLabel("日期");

        dateLabel.setBounds(50, 85, 50, 35);

        //日期
        JTextField dateText = new JTextField();

        dateText.setBounds(100, 85, 120, 35);

        //日期按钮
        JButton dateButton = new JButton("...");

        dateButton.setBounds(220, 85, 50, 35);

        //一条线
        JLabel line = new JLabel("～", JLabel.CENTER);

        line.setBounds(270, 85, 50, 35);
        line.setVisible(true);

        //结束日期
        JTextField dateEndText = new JTextField();

        dateEndText.setBounds(320, 85, 120, 35);

        //结束日期按钮
        JButton dateEndButton = new JButton("...");

        dateEndButton.setBounds(440, 85, 50, 35);

        //------
        JLabel line1 = new JLabel("～", JLabel.CENTER);
        JLabel line2 = new JLabel("～", JLabel.CENTER);

        line1.setBounds(150, 130, 50, 35);
        line2.setBounds(150, 180, 50, 35);

        //通道一标签
        JLabel tempLabel = new JLabel("通道一");

        tempLabel.setBounds(50, 130, 50, 35);
        
        //通道一
        JTextField channelOneText1 = new JTextField();
        JTextField channelOneText2 = new JTextField();

        channelOneText1.setBounds(100, 130, 50, 35);
        channelOneText1.setDocument(new NumberTextField());
        channelOneText2.setBounds(200, 130, 50, 35);
        channelOneText2.setDocument(new NumberTextField());

        //通道一波动值标签
        JLabel tempUndulationLabel = new JLabel("波动值");

        tempUndulationLabel.setBounds(300, 130, 50, 35);
        tempUndulationLabel.setVisible(true);

        //波动值
        JTextField channelOneUndulationText = new JTextField("0");

        channelOneUndulationText.setBounds(350, 130, 50, 35);
        channelOneUndulationText.setDocument(new NumberTextField());
        channelOneUndulationText.setText("0");

        //通道二标签
        JLabel humidityLabel = new JLabel("通道二");

        humidityLabel.setBounds(50, 180, 50, 35);

        //通道二
        JTextField channelTwoText1 = new JTextField();
        JTextField channelTwoText2 = new JTextField();

        channelTwoText1.setBounds(100, 180, 50, 35);
        channelTwoText1.setDocument(new NumberTextField());
        channelTwoText2.setBounds(200, 180, 50, 35);
        channelTwoText2.setDocument(new NumberTextField());

        //通道二波动值标签
        JLabel channelTwoUndulationLabel = new JLabel("波动值");

        channelTwoUndulationLabel.setBounds(300, 180, 50, 35);
        channelTwoUndulationLabel.setVisible(true);

        //波动值
        JTextField channelTwoUndulationText = new JTextField("0");

        channelTwoUndulationText.setBounds(350, 180, 50, 35);
        channelTwoUndulationText.setDocument(new NumberTextField());
        channelTwoUndulationText.setText("0");

        //插入
        JButton insertDate = new JButton("插入");

        insertDate.setBounds(225, 230, 100, 35);

        //左下角显示
        message.setBounds(0, 350, 400, 0);

        JPanel panel = new JPanel();

        panel.add(warehouseArea);
        panel.add(jTextField);
        panel.add(infoIdText);
        panel.add(dateLabel);
        panel.add(dateText);
        panel.add(dateButton);
        panel.add(line);
        panel.add(dateEndText);
        panel.add(dateEndButton);
        panel.add(tempLabel);
        panel.add(channelOneText1);
        panel.add(tempUndulationLabel);
        panel.add(channelOneUndulationText);
        panel.add(humidityLabel);
        panel.add(channelTwoText1);
        panel.add(channelTwoUndulationLabel);
        panel.add(channelTwoUndulationText);
        panel.add(insertDate);
        panel.add(channelOneText2);
        panel.add(channelTwoText2);
        panel.add(line1);
        panel.add(line2);
        panel.add(message);
        panel.setLayout(null);


        //监听库区选项
        warehouseArea.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jTextField.setText(snList.get(warehouseArea.getSelectedIndex()));
                infoIdText.setText(String.valueOf(getInfoId(snList.get(warehouseArea.getSelectedIndex()))));
                infoId = Integer.parseInt(infoIdText.getText());
            }
        });

        //监听日期按钮
        dateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               DateForm df = new DateForm();
               df.setModal(true);
               dateText.setText(df.getDate());
            }
        });

        dateEndButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DateForm df = new DateForm();
                df.setModal(true);
                dateEndText.setText(df.getDate());
            }
        });

        //监听插入按钮
        insertDate.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (dateText.getText().length() == 0 || dateEndText.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "日期不得为空");

                    return ;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                Date beginDate = dateFormat.parse(dateText.getText());
                Date endDate = dateFormat.parse(dateEndText.getText());

                if (endDate.before(beginDate)) {
                    JOptionPane.showMessageDialog(null,
                            "起始日期不得大于结束日期");
                    return ;
                }

                if (endDate.equals(beginDate)) {
                    JOptionPane.showMessageDialog(null,
                            "起始日期不能与结束日期相同");
                    return ;
                }

//                insertDataOne(beginDate, endDate, jTextField, dateText, channelOneText1, channelTwoText1,
//                        channelOneText2, channelTwoText2, insertDate,
//                        channelOneUndulationText, channelTwoUndulationText);

                double[] channelValueUpperLower = new double[getChannelNum(jTextField.getText()) * 2];
                double[] channelUndulationValue = new double[getChannelNum(jTextField.getText())];

                switch (getChannelNum(jTextField.getText())) {
                    case 2:
                        channelValueUpperLower[2] = Double.parseDouble(channelTwoText1.getText());
                        channelValueUpperLower[3] = Double.parseDouble(channelTwoText2.getText());
                        channelUndulationValue[1] = Double.parseDouble(channelTwoUndulationText.getText());
                    case 1:
                        channelValueUpperLower[0] = Double.parseDouble(channelOneText1.getText());
                        channelValueUpperLower[1] = Double.parseDouble(channelOneText2.getText());
                        channelUndulationValue[0] = Double.parseDouble(channelOneUndulationText.getText());
                }

                insertDataTwo(beginDate, endDate, jTextField.getText(),
                        channelValueUpperLower, channelUndulationValue, insertDate);
           }
        });

        this.setContentPane(panel);
        this.setVisible(true);
    }

    public int getChannelNum(String sn) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "Select ChannelNum from Dev_Info " +
                "where SN = '" + sn + "'";
        ResultSet res = sqlConUnit.executeQuery(str);
        res.next();

        return res.getInt("ChannelNum");
    }

    public int getInfoId(String sn) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "Select Id from Dev_Info " +
                "where SN = '" + sn + "'";
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        return res.getInt("Id");
    }

    //标准数据，无波动，适用于30分钟一条
    public double[] createStandardData(double value1, double value2, int count) {
        double[] data = new double[count];

        DecimalFormat to = new DecimalFormat("0.0");
        double difference = (value2 - value1) / count;

        for (int i = 0; i < count; i++) {
            data[i] = Double.parseDouble(to.format(value1 + difference * i));
        }

        return data;
    }

    //获取通道二后缀名称
    public String getChannelTwoSuffixName(String sn) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select TypeName from Dev_Channel " +
                "where [Index] = 2 and SN = '" + sn + "'";
        ResultSet res = sqlConUnit.executeQuery(sql);

        res.next();
        return res.getString("TypeName");
    }

    //获取通道上下限
    public double[] getChannelUpperLower(String sn) throws SQLException{
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select ChannelNum from Dev_Info where SN = '" + sn + "'";
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        int channelNum= res.getInt("channelNum");
        double[] channelUpperLower = new double[channelNum * 2];
        int count = 0;

        for (int channelPort = 1; channelPort <= channelNum; channelPort++) {
            sql = "select Upper, Lower from Dev_Channel " +
                    "where [Index] = " + channelPort + " and SN = '" + sn + "'";
            res = sqlConUnit.executeQuery(sql);

            res.next();
            channelUpperLower[count++] = res.getInt("Upper");
            channelUpperLower[count++] = res.getInt("Lower");
        }

        return channelUpperLower;
    }

    //获取波动值范围内的随机数据
    public double getUndulationRandData(double undulationValue) {
        DecimalFormat to = new DecimalFormat("0.0");

        return Double.parseDouble(to.format((new Random()).nextDouble()
                * undulationValue
                * Math.pow((-1), (new Random()).nextInt(2) + 1)));
    }

    //获取记录间隔(m)
    public int getRecordInterval(int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select RecordInterval from Dev_Info " +
                "where Id = " + infoId;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        return (res.getInt("RecordInterval") / 60000);
    }

    //获取超标间隔(m)
    public int getRecordOverInterval(int infoId) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select OverInterval from Dev_Info " +
                "where Id = " + infoId;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        return (res.getInt("OverInterval") / 60000);
    }

    //整体呈一种趋势
    public void insertDataOne(Date beginDate,
                              Date endDate,
                              JTextField jTextField,
                              JTextField dateText,
                              JTextField channelOneText1,
                              JTextField channelTwoText1,
                              JTextField channelOneText2,
                              JTextField channelTwoText2,
                              JButton insertData,
                              JTextField channelOneUndulationText,
                              JTextField channelTwoUndulationText) throws SQLException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int day = (int) ((endDate.getTime() - beginDate.getTime()) / (1000*3600*24));

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(beginDate);

        int beginHour = calendar.get(Calendar.HOUR);
        int beginMinute = calendar.get(Calendar.MINUTE);

        calendar.setTime(endDate);

        int endHour = calendar.get(Calendar.HOUR);
        int endMinute = calendar.get(Calendar.MINUTE);


        int count = day * 48 + (endHour - beginHour) * (60 / getRecordInterval(infoId))
                + ((endMinute - beginMinute) >= 0
                ? (endMinute - beginMinute) / getRecordInterval(infoId)
                : ((endMinute - beginMinute) / getRecordInterval(infoId)) - 1) + 1;


        calendar.setTime(beginDate);

        if (beginMinute > 60 - getRecordInterval(infoId)) {
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE),
                    beginHour + 1,
                    0);
        } else {
            if (beginMinute % getRecordInterval(infoId) == 0) {
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE),
                        beginHour,
                        beginMinute);
            } else {
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE),
                        beginHour,
                        ((beginMinute / getRecordInterval(infoId)) + 1)
                                * getRecordInterval(infoId));
            }
        }

        //获取时间 dateFormat.format(calendar.getTime())

        double[] channelOneData = createStandardData(
                Double.parseDouble(channelOneText1.getText()),
                Double.parseDouble(channelOneText2.getText()),
                count);
        double[] channelTwoData = createStandardData(
                Double.parseDouble(channelTwoText1.getText()),
                Double.parseDouble(channelTwoText2.getText()),
                count);

        AtomicInteger item = new AtomicInteger(0);
        DecimalFormat to = new DecimalFormat("0.0");
        AtomicReference<Double> channelOneUndulation = new AtomicReference<>(0.0);
        AtomicReference<Double> channelTwoUndulation = new AtomicReference<>(0.0);
        double[] channelUpperLower = getChannelUpperLower(jTextField.getText());

        new Thread(() -> {
            insertData.setEnabled(false);
            for (int i = 0; i < count; i++) {
                try {
                    channelOneUndulation.set(getUndulationRandData(Double.parseDouble(channelOneUndulationText.getText())));
                    channelTwoUndulation.set(getUndulationRandData(Double.parseDouble(channelTwoUndulationText.getText())));
                    if (swingArea.insertData(jTextField.getText(),
                            dateFormat.format(calendar.getTime()),
                            infoId,
                            channelOneData[i] + channelOneUndulation.get(),
                            channelTwoData[i] + channelTwoUndulation.get())) {
                        item.incrementAndGet();
                    }

                    //判断俩个通道是否有超标现象
                    if ((channelOneData[i] + channelOneUndulation.get() > channelUpperLower[0]
                            || channelOneData[i] + channelOneUndulation.get() < channelUpperLower[1]
                            || channelTwoData[i] + channelTwoUndulation.get() > channelUpperLower[2]
                            || channelTwoData[i] + channelTwoUndulation.get() < channelUpperLower[3])
                            && i + 1 != count) {
                        for (int j = 0; j < getRecordInterval(infoId) / 2; j++) {
                            calendar.add(Calendar.MINUTE, 2);

                            channelOneUndulation.set(getUndulationRandData(Double.parseDouble(channelOneUndulationText.getText())));
                            channelTwoUndulation.set(getUndulationRandData(Double.parseDouble(channelTwoUndulationText.getText())));


                            if (channelOneData[i] + channelOneUndulation.get() > channelUpperLower[0]
                                    || channelOneData[i] + channelOneUndulation.get() < channelUpperLower[1]
                                    || channelTwoData[i] + channelTwoUndulation.get() > channelUpperLower[2]
                                    || channelTwoData[i] + channelTwoUndulation.get() < channelUpperLower[3]) {

                                if (swingArea.insertData(jTextField.getText(),
                                        dateFormat.format(calendar.getTime()),
                                        infoId,
                                        channelOneData[i] + channelOneUndulation.get(),
                                        channelTwoData[i] + channelTwoUndulation.get())) {
                                    item.incrementAndGet();
                                }
                            }
                        }
                    } else {
                        calendar.add(Calendar.MINUTE, getRecordInterval(infoId));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "总计插入 " + item.get() + " 条");
            insertData.setEnabled(true);
        }).start();
    }

    //有上有下
    public void insertDataTwo(Date beginDate,
                              Date endDate,
                              String sn,
                              double[] channelValueUpperLower,
                              double[] channelUndulationValue,
                              JButton insertData) throws SQLException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DecimalFormat df = new DecimalFormat("0.0");

        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();  //结束时间

        calendar.setTime(beginDate);
        calendarEnd.setTime(endDate);

        int overInterval = getRecordOverInterval(getInfoId(sn));
        int recordInterval = getRecordInterval(getInfoId(sn));
        int[] channelFlag = new int[channelValueUpperLower.length / 2];
        double[] value = new double[channelValueUpperLower.length / 2];
        double[] channelUpperLower = getChannelUpperLower(sn);
        int infoId = getInfoId(sn);
        String[] typeName = getTypeName(sn, channelValueUpperLower.length / 2);

        for (int i = 0; i < channelValueUpperLower.length / 2; i++) {
            channelFlag[i] = channelValueUpperLower[(i * 2)]
                    > channelValueUpperLower[(i * 2) + 1]
                    ? (-1) : 1;
            value[i] = channelValueUpperLower[(i * 2)];
        }

        for (int i = 0; i < getChannelNum(sn); i++) {
            if (channelValueUpperLower[i * 2] > channelValueUpperLower[i * 2 + 1]) {
                double channelValue = channelValueUpperLower[i * 2];
                channelValueUpperLower[i * 2] = channelValueUpperLower[i * 2 + 1];
                channelValueUpperLower[i * 2 + 1] = channelValue;
            }
        }

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                int count = 0;
                insertData.setEnabled(false);
                Random random = new Random();
                double temporaryValue = 0.0;
                double[] temporaryValueArr = new double[value.length];  //储存临时值的数组

                while (calendar.before(calendarEnd) || calendar.equals(calendarEnd)) {
                    if (calendar.get(Calendar.MINUTE) % recordInterval != 0) {
                        if (determineInsertOverData(value, channelUpperLower)) {
                            for (int i = 0; i < getChannelNum(sn); i++) {
                                if (typeName[i].equals("湿度")) {
                                    //如果获取的值超过限定范围，那么就循环取值
                                    do {
                                        temporaryValueArr[i] = Double.parseDouble(df.format(value[i] +
                                                round((channelUndulationValue[i])
                                                                * (Math.pow((-1), (random.nextInt(2) + 1))) * random.nextDouble(),
                                                        1, BigDecimal.ROUND_HALF_UP)));
                                    } while(temporaryValueArr[i] < channelValueUpperLower[i * 2]
                                            || temporaryValueArr[i] > channelValueUpperLower[i * 2 + 1]);

                                } else {
                                    temporaryValueArr[i] = value[i];
                                }
                            }
                            //插入数据
                            if (insertData(sn, dateFormat.format(calendar.getTime()),
                                    infoId, getChannelNum(sn), temporaryValueArr))
                                count++;
                        }
                        calendar.add(Calendar.MINUTE, overInterval);
                        continue;
                    } else {
                        for (int i = 0; i < typeName.length; i++) {
                            //获取临时的值
                            temporaryValue = value[i] +
                                    round(channelUndulationValue[i] * channelFlag[i] * random.nextDouble(),
                                            1, BigDecimal.ROUND_HALF_UP);

                            //判断有无超出给定的界限值
                            if (temporaryValue < channelValueUpperLower[i * 2]
                                    || temporaryValue > channelValueUpperLower[i * 2 + 1]) {
                                channelFlag[i] = -channelFlag[i];

                                value[i] = value[i] +
                                        round(channelUndulationValue[i] * channelFlag[i] * random.nextDouble(),
                                                1, BigDecimal.ROUND_HALF_UP);
                            } else {
                                value[i] = temporaryValue;
                            }
                        }

                        if (insertData(sn, dateFormat.format(calendar.getTime()),
                                infoId, value.length, value))
                            count++;

                        if (determineInsertOverData(value, channelUpperLower)) {
                            calendar.add(Calendar.MINUTE, overInterval);
                        } else {
                            calendar.add(Calendar.MINUTE, recordInterval);
                        }
                    }
                }

                insertData.setEnabled(true);
                JOptionPane.showMessageDialog(null, "成功添加" + count + "条数据");
            }
        }).start();
    }

    //插入数据
    public boolean insertData(String sn, String devTime,
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
            return false;
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
        }

        return true;
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

    //判断有无插入超标数据
    public boolean determineInsertOverData(double[] value, double[] channelUpperLower) throws SQLException {
        for (int i = 0; i < value.length; i++) {
            if (value[i] > channelUpperLower[(i * 2)]
                    || value[i] < channelUpperLower[(i * 2) + 1]) {
                return true;
            }
        }
        return false;
    }

    //对小数进行四舍五入 1.值 2.小数位数 3.规则
    public static double round(double value, int scale, int roundingMode)
    {
        BigDecimal bigData = new BigDecimal(value);
        bigData = bigData.setScale(scale, roundingMode);
        double dv = bigData.doubleValue();
        bigData = null;
        return dv;
    }
}
