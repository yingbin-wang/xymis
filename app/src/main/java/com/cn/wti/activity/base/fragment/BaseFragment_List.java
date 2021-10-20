package com.cn.wti.activity.base.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.DateUtil;
import com.wticn.wyb.wtiapp.R;
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
import com.dina.ui.widget.UIListTableView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseFragment_List extends Fragment {

    protected  Context mContext;
    protected View view;
    protected  Map<String,Object> parms,mapAll;
    protected  List<Map<String,Object>> main_datalist,sencond_datalist,third_datalist,fourth_datalist;
    protected  String menu_code,menu_name,pars,qxMap;
    //列表list 控件
    protected UIListTableView tableView;
    // table 位置
    protected  int tab_postion,layout = 0;
    //分页参数
    protected  int pageIndex, recordcount,_layout;
    //进入编辑页面
    protected  String mxClass_;
    private   Map<String,Object >resMap;
    //得到缓存数据
    protected static PageDataSingleton _catch = PageDataSingleton.getInstance();
    protected int screenWidth,screenHeight;
    protected String[] contents,titles;
    protected Dialog mDialog;
    protected MyHandler myHandler;

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        BaseFragment_List._catch = _catch;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (layout == 0){
            view = inflater.inflate(R.layout.common_list,container,false);
        }else{
            view = inflater.inflate(layout,container,false);
        }

        mContext = getActivity();
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
        if (myHandler == null){
            myHandler = new MyHandler(mContext);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (initData()) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                            if (mDialog != null){
                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                        }
                    });
                } else {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"数据异常",Toast.LENGTH_SHORT).show();
                            if (mDialog != null){
                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                        }
                    });
                }
            }
        }).start();

        return view;
    }

    //绑定数据
    public boolean initData() {
        Bundle arc = this.getArguments();
        if(arc != null){
            System_one so = (System_one)arc.getSerializable("parms");
            if(so == null){
                return false;
            }
            resMap = so.getParms();
            tab_postion = Integer.parseInt(resMap.get("index_").toString());
            menu_code = resMap.get("menu_code").toString();
            menu_name = resMap.get("menu_name").toString();
            mapAll = FastJsonUtils.strToMap(resMap.get("mapAll").toString());
            contents = (String[]) resMap.get("contents");
            titles = (String[]) resMap.get("titles");
            pars = resMap.get("pars").toString();
            mxClass_ = resMap.get("mxClass_").toString();
            _layout = (int) resMap.get("_layout");

            if(get_catch().get(menu_code+"_maindata")!= null) {
                main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                recordcount = Integer.parseInt(get_catch().get(menu_code+"main_recordcount").toString());
                pageIndex = Integer.parseInt(get_catch().get(menu_code+"main_pageIndex").toString())+1;
            }

            switch (tab_postion){
                case 1:
                    if(get_catch().get(menu_code+"_maindata")!= null){
                        main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_maindata");
                        recordcount = Integer.parseInt(get_catch().get(menu_code+"main_recordcount").toString());
                        pageIndex = Integer.parseInt(get_catch().get(menu_code+"main_pageIndex").toString())+1;
                    }else{
                        if( main_datalist == null ){
                            recordcount = Integer.parseInt(resMap.get("recordcount").toString());
                            pageIndex = 1;
                            try {
                                main_datalist = FastJsonUtils.getBeanMapList(resMap.get("main_datalist").toString());
                                get_catch().put(menu_code+"_maindata",main_datalist);
                                get_catch().put(menu_code+"main_recordcount",recordcount);
                                get_catch().put(menu_code+"main_pageIndex",pageIndex);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case 2:
                    if(get_catch().get(menu_code+"_senconddata")!= null){
                        sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_senconddata");
                        recordcount = Integer.parseInt(get_catch().get(menu_code+"sencond_recordcount").toString());
                        pageIndex = Integer.parseInt(get_catch().get(menu_code+"sencond_pageIndex").toString())+1;
                    }else{
                        sencond_datalist = FastJsonUtils.findListPdByKey_and_Val(main_datalist,"approvalstatus","1");
                    }
                    break;
                case 3:
                    if(get_catch().get(menu_code+"_thirddata")!= null){
                        third_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_thirddata");
                        recordcount = Integer.parseInt(get_catch().get(menu_code+"third_recordcount").toString());
                        pageIndex = Integer.parseInt(get_catch().get(menu_code+"third_pageIndex").toString())+1;
                    }else{
                        third_datalist = FastJsonUtils.findListPdByKey_and_Val(main_datalist,"estatus","7");
                    }
                    break;

                case 4:
                    if(get_catch().get(menu_code+"_fourthdata")!= null){
                        fourth_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_fourthdata");
                        recordcount = Integer.parseInt(get_catch().get(menu_code+"fourth_recordcount").toString());
                        pageIndex = Integer.parseInt(get_catch().get(menu_code+"fourth_pageIndex").toString())+1;
                    }else{
                        fourth_datalist = findListPdByKey_and_Val(main_datalist);
                    }
                    break;
                default:
                    break;
            }

            parms = (Map<String, Object>) _catch.get(menu_code);
        }
        return  true;
    }

    public static List<Map<String, Object>> findListPdByKey_and_Val (List<Map<String,Object>> list) {
        List<Map<String, Object>> _dataList = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        for (Map<String,Object> pd : list) {
            if(pd.get("estatus") != null && pd.get("estatus").toString().equals("1"))  {
                if (pd.get("approvalstatus") != null && pd.get("approvalstatus").toString().equals("1")){
                    continue;
                }
                _dataList.add(pd);
            }
        }
        return _dataList;
    }

    public void createView(){
        //列表
        tableView = (UIListTableView) view.findViewById(R.id.tableView);
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
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
            Map<String,Object> map = findDataByTabpostion(index);
            if(map == null){
                Toast.makeText(mContext,"数据异常",Toast.LENGTH_SHORT).show();
                return;
            }
            String id = map.get("id").toString();
            parmsMap.put("mainData", FastJsonUtils.mapToString(map));
            parmsMap.put("mainGs",parms.get("main").toString());
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


    public void createList(List<Map<String,Object>> maings_list, String[] contents, UIListTableView mian_TableView, ClickListener listener) {

        Map<String,Object> map = null,dataMap;
        mian_TableView.setService_name(menu_code);

        String title,subtitle,content_01,content_02,content_03,content_04,content_05,content_06,id = "";
        int approvalstatus = 0,estatus= 0;
        List<String> values = null;
        //无数据操作
        if (maings_list == null || maings_list.size() == 0){
            refreshTb(maings_list);
            return;
        }
        for (int i=0,n = maings_list.size();i<n;i++){
            map = maings_list.get(i);
            values = createMain(map,contents);
            if (values==null || values.size() == 0){return;}
            String[] test_contents = (String[]) values.toArray(new String[values.size()]);
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
            mian_TableView.addBasicItem(id,String.valueOf(i+1), titles,test_contents, R.color.black, true, 1,approvalstatus,estatus);

        }
        mian_TableView.set_dataList(maings_list);
        mian_TableView.setClickListener(listener);
        mian_TableView.setService_name(menu_code);
        mian_TableView.set_layoutid(_layout);
        mian_TableView.commit();

    }

    /**
     * 刷新图表
     * @param maings_list
     */
    public void refreshTb(List<Map<String,Object>> maings_list){
        //无数据操作
        if (maings_list == null || maings_list.size() == 0){
            LinearLayout llt = (LinearLayout) view.findViewById(R.id.ts_llt);
            if (llt !=null){
                llt.setVisibility(View.VISIBLE);
                TextView tsxx = (TextView) view.findViewById(R.id.tsxx);
                tsxx.setText(getString(R.string.nodata));
                ImageView tstb = (ImageView) view.findViewById(R.id.tstb);
                tstb.setImageResource(R.mipmap.nodata);
            }else{
                Toast.makeText(mContext,"无提示视图",Toast.LENGTH_SHORT).show();
            }
        }

        //有数据的时候隐藏
        if (maings_list != null && maings_list.size()>0){
            LinearLayout llt = (LinearLayout) view.findViewById(R.id.ts_llt);
            llt.setVisibility(View.GONE);
        }
    }

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

        if (keys == null){
            Toast.makeText(mContext,"数据异常",Toast.LENGTH_SHORT).show();
            return resList;
        }

        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];

            if(key.indexOf("(drq)")>=0){
                key = key.replace("(drq)","");
                isDrqf = true;
            }else{
                isDrqf = false;
            }

            if ( data.get(key)!= null){
                if (key.equals("iscg")){
                    val = data.get(key).toString();
                    if (val.equals("0")){
                        val = "未完成";
                    }else{
                        val = "已完成";
                    }
                }else if(key.equals("sfyck")){
                    val = data.get(key).toString();
                    if (val.equals("否")){
                        val = "未出库";
                    }else{
                        val = "已出库";
                    }
                }
                else if(key.equals("isyh")){
                    val = data.get(key).toString();
                    if (val.equals("否")){
                        val = "未还";
                    }else{
                        val = "已还";
                    }
                }
                else if(key.equals("haspaid")){
                    //val = data.get("haspaid").toString();
                    if (data.get("haspaid").toString().equals(data.get("costapplymoney").toString())){
                        val = "已支付";
                    }else{
                        val = "未支付";
                    }
                }else if(isDrqf){
                    val = data.get(key).toString();
                    val = DateUtil.strFomatDate(val);
                }else{
                    val = data.get(key).toString();
                }

                if (StringUtils.isNumeric(val)){
                    if (val.indexOf(".")>=0){
                        val = val.substring(0,val.indexOf("."));
                    }
                }

            }else{
                val = "";
            }
            resList.add(val);
        }

        return  resList;
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

        Object res = ActivityController.getDataByPost(mContext.getApplicationContext(),menu_code,method,pars);
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
            if (resMap.get("data") instanceof JSONObject || resMap.get("data") instanceof Map){
                return (Map<String, Object>) resMap.get("data");
            }else if (resMap.get("data") instanceof String){
                return FastJsonUtils.strToMap(resMap.get("data").toString());
            }
        }
        return  null;
    }

    /**
     * 根据 table postion 返回 map 数据
     * @param index
     * @return
     */
    public Map<String,Object> findDataByTabpostion(int index){
        Map<String,Object> map = null;
        if (tableView != null){
            map = tableView.get_dataList().get(index);
        }else{
            map = null;
        }

       /* switch (tab_postion){
            case 1:
                map = main_datalist.get(index);
                break;
            case 2:
                map = sencond_datalist.get(index);
                break;
            case 3:
                map = third_datalist.get(index);
                break;
            case 4:
                map = fourth_datalist.get(index);
                break;
            default:
                break;
        }*/

        return  map;
    }

    public void clearOne(){
        //one
        get_catch().remove(menu_code + "_maindata");
        get_catch().remove(menu_code + "main_recordcount");
        get_catch().remove(menu_code + "main_pageIndex");

        get_catch().remove(menu_code + "_maindata");
        get_catch().remove(menu_code + "main_recordcount");
        get_catch().remove(menu_code + "main_pageIndex");
    }

    public void clearTwo(){
        //two
        get_catch().remove(menu_code + "_senconddata");
        get_catch().remove(menu_code + "sencond_recordcount");
        get_catch().remove(menu_code + "sencond_pageIndex");
    }

    public void clearThree(){
        //three
        get_catch().remove(menu_code + "_thirddata");
        get_catch().remove(menu_code + "third_recordcount");
        get_catch().remove(menu_code + "third_pageIndex");
    }

    public void clearfourth(){
        //fourth
        get_catch().remove(menu_code + "_fourthdata");
        get_catch().remove(menu_code + "fourth_recordcount");
        get_catch().remove(menu_code + "fourth_pageIndex");
    }

}
