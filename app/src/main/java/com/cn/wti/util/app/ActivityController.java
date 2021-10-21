package com.cn.wti.util.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ObjectArrayCodec;
import com.baidu.mapapi.http.HttpClient;
import com.cn.wti.activity.rwgl.myfile.MyFileActivity;
import com.cn.wti.activity.tab.MyFragmentActivity;
import com.cn.wti.activity.web.FilePreviewActivity;
import com.cn.wti.entity.Sys_user;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.adapter.MyTaskmxAdapter2;
import com.cn.wti.entity.adapter.viewHolder.Edit_ViewHolder;
import com.cn.wti.entity.adapter.viewHolder.Select_ViewHolder;
import com.cn.wti.entity.adapter.viewHolder.Text_ViewHolder;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckDn;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.entity.view.custom.dialog.window.SingleChoicePopWindow;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.dialog.DateTimePickDialogUtil;
import com.cn.wti.util.app.qx.BussinessUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.net.Net;
import com.cn.wti.util.number.FileUtils;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.dina.ui.model.MyTextWatcher;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UIListTableView;
import com.ease.ui.EaseChartActivity;
import com.ease.utils.HuanxinUtils;
import com.hyphenate.easeui.EaseConstant;
import com.wticn.wyb.wtiapp.BuildConfig;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.db.FastJsonUtils;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.UITableMxView;
import com.dina.ui.widget.UITableView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.cn.wti.activity.base.BaseActivity.get_data;
import static com.wticn.wyb.wtiapp.R.id.historymx_View;
import static com.wticn.wyb.wtiapp.R.id.mx5_recyclerView;
import static com.wticn.wyb.wtiapp.R.id.tableView;
import static com.wticn.wyb.wtiapp.R.id.top;

/**
 * Created by wangz on 2016/10/25.
 */
public class ActivityController {

    public static Dialog mDialog;
    private static Context mContext1;
    public  static List<Activity> activities = new ArrayList<Activity>();
    public static Map<String,Object> uploadMap = new HashMap<>();

    static {
        uploadMap.put("filename","filename");
        uploadMap.put("filepath","newfilename");
        uploadMap.put("fileid","id");
    }

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static  void finishAll(){
        for (Activity a:activities) {
            if(!a.isFinishing()){
                a.finish();
            }
        }
    }

    private static Handler messageHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Bundle msgBundle = msg.getData();
            String message = msgBundle.getString("message");
            Toast.makeText(mContext1,message,Toast.LENGTH_SHORT).show();
        }
    };

    public static void showDialog(Context context,String title,String[] items,final View view,List<Map<String,Object>> dataList,Map<String,Object> resMap,Map<String,Object>parMap){
        mContext1 = context;
        final String[] finalItems = items;
        final List<Map<String,Object>> finalDataList = dataList;
        final  Map<String,Object> finalResMap = resMap;
        final  Map<String,Object> finalParMap = parMap;

        new AlertDialog.Builder(context).setTitle(title).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        EditText_custom ed2 = AppUtils.findView_02(view);
                        Map<String,Object> dataMap = finalDataList.get(which);

                        Set<String> sets = finalParMap.keySet();
                        for (String key: sets) {
                            finalResMap.put(key,dataMap.get(finalParMap.get(key)));
                        }
                        ed2.setText(finalResMap.get(ed2.getCode()).toString());

                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    public static boolean execute(Context context,String service,String method,String pars){
        mContext1 = context;
        if (pars.indexOf("\"")<0){
            pars = StringUtils.strTOJsonstr(pars);
        }
        Object res = ActivityController.getDataByPost(context,service,method,pars);
        if (AppUtils.network_state && AppUtils.fwq_state){
            if (res != null && !res.toString().contains("(abcdef)")){
                Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());

                if (resMap == null){
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("message", "操作失败");
                    message.setData(bundle);
                    messageHandler.sendMessage(message);
                    message.what=1;
                    return false;
                }
                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("message", resMap.get("msg").toString());
                    message.setData(bundle);
                    messageHandler.sendMessage(message);
                    message.what=1;
                    return  true;
                }else{
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("message", resMap.get("msg") == null ? "" : resMap.get("msg").toString());
                    message.setData(bundle);
                    messageHandler.sendMessage(message);
                    message.what=1;
                    return  false;
                }
            }
        }
        return  false;
    }

    public static String executeForResult(Context context,String service,String method,String pars){
        mContext1 = context;
        Object res = ActivityController.getDataByPost(context,service,method,pars);
        if (res != null && !res.toString().contains("abcdef")){
            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
            if (resMap == null){
                sendMessage("操作失败");
                return "err";
            }
            if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                sendMessage(resMap.get("msg").toString());
                if (resMap.get("id")!= null){
                    return  resMap.get("id").toString();
                }else{
                    return  "success";
                }

            }else{
                return  "(abcdef)"+resMap.get("msg") == null ? "存在异常请求失败":resMap.get("msg").toString();
            }
        }else{
            return (String) res;
        }
    }

    public static void sendMessage(String msg){
        Message message=new Message();
        Bundle bundle=new Bundle();
        bundle.putString("message", msg);
        message.setData(bundle);
        messageHandler.sendMessage(message);
        message.what=1;
    }

    public static String executeForResult2(Context context,String service,String method,String pars){

        Object res = ActivityController.getDataByPost(context,service,method,pars);
        if (AppUtils.network_state && AppUtils.fwq_state){
            if (res != null && !res.equals("")){
                Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                if (resMap == null){
                    return "err";
                }
                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                    Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                    if (resMap.get("id")!= null){
                        return  resMap.get("id").toString();
                    }else{
                        if ( resMap.get("data") != null){
                            return  resMap.get("data").toString();
                        }else{
                            return  "";
                        }

                    }

                }else{
                    Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                    return  "err";
                }
            }
        }
        return  "";
    }

    public static String executeForResultByThread(Context context,String service,String method,String pars){

        Object res = ActivityController.getDataByPost(context,service,method,pars);
        if (AppUtils.network_state && AppUtils.fwq_state){
            if (res != null && !res.equals("")){
                Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                if (resMap == null){
                    return "err";
                }
                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                    if (resMap.get("id")!= null){
                        return  resMap.get("id").toString();
                    }else{
                        return  resMap.get("msg").toString();
                    }

                }else{
                    return  "err,"+resMap.get("msg").toString();
                }
            }
        }
        return  "";
    }

    /**
     * 弹出选择框 可以带数据
     * @param context
     * @param title  对话框标题
     * @param items  对话框数据
     * @param itemList 对话框数据集合
     * @param viewList 界面 view 集合
     * @param parMap   参数 map
     * @param tableView table对象
     */
    public static void showDialog(Context context, String title, String[] items, List<Map<String,Object>> itemList,
                                  List<IListItem> viewList, Map<String,Object>parMap, UITableView tableView, final int index){

        final String[] finalItems = items;
        final List<IListItem> finalViewList = viewList;
        Map<String,Object> testMap = new HashMap<String, Object>();
        testMap.putAll(parMap);
        testMap.remove("href");
        testMap.remove("parms");
        testMap.remove("col_name");
        /*testMap.remove("action");
        testMap.remove("object");*/
        final  Map<String,Object> finalParMap = testMap;
        final  List<Map<String,Object>> finalItemList = itemList;
        final UITableView finalTableView= tableView;
        final int finalindex = index;

        new AlertDialog.Builder(context).setTitle(title).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BasicItem  basicItem = null;
                        if (finalItemList.size()==0){
                            basicItem = (BasicItem) finalViewList.get(index);
                            basicItem.setSubtitle(finalItems[which]);
                            View v  = finalTableView.getLayoutList(index);
                            finalTableView.setupBasicItemValue(v,basicItem,index);
                        }else{
                            Map<String,Object> map = finalItemList.get(which);
                            if (finalParMap.get("action")!= null && finalParMap.get("action").toString().equals("clear")){
                                UITableView xmTableView = (UITableView)finalParMap.get("object");
                                xmTableView.clear();
                            }

                            Set<String> sets = finalParMap.keySet();
                            for (String key: sets) {
                                if (!key.equals("action") && !key.equals("object")){
                                    basicItem = getItem(finalViewList,key);
                                    if(map.get(finalParMap.get(key).toString()) != null){
                                        basicItem.setSubtitle(map.get(finalParMap.get(key).toString()).toString());
                                    }else{
                                        basicItem.setSubtitle(finalParMap.get(key).toString());
                                    }
                                    View v  = finalTableView.getLayoutList(basicItem.getIndex());
                                    finalTableView.setupBasicItemValue(v,basicItem,basicItem.getIndex());
                                }
                            }
                        }

                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    public static void showDialog2(final Context context, final String title, String[] items, List<Map<String,Object>> itemList,
                                   List<IListItem> viewList, final Map<String,Object>parMap, final UITableView tableView, final int index, final String parms1,
                                   View view, String service_name, String method_name, String pars, String column_name, int recordcount, int pageIndex){

        final String[] finalItems = items;
        final List<IListItem> finalViewList = viewList;
        Map<String,Object> testMap = new HashMap<String, Object>();
        testMap.putAll(parMap);
        testMap.remove("serviceName");
        testMap.remove("href");
        testMap.remove("parms");
        testMap.remove("col_name");
        testMap.remove("limit");
        final  Map<String,Object> finalParMap = testMap;
        Map<String,Object> parmsMap = null;
        final  List<Map<String,Object>> finalItemList = itemList;
        final UITableView finalTableView= tableView;
        final int finalindex = index;
        final String finalparms1 = parms1;

        int end = parms1.indexOf("~");
        String parms1111 = "",testPars="";

        if (end >=0){
            parms1111 = parms1.substring(0,end);
            String testParms = parms1.substring(end+1,parms1.length());
            Map<String,Object> parmsMap1 = FastJsonUtils.strToMap(testParms);
            parmsMap = parmsMap1;
            testPars = pars.substring(0,pars.indexOf("~"));
        }else{
            parms1111 = parms1;
            testPars = pars;
        }

        final Map<String, Object> finalParmsMap = parmsMap;
        final String finalparms1111 = parms1111;
        if (itemList.size() == 0 && items.length>0){
            for(String test:finalItems){
                Map<String,Object> map = new HashMap<>();
                map.put("name",test);
                itemList.add(map);
            }
            column_name = "name";
        }
       /* d*/
        final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(context, view,itemList,service_name,method_name,testPars,column_name,recordcount,pageIndex,title);
        //20171217 wang 添加 特殊视图处理
        if (title.contains("收货地址")){
            mSingleChoicePopWindow.getMSingleChoicAdapter().setLayoutid(R.layout.choice_list_item_vertical);
        }
        mSingleChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int which = mSingleChoicePopWindow.getSelectItem();
                BasicItem  basicItem = null;
                //当列表项 为空 返回 否则 执行选择操作
                if (finalItems.length == 0){
                    Set<String> sets = finalParMap.keySet();
                    sets.remove("object");
                    sets.remove("action");
                    for (String key: sets) {
                        basicItem = getItem(finalViewList,key);
                        basicItem.setSubtitle("");
                        View v1  = finalTableView.getLayoutList(basicItem.getIndex());
                        finalTableView.setupBasicItemValue(v1,basicItem,basicItem.getIndex());
                    }
                    return;
                }

                if (title.equals("下拉选择")){
                    finalItemList.clear();
                }

                if (finalItemList.size()==0){
                    basicItem = (BasicItem) finalViewList.get(index);
                    basicItem.setSubtitle(finalItems[which]);
                    if (basicItem.getDataType().equals("int")){

                        if (basicItem.getColor() != 999){
                            basicItem.setVal(String .valueOf(which+basicItem.getColor()));
                        }else{
                            basicItem.setVal(String .valueOf(which));
                        }
                    }

                    View v1  = finalTableView.getLayoutList(index);
                    finalTableView.setupBasicItemValue(v1,basicItem,index);
                }else{
                    final Map<String,Object> map = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                    if (finalParMap.get("action")!= null && finalParMap.get("action").toString().equals("clear")){
                        UITableMxView xmTableView = (UITableMxView)finalParMap.get("object");
                        xmTableView.clear();
                        if (xmTableView.getMxDataList() != null){
                            xmTableView.getMxDataList().clear();
                        }
                    }

                    final View finalView = mSingleChoicePopWindow.getMParentView();

                    Set<String> sets = finalParMap.keySet();
                    String parms;
                    List<String> sort_list3 = new ArrayList<String>() /*排序集合*/;
                    //更新状态
                    boolean updatestate = false;

                    if(finalParMap.get("sort")!= null){
                        String[] sorts = finalParMap.get("sort").toString().split(",");
                        sort_list3 = Arrays.asList(sorts);
                    }else{
                        sort_list3.addAll(sets) ;
                    }

                    for (String key: sort_list3) {

                        if (key.equals("methodName") || key.equals("sort")){continue;}

                        if (!key.equals("action") && !key.equals("object")){
                            String dj="";
                            if(finalParmsMap !=null && finalParmsMap.get(key)!=null){
                                Map<String,Object> map1 = FastJsonUtils.strToMap(finalParmsMap.get(key).toString());
                                String cs = map1.get("parms").toString(),key1,val,parms11 = "";

                                String  test1="",test2 = "";
                                if(!cs.equals("")){
                                    String[] css = cs.split(","),testKeys;

                                    for (int i=0,n=css.length;i<n;i++){
                                        test1 = css[i] ;
                                        if (test1.indexOf("limit")>=0 || test1.indexOf("start")>=0 || test1.indexOf("pageIndex")>=0){
                                            if (test2.equals("")){
                                                test2 += test1;
                                            }else{
                                                test2 += ","+test1;
                                            }
                                            continue;
                                        }
                                        testKeys = test1.split(":");
                                        key1 = testKeys[0];
                                        val = map.get(testKeys[1]).toString();
                                        if(parms11.equals("")){
                                            parms11 += key1+":"+val;
                                        }else{
                                            parms11 += key1+":"+val+",";
                                        }
                                    }
                                }
                                if (!parms11.equals("")){
                                    parms11 += ","+test2;
                                }else {
                                    parms11 = test2;
                                }

                                if (!finalparms1111.equals("")){
                                    parms = finalparms1111+","+parms11;
                                }else{
                                    parms = parms11;
                                }

                                parms = StringUtils.strTOJsonstr(parms);
                                dj =  ActivityController.getDataByPost(context,map1.get("seviceName").toString(),map1.get("methodName").toString(),parms).toString();
                            }

                            basicItem = getItem(finalViewList,key);
                            if (basicItem == null){
                                Toast.makeText(context,"不存在"+key,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(!dj.equals("")){
                                Map<String,Object> resMap = FastJsonUtils.strToMap(dj),dataMap;
                                if(resMap.get("state").toString().equals("success")){
                                    map.put(key,resMap.get("data"));
                                    List<Map<String,Object>> _dataList =null;
                                    if(resMap.get("data") != null){
                                        if(resMap.get("data") instanceof JSONObject){
                                            dataMap = (Map<String,Object>)resMap.get("data");
                                            if (dataMap.get("member_price")!= null){
                                                dj = dataMap.get("member_price").toString();
                                            }else{
                                                _dataList = (List<Map<String, Object>>) ((Map<String,Object>)resMap.get("data")).get("rows");
                                            }

                                        }else{
                                            _dataList = (List<Map<String, Object>>) resMap.get("data");
                                        }
                                    }
                                }
                            }

                            String val_key="",val="";
                            if (finalParMap.get(key) != null){
                                val_key = finalParMap.get(key).toString();
                            }else{
                                Toast.makeText(context,"不存在"+key,Toast.LENGTH_SHORT).show();
                                return;
                            }

                            /**
                             * 判断 指定 key 值 是否有值
                             * 如果 无 判断是否 是 默认值 不是 取 空字符
                             */
                            if(map.get(finalParMap.get(key).toString()) != null){
                                val = map.get(finalParMap.get(key).toString()).toString();
                                if (!dj.equals("")){
                                    val = dj;
                                }
                            }else{
                                if (finalParMap.get(key).toString().indexOf("_val")>=0 && finalParMap.get(key).toString().indexOf("{")<0){
                                    val = val_key.replace("_val","");
                                }else if(finalParMap.get(key) instanceof  JSONObject){

                                }else{
                                    val = "";
                                }
                            }

                            if(val.equals("{}")){
                                val = "";
                            }

                            if (!updatestate){
                                basicItem.setSubtitle(val);
                                View v1  = finalTableView.getLayoutList(basicItem.getIndex());
                                finalTableView.setupBasicItemValue(v1,basicItem,basicItem.getIndex());
                            }
                         }
                    }


                    //判断 带出动作
                    Object action_ = finalParMap.get("action");
                    if (action_!= null && action_ instanceof JSONObject){
                        Map<String,Object> action_map = (Map<String, Object>) finalParMap.get("action");
                        final Map<String,Object> resMap = (Map<String, Object>) get_data().get("gx");
                        //执行清除动作
                        Object clear_object = action_map.get("clear");
                        if(clear_object != null){

                            if(clear_object instanceof JSONObject){
                                Map<String,Object> clear_map = (Map<String, Object>) clear_object;
                                Set<String> clear_sets =  clear_map.keySet();
                                for (String clear_key:clear_sets) {
                                    //如果是 主数据 更新主数据
                                    if (clear_key.equals("main")){
                                        String clear_str = clear_map.get(clear_key).toString();
                                        if (clear_str!= null && !clear_str.equals("")){
                                            String[] clear_strs = clear_str.split(",");
                                            for (String clear_key1:clear_strs) {
                                                update_colval_Main(context,clear_key1,"");
                                            }
                                        }
                                    }else{
                                        String clear_str = clear_map.get(clear_key).toString();
                                        if (clear_str.equals("all")){
                                            if (!clear_key.equals("")){
                                                String name = (String) resMap.get(clear_key);
                                                int index = Integer.parseInt(name.replace("mx","").replace("_data",""));
                                                MyAdapter2 adapter = null;List<Map<String,Object>> _dataList = null;

                                                try {
                                                    adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(context,"myAdapter"+String.valueOf(index));
                                                    adapter.getDatas().clear();
                                                    adapter.notifyDataSetChanged();

                                                } catch (NoSuchFieldException e) {
                                                    e.printStackTrace();
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //执行关联数据
                        if(action_map.get("gl_data")!= null){

                            final Object gl_object = action_map.get("gl_data");
                            //如果 关联数据 是字符 直接带出 数据中 集合 否则 从URL 返回数据
                            MyAdapter2 adapter = null;List<Map<String,Object>> _dataList = null;
                            if (gl_object instanceof String){
                                String gl_data_name = action_map.get("gl_data").toString(),data_name;
                                String[] gldx_array;
                                if (gl_data_name.indexOf(",")>0){
                                    gldx_array = gl_data_name.split(",");
                                    gl_data_name = gldx_array[0];
                                    data_name = gldx_array[1];
                                }else{
                                    data_name = gl_data_name;
                                }
                                String name = (String) resMap.get(gl_data_name);
                                int index = Integer.parseInt(name.replace("mx","").replace("_data",""));
                                if(map.get(data_name) != null){
                                    try {
                                        adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(context,"myAdapter"+String.valueOf(index));
                                        if (map.get(data_name) instanceof String){
                                            try {
                                                adapter.getDatas().clear();
                                                adapter.getDatas().addAll(FastJsonUtils.getBeanMapList(map.get(data_name).toString()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            adapter.getDatas().clear();
                                            adapter.getDatas().addAll((List<Map<String, Object>>) map.get(data_name));
                                        }
                                        adapter.notifyDataSetChanged();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if (gl_object instanceof JSONObject) {
                                //mDialog = WeiboDialogUtils.createLoadingDialog(context,"加载数据中...");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String,Object> gldata_map = (Map<String, Object>) gl_object;
                                        List<Map<String,Object>>_dataList = null;
                                        Object res = getData4(gldata_map,map);
                                        String gl_data_name = "";
                                        if (res != null && !res.equals("")){
                                            if (res instanceof String){
                                                try {
                                                    String gl_dataStr = gldata_map.get("gl_data").toString();
                                                    String dataList = "";
                                                    if (gl_dataStr.indexOf(",")!= -1){
                                                        String data_name = getDataDetailName(gl_dataStr);
                                                        Map<String,Object>resMap = FastJsonUtils.strToMap(res.toString());
                                                        _dataList = FastJsonUtils.getBeanMapList(resMap.get(data_name).toString());
                                                        gl_data_name = gl_dataStr.substring(0,gl_dataStr.indexOf(","));
                                                    }else{
                                                        _dataList = FastJsonUtils.getBeanMapList(res.toString());
                                                        gl_data_name = gl_dataStr;
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                _dataList = (List<Map<String, Object>>) res;
                                            }

                                            String name = (String) resMap.get(gl_data_name);
                                            int index = Integer.parseInt(name.replace("mx","").replace("_data",""));
                                            try {
                                                final MyAdapter2 adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(context,"myAdapter"+String.valueOf(index));
                                                adapter.getDatas().clear();
                                                adapter.getDatas().addAll(_dataList);
                                                ((Activity)context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });

                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        //WeiboDialogUtils.closeDialog(mDialog);
                                    }
                                }).start();
                            }

                        }else if (action_map.get("change")!= null){
                            /**
                             * change 事件处理
                             */
                            if(action_map != null) {
                                //如果既有change 又有 update 先执行 change 后执行 update 然后执行 销售政策
                                if (action_map.get("change") != null && action_map.get("update") != null){
                                    //执行change 动作
                                    Map<String, Object> change_map = (Map<String, Object>) action_map.get("change");
                                    Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                                    Map<String, Object> salespolicy_map = (Map<String, Object>) action_map.get("salespolicy");
                                    change_map.put("action","update");
                                    change_map.put("update_map",update_map);
                                    change_map.put("salespolicy",salespolicy_map);
                                    change(context,change_map,tableView,"","",null);

                                }else if (action_map.get("change") != null) {
                                    //执行change 动作
                                    Map<String, Object> change_map = (Map<String, Object>) action_map.get("change");
                                    change(context,change_map,tableView,"","",null);
                                }else if (action_map.get("update") != null) {
                                    //执行 update 动作
                                    Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                                    update(context,update_map,tableView,"","",null);
                                    updatestate = true;
                                }

                            }
                        }else if (action_map.get("update")!= null){
                            //执行 update 动作
                            Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                            update(context,update_map,tableView,"","",null);
                        }
                    }
                }
            }
        });
        mSingleChoicePopWindow.show(true);
    }

    private static String getDataDetailName(String gl_data_name){
        String data_name;
        String[] gldx_array;
        if (gl_data_name.indexOf(",")>0){
            gldx_array = gl_data_name.split(",");
            gl_data_name = gldx_array[0];
            data_name = gldx_array[1];
        }else{
            data_name = gl_data_name;
        }

        return data_name;
    }
    //销售政策临时
    private static List<Map<String,Object>> salespolicyList = new ArrayList<Map<String,Object>>();

    private static void showDialogSalespolicy(final Context context, final Map<String, Object> salespolicy_map, final UITableView tableView, final Map<String,Object> dataMap, final MyTextWatcher textWatcher) {
        if (salespolicy_map != null){
            if(dataMap.get("sellpolicysetcombomx")!= null){
                String title = salespolicy_map.get("title").toString();
                //20171219 wang 记录政策ID
                if (dataMap.get("sellpolicysetid")!=null){
                    BussinessUtils.sellpolicysetid = dataMap.get("sellpolicysetid").toString();
                }else{
                    BussinessUtils.sellpolicysetid = "0";
                }

                if (title.indexOf("[")>=0 && title.indexOf("]")>=0){
                    int i = 0;
                    while (title.indexOf("[")>=0 && title.indexOf("]")>=0){
                        if (i>10){break;}
                        int start = title.indexOf("["),
                             end = title.indexOf("]");

                        String code = title.substring(start+1,end);
                        if (code != null && !code.equals("")){
                            code = code.replace("data.","");
                            if (dataMap.get(code)!= null){
                                title = title.replace("[data."+code+"]",dataMap.get(code).toString());
                            }
                        }
                        i++;
                    }
                }

                final List<Map<String,Object>> _dataList = (List<Map<String, Object>>) dataMap.get("sellpolicysetcombomx");
                List<Map<String,Object>> gxdataList = tableView.get_dataList(),
                                         gldataList = tableView.get_gxdataList(),
                                         copy_gldatalist = new ArrayList<Map<String,Object>>();

                //如果 列表存在数据 直接 取列表数据
                if (salespolicyList.size() == 0 && gxdataList != null && gxdataList.size()!=0){
                    salespolicyList.addAll(gxdataList);
                }

                if (gxdataList != null && gxdataList.size() == 0 && gldataList != null && gldataList.size()!=0){
                    salespolicyList.addAll(gldataList);
                }
                List<Map<String,Object>> zpMapList = null;
                if (gxdataList!= null && gldataList != null && gxdataList.size() == 0 && gldataList.size() != 0){
                    zpMapList = FastJsonUtils.ListPdTOListByKeys(gldataList,"dyrowid,mainid".split(","),tableView.getMxIndex()+","+dataMap.get("goodsid").toString());
                }else{
                    zpMapList = FastJsonUtils.ListPdTOListByKeys(gxdataList,"dyrowid,mainid".split(","),tableView.getMxIndex()+","+dataMap.get("goodsid").toString());
                }

                //20171213 wang 修改倍数
                if (zpMapList != null && zpMapList.size()>0 ){
                    if(Double.parseDouble(dataMap.get("quantity").toString()) % Double.parseDouble(dataMap.get("numclaim").toString()) == 0){
                       double bs =  Double.parseDouble(dataMap.get("sl").toString()) / Double.parseDouble(dataMap.get("numclaim").toString());
                        Map<String,Object> zpMap,glMap;
                        for(int i = 0 ; i < zpMapList.size() ; i++){
                            zpMap = zpMapList.get(i);
                            for(int j = 0 ; j < _dataList.size() ; j++){
                                glMap = _dataList.get(j);
                                if(zpMap.get("goodsid").toString().equals(glMap.get("goodsid").toString())){
                                    String rowid = zpMap.get("rowid").toString(),
                                           dyrowid = zpMap.get("dyrowid").toString();
                                    zpMap.putAll(glMap);
                                    zpMap.put("rowid",rowid);
                                    zpMap.put("dyrowid",dyrowid);
                                }
                            }
                            copy_gldatalist.add(zpMap);
                        }
                        //设置赠品 和 更新数据
                        tableView.set_gxdataList(copy_gldatalist);
                        salespolicyList.clear();
                    }
                }else{
                    final String finalTitle = title;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View finalView = null;
                            try {
                                finalView = (View) ReflectHelper.getValueByFieldName(tableView.getContext1(),"main_form");
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            final MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(context,finalView,_dataList,
                                    new boolean[_dataList.size()],"","","",_dataList.size(),1,"goods_name~goods_type_name",null, finalTitle);
                            multiChoicePopWindow.show(true);

                            multiChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean[] items = multiChoicePopWindow.getSelectItem();
                                    if (multiChoicePopWindow.getSlectList().size() > Integer.parseInt(dataMap.get("choose_gift_num").toString())){
                                        sendMessage("根据政策设定,此商品赠品类目不可以超出设定的可选赠品类目数:"+dataMap.get("choose_gift_num").toString());
                                        textWatcher.setJisuan(true);
                                        return;
                                    }
                                    salespolicyList.clear();
                                    if(items != null && items.length >0){
                                        for (int i=0,n=items.length;i<n;i++){
                                            if(items[i]){
                                                Object res = _dataList.get(i);
                                                Map<String,Object>resMap = (Map<String, Object>) res;
                                                resMap.put("dyrowid",tableView.getMxIndex());
                                                salespolicyList.add(resMap);
                                            }
                                        }
                                    }
                                    List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
                                    list.addAll(salespolicyList);
                                    tableView.set_gxdataList(list);
                                    salespolicyList.clear();
                                    //设置 不执行计算动作
                                    textWatcher.setJisuan(false);
                                    multiChoicePopWindow.show(false);
                                }
                            });
                        }
                    });
                }
            }else{
                List<Map<String,Object>> gxdataList = tableView.get_dataList();
                List<Map<String,Object>> zpMapList = FastJsonUtils.ListPdTOListByKeys(gxdataList,"dyrowid,mainid".split(","),tableView.getMxIndex()+","+dataMap.get("goodsid").toString());

                if (zpMapList.size()>0){
                    gxdataList.removeAll(zpMapList);
                    salespolicyList.removeAll(zpMapList);
                    tableView.set_removeList(zpMapList);
                }
                if (textWatcher != null){
                    textWatcher.setJisuan(true);
                }
            }
        }
    }

    public static String update(Context context,Map<String,Object> updateMap, UITableView tableView, String key, String val,MyTextWatcher textWatcher){

       if (updateMap != null) {

           Map<String, Object> column_map = (Map<String, Object>) updateMap.get("column");
           //获取更新集合
           Set<String> columnSets = column_map.keySet();
           List<String> sort_list = new ArrayList<String>() /*排序集合*/,
                   sort_list2 = new ArrayList<String>();
           if (column_map.get("sort") != null) {
               String[] sorts = column_map.get("sort").toString().split(",");
               sort_list = Arrays.asList(sorts);
           } else {
               sort_list.addAll(columnSets);
           }

           //this main
           for (String key1 : sort_list) {
               if (key1.equals("sort")) {
                   continue;
               }
               Map<String, Object> sortMap = (Map<String, Object>) column_map.get(key1);

               Map<String, Object> ms_map;
               Set<String> col_sets = sortMap.keySet();
               String type1 = ""/*计算类型*/, from = ""/**数据源*/, mx_name = ""/*/明细表名*/, mx_col = ""/*明细字段名*/, columns /*参与字段名称*/;

               if (sortMap != null) {
                   if (sortMap.get("sort") != null) {
                       String[] sorts = sortMap.get("sort").toString().split(",");
                       sort_list2 = Arrays.asList(sorts);
                   } else {
                       sort_list2.addAll(sortMap.keySet());
                   }
               }

               for (String key2 : sort_list2) {
                   if (key2.equals("sort")) {
                       continue;
                   }
                   if (sortMap.get(key2) instanceof Map<?, ?>) {
                       ms_map = (Map<String, Object>) sortMap.get(key2);

                       from = ms_map.get("from").toString();
                       if (from.indexOf("[") >= 0 && from.indexOf("]") >= 0) {
                           String res_jg = computeExpression(ms_map, context, tableView);
                           if (key.equals(key2)) {
                               val = res_jg;
                           }
                           /*更新当前视图*/
                           update_colval_Main(context, key2, res_jg);
                       }
                       else if (from.equals("main")) {
                           /*/执行 替换动作*/
                           if (type1.equals("replace") && ms_map.get("value") != null) {
                               String value_str = ms_map.get("value").toString();
                               try {
                                   value_str = replaceExpression(context, value_str);
                               } catch (NoSuchFieldException e) {
                                   e.printStackTrace();
                               } catch (IllegalAccessException e) {
                                   e.printStackTrace();
                               }
                               /*更新当前视图*/
                               update_colval_Main(context, key, value_str);
                           }
                       } else if (from.indexOf(":") >= 0) {
                           String[] froms = from.split(":");
                           mx_name = froms[0];
                           mx_col = froms[1];
                                                        /*/合计操作*/
                           if (type1.equals("sum")) {
                               try {
                                   Calculation_Main(getMxData(context, mx_name), key, mx_col, tableView);
                               } catch (NoSuchFieldException e) {
                                   e.printStackTrace();
                               } catch (IllegalAccessException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
                   }else if (sortMap.get(key2) instanceof String){
                       if (key1.equals("this")){
                           String val_key = sortMap.get(key2).toString();
                           BasicItem update_item = ActivityController.getItem(tableView.getIListItem(),key2);
                           BasicItem val_item = ActivityController.getItem(tableView.getIListItem(),val_key);
                           if (update_item != null && val_item != null){
                               update_item.setSubtitle(String.valueOf(val_item.getSubtitle()));
                               tableView.setupBasicItemValue(tableView.getLayoutList(update_item.getIndex()),update_item,update_item.getIndex());
                           }
                       }
                   }
               }
           }
       }
       return val;
   }

    public static void change(final Context context, final Map<String, Object> change_map, final UITableView tableView, final String key, final String val, final MyTextWatcher textWatcher){
            //判断参数 是对象 还是 字符串
            String parms1 = "",main_parms = "",mx_parms="";
            if (change_map.get("parms") instanceof String){
                parms1 = change_map.get("parms").toString();
            }else if(change_map.get("parms") instanceof JSONObject){
                Map<String,Object> parmsMap = (Map<String, Object>) change_map.get("parms");
                //参数字符串 key：val
                String parms_test;
                if (parmsMap!= null){
                    Set<String> parmsSet = parmsMap.keySet();
                    for (String parm_key :parmsSet){
                        switch (parm_key){
                            case "main":
                                parms_test = parmsMap.get("main").toString();
                                try {
                                    main_parms = (String) ReflectHelper.callMethod2(context,"setDialogParms",new Object[]{parms_test,null},String.class,UITableView.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "this":
                                parms_test = parmsMap.get("this").toString();
                                mx_parms = TableUtils.getParms(parms_test.split(","),tableView);
                                break;
                        }
                    }
                }
            }

            String[] who_parms = parms1.split(","), testKeys;
            String who_parm = "";
                                           /* *
                                             * 取得参数 并 赋值*/
            if (!main_parms.equals("") || !mx_parms.equals("")){
                if (who_parm.equals("")){
                    who_parm+= main_parms+mx_parms;
                }else{
                    who_parm+= ","+main_parms+mx_parms;
                }
            }
            if (who_parm.equals("")){
                who_parm = TableUtils.getParms(who_parms,tableView);

                if (who_parm.indexOf(key)>=0){
                    int start = who_parm.indexOf(",");
                    String who_parm_str = who_parm.substring(0,start);
                    if(who_parm_str.indexOf(":")>=0){
                        String[] who_array =  who_parm_str.split(":");
                        if(who_array.length == 1){
                            who_parm = who_parm.substring(0,start)+val+","+who_parm.substring(start+1);
                        }
                    }
                }
            }

        who_parm = StringUtils.strTOJsonstr(who_parm);
        //取得返回数据
        final String finalWho_parm = who_parm;
        new Thread(new Runnable() {
                @Override
                public void run() {
                    String dj = ActivityController.getDataByPost(context,change_map.get("seviceName").toString(),change_map.get("methodName").toString(), finalWho_parm).toString();
                    if (dj != null && !dj.equals("")){
                        final Map<String,Object> who_map = (Map<String, Object>) change_map.get("who");
                        final Map<String,Object> res_map = FastJsonUtils.strToMap(dj);
                        if(res_map == null){
                            sendMessage(context.getString(R.string.error_invalid_network));
                            return;
                        }
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(res_map.get("state").equals("success")){
                                    if ( res_map.get("data") instanceof JSONObject){
                                        Map<String,Object> data_map = (Map<String, Object>) res_map.get("data");
                                        //更新表单数据
                                        TableUtils.updateBasicItemByMap(data_map,who_map,tableView);

                                        //通知执行update 动作
                                        if (change_map.get("action")!= null && change_map.get("update_map")!= null){
                                            update(context, (Map<String, Object>) change_map.get("update_map"),tableView,key,val,null);
                                        }

                                        //20171212 wang执行计算公式
                                        if (textWatcher!= null){
                                            textWatcher.jisuan(val);
                                        }

                                        //执行销售政策
                                        if (change_map.get("salespolicy") != null){
                                            Map<String, Object> salespolicy_map = (Map<String, Object>) change_map.get("salespolicy");
                                            showDialogSalespolicy(context,salespolicy_map,tableView,data_map,textWatcher);
                                        }

                                    }else{
                                        sendMessage("获取表单数据异常");
                                        return;
                                    }

                                }else{
                                    if (res_map.get("msg") != null){
                                        sendMessage(res_map.get("msg").toString());
                                    }else{
                                        String cxsl = res_map.get("cxsl").toString(),
                                               total = res_map.get("total").toString();
                                        sendMessage("根据目前的销售政策,当前商品的促销数量为："+cxsl+",已经售出:"+total+",此订单完成后将超出促销数量,按原价执行！");
                                    }
                                }
                            }
                        });

                    }
                }
            }).start();

    }

    /**
     * 根据 tableView 封装表达式 进行 计算
     * @param ms_map
     * @param context
     * @param tableView
     * @return
     */
    public static String  computeExpression(Map<String,Object> ms_map,Context context,UITableView tableView){
        String from = ms_map.get("from").toString();
        String expression_ = "",type1="",mx_name="",mx_col="",val="",res_jg="";
        boolean isDate = false;

        if (from.indexOf("[") >= 0 && from.indexOf("]") >= 0) {
            int i = 0;
            while (from.indexOf("]") >= 0) {

                if (i > 10) {
                    break;
                }

                int start = from.indexOf("[");
                int end = from.indexOf("]");
                String val1 = from.substring(start + 1, end);
                if (ms_map.get(val1) != null) {
                    Object col_1 = ms_map.get(val1);
                    if (col_1 instanceof String) {
                        String test_col = col_1.toString();
                        if (test_col.indexOf(":") >= 0) {
                            String[] mhs = test_col.split(":");
                            if (mhs[0].equals("main")) {
                                String val_1 = get_colval_Main(context, mhs[1], (UITableView) get_data().get("mainView")).toString();
                                if (DateUtil.isDate(val_1)) {
                                    val_1 = DateUtil.date2TimeStamp(val_1, "yyyy-MM-dd");
                                    isDate = true;
                                }
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            } else if (mhs[0].equals("this")) {
                                String val_1 = get_colval_Main(context, mhs[1], tableView).toString();
                                if (DateUtil.isDate(val_1)) {
                                    val_1 = DateUtil.date2TimeStamp(val_1, "yyyy-MM-dd");
                                    isDate = true;
                                }
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            }
                        }
                    } else if (col_1 instanceof Map<?, ?>) {
                        Map<String, Object> colMap = (Map<String, Object>) col_1;
                        type1 = colMap.get("type").toString();
                        String testFrom = colMap.get("from").toString();
                        String[] froms = testFrom.split(":");
                        mx_name = froms[0];
                        mx_col = froms[1];
                                       /*合计操作*/
                        if (type1.equals("sum")) {
                            try {
                                String val_1 = Calculation_Val(getMxData(context, mx_name), mx_col);
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "不存在表达式：【" + val1 + "】", Toast.LENGTH_SHORT).show();
                }

                i++;
            }
            //执行表达式
            String precision = "";
            if (ms_map.get("precision") != null) {
                precision = ms_map.get("precision").toString();
            } else {
                precision = "2";
            }
            try{
                res_jg = SizheTool.eval2(from, isDate,precision).toString();
            }catch (Exception e){
                sendMessage("表达式异常！");
                res_jg = "";
            }

        }

       return  res_jg;
    }

    public static String  computeExpressionByMap(Map<String,Object> ms_map,Context context,Map<String,Object> valMap){
        String from = ms_map.get("from").toString();
        String expression_ = "",type1="",mx_name="",mx_col="",val="",res_jg="";
        boolean isDate = false;

        if (from.indexOf("[") >= 0 && from.indexOf("]") >= 0) {
            int i = 0;
            while (from.indexOf("]") >= 0) {

                if (i > 10) {
                    break;
                }

                int start = from.indexOf("[");
                int end = from.indexOf("]");
                String val1 = from.substring(start + 1, end);
                if (ms_map.get(val1) != null) {
                    Object col_1 = ms_map.get(val1);
                    if (col_1 instanceof String) {
                        String test_col = col_1.toString();
                        if (test_col.indexOf(":") >= 0) {
                            String[] mhs = test_col.split(":");
                            if (mhs[0].equals("main")) {
                                UITableView  tableView= null;
                                if (get_data().get("mainView")== null){
                                    try {
                                        tableView = (UITableView) ReflectHelper.getValueByFieldName(context,"tableView");
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    tableView = (UITableView) get_data().get("mainView");
                                }

                                String val_1 = get_colval_Main(context, mhs[1], tableView).toString();
                                if (DateUtil.isDate(val_1)) {
                                    val_1 = DateUtil.date2TimeStamp(val_1, "yyyy-MM-dd");
                                    isDate = true;
                                }
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            } else if (mhs[0].equals("this")) {
                                String val_1 = getcolvalByMap(context, mhs[1], valMap).toString();
                                if (DateUtil.isDate(val_1)) {
                                    val_1 = DateUtil.date2TimeStamp(val_1, "yyyy-MM-dd");
                                    isDate = true;
                                }
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            }
                        }
                    } else if (col_1 instanceof Map<?, ?>) {
                        Map<String, Object> colMap = (Map<String, Object>) col_1;
                        type1 = colMap.get("type").toString();
                        String testFrom = colMap.get("from").toString();
                        String[] froms = testFrom.split(":");
                        mx_name = froms[0];
                        mx_col = froms[1];
                                       /*合计操作*/
                        if (type1.equals("sum")) {
                            try {
                                String val_1 = Calculation_Val(getMxData(context, mx_name), mx_col);
                                from = from.replace("[" + val1 + "]", String.valueOf(val_1));
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "不存在表达式：【" + val1 + "】", Toast.LENGTH_SHORT).show();
                }

                i++;
            }
            //执行表达式
            String precision = "";
            if (ms_map.get("precision") != null) {
                precision = ms_map.get("precision").toString();
            } else {
                precision = "2";
            }
            try{
                res_jg = SizheTool.eval2(from, isDate,precision).toString();
            }catch (Exception e){
                res_jg = "";
            }

        }

        return  res_jg;
    }

    /**
     * 得到合计值
     * @param mx1_data
     * @param add_col_name
     * @return
     */
    public static String Calculation_Val(List<Map<String, Object>> mx1_data, String add_col_name){
        Map<String,Object> map;
        double je = 0,val = 0;
        for (int i=0,n=mx1_data.size();i<n;i++){
            map = mx1_data.get(i);
            if (map.get(add_col_name) != null){
                if (map.get(add_col_name).equals("")){
                    val = 0;
                }else{
                    val = Double.parseDouble(map.get(add_col_name).toString());
                }
                je +=val ;
            }else{
                je += 0;
            }

        }
        return String.valueOf(je);
    }

    /**
     * 计算主数据列
     * @param mx1_data
     * @param update_col_name
     * @param add_col_name
     * @param tableView
     */
    public static void Calculation_Main(List<Map<String, Object>> mx1_data, String update_col_name, String add_col_name, UITableView tableView){
        Map<String,Object> map;
        double je = 0;
        for (int i=0,n=mx1_data.size();i<n;i++){
            map = mx1_data.get(i);
            je += Double.parseDouble(map.get(add_col_name).toString());
        }
        BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
        item.setSubtitle(String.valueOf(je));
        tableView.setupBasicItemValue(tableView.getLayoutList(item.getIndex()),item,item.getIndex());
    }

    /**
     * 通过名字返回 数据
     * @param name
     * @return
     */
    public static List<Map<String,Object>> getMxData(Object service, String name) throws NoSuchFieldException, IllegalAccessException {
        Map<String,Object> resMap = (Map<String, Object>) get_data().get("gx");
        String mx_name = (String) resMap.get(name);
        List<Map<String,Object>> _list = null;
        try{
            _list = (List<Map<String,Object>>)ReflectHelper.getValueByFieldName(service,mx_name);
        }catch (Exception e){
            e.printStackTrace();
        }
        return _list;
    }

    public static String getMxName(String name){
        Map<String,Object> resMap = (Map<String, Object>) get_data().get("gx");
        return (String) resMap.get(name);
    }

    public static void addMxTableView(UITableMxView uiTableMxView){
        if(uiTableMxView.getMxDataList() != null && uiTableMxView.getMxDataList().size()>0){
            uiTableMxView.clear();
            List<Map<String,Object>> _dataList =  uiTableMxView.getMxDataList();
            String[] contents = uiTableMxView.getContents();

            createmx3View(_dataList,uiTableMxView,contents);
        }
    }

    public static void createmx3View(List<Map<String,Object>> _datalist, UITableMxView tableView,String[] contents) {

        Map<String,Object> map = null;
        List<IListItem> basicListItem;
        String res = "",id = "";
       /* Set<String> keys = showMap.keySet();*/
        if (_datalist == null || _datalist.size() == 0){return;}
        for (int i=0,n = _datalist.size();i<n;i++){
            map = _datalist.get(i);
            tableView.addBasicItem("","",StringUtils.returnString(map,contents[0]),StringUtils.returnString(map,contents[1]),
                    StringUtils.returnString(map,contents[2]),StringUtils.returnString(map,contents[3]),
                    StringUtils.returnString(map,contents[4]),StringUtils.returnString(map,contents[5]),
                    StringUtils.returnString(map,contents[6]),StringUtils.returnString(map,contents[7]),
                    R.color.black,true,1);
        }
        tableView.commit();
    }


    public static BasicItem getItem(List<IListItem> viewsList,String key){
        BasicItem item = null;
        for (int i=0,n=viewsList.size();i<n;i++){
            item = (BasicItem)viewsList.get(i);
            if(item.getCode().equals(key.replaceAll(" ",""))){
                item.setIndex(i);
                return item;
            }
        }
        return  null;
    }

    public static Object getDataByPost(String service_name,String method_name,String pars){

        Object res = "";

        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getPostState(res.toString())==0){
            return res;
        }else{
            return "(abcdef)"+res.toString();
        }
    }

    /**
     * 从服务器得到数据
     * @param service_name
     * @param method_name
     * @param pars
     * @return
     */
    public static Object getDataByPost(Context mContext,String service_name,String method_name,String pars){
        mContext1 = mContext;
        Object res = "";
        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getPostState(res.toString())==0){
            return res;
        }else{
            return "(abcdef)"+res.toString();
        }
    }

    public static Object getData4ByPost(String service_name,String method_name,String pars){
        String res = null;
        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
            if (getPostState(res.toString())  == 0) {
                Object data;
                List<Map<String, Object>> dataList;
                Map<String, Object> resMap = FastJsonUtils.strToMap(res);
                if(resMap == null){
                    return null;
                }
                if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                    data = resMap.get("data");
                    if (data instanceof JSONArray) {
                        dataList = (List<Map<String, Object>>) data;
                    } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                        Map<String,Object> resData = (Map<String, Object>) data;

                        if (resData.get("list")!= null && resData.get("columns")!= null){
                            if (resData.get("list")!= null && resData.get("list") instanceof String){
                                resData.put("list",FastJsonUtils.strToListMap(resData.get("list").toString()));
                            }
                            if (resData.get("columns")!= null && resData.get("columns") instanceof String){
                                resData.put("columns",FastJsonUtils.strToListMap(resData.get("columns").toString()));
                            }
                            return resData;
                        }else{
                            return (Map<String, Object>) data;
                        }
                    } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                    } else if(data instanceof String && ((String) data).indexOf("{") != -1){
                        return FastJsonUtils.strToJson(data.toString());
                    }else if (data instanceof String){
                        return data;
                    }else {
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                    }
                    return dataList;
                }else if(resMap.get("list")!= null){
                    return  resMap;
                }else if (resMap.get("state")!=null && resMap.get("state").toString().equals("err")){
                    return "(abcdef)"+resMap.get("msg");
                }
            }else{
                return  HttpClientUtils.backMessage(getPostState(res.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getData2ByPost(Context mContext,String service_name,String method_name,String pars){
        mContext1 = mContext;
        String res = null;
        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
            if (getPostState(res) == 0) {
                Object data;
                List<Map<String, Object>> dataList;
                Map<String, Object> resMap = FastJsonUtils.strToMap(res);
                if(resMap == null){
                    return null;
                }
                if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                    data = resMap.get("data");
                    if (data instanceof JSONArray) {
                        dataList = (List<Map<String, Object>>) data;
                    } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                        return (Map<String, Object>) data;
                    } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                    } else if(data instanceof String){
                        return data;
                    }else {
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                    }
                    return dataList;
                }else if(resMap.get("list")!= null){
                    return  resMap;
                }else{
                    return  "(abcdef)"+resMap.get("msg").toString();
                }
            }else{
                return "(abcdef)"+mContext.getString(R.string.error_invalid_network);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getDataObjectByPost(Context mContext,String service_name,String method_name,String pars){
        mContext1 = mContext;
        String res = null;
        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getPostState(res) == 0) {
            Object data;
            List<Map<String, Object>> dataList;
            Map<String, Object> resMap = FastJsonUtils.strToMap(res);
            if(resMap == null){
                return null;
            }
            if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                data = resMap.get("data");
                if (data instanceof JSONArray) {
                    dataList = (List<Map<String, Object>>) data;
                } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                    return (Map<String, Object>) data;
                } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                    dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                } else if(data instanceof String){
                    return data;
                }else {
                    dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                }
                return dataList;
            }else if(resMap.get("list")!= null){
                return  resMap;
            }else{
                return  resMap.get("msg").toString();
            }
        }else{
            if (mContext != null){
                return  "(abcdef)"+mContext.getString(R.string.error_invalid_network);
            }
        }
        return null;
    }

    /**
     *返回记录数 与 记录
     * @param service_name
     * @param method_name
     * @param pars
     * @return
     */
    public static Object getData3ByPost(String service_name,String method_name,String pars){

        String res = null;
        try {
            res = HttpClientUtils.exectePost(service_name,method_name,pars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getPostState(res) == 0) {
            Object data;
            List<Map<String, Object>> dataList;
            Map<String, Object> resMap = FastJsonUtils.strToMap(res);
            if(resMap == null){
                return null;
            }
            if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                data = resMap.get("data");
                return data;
            }else if(resMap.get("list")!= null){
                return  resMap;
            }

        }else{
            if (getPostState(res)==1){
                return "(abcdef)加载数据失败！请检查网路";
            }
        }
        return null;
    }

    public static Object getObjectData2ByPost(Context mContext,String service_name,String method_name,String pars){
        mContext1 = mContext;
        boolean isValiad = AppUtils.isNetworkAvailable((Activity) mContext);

        if (isValiad){
            Object res = null;
            try {
                res = HttpClientUtils.exectePost(service_name,method_name,pars);
                if (!res.equals("")) {
                    Object data;
                    List<Map<String, Object>> dataList;
                    JSONObject resMap  = FastJsonUtils.strToJson(res.toString());
                    if(resMap == null){
                        return null;
                    }
                    if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                        data = resMap.get("data");
                        if (data instanceof JSONArray) {
                            dataList = (List<Map<String, Object>>) data;
                        } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                            return (Map<String, Object>) data;
                        } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                            dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                        } else if(data instanceof String){
                            return data;
                        }else {
                            dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                        }
                        return dataList;
                    }else if(resMap.get("list")!= null){
                        return  resMap;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            mContext.getString(R.string.error_invalid_network);
        }

        return null;
    }

    /**
     * 从服务器 得到数据
     * @param serivce_name service 名称
     * @param selectMap 缓存数据
     * @param key        缓存Key
     * @param val_key   数值 Key
     * @param pagesize  分页 行数
     * @return
     */

    public  static String[] getDialogDataByPost(String serivce_name,String method_name,Map<String,Object>selectMap,String key,String val_key,String pagesize,String paramters){
        String[] items = null;
        String parms1 = "";
        if (paramters.indexOf("~")>=0){
            parms1 = paramters.substring(0,paramters.indexOf("~"));
        }else {
            parms1 = paramters;
        }
        parms1 = StringUtils.strTOJsonstr(parms1);

        Object res = null;
        try {
            res = HttpClientUtils.exectePost(serivce_name,method_name,parms1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object data;
        if (res != null && getPostState(res.toString()) == 0){
            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
            List<Map<String,Object>> dataList;
            if(resMap != null && resMap.get("state").toString().equals("success")){
                data = resMap.get("data");
                if(data instanceof JSONArray){
                    dataList = (List<Map<String,Object>>)data;
                }else{
                    Map<String,Object> dataMap = (Map<String,Object>)resMap.get("data");
                    dataList = (List<Map<String,Object>>) (dataMap.get("rows"));
                    selectMap.put(key+"_results",dataMap.get("results"));
                }
                selectMap.put(key,dataList);
                items = FastJsonUtils.ListMapToListStr((List<Map<String,Object>>) selectMap.get(key),val_key);
            }else{
                items = new String[]{};
            }
        }
        return  items;
    }

    /**
     * 封装对话框数据
     * @param serivce_name
     * @param method_name
     * @param selectMap
     * @param key
     * @param val_key
     * @param pagesize
     * @param paramters
     * @return
     */
    public  static List<String> getDialogData_listByPost(String serivce_name,String method_name,Map<String,Object>selectMap,String key,String val_key,String pagesize,String paramters){
        List<String> items = null;

        String parms1 = "";
        if (paramters.indexOf("~")>=0){
            parms1 = paramters.substring(0,paramters.indexOf("~"));
        }else {
            parms1 = paramters;
        }
        parms1 = StringUtils.strTOJsonstr(parms1);

        Object res = null;
        try {
            res = HttpClientUtils.exectePost(serivce_name,method_name,parms1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object data;
        if (res != null){
            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
            List<Map<String,Object>> dataList;
            if (resMap == null){return new ArrayList<>();}
            if(resMap.get("state").toString().equals("success")){
                data = resMap.get("data");
                if(data instanceof JSONArray){
                    dataList = (List<Map<String,Object>>)data;
                }else{
                    Map<String,Object> dataMap = (Map<String,Object>)resMap.get("data");
                    dataList = (List<Map<String,Object>>) (dataMap.get("rows"));
                    selectMap.put(key+"_results",dataMap.get("results"));
                }
                selectMap.put(key,dataList);
                items = FastJsonUtils.MapToListByKey((List<Map<String,Object>>) selectMap.get(key),val_key);
            }
        }
        /* }*/
        return  items;
    }

    /**
     * 修改 listview 对应的 adpater数据
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param dataMap
     * @param views
     */
    public  static void edit_listView_MapData2(AbsListView view, int firstVisibleItem, int visibleItemCount,Map<String,Object>dataMap,List<View> views){
        Map<String,Object> map = null;
        View v = null;
        for (int i = 0,n = view.getChildCount();i< n;i++){
            v = view.getChildAt(i);
            Object viewHolder = v.getTag();
            map = (Map<String,Object>)view.getItemAtPosition(firstVisibleItem+i);

            View v1 = null;
            if (viewHolder instanceof Edit_ViewHolder){
                Edit_ViewHolder holder = (Edit_ViewHolder) viewHolder;
                v1 = holder.content;
                EditText_custom tv1 = (EditText_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());

            }else if(viewHolder instanceof Text_ViewHolder){
                Text_ViewHolder holder = (Text_ViewHolder) viewHolder;
                v1 = holder.content;
                TextView_custom tv1 = (TextView_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());
            }else if(viewHolder instanceof Select_ViewHolder){
                Select_ViewHolder holder = (Select_ViewHolder) viewHolder;
                v1 = holder.content;
                EditText_custom tv1 = (EditText_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());
            }

            if (v1 != null && !views.contains(v1)){
                views.add(v1);
            }
        }
    }

    public  static void edit_listView_MapData1(AbsListView view,Map<String,Object>dataMap,List<View> views){
        Map<String,Object> map = null;
        View v = null;
        for (int i = 0,n = view.getChildCount();i< n;i++){
            v = view.getChildAt(i);
            Object viewHolder = v.getTag();
            map = (Map<String,Object>)view.getItemAtPosition(i);

            View v1 = null;
            if (viewHolder instanceof Edit_ViewHolder){
                Edit_ViewHolder holder = (Edit_ViewHolder) viewHolder;
                v1 = holder.content;
                EditText_custom tv1 = (EditText_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());

            }else if(viewHolder instanceof Text_ViewHolder){
                Text_ViewHolder holder = (Text_ViewHolder) viewHolder;
                v1 = holder.content;
                TextView_custom tv1 = (TextView_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());
            }else if(viewHolder instanceof Select_ViewHolder){
                Select_ViewHolder holder = (Select_ViewHolder) viewHolder;
                v1 = holder.content;
                EditText_custom tv1 = (EditText_custom) v1;
                tv1.setCode(map.get("code").toString());
                dataMap.put(tv1.getCode(),tv1.getText());
            }

            if (v1 != null && !views.contains(v1)){
                views.add(v1);
            }
        }
    }

    /**
     * 根据 数据与 数据格式 创建列表数据
     * @param gsListMap
     * @param dataMap
     * @param layout
     */
    public static void createlist_Tv(Context context,List<Map<String,Object>> gsListMap,Map<String,Object> dataMap,LinearLayout layout){

        boolean isvisible = false;
        int dq_item_num = 0;
        Map<String,Object> gsMap = null;

        LinearLayout linearLayout = null;
        //设置LinearLayout属性(宽和高)
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        //设置textview垂直居中
        tvParams.gravity = Gravity.CENTER_VERTICAL;
        tvParams.weight = 1;

        for (int i = 0,n = gsListMap.size();i<n;i++){
            gsMap = gsListMap.get(i);
            isvisible = false;
            if(gsMap.get("is_select") != null && !gsMap.get("is_select").toString().equals("")){
                isvisible = true;
            }else if(gsMap.get("type")!= null && gsMap.get("type").toString().equals("date")){
                isvisible = true;
            }else if(gsMap.get("is_write") != null &&!gsMap.get("is_write").toString().equals("")){
                isvisible = true;
            }else if(gsMap.get("is_visible") != null&&!gsMap.get("is_visible").toString().equals("")){
                isvisible = true;
            }

            if (isvisible){
                TextView_custom tv1 = null;
                dq_item_num ++ ;

                switch (dq_item_num){
                    case 1:
                        linearLayout = new LinearLayout(context);
                        //将以上的属性赋给LinearLayout
                        linearLayout.setLayoutParams(layoutParams);
                        tv1 = new TextView_custom(context);
                        tv1.setLayoutParams(tvParams);
                        break;
                    case 2:
                        tv1 = new TextView_custom(context);
                        tv1.setLayoutParams(tvParams);
                        break;
                    case 3:
                        tv1 = new TextView_custom(context);
                        tv1.setLayoutParams(tvParams);
                        dq_item_num = 0;
                        break;
                    default:
                        break;
                }

                if(dataMap.get(gsMap.get("code").toString())!= null){
                    tv1.setText(dataMap.get(gsMap.get("code").toString()).toString());
                }else{
                    tv1.setText("");
                }

                tv1.setCode(gsMap.get("code").toString());
                linearLayout.addView(tv1);
                if(dq_item_num == 0){
                    layout.addView(linearLayout);
                }
            }
        }
    }

    /**
     * 弹出选择框 带 查询框 可以带数据
     * @param context
     * @param title  对话框标题
     * @param items  对话框数据
     * @param itemList 对话框数据集合
     * @param viewList 界面 view 集合
     * @param parMap   参数 map
     * @param tableView table对象
     * @param parms1 带查询条件 的选择框
     */
    public static void showTaskDialog2(final Context context, String title, String[] items, List<Map<String,Object>> itemList,
                                   List<IListItem> viewList, Map<String,Object>parMap, UITableView tableView, final int index, final String parms1,
                                   View view,String service_name,String method_name,String pars,String column_name){

        final String[] finalItems = items;
        final List<IListItem> finalViewList = viewList;
        Map<String,Object> testMap = new HashMap<String, Object>();
        testMap.putAll(parMap);
        testMap.remove("href");
        testMap.remove("parms");
        testMap.remove("col_name");
        /*testMap.remove("action");
        testMap.remove("object");*/
        final  Map<String,Object> finalParMap = testMap;
        Map<String,Object> parmsMap = null;
        final  List<Map<String,Object>> finalItemList = itemList;
        final UITableView finalTableView= tableView;
        final int finalindex = index;
        final String finalparms1 = parms1;
        int end = parms1.indexOf("~");
        String parms1111 = "";

        if (end >=0){
            parms1111 = parms1.substring(0,end);
            String testParms = parms1.substring(end+1,parms1.length());
            Map<String,Object> parmsMap1 = FastJsonUtils.strToMap(testParms);
            parmsMap = parmsMap1;
        }else{
            parms1111 = parms1;
        }

        final Map<String, Object> finalParmsMap = parmsMap;
        final String finalparms1111 = parms1111;
        List list = new ArrayList(Arrays.asList(finalItems));
        /*final MultiChoicePopWindow multiChoicePopWindow = new MultiChoicePopWindow(context, view,itemList,list,null,service_name,method_name,pars,column_name);
        multiChoicePopWindow.setTitle(title);
        multiChoicePopWindow.show(true);*/

    }

    /**
     * 验证明细中 是否存在重复数据
     * @param mx1_data
     * @param keys
     * @return
     */
    public static boolean  checkMxIsRepetition(List<Map<String, Object>> mx1_data, String[] keys) {

        List<String> resList = new ArrayList<String>();
        if (mx1_data!= null && mx1_data.size() == 0){ return  false;}

        for (Map<String,Object> map:mx1_data) {
            int i = 0;
            String res="";
            for (String key:keys) {
                if(res.equals("")){
                    res += map.get(key);
                }else{
                    res += "~"+map.get(key);
                }
                if (keys.length-1 == i) {

                    if (resList.contains(res)){
                        return  false;
                    }else{
                        resList.add(res);
                    }
                }
                i++;
            }

        }

        return  true;
    }

    /**
     * 验证明细中 是否存在重复数据
     * @param mx1_data
     * @param keys
     * @return
     */
    public static boolean  checkMxIsRepetition(List<Map<String, Object>> mx1_data,Map<String,Object> mainMap, String[] keys,String ywlx) {

        List<String> resList = new ArrayList<String>();
        if (mx1_data!= null && mx1_data.size() == 0){ return  false;}

        int count = 0;
        String res="";
        for (Map<String,Object> map:mx1_data) {
            int i = 0;
            res = "";
            for (String key:keys) {
                if(res.equals("")){
                    res += map.get(key);
                }else{
                    res += "~"+map.get(key);
                }

                if (keys.length-1 == i) {
                    if (!resList.contains(res)){
                        resList.add(res);
                    }
                }
                i++;
            }
        }

        res = "";
        for (String key:keys) {
            if(res.equals("")){
                res += mainMap.get(key);
            }else{
                res += "~"+mainMap.get(key);
            }
        }

        for (String test:resList){
            if (ywlx.equals("add")){
                if (test.equals(res)){return true;}else{continue;}
            }else{
                count++;
            }
        }

        if(ywlx.equals("edit")){
            if (count > 1){return  true;}else {return  false;}
        }

        return  false;
    }


    /*将隐式启动转换为显示启动*/
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * 更新明细数据
     * @param mContext
     * @param data_index
     * @param gs_map
     * @param type
     * @param service
     * @param dataMap
     * @param index
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void updateMx(Context mContext,int data_index,Map<String,Object> gs_map,String type,String package_name,Object service,Map<String,Object>dataMap,int index) throws NoSuchFieldException, IllegalAccessException {
        mContext1 = mContext;
        MyAdapter2 adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(service,"myAdapter"+data_index);

        if(type.equals("add")){
            adapter.getDatas().add(index,dataMap);
        }else{
            adapter.getDatas().remove(index);
            adapter.getDatas().add(index,dataMap);
        }
        adapter.notifyDataSetChanged();
    }

    public static void updateMx(Context mContext,int data_index,Map<String,Object> gs_map,String type,String package_name,Object service,Map<String,Object>dataMap,int index,List<Map<String,Object>> gldata,List<Map<String,Object>> mxdata,List<Map<String,Object>> removeList) throws NoSuchFieldException, IllegalAccessException {
        mContext1 = mContext;
        MyAdapter2 adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(service,"myAdapter"+data_index);

        if(type.equals("add")){
            adapter.getDatas().add(index,dataMap);
            if (gldata != null){
                adapter.getDatas().addAll(gldata);
            }
        }else{
            adapter.getDatas().remove(index);
            adapter.getDatas().add(index,dataMap);
            if (removeList!= null){
                adapter.getDatas().removeAll(removeList);
            }
            if (gldata != null){
                Map<String,Object> resMap = null;
                for (Map<String,Object> glMap:gldata) {
                    resMap = FastJsonUtils.findMapToListByKey(adapter.getDatas(),"rowid",glMap.get("rowid").toString());
                    if (resMap != null){
                        int index1 = adapter.getDatas().indexOf(resMap);
                        adapter.getDatas().remove(index1);
                        adapter.getDatas().add(index1,glMap);
                    }else{
                        adapter.getDatas().add(glMap);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 根据add 规则 更新 主数据表 数据
     * @param _allMap 规则
     * @param main_data 主数据
     */
    public static void updateAddMainMap(Map<String,Object> _allMap,Map<String,Object> main_data){
        //判断是否 存在 新增调数据
        if (_allMap != null && _allMap.get("add") != null){
            Map<String,Object> add_map = (Map<String, Object>) _allMap.get("add");
            Set<String> addsets = add_map.keySet();

            String seviceName,methodName,parms,_parms="";
            String[] parm_array,key_array;

            for (String addkey:addsets) {
                Object o = add_map.get(addkey);
                if (o instanceof JSONObject){
                    Map<String,Object> get_map = (Map<String, Object>) o;
                    for (String get_key:get_map.keySet()) {
                        Map<String,Object> href_map = (Map<String, Object>) get_map.get(get_key);
                        seviceName = href_map.get("seviceName").toString();
                        methodName = href_map.get("methodName").toString();
                        parms = href_map.get("parms").toString();
                        if(parms != null && !parms.equals("")){
                            parm_array = parms.split(",");
                            for (String parm:parm_array) {
                                if (parm.indexOf(":")>=0){
                                    key_array = parm.split(":");
                                    if(key_array[1].equals("_zydnId")){
                                        if (_parms.equals("")){
                                            _parms = key_array[0]+":"+AppUtils.user.get_zydnId();
                                        }else{
                                            _parms += ","+key_array[0]+":"+AppUtils.user.get_zydnId();
                                        }
                                    }else {
                                        if (_parms.equals("")){
                                            _parms = key_array[0]+":"+main_data.get(key_array[1]);
                                        }else{
                                            _parms += ","+key_array[0]+":"+main_data.get(key_array[1]);
                                        }
                                    }
                                }
                            }
                        }

                        //获取数据
                        String test_parms = StringUtils.strTOJsonstr(_parms);
                        Object res = ActivityController.getData4ByPost(seviceName,methodName,test_parms);
                        if(res instanceof String){
                            main_data.put(get_key,res);
                        }
                    }

                }
            }
        }
    }

    //隐藏添加功能
    public static boolean setVisible_addbutton(Map<String,Object> res_map,View mx1_add) {
        boolean state = true;

        if (res_map.get("isEdit")!= null){
            int isEdit = (int) res_map.get("isEdit");
            if (isEdit == 0 || isEdit ==1){
                state = true;
            }else{
                state = false;
            }
        }

        if (res_map.get("action") != null) {
            if (state){
                Map<String, Object> action_map = (Map<String, Object>) res_map.get("action");
                if (action_map.get("add") != null) {
                    Object add_res = action_map.get("add");
                    if (add_res instanceof String) {
                        if (add_res.toString().equals("false")) {
                            mx1_add.setVisibility(View.GONE);
                            state = false;
                        }
                    }
                }
            }
        }
        return  state;
    }

    /**
     * 从 href 中获取数据
     * @param href_map
     * @param tableView
     * @return
     */
  /*  public static Object getData3(Map<String, Object> href_map, UITableView tableView){

        String[] who_parms = href_map.get("parms").toString().split(","), testKeys;
        String who_parm = "";
        *//**
         * 取得参数 并 赋值
         *//*
        who_parm = TableUtils.getParms(who_parms,tableView);
        //取得返回数据
        who_parm = StringUtils.strTOJsonstr(who_parm);
        return  ActivityController.getData(href_map.get("seviceName").toString(),href_map.get("methodName").toString(),who_parm).toString();
    }*/


    public static Object getData4(Map<String, Object> href_map, Map<String,Object> data_map){

        String[] who_parms = href_map.get("parms").toString().split(","), testKeys;
        String who_parm = "";
        /**
         * 取得参数 并 赋值
         */
        who_parm = TableUtils.getParmsByMap(who_parms,data_map);
        //取得返回数据
        who_parm = StringUtils.strTOJsonstr(who_parm);
        return  ActivityController.getData4ByPost(href_map.get("seviceName").toString(),href_map.get("methodName").toString(),who_parm).toString();
    }


    public static void createList(Context mContext,String menu_code,List<Map<String, Object>> maings_list, String[] contents, UIListTableView mian_TableView, ClickListener listener) {

        Map<String, Object> map = null, dataMap;
        mian_TableView.setService_name(menu_code);

        String id = "";
        int approvalstatus = 0, estatus = 0;
        List<String> values = null;
        if (maings_list == null || maings_list.size() ==0) {
            try {
                ReflectHelper.callMethod2(mContext,"refreshTb",new Object[]{maings_list},List.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        for (int i = 0, n = maings_list.size(); i < n; i++) {
            map = maings_list.get(i);
            values = createMain(map, contents);
            if (map.get("id") != null) {
                id = map.get("id").toString();
            }

            if (map.get("approvalstatus") != null) {
                approvalstatus = Integer.parseInt(map.get("approvalstatus").toString());
            }

            if (map.get("estatus") != null) {
                estatus = Integer.parseInt(map.get("estatus").toString());
            }

            //创建 审批状态 列表对象
            mian_TableView.addBasicItem(id, "",new String[]{},values.toArray(new String[values.size()]), R.color.black, true, 1, approvalstatus, estatus);

        }
        mian_TableView.set_dataList(maings_list);
        mian_TableView.setClickListener(listener);
        mian_TableView.setService_name(menu_code);
        mian_TableView.commit();

    }

    public static List<String> createMain(Map<String, Object> data, String[] keys) {
        List<String> resList = new ArrayList<String>();
        String key = "", val;
        for (int i = 0, n = keys.length; i < n; i++) {
            key = keys[i];
            if (data.get(key) != null) {
                val = data.get(key).toString();
            } else {
                val = "";
            }
            resList.add(val);
        }
        return resList;
    }

    /**
     * 将关联 名字写到 关联档案中
     * @param name
     * @param mapAll
     * @return
     */
    public static Map<String,Object> getMxOptions(String name,Map<String,Object> mapAll){

        Map<String,Object> mxMap = null;
        if (mapAll != null){
            Set<String> sets = mapAll.keySet();

            for (String key:sets) {
                if (key.equals("add")){
                    continue;
                }
                mxMap = (Map<String, Object>) mapAll.get(key);
                if (mxMap.get("code").toString().toLowerCase().equals(name.toLowerCase())){
                    mxMap.put("gl_name",key);
                    break;
                }
            }
        }
        return  mxMap;
    }

    /**
     * 替换表达式中的 值
     * @param expression
     */
    public static String replaceExpression(Context mContext,String expression) throws NoSuchFieldException, IllegalAccessException {
        String val_str;
        Object table = ReflectHelper.getValueByFieldName(mContext,"tableView");
        while (expression.indexOf("[")>=0 && expression.indexOf("]")>=0){
            int start = expression.indexOf("[");
            int end = expression.indexOf("]");
            String col_name = expression.substring(start+1,end);
            val_str = get_colval_Main(mContext,col_name,(UITableView)table).toString();
            expression = expression.replace("["+col_name+"]",String.valueOf(val_str));
        }
        return  expression;
    }

    /**
     * 得到主数据字段
     * @param mContext
     * @param update_col_name
     * @param tableView
     * @return
     */
    public static String get_colval_Main(Context mContext,String update_col_name,UITableView tableView){

        BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
        if(item == null){
            Toast.makeText(mContext,"未找到项目",Toast.LENGTH_SHORT).show();
            return "err";
        }
        if(item.getSubtitle().equals("")){
            return "0";
        }
        return  item.getSubtitle();
    }

    /**
     * 获取Map中字段值
     * @param mContext
     * @param update_col_name
     * @param itemMap
     * @return
     */
    public static String getcolvalByMap(Context mContext,String update_col_name,Map<String,Object> itemMap){

        if (itemMap.get(update_col_name)== null){
            if (update_col_name.equals("hydj")){
                return itemMap.get("dj").toString();
            }else{
                Toast.makeText(mContext,"未找到项目",Toast.LENGTH_SHORT).show();
                return "err";
            }

        }
        if(itemMap.get(update_col_name).toString().equals("")){
            return "0";
        }
        return  itemMap.get(update_col_name).toString();
    }

    /**
     * 更新主表数据
     * @param mContext
     * @param update_col_name
     * @param val
     */
    public static void update_colval_Main(Context mContext,String update_col_name,String val){

        UITableView tableView = null;
        try {
            tableView = (UITableView) ReflectHelper.getValueByFieldName(mContext,"tableView");
            BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
            if(item == null){
                Toast.makeText(mContext,"未找到项目"+update_col_name,Toast.LENGTH_SHORT).show();
                return;
            }
            item.setSubtitle(val);
            View v1  = tableView.getLayoutList(item.getIndex());
            tableView.setupBasicItemValue(v1,item,item.getIndex());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }




    /**
     * 验证 明细行数
     * @param mContext
     * @param count_map
     * @param gs_map
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static boolean validateCount(Context mContext,Map<String,Object> count_map,Map<String,Object> gs_map) throws NoSuchFieldException, IllegalAccessException {
        if(count_map.get("from") != null){
            String from = count_map.get("from").toString();
            Map<String,Object> mx_map = ActivityController.getMxOptions(from,gs_map);
            String name = (String) mx_map.get("gl_name");
            int index = Integer.parseInt(name.replace("mx","").replace("gs_list",""));
            List<Map<String,Object>> _dataList = null;
            switch (index){
                case 1:
                    _dataList = (List<Map<String, Object>>) ReflectHelper.getValueByFieldName(mContext,"mx"+index+"_data");
                    if (_dataList == null || _dataList.size()==0){
                        //Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                case 2:
                    _dataList = (List<Map<String, Object>>) ReflectHelper.getValueByFieldName(mContext,"mx"+index+"_data");
                    if (_dataList == null || _dataList.size()==0){
                        //Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                case 3:
                    _dataList = (List<Map<String, Object>>) ReflectHelper.getValueByFieldName(mContext,"mx"+index+"_data");
                    if (_dataList == null || _dataList.size()==0){
                        //Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
            }
        }
        return  true;
    }

    /**
     * 根据用户 更新 默认数据
     * @param main_data
     * @param data
     * @param user
     * @return
     */
    public static Map<String,Object>  initUserMainData(Map<String,Object> main_data,Map<String,String> data, Sys_user user){
        String val = "";
        if(data !=  null){
            Set<String> sets = data.keySet();
            for (String key:sets){
                val = data.get(key).toString();
                switch (val){
                    case "_zydnId":
                        main_data.put(key,user.get_zydnId());
                        break;
                    case "_zydnName":
                        main_data.put(key,user.get_zydnName());
                        break;
                    case "_bmdnId":
                        main_data.put(key,user.get_bmdnId());
                        break;
                    case "_bmdnName":
                        main_data.put(key,user.get_bmdnName());
                        break;
                    case "_loginName":
                        main_data.put(key,user.get_loginName());
                        break;
                    case  "trantime":
                        main_data.put(key, DateUtil.createDate());
                        break;
                    default:
                        break;
                }
            }
        }
        return  main_data;
    }


    /**
     * 显示对话框 并选择 带出数据
     * @param mContext
     * @param gldn
     * @param column_name
     * @param service_name
     * @param method_name
     * @param pars2
     * @param _catche
     * @param main_form
     */
    public static void showDialog_Val(final Context mContext, String gldn, String column_name,
                                      String service_name, String method_name, String pars2, Map<String, Object> _catche,
                                      final View main_form,ImageView reshData,boolean isPage){

        final String final_gldn = gldn;
        final ImageView finalReshData = reshData;

        String pars ="";
        if (isPage){
            pars = new ListParms("0","0",AppUtils.limit,service_name,pars2).getParms();
        }else{
            pars = pars2;
        }

        List<String> items_list= null;
        items_list = ActivityController.getDialogData_listByPost(service_name,method_name,_catche,gldn,column_name,AppUtils.limit,pars);

        int recordcount = 0 ,pageIndex = 0;
        List<Map<String,Object>>testMapList;
        if(_catche.get(gldn) != null){
            testMapList = (List<Map<String,Object>>)_catche.get(gldn);
            if (_catche.get(gldn+"_results") != null){
                recordcount = Integer.parseInt(_catche.get(gldn+"_results").toString());
                pageIndex = 1;
            }

        }else {
            testMapList = new ArrayList<Map<String,Object>>();
        }

        //得到 title

        String title="";
        if (gldn.indexOf("~")>=0){
            String[] gldns = gldn.split("~");
            title = gldns[1];
        }

        final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(mContext, main_form,testMapList,service_name,method_name,"",column_name,recordcount,pageIndex,title);
        mSingleChoicePopWindow.setOnOKButtonListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                        if (map != null){
                            finalReshData.setTag(map);
                            mSingleChoicePopWindow.show(false);
                            finalReshData.performClick();
                        }

                    }
                });
        mSingleChoicePopWindow.show(true);
    }



    /* 显示对话框 并选择 带出数据 指定查询字段
    * @param mContext
    * @param gldn
    * @param column_name
    * @param service_name
    * @param method_name
    * @param pars2
    * @param _catche
    * @param main_form
    */
    public static void showDialog_Val(final Context mContext, final String gldn, final String column_name, final String[] cx_columns,
                                      final String service_name, final String method_name, String pars2, final Map<String, Object> _catche,
                                      final View main_form, ImageView reshData, boolean isPage){

        final String final_gldn = gldn;
        final ImageView finalReshData = reshData;

        String pars ="";
        if (isPage){
            pars = new ListParms("0","0",AppUtils.limit,service_name,pars2).getParms();
        }else{
            pars = pars2;
        }

        final String finalPars = pars;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int recordcount = 0 ,pageIndex = 0;
                List<Map<String,Object>>testMapList = null;

                if(_catche.get(gldn) != null){
                    testMapList  = (List<Map<String,Object>>)_catche.get(gldn);
                    if (_catche.get(gldn+"_results") != null){
                        recordcount = Integer.parseInt(_catche.get(gldn+"_results").toString());
                        pageIndex = 1;
                    }

                }else {
                    Object res  = ActivityController.getData2ByPost(mContext,service_name,method_name, StringUtils.strTOJsonstr(finalPars));
                    if (res != null && !res.toString().contains("(abcdef)")){
                        testMapList = (List<Map<String, Object>>)res;
                    }
                }

                if (testMapList == null){testMapList = new ArrayList<Map<String,Object>>();}

                //得到 title

                String title="";
                if (gldn.indexOf("~")>=0){
                    String[] gldns = gldn.split("~");
                    title = gldns[1];
                }

                final int finalRecordcount = recordcount;
                final int finalPageIndex = pageIndex;
                final List<Map<String, Object>> finalTestMapList = testMapList;
                final String finalTitle = title;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(mContext, main_form, finalTestMapList,service_name,method_name,finalPars,column_name,cx_columns, finalRecordcount, finalPageIndex, finalTitle);
                        mSingleChoicePopWindow.setOnOKButtonListener(
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Map<String,Object> map = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                                        if (map != null){
                                            finalReshData.setTag(map);
                                            mSingleChoicePopWindow.show(false);
                                            finalReshData.performClick();
                                        }

                                    }
                                });
                        mSingleChoicePopWindow.show(true);
                    }
                });

            }
        }).start();

    }


    /**
     * 显示对话框 并选择 带出数据
     * @param mContext
     * @param gldn
     * @param column_name
     * @param select_map
     * @param service_name
     * @param method_name
     * @param pars2
     * @param _catche
     * @param main_form
     * @param views
     */
    public static void showDialog_UI(final Context mContext, final String gldn, final String column_name, final Map<String, Object> select_map,
                                     final String service_name, final String method_name, String pars2, final Map<String, Object> _catche,
                                     final View main_form, final List<String> views){

        //final 变量
        final Map<String, Object>final_select_map = select_map;
        final String final_gldn = gldn;

        Map<String,Object> _optionMap = (Map<String, Object>) select_map.get(gldn);
        final String pars = new ListParms("0","0",AppUtils.limit,service_name,pars2).getParms();


        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> items_list= null;
                if (final_select_map.get(service_name) != null) {
                    items_list = FastJsonUtils.MapToListByKey((List<Map<String, Object>>) final_select_map.get(service_name), column_name);
                } else {
                    items_list = ActivityController.getDialogData_listByPost(service_name,method_name,_catche,gldn,column_name,AppUtils.limit,pars);
                }

                int recordcount = 0 ,pageIndex = 0;
                List<Map<String,Object>>testMapList;
                if(_catche.get(gldn) != null){
                    testMapList = (List<Map<String,Object>>)_catche.get(gldn);
                    recordcount = Integer.parseInt(_catche.get(gldn+"_results").toString());
                    pageIndex = 1;
                }else {
                    testMapList = new ArrayList<Map<String,Object>>();
                }

                //得到 title

                String title="";
                if (gldn.indexOf("~")>=0){
                    String[] gldns = gldn.split("~");
                    title = gldns[1];
                }

                final List<Map<String,Object>> finaltestMapList = testMapList;
                final int finalrecordcount = recordcount,finalpageIndex = pageIndex;
                final String finaltitle = title;

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(mContext, main_form,finaltestMapList,service_name,method_name,pars,column_name,finalrecordcount,finalpageIndex,finaltitle);
                        mSingleChoicePopWindow.setOnOKButtonListener(
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int which = mSingleChoicePopWindow.getSelectItem();
                                        Map<String,Object> map = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                                        Map<String,Object> res_select = (Map<String, Object>) final_select_map.get(final_gldn);
                                        if (res_select != null){
                                            Set<String> keys= res_select.keySet();
                                            for (String key:keys) {
                                                updateOneUI_1(mContext,map,key,res_select.get(key).toString(),main_form,views);
                                            }
                                        }

                                    }
                                });
                        mSingleChoicePopWindow.show(true);

                        WeiboDialogUtils.closeDialog(mDialog);
                    }
                });

            }
        }).start();

    }

    /**
     * 通过 key 至 找到控件 更新值 到控件
     * @param mContext
     * @param map
     * @param id_key
     * @param key
     * @param main_form
     * @param view_names
     * @return
     */
    public static String updateOneUI_1(Context mContext, Map<String,Object> map, String id_key, String key, View main_form,List<String>view_names){

        int id = IDHelper.getViewID(mContext,id_key);
        View view = main_form.findViewById(id);
        String val="";
        if(map.get(key)!= null){
            val = map.get(key).toString();
        }else{
            val = "";
        }

        if (view instanceof TextView_custom){
            TextView_custom tv = (TextView_custom) view;
            tv.setText(val);
        }else if (view instanceof EditText_custom){
            EditText_custom ev = (EditText_custom) view;
            ev.setText(val);
        }

        //添加 到views 中
        if (!id_key.equals("")){
            view_names.add(id_key);
        }
        return  val;
    }

    /**
     * 显示 一个多选框
     * @param mContext    当前上下文对象
     * @param service_name 访问 service
     * @param method_name  访问  method
     * @param pars          访问参数
     * @param column_name  显示字段名称
     * @param rootView      弹出框 父视图
     * @param view_names    产生变化的视图名称集合
     */
    public static void showDialog_MoreSelect(final Context mContext, final String gldn, final String service_name, final String method_name, String pars, final String column_name, final View rootView, final List<String> view_names, final Map<String,Object>select_map){
        pars = StringUtils.strTOJsonstr(pars);
        final String  parms = pars;
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
        mContext1 = mContext;
        final Activity activity = (Activity) mContext;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object res = ActivityController.getData3ByPost(service_name,method_name,parms);
                if(res != null && !res.toString().contains("(abcdef)")){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Map<String,Object> _selectMap = (Map<String, Object>) select_map.get(gldn);

                            Map<String,Object> map = null;
                            int recordcount = 0,pageIndex = 1;
                            if (res instanceof  JSONObject){
                                map = (Map<String, Object>) res;
                            }

                            if (map == null){return;}

                            recordcount = Integer.parseInt(map.get("results").toString());

                            final List<Map<String,Object>>_dataList = (List<Map<String, Object>>) map.get("rows");
                            String title="";
                            if (gldn.indexOf("~")>=0){
                                String[] gldns = gldn.split("~");
                                title = gldns[1];
                            }
                            final MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(mContext,rootView,_dataList,
                                    new boolean[_dataList.size()],service_name,method_name,parms,recordcount,1,column_name,null,title);
                            multiChoicePopWindow.show(true);

                            WeiboDialogUtils.closeDialog(mDialog);

                            multiChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String values= "";
                                    //验证数据是否 为空
                                    if (_dataList == null){
                                        Toast.makeText(mContext,activity.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Set<String> sets = _selectMap.keySet();
                                    for (String key:sets){
                                        if (key.equals("href")){
                                            continue;
                                        }
                                        values="";
                                        boolean[] items = multiChoicePopWindow.getSelectItem();
                                        if(items != null && items.length >0){
                                            for (int i=0,n=items.length;i<n;i++){
                                                if(items[i]){
                                                    Object res = _dataList.get(i);
                                                    Map<String,Object>resMap = (Map<String, Object>) res;
                                                    if (values.equals("")){
                                                        values += resMap.get(_selectMap.get(key)).toString();
                                                    }else{
                                                        values += ","+resMap.get(_selectMap.get(key)).toString();
                                                    }
                                                }
                                            }
                                        }
                                        int id = IDHelper.getViewID(mContext,key);
                                        View view_ = rootView.findViewById(id);
                                        if(view_ instanceof TextView_custom){
                                            TextView_custom tv1 = (TextView_custom) view_;
                                            tv1.setText(values);
                                        }else if (view_ instanceof  EditText_custom){
                                            EditText_custom et1 = (EditText_custom) view_;
                                            et1.setText(values);
                                        }
                                        if (!view_names.contains(key)){
                                            view_names.add(key);
                                        }

                                    }
                                    multiChoicePopWindow.show(false);
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 单选框
     * @param mContext
     * @param parmsMap
     * @param serviceName
     * @param mothedName
     * @param pars
     * @param colName
     * @param main_form
     * @param ev
     * @param finalmap
     */
    public static void showDialog_OneSelect(final Context mContext, final Map<String,Object>parmsMap, final String serviceName, final String mothedName, String pars, final String colName, final View main_form, final View ev, final Map<String,Object> finalmap){
        mContext1 =mContext;
        final String testpars = StringUtils.strTOJsonstr(pars);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = ActivityController.getDataByPost(mContext,serviceName,mothedName,testpars);
                int recordcount=0,pageIndex=0;
                List<Map<String,Object>> result_list =null;

                final Map<String,Object> finalParMap = parmsMap;
                try {
                    if(!res.equals("")){
                        Map<String,Object>resMap = getResMap(res.toString());
                        if(resMap.get("results")!= null){
                            recordcount = Integer.parseInt(resMap.get("results").toString());
                            pageIndex = 1;
                            result_list = (List<Map<String, Object>>) resMap.get("rows");
                            List<String> list = FastJsonUtils.MapToListByKey(result_list,colName);

                            final List<Map<String, Object>> finalResult_list = result_list;
                            final int finalRecordcount = recordcount;
                            final int finalPageIndex = pageIndex;
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(mContext, main_form, finalResult_list,serviceName,mothedName,
                                            testpars,colName, finalRecordcount, finalPageIndex,"");
                                    mSingleChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            Map<String, Object> dataMap = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                                            Set<String> sets = finalParMap.keySet();
                                            for (String key: sets) {
                                                finalmap.put(key,dataMap.get(finalParMap.get(key)));
                                            }
                                            if (ev instanceof TextView_custom){
                                                TextView_custom tv1 = (TextView_custom) ev;
                                                tv1.setText(finalmap.get(tv1.getCode()).toString());
                                            }else if (ev instanceof  EditText_custom){
                                                EditText_custom ed1 = (EditText_custom) ev;
                                                ed1.setText(finalmap.get(ed1.getCode()).toString());
                                            }

                                            mSingleChoicePopWindow.show(false);
                                        }
                                    });
                                    mSingleChoicePopWindow.show(true);
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static Map<String,Object> getResMap(String res){
        Map<String,Object>resMap = FastJsonUtils.strToMap(res.toString());
        if(resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
            return (Map<String, Object>) resMap.get("data");
        }
        return  null;
    }

    /**
     * 显示日期控件
     * @param mContext
     * @param view
     */
    public static void showDialog_Datetime(Context mContext,View view){
        String dateq = "";
        if (view instanceof TextView_custom){
            TextView_custom v = (TextView_custom) view;
            dateq = v.getText().toString();
        }else if(view instanceof EditText_custom){
            EditText_custom v = (EditText_custom) view;
            dateq = v.getText().toString();
        }else if(view instanceof TextView){
            TextView v = (TextView) view;
            dateq = v.getText().toString();
        }else if(view instanceof EditText){
            EditText v = (EditText) view;
            dateq = v.getText().toString();
        }

        if(dateq.equals("")){
            dateq = DateUtil.createDate();
        }

        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil((Activity) mContext, dateq);
        dateTimePicKDialog.dateTimePicKDialog_view(view,1);
    }



    public static View openLeftBigWindow(WindowManager  wManager, LayoutInflater inflater, View.OnClickListener listener,Map<String,Object> map,int x,int y,String status) {
        WindowManager.LayoutParams wmParams = null;
        LinearLayout containerLayout;

        View view = null;

        if (map.get("removeView") == null) {

            //获取LayoutParams对象
            wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            wmParams.format = PixelFormat.RGBA_8888;;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            wmParams.x = x;
            wmParams.y = y;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            view =  inflater.inflate(R.layout.list_item_button, null);
            /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,10,10,10);//4个参数按顺序分别是左上右下
            view.setLayoutParams(layoutParams);*/
            wManager.addView(view, wmParams);
            map.put("removeView",view);
            if (view != null){
                Button wancheng = (Button) view.findViewById(R.id.wancheng);
                if (status.equals("未完成")){
                    wancheng.setText("完成");
                }else{
                    wancheng.setText("未完成");
                }
                wancheng.setTag(map);
                wancheng.setOnClickListener(listener);

                Button shanchu = (Button) view.findViewById(R.id.shanchu);
                shanchu.setOnClickListener(listener);
                shanchu.setTag(map);

                Button xiangqing = (Button) view.findViewById(R.id.xiangqing);
                xiangqing.setOnClickListener(listener);
                xiangqing.setTag(map);
            }


        } else {
            try {
                wManager.removeView((View) map.get("removeView"));
            }catch (Exception e){

            }

            map.remove("removeView");
        }
        return view;
    }

    /**
     * 设置tableVIew 单项值
     * @param tableView
     * @param item
     * @param view
     * @param index
     * @param val
     */
    public static void updateBasicItem(UITableView tableView,BasicItem item,View view,int index,String val){
        if (tableView != null){
            item.setSubtitle(val);
            tableView.setupBasicItemValue(view,item,index);
        }else{
            if (view instanceof TextView_custom){
                TextView_custom tv1 = (TextView_custom) view;
                tv1.setText(val);
            }else if(view instanceof EditText_custom){
                EditText_custom ed1 = (EditText_custom) view;
                ed1.setText(val);
            }
        }
    }

    /**
     * 设置
     * @param view
     * @param scrollView
     */
    public static   void setViewOnTouch(View view, ScrollView scrollView){

        final ScrollView finalScrollView =  scrollView;
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    finalScrollView.requestDisallowInterceptTouchEvent(false);
                }else{
                    finalScrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }


    public static View openLeftBigWindow(WindowManager wManager, LayoutInflater inflater, View.OnClickListener listener, Map<String,Object> map, int x, int y,int layout,String[] buttons) {
        WindowManager.LayoutParams wmParams = null;
        LinearLayout containerLayout;

        View view = null;

        if (map.get("removeView") == null) {

            //获取LayoutParams对象
            wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            wmParams.format = PixelFormat.RGBA_8888;;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            wmParams.x = x;
            wmParams.y = y;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            view =  inflater.inflate(layout, null);
            /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,10,10,10);//4个参数按顺序分别是左上右下
            view.setLayoutParams(layoutParams);*/
            wManager.addView(view, wmParams);
            map.put("removeView",view);
            if (view != null){
                for (String button_:buttons) {
                    int id = IDHelper.getViewID(view.getContext(),button_);
                    Button wancheng = (Button) view.findViewById(id);
                    wancheng.setTag(map);
                    wancheng.setOnClickListener(listener);
                }
                /*Button wancheng = (Button) view.findViewById(R.id.wancheng);
                wancheng.setTag(map);
                wancheng.setOnClickListener(listener);

                Button shanchu = (Button) view.findViewById(R.id.shanchu);
                shanchu.setOnClickListener(listener);
                shanchu.setTag(map);

                Button xiangqing = (Button) view.findViewById(R.id.xiangqing);
                xiangqing.setOnClickListener(listener);
                xiangqing.setTag(map);*/
            }


        } else {
            try {
                wManager.removeView((View) map.get("removeView"));
            }catch (Exception e){

            }

            map.remove("removeView");
        }
        return view;
    }

    public static String getIds(boolean[] selects,List<Map<String,Object>> _datalist){
        String ids = "";
        List<Map<String,Object>> deleteList  = new ArrayList<Map<String,Object>>();
        for (int i=0,n=selects.length;i<n;i++){
            if (selects[i]){
                deleteList.add(_datalist.get(i));
                if (ids.equals("")){
                    ids+=_datalist.get(i).get("id");
                }else{
                    ids+=","+_datalist.get(i).get("id");
                }
            }
        }
        return  ids;
    }

    public static  void setLayoutManager(RecyclerView historymx_View,Context mContext){
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        historymx_View.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        historymx_View.setHasFixedSize(true);
        RecyclerViewUtils.setLayoutManagerHeight(mContext,historymx_View,3);
    }

    /**
     * 从 Map中 得到 值
     * @param map
     * @param key
     * @return
     */
    public static String returnString(Map<String,Object> map,String key,List<Map<String,Object>> _gsList){
        String res = null;
        if (key.equals("")){return  "";}
        try{
            if(key.indexOf("：")>=0 || key.indexOf(":")>=0) {
                String[] _keys = null;
                if (key.indexOf("：")>=0){
                    _keys = key.split("：");
                }else if (key.indexOf(":")>=0){
                    _keys = key.split(":");
                }
                String map_key="";
                int start = _keys[1].indexOf("[");
                int end = _keys[1].indexOf("]");

                if(start>=0 && end >=0){
                    map_key = _keys[1].substring(start+1,end);
                }else{
                    map_key = _keys[1];
                }

                if(map.get(map_key) != null){
                    res = getFhz(map,map_key,_gsList,_keys,end);
                }
            }else{
                int start = key.indexOf("[");
                int end = key.indexOf("]");

                String map_key ="";
                if(start>=0 && end >=0){
                    map_key = key.substring(start+1,end);
                }else{
                    map_key = key;
                }
                if(map.get(map_key) != null){
                /*验证 是否存在select 选项*/
                    if(StringUtils.isNumeric(map.get(map_key).toString()) && map.get(map_key).toString().indexOf(".")<0 && Long.parseLong(map.get(map_key).toString())<=100){
                        int index = Integer.parseInt(map.get(map_key).toString());
                        String select_conents = StringUtils.getContents_select(map_key,_gsList);
                        String qsh = StringUtils.getContents_select(map_key,_gsList,"qsh");
                        if (select_conents.indexOf(",")>=0){
                            String[] contents = select_conents.split(",");
                            if (end+1 <= key.length()){
                                if (!qsh.equals("")){
                                    res = contents[index -Integer.parseInt(qsh)]+key.substring(end+1);
                                }else{
                                    res = contents[index]+key.substring(end+1);
                                }

                            }else{
                                if (!qsh.equals("")){
                                    res =contents[index-Integer.parseInt(qsh)];
                                }else{
                                    res =contents[index];
                                }
                            }
                        }else{
                            res = map.get(map_key).toString()+key.substring(end+1);
                        }
                    }else{
                        res = map.get(map_key).toString()+key.substring(end+1);
                    }
                }else{
                    res = "";
                }
            }
        }catch (ArrayIndexOutOfBoundsException ex){
            ex.printStackTrace();
            return "err下拉列表下标超出范围，请修改表单属性起始号";
        }

        return  res;
    }

    /**
     * 得到 返回值 如果 存在select 选项得到 相应值
     * @param map
     * @param map_key
     * @param _gsList
     * @param _keys
     * @param end
     * @return
     */
    public static String getFhz(Map<String,Object> map,String map_key,List<Map<String,Object>>_gsList,String[] _keys,int end){
        String res;
        /*验证 是否存在select 选项*/
        if(StringUtils.isNumeric(map.get(map_key).toString()) && map.get(map_key).toString().indexOf(".")<0 && Long.parseLong(map.get(map_key).toString())<=100){
            int index = Integer.parseInt(map.get(map_key).toString());
            String select_conents = StringUtils.getContents_select(map_key,_gsList);
            if (select_conents.indexOf(",")>=0){
                String[] contents = select_conents.split(",");
                if(end >=0){
                    res = _keys[0]+"："+contents[index]+_keys[1].substring(end+1);
                }else{
                    res = _keys[0]+"："+contents[index];
                }

            }else{
                if(end >=0){
                    res = _keys[0]+"："+map.get(map_key).toString()+_keys[1].substring(end+1);
                }else{
                    res = _keys[0]+"："+map.get(map_key).toString();
                }
            }
        }else{
            if(end >=0){
                res = _keys[0]+"："+map.get(map_key).toString()+_keys[1].substring(end+1);
            }else{
                res = _keys[0]+"："+map.get(map_key).toString();
            }
        }
        return res;
    }

    /**
     * 数字转文本
     * @param val
     * @return
     */
    public static String numberToString(String val){

        if(StringUtils.isNumeric(val)) {
            double val_ = Double.parseDouble(val);

            if (Math.abs(val_) > 10000) {
                val = SizheTool.jq2wxs(String.valueOf(val_ / 10000),"2") + "万";
            }/* else if (Math.abs(val_) > 1000) {
                val = SizheTool.jq2wxs(String.valueOf(val_ / 1000),"2") + "千";
            }*/
        }
        return val;
    }

    public static View showPopUp(View v, Context context, View.OnClickListener onClickListener,Map<String,Object> map,String[] views) {
        LinearLayout layout = new LinearLayout(context);
        mContext1 = context;
        Activity activity = (Activity) context;
        View tv = activity.getLayoutInflater().inflate(R.layout.list_item_button,null);
        //layout.setBackgroundColor(Color.GRAY);
        tv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        layout.addView(tv);
        PopupWindow popupWindow = new PopupWindow(layout,460,100);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0]-popupWindow.getWidth()+20, location[1]);

        for (String view:views){
            int id = IDHelper.getViewID(context,view);
            View cilck_view= tv.findViewById(id);
            if (view.equals("wancheng")){
                Button wanchengButton = (Button) cilck_view;
                if (map.get("val").toString().equals("未完成")){
                    wanchengButton.setText("完成");
                }else{
                    wanchengButton.setText("未完成");
                }
            }
            if (cilck_view != null){
                cilck_view.setOnClickListener(onClickListener);
                map.put("removeView",tv);
                map.put("popupWindow",popupWindow);
                cilck_view.setTag(map);
            }
        }
        return tv;
    }

    /**
     * 隐藏键盘
     * @param activity
     */
    public static void hiddenInput(Activity activity){
        InputMethodManager manager = ((InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE));
        if (activity.getCurrentFocus() != null){
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 验证权限
     * @param menu_code
     * @return
     */
    public static boolean checkQx(String menu_code){
        String pars = new ListParms("mobilerole","user_name:"+AppUtils.app_username+",menu_codes:"+menu_code,"showQx").getParms();
        Object res = ActivityController.getData4ByPost("mobilerole","findMenuByUserIdAndMenuCode", StringUtils.strTOJsonstr(pars));
        if (res != null && !res.toString().contains("(abcdef)")){
            JSONArray jsonArray = (JSONArray) res;
            Map<String,Object> resMap = null;
            if (jsonArray != null && jsonArray.size()>0){
                resMap = (Map<String, Object>) jsonArray.get(0);

                if (resMap.get("sfky") != null && resMap.get("sfky").equals("是")){
                    return  true;
                }else{
                    return  false;
                }
            }else{
                return  false;
            }

        }
        return false;
    }

    /**
     * 创建 更新界面 等待关闭 操作 线程
     * @param mContext
     * @param isSave
     * @param dialog
     * @return
     */
    public static  Runnable createWaitMiniteCz(Context mContext,String isSave,Dialog dialog) {
        mContext1= mContext;
        final String finalSave = isSave;
        final Context finalContext = mContext;
        final Dialog mDialog = dialog;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (finalSave != null) {
                    Map<String, Object> resMap = FastJsonUtils.strToMap(finalSave.toString());

                    if (resMap == null) {
                        Toast.makeText(finalContext, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                        WeiboDialogUtils.closeDialog(mDialog);
                        return;
                    }
                    if (resMap.get("state") != null && resMap.get("state").toString().equals("success")) {
                        Toast.makeText(finalContext, resMap.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        String id = resMap.get("data").toString();
                        if (id.indexOf("err") >= 0) {
                            Toast.makeText(finalContext, id.replace("err,", ""), Toast.LENGTH_SHORT).show();
                        } else {
                            ((Activity) finalContext).finish();
                        }

                    } else {
                        if (resMap.get("msg") != null){
                            Toast.makeText(finalContext, resMap.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                WeiboDialogUtils.closeDialog(mDialog);
            }
        };
        return  runnable;
    }

    public static void showMxRecyclerView(final Context mContext, final String menucode, final String ywcode, final String id, String title, final int index){
        mContext1 = mContext;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String parms = new ListParms(menucode,"ywcode:"+ywcode+",id:"+id).getParms();
                Object res = getData2ByPost(mContext,"formproperty","getMxData",StringUtils.strTOJsonstr(parms));
                if (res != null){
                    if (res instanceof JSONObject){
                        final Map<String,Object> resMap = (Map<String, Object>) res;
                        final Activity activity = (Activity) mContext;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RecyclerView recyclerView = (RecyclerView) activity.findViewById(IDHelper.getViewID(mContext,"mx"+index+"_recyclerView"));
                                ActivityController.setLayoutManager(recyclerView,mContext);
                                String[] contents = null,titles = null;
                                //设置标题
                                if (resMap.get("columns")!= null){
                                    List<Map<String,Object>> colList = (List<Map<String, Object>>) resMap.get("columns");
                                    Map<String,Object> colMap = getColumns(colList);
                                    titles = (String[]) colMap.get("titles");
                                    contents = (String[]) colMap.get("contents");
                                }

                                if (resMap.get("data")!= null){
                                    List<Map<String,Object>> dataList = (List<Map<String, Object>>) resMap.get("data");
                                  /*  if (titles != null){
                                        Map<String,Object> dataMap = new HashMap<String, Object>();
                                        int i = 0;
                                        for (String content:contents) {
                                            dataMap.put(content,titles[i]);
                                            i++;
                                        }
                                        dataList.add(0,dataMap);
                                    }*/
                                    //创建并设置Adapter
                                    MyTaskmxAdapter2 mAdapter1 = new MyTaskmxAdapter2(mContext,dataList,AppUtils.getScreenWidth(mContext),titles,contents);
                                    recyclerView.setAdapter(mAdapter1);
                                }
                            }
                        });
                    }
                }
            }
        }).start();

    }


    /**
     * 得到 字段列
     * @param _columnList
     * @return
     */
    public static Map<String,Object> getColumns(List<Map<String,Object>> _columnList){

        Map<String,Object> resMap = new LinkedHashMap<String, Object>();
        String[] name = null,contents = null,titles = null;
        List<Map<String,Object>> removeList = new ArrayList<Map<String,Object>>();
        if (_columnList != null && _columnList.size() > 0) {
            name = new String[_columnList.size()];
            int i = 0;
            for (Map<String, Object> columnMap : _columnList) {
                if (columnMap.get("visible") != null && columnMap.get("visible").equals("false")) {
                    removeList.add(columnMap);
                    continue;
                }
                name[i] = columnMap.get("title").toString();
                i++;
            }

            if (removeList.size() > 0) {
                _columnList.removeAll(removeList);
            }

            contents = FastJsonUtils.ListMapToListStr(_columnList,"field");
            titles = FastJsonUtils.ListMapToListStr(_columnList,"title");
        }

        resMap.put("titles",titles);
        resMap.put("contents",contents);
        return  resMap;
    }

    public static void showMxWebview(WebView wv, String menucode, String ywcode, String id, String title){

        final WebView finalWebview = wv;


        WebSettings webSettings = finalWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        finalWebview.setHorizontalScrollBarEnabled(false);//水平不显示
        String url = "";
        if (AppUtils.isIp(AppUtils.app_address)){
            url = "http://"+AppUtils.app_address+":8080/wtmis/formProperty/goMxView?menucode="+menucode+"&ywcode="+ywcode+"&id="+id+"&v_="+AppUtils.user.get_version();
        }else{
            url = "http://"+AppUtils.app_address+"/formProperty/goMxView?menucode="+menucode+"&ywcode="+ywcode+"&id="+id+"&v_="+AppUtils.user.get_version();
        }
        finalWebview.clearCache(true);
        finalWebview.loadUrl(url);
        //加载数据
        finalWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //tv_title.setText(parmsMap.get("title").toString());
                } else {
                    //tv_title.setText("加载中.......");
                }
            }
        });
        //这个是当网页上的连接被点击的时候
        finalWebview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view,final String url) {
                finalWebview.loadUrl(url);
                return true;
            }
        });
    }

    public static void releaseAllWebViewCallback() {
        if (android.os.Build.VERSION.SDK_INT < 16) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Field sConfigCallback = Class.forName(
                        "android.webkit.BrowserFrame").getDeclaredField(
                        "sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

  public  static  String getCurProcessName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {

               String name = appProcess.processName;
            Log.v("test",name);
        }
        return null;
    }

    /**
     * 创建线程更新数据
     * @param mContext
     * @param serviceName
     * @param methodName
     * @param parms
     */
    public static void createThreadDoing(Context mContext,String serviceName,String methodName,String parms){

        final  String finalServiceName = serviceName,
                finalMethodName = methodName,
                finalParms = parms;
        final Context finalmContext = mContext;
        mContext1 = mContext;
        //更新为已阅
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pars = new ListParms(finalServiceName,finalParms).getParms();
                String res = ActivityController.executeForResultByThread(finalmContext,finalServiceName,finalMethodName,StringUtils.strTOJsonstr(pars));
                if (res != null && res.indexOf("err")>= 0){
                    final  String finalres = res;
                    ((Activity)finalmContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(finalmContext,finalres.toString().replace("err,",""),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 显示 一个多选框 改进查询后可多选
     * @param mContext    当前上下文对象
     * @param service_name 访问 service
     * @param method_name  访问  method
     * @param pars          访问参数
     * @param column_name  显示字段名称
     * @param rootView      弹出框 父视图
     * @param view_names    产生变化的视图名称集合
     */
    public static void showDialog_MoreSelect2(final Context mContext, final String gldn, final String service_name, final String method_name, String pars, final String column_name, final View rootView, final List<String> view_names, final Map<String,Object>select_map){
        pars = StringUtils.strTOJsonstr(pars);
        final String  parms = pars;
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
        mContext1 = mContext;
        final Activity activity = (Activity) mContext;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object res = ActivityController.getData3ByPost(service_name,method_name,parms);
                if(res != null && !res.toString().contains("(abcdef)")){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Map<String,Object> _selectMap = (Map<String, Object>) select_map.get(gldn);

                            Map<String,Object> map = null;
                            int recordcount = 0,pageIndex = 1;
                            if (res instanceof  JSONObject){
                                map = (Map<String, Object>) res;
                            }

                            if (map == null){return;}

                            recordcount = Integer.parseInt(map.get("results").toString());

                            final List<Map<String,Object>>_dataList = (List<Map<String, Object>>) map.get("rows");
                            String title="";
                            if (gldn.indexOf("~")>=0){
                                String[] gldns = gldn.split("~");
                                title = gldns[1];
                            }

                            final MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(mContext,rootView,_dataList,
                                    new boolean[_dataList.size()],service_name,method_name,parms,recordcount,1,column_name,getVal(_selectMap),title,"1");
                            multiChoicePopWindow.show(true);

                            WeiboDialogUtils.closeDialog(mDialog);

                            multiChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String values= "";
                                    //验证数据是否 为空
                                    if (_dataList == null){
                                        Toast.makeText(mContext,activity.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Set<String> sets = _selectMap.keySet();
                                    int xh =0 ;
                                    for (String key:sets){
                                        if (key.equals("href")){
                                            continue;
                                        }
                                        List<String> items = multiChoicePopWindow.getSlectList();
                                        values = getSelect(_selectMap,key,xh,items);
                                        int id = IDHelper.getViewID(mContext,key);
                                        View view_ = rootView.findViewById(id);
                                        if(view_ instanceof TextView_custom){
                                            TextView_custom tv1 = (TextView_custom) view_;
                                            tv1.setText(values);
                                        }else if (view_ instanceof  EditText_custom){
                                            EditText_custom et1 = (EditText_custom) view_;
                                            et1.setText(values);
                                        }
                                        if (!view_names.contains(key)){
                                            view_names.add(key);
                                        }
                                        xh++;
                                    }
                                    multiChoicePopWindow.show(false);
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }

    public static String getSelect(Map<String,Object> _selectMap,String key,int xh,List<String> selectList){
       String valKey = _selectMap.get(key).toString(),
        resVal = "";
        if (valKey != null){
            String[] vals = null;
            String val = "";
            for (String key1:selectList) {
                if (!key1.equals("")){
                    vals = key1.split(",");
                    val = vals[xh];
                    if (resVal.equals("")){
                        resVal+=val;
                    }else{
                        resVal += ","+val;
                    }
                }
            }
        }
        return resVal;
    }

    public static String getVal(Map<String,Object> _selectMap){
        Set<String> sets = _selectMap.keySet();
        String vals = "";
        for (String key:sets) {
            if (vals.equals("")) {
                vals += _selectMap.get(key).toString();
            } else{
                vals += ","+_selectMap.get(key).toString();
            }
        }
        return  vals;
    }

    public static void  createNotification(Activity activity,String title,String content,String id,String name,String to){
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content) //设置通知栏显示内容
                .setContentIntent(getDefalutIntent(activity,Notification.FLAG_AUTO_CANCEL,id,name,to)) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("有一条新的短消息") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.logo);//设置通知小ICON
        int notifyId = 0;
        mNotificationManager.notify(notifyId, mBuilder.build());

    }

    public static PendingIntent getDefalutIntent(Activity activity,int flags, String id, String name,String to){
        Intent intent = new Intent();
        if (HuanxinUtils.getUserByCode(to)!= null){
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
        }else{
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
        }
        
        intent.putExtra(EaseConstant.EXTRA_USER_ID,id);
        intent.putExtra(EaseConstant.EXTRA_USER_NAME,name);
        intent.setClass(activity,EaseChartActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(activity, 1, intent, flags);
        return pendingIntent;
    }

    public static boolean createTv(String val,LinearLayout jsr_liner){
        if (jsr_liner != null){
            int count = jsr_liner.getChildCount();
            View view = null;
            TextView_custom tv = null;
            for (int i= 0;i<count;i++) {
                view = jsr_liner.getChildAt(i);
                if (view != null && view instanceof TextView_custom){
                    tv = (TextView_custom) view;
                    if (tv.getText().toString().equals(val)){
                        return true;
                    }
                }
            }
        }

        return  false;
    }

    public static int getPostState(String res){
        if (res == null || res.equals("")){
            return 1;
        }else if (res.contains("Network is unreachable")){
            return 2;
        }else if(res.contains("connect timed out") ||res.contains("failed to connect")){
            return 3;
        }else if(res.contains("No address associated with hostname")){
            return 4;
        }else if(res.contains("Gateway Time-out")){
            return 5;
        }else if(res.contains("request bad")){
            return 6;
        }else{
            return  0;
        }
    }

    /**
     * 验证是否 是主线程
     * @return
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    /**
     * 更新 内容数据
     * @param contents
     * @return
     */
    public static String[] updateContents(String[] contents) {

        String[] jssjContents = Constant.taxrate_array;
        List<String> list = FastJsonUtils.arrayToListStr(jssjContents),resList = new ArrayList<String>();
        if (AppUtils.user.getSjjs().equals("2") ) return  contents;

        int i = 0,start,end;
        for (String content:contents ) {
            if (content.indexOf("[")>=0){
                start = content.indexOf("[");
                end = content.indexOf("]");
                content = content.substring(start+1,end);
            }

            if (!list.contains(content)){
                resList.add(content);
            }
            i++;
        }

        if (resList.size()>0){
            contents = FastJsonUtils.listStrToArray(resList);
        }

        return  contents;

    }

    public static String setParms(Map parmsMap,Context mContext2){

        String parms="",_parms="";
        UITableView tableView = null;

        try {
            tableView = (UITableView) ReflectHelper.getValueByFieldName(mContext2,"tableView");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        if (parmsMap.get("parms")!=null){
            if (parmsMap.get("parms") instanceof JSONObject){
                Map<String,Object> test = (Map<String, Object>) parmsMap.get("parms"),testMap = null;
                Set<String> sets = test.keySet();
                int size = sets.size();
                /**
                 * 如果 key 等于 main 从主数据 去参数信息
                 * 否则 从自身取 参数信息
                 */
                for (String key:sets){

                    if (key.equals("main")){
                        if (get_data().get("mainView") != null ){
                            tableView = (UITableView) get_data().get("mainView");
                            testMap = (Map<String, Object>) test.get(key);
                        }else{
                            testMap = (Map<String, Object>) test.get(key);
                        }
                    }else{
                        testMap = new HashMap<String,Object>();
                        testMap.putAll(test);
                        testMap.remove("main");
                    }

                    if(testMap != null){
                        //
                       /* if(testMap.get("type")!= null) {
                            if (testMap.get("type").equals("validate")) {
                                String href_ = testMap.get("from").toString();
                                if (href_.indexOf("?") >= 0 && href_.indexOf(":") >= 0) {
                                    Map<String, Object> dataMap = null *//*updateSimulateMain()*//*;
                                    String[] keys = StringUtils.getKeys(href_);
                                    for (String key_ : keys) {
                                        if (dataMap.get(key_) == null) {
                                            return  "err,"+"无效参数：" + key_;
                                        }
                                        href_ = href_.replace("[" + key_ + "]", "'" + dataMap.get(key_).toString() + "'");
                                    }
                                    _parms = SizheTool.eval_back(href_);
                                }
                            }
                        }*/

                        Set<String> keys = testMap.keySet();
                        //如果 _parms 不为空 得到 参数 否则 取 map 中的 key
                        String[] keys_list;
                        if (!_parms.equals("")){
                            keys_list = _parms.split(",");
                        }else{
                            keys_list = FastJsonUtils.setStrToArray(keys);
                        }
                        for (String testKey:keys_list ) {
                            //如果 参数 中存在menu_code 直接过掉
                            String test_menucode="",test_val="";
                            if(testKey.indexOf("-~")>=0){
                                String[] hhs =  testKey.split("-~");
                                test_menucode = hhs[0];
                                test_val = hhs[1];
                                testKey = "menu_code";
                            }
                            if (testKey.equals("menu_code") || test_menucode.equals("menu_code")){
                                if(parms.equals("")){
                                    parms +=testKey+":"+test_val;
                                }else{
                                    parms += ","+testKey+":"+test_val;
                                }
                                continue;
                            }
                            BasicItem item = ActivityController.getItem(tableView.getIListItem(),testMap.get(testKey).toString());
                            String val = "";
                            if(item == null){
                                val = testMap.get(testKey).toString();
                            }else{
                                val = item.getSubtitle();
                            }

                            if (testKey.equals("sjjs")){
                                val = AppUtils.user.getSjjs();
                            }else if (testKey.equals("gsxz")){
                                val = AppUtils.user.getGsxz();
                            }

                            if(val.indexOf("_val")>=0){
                                val = val.replace("_val","");
                            }

                            if (val.indexOf("[")>=0 && val.indexOf("]")>=0){
                                try {
                                    val = ActivityController.replaceExpression(mContext2,val);
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(parms.equals("")){
                                parms +=testKey+":"+val;
                            }else{
                                parms += ","+testKey+":"+val;
                            }

                            if(val.equals("")){
                                return  "err,"+"必须先选择："+testKey;
                            }
                        }
                    }
                }
            }else{
                return  "err,"+"格式文件异常";
            }
        }
        return  parms;
    }

    /**
     * 获取特殊基础档案查询service和参数
     * @param service
     * @return
     */
    public static Map<String,String> getServiceAndParamsName(String service) {
        Map<String,String> map = new HashMap<>();
        if (!service.equals("")){
            int start = service.indexOf("_");
            if (start != -1){
                map.put("serviceName",service.substring(0,start));
                map.put("type",service.substring(start+1));
            }else{
                map.put("serviceName",service);
            }
        }
        return  map;
    }

    public static void uploadFile(final Context context, final Map<String,Object> resMap) {
        try {
            Net.UPLOAD_URL = AppUtils.app_address+"/menu/uploadFileToQiniu";
            File file = new File(resMap.get("filePath").toString());
            byte[] buffer = FileUtils.File2Bytes(file);
            Activity sevice = null;
            if (context != null){
                sevice  = (Activity) context;
            }
            final Activity finalSevice = sevice;
            Net.upload(Net.UPLOAD_URL, file.getName(), buffer, FastJsonUtils.mapTOmapStr(resMap), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(mContext1, "fail", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responseStr = response.body().string();
                    try {
                        if(200 <= response.code() && response.code() <= 299){
                            final JSONObject json = JSONObject.parseObject(responseStr);
                            if(json.getString("status").equals("ok")){
                                Map<String,Object> tempMap = FastJsonUtils.strToMap(json.toJSONString());
                                ReflectHelper.callMethod2(finalSevice,"uploadSuccess",new Object[]{tempMap},Map.class);
                            }else{
                                Log.d(getClass().getName(), context.getString(R.string.upload_fail));
                            }
                        }else{
                            Log.d(getClass().getName(), context.getString(R.string.upload_fail));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.d(getClass().getName(), responseStr);
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 查看文件
     * @param context
     * @param map
     */
    public static void showFile(Context context,Map<String,Object> map){
        Context mContext = context;
        Object filePath = map.get("filePath") == null ? map.get("newfilename") : map.get("filePath");
        String fileTemp = filePath == null ? "" : filePath.toString();
        if (map.get("fileName").toString().indexOf("pdf") != -1){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("http://"+AppUtils.file_address+"/"+fileTemp);
            intent.setDataAndType(uri, "application/pdf");
            ((Activity)mContext).startActivity(intent);
        }else{
            Intent intent = new Intent(mContext,FilePreviewActivity.class);
            Map<String,Object> parmsMap = new HashMap<String, Object>();
            parmsMap.put("title","文件预览");
            parmsMap.put("fileName",map.get("fileName"));
            parmsMap.put("filePath",filePath == null ? "" : filePath);
            intent.putExtras(AppUtils.setParms("",parmsMap));
            ((Activity)mContext).startActivity(intent);
        }
    }

    /**
     * 启动附件活动
     * @param context
     * @param map
     */
    public static void startMyFileActivity(Context context,Map<String,Object> map){
        Context mContext = context;
        Intent intent = new Intent(mContext,MyFileActivity.class);
        intent.putExtras(AppUtils.setParms("",map));
        ((Activity)mContext).startActivity(intent);
    }

    @RequiresApi(api = 29)
    public static boolean isActivityRunning(Context mContext, String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ActivityManager.RunningTaskInfo runningTaskInfo = info.get(0);
            ComponentName component = runningTaskInfo.topActivity;
            if(activityClassName.equals(component.getClassName())){
                return true;
            }
        }
        return false;
    }
}
