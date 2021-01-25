package com.cn.wti.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.cn.wti.util.db.DatabaseUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/9/21.
 */
public class System_one implements Serializable{

    private String ip;
    private String username;
    private List<Map<String,Object>> list;
    private Map<String,Object> parms;

    public  System_one(){}

    public  System_one(String ip,String username){
        this.username = username;
        this.ip = ip;
    }

    public  System_one(String ip,String username,Map<String,Object> map){
        this.parms = map;
        this.username = username;
        this.ip = ip;
    }

    public  System_one(Map<String,Object> map){
        this.parms = map;
    }

    public  System_one(List<Map<String,Object>> list){
        this.list = list;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, Object> getParms() {
        return parms;
    }

    public void setParms(Map<String, Object> parms) {
        this.parms = parms;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
