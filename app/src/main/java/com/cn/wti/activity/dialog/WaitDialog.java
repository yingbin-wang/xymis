package com.cn.wti.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/4/24.
 */

public class WaitDialog extends Dialog {
    private TextView waitText;

    public WaitDialog(Context context) {
        super(context, R.style.MyDialogStyle);//设置样式
        setCanceledOnTouchOutside(false);//按对话框以外的地方不起作用，按返回键可以取消对话框
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_wait_layout);
        waitText = (TextView) findViewById(R.id.tv_wait_dialog_text);
    }

    /**
     * 设置显示文字
     *
     * @param waitMsg
     */
    public void setText(CharSequence waitMsg) {
        waitText.setText(waitMsg);
    }

    /**
     * 设置文字
     *
     * @param resId
     */
    public void setText(int resId) {
        waitText.setText(resId);
    }
}