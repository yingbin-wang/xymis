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
import android.widget.ListView;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseContactList;
import com.wticn.wyb.wtiapp.R;
import java.util.List;
import java.util.Map;

@SuppressLint({"NewApi", "Registered"})
public class EaseChartActivity extends EaseBaseActivity {

    private List<Map<String,Object>> lxrList;
    private EaseContactList contactListLayout;
    private List<EaseUser> contactList;
    private ListView listView;
    private android.support.v4.app.FragmentManager manager;
    private android.support.v4.app.FragmentTransaction tran;
    public  static EaseChartActivity mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.ease_layout_list);

        mContext = EaseChartActivity.this;

        Intent intent = getIntent();
        int type = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE,0);
        String id = intent.getStringExtra(EaseConstant.EXTRA_USER_ID);
        String name = intent.getStringExtra(EaseConstant.EXTRA_USER_NAME);
        EaseChatFragment chatFragment = new EaseChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, type);
        args.putString(EaseConstant.EXTRA_USER_ID, id);
        args.putString(EaseConstant.EXTRA_USER_NAME, name);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, chatFragment).commit();
    }
}
