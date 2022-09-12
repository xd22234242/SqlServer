package com.example.sqlserver.Form;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class errDataSearchForm extends JFrame {
    //status 0:异常 1:超标
    public errDataSearchForm(JTable originalTable, List<Integer> rows, int status) {
        this.setBounds(300,200, 650, 450);
        this.setResizable(false);
        if (status == 0) {
            this.setTitle("异常数据列表");
        } else if (status == 1) {
            this.setTitle("超标数据列表");
        }

        JMenuBar mb = new JMenuBar();

        JMenu fun1 = new JMenu("批量修改");

        //批量增减表格温度数据
        JMenuItem function1 = new JMenuItem("温度");
        JMenuItem function2 = new JMenuItem("湿度");

        fun1.add(function1);
        fun1.add(function2);

        mb.add(fun1);

        //表格
        String[] title = {"ID", "库区", "SN", "通道一", "通道二", "记录时间", "InfoId", "超标"};
        JTable table = new JTable() {
            //设置表格不可编辑
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setBounds(50, 50, 550, 300);
        //设置表格不可拖动
        //table.getTableHeader().setReorderingAllowed( false );
        //设置列高
        table.setRowHeight(30);
        // 设置表格中的数据居中显示
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);

        DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
        defaultTableModel.setColumnIdentifiers(title);
        Vector line = new Vector();  //行
        Vector names = new Vector();   //列名
        Vector data = new Vector(); //行
        names.add(title[0]);
        names.add(title[1]);
        names.add(title[2]);
        names.add(title[3]);
        names.add(title[4]);
        names.add(title[5]);
        names.add(title[6]);
        names.add(title[7]);

        for (Integer row : rows) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                line.add(originalTable.getValueAt(row, i));
            }
            data.add(line);
            line = new Vector();
        }

        defaultTableModel.setDataVector(data, names);
        table.setModel(defaultTableModel);

        //隐藏第一列
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(0);
        //隐藏第七列
        table.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(6).setPreferredWidth(0);
        //隐藏第八列
        table.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(7).setPreferredWidth(0);

        //为表格设置滚动条
        JScrollPane jScrollPane = new JScrollPane(table);

        jScrollPane.setBounds(50, 50, 550, 300);
        jScrollPane.setViewportView(table);    //显示表格
        jScrollPane.setVerticalScrollBarPolicy(jScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);   //设置表格的纵向滚动条

        JPanel panel = new JPanel();

        panel.add(jScrollPane);

        panel.setLayout(null);

        this.setJMenuBar(mb);
        this.setContentPane(panel);
        this.setVisible(true);

        //批量添加温度数据
        function1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    SwingArea swingArea = new SwingArea();
                    String str = JOptionPane.showInputDialog("请输入温度更改的数值");
                    Double value;
                    if (str.contains("-")) {
                        value = Double.parseDouble(str.substring(1, str.length()));
                        swingArea.updateTData(-value, table);
                    } else {
                        value = Double.parseDouble(str);
                        swingArea.updateTData(value, table);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    JOptionPane.showMessageDialog(null,
                            "请输入纯数字",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //批量添加湿度数据
        function2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    SwingArea swingArea = new SwingArea();
                    String str = JOptionPane.showInputDialog("请输入湿度更改的数值");
                    Double value;
                    if (str.contains("-")) {
                        value = Double.parseDouble(str.substring(1, str.length()));
                        swingArea.updateHData(-value, table);
                    } else {
                        value = Double.parseDouble(str);
                        swingArea.updateHData(value, table);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    JOptionPane.showMessageDialog(null,
                            "请输入纯数字",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
