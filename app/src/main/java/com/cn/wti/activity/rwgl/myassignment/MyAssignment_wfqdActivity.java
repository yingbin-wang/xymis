package com.cn.wti.activity.rwgl.myassignment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.cn.wti.activity.base.list.BaseList_03_updateActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.Map;

public class MyAssignment_wfqdActivity extends BaseList_03_updateActivity {

    String pars,method;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
    }

    public  void initData(){

        menu_code = "createtask";
        menu_name="我发起的";
        mContext = MyAssignment_wfqdActivity.this;
        mxClass_ = "com.cn.wti.activity.rwgl.myassignment.Createtask_editActivity";
        //得到 服务器数据
        /*parms2 = "makeempid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1,cxlx:1,cs:completetime is null ";*/
        contents= new String[] {"make_emp_name","trantime","estatus","taskname","taskdetails"};
        tab_names = new String[]{"未完结","已完结","全部"};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        String  class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list";

        //清空所有缓存数据
        clearOneTwoThree();

        frag =null;
        if (class_name !=null &&!class_name.equals(""))
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
            parms.put("main_datalist",FastJsonUtils.ListMapToListStr(main_datalist));
        }else{
            parms.put("main_datalist","[]");
        }
        if (index == 1){
            parms2 = "fqrid:" + AppUtils.user.get_zydnId() +",make_empid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1,cxlx:1,parms:completetime is null ";
            contents= new String[] {"make_emp_name","trantime","estatus","taskname","taskdetails"};
        }else if (index == 2){
            parms2 = "fqrid:" + AppUtils.user.get_zydnId() +",make_empid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1,cxlx:1,parms:completetime is not null ";
            contents= new String[] {"make_emp_name","trantime","","taskname","taskdetails"};
        }else if(index == 3){
            parms2 = "fqrid:" + AppUtils.user.get_zydnId() +",make_empid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1,cxlx:1,parms:1=1";
            contents= new String[] {"make_emp_name","trantime","estatus","taskname","taskdetails"};
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
        parms.put("item_layout",R.layout.list_item_wfqd);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action = MyAssignment_wfqdActivity.this.getFragmentManager()
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
            map.put("table_postion",table_postion);
            intent.putExtras(AppUtils.setParms("add",map));
            startActivity(intent);
        }

    }
}
