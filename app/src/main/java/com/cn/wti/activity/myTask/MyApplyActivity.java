package com.cn.wti.activity.myTask;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplyActivity extends BaseList_02Activity {


    String pars;
    public List<Map<String,Object>> _datalist; //主格式
    private  String method;
    private Fragment frag =null;
    private View fragment_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initData();
        super.onCreate(savedInstanceState);
        //createView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public  void createView(){}

    public void initData() {

        menu_code = "process";
        menu_name="我的申请";

        mContext = MyApplyActivity.this;
        mxClass_ = "com.cn.wti.activity.myTask.MyApply_edit_Activity";
        //得到 服务器数据
        pars = "{pageIndex:0,start:0,limit:10,userId:"+AppUtils.app_username+"}";
        parms2 = "userId:"+AppUtils.app_username;
        super.initData(menu_code,menu_name,"findProcinstlistPage",pars);
        contents= new String[]{"NAME_","DJH_","DQRW_","START_TIME_"};
        tab_names = new String[]{"未完结","已完结"};

    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
        String  class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list";
        if (class_name !=null &&!class_name.equals(""))
            frag = null;
            try {
                if (frag == null){
                    frag = (Fragment)(Class.forName(class_name).newInstance());
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
            pars = new ListParms("0","0",AppUtils.list_limit,menu_code,"userId:"+AppUtils.app_username+",type:wwc").getParms();
            contents= new String[]{"NAME_","DJH_","DQRW_","START_TIME_"};
            method ="findProcinstlistPage";
            parms.put("item_layout",R.layout.list_item_myapply);
        }else if(index == 2){
            pars = new ListParms("0","0",AppUtils.list_limit,menu_code,"userId:"+AppUtils.app_username+",type:ywc").getParms();
            contents= new String[]{"NAME_","DJH_","","START_TIME_"};
            method ="findProcinstlistPage";
            parms.put("item_layout",R.layout.list_item_myapply_yiban);
        }

        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code+"TMTA_"+"myapply");
        parms.put("method",method);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("contents",contents);

        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action =MyApplyActivity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);

        action.commit();
    }

    @Override
    public void addAction(Context context, String class_name, int REQUEST_CODE) {
        Class class1 = ReflectHelper.getCalss(class_name);
        if (class1 != null){
            Intent intent = new Intent(context, class1);
            Map map= new HashMap<String,Object>();
            map.put("id","");
            intent.putExtras(AppUtils.setParms("add",map));
            startActivity(intent);
        }

    }
}