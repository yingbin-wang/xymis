package com.cn.wti.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UIListTableView;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/4/24.
 */

public class testService extends Service{

    //得到缓存数据
    private static PageDataSingleton _catch = PageDataSingleton.getInstance();

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        testService._catch = _catch;
    }

    private  String parms2;
    private  int recordcount,pageIndex;
    private List<Map<String,Object>> main_datalist;
    private String[] title_contents,contents,titles;
    private  boolean isSuccess = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

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
        pars = new ListParms("0","0","10",menu_code,parms2).getParms();
        pars = StringUtils.strTOJsonstr(pars);
        Map<String,Object>parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(menu_code,method,pars);
        try {
            if(!res.equals("")){
                Map<String,Object>resMap = getResMap(res.toString());
                if(resMap.get("results")!= null){
                    recordcount = Integer.parseInt(resMap.get("results").toString());
                    pageIndex = 1;
                    main_datalist = (List<Map<String, Object>>) resMap.get("rows");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (menu_code.equals("process")){return;}

        parms.put("menu_code",menu_code);
        parms.put("sfty","0");
        if(get_catch().get(menu_code)!= null){
            parms = (Map<String, Object>) get_catch().get(menu_code);
        }else{
            String res1 = WebServiceHelper.getResult(AppUtils.app_address,"findFormProperty",parms);
            if(!res1.equals("")){
                try {
                    parms = FastJsonUtils.strToMap(res1);

                    if(parms.get("gz") != null && !parms.get("gz").toString().equals("")){
                        Object gz_object = parms.get("gz");
                        if (gz_object instanceof String){
                            String key,value;
                            String gz = parms.get("gz").toString();
                            if(gz.equals("")){return;}
                            String[] gzs = gz.split("\n");
                            for (String test_gz:gzs) {
                                key = test_gz.substring(0,test_gz.indexOf("="));
                                value = test_gz.substring(test_gz.indexOf("=")+1).replace("\r","");
                                if(key.equals("contents")){
                                    title_contents = value.split(",",-1);
                                    split_title_content(title_contents);
                                    get_catch().put(menu_code+"_titles",titles);
                                    get_catch().put(menu_code+"_contents",contents);
                                }else{
                                    get_catch().put(menu_code+"_"+key,FastJsonUtils.strToMap(value));
                                }
                            }
                        }else if(gz_object instanceof JSONObject){
                            Map<String,Object> gz_map = (Map<String, Object>) gz_object;
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

                        }
                        parms.remove("gz");
                    }

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
        Map<String,Object>resMap = FastJsonUtils.strToMap(res.toString());
        if(resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
            return (Map<String, Object>) resMap.get("data");
        }
        return  null;
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

    public boolean isSuccess() {
        return isSuccess;
    }

    private String menu_code;
    private  View view;
    private int _layout;

    public void createList(Context mContext,List<Map<String,Object>> maings_list, String[] contents, UIListTableView mian_TableView, ClickListener listener) {

        Map<String,Object> map = null,dataMap;
        mian_TableView.setService_name(menu_code);

        String title,subtitle,content_01,content_02,content_03,content_04,content_05,content_06,id = "";
        int approvalstatus = 0,estatus= 0;
        List<String> values = null;
        //无数据操作
        if (maings_list == null){
            refreshTb(mContext,maings_list);
            return;
        }
        for (int i=0,n = maings_list.size();i<n;i++){
            map = maings_list.get(i);
            values = createMain(map,contents);
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

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

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
                }else if(isDrqf){
                    val = data.get(key).toString();
                    val = DateUtil.strFomatDate(val);
                }else{
                    val = data.get(key).toString();
                }

            }else{
                val = "";
            }
            resList.add(val);
        }

        return  resList;
    }

    /**
     * 刷新图表
     * @param maings_list
     */
    public void refreshTb(Context mContext, List<Map<String,Object>> maings_list){
        //无数据操作
        if (maings_list == null){
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
    }

    public void setMenu_code(String menu_code) {
        this.menu_code = menu_code;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void set_layout(int _layout) {
        this._layout = _layout;
    }
}
