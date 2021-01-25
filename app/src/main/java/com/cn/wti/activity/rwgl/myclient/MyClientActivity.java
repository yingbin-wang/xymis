package com.cn.wti.activity.rwgl.myclient;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.cn.wti.activity.base.list.BaseList_save_updateActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;
import java.util.HashMap;
import java.util.Map;

public class MyClientActivity extends BaseList_save_updateActivity {
    private  Map<String,Object> parms_map = new HashMap<String, Object>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initData();
        super.onCreate(savedInstanceState);
        //createView();
    }

    public  void createView(){

    }

    public void initData() {
        mxClass_ = "com.cn.wti.activity.rwgl.myclient.MyClient_editActivity";
        mContext = MyClientActivity.this;

        contents= new String[] {"sub_name"};
        tab_names = new String[]{"未建档","已建档"};

        //得到权限
        Intent intent = getIntent();
        qxMap = intent.getStringExtra("qxMap");
        menu_code = intent.getStringExtra("menucode");
        menu_name = intent.getStringExtra("menuname");
        serviceName = "customer";
    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
        String  class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list",
                type="";

        clearCatch();

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

        parms_map.put("index_",index);
        parms_map.put("recordcount",recordcount);
        parms_map.put("pageIndex",1);
        parms_map.put("qxMap",qxMap);
        if(main_datalist != null){
            parms_map.put("main_datalist", FastJsonUtils.ListMapToListStr(main_datalist));
        }else{
            parms_map.put("main_datalist","[]");
        }
        if (index == 1){
            //得到 服务器数据
            parms2 = "cxlx:1,parms:isdn='非档案'";
            type = "1";
            isEdit = 1;

        }else if (index == 2){
            parms2 = "cxlx:1,parms:isdn='档案'";
            type = "0";
            isEdit = 2;
        }

        pars = new ListParms("0","0",AppUtils.list_limit,menu_code,parms2,1).getParms();
        method ="list";
        final String finalType = type;
        final Dialog dialog =  WeiboDialogUtils.createLoadingDialog(mContext,"获取格式...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reshContent(finalType)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshView();
                        }
                    });
                }
                WeiboDialogUtils.closeDialog(dialog);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back2:
                this.finish();
                break;
            default:
                break;
        }
    }

    private boolean  reshContent(String type){
        pars = "{pageIndex:0,start:0,limit:10,userId:"+AppUtils.app_username+",type:"+type+"}";
        state = initData(menu_code,menu_name,"list",pars);

        titles= (String[]) get_catch().get(menu_code+"_titles");
        contents= (String[]) get_catch().get(menu_code+"_contents");
        mapAll = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
        return  state;
    }

    private  void  refreshView(){
        parms_map.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms_map.put("mxClass_",mxClass_);
        parms_map.put("menu_code",menu_code);
        parms_map.put("method",method);
        parms_map.put("service_name",serviceName);
        //明细 是否 可以编辑
        parms_map.put("isEdit",isEdit);
        parms_map.put("menu_name",menu_name);
        parms_map.put("pars",pars);
        parms_map.put("titles",titles);
        parms_map.put("contents",contents);

        String layoutType ="";
        if (get_catch().get(menu_code+"add_contentstype") != null){
            layoutType = get_catch().get(menu_code+"add_contentstype").toString();
        }else{
            layoutType = "one";
        }

        switch (layoutType){
            case "one":
                _layout = R.layout.list_item_middle_002;
                break;
            case "two":
                _layout = R.layout.list_item_middle_003;
                break;
            default:
                break;
        }


        parms_map.put("item_layout",_layout);
        Bundle bundle = AppUtils.setParms("",parms_map);
        frag.setArguments(bundle);

        FragmentTransaction action =MyClientActivity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);

        action.commit();
    }

}