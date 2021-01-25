package com.cn.wti.entity.view.custom.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/8/13.
 */

public class SimplePromptMessageDialog {

    public static Dialog mDialog = null;
    private static DialogInterface.OnClickListener listener = null;
    private static String title,message;

    public  static Dialog getInstance(Context mContext,String title,String message){

        if (mContext != null){
            listener = (DialogInterface.OnClickListener) mContext;
        }

        if (mDialog == null){
            mDialog = createDialog(mContext,title,message);
        }

        return  mDialog;
    }

    static Dialog createDialog(final Context mContext,String title,String message){
        Dialog dialog=new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setIcon(R.mipmap.xuanzhuan)
                .setMessage(message)
                 //相当于点击确认按钮
                .setPositiveButton("确认",listener)
                //相当于点击取消按钮
                .setNegativeButton("取消", listener)
                .create();
        return  dialog;
    }

    public void  show(){
        if (mDialog != null){
            mDialog.show();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
