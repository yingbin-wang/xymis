package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.dina.ui.widget.UITableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseEdit_02Activity extends BaseEditActivity{

    protected  Map<String,String> initMap;
    private String pars;
    private ActionBar actionBar;
    protected ImageButton btn_back,btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);*/
        super.onCreate(savedInstanceState);
        /*getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.layout_title_02);*/
        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.layout_title_02);
        //设置标题
        main_title = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
        main_title.setText("编辑明细");

        //返回按钮
        btn_back = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);

        btn_ok = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_ok2);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);
    }

}
