package com.cn.wti.activity.base.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.dina.ui.widget.Custom_listClickListener;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonFragment_list extends BaseFragment_List implements MyAdapter2.IonSlidingViewClickListener{

    public List<Map<String,Object>>
            _datalist =  new ArrayList<Map<String,Object>>(); //主格式
    int REQUEST_CODE =1;
    private String serviceName="",method,ywbm = "",parms_str;
    private RecyclerView mRecyclerView1;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter2 mAdapter2;
    private Custom_listClickListener listener;
    private Map<String,Object> deleteMap;
    private boolean[] mCheck;
    private int _layout_ = 0,isEdit = 0;
    private TextView isSelect;
    private LinearLayout ts_llt,testView;
    private ImageView tstb;
    private TextView tsxx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = R.layout.common_list_test;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void createView(){

        super.createView();

        mRecyclerView1 = (RecyclerView) view.findViewById(R.id.list_recyclerView);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView1.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView1.setHasFixedSize(true);

        _datalist.clear();
        if (tab_postion == 1){
            if (main_datalist != null){
                _datalist.addAll(main_datalist);
            }
            ywbm = "待办";
        }else if(tab_postion == 2){
            if (sencond_datalist != null){
                _datalist.addAll(sencond_datalist);
            }
            ywbm = "已办";
        }else if(tab_postion == 3){
            if (third_datalist != null){
                _datalist.addAll(third_datalist);
            }
        }else if(tab_postion == 4){
            if (fourth_datalist != null){
                _datalist.addAll(fourth_datalist);
            }
        }

        reshView();

        //创建并设置Adapter
        mCheck = new boolean[_datalist.size()];
        mAdapter2 = new MyAdapter2(CommonFragment_list.this,_datalist,screenWidth,titles,contents,_layout_,mCheck);
        mRecyclerView1.setAdapter(mAdapter2);

        isSelect = (TextView)view.findViewById(R.id.isSelect);

        // 添加上啦下拉刷新
        ((PullToRefreshLayout) view.findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,serviceName,menu_name,method,pars,mRecyclerView1,tab_postion,_datalist));
    }

    @Override
    public boolean initData() {

        boolean state = true;

        Bundle arc = this.getArguments();
        if(arc != null){
            System_one so = (System_one)arc.getSerializable("parms");
            if(so == null){
                return false;
            }
            Map<String,Object> resMap = so.getParms();
            tab_postion = Integer.parseInt(resMap.get("index_").toString());
            menu_code = resMap.get("menu_code").toString();
            if (resMap.get("service_name") != null){
                serviceName = resMap.get("service_name").toString();
            }else if (menu_code.indexOf("TMTA_")>=0){
                serviceName = menu_code.substring(0,menu_code.indexOf("TMTA_"));
            }else{
                serviceName = menu_code;
            }
            menu_name = resMap.get("menu_name").toString();
            method = resMap.get("method").toString();
            mapAll = FastJsonUtils.strToMap(resMap.get("mapAll").toString());
            titles = (String[]) resMap.get("titles");
            contents = (String[]) resMap.get("contents");
            pars = resMap.get("pars").toString();

            if (resMap.get("qxMap") != null){
                qxMap = resMap.get("qxMap").toString();
            }else{
                qxMap = "{}";
            }

            //20171227 wang 如果 存在 编辑状态
            if (resMap.get("isEdit")!= null){
                isEdit = (int) resMap.get("isEdit");
            }

            mxClass_ = resMap.get("mxClass_").toString();
            if (resMap.get("item_layout") != null){
                _layout_ = (int) resMap.get("item_layout");
            }
            //状态

            if (tab_postion == 1){
                if(get_catch().get(menu_code+"_maindata")!= null && ((List<Map<String,Object>>)get_catch().get(menu_code+"_maindata")).size()>0){
                    main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_maindata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"main_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"main_pageIndex").toString())+1;
                }else{
                    parms_str = StringUtils.strTOJsonstr(pars);

                    Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                    try {
                        resMap = getResMap(res.toString());
                        if (resMap == null){
                            state = false ;
                            return  state;
                        }
                        if(resMap.get("results")!= null){
                            recordcount = Integer.parseInt(resMap.get("results").toString());
                            pageIndex = 1;
                            main_datalist = (List<Map<String, Object>>) resMap.get("rows");
                            get_catch().put(menu_code+"_maindata",main_datalist);
                            get_catch().put(menu_code+"main_recordcount",recordcount);
                            get_catch().put(menu_code+"main_pageIndex",1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }else if(tab_postion == 2){
                if (get_catch().get(menu_code+"_senconddata")!=null){
                    sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_senconddata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"sencond_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"sencond_pageIndex").toString())+1;
                }else{
                    parms_str = StringUtils.strTOJsonstr(pars);

                    Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                    try {
                        resMap = getResMap(res.toString());
                        if (resMap == null){
                            state = false ;
                        }
                        if(resMap.get("results")!= null){
                            recordcount = Integer.parseInt(resMap.get("results").toString());
                            pageIndex = 1;
                            sencond_datalist = (List<Map<String, Object>>) resMap.get("rows");
                            get_catch().put(menu_code+"_senconddata",sencond_datalist);
                            get_catch().put(menu_code+"sencond_recordcount",recordcount);
                            get_catch().put(menu_code+"sencond_pageIndex",1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }else if(tab_postion == 3){
                if (get_catch().get(menu_code+"_thirddata")!=null){
                    third_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_thirddata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"third_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"third_pageIndex").toString())+1;
                }else{
                    parms_str = StringUtils.strTOJsonstr(pars);

                    Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                    try {
                        resMap = getResMap(res.toString());
                        if (resMap == null){
                            state = false ;
                        }
                        if(resMap.get("results")!= null){
                            recordcount = Integer.parseInt(resMap.get("results").toString());
                            pageIndex = 1;
                            third_datalist = (List<Map<String, Object>>) resMap.get("rows");
                            get_catch().put(menu_code+"_thirddata",third_datalist);
                            get_catch().put(menu_code+"third_recordcount",recordcount);
                            get_catch().put(menu_code+"third_pageIndex",1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }else if(tab_postion == 4){
                if (get_catch().get(menu_code+"_fourthdata")!=null){
                    fourth_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_fourthdata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"fourth_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"fourth_pageIndex").toString())+1;
                }else{
                    parms_str = StringUtils.strTOJsonstr(pars);

                    Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                    try {
                        resMap = getResMap(res.toString());
                        if (resMap == null){
                            state = false ;
                        }
                        if(resMap.get("results")!= null){
                            recordcount = Integer.parseInt(resMap.get("results").toString());
                            pageIndex = 1;
                            fourth_datalist = (List<Map<String, Object>>) resMap.get("rows");
                            get_catch().put(menu_code+"_fourthdata",fourth_datalist);
                            get_catch().put(menu_code+"fourth_recordcount",recordcount);
                            get_catch().put(menu_code+"fourth_pageIndex",1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            if (resMap == null){return  false;}

            if(tab_postion == 1 &&  (main_datalist == null || main_datalist.size() == 0)){

                try {
                    main_datalist = FastJsonUtils.getBeanMapList(resMap.get("main_datalist").toString());
                    _catch.put(menu_code+"main_recordcount",recordcount);
                    _catch.put(menu_code+"main_pageIndex",pageIndex);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tab_postion == 2 && (sencond_datalist == null || sencond_datalist.size() == 0)){

                parms_str = StringUtils.strTOJsonstr(pars);
                parms = new HashMap<String, Object>();
                Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                try {
                    resMap = getResMap(res.toString());
                    if(resMap.get("results")!= null){
                        recordcount = Integer.parseInt(resMap.get("results").toString());
                        pageIndex = 1;
                        sencond_datalist = (List<Map<String, Object>>) resMap.get("rows");
                        get_catch().put(menu_code+"_senconddata",sencond_datalist);
                        get_catch().put(menu_code+"sencond_recordcount",recordcount);
                        get_catch().put(menu_code+"sencond_pageIndex",1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            parms = (Map<String, Object>) _catch.get(menu_code);

        }
        return  state;
    }

    @Override
    public void onItemClick(View view, int position) {

        if (isSelect!= null && isSelect.getText().equals("取消")){
            mCheck = mAdapter2.getSelectItem();
            mCheck[position] = !mCheck[position];
            mAdapter2.notifyDataSetChanged();
        }else{
            if (_datalist.size() == 0){
                Toast.makeText(mContext,"数据存在异常",Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String,Object> map = _datalist.get(position);

            Intent intent = new Intent();

            Map<String,Object> resMap = new HashMap<String, Object>();
            resMap.put("type","edit");
            resMap.put("index",position);
            resMap.put("qxMap",qxMap);
            //20171227 wang 如果存在 serviceName 取 ser
            if (!serviceName.equals("")){
                resMap.put("serviceName",serviceName);
            }

            resMap.put("isEdit",isEdit);

            resMap.put("menucode",menu_code);
            resMap.put("menuname",menu_name);
            resMap.put("table_postion",tab_postion);
            resMap.put("mainData",map);
            String qz = "edit_";
            if (parms != null){
                resMap.put("mainGs",parms.get(qz+"main").toString());
                if(mapAll != null) {
                    Set<String> sets = mapAll.keySet();
                    for (String key : sets) {
                        Map<String, Object> _map = (Map<String, Object>) mapAll.get(key);

                        if (key.equals("mx1gs_list")) {
                            String code = _map.get("code").toString();
                            resMap.put("mx1gs_list", parms.get(qz+code).toString());
                            resMap.put("mx1_data", "");
                        } else if (key.equals("mx2gs_list")) {
                            String code = _map.get("code").toString();
                            resMap.put("mx2gs_list", parms.get(qz+code).toString());
                            resMap.put("mx2_data", "");
                        } else if (key.equals("add")) {
                        } else if (key.equals("mx3gs_list")){
                            String code = _map.get("code").toString();
                            resMap.put("mx3gs_list", parms.get(qz+code).toString());
                            resMap.put("mx3_data", "");
                        } else if (key.equals("mx4gs_list")){
                            String code = _map.get("code").toString();
                            resMap.put("mx4gs_list", parms.get(qz+code).toString());
                            resMap.put("mx4_data", "");
                        } else if (key.equals("mx5gs_list")){
                            String code = _map.get("code").toString();
                            resMap.put("mx5gs_list", parms.get(qz+code).toString());
                            resMap.put("mx5_data", "");
                        }
                    }
                }
            }

            if (map.get("id") != null){
                resMap.put("id",map.get("id"));
            }
            deleteMap = map;
            intent.putExtras(AppUtils.setParms("edit",resMap));
            intent.putExtra("ywbm",ywbm);

            Class class1 = ReflectHelper.getCalss(mxClass_);
            if (class1 != null){
                intent.setClass(mContext,class1);
                Activity test = (Activity) mContext;
                test.startActivityForResult(intent,REQUEST_CODE);
            }

        }
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

    @Override
    public void onCuibanBtnClick(View view, int position) {
        final Map<String,Object> map = _datalist.get(position);
        String parms = FastJsonUtils.mapToString(map),parms1 = "";
        if (parms != null){
            parms1 = parms.substring(0,parms.length()-1)+",\"userName\":\""+AppUtils.app_username+"\",\"zydn\":\""+AppUtils.user.get_zydnName()+"\",\"zydnid\":\""+AppUtils.user.get_zydnId()+"\"}";
        }

        //执行催办动作
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"催办...");
        final String finalParms = parms1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = ActivityController.executeForResultByThread(mContext,"process","cuiBanProcess", finalParms);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WeiboDialogUtils.closeDialog(mDialog);
                        if (res.indexOf("err")<0 && !res.equals("")){
                            Toast.makeText(mContext,res,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext,res.replace("err",""),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onZuofeiBtnClick(View view, int postion) {
        final Map<String,Object> map = _datalist.get(postion);
        String parms1 = "",ID_ = map.get("ID_").toString(),BUSINESS_KEY_ = map.get("BUSINESS_KEY_").toString();
        parms1 = "{\"processInstanceId\":\"" + ID_ + "\",\"bussinessKey\":\"" + BUSINESS_KEY_ + "\",\"userName\":\""+AppUtils.app_username+"\",\"zydn\":\""+AppUtils.user.get_zydnName()+"\",\"zydnid\":\""+AppUtils.user.get_zydnId()+"\"}";

        //执行作废动作
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"作废...");
        final String finalParms = parms1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = ActivityController.executeForResultByThread(mContext,"process","zfConller", finalParms);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WeiboDialogUtils.closeDialog(mDialog);
                        if (res.indexOf("err")<0 && !res.equals("")){
                            Toast.makeText(mContext,res,Toast.LENGTH_SHORT).show();
                            clearOne();
                            clearTwo();
                            reshData();
                        }else{
                            Toast.makeText(mContext,res.replace("err",""),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
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

    public void  reshView(){
        ts_llt = (LinearLayout) view.findViewById(R.id.ts_llt);
        tstb = (ImageView) view.findViewById(R.id.tstb);
        tsxx = (TextView) view.findViewById(R.id.tsxx);
        testView  = (LinearLayout) view.findViewById(R.id.testView);

        if (!AppUtils.network_state || !AppUtils.fwq_state){

            ts_llt.setVisibility(View.VISIBLE);
            tstb.setBackground(getResources().getDrawable(R.mipmap.wuwangluo));
            tsxx.setText(getString(R.string.error_invalid_network));
            testView.setVisibility(View.GONE);
            return;
        }else if (_datalist != null && _datalist.size()==0){
            ts_llt.setVisibility(View.VISIBLE);
            tstb.setBackground(getResources().getDrawable(R.mipmap.nodata));
            tsxx.setText(getString(R.string.error_invalid_nodata));
            testView.setVisibility(View.GONE);
        }else if (_datalist == null){
            return;
        }else{
            testView.setVisibility(View.VISIBLE);
            ts_llt.setVisibility(View.GONE);
        }

        ts_llt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reshData();
            }
        });
    }

    public void reshData(){

        parms_str = StringUtils.strTOJsonstr(pars);
        _datalist.clear();
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"刷新...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = ActivityController.getDataByPost(mContext,serviceName,method,parms_str);
                if (res != null){
                    if (mDialog != null){
                        WeiboDialogUtils.closeDialog(mDialog);
                        if (tab_postion == 1){
                            clearOne();
                            try {
                                Map<String,Object> resMap = getResMap(res.toString());
                                if(resMap.get("results")!= null){
                                    recordcount = Integer.parseInt(resMap.get("results").toString());
                                    pageIndex = 1;
                                    main_datalist = (List<Map<String, Object>>) resMap.get("rows");
                                    get_catch().put(menu_code+"_maindata",main_datalist);
                                    get_catch().put(menu_code+"main_recordcount",recordcount);
                                    get_catch().put(menu_code+"main_pageIndex",1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (main_datalist != null){
                                _datalist.addAll(main_datalist);
                            }

                        }else if(tab_postion == 2){
                            clearTwo();
                            try {
                                Map<String,Object> resMap = getResMap(res.toString());
                                if(resMap.get("results")!= null){
                                    recordcount = Integer.parseInt(resMap.get("results").toString());
                                    pageIndex = 1;
                                    sencond_datalist = (List<Map<String, Object>>) resMap.get("rows");
                                    get_catch().put(menu_code+"_senconddata",sencond_datalist);
                                    get_catch().put(menu_code+"sencond_recordcount",recordcount);
                                    get_catch().put(menu_code+"sencond_pageIndex",1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            if (sencond_datalist != null){
                                _datalist.addAll(sencond_datalist);
                            }

                        }else if(tab_postion == 3){
                            clearThree();
                            try {
                                Map<String,Object> resMap = getResMap(res.toString());
                                if(resMap.get("results")!= null){
                                    recordcount = Integer.parseInt(resMap.get("results").toString());
                                    pageIndex = 1;
                                    third_datalist = (List<Map<String, Object>>) resMap.get("rows");
                                    get_catch().put(menu_code+"_thirddata",third_datalist);
                                    get_catch().put(menu_code+"third_recordcount",recordcount);
                                    get_catch().put(menu_code+"third_pageIndex",1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (third_datalist != null){
                                _datalist.addAll(third_datalist);
                            }

                        }else if(tab_postion == 4){
                            clearfourth();
                            try {
                                Map<String,Object> resMap = getResMap(res.toString());
                                if(resMap.get("results")!= null){
                                    recordcount = Integer.parseInt(resMap.get("results").toString());
                                    pageIndex = 1;
                                    fourth_datalist = (List<Map<String, Object>>) resMap.get("rows");
                                    get_catch().put(menu_code+"_fourthdata",fourth_datalist);
                                    get_catch().put(menu_code+"fourth_recordcount",recordcount);
                                    get_catch().put(menu_code+"fourth_pageIndex",1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (fourth_datalist != null) {
                                _datalist.addAll(fourth_datalist);
                            }
                        }
                    }

                    if (_datalist != null){

                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter2.refreshData(new boolean[_datalist.size()]);
                                reshView();
                            }
                        });
                    }

                }
            }
        }).start();


    }
}
