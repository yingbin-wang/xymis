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

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.entity.avalidations.ValidationExecutor;
import com.wticn.wyb.wtiapp.R;

import java.util.regex.Pattern;

/**
 * @Description:  浮点数验证
 * @author ken.cai
 * @date 2014-11-21 下午9:43:25 
 * @version V1.0   
 * 
 */
public class GddhValidation extends ValidationExecutor {

	@Override
	public boolean doValidate(Context context,String title, String text) {

		if (!isFixedPhone(text)) {
			Toast.makeText(context, title+":"+context.getString(R.string.gddh_text), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;

	}

	@Override
	public boolean doValidate(Context context, View view) {
		return false;
	}

	public static boolean isFixedPhone(String fixedPhone){
		String reg="(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
				"(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
		return Pattern.matches(reg, fixedPhone);
	}

}
