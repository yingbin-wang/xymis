package com.cn.wti.activity.rwgl.myassignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseList_NoTable_Activity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListTableViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAssignment_Activity extends Activity implements View.OnClickListener{

    private ImageButton wofaqide,woderenwu,faqirenwu;
    private TextView title;
    private Context mContext;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myassignment);
        back = (ImageButton) findViewById(R.id.title_back2);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title_name2);
        title.setText("任务");

        wofaqide = (ImageButton) findViewById(R.id.wofaqide);
        woderenwu = (ImageButton) findViewById(R.id.woderenwu);
        faqirenwu = (ImageButton) findViewById(R.id.faqirenwu);

        wofaqide.setOnClickListener(this);
        woderenwu.setOnClickListener(this);
        faqirenwu.setOnClickListener(this);

        mContext = MyAssignment_Activity.this;

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.title_back2:
                this.finish();
                break;
            case R.id.woderenwu:
                intent = new Intent();
                intent.setClass(mContext,MyAssignment_wdrwActivity.class);
                startActivity(intent);
                break;
            case R.id.wofaqide:
                intent = new Intent();
                intent.setClass(mContext,MyAssignment_wfqdActivity.class);
                startActivity(intent);
                break;
            case R.id.faqirenwu:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityController.checkQx("mytaska")){
                            Intent intent = new Intent();
                            Map<String,Object> map = new HashMap<String, Object>();
                            map.put("mainData","{}");
                            map.put("type","add");
                            map.put("index","0");
                            map.put("table_postion","0");
                            intent.putExtras(AppUtils.setParms("add",map));
                            intent.setClass(mContext,Createtask_editActivity.class);
                            startActivity(intent);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,getString(R.string.error_invalid_user),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                break;
        }
    }
}