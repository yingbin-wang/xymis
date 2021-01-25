package com.cn.wti.activity.report.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseReportActivity;
import com.cn.wti.activity.dialog.Report_Cpxsrb_dialogActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wangz on 2016/9/19.
 */
public class CpxsrbActivity extends BaseReportActivity{

    private String splb_mcs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_select, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_search:
                String parms = new ListParms("0","0",AppUtils.limit,"goodsType","isReport<m>1").getParms();
                Dialog_ViewUtils.showReportCxtj(mContext,"goodsType","list",parms,rootView,"name","商品类别",myHandler);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getSelectParms(String parms) {
        return  pars.substring(0,pars.length()-1)+"<dh>splb_mcs<m>"+parms+"}";
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
        if (table_postion == 1) {
            parms = "dt<m>1";
            pars = new ReportParms("0","0", AppUtils.report_limit,"ProductSellDaily",parms,menuid).getParms();
        } else if (table_postion == 2) {
            parms = "by<m>1";
            pars = new ReportParms("0","0",AppUtils.report_limit,"ProductSellDaily",parms,menuid).getParms();
        }
        return pars;
    }

}
