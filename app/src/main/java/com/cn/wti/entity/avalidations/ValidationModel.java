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

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;

/**
 * 校验模型
 * @Description: 
 * @author ken.cai
 * @date 2014-11-21 下午9:38:40 
 * @version V1.0   
 * 
 */
public class ValidationModel {
	private View editText;
	private ValidationExecutor validationExecutor;

	public ValidationModel(View editText, ValidationExecutor validationExecutor) {
		this.editText = editText;
		this.validationExecutor = validationExecutor;
	}
	
	public View getEditText() {
		return editText;
	}

	public ValidationModel setEditText(EditText editText) {
		this.editText = editText;
		return this;
	}

	public ValidationExecutor getValidationExecutor() {
		return validationExecutor;
	}

	public ValidationModel setValidationExecutor(ValidationExecutor validationExecutor) {
		this.validationExecutor = validationExecutor;
		return this;
	}

	public boolean isTextEmpty() {

		if (editText instanceof EditText_custom){
			EditText_custom et1 = (EditText_custom) editText;
			if (editText==null||TextUtils.isEmpty(et1.getText())) {
				return true;
			}
		}else if (editText instanceof TextView_custom){
			TextView_custom et1 = (TextView_custom) editText;
			if (editText==null||TextUtils.isEmpty(et1.getText())) {
				return true;
			}
		}

		return false;
	}


}
