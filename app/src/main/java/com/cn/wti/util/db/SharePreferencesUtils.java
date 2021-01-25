package com.cn.wti.util.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wyb on 2017/7/21.
 */

public class SharePreferencesUtils {
    private static SharedPreferences sp;

    public static SharedPreferences  getInstend(Activity activity){
        if (sp == null){
            sp=activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
        return  sp;
    }

    public static  String  getVal(Activity activity,String key){
        return  getInstend(activity).getString(key,"");
    }

    public static void clear(Activity activity){
        SharedPreferences.Editor editor = getInstend(activity).edit();
        editor.remove("username");
        editor.commit();
    }
}
