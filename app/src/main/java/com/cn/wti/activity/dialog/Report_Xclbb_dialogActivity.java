package com.cn.wti.activity.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckDn;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;

import java.util.List;
import java.util.Map;

public class Report_Xclbb_dialogActivity extends BaseActivity implements View.OnClickListener{

    private String service_name,method_name,pars;
    private Object res;
    private List<Map<String,Object>> _dataList;
    private View rootView;
    private ImageButton spdn_select,splb_select;
    private EditText spdn_ed,splb_ed;
    private ImageButton btnOk,btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        setContentView(R.layout.popwindow_xclbb_dialog);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setLogo(R.mipmap.navigationbar_back);
        rootView = findViewById(R.id.rootView);
        spdn_select = (ImageButton) findViewById(R.id.spdnselect);
        splb_select = (ImageButton) findViewById(R.id.splbselect);
        spdn_select.setOnClickListener(this);
        splb_select.setOnClickListener(this);

        splb_ed = (EditText) findViewById(R.id.splbed);
        spdn_ed = (EditText) findViewById(R.id.spdned);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_xclbbtj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_ok:
                Intent intent = new Intent();
                intent.putExtra("splb_mcs",splb_ed.getText().toString().replace(",","dh"));
                intent.putExtra("spdn_mcs",spdn_ed.getText().toString().replace(",","dh"));
                setResult(1,intent);
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
            case R.id.spdnselect:
                service_name = "goods";
                method_name = "list";
                pars = new ListParms("0","0",AppUtils.limit,service_name,"").getParms();
                pars = StringUtils.strTOJsonstr(pars);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

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
                            final int finalRecordcount = recordcount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(Report_Xclbb_dialogActivity.this,rootView,_dataList,
                                            new boolean[_dataList.size()],service_name,method_name,pars, finalRecordcount,1,"name",spdn_ed,"商品档案");
                                    multiChoicePopWindow.show(true);
                                }
                            });

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res != null){
                                        Toast.makeText(getApplicationContext(), HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                }).start();


                break;
            case R.id.splbselect:

                service_name = "goodsType";
                method_name = "list";
                pars = new ListParms("0","0",AppUtils.limit,service_name,"").getParms();
                pars = StringUtils.strTOJsonstr(pars);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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

                            final int finalRecordcount = recordcount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MultiChoicePopWindow_CheckDn multiChoicePopWindow = new MultiChoicePopWindow_CheckDn(Report_Xclbb_dialogActivity.this,rootView,_dataList,
                                            new boolean[_dataList.size()],service_name,method_name,pars, finalRecordcount,1,"name",splb_ed,"商品类别");
                                    multiChoicePopWindow.show(true);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res != null){
                                        Toast.makeText(getApplicationContext(), HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).start();


                break;
        }
    }
}
