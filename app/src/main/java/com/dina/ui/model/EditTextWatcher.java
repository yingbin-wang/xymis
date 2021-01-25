package com.dina.ui.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.number.SizheTool;
import com.dina.ui.widget.UITableView;

/**
 * Created by wyb on 2016/11/6.
 */

public  class EditTextWatcher implements TextWatcher {

    private UITableView tableView;
    private String code;

    public EditTextWatcher(String code, UITableView tableView){
        this.tableView = tableView;
        this.code = code;
    }



    @Override
    public void onTextChanged(CharSequence s, int start, int before,int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        BasicItem item;

        View v  = null;

       /* if (!tableView.getEditTextValidator().validate(code)) {
            return;
        }*/

        item = ActivityController.getItem(tableView.getIListItem(),code);
        item.setSubtitle(s.toString());
        Log.v("test",s.toString());
    }
}