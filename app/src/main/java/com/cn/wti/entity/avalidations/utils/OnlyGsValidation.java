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

import com.amap.api.maps2d.model.Text;
import com.cn.wti.entity.avalidations.ValidationExecutor;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.UITableView;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.List;

/**
 * @Description:  浮点数验证
 * @author ken.cai
 * @date 2014-11-21 下午9:43:25 
 * @version V1.0   
 * 
 */
public class OnlyGsValidation extends ValidationExecutor {

	private String gs_str = "";

	@Override
	public boolean doValidate(Context context, String title, String text) {

		return false;
	}

	@Override
	public boolean doValidate(Context context, View view) {
		Object tag = null;
		String rule = null,s=null,title=null;

		if (view instanceof EditText_custom) {
			EditText_custom v = (EditText_custom) view;
			rule = v.getRules();
			s = v.getText().toString();
			title = v.getTitle();
			tag = v.getTag();
		}else if(view instanceof TextView_custom){
				TextView_custom v = (TextView_custom) view;
				rule = v.getRules();
				s = v.getText().toString();
				title = v.getTitle();
				tag = v.getTag();
		}
		UITableView tableView = (UITableView) tag;
		if (!rule.equals("")){
			int start = rule.indexOf("gs:");
			if(start >=0){
				String gs = rule.substring(start+3,rule.length());
				if (!s.toString().equals("") && !gs.equals("")) {
					String[] gss1 = gs.split(";");
					for (String gs2 : gss1) {
						if(!eval(tableView,gs2,s)){
							Toast.makeText(context,formatStr(title,gs_str),Toast.LENGTH_SHORT).show();
							return  false;
						}
					}
				}
			}
		}
		return true;
	}

	private String formatStr(String title, String gs){
		gs = gs.replaceAll("this",title);
		if (gs.indexOf(">=")>=0){
			gs = gs.replaceAll(">="," 大于等于 ");
		}

		if (gs.indexOf(">")>=0){
			gs = gs.replaceAll(">"," 大于 ");
		}

		if (gs.indexOf("<=")>=0){
			gs = gs.replaceAll("<="," 小于等于 ");
		}

		if (gs.indexOf("<")>=0){
			gs = gs.replaceAll(">="," 小于 ");
		}

		if (gs.indexOf("==")>=0){
			gs = gs.replaceAll("=="," 等于 ");
		}

		return  gs;
	}

	private boolean eval(UITableView tableView ,String gs,String val){

		if(tableView != null){

			//默认公式 提示信息
			if (gs.indexOf("msg")>=0){
				int start1 = gs.indexOf("msg");
				gs_str = gs.substring(start1+4);
				gs = gs.substring(0,start1-1);
			}else{
				gs_str = ":规则验证失败请检查，规则为："+gs+"！";
			}

			List<IListItem>  list = tableView.getIListItem();
			BasicItem basicItem = null;
			String title = "",val_="";
			for (IListItem item:list ) {
				basicItem = (BasicItem) item;
				title = basicItem.getCode();
				if (gs.indexOf("["+title+"]") >=0){
					if (DateUtil.isDate(basicItem.getSubtitle())){
						if (basicItem.getSubtitle().length() == 10){
							val_ = DateUtil.date2TimeStamp(basicItem.getSubtitle(),"yyyy-MM-dd");
						}else{
							val_ = DateUtil.date2TimeStamp(basicItem.getSubtitle(),"yyyy-MM-dd HH:mm:ss");
						}

					}else{
						val_ = basicItem.getSubtitle();
					}

					if (val_.equals("")){val_ = "0";}
					if (!StringUtils.isNumeric(val_)){
						val_ = "'"+val_+"'";
					}
					gs = gs.replace("["+title+"]",val_);
					gs_str = gs_str.replace("["+title+"]",basicItem.getTitle());
				}
			}

			if (!gs.equals("")){
				if (DateUtil.isDate(val)){
					if (val.length() == 10 && val.length() != 0){
						val_ = DateUtil.date2TimeStamp(val,"yyyy-MM-dd");
					}else{
						val_ = DateUtil.date2TimeStamp(val,"yyyy-MM-dd HH:mm:ss");
					}
				}else{
					val_ = val;
				}
				if (!StringUtils.isNumeric(val_)){
					val_ = "'"+val_+"'";
				}
				gs = gs.replace("this",val_);
				Evaluator evaluator = new Evaluator();//创建一个对象

				try {
					/*if(gs.indexOf("-")>0){
						gs = gs.replace("-","");
					}*/

					if (gs.indexOf("[ispricecontroll]")>=0){
						gs = gs.replace("[ispricecontroll]",AppUtils.user.getIspricecontroll());
					}

					String res = evaluator.evaluate(gs);
					if(res.equals("1.0")){
						return  true;
					}else{
						return  false;
					}

				} catch (EvaluationException e) {
					e.printStackTrace();
					gs_str = "无效表达式！";
					return  false;
				}

			}
		}

		return  false;
	}

}
