package com.cn.wti.activity.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListTableViewListener;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.Custom_taskClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTaskFragment extends BaseFragment_List{

    private List<Map<String,Object>>
            _datalist = null; //主格式
    int REQUEST_CODE =1;
    private String method,ywbm = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = R.layout.common_list_mytask;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void createView(){

        super.createView();

        if (tab_postion == 1){
            _datalist = main_datalist;
            ywbm = "待办";
        }else if(tab_postion == 2){
            _datalist = sencond_datalist;
            ywbm = "已办";
        }else if(tab_postion == 3){
            _datalist = third_datalist;
        }else if(tab_postion == 4){
            _datalist = fourth_datalist;
        }
        Custom_taskClickListener listener = new Custom_taskClickListener(this.getActivity(), tableView,mxClass_,ywbm);
        listener.setTable_postion(tab_postion);
        createList(_datalist,contents,tableView,listener);
        // 添加上啦下拉刷新
        ((PullToRefreshLayout) view.findViewById(R.id.refresh_view)).setOnRefreshListener(new UiListTableViewListener(_datalist,recordcount,pageIndex,menu_code,menu_name,method,pars,contents,tableView,listener,tab_postion));
    }

    @Override
    public boolean initData() {
        Bundle arc = this.getArguments();
        if(arc != null){
            System_one so = (System_one)arc.getSerializable("parms");
            if(so == null){
                return false;
            }
            Map<String,Object> resMap = so.getParms();
            tab_postion = Integer.parseInt(resMap.get("index_").toString());
            menu_code = resMap.get("menu_code").toString();
            menu_name = resMap.get("menu_name").toString();
            method = resMap.get("method").toString();
            mapAll = FastJsonUtils.strToMap(resMap.get("mapAll").toString());
            contents = (String[]) resMap.get("contents");
            pars = resMap.get("pars").toString();
            mxClass_ = resMap.get("mxClass_").toString();

            if (tab_postion == 1){
                if(get_catch().get(menu_code+"_maindata")!= null){
                    main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_maindata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"main_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"main_pageIndex").toString())+1;
                }

            }else if(tab_postion == 2){
                if (get_catch().get(menu_code+"_senconddata")!=null){
                    sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code+"_senconddata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code+"sencond_recordcount").toString());
                    pageIndex = Integer.parseInt(get_catch().get(menu_code+"sencond_pageIndex").toString())+1;
                }
            }

            if(tab_postion == 1 &&  (main_datalist == null || main_datalist.size() == 0)){
                recordcount = Integer.parseInt(resMap.get("recordcount").toString());
                pageIndex = 1;
                try {
                    main_datalist = FastJsonUtils.getBeanMapList(resMap.get("main_datalist").toString());
                    _catch.put(menu_code+"main_recordcount",recordcount);
                    _catch.put(menu_code+"main_pageIndex",pageIndex);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tab_postion == 2 && (sencond_datalist == null || sencond_datalist.size() == 0)){

                pars = StringUtils.strTOJsonstr(pars);
                parms = new HashMap<String, Object>();
                Object res = ActivityController.getDataByPost(mContext,menu_code,method,pars);
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
        return  true;
    }
}
