package com.cn.wti.util.app;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/12/25.
 */

public class ReportUtils {

    public static void addRowTitle_col(LinearLayout linearLayout_title,String[] names,int num, int view_id, int colorid, int visible){
        TextView title = (TextView) linearLayout_title.findViewById(view_id);
        title.setText(names[num-1]);
        title.setTextColor(colorid);
        title.setVisibility(visible);
    }

    public static void addRowContent_col(LinearLayout linearLayout_content,List<Map<String,Object>>_columnList,int view_id,int hj_num,Map<String,Object> map1){
        TextView txt = (TextView) linearLayout_content.findViewById(view_id);
        txt.setText(String.valueOf(getVal(_columnList,hj_num,map1)));
        txt.setVisibility(View.VISIBLE);
    }

  /*  public static void addRowContent_col(LinearLayout linearLayout_content,int view_id,int hj_num,Map<String,Object> map1){
        TextView txt = (TextView) linearLayout_content.findViewById(view_id);
        txt.setText(String.valueOf(getVal(_c)));
        txt.setVisibility(View.VISIBLE);
    }*/



    private static String getVal(List<Map<String, Object>> _columnList, int i, Map<String, Object> map){
        String key1 = ((Map<String,Object>) _columnList.get(i)).get("field").toString(),val;

        if(map.get(key1) == null){
            val = "";
        }else{
            val = map.get(key1).toString();
            if(StringUtils.isNumeric(val)){
                double val_ = Double.parseDouble(val);

                if(Math.abs(val_) > 10000 ){
                    val = SizheTool.jq2wxs(String.valueOf(val_ /10000),"2")+"万";
                }/*else if(Math.abs(val_) >1000){
                    val = SizheTool.jq2wxs(String.valueOf(val_ /1000),"2")+"千";
                }*/
            }
        }
        return val;
    }

}
