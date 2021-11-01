package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.cn.wti.activity.common.BadgeActionProvider;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.avalidations.EditTextValidator;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.DateUtil;
import com.dina.ui.widget.ClickListener;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.UITableMxView;
import com.dina.ui.widget.UITableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public abstract class BaseEdit_ProcessActivity extends BaseEditActivity implements MyAdapter2.IonSlidingViewClickListener{

    private ActionBar actionBar;
    protected boolean[] mCheck;
    protected MyAdapter2 mAdapter1;
    protected String index;
    protected WebView mx1_View,mx2_View,mx3_View,mx4_View,mx5_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        /*actionBar = this.getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setLogo(R.mipmap.navigationbar_back);*/

        mx1_View = (WebView) findViewById(R.id.mx1_webView);
        mx2_View = (WebView) findViewById(R.id.mx2_webView);
        mx3_View = (WebView) findViewById(R.id.mx3_webView);
        mx4_View = (WebView) findViewById(R.id.mx4_webView);
        mx5_View = (WebView) findViewById(R.id.mx5_webView);

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String resStr = initData();

                if (resStr.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!resStr.equals("")){
                                Toast.makeText(mContext,resStr.replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                WeiboDialogUtils.closeDialog(mDialog);
                                return;
                            }
                        }
                    });
                }
            }
        }).start();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //判断 是否 是已办任务
        Intent intent = getIntent();
        String ywbm = intent.getStringExtra("ywbm");
        if(ywbm.equals("已办")){
            return  false;
        }

        menu.addSubMenu("通过");
        menu.addSubMenu("不通过");
        menu.addSubMenu("驳回");
        menu.addSubMenu("委托");
        menu.addSubMenu("附件");
        return super.onCreateOptionsMenu(menu);

    }*/

    /*@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }*/

    public String initData(){return  "";}
    public void createView(){};

    /*public void  addTitle(String title){
        actionBar.setTitle(title);
    }*/

    public void createmx_TaskView(List<Map<String,Object>> _datalist, UITableMxView tableView, String[] contents) {

        Map<String,Object> map = null;
        List<IListItem> basicListItem;
        String res = "",id = "";
       /* Set<String> keys = showMap.keySet();*/
        if (_datalist == null || _datalist.size() == 0){return;}
        for (int i=0,n = _datalist.size();i<n;i++){
            map = _datalist.get(i);
            tableView.addBasicItem("","", StringUtils.returnString(map,contents[0]),StringUtils.returnString(map,contents[1]),
                    StringUtils.returnString(map,contents[2]),StringUtils.returnString(map,contents[3]),
                    StringUtils.returnString(map,contents[4]),StringUtils.returnString(map,contents[5]),
                    StringUtils.returnString(map,contents[6]),StringUtils.returnString(map,contents[7]),
                    R.color.black,true,1);
        }

        tableView.setMxDataList(_datalist);
        tableView.commit(false);
    }

    /**
     * 根据 列数 和 明细格式
     * @param _dataGsList
     * @return
     */
    public String[] createContents(List<Map<String,Object>> _dataGsList){
        String[] contents = new String[8];
        Map<String,Object> map  = null;
        for (int i=0;i<8;i++){
            if (i < _dataGsList.size()){
                map = _dataGsList.get(i);
                contents[i] = map.get("code").toString();
            }else{
                contents[i] = "";
            }
        }
        return  contents;
    }

    /**
     * 从数据 格式 中 返回 数据集
     * @param resMap
     * @return
     */
    public String getMxNames(Map<String,Object> resMap){
        List<String> list = new ArrayList<String>();
        if (resMap != null && resMap.size() >=0){
            Set<String> sets = resMap.keySet();
            for (String name:sets) {
                if (!name.contains("Data") && !name.contains("main")&& !name.contains("gz")){
                    list.add(name);
                }
            }
        }
        String name = "",names = "";
        for (int i=0,n=list.size();i<n;i++){
            name = list.get(i);
            name += ":"+"明细";
            if (i == n - 1){
                names += name;
            }else{
                if (!name.equals("")){
                    names += name+",";
                }
            }
        }
        return  names;
    }

    public void createList(UITableView tableView) {

        Map<String,Object> map = null,dataMap;
        String type = "",name = "",code = "",value = "";
        List<Map<String,Object>> _dataList = null;

        editTextValidator = new EditTextValidator(mContext);

        if (maings_list != null && maings_list.size()>0){
            _dataList = new ArrayList<Map<String,Object>>();
            _dataList.addAll(maings_list);
        }

        for (int i=0,n = maings_list.size();i<n;i++){
            map = _dataList.get(i);
            name = map.get("name").toString();
            code = map.get("code").toString();
            type = map.get("type").toString();

            if ( (map.get("is_select")!= null && !map.get("is_select").toString().equals("")) ||
                 (map.get("is_write")!= null && !map.get("is_write").toString().equals("")) ||
                 (map.get("type")!= null && (map.get("type").toString().equals("date") || map.get("type").toString().equals("datetime"))) ||
                 (map.get("is_visible_phone")!= null && !map.get("is_visible_phone").toString().equals(""))
                ){
                Object test_ob = main_data.get(code);
                if(test_ob != null){
                    value = test_ob.toString();
                }else{
                    value = "";
                }
                tableView.addBasicItem(code,name, value,R.color.black,false,1,type);
            }


        }

        tableView.commit();
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

            if(isDrqf){
                val = data.get(key).toString();
                val = DateUtil.strFomatDate(val);
            }else{
                val = getVal(key,data);
            }

            resList.add(val);
        }

        return  resList;
    }

    public void createmx3View(List<Map<String,Object>> _datalist, UITableMxView tableView,String[] contents) {

        Map<String,Object> map = null;
        if (_datalist == null || _datalist.size() == 0){return;}
        for (int i=0,n = _datalist.size();i<n;i++){
            map = _datalist.get(i);
            List<String> _contentList = createMain(map,contents);
            String[] testContent = _contentList.toArray(new String[_contentList.size()]);
            tableView.addBasicItem("","",new String[]{},testContent,R.color.black,true,1);
        }

        tableView.setMxDataList(_datalist);
        tableView.setContents(contents);
        tableView.commit();
    }

    public String returnString(Map<String,Object> map,String key){
        String res = null;
        if(key.indexOf("：")>=0) {
            String[] _keys = key.split("：");
            if(map.get(_keys[1]) != null){
                res = _keys[0]+"："+getVal(_keys[1],map);
            }
        }else{
            if(map.get(key) != null){
                res = getVal(key,map);
            }
        }
        return  res;
    }

    public String getVal(String key,Map<String,Object>map){return "";};

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onDeleteBtnClilck(View view, int position) {

    }

    @Override
    public void onCuibanBtnClick(View view, int position) {

    }

}
