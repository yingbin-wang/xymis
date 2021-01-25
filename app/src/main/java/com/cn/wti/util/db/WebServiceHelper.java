package com.cn.wti.util.db;

/**
 * Created by wangz on 2016/9/17.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.cn.wti.util.app.AppUtils;
import com.hyphenate.easeui.model.System_one;

/**
 * 访问Web Service的工具类
 * @author jCuckoo
 *
 */
@SuppressLint("NewApi")
public class WebServiceHelper {

    private static String msg="";

    static {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            // 4.0以后需要加入下列两行代码，才可以访问Web Service
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }
        //4.0以前版本不需要以上设置
    }

    /**
     *
     * @param serviceName
     * @param methodName
     * @param params
     * @return
     */
    public static SoapObject getSoapObject(String ip,String serviceName, String methodName,Map<String, Object> params) {

        String URL ="";
        if (AppUtils.isIp(ip)){
            URL = "http://"+ip+":8080/wtmis/webservice/"+ serviceName + "?wsdl";
        }else{
            URL = "http://"+ip+"/wtmis/webservice/"+ serviceName + "?wsdl";
        }

        String NAMESPACE = "http://webservice.plugin.wticn.com/";// 名称空间，服务器端生成的namespace属性值
        String METHOD_NAME = methodName;
        String SOAP_ACTION = NAMESPACE+methodName;

        SoapObject soap = null;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            if (params != null && params.size() > 0) {
                for (Entry<String, Object> item : params.entrySet()) {
                    if (item.getKey().equals("parameters") && item.getValue().toString().indexOf("DATA_IDS")>=0 ){
                        String val = item.getValue().toString(),val1;
                        val1 = val.substring(0,val.length()-1);
                        Map<String,Object> map  = FastJsonUtils.strToMap(item.getValue().toString());
                        rpc.addProperty(item.getKey(), val1+",\"array\":["+map.get("DATA_IDS")+"]}");
                        map = null;
                    }else {
                        rpc.addProperty(item.getKey(), item.getValue().toString());
                    }
                }
            }

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            //envelope.dotNet = true;// true--net; false--java;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE(URL ,10000);
            //ht.debug = true;
            ht.call(null, envelope);
            if (envelope.getResponse() instanceof String){
                soap = (SoapObject) envelope.bodyIn;
            }else{
                soap = (SoapObject) envelope.getResponse();
            }

        } catch (Exception ex) {
            /*ex.printStackTrace();*/
            if (ex instanceof java.net.SocketTimeoutException) {
                msg = "连接服务器超时,请检查网络";
            } else if (ex instanceof java.net.UnknownHostException) {
                msg = "未知服务器,请检查配置";
            }else{
                msg = "";
            }
            System.out.print("异常。。。。。。。。。。。。。呵呵呵额");
        }
        return soap;
    }

    private void test() throws Exception {
        URL wsUrl = new URL(AppUtils.app_address);

        HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");

        OutputStream os = conn.getOutputStream();

        String soap = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" soap:mustUnderstand=\"1\">"
                + "<wsse:UsernameToken>"
                + "<wsse:Username>admin</wsse:Username>"
                + "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">ps</wsse:Password>"
                + "</wsse:UsernameToken>"
                + "</wsse:Security>"
                + "</SOAP-ENV:Header>"
                + "<soap:Body>"
                + "<ns2:sayHello xmlns:ns2=\"http://easipass.com/\">"
                + "<name>foo</name>"
                + "</ns2:sayHello>"
                + "</soap:Body>"
                + "</soap:Envelope>";

        // 请求体(请根据实际情况复制修改)
        // String soap = "XXXXX";

        os.write(soap.getBytes());

        InputStream is = conn.getInputStream();

        byte[] b = new byte[1024];
        int len = 0;
        String s = "";
        while ((len = is.read(b)) != -1) {
            String ss = new String(b, 0, len, "UTF-8");
            s += ss;
        }
        System.out.println(s);

        is.close();
        os.close();
        conn.disconnect();
    }


    /***
     * 拼接请求webservice
     *
     * @return
     */
    private static StringEntity getEntity(String thecity) {
        StringEntity entity = null;

        //可以参照WSDL...
        String str = String
                .format("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        + "<soap:Body>"
                        + "<loginByUernameAndPassword xmlns=\"http://WebXml.com.cn/\">"
                        + "<username>nina.lu</username>"
                        + "<password>nina.lu</password>"
                        + "</loginByUernameAndPassword>" + "</soap:Body>"
                        + "</soap:Envelope>", thecity);
        try {
            entity = new StringEntity(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /***
     * webservice 请求
     */
    public static HttpPost httpPost(StringEntity entity, String SOAP_ACTION) {

        HttpPost request = new HttpPost(AppUtils.app_ip);
        request.addHeader("SOAPAction", SOAP_ACTION);
        request.addHeader("Host","www.webxml.com.cn");
        request.addHeader("Content-Type", "text/xml; charset=utf-8");
        request.setEntity(entity);
        return request;
    }


    /***
     * 获取响应webservice HttpResponse
     */
    public static HttpResponse getHttpResponse(HttpPost httpPost) {
        HttpClient client = new DefaultHttpClient();
        client.getParams()
                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                        10 * 1000);
        HttpResponse httpResponse = null;

        try {
            httpResponse = client.execute(httpPost);
        } catch (ClientProtocolException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpResponse;
    }




    /***
     * 获取内容 解析httpresponse
     */
    public static String getContent(HttpResponse httpResponse) {


        InputStream inputStream;
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        try {
            inputStream = httpResponse.getEntity().getContent();
            in = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                sb.append("\n");// 换行
            }
            in.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return sb.toString();
    }


    /**
     * 通过 IP 与 方法名 获取返回的数据 String
     * @param ip
     * @param methodName
     * @param params
     * @return
     */
    public static  String getResult(String ip, String methodName,Map<String, Object> params){
        String result = "";
        msg = "";
        SoapObject soap = getSoapObject(ip,"execWebService",methodName,params);
        if (!msg.equals("")){return  msg;}
        if(soap != null){
            // 获取返回的结果
             result = soap.getProperty(0).toString();

            String[] tests =  soap.getProperty(0).toString().split("\n");
            Log.v("","");
        }
        return  result;
    }

    public static  Object getObjectResult(String ip, String methodName,Map<String, Object> params){
        Object result = "";
        msg = "";
        SoapObject soap = getSoapObject(ip,"execWebService",methodName,params);
        if (!msg.equals("")){return  msg;}
        if(soap != null){
            // 获取返回的结果
            result = soap.getProperty(0);
        }
        return  result;
    }

    /**
     * 通过 webservice 得到 Map数据
     * @param ip
     * @param methodName
     * @param parms
     * @return
     */
    public static   List<Map<String,Object>> getResultListMap(String ip, String methodName,Map<String, Object> parms){

        List<Map<String,Object>> menu_list = null;

        String result = WebServiceHelper.getResult(ip,methodName,parms);
        if(!result.equals("")){
            try {
                menu_list = FastJsonUtils.getBeanMapList(result);
            } catch (Exception e) {
                /*e.printStackTrace();*/
            }
        }
        return  menu_list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}