package com.example.sqlserver.ControlConfig;

import com.example.sqlserver.Form.SwingArea;
import com.example.sqlserver.Unit.SqlConUnit;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyTableCellRenderer implements TableCellRenderer {
    @SneakyThrows
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableCellRenderer dtcr =new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        Component renderer = dtcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String str = (String) value;
        SwingArea swingArea = new SwingArea();
        double[] channelUpperLower = getChannelUpperLower(Integer.valueOf((String) table.getValueAt(row, 6)), column - 2);
        String typeUnit = getTypeUnit(Integer.valueOf((String) table.getValueAt(row, 6)),
                column - 2, table.getValueAt(row, 5).toString());

        if (str == null) {
            return renderer;
        }


        if (Integer.valueOf(table.getValueAt(row, 7).toString()) == 1) {
            renderer.setBackground(Color.YELLOW);
        }

        str = str.replace(typeUnit, "");

        double channelValue = Double.parseDouble(str);

        if (channelValue > channelUpperLower[0] || channelValue < channelUpperLower[1]
                || getRecordChannelIsOver(Integer.valueOf((String) table.getValueAt(row, 6)),
                column - 2, table.getValueAt(row, 5).toString()) == 1) {
            renderer.setForeground(Color.RED);
        }

        return renderer;
    }

    private double[] getChannelUpperLower(int infoId, int channelPort) throws SQLException {
        double[] channelUpperLower = new double[2];

        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select Upper, Lower from Dev_Channel " +
                "where InfoId = " + infoId + " " +
                "and [Index] = " + channelPort;
        res = sqlConUnit.executeQuery(str);

        res.next();
        channelUpperLower[0] = res.getDouble("Upper");
        channelUpperLower[1] = res.getDouble("Lower");

        return channelUpperLower;
    }

    public String getTypeUnit(int infoId, int channelPort, String devTime) throws SQLException{
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select TypeUnit from Record_Channel " +
                "where InfoId = " + infoId + " " +
                "and channelPort = " + channelPort + " " +
                "and DevTime = '" + devTime + "' ";
        res = sqlConUnit.executeQuery(str);

        res.next();

        return res.getString("TypeUnit");
    }

    public int getRecordChannelIsOver(int infoId, int channelPort, String devTime) throws SQLException {
        SqlConUnit sqlConUnit = new SqlConUnit();
        String str = "";
        ResultSet res = null;

        str = "select IsOver from Record_Channel " +
                "where InfoId = " + infoId + " " +
                "and channelPort = " + channelPort + " " +
                "and DevTime = '" + devTime + "' ";
        res = sqlConUnit.executeQuery(str);

        res.next();

        return res.getInt("IsOver");
    }
}
