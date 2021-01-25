package com.cn.wti.activity.fwq_address;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.DatabaseUtils;

import java.util.Map;

public class FwqAddress_addActivity extends Activity {

    private EditText fwq_add_name,fwq_add_address;
    private DatabaseUtils dbUtils = null;
    private ImageButton ib_back,ib_ok;
    private TextView tv_title;
    private Map<String,Object> parmsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_fwq_address_add);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.layout_title_02);

        ib_back = (ImageButton) findViewById(R.id.title_back2);
        ib_ok = (ImageButton)findViewById(R.id.title_ok2);
        tv_title = (TextView)findViewById(R.id.title_name2);

        ib_back.setOnClickListener(new ImageButtonClickListener());
        ib_ok.setOnClickListener(new ImageButtonClickListener());

        tv_title.setText("操作服务器地址");

        fwq_add_address = (EditText) findViewById(R.id.fwq_add_address);
        fwq_add_name = (EditText)findViewById(R.id.fwq_add_name);
        
        refshData();

    }

    /**
     * 获取编辑数据
     */
    private void refshData() {
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();
        String name = AppUtils.getMapVal(parmsMap,"name").toString();
        String address = AppUtils.getMapVal(parmsMap,"address").toString();
        String type = AppUtils.getMapVal(parmsMap,"type").toString();

        if(type.equals("edit") ){
            fwq_add_address.setText(address) ;
            fwq_add_name.setText(name);
        }
    }

    private  class ImageButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.title_back2:
                    FwqAddress_addActivity.this.finish();
                    break;
                case  R.id.title_ok2:
                    String address = fwq_add_address.getText().toString();
                    String name = fwq_add_name.getText().toString();
                    if(address.equals("") || name.equals("")){
                        Toast.makeText(FwqAddress_addActivity.this,"服务器名称或服务器地址为空",Toast.LENGTH_LONG);
                        return;
                    }
                    //执行添加动作
                    long updates = saveFwqAddress(name,address);
                    if(updates == 0){
                        Toast.makeText(FwqAddress_addActivity.this,"添加失败",Toast.LENGTH_LONG);
                    }else{
                        setResult(1, new Intent());
                        FwqAddress_addActivity.this.finish();
                    }

                    break;
                default:
                    break;
            }
        }
    }

    private long saveFwqAddress(String name,String address){

        dbUtils  = DatabaseUtils.getInstance(FwqAddress_addActivity.this, "wtmis.db");
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("address",address);
        String type = "";
        if(parmsMap!= null){
            type = AppUtils.getMapVal(parmsMap,"type").toString();
        }else{
            type = "";
        }
        if (type.equals("edit") ){
            String id = AppUtils.getMapVal(parmsMap,"id").toString();
            return  dbUtils.exec_update("sys_fwqaddress","name,address",name+","+address," id = ?",id);
        }else {
            return dbUtils.exec_insert("sys_fwqaddress", cv);
        }
    }
}
