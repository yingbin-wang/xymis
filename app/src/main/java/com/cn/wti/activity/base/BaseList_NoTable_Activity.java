package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.RecyclerViewClickListener;
import com.dina.ui.widget.UIListTableView;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseList_NoTable_Activity extends Activity implements View.OnClickListener,MyAdapter2.IonSlidingViewClickListener{

    protected String[]tab_names = null;
    protected  Context mContext;
    protected View view;
    protected  Map<String,Object> parms,mapAll;
    protected  List<Map<String,Object>> main_datalist;
    protected  String menu_code,menu_name,pars,class_name,index,
                        parms2="" /*特殊参数*/;
    //列表list 控件
    protected UIListTableView tableView;
    protected String[] contents;
    //分页参数
    protected  int pageIndex, recordcount;
    //进入编辑页面
    protected  String mxClass_;
    private   Map<String,Object >resMap;
    //得到缓存数据
    private static PageDataSingleton _catch = PageDataSingleton.getInstance();
    protected int REQUEST_CODE = 1,title_layout=0,item_layoyt = 0,content_layout = 0;

    protected ImageButton btn_back,btn_ok;
    protected ActionBar actionBar;

    protected  int screenWidth,screenHeight;

    protected TextView title_tv;
    protected RecyclerView mRecyclerView1;
    private LinearLayoutManager mLayoutManager;
    protected MyAdapter2 mAdapter2;
    protected List<Map<String,Object>> _datalist =  new ArrayList<Map<String,Object>>(); //主格式
    protected boolean[] mCheck = {};
    protected Dialog mDialog;

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        BaseList_NoTable_Activity._catch = _catch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        actionBar = this.getActionBar();


        if (actionBar != null){

            setTheme(R.style.CustomActionBarTheme);
            if(content_layout != 0){
                setContentView(content_layout);
            }else{
                setContentView(R.layout.common_list);
            }

            /*if (tab_names == null){
                tab_names = new String[]{"全部","已审核","未审核","审批中"};
            }*/

            if (title_layout != 0){
                actionBar.setCustomView(title_layout);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                        | ActionBar.DISPLAY_SHOW_HOME);
                actionBar.setDisplayShowHomeEnabled(false);
                //设置标题
                TextView main_title = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
                main_title.setText(menu_name);
                //返回按钮
                btn_back = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);
                btn_ok = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_ok2);

                if (btn_back != null){
                    btn_back.setOnClickListener(this);
                }

                if (btn_ok != null){
                    btn_ok.setOnClickListener(this);
                }

            }

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setLogo(R.mipmap.navigationbar_back);
        }

        //列表 控件
        tableView = (UIListTableView) this.findViewById(R.id.tableView);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    /*addAction(mContext,mxClass_,REQUEST_CODE);*/

    public void addAction(Context context,String class_name,int REQUEST_CODE){
        Map<String,Object> parmsMap = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        Intent intent = new Intent();
        parmsMap = createIntentMap(parmsMap,map,mapAll,"");
        intent.putExtras(AppUtils.setParms("add",parmsMap));
        Class class1 = ReflectHelper.getCalss(class_name);
        intent.setClass(context,class1);
        startActivityForResult(intent,REQUEST_CODE);
    }

    public Map<String,Object> createIntentMap(Map<String,Object> parmsMap,Map<String,Object> map,Map<String,Object> mxNames,String id){
        parmsMap.put("mainData",FastJsonUtils.mapToString(map));
        parmsMap.put("mainGs",parms.get("main").toString());
        if(mxNames != null){
            Set<String> sets = mxNames.keySet();
            for (String key :sets){
                Map<String,Object> _map = (Map<String,Object>)mxNames.get(key);

                if (key.equals("mx1gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx1gs_list",parms.get(code));
                    parmsMap.put("mx1_data","");
                }else if(key.equals("mx2gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx2gs_list",parms.get(code));
                    parmsMap.put("mx2_data","");
                }else if(key.equals("mx3gs_list")){
                    String code = _map.get("code").toString();
                    parmsMap.put("mx3gs_list",parms.get(code));
                    parmsMap.put("mx3_data","");
                }
            }
        }
        parmsMap.put("id",id);
        return  parmsMap;
    }


    public void showList() {
        Fragment frag =null;
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

        Bundle bundle = new Bundle();
        Map<String,Object> parms = new HashMap<String, Object>();
        parms.put("recordcount",recordcount);
        parms.put("pageIndex",1);
        parms.put("main_datalist",FastJsonUtils.ListMapToListStr(main_datalist));
        parms.put("mapAll",FastJsonUtils.mapToString(mapAll));
        parms.put("mxClass_",mxClass_);
        parms.put("menu_code",menu_code);
        parms.put("menu_name",menu_name);
        parms.put("pars",pars);
        parms.put("contents",contents);
        bundle = AppUtils.setParms("",parms);
        frag.setArguments(bundle);

        FragmentTransaction action = BaseList_NoTable_Activity.this.getFragmentManager()
                .beginTransaction();

        action.replace(R.id.container, frag);
        action.commit();
    }

    /**
     * 初始化 数据 与 显示 格式
     * @param menu_code
     * @param menu_name
     * @param method
     * @param pars
     */
    public  void initData(String menu_code,String menu_name,String method,String pars){

        /*pars = "{pageIndex:0,start:0,limit:10,userId:"+AppUtils.app_username+"}";*/
        //添加 数据权限 菜单编号
        pars = new ListParms("0","0",AppUtils.list_limit,menu_code,parms2).getParms();
        this.pars = pars;
        class_name = "com.cn.wti.activity.base.fragment.CommonFragment";

        pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(mContext,menu_code,method,pars);
        try {
           if(!res.equals("")){
               resMap = getResMap(res.toString());
               if(resMap.get("results")!= null){
                   recordcount = Integer.parseInt(resMap.get("results").toString());
                   pageIndex = 1;
                   main_datalist = (List<Map<String, Object>>) resMap.get("rows");
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        parms.put("menu_code",menu_code);
        parms.put("sfty","0");
        if(get_catch().get(menu_code)!= null){
            parms = (Map<String, Object>) get_catch().get(menu_code);
        }else{
            String res1 = WebServiceHelper.getResult(AppUtils.app_address,"findFormProperty",parms);
            if(!res1.equals("")){
                try {
                    parms = FastJsonUtils.strToMap(res1);

                    if(parms.get("gz") != null){
                        String key,value;
                        String gz = parms.get("gz").toString();
                        if(gz.equals("")){return;}
                        String[] gzs = gz.split("\n");
                        for (String test_gz:gzs) {
                            key = test_gz.substring(0,test_gz.indexOf("="));
                            value = test_gz.substring(test_gz.indexOf("=")+1).replace("\r","");
                            if(key.equals("contents")){
                                get_catch().put(menu_code+"_"+key,value.split(",",-1));
                            }else{
                                get_catch().put(menu_code+"_"+key,FastJsonUtils.strToMap(value));
                            }
                        }
                    }
                    parms.remove("gz");
                    get_catch().put(menu_code,parms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public int getTitle_layout() {
        return title_layout;
    }

    public void setTitle_layout(int title_layout) {
        this.title_layout = title_layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back2:
                this.finish();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //编辑按钮
        Button bianji_tv = (Button) findViewById(R.id.ok);
        if (bianji_tv != null && bianji_tv.getText().equals("取消")){
            mCheck = mAdapter2.getSelectItem();
            mCheck[position] = !mCheck[position];
            mAdapter2.notifyDataSetChanged();
        }
        else{
            Map<String,Object> map = mAdapter2.getDatas().get(position);

            Intent intent = new Intent();

            Map<String,Object> resMap = new HashMap<String, Object>();
            resMap.put("type","edit");
            resMap.put("mainData",map);
            resMap.put("table_postion",1);
            if (map.get("id") != null){
                resMap.put("id",map.get("id"));
            }
            resMap.put("index",position);
            intent.putExtras(AppUtils.setParms("edit",resMap));

            Class class1 = ReflectHelper.getCalss(mxClass_);
            intent.setClass(mContext,class1);
            Activity test = (Activity) mContext;
            test.startActivityForResult(intent,1);
        }

    }

    @Override
    public void onCuibanBtnClick(View view, int position) {

    }

    @Override
    public void onZuofeiBtnClick(View view, int postion) {

    }

    @Override
    public void onDeleteBtnClilck(View view, int position) {
        Map<String,Object> map = _datalist.get(position);
        if (map != null){
            String ids = map.get("id").toString();
            boolean isDel = delteAll(ids);
            if (isDel){
                _datalist.remove(map);
                mAdapter2.notifyDataSetChanged();
            }
        }
    }

    /**
     * main 点击事件
     */
    public class CustomClickListener implements ClickListener {

        private List<Map<String,Object>> _list;
        private Context context;
        private List<IListItem> _basicItemList;
        private BasicItem basicItem;
        private Map<String,Object> map,mxNames;
        private String className;
        private int REQUEST_CODE;

        public CustomClickListener(Context context,List<Map<String,Object>> _list,List<IListItem> _basicItemList,String className,int REQUEST_CODE){
            this._list = _list;
            this.context = context;
            this._basicItemList = _basicItemList;
            this.className = className;
            this.REQUEST_CODE = REQUEST_CODE;
        }

        public CustomClickListener(Context context,List<Map<String,Object>> _list,List<IListItem> _basicItemList,String className,int REQUEST_CODE,Map<String,Object> mxNames){
            this._list = _list;
            this.context = context;
            this._basicItemList = _basicItemList;
            this.className = className;
            this.REQUEST_CODE = REQUEST_CODE;
            this.mxNames = mxNames;
        }

        @Override
        public void onClick(final int index,View objectView) {

            Map<String,Object> parmsMap = new HashMap<String, Object>();
            Intent intent = new Intent();
            Map<String,Object> map = main_datalist.get(index);
            String id = map.get("id").toString();
            parmsMap.put("mainData", FastJsonUtils.mapToString(map));
            if(mxNames != null){
                Set<String> sets = mxNames.keySet();
                for (String key :sets){
                    Map<String,Object> _map = (Map<String,Object>)mxNames.get(key);

                    if (key.equals("mx1gs_list")){
                        String code = _map.get("code").toString();
                        parmsMap.put("mx1gs_list",parms.get(code).toString());
                        parmsMap.put("mx1_data","");
                    }else if(key.equals("mx2gs_list")){
                        String code = _map.get("code").toString();
                        parmsMap.put("mx2gs_list",parms.get(code).toString());
                        parmsMap.put("mx2_data","");
                    }else if(key.equals("add")){
                    }else{
                        String code = _map.get("code").toString();
                        parmsMap.put("mx3gs_list",parms.get(code).toString());
                        parmsMap.put("mx3_data","");
                    }
                }
            }
            parmsMap.put("id",id);
            intent.putExtras(AppUtils.setParms("edit",parmsMap));
            Class class1 = ReflectHelper.getCalss(className);
            intent.setClass(context,class1);
            startActivityForResult(intent,REQUEST_CODE);
        }
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
        }else{
            return  resMap;
        }
    }

    /**
     * 刷新图表
     * @param maings_list
     */
    public void refreshTb(List<Map<String,Object>> maings_list){
        //无数据操作
        if (maings_list == null ||maings_list.size()==0){
            LinearLayout llt = (LinearLayout) findViewById(R.id.ts_llt);
            if (llt !=null){
                llt.setVisibility(View.VISIBLE);
                TextView tsxx = (TextView) findViewById(R.id.tsxx);
                tsxx.setText(getString(R.string.nodata));
                ImageView tstb = (ImageView) findViewById(R.id.tstb);
                tstb.setImageResource(R.mipmap.nodata);
            }else{
                Toast.makeText(mContext,"无提示视图",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        _catch.remove(menu_code+"_maindata");
        _catch.remove(menu_code+"_recordcount");
        _catch.remove(menu_code+"_pageIndex");
        super.onDestroy();
    }

    public  void createView(){

        mRecyclerView1 = (RecyclerView) findViewById(R.id.list_recyclerView);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView1.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView1.setHasFixedSize(true);
        //创建并设置Adapter

        mCheck = new boolean[_datalist.size()];

        mAdapter2 = new MyAdapter2(mContext,_datalist,screenWidth,new String[]{},contents,item_layoyt,mCheck);
        mRecyclerView1.setAdapter(mAdapter2);

        // 添加上啦下拉刷新
        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,menu_code,menu_name,"list",pars,mRecyclerView1,1,_datalist));
    }

    /**
     * 删除动作
     * @param ids
     * @return
     */
    public boolean delteAll(String ids){
        pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+ids+"\"}";
        String isDel = ActivityController.executeForResult(mContext,menu_code,"deleteAll",pars);
        if (!isDel.equals("err")){
            return  true;
        }else{
            return  false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== 1){
            String type = data.getStringExtra("type");
            if(!type.equals("")){
                int index = Integer.parseInt(data.getStringExtra("index"));
                mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();
                mAdapter2.getDatas().remove(index);
                mAdapter2.notifyDataSetChanged();
            }else{
                int index = Integer.parseInt(data.getStringExtra("index"));
                mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();
                System_one one = (System_one) data.getSerializableExtra("parms");
                if (one != null) {
                    Map<String, Object> dataMap = one.getParms();
                    mAdapter2.getDatas().remove(index);
                    mAdapter2.getDatas().add(index, dataMap);
                }
            }

        }else if (resultCode == 2){
            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type");
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();
            System_one one = (System_one) data.getSerializableExtra("parms");
            if (one != null){
                Map<String,Object> dataMap = one.getParms();
                if (type.equals("add")){
                    mAdapter2.getDatas().add(0,dataMap);
                }else if (type.equals("edit")){
                    mAdapter2.getDatas().remove(index);
                    mAdapter2.getDatas().add(index,dataMap);
                }
                mAdapter2.refreshData(mAdapter2.getSelectItem());
            }
        }
    }
}
