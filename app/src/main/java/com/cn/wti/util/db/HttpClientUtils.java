package com.cn.wti.util.db;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ASUS on 2017/9/15.
 */

public class HttpClientUtils {
    /*
     * Function  :   发送Post请求到服务器
     * Param     :   params请求体内容，encode编码格式
     */
    public static String submitPostData(String strUrlPath,Map<String, String> params, String encode) {
        OutputStream outputStream = null;
        InputStream inptStream = null;
        HttpURLConnection httpURLConnection = null;
        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        URL url = null;
        try {
            url = new URL(strUrlPath);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(15000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }else if(response == 504){
                return "err:Gateway Time-out";
            }else if (response == 404){
                return "err:request bad";
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }finally {

            try {
                if (outputStream != null){
                    outputStream.flush();
                    outputStream.close();
                }
                if (inptStream != null){
                    inptStream.close();
                }
                if (data != null){
                    data = null;
                }
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "-1";
    }

    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    /**
     * 执行post 请求
     * @param servicename
     * @param methodname
     * @param parms
     * @return
     * @throws Exception
     */
    public static String exectePost(String servicename,String methodname,String parms) throws Exception{
        String url = getUrl();

        //服务器请求路径
        String strUrlPath = url+"?DateTime=" + DateUtil.createDate().replace(" ","-");
        Map<String,String> formparams = new HashMap<String, String>();
        formparams.put("wsmd", "execute");
        formparams.put("sm", servicename);
        formparams.put("mm", methodname);
        if (!TextUtils.isEmpty(parms)){
            JSONObject paramsJot = FastJsonUtils.strToJson(parms);
            paramsJot.put("currentUserId",AppUtils.user.get_id());
            paramsJot.put("v_",AppUtils.user.get_version());
            paramsJot.put("book_name",AppUtils.book_name);
            paramsJot.put("user_name",AppUtils.user.get_loginName());
            paramsJot.put("user_p",AppUtils.user.get_password());
            parms = paramsJot.toJSONString();
        }
        formparams.put("pv", parms);
        String res=submitPostData(strUrlPath,formparams, "utf-8");
        if (res!= null && !res.equals("")){
            return res;
        }else{
            return "网络异常";
        }
    }

    public static String getUrl(){
        String url ="";
       if (AppUtils.isIp(AppUtils.app_address)){
            url = "http://"+ AppUtils.app_address+":8080/wtmis/webService2/get.do";
        }else{
            url = "http://"+ AppUtils.app_address+"/webService2/get.do";
        }
        return  url;
    }

    /**
     * 执行post 请求
     * @param wsmd
     * @param parms
     * @return
     * @throws Exception
     */
    public static String webService(String wsmd,String parms) throws Exception{

        String url =getUrl();
        //服务器请求路径
        String strUrlPath = url+"?DateTime=" + DateUtil.createDate().replace(" ","-");
        Map<String,String> formparams = new HashMap<String, String>();
        formparams.put("wsmd", wsmd);
        if (!TextUtils.isEmpty(parms)){
            JSONObject paramsJot = FastJsonUtils.strToJson(parms);
            if (AppUtils.user != null){
                paramsJot.put("currentUserId",AppUtils.user.get_id());
                if (!wsmd.equals("login")){
                    paramsJot.put("v_",AppUtils.user.get_version());
                }
                parms = paramsJot.toJSONString();
            }
            if (paramsJot.get("parms")!=null){
                JSONObject pJot = FastJsonUtils.strToJson(paramsJot.get("parms").toString());
                if (!wsmd.equals("login")){
                    pJot.put("v_",AppUtils.user.get_version());
                }
                paramsJot.put("parms",pJot.toJSONString());
                parms = paramsJot.toJSONString();
            }
        }
        formparams.put("pv", parms);
        String res=submitPostData(strUrlPath,formparams, "utf-8");
        if (res!= null && !res.equals("")){
            if (ActivityController.getPostState(res)==0){
                return res;
            }else{
                return "(abcdef)"+res.toString();
            }
        }else{
            return "(abcdef)数据异常";
        }
    }

    public static void sendMessageErr(int postState, final Context context) {
        String msg = "";
        switch (postState){
            case 1:
                msg = "数据异常";
                break;
            case 2:
                msg = "网络异常";
                break;
            case 3:
                msg = "连接超时";
                break;
            case 4:
                msg = "找不到服务器";
                break;
        }

        if (ActivityController.isMainThread()){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }else{
            final String finalMsg = msg;
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, finalMsg,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static String backMessage(int postState) {
        String msg = "";
        switch (postState){
            case 1:
                msg = "(abcdef)数据异常";
                break;
            case 2:
                msg = "(abcdef)网络异常";
                break;
            case 3:
                msg = "(abcdef)连接超时";
                break;
            case 4:
                msg = "(abcdef)找不到服务器";
                break;
            case 5:
                msg = "(abcdef)连接超时";
                break;
            default:
                msg = "(abcdef)数据异常";
                break;
        }
        return msg;
    }

    public static String GetNetIp(){
        String res = submitGetData("http://ip.chinaz.com/getip.aspx",null);
        if (res != null && !res.equals("")){
            if (res.indexOf("{")>=0 && res.indexOf("}")>=0){
                Map<String,Object> resMap = FastJsonUtils.strToMap(res);
                if (resMap == null){
                    return "";
                }
                return  resMap.get("ip").toString();
            }
        }else{
            return  "(abcdef)获取IP失败";
        }
        return "";
    }

    /**
     * 获取IP地址
     * @return
     */
    public static String submitGetData(String url1,Map<String,Object>parms) {
        StringBuilder buf = new StringBuilder(url1);
        URL url = null;
        InputStream inptStream = null;
        try {
            if (parms != null && parms.size() >0){
                buf.append("?");
                Set<String> parmsSet = parms.keySet();
                for (String key:parmsSet) {
                    buf.append(key+"="+URLEncoder.encode(parms.get(key).toString(),"UTF-8")+"&");
                }
                buf.append("time__=1");
            }
            url = new URL(buf.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(2000);
            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码

            if(response == HttpURLConnection.HTTP_OK) {
                inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }else if(response == 504){
                return "err:Gateway Time-out";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }finally {
            try {
                if(inptStream != null){
                    inptStream.close();
                    inptStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "-1";
    }

}
