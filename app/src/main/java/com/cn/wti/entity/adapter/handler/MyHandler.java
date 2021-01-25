package com.cn.wti.entity.adapter.handler;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/5/31.
 */

public class MyHandler extends Handler {
    private Dialog dialog;
    private Context mContext;
    private Fragment fragment;
    private Object res;

    public MyHandler(Context mContext){
        this.mContext = mContext;
    }

    public MyHandler(Looper looper,Context mContext) {
        super(looper);
        this.mContext = mContext;
    }

    public MyHandler(Looper looper,Fragment fragment) {
        super(looper);
        this.fragment = fragment;
    }

    public MyHandler(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                try {
                    res = ReflectHelper.callMethod2(mContext,"createView",null,String.class);
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                try {
                    res = ReflectHelper.callMethod2(mContext,"saveOrEdit",null,String.class);
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    WeiboDialogUtils.closeDialog(dialog);
                }

                break;
            case 3:
                try {
                    res = ReflectHelper.callMethod2(mContext,"deleteAll",null,String.class);
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    WeiboDialogUtils.closeDialog(dialog);
                }

                break;
            case 4:
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    Bundle b = msg.getData();
                    String pars = b.getString("pars");
                    res = ReflectHelper.callMethod2(mContext,"noSpAudit",new Object[]{pars},String.class);
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    WeiboDialogUtils.closeDialog(dialog);
                }

                break;
            case 5:
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    Bundle b = msg.getData();
                    String pars = b.getString("pars");
                    String method = b.getString("method");
                    res = ReflectHelper.callMethod2(mContext,"audit",new Object[]{mContext,method,pars},new Class[]{Context.class,String.class,String.class});
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    WeiboDialogUtils.closeDialog(dialog);
                }

                break;
            case 6:
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(fragment,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 7:
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 8://关闭对话框并执行setResult方法
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                    res = ReflectHelper.callMethod2(mContext,"setResult",null,String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 9://关闭对话框并关闭当前视图
                try {
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                    Activity activity = (Activity) mContext;
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 10://关闭对话框并执行reshData方法
                try {
                    res = ReflectHelper.callMethod2(mContext,"reshData",null,String.class);
                    Activity activity = (Activity) mContext;
                    boolean state = (boolean) res;
                    if (state){
                        Toast.makeText(mContext,activity.getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                    }
                    dialog = (Dialog) ReflectHelper.getValueByFieldName(mContext,"mDialog");
                    WeiboDialogUtils.closeDialog(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 11://关闭对话框并执行reshData方法
                try {
                    Bundle b = msg.getData();
                    String pars = b.getString("pars");
                    res = ReflectHelper.callMethod2(mContext,"reshData",new Object[]{pars},String.class);
                    Activity activity = (Activity) mContext;
                    boolean state = (boolean) res;
                    if (state){
                        Toast.makeText(mContext,activity.getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
