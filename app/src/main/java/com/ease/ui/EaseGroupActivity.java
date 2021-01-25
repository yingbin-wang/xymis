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

import com.cn.wti.util.app.AppUtils;
import com.ease.widget.EaseContactList;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.wticn.wyb.wtiapp.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint({"NewApi", "Registered"})
public class EaseGroupActivity extends EaseBaseActivity implements View.OnClickListener{

    private List<Map<String,Object>> groupList;
    List<EMGroup> grouplist = null;
    private EaseContactList contactListLayout;
    private List<EaseUser> contactList;
    private ListView listView;
    private EaseTitleBar titleBar;
    private EditText query;
    private ImageButton clearSearch;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    reshView();
                    break;
            }
        }
    };

    private void reshView() {
        //groupList = (List<Map<String, Object>>) _data.get("groupList");
        try {
            //从本地加载群组列表
            //grouplist = EMClient.getInstance().groupManager().getAllGroups();
            //从服务器获取自己加入的和创建的群组列表，此api获取的群组sdk会自动保存到内存和db。
            grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

        } catch (HyphenateException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 201){
                boolean state = AppUtils.loginHuanXin(AppUtils.huanxincode,AppUtils.huanxinpassword);
                if (state){
                    try {
                        grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    } catch (HyphenateException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        contactList = getContacts();

        //初始化时需要传入群list
        contactListLayout.init(contactList, R.layout.easeui_row_group);
        //刷新列表
        contactListLayout.refresh();

    }

    private List<EaseUser> getContacts(){

        List<EaseUser> list = new ArrayList<EaseUser>();

        if (grouplist != null && grouplist.size()>0){
            for (EMGroup group:grouplist ) {
                EaseUser user = new EaseUser(group.getGroupId(),group.getMemberCount());
                user.setNick(group.getGroupName());
                user.setAvatar(group.getDescription());
                list.add(user);
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
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID,id1);
                        intent.putExtra(EaseConstant.EXTRA_USER_NAME,name);
                        intent.setClass(EaseGroupActivity.this,EaseChartActivity.class);
                        startActivity(intent);
                        EaseGroupActivity.this.finish();
                    }
                }
            });
        }

        //绑定返回事件
        titleBar = (EaseTitleBar)findViewById(R.id.title_bar);
        titleBar.setLeftImageResource(R.drawable.ease_mm_title_back);
        titleBar.setLeftLayoutClickListener(this);

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

        mHandler.sendEmptyMessageDelayed(1,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_layout){
            this.finish();
        }else if (v.getId() == R.id.search_clear){
            query.setText("");
        }
    }
}
