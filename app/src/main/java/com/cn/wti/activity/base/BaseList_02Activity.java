package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.widget.UIListTableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseList_02Activity extends Activity implements ActionBar.TabListener,View.OnClickListener{

    protected String[]tab_names = null;
    protected  Context mContext;
    protected View view,fragment_view;
    protected  Map<String,Object> parms,mapAll,qxMap1 = null;
    protected  List<Map<String,Object>> main_datalist,sencond_datalist,third_datalist,fourth_datalist;
    protected  String menu_code,menu_name,pars,class_name,method,serviceName="",
            parms2="" /*特殊参数*/,
            qxMap = "";
    //列表list 控件
    protected UIListTableView tableView;
    protected String[] contents,titles,title_contents;
    //分页参数
    protected  int pageIndex, recordcount,sencond_recordcount,third_recordcount,fourth_recordcount,_layout=0,list_layoyt=0,table_postion,isEdit = 0;
    //进入编辑页面
    protected  String mxClass_;
    private   Map<String,Object >resMap;
    //得到缓存数据
    protected static PageDataSingleton _catch = PageDataSingleton.getInstance();
    private int REQUEST_CODE = 1;
    protected  int screenWidth,screenHeight;
    protected ActionBar actionBar;
    protected boolean state = true;

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        BaseList_02Activity._catch = _catch;
    }


    //刷新数据
    protected RecyclerView mRecyclerView1;
    protected MyAdapter2 mAdapter2;
    protected Fragment frag;
    private Dialog mDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        actionBar = this.getActionBar();
        if (tab_names == null){
            tab_names = new String[]{"未审核","审批中","已审核","全部"};
        }

        //列表 控件
        tableView = (UIListTableView) this.findViewById(R.id.tableView);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        mDialog = WeiboDialogUtils.createLoadingDialog(this,"获取表单数据...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
                final Activity activity = (Activity) mContext;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (list_layoyt == 0){
                            setContentView(R.layout.common_report);
                        }else{
                            setContentView(list_layoyt);
                        }

                        AppUtils.setStatusBarColor(activity);
                        if (actionBar != null){
                            actionBar.setDisplayShowHomeEnabled(true);
                            actionBar.setHomeButtonEnabled(true);
                            actionBar.setLogo(R.mipmap.navigationbar_back);
                            //修改标题
                            actionBar.setTitle(menu_name);

                            //如果 存在Tabs 添加
                            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                            if(tab_names != null){
                                for (String name:tab_names){
                                    actionBar.addTab(actionBar.newTab().setText(name)
                                            .setTabListener(BaseList_02Activity.this));
                                }
                            }
                        }

                        if (!state){
                            //Toast.makeText(mContext,getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                        //加载本身视图
                        createView();

                        WeiboDialogUtils.closeDialog(mDialog);
                    }
                });
            }
        }).start();
    }
    public  void initData(){};
    public  void createView(){};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (qxMap != null && !qxMap.equals("")){
            qxMap1 = FastJsonUtils.strToMap(qxMap);
        }

        if (qxMap1 != null){
            if (qxMap1.get("addQx")!= null && qxMap1.get("addQx").toString().equals("true")){
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
                return super.onCreateOptionsMenu(menu);
            }else{
                return  true;
            }
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
            return super.onCreateOptionsMenu(menu);
        }

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add:
                if (isEdit == 0 || isEdit == 1){
                    addAction(mContext,mxClass_,REQUEST_CODE);
                }else{
                    Toast.makeText(mContext,"无新增权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void addAction(Context context,String class_name,int REQUEST_CODE){
        Map<String,Object> parmsMap = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        Intent intent = new Intent();
        parmsMap = createIntentMap(parmsMap,map,mapAll,"");
        parmsMap.put("table_postion",table_postion);
        if (serviceName.equals("")){serviceName = menu_code;}
        parmsMap.put("menucode",menu_code);
        parmsMap.put("serviceName",serviceName);
        parmsMap.put("menuname",menu_name);
        parmsMap.put("qxMap",qxMap);
        parmsMap.put("index","0");
        intent.putExtras(AppUtils.setParms("add",parmsMap));
        Class class1 = ReflectHelper.getCalss(class_name);
        if (class1 != null){
            intent.setClass(context,class1);
            startActivityForResult(intent,REQUEST_CODE);
        }

    }

    public Map<String,Object> createIntentMap(Map<String,Object> parmsMap,Map<String,Object> map,Map<String,Object> mxNames,String id){
        parmsMap.put("mainData",FastJsonUtils.mapToString(map));
        String qz = "add_";
        parmsMap.put("mainGs",parms.get(qz+"main").toString());
        if(mxNames != null){
            Set<String> sets = mxNames.keySet();
            for (String key :sets){
                Map<String,Object> _map = (Map<String,Object>)mxNames.get(key);

                if (key.equals("mx1gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx1gs_list",parms.get(qz+code));
                    parmsMap.put("mx1_data","");
                }else if(key.equals("mx2gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx2gs_list",parms.get(qz+code));
                    parmsMap.put("mx2_data","");
                }else if(key.equals("mx3gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx3gs_list",parms.get(qz+code));
                    parmsMap.put("mx3_data","");
                }else if(key.equals("mx4gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx4gs_list",parms.get(qz+code));
                    parmsMap.put("mx4_data","");
                }else if(key.equals("mx5gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx5gs_list",parms.get(qz+code));
                    parmsMap.put("mx5_data","");
                }
            }
        }
        parmsMap.put("id",id);
        return  parmsMap;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        if (!state){
            this.finish();
            return;
        }

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
            parms2 = "cxlx:1,parms:estatus=1 and ifnull(approvalstatus<dh>0) !=1";
        }else if (index == 2){
            parms2 = "cxlx:1,parms:ifnull(approvalstatus<dh>0) =1";
        }else if (index == 3){
            parms2 = "cxlx:1,parms:estatus=7";
        }else if (index == 4){
            parms2 = "";
        }

        pars = new ListParms("0","0",AppUtils.list_limit,menu_code,parms2,1).getParms();
        method ="list";

        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code);
        parms.put("method",method);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("titles",titles);
        parms.put("contents",contents);
        parms.put("qxMap",qxMap);
        parms.put("item_layout",_layout);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action =this.getFragmentManager()
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

    /**
     * 初始化 数据 与 显示 格式
     * @param menu_code
     * @param menu_name
     * @param method
     * @param pars
     */
    public  boolean initData(String menu_code,String menu_name,String method,String pars){

        /*pars = "{pageIndex:0,start:0,limit:10,userId:"+AppUtils.app_username+"}";*/
        //如果参数 不为空 取得参数 验证 类型 参数 20171226 wang
        String type = "";
        if (!pars.equals("")){
            String testParms = StringUtils.strTOJsonstr(pars);
            Map<String,Object> parmsMap = FastJsonUtils.strToMap(testParms);
            if (parmsMap.get("type")!= null){
                type = parmsMap.get("type").toString();
            }else {
                type = "shuilv";
            }
        }
        //添加 数据权限 菜单编号
        //20171226 wang 添加 显示类型
        if (!type.equals("") && !parms2.equals("")){
            parms2 +=",type:"+type;
        }else if (!type.equals("") && parms2.equals("")){
            parms2 +="type:"+type;
        }
        pars = new ListParms("0","0",AppUtils.limit,menu_code,parms2).getParms();
        this.pars = pars;
        class_name = "com.cn.wti.activity.base.fragment.CommonFragment_list";

        //pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        if (menu_code.equals("process")){return true;}

        parms.put("menucode",menu_code);
        parms.put("sfty","0");
        parms.put("sjjs",AppUtils.user.getSjjs());
        parms.put("type",type);

        String res1 = null;
        try {
            res1 = HttpClientUtils.webService("findFormProperty", FastJsonUtils.mapToString(parms));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!res1.equals("") && !res1.contains("(abcdef)")){
            try {
                parms = FastJsonUtils.strToMap(res1);
                //编辑 规则
                if(parms.get("gz") != null && !parms.get("gz").toString().equals("")){
                    Object gz_object = parms.get("gz");
                    if (gz_object instanceof String){
                        Map<String,Object> gz_map = FastJsonUtils.strToMap(gz_object.toString()) ;
                        String gz_string = "contents,mapAll,select,gs,rule,init";
                        String[] gz_keys = gz_string.split(",");
                        for (String key:gz_keys) {
                            if(key.equals("contents")){
                                title_contents = gz_map.get(key).toString().split(",",-1);
                                split_title_content(title_contents);
                                get_catch().put(menu_code+"_titles",titles);
                                get_catch().put(menu_code+"_contents",contents);
                            }else{
                                get_catch().put(menu_code+"_"+key,(Map<String,Object>)gz_map.get(key));
                            }
                        }
                    }else if(gz_object instanceof JSONObject){
                        Map<String,Object> gz_map = (Map<String, Object>) gz_object;
                        String gz_string = "contents,mapAll,select,gs,rule,init,contentstype";
                        String[] gz_keys = gz_string.split(",");
                        for (String key:gz_keys) {
                            if(key.equals("contents")){
                                title_contents = gz_map.get(key).toString().split(",",-1);
                                split_title_content(title_contents);
                                get_catch().put(menu_code+"_titles",titles);
                                get_catch().put(menu_code+"_contents",contents);
                            }else if(key.equals("contentstype")){
                                get_catch().put(menu_code+"_contentstype",gz_map.get(key).toString());
                            }else{
                                get_catch().put(menu_code+"_"+key,(Map<String,Object>)gz_map.get(key));
                            }
                        }

                    }
                    parms.remove("gz");
                }

                //新增规则
                Object gz_object = null;
                if(parms.get("addgz") != null && !parms.get("addgz").toString().equals("")) {
                    gz_object = parms.get("addgz");
                    if(gz_object instanceof String){
                        gz_object= FastJsonUtils.strToJson(gz_object.toString());
                    }
                    if (gz_object instanceof JSONObject) {
                        Map<String, Object> gz_map = (Map<String, Object>) gz_object;
                        String gz_string = "contents,mapAll,select,gs,rule,init,contentstype";
                        String[] gz_keys = gz_string.split(",");
                        for (String key : gz_keys) {
                            if (key.equals("contents")) {
                                title_contents = gz_map.get(key).toString().split(",", -1);
                                split_title_content(title_contents);
                                get_catch().put(menu_code + "add_titles", titles);
                                get_catch().put(menu_code + "add_contents", contents);
                            } else if (key.equals("contentstype")) {
                                get_catch().put(menu_code + "add_contentstype", gz_map.get(key).toString());
                            } else {
                                get_catch().put(menu_code + "add_" + key, (Map<String, Object>) gz_map.get(key));
                            }
                        }

                    }
                    parms.remove("addgz");
                }

                get_catch().put(menu_code, parms);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return  false;
            }
        }else{
            if (!res1.equals("")){
                final String finalRes = res1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(finalRes)).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        }
        return  false;
    }

    /**
     * 将 contents 拆分 成 标题 和 内容
     * @param title_contents
     */
    public void split_title_content(String[] title_contents){
        String[] test;
        List<String> titleList = new ArrayList<String>(),
                contentList = new ArrayList<String>();
        for (String title_content:title_contents) {
            if(title_content.indexOf(":")>=0){
                test = title_content.split(":");
                titleList.add(test[0]);
                contentList.add(test[1]);
            }else{
                contentList.add(title_content);
            }
        }

        titles = (String[])titleList.toArray(new String[titleList.size()]);
        contents = (String[])contentList.toArray(new String[contentList.size()]);
    }

    /**
     * 得到 返回数据 带 总记录数
     * @param res
     * @return
     */
    public Map<String,Object> getResMap(String res){
        resMap = FastJsonUtils.strToMap(res.toString());
        if(resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
            return (Map<String, Object>) resMap.get("data");
        }
        return  null;
    }

    @Override
    protected void onDestroy() {
        _catch.remove(menu_code+"_maindata");
        _catch.remove(menu_code+"_recordcount");
        _catch.remove(menu_code+"_pageIndex");

        _catch.remove(menu_code+"_senconddata");
        _catch.remove(menu_code+"sencond_recordcount");
        _catch.remove(menu_code+"sencond_pageIndex");

        _catch.remove(menu_code+"_thirddata");
        _catch.remove(menu_code+"third_recordcount");
        _catch.remove(menu_code+"third_pageIndex");

        _catch.remove(menu_code+"_fourthdata");
        _catch.remove(menu_code+"fourth_recordcount");
        _catch.remove(menu_code+"fourth_pageIndex");

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                System_one one = (System_one) data.getSerializableExtra("parms");
                if (one == null){return;}
                Map<String,Object> dataMap = one.getParms();
                reshView(dataMap);
                break;
        }
    }

    private void reshView(Map<String,Object> dataMap) {
        if (frag != null) {
            fragment_view = frag.getView();
            mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();

            if (dataMap.get("current_type") != null) {
                String id = dataMap.get("id").toString();

                if (get_catch().get(menu_code + "_maindata") != null) {
                    main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());
                    if (!FastJsonUtils.isExitVal(main_datalist, "id", id)) {
                        main_datalist.add(dataMap);
                        recordcount = recordcount + 1;
                        get_catch().put(menu_code + "_maindata", main_datalist);
                        get_catch().put(menu_code + "main_recordcount", recordcount);
                        get_catch().put(menu_code + "main_pageIndex", 1);
                        //删除缓存
                        get_catch().remove(menu_code + "_senconddata");
                        get_catch().remove(menu_code + "sencond_recordcount");
                        get_catch().remove(menu_code + "sencond_pageIndex");

                        get_catch().remove(menu_code + "_thirddata");
                        get_catch().remove(menu_code + "third_recordcount");
                        get_catch().remove(menu_code + "third_pageIndex");

                        get_catch().remove(menu_code + "_fourthdata");
                        get_catch().remove(menu_code + "fourth_recordcount");
                        get_catch().remove(menu_code + "fourth_pageIndex");

                    }
                }

                int backtable_postion = (int) dataMap.get("table_postion");
                if (dataMap.get("index") == null || dataMap.get("index").toString().equals("")){
                    return;
                }
                int index = Integer.parseInt(dataMap.get("index").toString()) ;

                if (table_postion == 4 && (backtable_postion == 2 || backtable_postion == 3)){
                    mAdapter2.getDatas().remove(index);
                    mAdapter2.notifyDataSetChanged();
                }else if (table_postion == backtable_postion &&(table_postion == 1 || table_postion == 4)){
                    if (!FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                        mAdapter2.getDatas().add(dataMap);
                        mAdapter2.notifyDataSetChanged();
                    }
                }else if (table_postion == 2 &&(backtable_postion == 3 )){
                    if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.notifyDataSetChanged();
                    }
                }else if (dataMap.get("current_type")!= null && dataMap.get("current_type").toString().equals("删除")){
                    if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                        /*main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                        main_datalist.remove()*/
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.notifyDataSetChanged();
                    }
                }

                /*if (!FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                    if (table_postion != (int) dataMap.get("table_postion")) {
                        mAdapter2.getDatas().add(dataMap);
                        mAdapter2.notifyDataSetChanged();
                    }
                }*/
            }
        }
    }

    /**
     * 清除 缓存
     */
    public void clearCatch(){
        _catch.remove(menu_code+"_maindata");
        _catch.remove(menu_code+"_recordcount");
        _catch.remove(menu_code+"_pageIndex");

        _catch.remove(menu_code+"_senconddata");
        _catch.remove(menu_code+"sencond_recordcount");
        _catch.remove(menu_code+"sencond_pageIndex");

        _catch.remove(menu_code+"_thirddata");
        _catch.remove(menu_code+"third_recordcount");
        _catch.remove(menu_code+"third_pageIndex");

        _catch.remove(menu_code+"_fourthdata");
        _catch.remove(menu_code+"fourth_recordcount");
        _catch.remove(menu_code+"fourth_pageIndex");
    }

}
