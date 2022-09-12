package com.example.sqlserver.Form;

import com.example.sqlserver.ControlConfig.MyTableCellRenderer;
import com.example.sqlserver.Unit.SqlConUnit;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;

public class ControlFunction {

    static int page = 1;
    static int size = 100000;

    static boolean isSearch = true; //判断是否是点击查询按钮
    static Vector data = new Vector(); //行
    static int rowCount = 0;   //获取总列数,由于表的原因，俩行合并成一行
    JTextField pageShow = new JTextField();
    static DefaultTableModel model = new DefaultTableModel();

    static JTextField showMessage = new JTextField();

    public void Search(String warehouseArea, String beginTime, String endTime, JTable table, JPanel panel) throws SQLException {

        String[] value = warehouseArea.split(",");
        String infoIds = getInfoIds(value);

        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select * from ( " +
                "select " +
                "COUNT(1) OVER() AS [rowCount], " +
                "(ROW_NUMBER() over (PARTITION BY p.Name " +
                "ORDER BY p.Id asc)) as row, " +
                "* from (" +
                "select " +
                "distinct " +
                "RC.Id, RC.Value, RC.TypeUnit, RC.DevTime, RC.TypeName, RC.ChannelPort, " +
                "RD.SN, RD.InfoId, RD.IsDevOver, " +
                "DI.Name, DI.PortName, DI.ChannelNum " +
                "from Record_Channel as RC " +
                "left join Record_Data as RD " +
                "on RD.Id = RC.DataId " +
                "left join Dev_Info as DI " +
                "on DI.SN = RD.SN " +
                "where (" + infoIds + ") " +
                "and RC.DevTime >=  '" + beginTime + "' " +
                "and RC.DevTime <=  '" + endTime + "' " +
                ") p " +
                ") p1 " +
                "where p1.row >= " + ((page - 1) * size * 2) + " " +
                "and p1.row <= " + (page * size * 2) + " " +
                "order by p1.DevTime desc, p1.Id asc";

        ResultSet res = sqlConUnit.executeQuery(sql);

        if(model.getRowCount() != 0) {
            model.getDataVector().clear();
        }

        Vector names = new Vector();    //列名
        String[] title = {"ID", "库区", "SN", "通道一", "通道二", "记录时间", "InfoId", "超标"};
        names.add(title[0]);
        names.add(title[1]);
        names.add(title[2]);
        names.add(title[3]);
        names.add(title[4]);
        names.add(title[5]);
        names.add(title[6]);
        names.add(title[7]);

        DecimalFormat to = new DecimalFormat("0.0");

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Vector row = new Vector();  //列
                row.setSize(8);
                boolean flag = true;    //节省rowCount重复赋值的资源
                int count = 0;

                while(res.next()) {
                    try {
                        if (flag) {
                            rowCount = res.getInt("rowCount") / 2;
                            flag = false;
                        }

                        if (res.getInt("ChannelPort") == 1) {
                            row.set(0, res.getObject("Id").toString());
                            row.set(1, getDIName(res));
                            row.set(2, res.getObject("SN").toString());
                            row.set(3, Double.parseDouble(to.format(res.getObject("Value")).toString()) + res.getObject("TypeUnit").toString());
                            row.set(5, res.getObject("DevTime").toString());
                            row.set(6, res.getObject("InfoId").toString());
                            row.set(7, res.getInt("IsDevOver"));

                            if (res.getInt("ChannelNum") == 1) {
                                data.add(row);
                                row = new Vector();
                                row.setSize(8);
                                count++;
                            }

                            continue;
                        }

//                        if (res.getInt("ChannelNum") == 2) {
//                            row.set(4, Double.parseDouble(to.format(res.getObject("Value")).toString()) + res.getObject("TypeUnit").toString());
//                            data.add(row);
//                            row = new Vector();
//                            row.setSize(8);
//                            count++;
//                        }
                        if (res.getInt("ChannelPort") == 2) {
                            row.set(4, Double.parseDouble(to.format(res.getObject("Value")).toString()) + res.getObject("TypeUnit").toString());
                            data.add(row);
                            row = new Vector();
                            row.setSize(8);
                            count++;
                        }

                        showMessage.setText("加载资源(" + count + "/" + rowCount + ")");
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }).start();


        model.setDataVector(data, names);
        table.setModel(model);

        SwingArea swingArea = new SwingArea();

        if (swingArea.getIsMarking()) {
            //超标标红
            TableColumn tempColumn = table.getColumn("通道一");
            TableColumn humidityColumn = table.getColumn("通道二");
            tempColumn.setCellRenderer(new MyTableCellRenderer());
            humidityColumn.setCellRenderer(new MyTableCellRenderer());
        }

        //表格的监听
        model.addTableModelListener(new TableModelListener() {
            @SneakyThrows
            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {
                if (swingArea.getIsSqlChange()) {
                    if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
                        //温度
                        if (tableModelEvent.getColumn() == 3) {
                            swingArea.setTData(Double.parseDouble(table.getValueAt(table.getSelectedRow(), 3).toString().replace("℃", "")),
                                    Integer.valueOf(table.getValueAt(table.getSelectedRow(), 0).toString()),
                                    Integer.valueOf(table.getValueAt(table.getSelectedRow(), 6).toString()));
                        } else if (tableModelEvent.getColumn() == 4) {  //湿度 or 温度
                            if (swingArea.getHSuffix().contains("℃")) {
                                swingArea.setTDataTwo(Double.parseDouble(table.getValueAt(table.getSelectedRow(), 4).toString().replace("℃", "")),
                                        Integer.valueOf(table.getValueAt(table.getSelectedRow(), 0).toString()),
                                        Integer.valueOf(table.getValueAt(table.getSelectedRow(), 6).toString()),
                                        table.getValueAt(table.getSelectedRow(), 5).toString());
                            } else if (swingArea.getHSuffix().contains("%RH")) {
                                swingArea.setHData(Double.parseDouble(table.getValueAt(table.getSelectedRow(), 4).toString().replace("%RH", "")),
                                        Integer.valueOf(table.getValueAt(table.getSelectedRow(), 0).toString()));
                            }

                        }
                    }
                }
            }
        });

        //左下角显示
        showMessage.setText("共查询了 " + rowCount + " 条数据");
        showMessage.setBounds(0, 630, 900, 35);
        showMessage.setVisible(true);  //设置可见
        showMessage.setHorizontalAlignment(JTextField.LEFT); //设置靠左
        showMessage.setEditable(false);    //设置不可编辑
        panel.add(showMessage);

        //隐藏第一列
        swingArea.hideTableColumn(table, 0);
        //隐藏第六列
        swingArea.hideTableColumn(table, 6);
        //隐藏第七列
        swingArea.hideTableColumn(table, 7);

        isSearch = true;

        JButton forPage = new JButton("上一页");
        JButton nextPage = new JButton("下一页");
        JButton headPage = new JButton("首页");
        JButton endPage = new JButton("尾页");


        forPage.setBounds(200, 550, 100, 50);
        nextPage.setBounds(550, 550, 100, 50);
        headPage.setBounds(50, 550, 100, 50);
        endPage.setBounds(700, 550, 100, 50);

        forPage.setVisible(true);
        nextPage.setVisible(true);
        headPage.setVisible(true);
        endPage.setVisible(true);

        panel.add(forPage);
        panel.add(nextPage);
        panel.add(headPage);
        panel.add(endPage);

        pageShow.setText(page + " / " + (rowCount / size + 1));
        pageShow.setBounds(400, 550, 75, 50);
        pageShow.setVisible(true);  //设置可见
        pageShow.setHorizontalAlignment(JTextField.CENTER); //设置居中
        pageShow.setEditable(false);    //设置不可编辑

        panel.add(pageShow);

        forPage.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (page != 1) {
                    page -= 1;
                    //清除数据
                    model.getDataVector().clear();
                    isSearch = false;
                    pageTurn(warehouseArea, beginTime, endTime, table,
                            model, names);
                }
            }
        });

        nextPage.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (data.size() == size || rowCount / size == page) {
                    page += 1;  //页数+1
                    isSearch = false;   //设置不是通过查询按钮访问的数据库
                    //清除数据
                    model.getDataVector().clear();
                    pageTurn(warehouseArea, beginTime, endTime, table,
                            model, names);
                }
            }
        });

        headPage.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(page != 1) {
                    isSearch = false;   //设置不是通过查询按钮访问的数据库
                    //清除数据
                    model.getDataVector().clear();
                    page = 1;
                    pageTurn(warehouseArea, beginTime, endTime, table,
                            model, names);
                }
            }
        });

        endPage.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (data.size() == size || rowCount / size == page) {
                    isSearch = false;   //设置不是通过查询按钮访问的数据库
                    //清除数据
                    model.getDataVector().clear();
                    page = rowCount / size + 1;
                    pageTurn(warehouseArea, beginTime, endTime, table,
                            model, names);
                }
            }
        });
    }

    public void setSize(int size) {
        this.size = size;
        System.out.println("size: " + this.size);
        JOptionPane.showMessageDialog(null,
                "修改成功，当前每页显示数据 " + size + " 条",
                "消息",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void pageTurn(String warehouseArea,
                         String beginTime,
                         String endTime,
                         JTable table,
                         DefaultTableModel model,
                         Vector names) throws SQLException {
        String[] value = warehouseArea.split(",");
        String values = new String();
        for (int i = 0; i < value.length; i++) {
            values += i == (value.length - 1) ? "DI.Name = '" + value[i] + "'" : "DI.Name = '" + value[i] + "' or ";
        }
        SqlConUnit sqlConUnit = new SqlConUnit();

        String sql = "select * from ( " +
                "select " +
                "COUNT(1) OVER() AS [rowCount], " +
                "(ROW_NUMBER() over (PARTITION BY p.Name " +
                "ORDER BY p.Id asc)) as row, " +
                "* from (" +
                "select " +
                "distinct " +
                "RC.Id, RC.Value, RC.TypeUnit, RC.DevTime, RC.TypeName, RC.ChannelPort, " +
                "RD.SN, RD.InfoId, " +
                "DI.Name, DI.PortName " +
                "from Record_Channel as RC " +
                "left join Record_Data as RD " +
                "on RD.Id = RC.DataId " +
                "left join Dev_Info as DI " +
                "on DI.SN = RD.SN " +
                "where (" + values + ") " +
                "and RC.DevTime >=  '" + beginTime + "' " +
                "and RC.DevTime <=  '" + endTime + "' " +
                ") p " +
                ") p1 " +
                "where p1.row >= " + ((page - 1) * size * 2) + " " +
                "and p1.row <= " + (page * size * 2) + " " +
                "order by p1.DevTime desc";
        ResultSet res = sqlConUnit.executeQuery(sql);

        Vector row = new Vector();  //行

        while(res.next()) {
            try {
                if (res.getInt("ChannelPort") % 2 != 0) {
                    row.add(0, res.getObject("Id").toString());
                    row.add(1, res.getObject("Name").toString());
                    row.add(2, res.getObject("SN").toString());
                    row.add(3, res.getObject("Value").toString() + res.getObject("TypeUnit").toString());
                    continue;
                }

                row.add(4, res.getObject("Value").toString() + res.getObject("TypeUnit").toString());
                row.add(5, res.getObject("DevTime").toString());
                row.add(6, res.getObject("InfoId").toString());

//                if(row.size() != title.length) {
//                    row = new Vector();
//                    continue;
//                }

//                System.out.println(row);
                data.add(row);
                row = new Vector();
            } catch (Exception e) {
                continue;
            }

        }

        model.setDataVector(data, names);
        table.setModel(model);

        SwingArea swingArea = new SwingArea();
        //隐藏第一列
        swingArea.hideTableColumn(table, 0);
        //隐藏第六列
        swingArea.hideTableColumn(table, 6);

        pageShow.setText(page + " / " + (rowCount / size + 1));
    }

    //通过Dev_Info的Name值获取InfoId的值
    public String getInfoIds(String[] value) throws SQLException {
        String infoIds = "";
        SqlConUnit sqlConUnit = new SqlConUnit();
        ResultSet res;

        for (int i = 0; i < value.length; i++) {
            res = sqlConUnit.executeQuery("select Id from Dev_Info where Name = '" + value[i] + "'");
            res.next();
            infoIds += i == (value.length - 1) ? "RD.InfoId = '" + res.getInt("Id") + "'" : "RD.InfoId = '" + res.getInt("Id") + "' or ";
        }

        return infoIds;
    }

    public String getDIName(ResultSet res) throws SQLException{
        try {
            res.getObject("Name");
            return res.getObject("Name").toString();
        } catch (Exception e) {
            return "nuLL";
        }
    }
}
