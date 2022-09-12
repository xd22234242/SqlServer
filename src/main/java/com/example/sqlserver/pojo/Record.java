package com.example.sqlserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    String name;
    String sn;
    String DevTime;
    String tValue;
    String hValue;
    String tTypeUnit;
    String hTypeUnit;
}
