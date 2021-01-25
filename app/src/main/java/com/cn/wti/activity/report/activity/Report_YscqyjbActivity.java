package com.cn.wti.activity.report.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cn.wti.activity.base.BaseReportActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.wticn.wyb.wtiapp.R;

public class Report_YscqyjbActivity extends BaseReportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_select, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_search:
                sfcx = 2;
                String parms = new ListParms("0","0",AppUtils.limit,"customer").getParms();
                Dialog_ViewUtils.showReportCxtj(mContext,"customer","list",parms,rootView,"sub_name","客户档案",myHandler);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean initData() {

        super.initData();
        service_name = "report";
        method_name = "findGridData";
        tab_names = new String[]{"已到期","未到期"};
        class_name ="com.cn.wti.activity.report.fragment.Report_one_Fragment";
        hjcolumns = "2";
        hjcolumn_names = "合计应收余额";
        table_title_layoutid = R.layout.list_item_tablerow_title_yscqyjb;
        table_data_layoutid = R.layout.list_item_tablerow_data_yscqyjb;
        mContext = Report_YscqyjbActivity.this;
        return true;
    }

    @Override
    public String getParms(){
        if(table_postion == 1){
            pars =  new ReportParms("0","0",AppUtils.report_limit,"ReceOverdueRemind","ycq<m>1",menuid).getParms();
        }else if(table_postion == 2){
            pars =  new ReportParms("0","0",AppUtils.report_limit,"ReceOverdueRemind","wcq<m>1",menuid).getParms();
        }
        return pars;
    }

    @Override
    public String getSelectParms(String parms) {
        return  getParms().substring(0,getParms().length()-1)+"<dh>khjc_mcs<m>"+parms+"}";
    }
}
