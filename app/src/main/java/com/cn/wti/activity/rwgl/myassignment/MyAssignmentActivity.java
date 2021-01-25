package com.cn.wti.activity.rwgl.myassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.activity.base.BaseList_NoTable_Activity;
import com.cn.wti.activity.rwgl.notebook.NoteBook_editActivity;
import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.RecyclerViewClickListener;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAssignmentActivity extends BaseList_NoTable_Activity {

    private Map<String, Object> resMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        content_layout =  R.layout.common_list_test;
        super.onCreate(savedInstanceState);
        actionBar.setCustomView(R.layout.layout_title_back);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.app.ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);
        //设置标题
        title_tv = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
        title_tv.setWidth(title_tv.getWidth()+110);
        //返回按钮
        btn_back = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);
        btn_back.setOnClickListener(this);
        title_tv.setText(menu_name);

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (initData1(menu_code, menu_name, "list", pars)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void initData() {
        menu_code = "mytaska";
        menu_name = "我的任务";
        mxClass_ = "com.cn.wti.activity.rwgl.myassignment.MyAssignment_editActivity";
        mContext = MyAssignmentActivity.this;
        //得到 服务器数据
        parms2 = "makeempid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1,cxlx:1,parms:completetime is null ";
        contents= new String[] {"make_emp_name","trantime","taskname","","","taskdetails","",""};
    }

    public boolean initData1(String menu_code, String menu_name, String method_name, String pars) {

        boolean flage = false;

        pars = new ListParms("0", "0", AppUtils.list_limit, menu_code, parms2,1).getParms();
        this.pars = pars;
        pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(menu_code, method_name, pars);
        try {
            if (!res.equals("")) {
                resMap = getResMap(res.toString());
                if (resMap.get("results") != null) {
                    recordcount = Integer.parseInt(resMap.get("results").toString());
                    pageIndex = 1;
                    main_datalist = (List<Map<String, Object>>) resMap.get("rows");
                    _datalist.addAll(main_datalist);
                }
            }
            flage = true;
        } catch (Exception e) {
            e.printStackTrace();
            flage = false;
        }
        return  flage;
    }

}