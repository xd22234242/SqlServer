package com.example.sqlserver.Dao;

import com.example.sqlserver.Unit.SqlConUnit;
import com.example.sqlserver.pojo.Temp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TempImpl implements TempMapper {

    @Override
    public List<Temp> getTHUpperLower() throws SQLException {
        List<Temp> tempList = new ArrayList<>();
        Temp tempUpperLower = new Temp();
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select SN, Lower, Upper, [Index], InfoId from Dev_Channel";
        ResultSet res = sqlConUnit.executeQuery(sql);

        while(res.next()) {
            if (res.getInt("Index") % 2 == 1)
            {
                tempUpperLower.setSn(res.getString("SN"));
                tempUpperLower.setUpperTemp(res.getInt("Upper"));
                tempUpperLower.setLowerTemp(res.getInt("Lower"));
                tempUpperLower.setInfoId(res.getInt("InfoId"));
                tempList.add(tempUpperLower);
                tempUpperLower = new Temp();
            }
        }

        return tempList;
    }
}
