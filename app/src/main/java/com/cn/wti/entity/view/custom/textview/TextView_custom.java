package com.cn.wti.entity.view.custom.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wangz on 2016/10/20.
 */
public class TextView_custom extends TextView{

    private String code;
    private String title;
    private String rules;

    public TextView_custom(Context context) {
        super(context);
    }
    public TextView_custom(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public TextView_custom(Context context, AttributeSet attrs, int defStyle){
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

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
