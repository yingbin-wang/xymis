package com.cn.wti.activity.base.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListTableViewListener;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.widget.UIListTableView;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

public class Common_NoTable_Fragment extends Fragment {

    protected  int layout = 0;
    protected  View view;
    protected  UIListTableView tableView;
    private Map<String,Object> resMap,mapAll,parms;

    protected String menu_code,menu_name,mxClass_,pars;
    private String contents[];
    private List<Map<String,Object>> main_datalist;
    protected  int recordcount,pageIndex;

    //得到缓存数据
    protected static PageDataSingleton _catch = PageDataSingleton.getInstance();

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
        initData();
        createView();
        return view;
    }

    public void createView(){
        //列表
        tableView = (UIListTableView) view.findViewById(R.id.tableView);
    }

    //绑定数据
    public void initData() {
        Bundle arc = this.getArguments();
        if(arc != null){
            System_one so = (System_one)arc.getSerializable("parms");
            if(so == null){
                return;
            }
            resMap = so.getParms();
            menu_code = resMap.get("menu_code").toString();
            menu_name = resMap.get("menu_name").toString();
            mapAll = FastJsonUtils.strToMap(resMap.get("mapAll").toString());
            contents = (String[]) resMap.get("contents");
            pars = resMap.get("pars").toString();
            mxClass_ = resMap.get("mxClass_").toString();

            if(get_catch().get(menu_code+"_maindata")!= null) {
                main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                recordcount = Integer.parseInt(get_catch().get(menu_code+"main_recordcount").toString());
                pageIndex = Integer.parseInt(get_catch().get(menu_code+"main_pageIndex").toString())+1;
            }
            parms = (Map<String, Object>) _catch.get(menu_code);
        }
    }
}
