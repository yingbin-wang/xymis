package com.cn.wti.activity.report.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseReportActivity;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wangz on 2016/9/19.
 */
public class BmxsrbActivity extends BaseReportActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_reshdata, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean initData() {

        super.initData();
        service_name = "report";
        method_name = "findGridData";
        tab_names = new String[]{"本日","本月"};
        class_name ="com.cn.wti.activity.report.fragment.Report_one_Fragment";
        hjcolumns = "1,2,3";
        hjcolumn_names = "总销量,总销售额,总毛利";
        mContext = this;
        return true;
    }

    @Override
    public String getParms(){
        if(table_postion == 1){
            pars =  new ReportParms("0","0",AppUtils.report_limit,"DeptSellDaily","dt<m>1<dh>isReport<m>1",menuid).getParms();
        }else if(table_postion == 2){
            pars =  new ReportParms("0","0",AppUtils.report_limit,"DeptSellDaily","by<m>1<dh>isReport<m>1",menuid).getParms();
        }
        return pars;
    }

}
