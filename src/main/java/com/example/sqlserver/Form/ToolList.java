package com.example.sqlserver.Form;

import com.example.sqlserver.MDBTool.FileFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolList extends JFrame {
    public ToolList() {
        this.setBounds(400, 300, 350, 200);
        this.setTitle("咸鱼专用");

        JComboBox toolList = new JComboBox();

        toolList.setBounds(50, 50, 150, 35);
        toolList.addItem("MDB解析");

        JButton submit = new JButton("确定");

        submit.setBounds(200, 50, 100, 35);

        JPanel panel = new JPanel();

        panel.add(toolList);
        panel.add(submit);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                switch (toolList.getSelectedIndex()) {
                    //mdb解析
                    case 0:
                        new FileFrame();
                        break;
                }

                dispose();
            }
        });
    }
}
