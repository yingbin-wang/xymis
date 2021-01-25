package com.ease.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.cn.wti.util.number.FileUtils;
import com.ease.ui.EaseNewGroupActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/9.
 */

public class HuanxinUtils {

    /**
     * 在控件位置显示 对话框
     * @param mContext
     * @param layout
     * @param view
     */
    public static void showTextViewDialog(Activity mContext, int layout, View view){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        final Display display = mContext.getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);

        MyAlertDialog myDialog = new MyAlertDialog(mContext, layout);
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = 120;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        params.gravity = Gravity.TOP;
        params.x = location[0];
        params.y = location[1]+5;
        myDialog.getWindow().setAttributes(params);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
    }

    public static Map<String,EaseUser> getContacts(List<Map<String,Object>> lxrList){

        Map<String,EaseUser> map = new HashMap<String, EaseUser>();
        if (lxrList != null && lxrList.size()>0){
            String userId = EMClient.getInstance().getCurrentUser();
            for (Map<String,Object> lxrMap:lxrList ) {
                if (!userId.equals(lxrMap.get("huanxincode").toString())){
                    EaseUser user = new EaseUser(lxrMap.get("huanxincode").toString());
                    user.setNick(lxrMap.get("name").toString());
                    if (lxrMap.get("photo_16") != null){
                        user.setAvatar(lxrMap.get("photo_16").toString().replace("data:image/png;base64,",""));
                    }
                    map.put(user.getUsername(),user);
                }
            }
        }
        return  map;
    }

    /**
     * 通过用户名找到用户
     * @param username
     * @return
     */
    public static EaseUser getUserByCode(String username){

        return  EaseUserUtils.getUserInfo(username);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public static boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

}
