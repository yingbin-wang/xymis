/**
 *  Copyright 2014 ken.cai (http://www.shangpuyun.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *
 */
package com.cn.wti.entity.avalidations;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;

/**
 * EditText校验器
 * 
 * @author ken.cai
 * 
 */
public class EditTextValidator {

	public ArrayList<ValidationModel> getValidationModels() {
		return validationModels;
	}

	public void setValidationModels(ArrayList<ValidationModel> validationModels) {
		this.validationModels = validationModels;
	}

	private ArrayList<ValidationModel> validationModels;

	private View button;

	private Context context;

	public EditTextValidator(Context context) {
		init(context, null);
	}

	public EditTextValidator(Context context, View button) {

		init(context, button);
	}

	private void init(Context context, View button) {
		this.context = context;
		this.button = button;
		validationModels = new ArrayList<ValidationModel>();
	}

	/**
	 * 
	 * 设置button，支持各种有点击事件的view
	 * 
	 * @param button
	 * @return
	 */
	public EditTextValidator setButton(View button) {
		this.button = button;
		return this;
	}

	public EditTextValidator add(ValidationModel validationModel) {
		validationModels.add(validationModel);
		return this;
	}

	public EditTextValidator execute() {
		for (final ValidationModel validationModel : validationModels) {
			if (validationModel.getEditText() == null) {
				return this;
			}

			View view = validationModel.getEditText();

			if (view instanceof TextView_custom){
				TextView_custom tv1 = (TextView_custom) view;
				tv1.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						setEnabled();
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}else if (view instanceof  EditText_custom){
				EditText_custom et1 = (EditText_custom) view;
				et1.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						setEnabled();
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}

		}

		setEnabled();

		return this;
	}

	private void setEnabled() {
		for (final ValidationModel validationModel : validationModels) {
			if (button != null) {

				if (validationModel.isTextEmpty()) {// 如果有一个是空的，button直接不可点击
					button.setEnabled(false);
					return;
				} else {
					if (!button.isEnabled()) {
						button.setEnabled(true);
					}
				}
			}
		}

	}

	public boolean validate() {
		for (ValidationModel validationModel : validationModels) {
			if (validationModel.getValidationExecutor() == null || validationModel.getEditText() == null) {
				// 如果没有验证处理器，直接返回正确
				return true;
			}
			EditText_custom et_custom = null;
			TextView_custom tv_custom = null;
			View view = validationModel.getEditText();

			if(view instanceof EditText_custom){
				et_custom  = (EditText_custom) view;
				if (et_custom.getRules()!= null && et_custom.getRules().indexOf("gs")>=0){
					if (!validationModel.getValidationExecutor().doValidate(context,et_custom)) {
						return false;
					}
				}else{
					if (!validationModel.getValidationExecutor().doValidate(context,et_custom.getTitle(), et_custom.getText().toString())) {
						// 如果需要做单个EditText验证不通过标记，可以在这里实现
						return false;// 只要有不通过的直接返回false，不要往下执行了
					}
				}
			}else if (view instanceof TextView_custom){
				tv_custom  = (TextView_custom) view;
				if (tv_custom.getRules()!= null && tv_custom.getRules().indexOf("gs")>=0){
					if (!validationModel.getValidationExecutor().doValidate(context,tv_custom)) {
						return false;
					}
				}else{
					if (!validationModel.getValidationExecutor().doValidate(context,tv_custom.getTitle(), tv_custom.getText().toString())) {
						// 如果需要做单个EditText验证不通过标记，可以在这里实现
						return false;// 只要有不通过的直接返回false，不要往下执行了
					}
				}
			}

			/*if (!validationModel.getValidationExecutor().doValidate(context,text_custom.getTitle(), text_custom.getText().toString())) {

				// 如果需要做单个EditText验证不通过标记，可以在这里实现

				return false;// 只要有不通过的直接返回false，不要往下执行了
			}*/
		}
		return true;
	}


	public boolean validate(String code) {
		for (ValidationModel validationModel : validationModels) {
			if (validationModel.getValidationExecutor() == null || validationModel.getEditText() == null) {
				// 如果没有验证处理器，直接返回正确
				return true;
			}
			EditText_custom text_custom = null;

			if(validationModel.getEditText() instanceof EditText_custom){
				text_custom  = (EditText_custom) validationModel.getEditText();
			}

			if(!text_custom.getCode().equals(code) ){
				continue;
			}

			if (text_custom.getRules()!= null && text_custom.getRules().indexOf("gs")>=0){
				if (!validationModel.getValidationExecutor().doValidate(context,text_custom)) {
					return false;
				}
			}else{
				if (!validationModel.getValidationExecutor().doValidate(context,text_custom.getTitle(), text_custom.getText().toString())) {
					// 如果需要做单个EditText验证不通过标记，可以在这里实现
					return false;// 只要有不通过的直接返回false，不要往下执行了
				}
			}


		}
		return true;
	}

	public View getButton() {
		return button;
	}

}
