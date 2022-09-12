package com.example.sqlserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Temp {
    private String sn;
    private int upperTemp;
    private int lowerTemp;
    private int infoId;
    private int channelPort;
}
