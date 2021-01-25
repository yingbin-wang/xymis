package com.cn.wti.activity.rwgl.systemmsg;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.activity.base.BaseList_NoTable_Activity;
import com.cn.wti.activity.base.list.BaseList_01_updateActivity;
import com.cn.wti.activity.base.list.BaseList_04_updateActivity;
import com.cn.wti.activity.base.list.BaseList_yiyueweiyue_updateActivity;
import com.cn.wti.activity.rwgl.notebook.NoteBook_editActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.RecyclerViewClickListener;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemmsgActivity extends BaseList_yiyueweiyue_updateActivity {


    String pars;
    private Map<String, Object> resMap;
    private TextView title_tv;
    private ImageButton btn_back;
    private View mLayoutManager;
    public List<Map<String,Object>> _datalist; //主格式
    private boolean[] mCheck = {};
    private  String method;
    private Button btn_selectAll,btn_ok;
    private Dialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initData();
        list_layoyt = R.layout.activity_systemmsg;
        super.onCreate(savedInstanceState);
        //createView();
    }

    public  void createView(){

        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.layout_title_myassignment_edit);
        //设置标题
        title_tv = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
        //返回按钮
        btn_back = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);
        btn_ok = (Button) actionBar.getCustomView().findViewById(R.id.ok);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.app.ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);
        //返回 OK 按钮
        if (table_postion == 1){
            btn_ok.setOnClickListener(this);
            btn_ok.setText("编辑");
        }else{
            btn_ok.setVisibility(View.GONE);
        }
        btn_back.setOnClickListener(this);

        //标题
        title_tv.setText(menu_name);
        mLayoutManager = findViewById(R.id.llt);

        btn_selectAll = (Button) findViewById(R.id.btn_selectAll);
        btn_selectAll.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void initData() {
        menu_code = "systemmsg";
        menu_name = "系统消息";
        mxClass_ = "com.cn.wti.activity.rwgl.systemmsg.Systemmsg_editActivity";
        mContext = SystemmsgActivity.this;

        contents= new String[] {"senddate","msgcontent"};
        tab_names = new String[]{"未阅","已阅"};
    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
        String  class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list";

        clearOneTwo();

        if (class_name !=null &&!class_name.equals(""))
            frag = null;
            try {
                if (frag == null){
                    frag = (Fragment)(Class.forName(class_name).newInstance());//new Report_BmxsrbActivity()
                }

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        int index = tab.getPosition() + 1;
        table_postion = index;
        Bundle bundle = new Bundle();
        Map<String,Object> parms = new HashMap<String, Object>();
        parms.put("index_",index);
        parms.put("recordcount",recordcount);
        parms.put("pageIndex",1);
        if(main_datalist != null){
            parms.put("main_datalist", FastJsonUtils.ListMapToListStr(main_datalist));
        }else{
            parms.put("main_datalist","[]");
        }
        if (index == 1){
            //得到 服务器数据
            parms2 = "receiver:" + AppUtils.app_username + ",isDataQxf:1,islooked:0";

        }else if (index == 2){
            parms2 = "receiver:" + AppUtils.app_username  + ",isDataQxf:1,islooked:1";
        }

        pars = new ListParms("0","0",AppUtils.list_limit,menu_code,parms2,1).getParms();
        method ="list";

        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code);
        parms.put("method",method);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("contents",contents);
        parms.put("item_layout",R.layout.list_item_systemmsg);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action =SystemmsgActivity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);

        action.commit();

        if (table_postion == 1 && btn_ok != null){
            btn_ok.setVisibility(View.VISIBLE);
        }else if (table_postion == 2  && btn_ok != null){
            btn_ok.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back2:
                this.finish();
                break;
            case R.id.ok:
                if (btn_ok.getText().equals("编辑") && table_postion == 1){
                    btn_ok.setText("取消");
                    fragment_view = frag.getView();
                    mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
                    mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();
                    mAdapter2.setSfVisible(true);
                    mAdapter2.notifyDataSetChanged();
                    ((TextView)fragment_view.findViewById(R.id.isSelect)).setText("取消");
                    mLayoutManager.setVisibility(View.VISIBLE);
                }else if(btn_ok.getText().equals("取消") && table_postion == 1){
                    mCheck = new boolean[mAdapter2.getItemCount()];
                    mAdapter2.refreshData(mCheck);
                    mAdapter2.setSfVisible(false);
                    mAdapter2.notifyDataSetChanged();
                    btn_ok.setText("编辑");
                    ((TextView)fragment_view.findViewById(R.id.isSelect)).setText("编辑");
                    mLayoutManager.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_selectAll:
                mCheck =  mAdapter2.getSelectItem();
                String ids = ActivityController.getIds(mCheck,mAdapter2.getDatas());
                pars = new ListParms(menu_code,"id:"+ids.replaceAll(",","<dh>")+",receiver:"+AppUtils.app_username).getParms();

                mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String isUpdate = ActivityController.executeForResultByThread(mContext,menu_code,"updateIslooked",StringUtils.strTOJsonstr(pars));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isUpdate.indexOf("err")<0){
                                    List<Map<String,Object>> resList = new ArrayList<Map<String, Object>>();
                                    for (int i=0;i<mCheck.length;i++) {
                                        if (mCheck[i]){
                                            resList.add(mAdapter2.getDatas().get(i));
                                        }
                                    }
                                    if (resList.size()>0){
                                        mAdapter2.getDatas().removeAll(resList);
                                    }
                                    mAdapter2.refreshData(new boolean[mAdapter2.getDatas().size()]);
                                    Toast.makeText(mContext,getString(R.string.success_operation),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(mContext,isUpdate.replace("err,",""),Toast.LENGTH_SHORT).show();
                                }

                                if (mDialog != null){
                                    WeiboDialogUtils.closeDialog(mDialog);
                                   /* //刷新视图
                                    try {
                                        ReflectHelper.callMethod2(frag,"reshView",null,String.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }*/
                                }
                            }
                        });
                    }
                }).start();

                break;
            default:
                break;
        }
    }

}