package com.cn.wti.entity.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by wangz on 2016/10/20.
 */
public class CheckBox_custom extends CheckBox{

    private  String code;
    private String title;

    public CheckBox_custom(Context context) {
        super(context);
    }
    public CheckBox_custom(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public CheckBox_custom(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs,defStyle);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
