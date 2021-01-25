package com.cn.wti.entity.view.custom.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

/* ****************************************
 *                                       *
 *  @dookay.com Internet make it happen *
 *  ----------- -----------------------  *
 *  dddd  ddddd Internet make it happen  *
 *  o    o    o Internet make it happen  *
 *  k    k    k Internet make it happen  *
 *  a    a    a Internet make it happen  *
 *  yyyy  yyyyy Internet make it happen  *
 *  ----------- -----------------------  *
 *  @dookay.com Internet make it happen *
 *                                       *
 *************************************** */
/*
 * @author：WangXu
 * @date：2018/6/5  下午3:10
 * @describe:
 */
@SuppressLint("ValidFragment")
public class AlertDialog extends DialogFragment implements View.OnClickListener {

    String                     content   = "";
    String                     title     = "";
    DialogClick dialogClick;
    boolean     mIsBind;
    Integer type = 0;

    public AlertDialog(String title,String content, DialogClick dialogClick) {
        this.title =title;
        this.content = content;
        this.dialogClick = dialogClick;
    }

    public AlertDialog(String title,String content,Integer type, DialogClick dialogClick) {
        this.title =title;
        this.content = content;
        this.dialogClick = dialogClick;
        this.type = type;
    }

    Activity activity;

    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }


    int current = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //添加这一行
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout view = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_alert_usertip, null);

        if (type  == 1) {
            TextView tvLeft = view.findViewById(R.id.tv_left);
            TextView tvRight = view.findViewById(R.id.tv_right);
            tvRight.setVisibility(View.GONE);
            View line = view.findViewById(R.id.line);
            line.setVisibility(View.GONE);
            tvLeft.setText("确定");
        }

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        TextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText(Html.fromHtml(content));

        view.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClick != null) {
                    dialogClick.leftClick();
                    AlertDialog.this.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClick != null) {
                    dialogClick.rightCLick();
                    AlertDialog.this.dismiss();
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public interface DialogClick {
        void leftClick();

        void rightCLick();
    }
}
