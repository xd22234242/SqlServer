package com.example.sqlserver.MDBTool;

import com.example.sqlserver.Unit.MDBConUnit;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileFrame extends JFrame {
    public FileFrame() {
        this.setBounds(300, 200, 550, 200);
        this.setResizable(false);
        this.setTitle("仅允许mdb类型文件");

        JTextField fileName = new JTextField();

        fileName.setBounds(50, 50, 350, 35);
        setJTextFileDragFile(fileName);

        JButton insert = new JButton("查看");

        insert.setBounds(400, 50, 100, 35);

        JPanel panel = new JPanel();

        panel.add(insert);
        panel.add(fileName);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!fileName.getText().contains(".mdb")) {
                    //非法文件
                    JOptionPane.showMessageDialog(null,
                            "非法文件");
                    return ;
                }
                new MDBConUnit(new File(fileName.getText()));
//                new MDBLogin();
                dispose();
            }
        });
    }

    public void setJTextFileDragFile(JTextField fileName) {
        fileName.setTransferHandler(new TransferHandler()
        {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

                    String filepath = o.toString();
                    if (filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length() - 1);
                    }
                    System.out.println(filepath);
                    fileName.setText(filepath);
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for (int i = 0; i < flavors.length; i++) {
                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
