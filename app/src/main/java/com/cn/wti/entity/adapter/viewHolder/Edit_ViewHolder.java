package com.cn.wti.entity.adapter.viewHolder;

import android.view.View;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wangz on 2016/10/28.
 */
public class Edit_ViewHolder {

    public static TextView_custom title;
    public static EditText_custom content;
    private View view = null;

    TextView_custom tv1 ;
    EditText_custom ed2 ;

    public Edit_ViewHolder(View view){
        this.view = view;
    }

    public void reshData(){
        tv1 =  (TextView_custom) view.findViewById(R.id.item02_title);
        ed2 = (EditText_custom) view.findViewById(R.id.item02_content);
        tv1.setText(title.getText());
        ed2.setText(content.getText());
    }


}
