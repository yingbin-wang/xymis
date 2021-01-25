package com.cn.wti.util.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.cn.wti.activity.SplashActivity;
import com.cn.wti.activity.tab.MyFragmentActivity;
import com.wticn.wyb.wtiapp.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by wyb on 2017/2/8.
 */

public class TipUtils {

    private static String SYSTEM_XIAOMI="XIAOMI";
    private static String SYSTEM_SAMSUNG="SAMSUNG";
    private static String SYSTEM_HUAWEI_HONOR="HONOR";
    private static String SYSTEM_HUAWEI="HUAWEI";
    private static String SYSTEM_NOVA="NOVA";
    private static String SYSTEM_SONY="SONY";
    private static String SYSTEM_VIVO="VIVO";
    private static String SYSTEM_OPPO="OPPO";
    private static String SYSTEM_LG="LG";
    private static String SYSTEM_ZUK="ZUK";
    private static String SYSTEM_HTC="HTC";

    private static int MQTT_IM_NOTIFICATION_ID=1007;

    /**
     * 设置桌面Icon角标，当前支持小米，索尼，三星。
     * @param context
     * @param number
     */
    public static void sendBadgeNumber(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }

        Notification notification = createNotification(context);

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            setBadgeOfXiaomi(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            setBadgeOfSony(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            setBadgeOfSamsung(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("huawei")) {
            setBadgeOfHuaWei(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {
            setBadgeOfOPPO(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            setBadgeOfVIVO(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            setBadgeOfHTC(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            setBadgeOfSony(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("zuk")) {
            setBadgeOfZUK(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {
            setBadgeOfNOVA(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }
        else {
            setBadgeOfDefault(context,notification,MQTT_IM_NOTIFICATION_ID,Integer.parseInt(number));
        }

    }
    private final static String lancherActivityClassName = SplashActivity.class.getName();
    private static void sendToXiaoMi(Context context,String number) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有"+number+"未读消息");
            builder.setTicker("您有"+number+"未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.icon);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, Integer.valueOf(number));// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name",context.getPackageName() + "/"+ lancherActivityClassName );
            localIntent.putExtra("android.intent.extra.update_application_message_text",number);
            context.sendBroadcast(localIntent);
        }
        finally
        {
            if(notification!=null && isMiUIV6 )
            {
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }

    }

    private static  void sendToSony(Context context,String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",lancherActivityClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }

    private static void sendToSamsumg(Context context,String number)
    {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", Integer.valueOf(number));//数字
        localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name",lancherActivityClassName ); //启动页
        context.sendBroadcast(localIntent);
        Log.d("AppUtils","Samsumg isSendOk"+number);
    }

    /** set badge number*/
    public static void sendHuaweiNumber(Context context,String num){
        try{
            Bundle bunlde =new Bundle();
            bunlde.putString("package", "com.wticn.wyb.wtiapp");
            // com.test.badge is your package name
            bunlde.putString("class",  "com.cn.wti.activity.tab.MyFragmentActivity");
            //  com.test.  badge.MainActivity  is your apk main activity
            bunlde.putInt("badgenumber",Integer.parseInt(num));
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        }catch(Exception e){
        }
    }

    //华为 系列
    private static void setBadgeOfHuaWei(Context context, Notification notification,int NOTIFI_ID,int num) {


        try{
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", getLauncherClassName(context));
            localBundle.putInt("badgenumber", num);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);
        }catch(Exception e){
            e.printStackTrace();
            Log.e("HUAWEI" + " Badge error", "set Badge failed");
        }

    }

    //小米
    private static void setBadgeOfXiaomi(final Context context,final Notification notification,final int NOTIFI_ID,final int  num){


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    Field field = notification.getClass().getDeclaredField("extraNotification");

                    Object extraNotification = field.get(notification);

                    Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

                    method.invoke(extraNotification, num);

                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Xiaomi" + " Badge error", "set Badge failed");

                }

//                mNotificationManager.notify(0,notification);
                NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
                if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


            }
        },550);

    }

    //三星和LG
    private static void setBadgeOfSamsung(Context context,Notification notification, int NOTIFI_ID,int num) {
        // 获取你当前的应用
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", num);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("SAMSUNG" + " Badge error", "set Badge failed");
        }
    }

    //索尼
    private static void setBadgeOfSony(Context context,Notification notification,int NOTIFI_ID, int num){
        String numString="";
        String activityName = getLauncherClassName(context);
        if (activityName == null){
            return;
        }
        Intent localIntent = new Intent();
//        int numInt = Integer.valueOf(num);
        boolean isShow = true;
        if (num < 1){
            numString = "";
            isShow = false;
        }else if (num > 99){
            numString = "99";
        }

        try {
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);
            localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", activityName);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", numString);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            context.sendBroadcast(localIntent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("SONY" + " Badge error", "set Badge failed");
        }
    }

    //VIVO
    private static void setBadgeOfVIVO(Context context,Notification notification,int NOTIFI_ID,int num){
        try {
            Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            localIntent.putExtra("packageName", context.getPackageName());
            localIntent.putExtra("className", getLauncherClassName(context));
            localIntent.putExtra("notificationNum", num);
            context.sendBroadcast(localIntent);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("VIVO" + " Badge error", "set Badge failed");
        }
    }

    //OPPO *只支持部分机型
    private static void setBadgeOfOPPO(Context context, Notification notification,int NOTIFI_ID,int num){
        try {

            if (num == 0) {
                num = -1;
            }

            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("pakeageName", context.getPackageName());
            intent.putExtra("number", num);
            intent.putExtra("upgradeNumber", num);
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {

                try {
                    Bundle extras = new Bundle();
                    extras.putInt("app_badge_count", num);
                    context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);

                    NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
                    if(num!=0)
                        notifyMgr.notify(NOTIFI_ID, notification);
                    else notifyMgr.cancel(NOTIFI_ID);

                } catch (Throwable th) {
                    Log.e("OPPO" + " Badge error", "unable to resolve intent: " + intent.toString());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e("OPPO" + " Badge error", "set Badge failed");
        }
    }

    //ZUK
    private static void setBadgeOfZUK(Context context,Notification notification,int NOTIFI_ID, int num){
        final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");
        try {
            Bundle extra = new Bundle();
            extra.putInt("app_badge_count", num);
            context.getContentResolver().call(CONTENT_URI, "setAppBadgeCount", null, extra);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("ZUK" + " Badge error", "set Badge failed");
        }

    }

    //HTC
    private static void setBadgeOfHTC(Context context,Notification notification,int NOTIFI_ID,int num){

        try {
            Intent intent1 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            intent1.putExtra("com.htc.launcher.extra.COMPONENT", context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().flattenToShortString());
            intent1.putExtra("com.htc.launcher.extra.COUNT", num);

            Intent intent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intent.putExtra("packagename", context.getPackageName());
            intent.putExtra("count", num);

            if (canResolveBroadcast(context, intent1) || canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent1);
                context.sendBroadcast(intent);
            } else {
                Log.e("HTC" + " Badge error", "unable to resolve intent: " + intent.toString());
            }

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("HTC" + " Badge error", "set Badge failed");
        }

    }

    //NOVA
    private static void setBadgeOfNOVA(Context context,Notification notification,int NOTIFI_ID,int num){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", context.getPackageName()+ "/" +getLauncherClassName(context));
            contentValues.put("count", num);
            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("NOVA" + " Badge error", "set Badge failed");
        }
    }

    //其他
    private static void setBadgeOfDefault(Context context,Notification notification,int NOTIFI_ID,int num){

        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", num);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", getLauncherClassName(context));
            if (canResolveBroadcast(context, intent)) {
                context.sendBroadcast(intent);
            } else {
                Log.e("Default" + " Badge error", "unable to resolve intent: " + intent.toString());
            }

            NotificationManager notifyMgr = (NotificationManager)(context.getSystemService(NOTIFICATION_SERVICE));
            if(num!=0)notifyMgr.notify(NOTIFI_ID, notification);else notifyMgr.cancel(NOTIFI_ID);


        }catch (Exception e){
            e.printStackTrace();
            Log.e("Default" + " Badge error", "set Badge failed");
        }

    }

    //获取类名
    public static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }

    //广播
    public static boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    public static  Notification createNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setTicker("收到1条消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(((Activity)context).getBaseContext(), MyFragmentActivity.class);

        intent.setAction(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(((Activity)context).getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        builder.setContentTitle(context.getResources().getText(R.string.app_name));
        builder.setContentText("收到1条消息");

        final Notification n = builder.build();
        int defaults = Notification.DEFAULT_LIGHTS;

        defaults |= Notification.DEFAULT_SOUND;

        defaults |= Notification.DEFAULT_VIBRATE;


        n.defaults = defaults;
        n.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        return n;
    }

}
