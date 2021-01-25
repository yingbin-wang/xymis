package com.cn.wti.activity.common;

import android.content.Intent;
import android.os.Bundle;

import com.cn.wti.activity.base.list.BaseList_02_updateActivity;
import com.cn.wti.util.app.AppUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.Map;

public class CommonListActivity extends BaseList_02_updateActivity {

    String pars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public  void initData(){

        //得到权限
        Intent intent = getIntent();
        qxMap = intent.getStringExtra("qxMap");
        menu_code = intent.getStringExtra("menucode");
        menu_name = intent.getStringExtra("menuname");

        mContext = CommonListActivity.this;
        mxClass_ = "com.cn.wti.activity.common.CommonEditActivity";
        //得到 服务器数据
        parms = new HashMap<String, Object>();
        pars = "{pageIndex:0,start:0,limit:10,username:"+AppUtils.app_username+"}";
        state = initData(menu_code,menu_name,"list",pars);

        titles= (String[]) get_catch().get(menu_code+"_titles");
        contents= (String[]) get_catch().get(menu_code+"_contents");
        mapAll = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");

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
    }

}
