package com.example.sqlserver.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateForm extends JDialog {
    private static Date now = new Date();
    private int day;

    public DateForm() {
        Point point = java.awt.MouseInfo.getPointerInfo().getLocation();

        this.setBounds(point.x - 150, point.y + 5, 350, 270);
        this.setUndecorated(true);
        this.setResizable(false);
        this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        this.setModal(true);

        JButton leftButton = new JButton("<");

        leftButton.setBounds(0, 0, 50, 35);

        JButton rightButton = new JButton(">");

        rightButton.setBounds(300, 0, 50, 35);

        JPanel panel = new JPanel();
        panel.add(leftButton);
        panel.add(rightButton);

        init(panel);

        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.MONTH, -1);
                now = calendar.getTime();
                updateDate(now, panel);
            }
        });

        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.MONTH, 1);
                now = calendar.getTime();
                updateDate(now, panel);
            }
        });

        this.setContentPane(panel);
        this.setVisible(true);
    }

    private static JLabel dateYM;

    public void init(JPanel panel) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
        dateYM = new JLabel(ft.format(now), JLabel.CENTER);

        dateYM.setBounds(50, 0, 250, 35);

        panel.add(dateYM);

        panel.setLayout(null);

        //加入天按钮
        for (int i = 0; i < getMonthDay(now); i++) {
            addDayButton(this, dateYM.getText(), true, i, panel);
        }

        for (int i = getMonthDay(now); i < 31; i++) {
            addDayButton(this, dateYM.getText(), false, i, panel);
        }
    }

    //获取一个月里有多少天
    public int getMonthDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    //设置天按钮
    public void addDayButton(JDialog jd, String text, boolean isDay, int day, JPanel panel) {
        JButton dayButton = new JButton(String.valueOf(day + 1));

        dayButton.setBounds((day % 7) * 50, (day / 7 + 1) * 40, 50, 35);
        dayButton.setEnabled(isDay);

        panel.add(dayButton);

        dayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               setDay(Integer.valueOf(dayButton.getText()));
               jd.dispose();
            }
        });
    }

    public String getDate() {
        if(day == 0) {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            return (ft.format(new Date()) + " 00:00");
        }
        return dateYM.getText() + "-" + String.format("%02d", day) + " 00:00";
    }
    
    public void updateDate(Date date, JPanel panel) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM");
        dateYM.setText(ft.format(date));
        dateYM.updateUI();

        //加入天按钮
        for (int i = 31; i < getMonthDay(now) + 3; i++) {
            Component component = panel.getComponent(i);
            JButton btn = (JButton) component;
            btn.setEnabled(true);
        }

        for (int i = getMonthDay(now) + 3; i < 34; i++) {
            Component component = panel.getComponent(i);
            JButton btn = (JButton) component;
            btn.setEnabled(false);
        }
    }

    public void setDay(int day) {
        this.day = day;
    }
}
