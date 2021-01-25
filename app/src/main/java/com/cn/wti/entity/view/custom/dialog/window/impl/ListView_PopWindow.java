package com.cn.wti.entity.view.custom.dialog.window.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.window.AbstractChoicePopWindow_simple2;
import com.wticn.wyb.wtiapp.R;

import java.util.List;

/**
 * Created by wyb on 2017/5/7.
 */

public class ListView_PopWindow extends AbstractChoicePopWindow_simple2{

    private ImageButton mButtonOK;
    private String title_text;

    public ListView_PopWindow(Context context,List<String> mList,String title_text) {
        super(context);
        this.mList = mList;
        this.title_text = title_text;
        init(context);
        setPopupWindow();
    }

    @Override
    public void init(Context context) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        //绑定布局
        setLayout_(R.layout.popwindow_selectlistview_norefresh);
        super.init(context);

        if (title != null){
            title.setText(title_text);
        }

    }

}
