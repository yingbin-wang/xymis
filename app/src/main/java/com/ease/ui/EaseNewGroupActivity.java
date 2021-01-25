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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.wticn.wyb.wtiapp.R;

public class EaseNewGroupActivity extends EaseBaseActivity implements View.OnClickListener{
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox publibCheckBox;
	private CheckBox memberCheckbox;
	private TextView secondTextView,select_chengyuan,chengyuan_tv;
	String[] members;
	String[] membernames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easeui_activity_new_group);
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		publibCheckBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		secondTextView = (TextView) findViewById(R.id.second_desc);

		publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					secondTextView.setText(R.string.join_need_owner_approval);
				}else{
					secondTextView.setText(R.string.Open_group_members_invited);
				}
			}
		});

		//
		chengyuan_tv = (TextView) findViewById(R.id.chengyuan_tv);
		select_chengyuan = (TextView) findViewById(R.id.select_chengyuan);
		select_chengyuan.setOnClickListener(this);
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String name = groupNameEditText.getText().toString();
		if (!TextUtils.isEmpty(name) ) {

			if (!TextUtils.isEmpty(chengyuan_tv.getText().toString())){
				String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
				final String st2 = getResources().getString(R.string.Failed_to_create_groups);

				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

				new Thread(new Runnable() {
					@Override
					public void run() {
						final String groupName = groupNameEditText.getText().toString().trim();
						String desc = introductionEditText.getText().toString();
						try {
							EMGroupOptions option = new EMGroupOptions();
							option.maxUsers = 200;
							option.inviteNeedConfirm = true;

							if(publibCheckBox.isChecked()){
								option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
							}else{
								option.style = memberCheckbox.isChecked()?EMGroupStyle.EMGroupStylePrivateMemberCanInvite:EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
							}

							final String[] finalmembers = members;
							final String finaldesc = desc;

							runOnUiThread(new Runnable() {
								public void run() {
									String  res = saveGroup(StringUtils.arrayTostr(finalmembers),groupName,EMClient.getInstance().getCurrentUser(),finaldesc);
									if (res.indexOf("err")< 0 && !res.equals("")){
										progressDialog.dismiss();
										setResult(RESULT_OK);
										finish();
										Intent intent = new Intent(EaseNewGroupActivity.this,EaseGroupActivity.class);
										intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
										intent.putExtra(EaseConstant.EXTRA_USER_ID,res);
										intent.putExtra(EaseConstant.EXTRA_USER_NAME,groupName);
										intent.setClass(EaseNewGroupActivity.this,EaseChartActivity.class);
										startActivity(intent);
									}else{
										progressDialog.dismiss();
										setResult(RESULT_OK);
										finish();
									}
								}
							});
						} catch (final Exception e) {
							runOnUiThread(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(EaseNewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
								}
							});
						}
					}
				}).start();
			}else {
				new EaseAlertDialog(this, R.string.Group_members_cannot_be_empty).show();
			}
		}else{
			new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
		}

		/* else {
			// select from contact list
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), 0);
		}*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		if (resultCode == RESULT_OK) {
			final String groupName = groupNameEditText.getText().toString().trim();
			String desc = introductionEditText.getText().toString();
			members = data.getStringArrayExtra("newmembers");
			membernames = data.getStringArrayExtra("newmembernames");
			if (membernames != null){
				chengyuan_tv.setText(StringUtils.arrayTostrSplit(membernames,"  "));
			}
		}
		/*if (resultCode == RESULT_OK) {
			//new group
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(st1);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					final String groupName = groupNameEditText.getText().toString().trim();
					String desc = introductionEditText.getText().toString();
					String[] members = data.getStringArrayExtra("newmembers");
					try {
						EMGroupOptions option = new EMGroupOptions();
					    option.maxUsers = 200;
						option.inviteNeedConfirm = true;
					    
					    //String reason = EaseNewGroupActivity.this.getString(R.string.invite_join_group);
					    //reason  = EMClient.getInstance().getCurrentUser() + reason + groupName;
					    
						if(publibCheckBox.isChecked()){
						    option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
						}else{
						    option.style = memberCheckbox.isChecked()?EMGroupStyle.EMGroupStylePrivateMemberCanInvite:EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
						}
                        //EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);

						final String[] finalmembers = members;
						final String finaldesc = desc;

						runOnUiThread(new Runnable() {
							public void run() {
								boolean state = saveGroup(StringUtils.arrayTostr(finalmembers),groupName,EMClient.getInstance().getCurrentUser(),finaldesc);
								if (state){
									progressDialog.dismiss();
									setResult(RESULT_OK);
									finish();
									Intent intent = new Intent(EaseNewGroupActivity.this,EaseGroupActivity.class);
									startActivity(intent);
								}
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(EaseNewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}
					
				}
			}).start();
		}*/
	}

	public String saveGroup(String chengyuan,String name,String zhuguan_name,String tranmemo){

		String pars = "{\"chengyuan\":["+chengyuan+"],\"name\":\""+name+"\",\"zhuguan_name\":\""+zhuguan_name+"\",\"tranmemo\":\""+tranmemo+"\"}";
		String res = ActivityController.executeForResult2(this,"huanxin","createChatGroup",pars);
		if (!res.equals("err")){
			return  res;
		}
		return  "err";
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.select_chengyuan){
			String name = groupNameEditText.getText().toString();
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), 0);
		}
	}
}
