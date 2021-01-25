package com.dina.ui.widget;

/**
 * Created by wyb on 2016/12/13.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.BasicItem_List;
import com.dina.ui.model.IListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 明细点击事件
 */
public class MxClickListener2 implements ClickListener {

    private List<Map<String,Object>> _gslist,_datalist;
    private Context context;
    private List<IListItem> _basicItemList;
    private BasicItem basicItem;
    private Map<String,Object> map,parmsMap,_dataMap;
    private int MXREQUEST_CODE;
    private String class_name,parms1;
    private UITableView mainView;

    public MxClickListener2(Context context,List<Map<String,Object>> _gslist,List<IListItem> _basicItemList,List<Map<String,Object>> _datalist,int REQUEST_CODE,String class_name,UITableView mainView,String parms1){
        this._gslist = _gslist;
        this.context = context;
        this._basicItemList = _basicItemList;
        this._datalist = _datalist;
        this.MXREQUEST_CODE = REQUEST_CODE;
        this.class_name = class_name;
        this.parms1 = parms1;
        this.mainView = mainView;
    }

    public MxClickListener2(Context context,List<Map<String,Object>> _gslist,List<IListItem> _basicItemList,List<Map<String,Object>> _datalist,
                             int REQUEST_CODE,String class_name,UITableView mainView,String parms1,Map<String,Object> _dataMap){
        this._gslist = _gslist;
        this.context = context;
        this._basicItemList = _basicItemList;
        this._datalist = _datalist;
        this.MXREQUEST_CODE = REQUEST_CODE;
        this.class_name = class_name;
        this.parms1 = parms1;
        this.mainView = mainView;
        this._dataMap = _dataMap;
    }

    @Override
    public void onClick(final int index) {
        basicItem = (BasicItem) _basicItemList.get(index);
        Intent intent = new Intent();
        parmsMap = new HashMap<String, Object>();
        parmsMap.put("mainData",FastJsonUtils.mapToString(_datalist.get(index)));
        parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(_gslist));
        parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(_datalist));
        parmsMap.put("index",index);
        parmsMap.put("type","edit");
        //写入缓存数据
        //主数据
        _dataMap.put("mainView",mainView);
        String parms2 = "";
        parms2  = setParms(parms1,parms2,mainView);
        parmsMap.put("parms",parms2);
        intent.putExtras(AppUtils.setParms("edit",parmsMap));
        Class testClass = ReflectHelper.getCalss(class_name);
        intent.setClass(context,testClass);
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent,MXREQUEST_CODE);
    }

    public String setParms(String parms1,String parms2,UITableView mainView){
        if (!parms1.equals("")){
            String[] parms1s =  parms1.split(","),ps;
            String val1="";
            parms2 = "";
            int i=0;
            for (String key:parms1s){
                if(key.indexOf(":")>=0){
                    String[] keys = key.split(":");
                    key = keys[0];
                    BasicItem item1 = ActivityController.getItem(mainView.getIListItem(),keys[1]);
                    if(item1 == null){
                        val1 = keys[1];
                    }else {
                        val1 = item1.getSubtitle();
                    }

                }else{
                    BasicItem item1 = ActivityController.getItem(mainView.getIListItem(),key);
                    val1 = item1.getSubtitle();
                }
                if(i == parms1s.length-1){
                    parms2+= key+":"+val1;
                }else{
                    parms2+= key+":"+val1+",";
                }
                i++;
            }
        }
        return  parms2;
    }
}