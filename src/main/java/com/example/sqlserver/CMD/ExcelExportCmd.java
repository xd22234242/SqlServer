package com.example.sqlserver.CMD;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelExportCmd {
    public ExcelExportCmd() {

    }

    public String getTableContext(JTable table) {
        String str = "";

        str += "时间#仪器名称#SN#超标#通道一#通道二#通道三#通道四#通道五#通道六#通道七#通道八#超标原因@";

        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null,
                    "列表为空");
            return "";
        }

        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            str += table.getValueAt(i, 5) + "#";
            str += table.getValueAt(i, 1) + "#";
            str += table.getValueAt(i, 2) + "#";
            str += (Integer.valueOf(table.getValueAt(i, 7).toString()) == 0 ? "否#" : "是#");
            str += table.getValueAt(i, 3) + "#";
            str += table.getValueAt(i, 4) + "@";
        }

        System.out.println(System.getProperty("user.dir"));
        
        return str;
    }

    //0表示xls,1表示xlsx
    public void exportExcel(String result, int excelFormat, String fileName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

//        String exportFileName = sdf.format(new Date()) + (excelFormat == 0 ? ".xls" : ".xlsx");
        String exportFileName = fileName + (excelFormat == 0 ? ".xls" : ".xlsx");

        try {
            //创建工作簿
            Workbook workbook;
            if (exportFileName.contains(".xlsx")) {
                workbook = new XSSFWorkbook();
            } else {
                workbook = new HSSFWorkbook();
            }
            //创建sheet
            Sheet sheet = workbook.createSheet("sheet1");
            int column = 0;
            int rowCount = 0;
            String[] strRows = result.split("@");
            for (String strRow : strRows) {
                Row row = sheet.createRow(rowCount++);
                for (String s : strRow.split("#")) {
                    Cell cell = row.createCell(column++);
                    cell.setCellValue(s);
                }
                column = 0;
            }

            OutputStream stream = new FileOutputStream(System.getProperty("user.dir") + "\\" + exportFileName);
//            OutputStream stream = new FileOutputStream("C:\\Users\\admin\\Desktop\\输出.xls");
            workbook.write(stream);
            stream.close();
            JOptionPane.showMessageDialog(null,
                    "成功导出文件，导出文件名为 " + exportFileName);
            System.out.println("成功写入文件");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "导出失败");
            e.printStackTrace();
        }
    }
}
