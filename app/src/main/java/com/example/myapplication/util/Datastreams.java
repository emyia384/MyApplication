package com.example.myapplication.util;

import com.example.myapplication.util.Datapoints;

import java.util.List;

//获取数据流
public class Datastreams {
    private List<Datapoints>datapoints;
    private String id;

    public List<Datapoints> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<Datapoints> datapoints) {
        this.datapoints = datapoints;
    }
}
