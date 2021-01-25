package com.cn.wti.entity.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by wyb on 2016/11/1.
 */

public class Button_custom extends Button{

    private String code;

    public Button_custom(Context context) {
        super(context);
    }

    public Button_custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Button_custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnclick(OnClickListener listener){
        this.setOnClickListener(listener);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
