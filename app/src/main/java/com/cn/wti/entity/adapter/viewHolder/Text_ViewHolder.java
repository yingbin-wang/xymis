package com.cn.wti.entity.adapter.viewHolder;

import android.view.View;

import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wangz on 2016/10/28.
 */
public class Text_ViewHolder {

    private View testView = null;
    public static TextView_custom title;
    public static TextView_custom content;
    TextView_custom tv1 ;
    TextView_custom tv2 ;

    public Text_ViewHolder(View view){
        this.testView = view;
    }

    public View getTestView() {
        return testView;
    }

    public void setTestView(View testView) {
        this.testView = testView;
    }

    public void reshData(){
        tv1 = (TextView_custom) testView.findViewById(R.id.item01_title);
        tv2 = (TextView_custom) testView.findViewById(R.id.item01_content);
        tv1.setText(title.getText());
        tv2.setText(content.getText());
    }
}
