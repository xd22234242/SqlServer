package com.example.sqlserver.Dao;

import com.example.sqlserver.pojo.Temp;

import java.sql.SQLException;
import java.util.List;

public interface TempMapper {
    List<Temp> getTHUpperLower() throws SQLException;
}
