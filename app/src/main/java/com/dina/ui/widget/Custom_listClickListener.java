package com.dina.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/12/22.
 */

public class Custom_listClickListener implements  ClickListener {

    private List<Map<String,Object>> _gsList,_dataList;
    private Context context;
    private List<IListItem> _basicItemList;
    private BasicItem basicItem;
    private Map<String,Object> map,mxNames,parms;
    private String className,ywbm;
    private int REQUEST_CODE;
    private int table_postion;
    private UIListTableView tableView;

    public Custom_listClickListener(Context context, List<Map<String,Object>> _dataList, String className){
        this.context = context;
        this._dataList = _dataList;
        this.className = className;
    }

    public Custom_listClickListener(Context context, List<Map<String,Object>> _dataList, String className, String ywbm){
        this.context = context;
        this._dataList = _dataList;
        this.className = className;
        this.ywbm = ywbm;
    }

    public Custom_listClickListener(Context context, UIListTableView tableView, String className, String ywbm){
        this.context = context;
        this.tableView = tableView;
        this.className = className;
        this.ywbm = ywbm;
    }

    @Override
    public void onClick(int index,View ObjectView) {

    }

    public void onClick(final View view, final int index) {

        if (view.getTag() != null){
            _dataList = (List<Map<String, Object>>) view.getTag();
        }

        if (_dataList.size() == 0){
            Toast.makeText(context,"数据存在异常",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> map = _dataList.get(index);

        Intent intent = new Intent();
        map.put("table_postion",table_postion);
        intent.putExtras(AppUtils.setParms("",map));
        intent.putExtra("ywbm",ywbm);

        Class class1 = ReflectHelper.getCalss(className);
        intent.setClass(context,class1);
        Activity test = (Activity) context;
        test.startActivityForResult(intent,REQUEST_CODE);

    }


    public int getTable_postion() {
        return table_postion;
    }

    public void setTable_postion(int table_postion) {
        this.table_postion = table_postion;
    }
}

