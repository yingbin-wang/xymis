package com.cn.wti.activity.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.page.FormDataSingleton;
import com.cn.wti.util.page.PageDataSingleton;
import com.wticn.wyb.wtiapp.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/9/19.
 */
public class BaseActivity extends Activity{

    private int listview_item_height;
    private int listview_item_textsize;
    public static Map<String,Object> selectMap = new HashMap<String,Object>();
    public static List<String> selectList = new ArrayList<String>();
    public static  List<View> views = new ArrayList<View>();
    private Dialog mDialog;

    private static PageDataSingleton  _catch = PageDataSingleton.getInstance();
    private static FormDataSingleton _data = FormDataSingleton.getInstance();

    /**
     * 参数Map
     */
    public static Map<String,Object> parmsMap = new HashMap<String,Object>();

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        BaseActivity._catch = _catch;
    }

    public static FormDataSingleton get_data() {
        return _data;
    }

    public static void set_data(FormDataSingleton _data) {
        BaseActivity._data = _data;
    }

    public int getListview_item_height() {
        return R.string.listview_editview_height;
    }

    public void setListview_item_height(int listview_item_height) {
        this.listview_item_height = listview_item_height;
    }

    public int getListview_item_textsize() {

        listview_item_textsize = R.string.listview_editview_textsize;
        return listview_item_textsize;
    }

    public void setListview_item_textsize(int listview_item_textsize) {
        this.listview_item_textsize = listview_item_textsize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];
            if ( data.get(key)!= null){
                val = data.get(key).toString();
            }else{
                val = "";
            }
            resList.add(val);
        }

        return  resList;
    }

    /**
     * 带线程 执行网络请求
     * @param service_name
     * @param method_name
     * @param pars
     * @return
     */
    public void exectueThreadreturnObj(final Context mContext, final String service_name, final String method_name, final String pars, final String type, final Map<String,Object> map){

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object res = ActivityController.getDataByPost(service_name,method_name,pars);
                if (res != null && !res.toString().contains("(abcdef)")){
                    execute_BackMethod(res,type,map);
                }else{
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res != null){
                                Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                WeiboDialogUtils.closeDialog(mDialog);
            }
        }).start();
    }

    public Object execute_BackMethod(Object res,String type,Map<String,Object> map){
        if (res.toString().indexOf("{")>=0 && res.toString().indexOf("}")>=0){
            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
            if (resMap.get("state") != null && resMap.get("state").toString().equals("err")){
                return "err"+resMap.get("msg");
            }else if (resMap.get("state") != null && resMap.get("state").toString().equals("success") && resMap.get("data").equals("")){
                return "err执行操作失败";
            }else{
                return resMap.get("data").toString();
            }
        }
        return  "";
    };

    public void sendBackMessage(Context mContext,String message){
        Toast.makeText(mContext,message.replaceAll("err","").replace("<br>",""),Toast.LENGTH_SHORT).show();
    }
}
