package com.cn.wti.activity.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

/**
 * Created by ASUS on 2019/7/12.
 */
@SuppressLint("ValidFragment")
public class FileSelectDialog extends DialogFragment {

    LinearLayout iv_title;
    LinearLayout iv_photo;
    LinearLayout iv_select;
    LinearLayout iv_cancel;
    LinearLayout ll_delete;
    private Handler handler;
    private String title,one,two;
    private DialogClick dialogClick;
    private TextView titleOne,titleTwo;
    private Boolean isShowOne,isShowTwo,isShowThree;

    View cView;

    public FileSelectDialog(String title, DialogClick dialogClick) {
        this.dialogClick = dialogClick;
        this.title = title;
    }

    public FileSelectDialog(String selectOne, String selectTwo, boolean isShowOne, boolean isShowTwo, boolean isShowThree, DialogClick dialogClick) {
        this.dialogClick = dialogClick;
        this.one = selectOne;
        this.two = selectTwo;
        this.isShowOne = isShowOne;
        this.isShowTwo = isShowTwo;
        this.isShowThree =isShowThree;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cView = inflater.inflate(R.layout.dialog_tip_select, container);
        setStyle(R.style.Animation_Bottom_Dialog,R.style.loading_dialog);
        initView(cView);
        return cView;
    }

    void initView(View view){
        iv_title = view.findViewById(R.id.layout_title);
        iv_photo = view.findViewById(R.id.iv_photo);
        iv_select = view.findViewById(R.id.iv_select);
        iv_cancel = view.findViewById(R.id.iv_cancel);
        titleOne = view.findViewById(R.id.title_one);
        titleTwo = view.findViewById(R.id.title_two);
        ll_delete = view.findViewById(R.id.ll_delete);

        if (this.title == null || this.title.equals("")){
            iv_title.setVisibility(View.GONE);
        }else{
            iv_title.setVisibility(View.VISIBLE);
            TextView textView = iv_title.findViewById(R.id.tv_title);
            textView.setText(title);
        }

        if (!TextUtils.isEmpty(one)){
            titleOne.setText(one);
        }

        if (!TextUtils.isEmpty(two)){
            titleTwo.setText(two);
        }

        if (isShowOne!=null && !isShowOne){
            iv_photo.setVisibility(View.GONE);
        }

        if (isShowTwo!=null && !isShowTwo){
            iv_photo.setVisibility(View.GONE);
        }

        if(isShowThree!=null && isShowThree){
            ll_delete.setVisibility(View.VISIBLE);
        }

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClick != null) {
                    dialogClick.photographClick();
                    FileSelectDialog.this.dismiss();
                }
            }
        });

        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClick != null) {
                    dialogClick.selectCLick();
                    FileSelectDialog.this.dismiss();
                }
            }
        });

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSelectDialog.this.dismiss();
            }
        });

        //删除
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogClick!=null)
                    dialogClick.deleteItem();
                FileSelectDialog.this.dismiss();
            }
        });

        Window window = getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.dimAmount = 0.8f;
        attributes.gravity = Gravity.BOTTOM;
        window.setAttributes(attributes);

    }

    public interface DialogClick {
        void photographClick();

        void selectCLick();

        void deleteItem();
    }

}
