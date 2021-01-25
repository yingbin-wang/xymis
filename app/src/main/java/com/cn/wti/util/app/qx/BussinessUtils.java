package com.cn.wti.util.app.qx;

import com.cn.wti.util.Constant;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2017/12/11.
 */

public class BussinessUtils {

    /**
     * 更新 列表显示内容数据
     * @param contents
     * @return
     */
    public static String[] updateContents(String[] contents,List<String> list) {

        String[] jssjContents = Constant.taxrate_array;
        List<String> resList = new ArrayList<String>();
        if (AppUtils.user.getSjjs().equals("2") ) return  contents;

        int i = 0,start,end;
        String content1="";
        for (String content:contents ) {
            if (content.indexOf("[")>=0){
                start = content.indexOf("[");
                end = content.indexOf("]");
                content1 = content.substring(start+1,end);
            }

            if (!list.contains(content1)){
                resList.add(content);
            }
            i++;
        }

        if (resList.size()>0){
            contents = FastJsonUtils.listStrToArray(resList);
        }

        return  contents;

    }

    public static String sellpolicysetid = "0";
}
