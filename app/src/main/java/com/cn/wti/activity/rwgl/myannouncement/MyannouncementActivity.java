package com.cn.wti.activity.rwgl.myannouncement;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.activity.base.list.BaseList_01_updateActivity;
import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyannouncementActivity extends BaseList_01_updateActivity {


    String pars;
    public List<Map<String,Object>> _datalist; //主格式
    private  String method,ywtype="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initData();
        super.onCreate(savedInstanceState);
       // createView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final Menu finalmenu = menu;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityController.checkQx(menu_code.toLowerCase())){

                    if (qxMap != null && !qxMap.equals("")){
                        qxMap1 = FastJsonUtils.strToMap(qxMap);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (qxMap1 != null){
                                if (qxMap1.get("addQx")!= null && qxMap1.get("addQx").toString().equals("true")){
                                    MenuInflater inflater = getMenuInflater();
                                    inflater.inflate(R.menu.menu_myannouncement_list, finalmenu);
                                }
                            }else{
                                MenuInflater inflater = getMenuInflater();
                                inflater.inflate(R.menu.menu_myannouncement_list, finalmenu);
                            }
                        }
                    });
                }
            }
        }).start();

        return true;
    }

    public  void createView(){}

    public void initData() {

        menu_code = "myannouncement";
        menu_name="通知公告";
        mContext = MyannouncementActivity.this;
        mxClass_ = "com.cn.wti.activity.rwgl.myannouncement.Myannouncement_edit_Activity";
        contents= new String[]{"notificationtitle","notificationtype_name","noticecontent","make_emp_name","trantime"};
        tab_names = new String[]{"未阅","已阅"};
    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {

        clearOneTwo();
        String  class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list";
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
                    "receive_empid:"+AppUtils.user.get_zydnId()+/*",make_empid:"+AppUtils.user.get_zydnId()+*/
                            ",cxlx:1,parms:myannouncementlookDetailid is null",1).getParms();
            method ="list";
        }else if (index == 2){
            pars = new ListParms("0","0",AppUtils.list_limit,menu_code,
                    "receive_empid:"+AppUtils.user.get_zydnId()+/*",make_empid:"+AppUtils.user.get_zydnId()+*/
                            ",cxlx:1,parms:myannouncementlookDetailid is not null",1).getParms();
            method ="list";
        }

        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code);
        parms.put("method",method);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("contents",contents);
        parms.put("item_layout",R.layout.list_item_middle_myannouncement);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action =MyannouncementActivity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);

        action.commit();
    }

    @Override
    public void addAction(final Context context, final String class_name, int REQUEST_CODE) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityController.checkQx("announcement")){
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
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,getString(R.string.error_invalid_user),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add:
                addAction(mContext,mxClass_,1);
                break;
            case R.id.showwfqd:
                Intent intent = new Intent(MyannouncementActivity.this,Myannouncement_wfqd_Activity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return  true;
    }
}