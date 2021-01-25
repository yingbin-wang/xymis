/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ease.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.DatabaseUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.System_one;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.wticn.wyb.wtiapp.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"NewApi", "Registered"})
public class EaseUserActivity extends EaseBaseActivity implements View.OnClickListener{

    private List<Map<String,Object>> lxrList;
    private EaseContactList contactListLayout;
    private List<EaseUser> contactList;
    private ListView listView;
    private EaseTitleBar titleBar;
    private EditText query;
    private ImageButton clearSearch;
    private Dialog mDialog;
    private boolean state =false;
    protected static PageDataSingleton _catch = PageDataSingleton.getInstance();

  /*  private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    reshView();
                    break;
                case 2:
                    getLxrList();
                    reshView();
                    WeiboDialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };*/

    public Map<String,Object> getLxrList(){
        List<Map<String,Object>> lxrList = null,groupList = null;

        if (_catch != null && _catch.get("lxrList") == null){
            AppUtils.dbUtils = DatabaseUtils.getInstance(this,"wtmis.db");
            lxrList = AppUtils.dbUtils.exec_select("sys_user",new String[]{"address","login_name","name","huanxincode","huanxinpassword","photo_16"},"address = ?",new String[]{AppUtils.app_address},null,null,"pinyin");
            if (lxrList == null || lxrList.size() == 0){
                state = true;
                String pars = new ListParms("0","0","10000","zydn","isDataQxf:1").getParms();
                Object res = ActivityController.getObjectData2ByPost(this,"zydn","list", StringUtils.strTOJsonstr(pars));

                if (res != null && res instanceof JSONArray){
                    lxrList = (List<Map<String, Object>>) res;
                    _catch.remove("lxrList");
                    _catch.put("lxrList",lxrList);
                }
            }else{
                _catch.remove("lxrList");
                _catch.put("lxrList",lxrList);
            }

        }

        if (_catch != null && _catch.get("groupList") == null){
            String pars = new ListParms("0","0","10000","bmdn","isDataQxf:1").getParms();
            Object res = ActivityController.getObjectData2ByPost(this,"bmdn","list", StringUtils.strTOJsonstr(pars));

            if (res != null && res instanceof JSONArray){
                lxrList = (List<Map<String, Object>>) res;
                _catch.remove("groupList");
                _catch.put("groupList",lxrList);
            }
        }

        Map<String,Object> resMap = new HashMap<String, Object>();

        if (_catch.get("lxrList") != null &&  _catch.get("groupList") != null){
            resMap.put("lxrList",_catch.get("lxrList"));
            resMap.put("groupList",_catch.get("groupList"));
        }

        return  resMap;
    }

    /**
     * 刷新用户
     * @return
     */
    public Map<String,Object> refreshLxrList(){
        List<Map<String,Object>> lxrList = null,groupList = null;
        state = true;
        String pars = new ListParms("0","0","10000","zydn","isDataQxf:1",1).getParms();
        Object res = ActivityController.getObjectData2ByPost(this,"zydn","list", StringUtils.strTOJsonstr(pars));

        if (res != null && res instanceof JSONArray){
            lxrList = (List<Map<String, Object>>) res;
            _catch.remove("lxrList");
            _catch.put("lxrList",lxrList);
        }

        pars = new ListParms("0","0","10000","bmdn","isDataQxf:1",1).getParms();
        res = ActivityController.getObjectData2ByPost(this,"bmdn","list", StringUtils.strTOJsonstr(pars));

        if (res != null && res instanceof JSONArray){
            lxrList = (List<Map<String, Object>>) res;
            _catch.remove("groupList");
            _catch.put("groupList",groupList);
        }

        Map<String,Object> resMap = new HashMap<String, Object>();

        if (_catch.get("lxrList") != null &&  _catch.get("groupList") != null){
            resMap.put("lxrList",_catch.get("lxrList"));
            resMap.put("groupList",_catch.get("groupList"));
        }

        return  resMap;
    }

    private void reshView() {
        lxrList = (List<Map<String, Object>>) _catch.get("lxrList");
        contactList = getContacts();

        //初始化时需要传入联系人list
        contactListLayout.init(contactList);
        //刷新列表
        contactListLayout.refresh();

    }

    private List<EaseUser> getContacts(){

        List<EaseUser> list = new ArrayList<EaseUser>();

        if (lxrList != null && lxrList.size()>0){
            String userId = EMClient.getInstance().getCurrentUser();
            for (Map<String,Object> lxrMap:lxrList ) {
                if (!userId.equals(lxrMap.get("huanxincode").toString())){
                    EaseUser user = new EaseUser(lxrMap.get("huanxincode").toString());
                    user.setNick(lxrMap.get("name").toString());
                    if (lxrMap.get("photo_16") != null){
                        user.setAvatar(lxrMap.get("photo_16").toString().replace("data:image/png;base64,",""));
                    }

                    list.add(user);
                }
            }
        }
        return  list;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.easeui_fragment_contact_list);

        contactListLayout = (EaseContactList) findViewById(R.id.contact_list);

        listView = contactListLayout.getListView();
        if (listView != null){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EaseUser user = (EaseUser)listView.getItemAtPosition(position);
                    String id1 = user.getUsername();
                    String name = user.getNick();
                    if (id1 != null && !id1.equals("")){
                        Intent intent = new Intent();
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID,id1);
                        intent.putExtra(EaseConstant.EXTRA_USER_NAME,name);
                        intent.setClass(EaseUserActivity.this,EaseChartActivity.class);
                        startActivity(intent);
                        EaseUserActivity.this.finish();
                    }
                }
            });
        }

        //绑定返回事件
        titleBar = (EaseTitleBar)findViewById(R.id.title_bar);
        titleBar.setLeftImageResource(R.drawable.ease_mm_title_back);
        titleBar.setLeftLayoutClickListener(this);

        //添加刷新动作
        titleBar.setRightImageResource(R.mipmap.refresh);
        titleBar.setRightLayoutClickListener(this);

        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactListLayout.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        clearSearch.setOnClickListener(this);

        getLxrList();
        reshView();

/*        mDialog = WeiboDialogUtils.createLoadingDialog(this, "");
        mHandler.sendEmptyMessageDelayed(2,10);*/
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_layout){
            this.finish();
        }else if (v.getId() == R.id.right_layout){
            updateUser();
        }else if (v.getId() == R.id.search_clear){
            query.setText("");
        }
    }

    public void updateUser(){
        mDialog = WeiboDialogUtils.createLoadingDialog(this, "数据准备中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(refreshLxrList()!= null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //username varchar(100),name varchar(40),huanxincode varchar(100),huanxinname varchar(100),photo text,
                            if (state){
                                //更新 和 插入
                                AppUtils.dbUtils.execSql2("sys_user", (List<Map<String, Object>>) _catch.get("lxrList"),"login_name,name,huanxincode,huanxinpassword,photo_16,"+AppUtils.app_address+",8594584","login_name,name,huanxincode,huanxinpassword,photo_16,address,pinyin");
                                //删除
                                AppUtils.dbUtils.exec_delete("sys_user"," address = ? and login_name not in (?)", AppUtils.app_address+","+ FastJsonUtils.ListMapToStr((List<Map<String, Object>>) _catch.get("lxrList"),"login_name","<dh>"));
                                reshView();
                            }
                            WeiboDialogUtils.closeDialog(mDialog);
                            Toast.makeText(getApplicationContext(),"刷新完成",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
