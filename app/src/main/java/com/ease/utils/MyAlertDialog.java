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
package com.ease.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.ease.ui.EaseGroupActivity;
import com.ease.ui.EaseNewGroupActivity;
import com.ease.ui.EaseUserActivity;
import com.wticn.wyb.wtiapp.R;

public class MyAlertDialog extends Dialog implements View.OnClickListener{


	private String title;
	private Bundle bundle;
	private boolean showCancel = false;
	private Context mContext;

	public MyAlertDialog(Context context, int msgId) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.mContext = context;
		this.setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.easeui_my_dialog);

		//单聊 群聊 创建群
		Button faqidanliao = (Button) findViewById(R.id.faqidanliao);
		Button faqiqunliao = (Button) findViewById(R.id.faqiqunliao);
		Button createqun = (Button) findViewById(R.id.createqun);

		faqidanliao.setOnClickListener(this);
		faqiqunliao.setOnClickListener(this);
		createqun.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.faqidanliao) {
			Intent intent = new Intent();
            intent.setClass(getContext(),EaseUserActivity.class);
			((Activity)mContext).startActivityForResult(intent,1);
			this.dismiss();
		} else if (view.getId() == R.id.faqiqunliao) {
			Intent intent = new Intent();
			intent.setClass(getContext(),EaseGroupActivity.class);
			((Activity)mContext).startActivityForResult(intent,1);
			this.dismiss();
		}else if(view.getId() == R.id.createqun){
			Intent intent = new Intent();
			intent.setClass(getContext(),EaseNewGroupActivity.class);
			((Activity)mContext).startActivityForResult(intent,1);
			this.dismiss();
		}else if (view.getId() == R.id.clear){

		}
	}

}
