package com.example.sqlserver.Form;

import com.example.sqlserver.Dao.DevInfoImpl;
import com.example.sqlserver.Unit.SqlConUnit;
import com.example.sqlserver.pojo.DevInfo;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InsertExcelDataForm extends JFrame {
    JLabel textLabel = new JLabel();
    JProgressBar progressBar = new JProgressBar();

    public InsertExcelDataForm() {
        this.setTitle("插入表格数据");
        this.setBounds(300, 200, 550, 300);
        this.setResizable(false);

        JTextField fileName = new JTextField();

        fileName.setBounds(50, 50, 350, 35);
        setJTextFileDragFile(fileName);

        JButton insert = new JButton("插入");

        insert.setBounds(400, 50, 100, 35);

        textLabel.setBounds(50, 150, 350, 35);
        progressBar.setBounds(50, 100, 350, 35);
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel();

        panel.add(insert);
        panel.add(fileName);
        panel.add(textLabel);
        panel.add(progressBar);

        panel.setLayout(null);

        this.setContentPane(panel);
        this.setVisible(true);

        insert.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String excelData = getExcelContext(fileName.getText());

                if (excelData.equals("!@#$%^&*()_+")) {
                    JOptionPane.showMessageDialog(null,
                            "请检查你的表格是否正确");
                    return ;
                }

                if (!dataDealWith(excelData)) {
                    JOptionPane.showMessageDialog(null,
                            "插入失败");
                }
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

    public String getExcelContext(String fileName) throws IOException {
        String context = "";
        boolean isBlank = false;    //判断单元格是否为空

        if (fileName.contains(".xlsx")) {
            //创建工作簿对象
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(fileName));

            //获取工作簿下sheet的个数
            int sheetNumber = xssfWorkbook.getNumberOfSheets();
            //遍历工作簿中的所有数据（只选择第一个工作簿）
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            //获取总行数
            int maxRow = sheet.getLastRowNum();
            //遍历
            for (int row = 0; row <= maxRow; row++) {
                //获取单行内单元格总数
                int maxRol = sheet.getRow(row).getLastCellNum();
                if (maxRol < 5) {
                    return "!@#$%^&*()_+";
                }
                for (int rol = 0; rol < maxRol; rol++) {
                    if (sheet.getRow(row).getCell(rol) == null
                    || sheet.getRow(row).getCell(rol).equals("")
                    || sheet.getRow(row).getCell(rol).getCellType() == CellType.BLANK) {
                        return context;
                    }

                    if (rol == 0) {
                        if (sheet.getRow(row).getCell(rol).getCellType() == CellType.STRING) {
                            context += sheet.getRow(row).getCell(rol).getStringCellValue() + " ";
                        } else if (sheet.getRow(row).getCell(rol).getCellType() == CellType.NUMERIC) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            context += sdf.format(HSSFDateUtil.getJavaDate(sheet.getRow(row).getCell(rol).getNumericCellValue())) + " ";
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            context += sdf.format(sheet.getRow(row).getCell(rol).getNumericCellValue()) + " ";
                        }
                    } else {
                        context += sheet.getRow(row).getCell(rol) + " ";
                    }
                }
                context += '\n';
            }
        } else if (fileName.contains(".xls")) {
            //创建工作簿对象
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(fileName));
            //获取工作簿下sheet的个数
            int sheetNumber = hssfWorkbook.getNumberOfSheets();
            //遍历工作簿中的所有数据（只选择第一个工作簿）
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            //获取总行数
            int maxRow = sheet.getLastRowNum();
            //遍历
            for (int row = 0; row <= maxRow; row++) {
                //获取单行内单元格总数
                int maxRol = sheet.getRow(row).getLastCellNum();
                if (maxRol < 5) {
                    return "!@#$%^&*()_+";
                }
                for (int rol = 0; rol < maxRol; rol++) {
                    if (sheet.getRow(row).getCell(rol) == null
                            || sheet.getRow(row).getCell(rol).equals("")
                            || sheet.getRow(row).getCell(rol).getCellType() == CellType.BLANK) {
                        return context;
                    }

                    if (rol == 0) {
                        if (sheet.getRow(row).getCell(rol).getCellType() == CellType.STRING) {
                            context += sheet.getRow(row).getCell(rol).getStringCellValue() + " ";
                        } else if (sheet.getRow(row).getCell(rol).getCellType() == CellType.NUMERIC) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            context += sdf.format(HSSFDateUtil.getJavaDate(sheet.getRow(row).getCell(rol).getNumericCellValue())) + " ";
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            context += sdf.format(sheet.getRow(row).getCell(rol).getNumericCellValue()) + " ";
                        }
                    } else if (rol == 2) {
                        if (sheet.getRow(row).getCell(rol).getCellType() == CellType.NUMERIC) {
                            DecimalFormat df = new DecimalFormat("0");
                            String v = df.format(sheet.getRow(row).getCell(rol).getNumericCellValue());
                            context += subZeroAndDot(v) + " ";
                        } else {
                            context += sheet.getRow(row).getCell(rol) + " ";
                        }
                    } else {
                        context += sheet.getRow(row).getCell(rol) + " ";
                    }
                }
                context += '\n';
            }
        } else {
            return null;
        }

        return context;
    }

    private String subZeroAndDot(String s) {
        if (s.indexOf(".0") > 0) {
            // 去掉多余的
            s = s.replaceAll("0+?$", "");
            // 如果最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    public boolean dataDealWith(String data) throws SQLException, ParseException {
        int col = 0;
        int rowCount = 0;
        String str = "";
        boolean isEqual = false;
        boolean isFirstRow = true;  //判断是否是首行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        DevInfoImpl devInfo = new DevInfoImpl();
        List<DevInfo> devInfoList = devInfo.getDevInfo();

        for (String s : data.split("\n")) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            for (String s1 : s.split(" ")) {
                if (col == 0) {     //日期
                    str += s1 + " ";
                    col++;
                    continue;
                } else if (col == 1) {  //时间
                    str += s1 + "#";
                    col++;
                    continue;
                }
                else if (col == 2) {  //库名
                    for (DevInfo info : devInfoList) {
                        if (info.name.equals(s1)) {
                            isEqual = true;
                            break;
                        }
                    }
                    if (!isEqual) {
                        return false;
                    }
                    isEqual = false;
                } else if (col == 3) {  //SN
                    for (DevInfo info : devInfoList) {
                        if (info.sn.equals(s1)) {
                            isEqual = true;
                            break;
                        }
                    }
                    if (!isEqual) {
                        return false;
                    }
                    isEqual = false;
                }
                str += s1 + "#";
                col++;
            }
            str = str.substring(0, str.length() - 1);
            col = 0;
            rowCount++;
            str += "@";
        }

//        System.out.println(rowCount);

        String[] dateList = new String[rowCount];
        String[] wareList = new String[rowCount];
        String[] snList = new String[rowCount];
        String[] exceedList = new String[rowCount];
        String[] dataList = new String[rowCount];
        col = 0;
        int row = 0;

        for (String s : str.split("@")) {
            dataList[row] = "";
            for (String s1 : s.split("#")) {
                switch (col) {
                    case 0:
                        dateList[row] = s1;
                        break;
                    case 1:
                        wareList[row] = s1;
                        break;
                    case 2:
                        snList[row] = s1;
                        break;
                    case 3:
                        exceedList[row] = s1;
                        break;
                    default:
                        dataList[row] += s1 + "#";
                        break;
                }
                col++;
            }
            dataList[row] = dataList[row].substring(0,dataList[row].length() - 1);
            row++;
            col = 0;
        }

        try {
            progressBar.setMaximum(dataList.length);
            progressBar.setMinimum(0);

            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    for (int i = 0; i < dataList.length; i++) {
                        textLabel.setText("正在插入数据(" + (i + 1) + "/" + dataList.length + ")");
                        progressBar.setValue(i + 1);
                        progressBar.updateUI();
                        if (exceedList[i].equals("超标") || exceedList[i].equals("是")) {
                            insertData(dateList[i], wareList[i], snList[i], 1, dataList[i]);
                        } else {
                            insertData(dateList[i], wareList[i], snList[i], 0, dataList[i]);
                        }
//            System.out.println(dateList[i] + " " + wareList[i] + " " + snList[i] + " " + exceedList[i] + " " + dataList[i]);
                    }

                    textLabel.setText("");

                    JOptionPane.showMessageDialog(null,
                            "成功添加并修改了 " + dataList.length + " 条");
                }
            }).start();

        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,
                    "插入失败\n" + e.getMessage());
        }

        return true;
    }

    public void insertData(String devTime, String wareName,
                           String sn, int isExceed, String data) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql;
        ResultSet res;
        int infoId;
        int col = 1;
        String[] typeNameList = new String[data.split("#").length + 1];
        String[] typeUnitList = new String[data.split("#").length + 1];
        double[] upperList = new double[data.split("#").length + 1];
        double[] lowerList = new double[data.split("#").length + 1];
        int dataId = 0;

        sql = "select InfoId from Dev_Channel " +
                "where SN = '" + sn + "' ";
        res = sqlConUnit.executeQuery(sql);
        res.next();
        infoId = res.getInt("InfoId");

        sql = "select [Index], TypeName, TypeUnit, Upper, Lower from Dev_Channel where SN = '" + sn + "'";
        res = sqlConUnit.executeQuery(sql);
        while (res.next()) {
            typeNameList[res.getInt("Index")] = res.getString("TypeName");
            typeUnitList[res.getInt("Index")] = res.getString("TypeUnit");
            upperList[res.getInt("Index")] = res.getDouble("Upper");
            lowerList[res.getInt("Index")] = res.getDouble("Lower");
        }

        sql = "select Id from Record_Data where DevTime = '" + devTime + "' and InfoId = " + infoId;
        res = sqlConUnit.executeQuery(sql);
        res.next();
        try {
            res.getString("Id");

            for (String s : data.split("#")) {
                s = s.replace(typeUnitList[col], "");

                if (Double.parseDouble(s) >= lowerList[col] && Double.parseDouble(s) <= upperList[col]) {
                    isExceed = 0;
                } else {
                    isExceed = 1;
                }

                sql = "UPDATE Record_Channel SET Value = " + Double.parseDouble(s) +
                        ", IsOver = " + isExceed + " where ChannelPort = " + col + " " +
                        "and DevTime = '" + devTime + "' " +
                        "and InfoId = " + infoId + " ";
                sqlConUnit.executeUpdate(sql);

                col++;
            }
        } catch (Exception e) {
            sql = "insert into Record_Data " +
                    "(SN, IsDevOver, InfoId, HexStr, Battery, [External], OverReason, Type, DevTime, AddTime, DataTime)" +
                    "Values ('" + sn + "', " + isExceed + ", " + infoId + ", '', 0, 255, '', 0, '" + devTime + "','" + devTime + "','" + devTime + "')";
            sqlConUnit.executeUpdate(sql);

            sql = "select Id from Record_Data where DevTime = '" + devTime + "' and InfoId = " + infoId;
            res = sqlConUnit.executeQuery(sql);
            res.next();
            dataId = res.getInt("Id");

            for (String s : data.split("#")) {
                s = s.replace(typeUnitList[col], "");

                if (s.equals("NULL")) {
                    isExceed = 1;
                } else if (Double.parseDouble(s) >= lowerList[col] && Double.parseDouble(s) <= upperList[col]) {
                    isExceed = 0;
                } else {
                    isExceed = 1;
                }

                sql = "insert into Record_Channel " +
                        "(InfoId, DataId, ChannelPort, Value, IsOver, TypeName, TypeUnit, Format, DevTime) " +
                        "VALUES " +
                        "(" + infoId + ", " + dataId + ", " + col + ", " + Double.parseDouble(s) + ", " + isExceed + ", '" + typeNameList[col] + "', '" + typeUnitList[col] + "', 0.1, '" + devTime + "')";
                System.out.println(sql);

                sqlConUnit.executeUpdate(sql);
                col++;
            }
        }
    }
}
