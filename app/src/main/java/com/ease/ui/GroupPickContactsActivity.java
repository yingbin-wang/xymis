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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.Constant;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GroupPickContactsActivity extends EaseBaseActivity {
	/** if this is a new group */
	protected boolean isCreatingNewGroup;
	private PickContactAdapter contactAdapter;
	/** members already in the group */
	private List<String> existMembers;
	private List<Map<String,Object>> lxrList;
	protected static PageDataSingleton _catch = PageDataSingleton.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ease_activity_group_pick_contacts);

		String groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// create new group
			isCreatingNewGroup = true;
		} else {
			// get members of the group
			EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
			existMembers = group.getMembers();
			existMembers.add(group.getOwner());
			existMembers.addAll(group.getAdminList());
		}
		if(existMembers == null)
			existMembers = new ArrayList<String>();
		// get contact list
		final List<EaseUser> alluserList = new ArrayList<EaseUser>();
		//联系人列表
		lxrList = (List<Map<String, Object>>) _catch.get("lxrList");
		if (lxrList == null){return;}
		for (Map<String,Object> userMap: lxrList) {
			String username = userMap.get("huanxincode").toString();
			if (!username.equals(Constant.NEW_FRIENDS_USERNAME) & !username.equals(Constant.GROUP_USERNAME) & !username.equals(Constant.CHAT_ROOM) & !username.equals(Constant.CHAT_ROBOT)){
				EaseUser user = new EaseUser(userMap.get("huanxincode").toString());
				user.setNick(userMap.get("name").toString());
				user.setAvatar(userMap.get("photo_16").toString());
				alluserList.add(user);
			}
		}
		// sort the list
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNick().compareTo(rhs.getNick());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
                
            }
        });

		ListView listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this, R.layout.ease_row_contact_with_checkbox, alluserList);
		listView.setAdapter(contactAdapter);
		((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();

			}
		});
	}

	/**
	 * save selected members
	 * 
	 * @param v
	 */
	public void save(View v) {
		List<String> var = getToBeAddMembers();
		List<String> memberNames = getToBeAddMemberNames();
		Intent intent = new Intent()
				.putExtra("newmembers", var.toArray(new String[var.size()]))
				.putExtra("newmembernames",memberNames.toArray(new String[memberNames.size()]));
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * get selected members
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> members = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			//别名 2017-6-9
			String username = contactAdapter.getItem(i).getUsername();
			if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
				members.add(username);
			}
		}

		return members;
	}

	private List<String> getToBeAddMemberNames() {
		List<String> members = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			//别名 2017-6-9
			String username = contactAdapter.getItem(i).getNick();
			if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
				members.add(username);
			}
		}

		return members;
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends EaseContactAdapter {

		private boolean[] isCheckedArray;

		public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			final String username = getItem(position).getUsername();
			EaseUser user = getItem(position);

			final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
			ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
			TextView nameView = (TextView) view.findViewById(R.id.name);

			if (avatarView != null){
				avatarView.setImageBitmap(AppUtils.convertStringToIconBase64(user.getAvatar()));
			}

			if (nameView != null){
				nameView.setText(user.getNick());
			}

			if (checkBox != null) {
			    if(existMembers != null && existMembers.contains(username)){
                    checkBox.setButtonDrawable(R.drawable.ease_checkbox_bg_gray_selector);
                }else{
                    checkBox.setButtonDrawable(R.drawable.selector_checkbox1);
                }

				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// check the exist members
						if (existMembers.contains(username)) {
								isChecked = true;
								checkBox.setChecked(true);
						}
						isCheckedArray[position] = isChecked;

					}
				});
				// keep exist members checked
				if (existMembers.contains(username)) {
						checkBox.setChecked(true);
						isCheckedArray[position] = true;
				} else {
					checkBox.setChecked(isCheckedArray[position]);
				}
			}

			return view;
		}
	}

	public void back(View view){
		finish();
	}
	
}
