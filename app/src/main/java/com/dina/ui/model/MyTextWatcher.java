package com.dina.ui.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.UITableView;

import java.util.Map;

/**
 * Created by wyb on 2016/11/6.
 */

public  class MyTextWatcher implements TextWatcher {

    private String[] text_custom;
    private String gs;
    private UITableView tableView;
    private String result,result_copy,precision;
    private String code,preVal=""/*上一次值*/,afterVal=""/*修改之后*/;
    private Map<String,Object> action_map = null;
    private View mainView = null;
    private boolean isEdit = false,isJisuan = true;

    public  MyTextWatcher(String code,String[] text_custom,String gs,UITableView tableView){
        this.text_custom = text_custom;
        this.gs = gs;
        this.tableView = tableView;
        this.code = code;
        try {
            mainView = (View) ReflectHelper.getValueByFieldName(tableView.getContext1(),"main_form");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public  MyTextWatcher(String code,Map<String,Object> action_map,String gs,UITableView tableView){
        this.action_map = action_map;
        this.gs = gs;
        this.tableView = tableView;
        this.code = code;
        try {
            mainView = (View) ReflectHelper.getValueByFieldName(tableView.getContext1(),"main_form");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public  MyTextWatcher(String code,UITableView tableView){
        this.tableView = tableView;
        this.code = code;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,int count) {
        /*Log.v("test","1");*/
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        preVal = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.v("test","3");
        afterVal = s.toString();
        //验证 是否 输入
        if (!isEdit){return;}
        //是否 一致
        if (isCompare()){return;}
        //为空直接 返回 20171217 为空时清空 设置 为0 重新计算 其他值
        if (s.toString().equals("")){
            /*if (code != null && tableView != null) {
                BasicItem item = ActivityController.getItem(tableView.getIListItem(),code);
                item.setSubtitle("0");
                View v  = tableView.getLayoutList(item.getIndex());
                tableView.setupBasicItemValue(v,item,item.getIndex());
            }*/
            //重新 计算公式
            jisuan("0");
            return;
        }
        /*//验证数据
        if (!tableView.getEditTextValidator().validate(code)) {
            return;
        }*/


        //执行 修改 和 更新 其他动作
        if (action_map != null && action_map.get("change_code").equals(code)){
            //20171213 wang 验证 是否 计算 chang 与 update 动作
            if (action_map.get("validate")!= null){
                Map<String,Object> validateMap = (Map<String, Object>) action_map.get("validate");
                if (validateMap.get("from") != null){
                    String res = ActivityController.computeExpression(validateMap,tableView.getContext1(),tableView);
                    if (res.equals("1.00")){
                        isJisuan = true;
                    }else{
                        isJisuan = false;
                    }
                }
            }else{
                isJisuan = true;
            }
            if (isJisuan && action_map.get("change")!= null){
                /**
                 * change 事件处理
                 */
                if(action_map != null) {
                    //如果既有change 又有 update 先执行 change 后执行 update 然后执行 销售政策
                    if (action_map.get("change") != null && action_map.get("update") != null){
                        //执行change 动作
                        Map<String, Object> change_map = (Map<String, Object>) action_map.get("change");
                        Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                        Map<String, Object> salespolicy_map = (Map<String, Object>) action_map.get("salespolicy");
                        change_map.put("action","update");
                        change_map.put("update_map",update_map);
                        change_map.put("salespolicy",salespolicy_map);
                        ActivityController.change(tableView.getContext1(),change_map,tableView,"",s.toString(),this);

                    }else if (action_map.get("change") != null) {
                        //执行change 动作
                        Map<String, Object> change_map = (Map<String, Object>) action_map.get("change");
                        ActivityController.change(tableView.getContext1(),change_map,tableView,"",s.toString(),this);
                    }else if (action_map.get("update") != null) {
                        //执行 update 动作
                        Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                        ActivityController.update(tableView.getContext1(),update_map,tableView,"",s.toString(),this);
                    }

                }
            }else if (isJisuan && action_map.get("update")!= null){
                //执行 update 动作
                Map<String, Object> update_map = (Map<String, Object>) action_map.get("update");
                ActivityController.update(tableView.getContext1(),update_map,tableView,"",s.toString(),this);
            }
        }else{
            //计算公式
            jisuan(s.toString());
        }

        if (!isJisuan){
            //计算公式
            jisuan(s.toString());
        }
    }

    public void jisuan(String s){
        String[] gss = null;
        BasicItem test_item = null,item;
        String val = "",gs1 = "",res;

        View v  = null;
        //再执行 计算动作
        if (gs == null || gs.equals("")){
            item = ActivityController.getItem(tableView.getIListItem(),code);
            item.setSubtitle(s.toString());
        }else{
            if (!s.toString().equals("") && !gs.equals("")){
                String[] gss1 =  gs.split(";");
                if (gs.indexOf("{")>=0 && gs.indexOf("}")>=0){
                    Map<String,Object> gsMap = FastJsonUtils.strToMap(gs);
                    if (gsMap != null){
                        if ( gsMap.get("clear") != null){
                            String clrears = gsMap.get("clear").toString();
                            String[] clrearArray =  clrears.split(",");
                            for (String code:clrearArray) {
                                item = ActivityController.getItem(tableView.getIListItem(),code);
                                item.setSubtitle("");
                                v  = tableView.getLayoutList(item.getIndex());
                                tableView.setupBasicItemValue(v,item,item.getIndex());
                            }
                        }
                    }
                }else{
                    for (String gs2:gss1){
                        gss = gs2.split("=");
                        result = gss[1];
                        gs1 = gss[0];

                        item = ActivityController.getItem(tableView.getIListItem(),code);
                        item.setSubtitle(s.toString());
                        int start=0,end=0,i=0;
                        String test = "";

                        while (gs1.indexOf("[")>=0 && gs1.indexOf("]")>=0){
                            if (i>20) return;
                            start = gs1.indexOf("[");
                            end = gs1.indexOf("]");
                            test  = gs1.substring(start+1,end);
                            item = ActivityController.getItem(tableView.getIListItem(),test);
                            if (item != null){
                                v  = tableView.getLayoutList(item.getIndex());
                                val = tableView.getBasicItemValue(v,item,item.getIndex());
                            }

                            if (test.equals("sjjs")){
                                val = AppUtils.user.getSjjs();
                            }

                            if(val.equals("")){
                                val = "0";
                                item.setSubtitle("0");
                            }



                            gs1 = gs1.replace("["+test+"]",val);
                            i++;
                        }

                        if (!gs1.equals("")){
                            try{
                                if (result.indexOf("(")>=0 && result.indexOf(")")>=0){
                                    int start1 = result.indexOf("(")+1;
                                    int end1 = result.indexOf(")");
                                    result_copy = result.substring(0,start1-1);
                                    precision = result.substring(start1,end1);
                                }else{
                                    result_copy = result;
                                    precision = "2";
                                }
                                res = SizheTool.eval2(gs1,precision).toString();
                            }catch (Exception e){
                                e.printStackTrace();
                                res = "";
                            }

                            item = ActivityController.getItem(tableView.getIListItem(),result_copy);
                            if (item != null){
                                item.setSubtitle(res);
                                v  = tableView.getLayoutList(item.getIndex());
                                tableView.setupBasicItemValue(v,item,item.getIndex());
                            }

                        }
                    }
                }
            }
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public String getPreVal() {
        return preVal;
    }

    public void setPreVal(String preVal) {
        this.preVal = preVal;
    }

    public boolean isJisuan() {
        return isJisuan;
    }

    public void setJisuan(boolean jisuan) {
        isJisuan = jisuan;
    }

    /**
     * 比较 修改前后 是否 一致
     * @return
     */
    public boolean isCompare(){
        if (StringUtils.isNumeric(preVal)){
            try {
                if (String.valueOf(Double.parseDouble(preVal)).equals(String.valueOf(Double.parseDouble(afterVal)))){
                    return  true;
                }
            }catch (Exception e){
                return  false;
            }

        }
        return  false;
    }

    public String getFrom(String from){
        int i = 0,start,end;
        String test = "",val="";
        BasicItem item;
        View v;
        while (from.indexOf("[")>=0 && from.indexOf("]")>=0){
            if (i>20) return "";
            start = from.indexOf("[");
            end = from.indexOf("]");
            test  = from.substring(start+1,end);
            item = ActivityController.getItem(tableView.getIListItem(),test);
            if (item != null){
                v  = tableView.getLayoutList(item.getIndex());
                val = tableView.getBasicItemValue(v,item,item.getIndex());
            }

            if (test.equals("sjjs")){
                val = AppUtils.user.getSjjs();
            }

            if(val.equals("")){
                val = "0";
                item.setSubtitle("0");
            }
            from = from.replace("["+test+"]",val);
            i++;
        }

        return  from;
    }
}