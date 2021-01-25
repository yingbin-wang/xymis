package com.cn.wti.util.app.version;

import android.app.Activity;
import android.util.Xml;
import com.cn.wti.util.other.StringUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyb on 2017/9/4.
 */

public class HttpUtils {

   static String  uriAPI = "http://android.myapp.com/myapp/detail.htm?apkName=com.wticn.wyb.wtiapp";  //声明网址字符串

    public  static String getVersion(){
        HttpPost httpRequest = new HttpPost(uriAPI);   //建立HTTP POST联机
        List<NameValuePair> params = new ArrayList<NameValuePair>();   //Post运作传送变量必须用NameValuePair[]数组储存
        //params.add(new BasicNameValuePair("str", "I am Post String"));
        try {
            //httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   //发出http请求
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
            if (httpResponse.getStatusLine().getStatusCode() == 200){
                String strResult = EntityUtils.toString(httpResponse.getEntity()); //获取字符串
                if (strResult != null){
                    String version = getVersion(strResult);
                    String downUrl = getdownUrl(strResult);
                    if (!version.equals("")){
                        return  version+";"+downUrl;
                    }
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  "";

    }

    public static String getVersion(String res){

        String test,resStr="";

        if (res.indexOf("\n")>=0){
            String [] ss=res.split("\n");

            for (int i = 0; i < ss.length; i++) {
                test = ss[i].replaceAll(" ","").replace(",","");
                if(test.indexOf("apkCode")>0){
                    test = test.replaceAll("\t","").replaceAll("\r","").replaceAll("\"","");
                    if (StringUtils.isNumeric(test.substring(test.indexOf(":")+1))){
                        resStr = test.substring(test.indexOf(":")+1);
                        break;
                    }
                }
            }
        }
        return resStr;
    }

    public static String getdownUrl(String res){

        String test,resStr="";

        if (res.indexOf("\n")>=0){
            String [] ss=res.split("\n");

            for (int i = 0; i < ss.length; i++) {
                test = ss[i].replaceAll(" ","").replace(",","");
                if(test.indexOf("downUrl")>0){
                    test = test.replaceAll("\t","").replaceAll("\r","").replaceAll("\"","");
                    resStr = test.substring(test.indexOf(":")+1);
                    break;
                }
            }
        }
        return resStr;
    }

}
