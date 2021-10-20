package com.dina.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/12/22.
 */

public class Custom_main_testClickListener  implements ClickListener {

    private List<Map<String,Object>> _gsList,_dataList;
    private Context context;
    private List<IListItem> _basicItemList;
    private BasicItem basicItem;
    private Map<String,Object> map,mxNames,parms;
    private String className;
    private int REQUEST_CODE;

    public Custom_main_testClickListener(Context context, List<Map<String,Object>> _gsList, Map<String,Object> parms,List<IListItem> _basicItemList, String className, int REQUEST_CODE, Map<String,Object> mxNames){
        this._gsList = _gsList;
        this._dataList = _dataList;
        this.parms = parms;
        this.context = context;
        this._basicItemList = _basicItemList;
        this.className = className;
        this.REQUEST_CODE = REQUEST_CODE;
        this.mxNames = mxNames;
    }

    @Override
    public void onClick(final int index, View ObjectView) {

        Map<String,Object> parmsMap = new HashMap<String, Object>();
        Intent intent = new Intent();
        Map<String,Object> map = new HashMap<String, Object>(_dataList.get(index));
        String id = map.get("id").toString();
        parmsMap.put("mainData", FastJsonUtils.mapToString(map));
        parmsMap.put("mainGs",parms.get("main").toString());
        if(mxNames != null){
            Set<String> sets = mxNames.keySet();
            for (String key :sets){
                if (key.equals("mx1gs_list")){
                    parmsMap.put("mx1gs_list",parms.get(mxNames.get(key)).toString());
                    parmsMap.put("mx1_data","");
                }else{
                    parmsMap.put("mx2gs_list",parms.get(mxNames.get(key)).toString());
                    parmsMap.put("mx2_data","");
                }
            }
        }
        parmsMap.put("id",id);
        intent.putExtras(AppUtils.setParms("edit",parmsMap));
        Class class1 = ReflectHelper.getCalss(className);
        intent.setClass(context,class1);
        Activity test = (Activity) context;
        test.startActivityForResult(intent,REQUEST_CODE);

        /*  Intent intent = new Intent();
        intent.putExtras(AppUtils.setParms("",finalmap));
        intent.setClass(MyTaskActivity.this,MyTask_edit_Activity.class);
        startActivityForResult(intent,REQUEST_CODE);*/
    }


}

