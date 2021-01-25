package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.ReportAdapter;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.FastJsonUtils;

import java.util.Map;

/**
 * Created by wangz on 2016/9/19.
 */
public class BaseReportActivity extends Activity implements ActionBar.TabListener{

    ActionBar actionBar = null;
    protected String [] tab_names;
    protected String class_name,pars="",parms="",service_name,method_name,hjcolumns,hjcolumn_names,menuid;
    protected  int sfcx = 0,table_postion,table_title_layoutid,table_data_layoutid;
    protected Fragment frag =null;
    protected ReportAdapter reportAdapter;
    protected Map<String,Object> resMap;
    protected Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 11:
                    Bundle b = msg.getData();
                    String pars = b.getString("pars");
                    //parms = pars.substring(0,pars.length()-1)+"spdn_mcs<m>"+spdn_mcs+"<dh>splb_mcs<m>"+splb_mcs+"}";
                    /**
                     * 得到返回 选择参数
                     */
                    pars = pars.replaceAll(",","dh");
                    final String parms  = getSelectParms(pars);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Object res = null;
                            try {
                                res = ReflectHelper.callMethod2(frag,"reshData",new Object[]{parms},String.class);
                                final Object finalRes = res;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            reportAdapter = (ReportAdapter) ReflectHelper.getValueByFieldName(frag,"reportAdapter");
                                        } catch (NoSuchFieldException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                        reportAdapter.refreshData();
                                        if ((boolean) finalRes){
                                            Toast.makeText(mContext,getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();


                    break;
            }
        }
    };
    protected Dialog mDialog = null;
    protected Context mContext;
    protected View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        setContentView(R.layout.common_report);

        AppUtils.setStatusBarColor(this);
        actionBar = this.getActionBar();
        actionBar = this.getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setLogo(R.mipmap.navigationbar_back);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        if(tab_names != null){
            for (String name:tab_names){
                actionBar.addTab(actionBar.newTab().setText(name)
                        .setTabListener(this));
            }
        }

        rootView = findViewById(R.id.rootView);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        if (class_name !=null &&!class_name.equals(""))
            try {
               frag = (Fragment)(Class.forName(class_name).newInstance());//new Report_BmxsrbActivity()
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        table_postion = tab.getPosition() + 1;

        pars = getParms();

        Bundle bundle = new Bundle();
        bundle.putInt("key", table_postion);
        if (pars== null){pars = "";}
        bundle.putString("parms",pars);
        bundle.putInt("sfcx",sfcx);
        bundle.putString("service_name",service_name);
        bundle.putString("method_name",method_name);
        bundle.putString("hjcolumns",hjcolumns);
        bundle.putString("hjcolumn_names",hjcolumn_names);
        bundle.putString("menuMap",FastJsonUtils.mapToString(resMap));
        bundle.putInt("table_title_layoutid",table_title_layoutid);
        bundle.putInt("table_data_layoutid",table_data_layoutid);

        frag.setArguments(bundle);

        FragmentTransaction action = BaseReportActivity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);
        action.commit();

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_refresh:
                sfcx = 2;
                mDialog = WeiboDialogUtils.createLoadingDialog(this, "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Object res = null;
                        try {
                            res = ReflectHelper.callMethod2(frag,"reshData",null,String.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if((boolean)res){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                try {
                                    reportAdapter = (ReportAdapter) ReflectHelper.getValueByFieldName(frag,"reportAdapter");
                                    reportAdapter.refreshData();
                                    WeiboDialogUtils.closeDialog(mDialog);
                                    Toast.makeText(frag.getActivity(),getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                }
                            });
                        }else{
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    }
                }).start();

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getParms(){ return  "";}

    public String getSelectParms(String parms){return  "";}

    public boolean  initData(){
        //得到菜单数据
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return false;
        }

        resMap = so.getParms();

        if (resMap != null && resMap.get("menu_id")!= null){
            menuid = resMap.get("menu_id").toString();
        }
        return  true;
    }

}
