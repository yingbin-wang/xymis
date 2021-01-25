package com.cn.wti.util.app;

import android.content.Context;
import android.content.res.ObbInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cn.wti.entity.Sys_user;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.db.DatabaseUtils;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.UITableView;
import com.wticn.wyb.wtiapp.R;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangz on 2016/9/22.
 */
public class TableUtils {

    /**
     * 将数据写入表格
     * @param data_map 数据
     * @param whos 字段名
     * @param tableView 表格
     * @return
     */
    public static void updateBasicItem(Map<String,Object> data_map, String[] whos, UITableView tableView){
        List<IListItem> finalViewList = tableView.getIListItem();
        BasicItem basicItem;
        String val="";
        if(whos != null) {
            for (String who : whos) {
                String[] test_whos = who.split(":");
                basicItem = ActivityController.getItem(finalViewList, test_whos[0]);
                if (data_map.get(test_whos[1]) == null) {
                    val = "";
                } else {
                    val = data_map.get(test_whos[1]).toString();
                }
                basicItem.setSubtitle(val);
                View v1 = tableView.getLayoutList(basicItem.getIndex());
                tableView.setupBasicItemValue(v1, basicItem, basicItem.getIndex());
            }
        }
    }

    /**
     * 通过Map 的形式 更新 item
     * @param data_map
     * @param whoMap
     * @param tableView
     */
    public static void updateBasicItemByMap(Map<String,Object> data_map, Map<String,Object> whoMap, UITableView tableView){
        List<IListItem> finalViewList = tableView.getIListItem();
        BasicItem basicItem;
        String val="";
        if(whoMap != null) {
            Set<String> whos = whoMap.keySet();
            for (String who : whos) {
                String who_val = whoMap.get(who).toString();
                basicItem = ActivityController.getItem(finalViewList, who);
                if (data_map.get(who_val) == null) {
                    val = "";
                } else {
                    val = data_map.get(who_val).toString();
                }
                if (basicItem == null){
                    return;
                }
                basicItem.setSubtitle(val);
                View v1 = tableView.getLayoutList(basicItem.getIndex());
                tableView.setupBasicItemValue(v1, basicItem, basicItem.getIndex());
            }
        }
    }

    public static String getParms(String[] who_parms,UITableView tableView ){
        /**
         * 取得参数 并 赋值
         */
        BasicItem basicItem_1;
        String who ="",key1,val1,who_parm="";
        String[]testKeys;
        List<IListItem> finalViewList = tableView.getIListItem();
        for (int i = 0, n = who_parms.length; i < n; i++) {
            who = who_parms[i];
            testKeys = who.split(":");
            key1 = testKeys[1];
            if (key1.indexOf("_val")<0){
                basicItem_1 = ActivityController.getItem(finalViewList,key1);
                val1 = basicItem_1.getSubtitle();
            }else{
                val1 = key1.replace("_val","");
            }

            if (who_parms.equals("")) {
                who_parm += testKeys[0] + ":" + val1;
            } else {
                who_parm += testKeys[0] + ":" + val1 + ",";
            }
        }
        return  who_parm;
    }

    public static String getParmsByMap(String[] who_parms,Map<String,Object> data_map){
        /**
         * 取得参数 并 赋值
         */
        BasicItem basicItem_1;
        String who ="",key1,val1,who_parm="";
        String[]testKeys;

        for (int i = 0, n = who_parms.length; i < n; i++) {
            who = who_parms[i];
            testKeys = who.split(":");
            key1 = testKeys[1];
            if(data_map.get(key1) != null){
                val1 = data_map.get(key1).toString();
            }else {
                val1 = key1;
            }

            if (who_parms.equals("")) {
                who_parm += testKeys[0] + ":" + val1;
            } else {
                who_parm += testKeys[0] + ":" + val1 + ",";
            }
        }
        return  who_parm;
    }

}
