package com.cn.wti.util.app.qx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
/**
 * Created by wyb on 2017/8/23.
 */

public class QxUtils {

    final private static  int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static   Activity activity;
    static QxUtils qxUtils = null;

    public static QxUtils getInstance(Activity activity1){
        if (qxUtils == null){
            qxUtils = new QxUtils();
        }
        activity = activity1;
        return  qxUtils;
        //qxUtils.insertDummyContactWrapper();
    }

    public void insertDummyContactWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CAMERA},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        //自行操作
    }

    private  void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    /**
     * 读写权限 自己可以添加需要判断的权限
     */
    public String[]permissions={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };


    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限 false-表示权限已开启
     */
    public boolean lacksPermissions(Context mContexts) {
        for (String permission : permissions) {
            if (lacksPermission(mContexts,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
