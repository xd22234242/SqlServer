package com.example.sqlserver.Dao;

import com.example.sqlserver.pojo.DevInfo;

import java.sql.SQLException;
import java.util.List;

public interface DevInfoMapper {
    List<DevInfo> getDevInfo() throws SQLException;
}
