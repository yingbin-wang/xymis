package com.cn.wti.activity.rwgl.myannouncement;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cn.wti.activity.base.list.BaseList_01_updateActivity;
import com.cn.wti.activity.base.list.BaseList_04_updateActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Myannouncement_wfqd_Activity extends BaseList_04_updateActivity {


    String pars;
    public List<Map<String,Object>> _datalist; //主格式
    private  String method,ywtype="wfqd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        createView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return  true;}

    public  void createView(){}

    public void initData() {

        menu_code = "myannouncement";
        menu_name="我发起的通知";
        mContext = Myannouncement_wfqd_Activity.this;
        mxClass_ = "com.cn.wti.activity.rwgl.myannouncement.Myannouncement_edit_Activity";
        contents= new String[]{"notificationtitle","notificationtype_name","noticecontent","make_emp_name","trantime"};
        tab_names = new String[]{"未发布","已发布"};
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
            pars = new ListParms("0","0",AppUtils.list_limit,menu_code,
                    "username:"+AppUtils.app_username+",make_empid:"+AppUtils.user.get_zydnId()+",receive_empid:"+
                            ",cxlx:1,parms:estatus = 1",1).getParms();
            method ="list";
        }else if (index == 2){
            pars = new ListParms("0","0",AppUtils.list_limit,menu_code,
                    "username:"+AppUtils.app_username+",make_empid:"+AppUtils.user.get_zydnId()+",receive_empid:"+
                            ",isList:1,cxlx:1,parms:estatus = 7",1).getParms();
            method ="list";
        }

        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code+"TMTA_"+"wfqd");
        parms.put("method",method);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("contents",contents);
        parms.put("item_layout",R.layout.list_item_middle_myannouncement);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action =Myannouncement_wfqd_Activity.this.getFragmentManager()
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
            map.put("index",0);
            map.put("mainData","{}");
            map.put("table_postion","1");
            intent.putExtras(AppUtils.setParms("add",map));
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _catch.remove(menu_code+"TMTA_"+"wfqd"+"_maindata");
        _catch.remove(menu_code+"TMTA_"+"wfqd"+"_recordcount");
        _catch.remove(menu_code+"TMTA_"+"wfqd"+"_pageIndex");

        _catch.remove(menu_code+"TMTA_"+"wfqd"+"_senconddata");
        _catch.remove(menu_code+"TMTA_"+"wfqd"+"sencond_recordcount");
        _catch.remove(menu_code+"TMTA_"+"wfqd"+"sencond_pageIndex");
    }
}