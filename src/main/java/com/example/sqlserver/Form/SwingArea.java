package com.example.sqlserver.Form;

import com.example.sqlserver.CMD.ExcelExportCmd;
import com.example.sqlserver.ControlConfig.MultiComboBox;
import com.example.sqlserver.Dao.DevInfoImpl;
import com.example.sqlserver.Dao.TempImpl;
import com.example.sqlserver.Delete.DeleteNotStandardData;
import com.example.sqlserver.Unit.SqlConUnit;
import com.example.sqlserver.pojo.DevInfo;
import com.example.sqlserver.pojo.Temp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SwingArea extends JFrame {
    private volatile static SwingArea instance = null;
    private static Boolean isMarking = false;  //超标是否标红
    private static Boolean isSqlChange = false; //修改表格的同时是否修改数据库
    ButtonGroup group;

    public SwingArea() {

    }

    public static SwingArea getInstance() {
        if (null == instance) {
            synchronized (SwingArea.class) {
                if(null == instance) {
                    instance = new SwingArea();
                }
            }
        }

        return instance;
    }

    public void initUI() throws SQLException {
        new SqlConUnit().init();
        this.setTitle("改数据前要备份！！！！"); //设置标题
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //设置退出模式
        this.setBounds(100, 100, 900, 730); //设置窗口位置大小

        //获取当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        //标签(测点)
        JLabel jlMeasurePoints = new JLabel("测点");

        jlMeasurePoints.setBounds(50, 50, 50, 35);

        //库区分级
        JComboBox<Object> jcWarehouses = new JComboBox<>();

        jcWarehouses.setBounds(100, 7, 150, 35);
        setWarehouses(jcWarehouses);

        //测点列表
        Object[] value = new String[1];
        value[0] = "全选";
        MultiComboBox warehouseArea = new MultiComboBox(value);

        warehouseArea.setBounds(100, 50, 150, 35);

        allWarehouseArea(warehouseArea);

        //设置温湿度最高值最低值
        setTHUpLower();

        //标签(开始时间)
        JLabel jlTimeStart = new JLabel("开始时间");

        jlTimeStart.setBounds(260, 50, 60, 35);

        //开始时间
        JTextField jtfTimeStart = new JTextField(dateFormat.format(calendar.getTime()));

        jtfTimeStart.setBounds(325, 50, 130, 35);

        //开始时间按钮
        JButton beginTimeButton = new JButton("...");

        beginTimeButton.setBounds(455, 50, 20, 35);

        //标签(结束时间)
        JLabel jlTimeEnd = new JLabel("结束时间");

        jlTimeEnd.setBounds(500, 50, 60, 35);

        //结束时间
        JTextField jtfTimeEnd = new JTextField(dateFormat.format(date));

        jtfTimeEnd.setBounds(565, 50, 130, 35);

        //开始时间按钮
        JButton endTimeButton = new JButton("...");

        endTimeButton.setBounds(695, 50, 20, 35);

        //查询按钮
        JButton jbSearch = new JButton("查询");

        jbSearch.setBounds(750, 50, 75, 35);

        //表格
        String[] title = {"ID", "库区", "SN", "通道一", "通道二", "记录时间", "InfoId", "超标"};
        JTable jTable = new JTable() {
            //设置表格不可编辑
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        DefaultTableModel defaultTableModel = (DefaultTableModel)jTable.getModel();
        defaultTableModel.setColumnIdentifiers(title);

        jTable.setBounds(50, 100, 775, 430);
        //设置表格不可拖动
        jTable.getTableHeader().setReorderingAllowed( false );
        //设置列高
        jTable.setRowHeight(30);
        // 设置表格中的数据居中显示
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Object.class,r);
        //隐藏第一列
        hideTableColumn(jTable, 0);
        //隐藏第六列
        hideTableColumn(jTable, 6);
        //隐藏第七列
        hideTableColumn(jTable, 7);

        //为表格设置滚动条
        JScrollPane jScrollPane = new JScrollPane();

        jScrollPane.setBounds(50, 100, 775, 430);
        jScrollPane.setViewportView(jTable);    //显示表格
        //jScrollPane.setHorizontalScrollBarPolicy(jScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(jScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);   //设置表格的纵向滚动条

        //菜单栏
        JMenuBar mb = new JMenuBar();
        //菜单
        JMenu fun = new JMenu("功     能");
        JMenu fun1 = new JMenu("| 观 赏 性 设 置");
        JMenu fun2 = new JMenu("| 数 据 库 连 接");
//        JMenu fun3 = new JMenu("| 选 择 性 修 改");
        JMenu fun4 = new JMenu("| 导 出 数 据");
        JMenu fun5 = new JMenu("| 数 据 检 查");
        JMenu fun6 = new JMenu("| 其 它 工 具");
        //菜单项
        JMenuItem setSize = new JMenuItem("设置每页显示的数量（建议设定值为∞）");
        JMenuItem deleteExceedData = new JMenuItem("删除非标准间隔数据");
//        JMenuItem deleteNoExceedData = new JMenuItem("删除表内数据（在间隔内出现的正常数据）");
        JMenuItem updateTableTData = new JMenuItem("对表内通道一数据进行统一增减");
        JMenuItem updateTableHData = new JMenuItem("对表内通道二数据进行统一增减");
        JMenuItem insertTableData = new JMenuItem("添加一段温湿度数据（功能待完善）");
        JMenuItem insertAloneData = new JMenuItem("添加一条数据");
//        JMenuItem chooseHumidity = new JMenuItem("湿度");
        JMenuItem updateTHData = new JMenuItem("批量调整数据");
        JMenuItem deleteTableSelectedRows = new JMenuItem("删除表中选中行");
        JMenuItem reviseTableSelectedRows = new JMenuItem("修改选中行为统一数据");
        JMenuItem insertExcelData = new JMenuItem("插入Excel表格数据");
        JMenuItem exportExcel = new JMenuItem("xls格式");
        JMenuItem exportExcelXlsx = new JMenuItem("xlsx格式");
        JMenuItem checkExceedData = new JMenuItem("超标&异常数据检查");
        JMenuItem otherTool = new JMenuItem("+");
        JMenuItem tableComletion = new JMenuItem("数据补全");

        JCheckBox isMarkRed = new JCheckBox("超标标红设置");
        JCheckBox isChangeData = new JCheckBox("数据库修改模式");

        //设置菜单栏（数据库连接）单选框
        group = new ButtonGroup();
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "SELECT name " +
                "FROM  master..sysdatabases " +
                "WHERE name " +
                "NOT IN ( 'master', 'model', 'msdb', 'tempdb', 'ReportServerTempDB' )";

        ResultSet res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            //addMenuRadioButton(res.getString("name"), fun2, jcWarehouseArea);
            addMenuRadioButton(res.getString("name"), fun2, warehouseArea, jcWarehouses);
        }

        fun.add(setSize);
        fun.addSeparator();
        fun.add(deleteExceedData);
        fun.addSeparator();
//        fun.add(deleteNoExceedData);
        fun.addSeparator();
        fun.add(deleteTableSelectedRows);
        fun.addSeparator();
        fun.add(reviseTableSelectedRows);
        fun.addSeparator();
        fun.add(updateTHData);
        fun.addSeparator();
        fun.add(updateTableTData);
        fun.addSeparator();
        fun.add(updateTableHData);
        fun.addSeparator();
        fun.add(insertTableData);
        fun.addSeparator();
        fun.add(insertAloneData);
        fun.addSeparator();
        fun.add(insertExcelData);
        fun.addSeparator();
        fun.add(tableComletion);

        fun1.add(isMarkRed);
        fun1.add(isChangeData);

//        fun3.add(chooseHumidity);

        fun4.add(exportExcel);
        fun4.add(exportExcelXlsx);

        fun5.add(checkExceedData);

        fun6.add(otherTool);

        mb.add(fun);
        mb.add(fun1);
        mb.add(fun2);
//        mb.add(fun3);
        mb.add(fun4);
        mb.add(fun5);
        mb.add(fun6);

        //画布
        JPanel panel = new JPanel();

        panel.add(jbSearch);
        //panel.add(jcWarehouseArea);
        panel.add(jlMeasurePoints);
        panel.add(jlTimeStart);
        panel.add(jtfTimeStart);
        panel.add(beginTimeButton);
        panel.add(jlTimeEnd);
        panel.add(jtfTimeEnd);
        panel.add(endTimeButton);
        panel.add(jScrollPane);
        panel.add(jcWarehouses);
        panel.setLayout(null);

        panel.add(warehouseArea);

        this.setContentPane(panel);
        this.setJMenuBar(mb);
        this.setVisible(true);

        //开始时间按钮
        beginTimeButton.addActionListener(actionEvent -> {
            DateForm df = new DateForm();
            jtfTimeStart.setText(df.getDate());
        });

        //结束时间按钮
        endTimeButton.addActionListener(actionEvent -> {
            DateForm df = new DateForm();
            jtfTimeEnd.setText(df.getDate());
        });

        //监听查询按钮
        jbSearch.addActionListener(actionEvent -> {
            ControlFunction controlFunction = new ControlFunction();
            //controlFunction.Search((String)jcWarehouseArea.getSelectedItem(), jtfTimeStart.getText(), jtfTimeEnd.getText(), jTable, panel);
            try {
                controlFunction.Search(getMultiComboBox(warehouseArea), jtfTimeStart.getText(), jtfTimeEnd.getText(), jTable, panel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //设置每页显示的数量
        setSize.addActionListener(actionEvent -> {
            try {
                String inputValue = JOptionPane.showInputDialog("请输入每页所显示的数量");
                if (inputValue != null) {
                    ControlFunction controlFunction = new ControlFunction();
                    controlFunction.setSize(Integer.parseInt(inputValue));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "请输入纯数字",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        //删除表内超标数据
        deleteExceedData.addActionListener(actionEvent -> {
//            int response = JOptionPane.showConfirmDialog(null,
//                    "是否删除表格内的超标数据",
//                    "删除",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE
//                    );

            try {
                deleteTableExceedData(jTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

//        //删除表内未超标数据
//        deleteNoExceedData.addActionListener(actionEvent -> {
//            int response = JOptionPane.showConfirmDialog(null,
//                    "是否删除表格内的未超标数据（温度达到范围却还是俩分钟一条的数据）",
//                    "删除",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE
//            );
//
//            if (response == 0) {
//                try {
//                    deleteTableNoExceedData(jTable);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //更新表内温度数据
        updateTableTData.addActionListener(actionEvent -> {
            try {
                String str = JOptionPane.showInputDialog("请输入通道一上调的数值（负数为下调）");
                double value1;
                if (str.contains("-")) {
                    value1 = Double.parseDouble(str.substring(1));
                    updateTData(-value1, jTable);
                } else {
                    value1 = Double.parseDouble(str);
                    updateTData(value1, jTable);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "请输入纯数字",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        //更新表内湿度数据
        updateTableHData.addActionListener(actionEvent -> {
            try {
                String str = JOptionPane.showInputDialog("请输入通道二上调的数值（负数为下调）");
                double value12;
                if (str.contains("-")) {
                    value12 = Double.parseDouble(str.substring(1));
                    updateHData(-value12, jTable);
                } else {
                    value12 = Double.parseDouble(str);
                    updateHData(value12, jTable);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "请输入纯数字",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        //超标标红设置
        isMarkRed.addActionListener(actionEvent -> {
            if(isMarkRed.isSelected()) {
                int index = JOptionPane.showConfirmDialog(null,
                        "是否开启超标标红（注：开启后程序可能会变卡）",
                        "提示",
                        JOptionPane.YES_NO_OPTION);

                if (index == 0) {
                    isMarkRed.setSelected(true);
                    isMarking = true;
                } else {
                    isMarkRed.setSelected(false);
                }
            } else {
                isMarking = false;
            }
        });

        //修改表格的同时是否修改数据库的监听
        isChangeData.addActionListener(actionEvent -> {
            if (isChangeData.isSelected()) {
                int index = JOptionPane.showConfirmDialog(null,
                        "是否开启修改模式，数据无价，修改后就无法还原了",
                        "模式选择",
                        JOptionPane.YES_NO_OPTION);
                if (0 == index) {
                    isSqlChange = true;
                    isChangeData.setSelected(true);
                } else {
                    isChangeData.setSelected(false);
                }
            } else {
                isSqlChange = false;
            }
        });

        //插入数据
        insertTableData.addActionListener(actionEvent -> {
//                String time = JOptionPane.showInputDialog("请输入插入的时间（20xx-xx-xx xx:xx）");
//                String value = JOptionPane.showInputDialog("请输入插入的温度");
//                double temp, humidity;
//                temp = value.contains("-") ? -Double.parseDouble(value.substring(1)) : Double.parseDouble(value);
//                value = JOptionPane.showInputDialog("请输入插入的湿度");
//                humidity = value.contains("-") ? -Double.parseDouble(value.substring(1)) : Double.parseDouble(value);
//
//                insertData((String) jTable.getValueAt(0, 2), time, (int) jTable.getValueAt(0, 6), temp, humidity);

            try {
                new InsertDataForm();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //修改局部湿度
//        chooseHumidity.addActionListener(new ActionListener() {
//            @SneakyThrows
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                double value = Double.parseDouble(JOptionPane.showInputDialog("请输入更改的湿度数据"));
//
//                for (int selectedRow : jTable.getSelectedRows()) {
//                    setHData(value, (Integer) jTable.getValueAt(selectedRow, 0));
//                    jTable.setValueAt(value + "%RH", selectedRow, 4);
//                }
//
//                JOptionPane.showMessageDialog(null,
//                        "更改了 " + jTable.getSelectedRows().length + " 条数据");
//            }
//        });

        //更新表内温湿度数据
        updateTHData.addActionListener(actionEvent -> {
            try {
                new CriticalityForm();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //对表格进行enter键注册
        InputMap enter = jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        enter.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        jTable.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jTable.editCellAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                JTextField jText = (JTextField) ( (DefaultCellEditor) jTable.getCellEditor(jTable.getSelectedRow(), jTable.getSelectedColumn())).getComponent();
                jText.requestFocus();
                jText.selectAll();
            }
        });

        //删除表内选中行
        deleteTableSelectedRows.addActionListener(actionEvent -> {
            if (jTable.getRowCount() == 0) {
                return ;
            }

            DefaultTableModel defaultTableModel1 = (DefaultTableModel) jTable.getModel();
            int index = JOptionPane.showConfirmDialog(null,
                    "是否删除选中行（慎重！删除后无法恢复）",
                    "delete",
                    JOptionPane.YES_NO_OPTION);

            if (1 == index) {
                return ;
            }

            int[] rows = jTable.getSelectedRows();

            for (int row : rows) {
                try {
                    deleteData(row, jTable);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < rows.length; i++) {
                defaultTableModel1.removeRow(rows[rows[i] - i]);
            }

            jTable.repaint();

            JOptionPane.showMessageDialog(null,
                    "删除了 " + rows.length + "行");
        });

        reviseTableSelectedRows.addActionListener(actionEvent -> {
            if (jTable.getRowCount() == 0) {
                return ;
            }

            DefaultTableModel defaultTableModel1 = (DefaultTableModel) jTable.getModel();
            int index = JOptionPane.showConfirmDialog(null,
                    "是否修改选中行（慎重！修改后无法撤回）",
                    "revise",
                    JOptionPane.YES_NO_OPTION);

            if (1 == index) {
                return ;
            }

            Object[] Channel = {"通道一", "通道二"};
            int result = JOptionPane.showOptionDialog(null, "请选择修改的通道", "选择", JOptionPane.YES_NO_CANCEL_OPTION ,JOptionPane.QUESTION_MESSAGE,null, Channel, Channel[0]);

            double data;

            try {
                data = Double.parseDouble(JOptionPane.showInputDialog(null, "请输入修改的数据"));
            } catch (Exception e) {
                System.out.println("输入错误！！！");
                return ;
            }

            AtomicInteger count = new AtomicInteger(0);
            int[] rows = jTable.getSelectedRows();
            ProcessForm process = new ProcessForm();
            process.setInterval(0, rows.length);
            new Thread(() -> {
                try {
                    switch (result) {
                        //通道一
                        case 0:
                            for (int row : rows) {
                                setTData(data,
                                        Integer.parseInt(jTable.getValueAt(row, 0).toString()),
                                        Integer.parseInt(jTable.getValueAt(row, 6).toString()));
                                process.setProcess(row + 1);
                                count.incrementAndGet();
                                process.setText("修改数据中...(" + count + " / " + rows.length + ")");
                            }
                            break;

                        //通道二
                        case 1:
                            for (int row : rows) {
                                setTDataTwo(data,
                                        Integer.parseInt(jTable.getValueAt(row, 0).toString()),
                                        Integer.parseInt(jTable.getValueAt(row, 6).toString()),
                                        jTable.getValueAt(row, 5).toString());
                                process.setProcess(row + 1);
                                count.incrementAndGet();
                                process.setText("修改数据中...(" + count + " / " + rows.length + ")");
                            }
                            break;
                    }
                } catch (Exception e) {
                    return ;
                }
            }).start();


        });

        //插入Excel数据
        insertExcelData.addActionListener(actionEvent -> new InsertExcelDataForm());

        //导出Excel数据
        exportExcel.addActionListener(actionEvent -> {
            ExcelExportCmd excelExportCmd = new ExcelExportCmd();
            String result = excelExportCmd.getTableContext(jTable);
            if (result.equals("")) {
                return ;
            }

            excelExportCmd.exportExcel(result, 0, getMultiComboBox(warehouseArea).replace(",", "-"));
        });

        //导出excel数据（xlsx格式）
        exportExcelXlsx.addActionListener(actionEvent -> {
            ExcelExportCmd excelExportCmd = new ExcelExportCmd();
            String result = excelExportCmd.getTableContext(jTable);
            if (result.equals("")) {
                return ;
            }

            excelExportCmd.exportExcel(result, 1, getMultiComboBox(warehouseArea).replace(",", "-"));
        });

        //超标检查
        checkExceedData.addActionListener(actionEvent -> new CheckExceedDataForm(jTable));

        //监听库区类型（jcWarehouses）
        jcWarehouses.addActionListener(actionEvent -> {
            if (jcWarehouses.getSelectedItem() == null)
                return ;

            if (jcWarehouses.getSelectedItem().toString().equals("全选")) {
                try {
                    allWarehouseArea(warehouseArea);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return ;
            }

            try {
                aloneWarehouseArea(warehouseArea, jcWarehouses);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //其他工具
        otherTool.addActionListener(actionEvent -> new ToolList());

        //插入单条数据
        insertAloneData.addActionListener(actionEvent -> {
            try {
                new InsertAloneData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        //数据补全
        tableComletion.addActionListener(actionEvent -> {
            new TableComletionForm();
        });
    }

    //是否超标标红
    public Boolean getIsMarking() {
        return isMarking;
    }

    //是否修改表格的同时修改数据库
    public Boolean getIsSqlChange() {
        return isSqlChange;
    }

    //温度湿度上下限
    private static int[] upperTemp;
    private static int[] lowerTemp;
    private static int upperHumidity;
    private static int lowerHumidity;

    //删除表内超标数据
    public void deleteTableExceedData(JTable table) throws SQLException {
//        if (table.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(null,
//                    "表格内容为空，请先查询再删除",
//                    "删除",
//                    JOptionPane.ERROR_MESSAGE);
//
//            return ;
//        }

//        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();

        try {
//            int interval = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入删除的间隔（五分钟一条填5）"));

            //TODO delete from xxx where DATAPART(mi, DevTime) % 30 != 0

            new DeleteNotStandardData();

//            ProcessForm processForm = new ProcessForm();
//            processForm.setInterval(0, table.getRowCount());
//
//            new Thread(() -> {
//                int rowCount = 0;   //记录超标条数
//                double temp;       //温度
//                double humidity;   //湿度
//                for (int i = 0; i < table.getRowCount(); i++) {
//                    //            if (table.getValueAt(i, 5).toString().substring(14, 16).equals("00")) {
//                    //                continue;
//                    //            } else if (table.getValueAt(i, 5).toString().substring(14, 16).equals("30")) {
//                    //                continue;
//                    //            }
//                    processForm.setText("扫描中(" + (i + 1) + "/" + table.getRowCount() + ") " + "已删除：" + rowCount);
//                    processForm.setProcess(i + 1);
//
//                    if (Integer.parseInt(table.getValueAt(i, 5).toString().substring(14, 16)) % interval == 0) {
//                        continue;
//                    }
//
//                    temp = Double.parseDouble(table.getValueAt(i, 3).toString().substring(0,
//                            table.getValueAt(i, 3).toString().length() - 1));
//                    humidity = Double.parseDouble(table.getValueAt(i, 4).toString().substring(0,
//                            table.getValueAt(i, 4).toString().length() - 3));
//
//                    if (temp > upperTemp[Integer.parseInt(table.getValueAt(i, 6).toString()) ]
//                            || temp < lowerTemp[Integer.parseInt(table.getValueAt(i, 6).toString())]
//                            || humidity > upperHumidity || humidity < lowerHumidity) {
//                        try {
//                            deleteData(i, table);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//
//                        defaultTableModel.removeRow(i--);
//                        rowCount++;
//                    }
//                }
//            }).start();

//            table.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

//        JOptionPane.showMessageDialog(null, "共删除了" + rowCount + "条超标数据",
//                "删除成功", JOptionPane.INFORMATION_MESSAGE);
    }

    //删除表内未超标数据
    public void deleteTableNoExceedData(JTable table) throws SQLException {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null,
                    "表格内容为空，请先查询再删除",
                    "删除",
                    JOptionPane.ERROR_MESSAGE);

            return ;
        }

        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();

        try {
            int interval = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入删除的间隔（五分钟一条填5）"));

            ProcessForm processForm = new ProcessForm();
            processForm.setInterval(0, table.getRowCount());

            new Thread(() -> {
                int rowCount = 0;   //记录超标条数
                double temp;       //温度
                double humidity;   //湿度
                for (int i = 0; i < table.getRowCount(); i++) {
                    //            if (table.getValueAt(i, 5).toString().substring(14, 16).equals("00")) {
                    //                continue;
                    //            } else if (table.getValueAt(i, 5).toString().substring(14, 16).equals("30")) {
                    //                continue;
                    //            }
                    processForm.setText("扫描中(" + (i + 1) + "/" + table.getRowCount() + ") " + "已删除：" + rowCount);
                    processForm.setProcess(i + 1);

                    if (Integer.parseInt(table.getValueAt(i, 5).toString().substring(14, 16)) % interval == 0) {
                        continue;
                    }

                    temp = Double.parseDouble(table.getValueAt(i, 3).toString().substring(0,
                            table.getValueAt(i, 3).toString().length() - 1));
                    humidity = Double.parseDouble(table.getValueAt(i, 4).toString().substring(0,
                            table.getValueAt(i, 4).toString().length() - 3));

                    if (temp <= upperTemp[Integer.parseInt(table.getValueAt(i, 6).toString())]
                            || temp >= lowerTemp[Integer.parseInt(table.getValueAt(i, 6).toString())]
                            || humidity <= upperHumidity || humidity >= lowerHumidity) {
                        try {
                            deleteData(i, table);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        defaultTableModel.removeRow(i--);
                        rowCount++;
                    }
                }
            }).start();

            table.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //删除数据库单条数据
    public void deleteData(int i, JTable table) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "delete from Record_Channel " +
                "where Id = " + Integer.valueOf(table.getValueAt(i, 0).toString()) + " " +
                "or Id = " + (Integer.parseInt(table.getValueAt(i, 0).toString()) + 1);
        sqlConUnit.executeUpdate(sql);

        sql = "delete from Record_Data " +
                "where SN = '" + table.getValueAt(i, 2).toString() + "' " +
                "and DevTime = '" + table.getValueAt(i, 5).toString() + "' ";
        sqlConUnit.executeUpdate(sql);
    }

    //设置温湿度度上下限
    public void setTHUpLower() throws SQLException {

        TempImpl temp = new TempImpl();
        List<Temp> tempList = temp.getTHUpperLower();
        int maxInfoId = 0;
        for (Temp temp1 : tempList) {
            maxInfoId = Math.max(temp1.getInfoId(), maxInfoId);
        }

        upperTemp = new int[maxInfoId + 1];
        lowerTemp = new int[maxInfoId + 1];
        for (Temp temp1 : tempList) {
            upperTemp[temp1.getInfoId()] = temp1.getUpperTemp();
            lowerTemp[temp1.getInfoId()] = temp1.getLowerTemp();
        }

        upperHumidity = 75;
        lowerHumidity = 35;
    }

    //获取温度上限
    public int getUpperTemp(int infoId) {
        return upperTemp[infoId];
    }

    //获取温度下限
    public int getLowerTemp(int infoId) {
        return lowerTemp[infoId];
    }

    //获取湿度上限
    public int getUpperHumidity() {
        return upperHumidity;
    }

    //获取湿度下限
    public int getLowerHumidity() {
        return lowerHumidity;
    }

    //更新表内温度数据
    public void updateTData(double value, JTable table) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null,
                    "表格内容为空，请先查询再修改",
                    "修改",
                    JOptionPane.ERROR_MESSAGE);

            return ;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        ProcessForm processForm = new ProcessForm();
        processForm.setInterval(0, table.getRowCount());

        new Thread(() -> {
            int rowCount = 0;   //记录修改条数
            double temp;        //温度值
            String suffix = null;   //获取温度后缀
            try {
                suffix = getTSuffix();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < table.getRowCount(); i++) {
                processForm.setProcess(i + 1);

                temp = Double.parseDouble(table.getValueAt(i, 3).toString().substring(0,
                        table.getValueAt(i, 3).toString().length() - 1));
                DecimalFormat to = new DecimalFormat("0.0");
                model.setValueAt(to.format(temp + value) + suffix, i, 3);
                try {
                    setTData(Double.parseDouble(to.format(temp + value)), Integer.parseInt((String) table.getValueAt(i, 0)), Integer.parseInt((String) table.getValueAt(i, 6)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rowCount++;
            }

            table.repaint();
            JOptionPane.showMessageDialog(null,
                    "共修改了 " + rowCount + " 条温度数据",
                    "修改",
                    JOptionPane.INFORMATION_MESSAGE);
        }).start();
    }

    //更新表内湿度数据
    public void updateHData(double value, JTable table) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null,
                    "表格内容为空，请先查询再修改",
                    "修改",
                    JOptionPane.ERROR_MESSAGE);

            return ;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        ProcessForm processForm = new ProcessForm();

        processForm.setInterval(0, table.getRowCount());

        new Thread(() -> {
            int rowCount = 0;   //记录修改条数
            double humidity;        //湿度值
            String suffix = null;   //获取湿度后缀
            try {
                suffix = getHSuffix();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < table.getRowCount(); i++) {
                processForm.setProcess(i + 1);

                humidity = Double.parseDouble(table.getValueAt(i, 4).toString().substring(0,
                        table.getValueAt(i, 4).toString().length() - 3));
                DecimalFormat to = new DecimalFormat("0.0");
                model.setValueAt(to.format(humidity + value) + suffix, i, 4);

                try {
                    setHData(Double.parseDouble(to.format(humidity + value)), Integer.parseInt((String) table.getValueAt(i, 0)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rowCount++;
            }

            table.repaint();
            JOptionPane.showMessageDialog(null,
                    "共修改了 " + rowCount + " 条湿度数据",
                    "修改",
                    JOptionPane.INFORMATION_MESSAGE);
        }).start();

    }

    //获取温度后缀
    public String getTSuffix() throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "select TypeUnit " +
                "from Record_Channel " +
                "where ChannelPort = 1";
        ResultSet res = sqlConUnit.executeQuery(str);

        res.next();
        return res.getString("TypeUnit");
    }

    //获取湿度后缀
    public String getHSuffix() throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "select TypeUnit " +
                "from Record_Channel " +
                "where ChannelPort = 2";
        ResultSet res = sqlConUnit.executeQuery(str);

        res.next();
        return res.getString("TypeUnit");
    }

    //修改数据库温度的数据
    public void setTData(double value, int id, int infoId) throws SQLException {
        int tempIsOver = 0;
        if (value > upperTemp[infoId] || value < lowerTemp[infoId]) {
            tempIsOver = 1;
        }

        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "update Record_Channel " +
                "set Value = " + value + ", IsOver = " + tempIsOver + " " +
                "where Id = " + id;
        sqlConUnit.executeUpdate(sql);

        sql = "select DataId, IsOver from Record_Channel " +
                "where Id = " + (id + 1);
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        int dataId = res.getInt("DataId");
        if (res.getInt("IsOver") == 1) {
            tempIsOver = 1;
        }

        sql = "update Record_Data " +
                "set IsDevOver = " + tempIsOver + " " +
                "where Id = " + dataId;
        sqlConUnit.executeUpdate(sql);
    }

    //修改数据库温度的数据，第二通道口
    public void setTDataTwo(double value, int id, int infoId, String devTime) throws SQLException {
        int tempIsOver = 0;
        if (value > upperTemp[infoId] || value < lowerTemp[infoId]) {
            tempIsOver = 1;
        }

        SqlConUnit sqlConUnit = new SqlConUnit();

        String sql = "select Id from Record_Channel " +
                "where devTime = '" + devTime + "' " +
                "and ChannelPort = 2 where InfoId = " + infoId;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        System.out.println(res.getInt("Id"));
        sql = "update Record_Channel " +
                "set Value = " + value + ", IsOver = " + tempIsOver + " " +
                "where Id = " + res.getInt("Id") + " " +
                "and InfoId = " + infoId;
        sqlConUnit.executeUpdate(sql);

        sql = "select DataId, IsOver from Record_Channel " +
                "where Id = " + id;
        res = sqlConUnit.executeQuery(sql);
        res.next();
        int dataId = res.getInt("DataId");
        if (res.getInt("IsOver") == 1) {
            tempIsOver = 1;
        }

        sql = "update Record_Data " +
                "set IsDevOver = " + tempIsOver + " " +
                "where Id = " + dataId;
        sqlConUnit.executeUpdate(sql);
    }

    //修改数据库湿度的数据
    public void setHData(double value, int id) throws SQLException {
        int humidityIsOver = 0;
        if (value > upperHumidity || value < lowerHumidity) {
            humidityIsOver = 1;
        }

        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "update Record_Channel " +
                "set Value = " + value + ", IsOver = " + humidityIsOver + " " +
                "where Id = " + (id + 1);
        sqlConUnit.executeUpdate(sql);

        sql = "select DataId, IsOver from Record_Channel " +
                "where Id = " + id;
        ResultSet res = sqlConUnit.executeQuery(sql);
        res.next();
        int dataId = res.getInt("DataId");
        if (res.getInt("IsOver") == 1) {
            humidityIsOver = 1;
        }

        sql = "update Record_Data " +
                "set IsDevOver = " + humidityIsOver + " " +
                "where Id = " + dataId;
        sqlConUnit.executeUpdate(sql);
    }

    //添加菜单栏（数据库连接）单选框按钮
    /*
    public void addMenuRadioButton(String name, JMenu fun, JComboBox jcWarehouseArea) {
        SqlConUnit sqlConUnit = new SqlConUnit();
        JRadioButton button;

        if (name.equals(sqlConUnit.getDatabaseName()))
            button = new JRadioButton(name, true);
        else
            button = new JRadioButton(name);

        button.setActionCommand(name);  //设置name为actionCommand
        group.add(button);
        fun.add(button);
        //构建监听，响应RadioButton事件
        button.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sqlConUnit.setDbURL(name);
                jcWarehouseArea.removeAllItems();
                DevInfoImpl mapper = new DevInfoImpl();
                List<DevInfo> devInfoList = mapper.getDevInfo();
                for (DevInfo devInfo : devInfoList) {
                    jcWarehouseArea.addItem(devInfo.name);
                }
            }
        });
    }
    */

    //添加菜单栏（数据库连接）单选框按钮（重载）
    public void addMenuRadioButton(String name, JMenu fun, MultiComboBox jcWarehouseArea, JComboBox<Object> jcWarehouses) {
        SqlConUnit sqlConUnit = new SqlConUnit();
        JRadioButton button;

        if (name.equals(sqlConUnit.getDatabaseName()))
            button = new JRadioButton(name, true);
        else
            button = new JRadioButton(name);

        button.setActionCommand(name);  //设置name为actionCommand
        group.add(button);
        fun.add(button);
        //构建监听，响应RadioButton事件
        button.addActionListener(actionEvent -> {
            jcWarehouses.removeAllItems();
            try {
                setWarehouses(jcWarehouses);

                sqlConUnit.setDbURL(name);

                allWarehouseArea(jcWarehouseArea);      //获取所有测点名称

                //设置温湿度上下限
                setTHUpLower();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    //隐藏表格列
    public void hideTableColumn(JTable table, int column) {
        table.getColumnModel().getColumn(column).setMaxWidth(0);
        table.getColumnModel().getColumn(column).setMinWidth(0);
        table.getColumnModel().getColumn(column).setPreferredWidth(0);
    }

    //插入数据
    public boolean insertData(String sn, String devTime, int infoId, double temp, double humidity) throws SQLException {
        //System.out.println(infoId + " " + sn + " " + temp + " " + humidity + " " + devTime);
        devTime += ":00.000";
        int tempIsOver = 0;
        int isDevOver = 0;
        int humidityIsOver = 0;
        int dataId;

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

        sql = "select Upper, Lower from Dev_Channel " +
                "where SN = '" + sn +"' ";
        res = sqlConUnit.executeQuery(sql);
        res.next();
        if (temp > res.getInt("Upper") || temp < res.getInt("Lower")) {
            tempIsOver = 1;
            isDevOver = 1;
        }
        res.next();
        if (humidity > res.getInt("Upper") || humidity < res.getInt("Lower")) {
            humidityIsOver = 1;
            isDevOver = 1;
        }

        sql = "insert into Record_Data " +
                "(SN, IsDevOver, InfoId, HexStr, Battery, [External], OverReason, Type, DevTime, AddTime, DataTime)" +
                "Values ('" + sn + "', " + isDevOver + ", " + infoId + ", '', 0, 255, '', 0, '" + devTime + "','" + devTime + "','" + devTime + "')";
        sqlConUnit.executeUpdate(sql);

        //插入温度
        sql = "insert into Record_Channel " +
                "(InfoId, ChannelPort, Value, IsOver, TypeName, TypeUnit, Format, DevTime) " +
                "VALUES " +
                "(" + infoId + ", 1, " + temp + ", " + tempIsOver + ", '温度', '℃', 0.1, '" + devTime + "')";
        sqlConUnit.executeUpdate(sql);

        sql = "select Id from Record_Data where DevTime = '" + devTime + "' " +
                "and InfoId = " + infoId;
        res = sqlConUnit.executeQuery(sql);

        res.next();
        dataId = res.getInt("Id");

        sql = "Update Record_Channel set DataId = " + dataId + " " +
                "where devTime = '" + devTime + "' " +
                "and InfoId = " + infoId + " " +
                "and ChannelPort = 1";

        sqlConUnit.executeUpdate(sql);

        //插入湿度
        sql = "insert into Record_Channel " +
                "(InfoId, ChannelPort, Value, IsOver, TypeName, TypeUnit, Format, DevTime) " +
                "VALUES " +
                "(" + infoId + ", 2, " + humidity + ", " + humidityIsOver + ", '湿度', '%RH', 0.1, '" + devTime + "')";
        sqlConUnit.executeUpdate(sql);

        sql = "Update Record_Channel set DataId =  " + dataId + " " +
                "where devTime = '" + devTime + "' " +
                "and InfoId = " + infoId + " " +
                "and ChannelPort = 2";

        sqlConUnit.executeUpdate(sql);

        return true;
    }

    //获取MultiComboBox值
    public String getMultiComboBox(MultiComboBox warehouseArea) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < warehouseArea.getSelectedValues().length; i++) {
            value.append(i == warehouseArea.getSelectedValues().length - 1 ? (warehouseArea.getSelectedValues()[i]) : (warehouseArea.getSelectedValues()[i] + ","));
        }

        return value.toString();
    }

    //设置库区类别的值
    public void setWarehouses(JComboBox<Object> warehouses) throws SQLException {
        warehouses.addItem("全选");
        warehouses.setSelectedIndex(0);
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select * from WareHouse_Info";
        ResultSet res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            warehouses.addItem(res.getString("Name"));
        }
    }

    //库区类型(全选) 获取所有测点
    public void allWarehouseArea(MultiComboBox warehouseArea) throws SQLException {
        List<String> warehouseAreas = new ArrayList<>();

        DevInfoImpl mapper = new DevInfoImpl();
        List<DevInfo> devInfoList = mapper.getDevInfo();
        for (DevInfo devInfo : devInfoList) {
            //jcWarehouseArea.addItem(devInfo.name);
            warehouseAreas.add(devInfo.name);
        }

        Object[] value = new String[warehouseAreas.size() + 1];
        value[0] = "全选";
        for (int i = 0; i < warehouseAreas.size(); i++) {
            value[i + 1] = warehouseAreas.get(i);
        }
        Object[] defaultValue = new String[] { value[1].toString() };
        warehouseArea.setValues(value);

        warehouseArea.setSelectValues(defaultValue);
    }

    //库区（单选）获取指定库区的所有测点
    public void aloneWarehouseArea(MultiComboBox warehouseArea, JComboBox<Object> warehouses) throws SQLException {
        List<String> warehouseAreas = new ArrayList<>();

        try {
            SqlConUnit sqlConUnit = new SqlConUnit();
            String str = "select Id, Name from Dev_Info where Id in " +
                    "(select DevId from WareHouse_Dev " +
                    "where WareHouseId = " +
                    "(select Id from WareHouse_Info " +
                    "where name = '" + warehouses.getSelectedItem() + "')) ";
            ResultSet res = sqlConUnit.executeQuery(str);
            while (res.next()) {
                warehouseAreas.add(res.getString("Name"));
            }

            Object[] value = new String[warehouseAreas.size() + 1];
            value[0] = "全选";
            for (int i = 0; i < warehouseAreas.size(); i++) {
                value[i + 1] = warehouseAreas.get(i);
            }

            Object[] defaultValue = new String[] { value[1].toString() };
            warehouseArea.setValues(value);

            warehouseArea.setSelectValues(defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
