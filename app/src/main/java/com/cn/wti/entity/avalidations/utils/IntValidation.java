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
package com.cn.wti.entity.avalidations.utils;

import java.util.regex.Pattern;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.entity.avalidations.ValidationExecutor;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.other.StringUtils;

/**
 * @Description: 正数验证
 * @author ken.cai
 * @date 2014-11-21 下午9:43:25 
 * @version V1.0   
 * 
 */
public class IntValidation extends ValidationExecutor {

	@Override
	public boolean doValidate(Context context,String title, String text) {

		boolean result = false;
		if (StringUtils.isNumeric(text)){
			double val_ = Double.parseDouble(text) ;
			if (val_ > 0 ){
				result =  true;
			}else{
				result  = false;
			}
		}else{
			result = false;
		}
		if (!result) {
			Toast.makeText(context, title+":"+context.getString(R.string.int_text), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;

	}

	@Override
	public boolean doValidate(Context context, View view) {
		return false;
	}
	/*^[-+]?[0-9]*\.?[0-9]+$*/
}
