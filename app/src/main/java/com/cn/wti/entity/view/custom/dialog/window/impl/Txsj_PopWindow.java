package com.cn.wti.entity.view.custom.dialog.window.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.window.AbstractChoicePopWindow_simple2;
import com.wticn.wyb.wtiapp.R;

import java.util.List;

/**
 * Created by wyb on 2017/5/7.
 */

public class Txsj_PopWindow extends AbstractChoicePopWindow_simple2{

    private ImageButton mButtonOK,btnCancel;
    private EditText_custom tqdate,tqhour,tqmin;
    private String str_tqdate,str_tqhour,str_tqmin,title_text;

    public Txsj_PopWindow(Context context,Object tqdate,Object tqhour,Object tqmin,String title_text) {
        super(context);
        if (tqdate == null || tqdate.toString().equals("")){
            this.str_tqdate = "0";
        }else{
            this.str_tqdate = tqdate.toString();
        }

        if (tqhour == null || tqhour.toString().equals("")){
            this.str_tqhour = "0";
        }else{
            this.str_tqhour = tqhour.toString();
        }


        if (tqmin == null  || tqmin.toString().equals("")){
            this.str_tqmin = "10";
        }else{
            this.str_tqmin = tqmin.toString();
        }

        this.title_text = title_text;

        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        init(context);
        setPopupWindow();

    }

    @Override
    public String getTxsj(){
        str_tqdate =  tqdate.getText().toString();
        str_tqhour = tqhour.getText().toString();
        str_tqmin = tqmin.getText().toString();
        return  str_tqdate+","+str_tqhour+","+str_tqmin;
    }

    @Override
    public void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //绑定布局
        mPopView = inflater.inflate(R.layout.popwindow_txsj_norefresh, null);
        mButtonOK = (ImageButton) mPopView.findViewById(R.id.btnOK);
        mButtonOK.setOnClickListener(this);

        btnCancel = (ImageButton) mPopView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        tqdate = (EditText_custom) mPopView.findViewById(R.id.tqdate);
        tqhour = (EditText_custom) mPopView.findViewById(R.id.tqhour);
        tqmin = (EditText_custom) mPopView.findViewById(R.id.tqmin);

        tqdate.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        tqhour.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        tqmin.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        if (str_tqdate != null){
            tqdate.setText(str_tqdate);
            tqhour.setText(str_tqhour);
            tqmin.setText(str_tqmin);
        }

        if (title != null){
            title.setText(title_text);
        }
    }

}
