package com.cn.wti.activity.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseActivity;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckDn;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;

import java.util.List;
import java.util.Map;

public class Report_Cpxsrb_dialogActivity extends BaseActivity implements View.OnClickListener{

    private String service_name,method_name,pars;
    private Object res;
    private List<Map<String,Object>> _dataList;
    private View rootView;
    private ImageButton splb_select;
    private EditText splb_ed;
    private ImageButton btnOk,btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow_cpxsrb_dialog);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = findViewById(R.id.rootView);
        splb_select = (ImageButton) findViewById(R.id.splbselect);
        splb_select.setOnClickListener(this);

        splb_ed = (EditText) findViewById(R.id.splbed);
        //确定 取消 按钮
        btnOk = (ImageButton) findViewById(R.id.btnOK);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splbselect:

                service_name = "splb";
                method_name = "list";
                pars = "{pageIndex:1,start:0,limit:10,menu_code:SPLB,userId:" + AppUtils.app_username + ",user_id:" + AppUtils.app_username + "}";
                pars = StringUtils.strTOJsonstr(pars);
                res = ActivityController.getData3ByPost(service_name,method_name,pars);
                if(res != null && !res.toString().contains("(abcdef)")){

                    Map<String,Object> map = null;
                    int recordcount = 0,pageIndex = 1;
                    if (res instanceof JSONObject){
                        map = (Map<String, Object>) res;
                    }

                    if (map == null){return;}
                    recordcount = Integer.parseInt(map.get("results").toString());
                    _dataList = (List<Map<String, Object>>) map.get("rows");

                    MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(Report_Cpxsrb_dialogActivity.this,rootView,_dataList,
                            new boolean[_dataList.size()],service_name,method_name,pars,recordcount,1,"name",splb_ed,"商品类别");
                    multiChoicePopWindow.show(true);
                }

                break;
            case R.id.btnOK:
                Intent intent = new Intent();
                intent.putExtra("splb_mcs",splb_ed.getText().toString());
                setResult(1,intent);
                this.finish();
                break;
            case R.id.btnCancel:
                this.finish();
                break;
        }
    }
}
