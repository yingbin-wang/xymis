package com.cn.wti.activity.base.fragment;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;

import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListTableViewListener;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

public class CommonFragment extends BaseFragment_List {

    private List<Map<String,Object>>
            _datalist = null; //主格式
    private Map<String,Object> parms = null;
    int RESULT_OK = 1,REQUEST_CODE =1,start_postion;
    private String parms_="";
    Object res;
    private Dialog mDialog;
    private CustomClickListener listener = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    createList(_datalist,contents,tableView,listener);
                    WeiboDialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };

    @Override
    public void createView(){
        super.createView();
        mContext = this.getActivity();
       listener = new CustomClickListener(this.getActivity(), null, tableView.getIListItem(),mxClass_,REQUEST_CODE,mapAll);
        start_postion = pars.indexOf("}");
        if (tab_postion == 1){
            _datalist = main_datalist;
            parms_ = pars;
        }else if(tab_postion == 2){
            _datalist = sencond_datalist;
            parms_ = pars.substring(0,start_postion)+",cxlx:1,parms:approvalstatus =1 "+"}";
        }else if(tab_postion == 3){
            _datalist = third_datalist;
            parms_ = pars.substring(0,start_postion)+",cxlx:1,parms:estatus=7"+"}";
        }else if(tab_postion == 4){
            _datalist = fourth_datalist;
            parms_ = pars.substring(0,start_postion)+",cxlx:1,parms:estatus=1 and approvalstatus !=1 "+"}";
        }

        /*testService testService = new testService();
        testService.setMenu_code(menu_code);
        testService.set_layout(_layout);
        testService.setView(view);
        testService.createList(mContext,_datalist,contents,tableView,listener);*/
        mDialog = WeiboDialogUtils.createLoadingDialog(this.getActivity(), "加载中...");
        mHandler.sendEmptyMessageDelayed(1,1000);
        //添加 布局文件
        /*createList(_datalist,contents,tableView,listener);*/
        // 添加上啦下拉刷新
        try {
            ((PullToRefreshLayout) view.findViewById(R.id.refresh_view)).setOnRefreshListener(new UiListTableViewListener(this,recordcount,pageIndex,menu_code,menu_name,"list",parms_,tableView,listener,tab_postion));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
