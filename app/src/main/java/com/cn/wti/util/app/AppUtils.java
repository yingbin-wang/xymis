package com.cn.wti.util.app;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cn.wti.entity.Sys_user;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.number.FileUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.DatabaseUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wangz on 2016/9/22.
 */
public class AppUtils {

    public  static  String app_name;
    public  static  String app_address,book_name,file_address="file.newstanding.com";
    public  static  String app_ip,nw_ip;
    public  static  String app_userid,app_username,huanxincode,huanxinpassword,app_ioc,version="";
    public  static  boolean huanxin_state = false,network_state = true,fwq_state = true,update_state = false,login_state = false;

    public  static Sys_user user;
    public  static DatabaseUtils dbUtils;
    public  static  List<Map<String,Object>> fwq_menuList;
    public  static  List<Map<String,Object>> app_menuList;
    public  static  List<Map<String,Object>> fwq_reportmenuList;
    public  static  Map<String,Object> app_action,worker_role;
    public  static int isSaleZc = 0;

    // 分页参数
    public int start = 0;
    public static String limit  = "20";
    public static String list_limit  = "30";
    public static String report_limit  = "100";
    public int pageindex = 0;

    /**
     * 传递Intent参数 对象 返回Bundle
     * @param type
     * @param map
     * @return
     */
    public static Bundle setParms(String type, Map<String,Object> map){
        Bundle bundle = new Bundle();
        Map<String,Object> map1 = new HashMap<String, Object>();
        map1.putAll(map);
        map1.remove("slideView");
        map1.put("type",type);
        System_one system = new System_one(map1);
        bundle.putSerializable("parms",system);
        return  bundle;
    }

    public static Bundle setParms(String type, List<Map<String, Object>> list){
        Bundle bundle = new Bundle();
        List< Map<String,Object>> resList = new ArrayList< Map<String,Object>>();
        if(list != null && list.size() >0){
            Map<String,Object> map1 = null;
            for (Map<String,Object> map:list) {
                map1 = new HashMap<String, Object>();
                map1.putAll(map);
                map1.remove("slideView");
                map1.put("type",type);
                resList.add(map1);
            }
        }
        System_one system = new System_one(resList);
        bundle.putSerializable("parms",system);
        return  bundle;
    }

    public static Bundle setParms2(String type, Map<String, Object> map){
        Bundle bundle = new Bundle();
        com.hyphenate.easeui.model.System_one system = new com.hyphenate.easeui.model.System_one(map);
        bundle.putSerializable("parms",system);
        return  bundle;
    }

    /**
     * Map 指定key  值是否为空
     * @param map
     * @param key
     * @return
     */
    public  static Object getMapVal(Map<String,Object> map,String key){
        if(map.get(key) != null){
            return  map.get(key);
        }else{
            return  "";
        }
    }

    public static String delspace(String IP){//去掉IP字符串前后所有的空格
        if (IP == null || IP.equals("")){return "";}
        while(IP.startsWith(" ")){
            IP= IP.substring(1,IP.length()).trim();
        }
        while(IP.endsWith(" ")){
            IP= IP.substring(0,IP.length()-1).trim();
        }
        return IP;
    }

    public static boolean isIp(String IP){//判断是否是一个IP
        boolean b = false;
        IP = delspace(IP);
        if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){
            String s[] = IP.split("\\.");
            if(Integer.parseInt(s[0])<255)
                if(Integer.parseInt(s[1])<255)
                    if(Integer.parseInt(s[2])<255)
                        if(Integer.parseInt(s[3])<255)
                            b = true;
        }
        return b;
    }

    /**
     * 获取登录用户的IP
     * @throws Exception
     */
    public static String getRemortIP() throws Exception {
        InetAddress ia=null;
        String localip = "";
        try {
            ia=InetAddress.getLocalHost();
            String localname=ia.getHostName();
            localip =ia.getHostAddress();
            System.out.println("本机名称是："+ localname);
            System.out.println("本机的ip是 ："+localip);
        } catch (Exception e) {
            e.printStackTrace();
            localip = "192.168.16.85";
            /*localip = "192.168.0.101";*/
        }
        return localip;
    }

    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
        }
        return null;
    }


    /**
     * 得到R 资源ID
     * @param pid
     * @return
     */
    public static int getPic(String pid) {
        Field f;
        try {

            if( pid == null){
                return  R.mipmap.salesdaily;
            }else{
                f = R.mipmap.class.getField(pid.toLowerCase());
                return f.getInt(null);
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return  R.mipmap.salesdaily;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 重载 查找图片资源
     * @param pid
     * @return
     */
    public static int getPic(Object pid) {
        Field f;
        try {
            if(pid == null){
                return  R.mipmap.app_aligame;
            }
            f = R.mipmap.class.getField(pid.toString());
            return f.getInt(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到 R id 数组 通过 id text
     *
     * @param ids_text
     * @return
     */
    public static  int[] getRids(String[] ids_text){
        int[] rids = new int[ids_text.length];
        int i = 0;
        for (String id_text:ids_text) {
            rids[i] = getPic(ids_text[i]);
            i++;
        }
        return  rids;
    }

    public static int getWindowWidth(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static int getWindowHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 根据视图编号属性 得到 视图
     * @param list
     * @param val
     * @return
     */
    public static View findViewByCode (List<View> list, String val) {
        View view = null;
        for (View v:list) {
            if(v instanceof TextView_custom){
                TextView_custom test =  (TextView_custom)v;
                if(test.getCode().equals(val)){
                    return  test;
                };
            }else if(v instanceof EditText_custom){
                EditText_custom test =  (EditText_custom)v;
                if(test.getCode().equals(val)){
                    return  test;
                };
            }

        }
        return view;
    }

    public static TextView_custom findView_01 (View v) {

        if(v instanceof TextView_custom){
            TextView_custom test =  (TextView_custom)v;
            return  test;
        }
        return null;
    }

    public static EditText_custom findView_02 (View v) {

        if(v instanceof EditText_custom){
            EditText_custom test =  (EditText_custom)v;
            return  test;
        }
        return null;
    }

    /**
     *根据 List view 封装 表单数据
     * @param list
     * @return
     */
    public static Map<String,Object> createFormDataMap (List<View> list) {
        Map<String,Object> res = new HashMap<String, Object>();
        View view = null;
        for (View v:list) {

            if(v instanceof TextView_custom){
                TextView_custom test =  (TextView_custom)v;
                res.put(test.getCode(),test.getText());
            }else if(v instanceof EditText_custom){
                EditText_custom test =  (EditText_custom)v;
                res.put(test.getCode(),test.getText());
            }
        }
        return res;
    }

    public static Object returnVal(String type,Object val){

        Object  res = null;
        switch (type){
            case "int":
                if (val == null){
                    res =  0;
                }else{
                    res = val;
                }
                break;
            case "string":
                if (val == null){
                    res = "";
                }else{
                    res = val;
                }
                break;
            case "date":
                if (val == null){
                    res = "";
                }else{
                    res = val;
                }
                break;
            default:
                break;
        }
        return  res;
    }

    /**
     * 得到字符串 所对应的 宽度
     * @param text
     * @return
     */
    public static float getWidthByStr(String text){
        Paint paint = new Paint();
        paint.measureText(text);
        return paint.measureText(text);
    }

    public static  float getWidthByView(View tv_name){
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_name.measure(spec,spec);
        int measuredWidthTicketNum = tv_name.getMeasuredWidth();
        return measuredWidthTicketNum;
    }

    /**
     * dp转px
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context,float dp)
    {
        return (int ) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    public static int dp2pxInt(Context context,float dp)
    {
        return (int ) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics()));
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .widthPixels ;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .heightPixels ;
    }

    /**
     * 解析 头像
     * @param activity
     * @return
     */
    public static Bitmap getBitmap(Activity activity){
        Bitmap bitmap = null;
        SharedPreferences sp= activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String photo_16 = sp.getString("photo_16","");
        if (!photo_16.equals("")){
           /* byte [] data_16 = FileUtils.str2byte(photo_16);
            bitmap = FileUtils.readBitmapFromByteArray(data_16,200,200);*/
            photo_16 = photo_16.replace("data:image/png;base64,","");
            bitmap = convertStringToIcon(photo_16);
            bitmap = FileUtils.circleBitmapByShader(bitmap,200,100);
        }

        return bitmap;
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    public static Bitmap convertStringToIcon(String st)
    {
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray=Base64.decode(st, Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap convertStringToIconBase64(String photo_16){
        Bitmap bitmap = null;

        if (!photo_16.equals("")){
            photo_16 = photo_16.replace("data:image/png;base64,","");
            bitmap = convertStringToIcon(photo_16);
            bitmap = FileUtils.circleBitmapByShader(bitmap,50,50);
        }

        return bitmap;
    }

    /**
     * 显示图形 字符串转图形
     * @param photo_16
     * @param type
     * @return
     */
    public static Bitmap convertStringToIconBase64(String photo_16,int type,int width,int height){
        Bitmap bitmap = null;

        if (!photo_16.equals("")){
            photo_16 = photo_16.replace("data:image/png;base64,","");
            bitmap = convertStringToIcon(photo_16);
            switch (type){
                case 1: //画圆
                    bitmap = FileUtils.circleBitmapByShader(bitmap,width,height);
            }

        }

        return bitmap;
    }

    /**
     * 环信 登陆
     * @param userName
     * @param password
     * @return
     */
    public static boolean loginHuanXin(String userName, String password){

        final boolean[] state = {false};
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                state[0] = true;
                new Thread(){
                    @Override
                    public void run() {
                        AppUtils.huanxin_state = state[0];
                    }
                }.start();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
                state[0] = false;
                new Thread(){
                    @Override
                    public void run() {
                        AppUtils.huanxin_state = state[0];
                    }
                }.start();
            }
        });
        return state[0];
    }

    public static void setStatusBarColor(Activity activity){
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            mChildView.setFitsSystemWindows(true);
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);

        statusBarView.setBackgroundColor(activity.getResources().getColor(R.color.light_blue));
        decorViewGroup.addView(statusBarView);
    }

    /*public static void setStatusBarColor(Activity activity){
        Window window = activity.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(activity.getResources().getColor(R.color.light_blue));

        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }*/

    private static int getStatusBarHeight(Activity activity) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return  statusBarHeight1;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */

    public static boolean isNetworkAvailable(Activity activity) {

        if (activity == null){return  false;}
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    //Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    //Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean checkPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
        }
        return  true;
    }


    private static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static int getVersion(Activity activity){
        PackageManager pm = activity.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(activity.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = pi.versionCode;
        return  version;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
