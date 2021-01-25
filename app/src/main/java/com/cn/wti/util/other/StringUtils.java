package com.cn.wti.util.other;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.cn.wti.util.number.SizheTool;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;*/

/**
 * Created by wangz on 2016/9/28.
 */
public class StringUtils {

    /*static ScriptEngine ScrEngin = new ScriptEngineManager().getEngineByName("JavaScript");*/

    public static boolean isNumeric(String str){
        int result = 0;
        String regex1 = "^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$";
        boolean flag1 = str.matches(regex1);
        if(flag1){
            String regex2 = "^[-+]?([0-9]+)$";
            boolean flag2 = str.matches(regex2);
            if(flag2){
                result = 1;
            }
            else{
                result = 2;
            }
        }
        if (str.equals("") || str.equals("-")){
            return  false;
        }else if(result!= 0){
            return  true;
        }else{
            return  false;
        }
    }

    public static String getVal(String val){

        if(StringUtils.isNumeric(val)){
            double val_ = Double.parseDouble(val);
            if(Math.abs(val_) > 10000 ){
                val = SizheTool.jq2wxs(String.valueOf(val_ /10000),"2")+"万";
            }/*else if(Math.abs(val_) >1000){
                val = SizheTool.jq2wxs(String.valueOf(val_ /1000),"2")+"千";
            }*/
        }
        return val;
    }

    /**
     * 将 str 转成 json 字符串
     * @param str
     * @return
     */
    public static  String strTOJsonstr(String str){

        String res = "";
        if(str != null && !str.equals("")){
            str = str.replace("{","").replace("}","").replaceAll("\"","");
            String[] strs = str.split(",");
            int n = strs.length,i = 0;

            for (String s:strs) {
                String[] options = s.split(":");
                String key = "\""+options[0]+"\"";
                String val = "";
                if (options.length == 1){
                    val = "\""+""+"\"";
                }else{
                    val = "\""+options[1]+"\"";
                }
                
                if(i++ == n - 1){
                    res += key+":"+val;
                }else{
                    res += key+":"+val+",";
                }
            }
        }
        if(res.indexOf("<m>")>=0){
            res = res.replaceAll("<m>",":").replaceAll("<dh>",",");
        }
        if(res.indexOf("<dh>")>=0){
            res = res.replaceAll("<dh>",",");
        }
        return  "{"+res+"}";
    }

    /**
     * 计算表达式
     * @param expression
     * @return
     */
    public static Object eval(String expression){
        StringBuilder builder = new StringBuilder();
        if (expression.contains("(")) {
            Pattern pattern = Pattern.compile("\\(([^()]+)\\)");
            Matcher matcher = pattern.matcher(expression);
            int lastEnd = 0;
            while (matcher.find()) {

                builder.append(expression.substring(lastEnd, matcher.start()));
                System.out.println(builder.toString());
                builder.append(eval(matcher.group(1)));
                lastEnd = matcher.end();
            }
            builder.append(expression.substring(lastEnd));
        } else {
            Pattern pattern = Pattern.compile("([\\d.]+)\\s*([*/])\\s*([\\d.]+)");
            builder.append(expression);
            Matcher matcher = pattern.matcher(builder.toString());
            while (matcher.find()){
                float f1 = Float.parseFloat(matcher.group(1));
                float f2 = Float.parseFloat(matcher.group(3));
                float result = 0;
                switch (matcher.group(2)){
                    case "*":
                        result = f1 * f2;
                        break;
                    case "/":
                        result = f1 / f2;
                        break;
                }
                builder.replace(matcher.start(), matcher.end(),
                        String.valueOf(result));
                matcher.reset(builder.toString());
            }
            pattern = Pattern.compile("([\\d.]+)\\s*([+-])\\s*([\\d.]+)");
            matcher = pattern.matcher(builder.toString());
            while (matcher.find()){
                float f1 = Float.parseFloat(matcher.group(1));
                float f2 = Float.parseFloat(matcher.group(3));
                float result = 0;
                switch (matcher.group(2)){
                    case "+":
                        result = f1 + f2;
                        break;
                    case "-":
                        result = f1 - f2;
                        break;
                }
                builder.replace(matcher.start(), matcher.end(),
                        String.valueOf(result));
                matcher.reset(builder.toString());
            }
            return builder.toString();
        }
        System.out.println(builder);
        return eval(builder.toString());
    }

    /**
     * 从 Map中 得到 值
     * @param map
     * @param key
     * @return
     */
    public static String returnString(Map<String,Object> map,String key){
        String res = null;
        if (key.equals("")){return  "";}

        if(key.indexOf("：")>=0) {
            String[] _keys = key.split("：");
            String map_key="";
            int start = _keys[1].indexOf("[");
            int end = _keys[1].indexOf("]");
            map_key = _keys[1].substring(start+1,end);
            if(map.get(map_key) != null){
                /*//验证 是否是 select 选项
                getContents_select(map_key,);*/
               res = _keys[0]+"："+map.get(map_key).toString()+_keys[1].substring(end+1);
            }
        }else{
            int start = key.indexOf("[");
            int end = key.indexOf("]");
            String map_key = key.substring(start+1,end);
            if(map.get(map_key) != null){
                if (end+1 <= key.length()){
                    res = map.get(map_key).toString()+key.substring(end+1);
                }else{
                    res = map.get(map_key).toString();
                }
            }
        }
        return  res;
    }

    public static String getContents_select(String code, List<Map<String, Object>> gs_list){

        String contents="";
        if(gs_list == null){return contents;}
        for (Map<String,Object> map:gs_list) {
            if(map.get("code").toString().equals(code)){
                if(map.get("gldn") != null){
                    contents = map.get("gldn").toString();
                }
                break;
            }
        }
        return contents;
    }

    public static String getContents_select(String code, List<Map<String, Object>> gs_list,String key){

        String contents="";
        if(gs_list == null){return contents;}
        for (Map<String,Object> map:gs_list) {
            if(map.get("code").toString().equals(code)){
                if(map.get(key) != null){
                    contents = map.get(key).toString();
                }
                break;
            }
        }
        return contents;
    }

    /**
     * 替换 start
     * @param pars
     * @param split
     * @return
     */
    public static String replaceStart(String pars,String split,String start_1){
        String replace = "";
        String test_pars= pars;
        test_pars = test_pars.replaceAll("\"","");
        if (test_pars.indexOf(split)>=0){
            int start = test_pars.indexOf(split);
            String endStr = test_pars.substring(start);
            int end_1;
            if (endStr.indexOf(",")>=0){
                end_1 = endStr.indexOf(",");
            }else{
                end_1 = endStr.indexOf("}");
            }

            int end = start + end_1;
            replace = test_pars.substring(start,end);
        }
        if (!replace.equals("")){
            test_pars = test_pars.replace(replace,start_1);
        }

        return  test_pars;
    }

    /**
     * 替换冒号
     * @param pars
     * @param split
     * @param start_1
     * @return
     */
    public static String replaceStart_mh(String pars,String split,String start_1){
        String replace = "";
        pars = pars.replaceAll("\"","");
        if (pars.indexOf(split)>=0){
            int start = pars.indexOf(split);
            String endStr = pars.substring(start);
            int end = start + endStr.indexOf(",");
            replace = pars.substring(start,end);
        }
        return  pars.replace(replace,start_1);
    }

    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 获取字符串宽度
     * @param text
     * @return
     */

    public static double getWidthByText(String text){
        Paint paint = new Paint();
        paint.measureText(text);
        return paint.measureText(text);
    }

    /**
     * 获取字符串高度
     * @param text
     * @return
     */
    public static double getHeightByText(String text){
        Paint paint = new Paint();
        Rect r = new Rect();
        paint.getTextBounds(text, 0, 1, r);
        Log.e("tt", "字符:" + text + "---height:" + (r.bottom - r.top));
        Log.e("tt", "字符:" + text + "width:" + (r.right - r.left));
        return (r.bottom - r.top);
    }

    /**
     * 根据 【】 截取字符 集合
     * @param href
     * @return
     */
    public static   String[] getKeys(String href){
        String keys = "",key="";
        int start,end,i=0;
        while (href.indexOf("[")>=0){
            start = href.indexOf("[");
            end = href.indexOf("]");
            key = href.substring(start+1,end);
            if(!key.equals("")){
               if (keys.equals("")){
                   keys+=key;
               }else {
                   keys+=","+key;
               }
                href = href.replace("["+key+"]","");
            }
        }
        return  keys.split(",");
    }

    public static String arrayTostr(String[] strs){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strs.length; i++){
            if (sb.toString().equals("")){
                sb. append("\""+strs[i]+"\"");
            }else{
                sb. append(",\""+strs[i]+"\"");
            }
        }
        return  sb.toString();
    }

    public static String arrayTostrSplit(String[] strs,String split){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strs.length; i++){
            if (sb.toString().equals("")){
                sb. append(strs[i]);
            }else{
                sb. append(split+strs[i]);
            }
        }
        return  sb.toString();
    }

    /**
     * list字符串转字符
     * @param list
     * @param split
     * @return
     */
    public static String listStrTostrSplit(List<String> list,String split){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < list.size(); i++){
            if (sb.toString().equals("")){
                sb. append(list.get(i));
            }else{
                sb. append(split+list.get(i));
            }
        }
        return  sb.toString();
    }

    public static String listTostr(List strs){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strs.size(); i++){
            if (sb.toString().equals("")){
                sb. append(strs.get(i));
            }else{
                sb. append(","+strs.get(i));
            }
        }
        return  sb.toString();
    }

    public static boolean isExitVal(String[] arr,String targetValue){
        return Arrays.asList(arr).contains(targetValue);
    }

    public static int getOccur(String src,String find){
        int o = 0;
        int index=-1;
        while((index=src.indexOf(find,index))>-1){
            ++index;
            ++o;
        }
        return o;
    }

    public static String replaceSpecialCharterBack(String avc){
        avc = avc.replace("&nbsp;"," ")
		   		 .replace("\\\"","\"")
                .replace("\\/","/")
                .replace( "\\n","\n").replace("\\r","\r")
                .replace( "&#39;","\'").replace("\\\\","\\" );
        return avc.replace("<br>","\n").replace("<br/>","\n");
    }

    public static String firstLowerStr(String str){
        if (!str.equals("")){
            return str.substring(0,1).toLowerCase()+str.substring(1);
        }
        return str;
    }

}
