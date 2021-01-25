package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UIListTableView;
import com.dina.ui.widget.UITableView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseListActivity extends BaseActivity implements ActionBar.TabListener{

    protected  String menu_code,menu_name;
    protected  LayoutInflater inflater;
    protected  ImageButton btn_back,btn_ok;
    protected  Map<String,Object> parms,mapAll;
    protected  List<Map<String,Object>> main_datalist;
    /*maxid,minid,*/
    protected  String mxClass_;
    protected ActionBar actionBar;
    protected  Context mContext;
    private   Map<String,Object >resMap;

    int REQUEST_CODE = 1;
    protected  int pageIndex, recordcount;

    //tab 名称
    /*protected String[] tab_names = new String[]{"全部","已审核","未审核","审批中","已完成"};*/
    protected String[] tab_names = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list);
        this.inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar = this.getActionBar();
        //如果 存在Tabs 添加
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        if(tab_names != null){
            for (String name:tab_names){
                actionBar.addTab(actionBar.newTab().setText(name)
                        .setTabListener(this));
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

    public void createList(List<Map<String,Object>> maings_list, String[] contents, UIListTableView mian_TableView, ClickListener listener) {

        Map<String,Object> map = null,dataMap;
        mian_TableView.setService_name(menu_code);

        String title,subtitle,content_01,content_02,content_03,content_04,content_05,content_06,id = "";
        int approvalstatus = 0,estatus= 0;
        List<String> values = null;
        if (maings_list == null){return;}
        for (int i=0,n = maings_list.size();i<n;i++){
            map = maings_list.get(i);
            values = createMain(map,contents);
            if(map.get("id") != null){
                id = map.get("id").toString();
            }

            if(map.get("approvalstatus") != null){
                approvalstatus = Integer.parseInt(map.get("approvalstatus").toString());
            }

            if(map.get("estatus") != null){
                estatus = Integer.parseInt(map.get("estatus").toString());
            }

            //创建 审批状态 列表对象
            mian_TableView.addBasicItem(id,String.valueOf(i+1), values.get(0),values.get(1),values.get(2),values.get(3),
                    values.get(4),values.get(5),values.get(6),"", R.color.black, true, 1,approvalstatus,estatus);

        }
        mian_TableView.setClickListener(listener);
        mian_TableView.setService_name(menu_code);
        mian_TableView.commit();

    }

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


    /**
     * 初始化 数据 与 显示 格式
     * @param menu_code
     * @param menu_name
     * @param method
     * @param pars
     */
    public  void initData(String menu_code,String menu_name,String method,String pars){

        pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(mContext,menu_code,method,pars);
        try {
            resMap = getResMap(res.toString());
            if(resMap.get("results")!= null){
                recordcount = Integer.parseInt(resMap.get("results").toString());
                pageIndex = 1;
                main_datalist = (List<Map<String, Object>>) resMap.get("rows");
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
                    get_catch().put(menu_code,parms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        }
        return  null;
    }

    /**
     * 加载数据
     * @param menu_code
     * @param menu_name
     * @param method
     * @param pars
     */
    public  void loadData(String menu_code,String menu_name,String method,String pars){

        List<Map<String,Object>> resList = null;
        pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(mContext,menu_code,"list",pars);
        try {
            resList = FastJsonUtils.getResultList(FastJsonUtils.strToMap(res.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (main_datalist != null && main_datalist.size()>0){
            main_datalist.addAll(resList);
        }else{
            main_datalist = resList;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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
        public void onClick(final int index) {

            Map<String,Object> parmsMap = new HashMap<String, Object>();
            Intent intent = new Intent();
            Map<String,Object> map = new HashMap<String, Object>(main_datalist.get(index));
            String id = map.get("id").toString();
            parmsMap.put("mainData",FastJsonUtils.mapToString(map));
            parmsMap.put("mainGs",parms.get("main").toString());
            if(mxNames != null){
                Set<String> sets = mxNames.keySet();
                for (String key :sets){
                    if (key.equals("mx1gs_list")){
                        parmsMap.put("mx1gs_list",parms.get(mxNames.get(key)).toString());
                        parmsMap.put("mx1_data","");
                    }else{
                        parmsMap.put("mx2gs_list",parms.get(mxNames.get(key)).toString());
                        parmsMap.put("mx2_data","");
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

    public Map<String,Object> createIntentMap(Map<String,Object> parmsMap,Map<String,Object> map,Map<String,Object> mxNames,String id){
        parmsMap.put("mainData",FastJsonUtils.mapToString(map));
        parmsMap.put("mainGs",parms.get("main").toString());
        if(mxNames != null){
            Set<String> sets = mxNames.keySet();
            for (String key :sets){
                if (key.equals("mx1gs_list")){
                    parmsMap.put("mx1gs_list",parms.get(mxNames.get(key)).toString());
                    parmsMap.put("mx1_data","");
                }else{
                    parmsMap.put("mx2gs_list",parms.get(mxNames.get(key)).toString());
                    parmsMap.put("mx2_data","");
                }
            }
        }
        parmsMap.put("id",id);
        return  parmsMap;
    }

   /* protected class CustomClickListener2 implements ClickListener {

        private List<Map<String,Object>> _list;
        private Context context;
        private List<IListItem> _basicItemList;
        private BasicItem basicItem;
        private Map<String,Object> map;
        private String title;
        private UITableView tableView;
        private String parms1;

        public CustomClickListener2(Context context,List<Map<String,Object>> _list,UITableView tableView,String parms1){
            this._list = _list;
            this.context = context;
            this.tableView = tableView;
            this.parms1 = parms1;
            _basicItemList = tableView.getIListItem();
        }

        @Override
        public void onClick(final int index) {
            *//*Toast.makeText(xsdd_editActivity.this, "item clicked: " + index, Toast.LENGTH_SHORT).show();*//*
            *//*获取表单格式*//*
            map = _list.get(index);
            *//*获取数据模板*//*
            basicItem = (BasicItem) _basicItemList.get(index);
            int type = 1;

            final View v  = tableView.getLayoutList(index);

            String[] gldns = null;
            if (map.get("gldn") != null && !map.get("gldn").toString().equals("")) {
                String[] items = null;

                final String gldn = map.get("gldn").toString();
                if (gldn.equals("")) {
                    return;
                } else if (gldn.contains(",")) {
                    items = gldn.split(",");
                    type = 1;
                } else {

                    gldns = gldn.split("~");
                    String col_name = getColumnName(gldn);

                    if (selectMap.get(gldns[0]) != null) {
                        items = FastJsonUtils.ListMapToListStr((List<Map<String, Object>>) selectMap.get(gldns[0]), col_name);
                    } else {
                        String method = getMethodName(gldn);
                        if (parms1 == null ||parms1.equals("")){
                            parms1 = setParms(gldn,tableView);
                        }

                        String pars = "{pageIndex:0,start:0,limit:"+AppUtils.limit+",menu_code:"+menu_code+","+parms1+"}";
                        if(pars.indexOf("~")>=0){
                            int end = pars.indexOf("~");
                            String pars1 = pars.substring(0,end);
                            String pars2 = pars.substring(end,pars.length());
                            pars1 = StringUtils.strTOJsonstr(pars1);
                            pars = pars1 + pars2;
                        }else{
                            pars = StringUtils.strTOJsonstr(pars);
                        }
                        items = ActivityController.getDialogData(gldns[0].toLowerCase(),method,get_catch(),gldn,col_name,AppUtils.limit,pars);
                    }

                    type = 2;
                }

                List<Map<String,Object>> testMapList;
                if(get_catch().get(gldn) != null){
                    testMapList = (List<Map<String,Object>>)get_catch().get(gldn);
                }else {
                    testMapList = new ArrayList<Map<String,Object>>();
                }
                Map<String,Object> testMap = null;
                if (parms.get(gldn) != null){
                    testMap = (Map<String, Object>) parms.get(gldn);
                }else{
                    testMap = new HashMap<String, Object>();
                }

                if (gldns == null){
                    title = "下拉选择";
                }else{
                    title = gldns[1];
                }
                if(items == null){
                    items = new String[]{};
                }
                *//*ActivityController.showDialog(context,title,items,testMapList,_basicItemList,testMap,tableView,index);*//*
                ActivityController.showDialog(context,title,items,testMapList,_basicItemList,testMap,tableView,index,parms1);
            }else{
                Date date = null;
                if(basicItem.getSubtitle().equals("")){
                    date = DateUtil.fomatDate(DateUtil.createDate());
                }else{
                    date = DateUtil.fomatDate(basicItem.getSubtitle());
                }
                final Date dateq = date;
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        basicItem.setSubtitle(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        tableView.setupBasicItemValue(v,basicItem,index);
                    }
                }, dateq.getYear()+1900, dateq.getMonth(), Integer.parseInt(DateUtil.getDay(dateq)));

                datePickerDialog.show();
            }
        }
    }*/

    public String setParms(String gldn,UITableView tableView){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);

        String parms = "";
        if (parmsMap.get("parms")!=null){
            Map<String,Object> test = FastJsonUtils.strToMap(parmsMap.get("parms").toString());
            Set<String> sets = test.keySet();

            int size = sets.size();
            int i = 0;
            for (String key:sets){

                BasicItem item = ActivityController.getItem(tableView.getIListItem(),test.get(key).toString());
                String val = "";
                if(item == null){
                    val = test.get(key).toString();
                }else{
                    val = item.getSubtitle();
                }

                if(i== size -1){
                    parms +=key+":"+val;
                }else{
                    parms +=key+":"+val+",";
                }
            }

        }
        return  parms;
    }

    public String getMethodName(String gldn){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);
        String method = "";
        if(parmsMap.get("href") == null){
            method = "list";
        }else{
            method = parmsMap.get("href").toString();
        }

        return  method;
    }

    public String getColumnName(String gldn){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);
        String method = "";
        if(parmsMap.get("col_name") == null){
            method = "name";
        }else{
            method = parmsMap.get("col_name").toString();
        }

        return  method;
    }

}
