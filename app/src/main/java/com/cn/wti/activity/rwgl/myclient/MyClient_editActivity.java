package com.cn.wti.activity.rwgl.myclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.activity.common.CommonEditActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.Map;

public class MyClient_editActivity extends CommonEditActivity{

    @Override
    public void checkQx() {
        if (qxMap != null){
            if (qxMap.get("modQx")!= null && qxMap.get("modQx").toString().equals("true")){
                qxList.add("save");
            }
            if (qxMap.get("delQx")!= null && qxMap.get("delQx").toString().equals("true")){
                qxList.add("delete");
            }
        }

        qxList.remove("add");
    }

    @Override
    public void clearColumn(Map<String, Object> main_data) {
        main_data.remove("code");
    }

    /*@Override
    public Menu checkQx(Menu menu) {
        if (qxMap != null){
            if (qxMap.get("modQx")!= null && qxMap.get("modQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.save,2,"保存");
            }
            if (qxMap.get("delQx")!= null && qxMap.get("delQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.delete,3,"删除");
            }
        }
        return  menu;
    }*/

}
