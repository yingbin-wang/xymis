package com.cn.wti.util.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.dialog.Report_Cpxsrb_dialogActivity;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckDn;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

import static android.R.attr.key;

/**
 * Created by wyb on 2017/6/12.
 */

public class Dialog_ViewUtils {

    public static View showPopUp(View v, Context context, View.OnClickListener onClickListener,int layoutid,int width,int height,Map<String,Object> map, String[] views,String[] childviews,int type) {
        LinearLayout layout = new LinearLayout(context);

        Activity activity = (Activity) context;
        View tv = activity.getLayoutInflater().inflate(layoutid,null);
        //layout.setBackgroundColor(Color.GRAY);
        tv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        layout.addView(tv);
        if (childviews != null && childviews.length == 3){
            width = 500;
        }else if (childviews != null && childviews.length ==2){
            width = 380;
        }

        PopupWindow popupWindow = new PopupWindow(layout,width,height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);


        List<String> childList = null;
        if (childviews != null){
            childList = FastJsonUtils.arrayToListStr(childviews);
        }

        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
        if (type == 1){
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]*2 +20 );
        }else if (type == 2){
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] +80, location[1] + 60);
        }

        int test = 0;

        for (String view:views){
            int id = IDHelper.getViewID(context,view);
            View cilck_view= tv.findViewById(id);
            if (cilck_view != null){

                if (view.equals("wancheng")){
                    Button wanchengButton = (Button) cilck_view;
                    if (map.get("status").toString().equals("未完成")){
                        wanchengButton.setText("完成");
                    }else{
                        wanchengButton.setText("未完成");
                    }
                }

                if (childList != null && !childList.contains(view)){
                    cilck_view.setVisibility(View.GONE);
                    test = 1;
                }else{
                    cilck_view.setOnClickListener(onClickListener);
                    map.put("popupWindow",popupWindow);

                    if(view.equals("xiangqing") && test == 1){
                        map.put("qx",1);
                    }

                    cilck_view.setTag(map);
                }
            }
        }
        return tv;
    }

    public  static  void showReportCxtj(final Context mContext, String service_name, String method_name, String pars, View rootView, String columns, String name, Handler mHandler){

        final Dialog dialog = WeiboDialogUtils.createLoadingDialog(mContext,"");
        final  String finalservice_name = service_name,finalmethod_name = method_name,finalpars = pars,finalcolumns = columns,finalname = name;
        final Context finalcontext = mContext;
        final View finalrootView = rootView;
        final Handler finalHandler = mHandler;

        new Thread(){
            @Override
            public void run() {
                final String pars = StringUtils.strTOJsonstr(finalpars);
                final Activity activity = (Activity) finalcontext;
                final Object res = ActivityController.getData3ByPost(finalservice_name,finalmethod_name,pars);
                if(res != null  && !res.toString().contains("(abcdef)")) {

                    Map<String, Object> map = null;
                    int recordcount = 0, pageIndex = 1;
                    if (res instanceof JSONObject) {
                        map = (Map<String, Object>) res;
                    }

                    if (map == null) {
                        return;
                    }
                    recordcount = Integer.parseInt(map.get("results").toString());
                    final List<Map<String, Object>> _dataList = (List<Map<String, Object>>) map.get("rows");
                    final int finalrecordcount = recordcount;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(finalcontext, finalrootView, _dataList,
                                    new boolean[_dataList.size()], finalservice_name, finalmethod_name, pars, finalrecordcount, 1, finalcolumns,finalcolumns, null, finalname);
                            multiChoicePopWindow.show(true);

                            multiChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String values= "";
                                    List<String> select_list = multiChoicePopWindow.getSlectList();
                                    if(select_list != null && select_list.size() >0){
                                       /* for (int i=0,n=items.length;i<n;i++){
                                            if(items[i]){
                                                Object res = _dataList.get(i);
                                                Map<String,Object>resMap = (Map<String, Object>) res;
                                                if(values.equals("")){
                                                    values += resMap.get(finalcolumns).toString();
                                                }else{
                                                    values +=  "," + resMap.get(finalcolumns).toString();
                                                }
                                            }
                                        }*/
                                        //转字符串
                                        values = FastJsonUtils.listStrTOStr(select_list,",");
                                        Message msg = finalHandler.obtainMessage();
                                        //利用bundle对象来传值
                                        Bundle b = new Bundle();
                                        b.putString("pars", values);
                                        msg.what = 11;
                                        msg.setData(b);
                                        msg.sendToTarget();
                                        multiChoicePopWindow.show(false);
                                    }
                                }
                            });
                            WeiboDialogUtils.closeDialog(dialog);
                        }
                    });
                }else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res != null){
                                Toast.makeText(activity,HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(activity,activity.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    WeiboDialogUtils.closeDialog(dialog);
                }
            }
        }.start();

    }

    public static View showPopUp(View v, Context context, View.OnClickListener onClickListener,int layoutid,int width,int height,Map<String,Object> map,
                                 String[] views,String[] childviews,String[] names,int type) {
        LinearLayout layout = new LinearLayout(context);

        Activity activity = (Activity) context;
        View tv = activity.getLayoutInflater().inflate(layoutid,null);
        //layout.setBackgroundColor(Color.GRAY);
        tv.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        layout.addView(tv);
        PopupWindow popupWindow = new PopupWindow(layout,width,height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);


        List<String> childList = null;
        if (childviews != null){
            childList = FastJsonUtils.arrayToListStr(childviews);
        }

        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
        if (type == 1){
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]*2 +20 );
        }else if (type == 2){
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] +80, location[1] + 60);
        }

        int test = 0;
        int i = 0;
        for (String view:views){
            int id = IDHelper.getViewID(context,view);
            View cilck_view= tv.findViewById(id);
            if (cilck_view != null){
                Button btn = (Button) cilck_view;
                btn.setText(names[i]);
                if (childList != null && !childList.contains(view)){
                    cilck_view.setVisibility(View.GONE);
                    test = 1;
                }else{
                    cilck_view.setOnClickListener(onClickListener);
                    map.put("popupWindow",popupWindow);

                    cilck_view.setTag(map);
                }
            }
            i++;
        }
        return tv;
    }

}
