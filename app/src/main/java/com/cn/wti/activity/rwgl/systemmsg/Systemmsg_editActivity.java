package com.cn.wti.activity.rwgl.systemmsg;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.Map;

public class Systemmsg_editActivity extends BaseEdit_NoTable_Activity{


    private LayoutInflater inflater = null;
    private ImageButton title_back2;
    private TextView title;
    private Map<String,Object> parmsMap;
    private String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_systemmsg_edit;
        super.onCreate(savedInstanceState);
        initData();
        createView();

    }

    public void initData(){
        menu_code = "systemmsg";
        menu_name = "系统消息";
        mContext = Systemmsg_editActivity.this;
    }

   public void createView(){
       title_back2 = (ImageButton) findViewById(R.id.title_back2);
       title_back2.setOnClickListener(this);
       title = (TextView) findViewById(R.id.title_name2);
       title.setText("消息详情");

       Intent intent = getIntent();
       System_one so = (System_one)intent.getSerializableExtra("parms");
       if(so == null){
           return;
       }
       parmsMap = so.getParms();

       if (parmsMap!= null){
           try {
               id = parmsMap.get("id").toString();
               ywtype = parmsMap.get("type").toString();
               index = parmsMap.get("index").toString();
               if (ywtype.equals("add")){
                   main_data = new HashMap<String, Object>();
                   ActivityController.initUserMainData(main_data,initMap,AppUtils.user);
               }else{
                   if (parmsMap.get("mainData") instanceof JSONObject || parmsMap.get("mainData") instanceof Map){
                       main_data = (Map<String, Object>) parmsMap.get("mainData");
                   }else{
                       main_data = FastJsonUtils.strToMap(parmsMap.get("mainData").toString()) ;
                   }
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       reshDataUI();

   }

    public void reshDataUI() {
        //时间
        updateOneUI(main_data, "senddate");
        //内容
        updateOneUI(main_data, "msgcontent");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back2:
                //更新已阅
                if (main_data.get("islooked").toString().equals("0")){
                    final String pars = new ListParms(menu_code,"id:"+id/*+",receiver:"+AppUtils.app_username*/).getParms();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String isUpdate = ActivityController.executeForResult(mContext,menu_code,"updateIslooked", StringUtils.strTOJsonstr(pars));
                            if (!isUpdate.equals("err")){
                                Intent intent = new Intent();
                                intent.putExtra("index",index);
                                intent.putExtra("type","update");
                                main_data.put("islooked",1);
                                intent.putExtras(AppUtils.setParms("",main_data));
                                setResult(1,intent);
                                Systemmsg_editActivity.this.finish();
                            }
                        }
                    }).start();

                }else{
                    this.finish();
                }
                break;
        }

    }

}
