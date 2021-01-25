package com.cn.wti.util.app.webservice;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 2017/9/18.
 */

public class ActivityUtilsByWebService {

    public static Object getObjectData2(Context mContext, String service_name, String method_name, String pars){
        Map<String,Object> parms = new HashMap<String,Object>();
        parms.put("serviceName",service_name);
        parms.put("methodName",method_name);
        parms.put("parameters",pars);

        boolean isValiad = AppUtils.isNetworkAvailable((Activity) mContext);

        if (isValiad){
            Object res = WebServiceHelper.getObjectResult(AppUtils.app_address,"execute",parms);
            if (!res.equals("")) {
                Object data;
                List<Map<String, Object>> dataList;
                JSONObject resMap  = FastJsonUtils.strToJson(res.toString());
                if(resMap == null){
                    return null;
                }
                if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                    data = resMap.get("data");
                    if (data instanceof JSONArray) {
                        dataList = (List<Map<String, Object>>) data;
                    } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                        return (Map<String, Object>) data;
                    } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                    } else if(data instanceof String){
                        return data;
                    }else {
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                    }
                    return dataList;
                }else if(resMap.get("list")!= null){
                    return  resMap;
                }

            }
        }else{
            Toast.makeText(mContext,mContext.getString(R.string.error_invalid_network),Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    public static Object getData2(String service_name,String method_name,String pars){
        Map<String,Object> parms = new HashMap<String,Object>();
        parms.put("serviceName",service_name);
        parms.put("methodName",method_name);
        parms.put("parameters",pars);
        String res = WebServiceHelper.getResult(AppUtils.app_address,"execute",parms);
        if (!res.equals("")) {
            Object data;
            List<Map<String, Object>> dataList;
            Map<String, Object> resMap = FastJsonUtils.strToMap(res);
            if(resMap == null){
                return null;
            }
            if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                data = resMap.get("data");
                if (data instanceof JSONArray) {
                    dataList = (List<Map<String, Object>>) data;
                } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                    return (Map<String, Object>) data;
                } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                    dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                } else if(data instanceof String){
                    return data;
                }else {
                    dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                }
                return dataList;
            }else if(resMap.get("list")!= null){
                return  resMap;
            }

        }
        return null;
    }

    public static Object getData2(Context mContext,String service_name,String method_name,String pars){
        Map<String,Object> parms = new HashMap<String,Object>();
        parms.put("serviceName",service_name);
        parms.put("methodName",method_name);
        parms.put("parameters",pars);

        boolean isValiad = AppUtils.isNetworkAvailable((Activity) mContext);

        if (isValiad){
            String res = WebServiceHelper.getResult(AppUtils.app_address,"execute",parms);
            if (!res.equals("")) {
                Object data;
                List<Map<String, Object>> dataList;
                Map<String, Object> resMap = FastJsonUtils.strToMap(res);
                if(resMap == null){
                    return null;
                }
                if (resMap.get("state")!=null && resMap.get("state").toString().equals("success")) {
                    data = resMap.get("data");
                    if (data instanceof JSONArray) {
                        dataList = (List<Map<String, Object>>) data;
                    } else if (data instanceof JSONObject && ((JSONObject) data).get("rows") == null ) {
                        return (Map<String, Object>) data;
                    } else if(data instanceof JSONObject && ((JSONObject) data).get("list") != null){
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("list"));
                    } else if(data instanceof String){
                        return data;
                    }else {
                        dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                    }
                    return dataList;
                }else if(resMap.get("list")!= null){
                    return  resMap;
                }else{
                    return  "err"+resMap.get("msg").toString();
                }
            }
        }else{
            if (mContext != null){
                return  "err"+mContext.getString(R.string.error_invalid_network);
            }else{
                return  "err网络异常";
            }

        }
        return null;
    }

    public static Object getData(String service_name,String method_name,String pars){

        Object res = "";

        Map<String,Object> parms = new HashMap<String,Object>();
        parms.put("serviceName",service_name);
        parms.put("methodName",method_name);
        parms.put("parameters",pars);
        res = WebServiceHelper.getResult(AppUtils.app_address,"execute",parms);

        if (res.toString().indexOf("服务器")>=0){
            AppUtils.fwq_state = false;
        }

        return res;
    }

    /**
     * 从服务器得到数据
     * @param service_name
     * @param method_name
     * @param pars
     * @return
     */
    public static Object getData(Context mContext,String service_name,String method_name,String pars){

        Object res = "";

        if (!AppUtils.isNetworkAvailable((Activity) mContext)){
            AppUtils.network_state = false;
        }else{
            AppUtils.network_state = true;
        }

        if (AppUtils.network_state){
            Map<String,Object> parms = new HashMap<String,Object>();
            parms.put("serviceName",service_name);
            parms.put("methodName",method_name);
            parms.put("parameters",pars);
            res = WebServiceHelper.getResult(AppUtils.app_address,"execute",parms);

            if (res.toString().indexOf("服务器")>=0){
                AppUtils.fwq_state = false;
            }else{
                AppUtils.fwq_state = true;
            }
        }else{
            res = "";
        }

        return res;
    }
}
