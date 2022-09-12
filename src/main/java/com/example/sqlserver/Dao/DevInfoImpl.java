package com.example.sqlserver.Dao;

import com.example.sqlserver.Unit.SqlConUnit;
import com.example.sqlserver.pojo.DevInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DevInfoImpl implements DevInfoMapper{
    @Override
    public List<DevInfo> getDevInfo() throws SQLException {
        DevInfo devInfo;
        List<DevInfo> devInfoList = new ArrayList<>();
        SqlConUnit sqlConUnit = new SqlConUnit();
        String sql = "select * from Dev_Info";
        ResultSet res = sqlConUnit.executeQuery(sql);
        while(res.next()) {
            devInfo = new DevInfo();
            devInfo.name = res.getString("Name");
            devInfo.sn = res.getString("SN");
            devInfo.portName = res.getString("PortName");
            devInfoList.add(devInfo);
        }


        return devInfoList;
    }
}
