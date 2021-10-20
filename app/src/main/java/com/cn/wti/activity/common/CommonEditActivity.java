package com.cn.wti.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_01Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.file.AlbumUtils;
import com.cn.wti.util.file.FileUtil;
import com.cn.wti.util.net.Net;
import com.cn.wti.util.number.FileUtils;
import com.cn.wti.util.number.IniUtils;
import com.dina.ui.model.BasicItem;
import com.wticn.wyb.wtiapp.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommonEditActivity extends BaseEdit_01Activity {

    int RESULT_OK = 1,REQUEST_CODE =1,MXREQUEST_CODE =2;
    private  String pars;
    private String[] contents;
    private Map<String,Object> gs_map/*明细结构*/,
                                 gongshi_map/*公式规则*/;

    Object res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initData() {
        mContext = CommonEditActivity.this;

        initMap = (Map<String, String>) get_catch().get(menu_code+"_init");
        gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
        super.initData("main");

    }

    public  void createView(){

        super.createView();
        //添加明细
        createMx();
    }

    /**
     * 保存和编辑方法
     */
    @Override
    public boolean saveOrEdit() {

        if (menu_code.toLowerCase().equals("saleOrder")){
           /* //处理销售订单 政策 数据
            List<Map<String,Object>> xsddmxList = myAdapter1.getDatas();
            List<Map<String,Object>> salezcmx = FastJsonUtils.mergSumRecordsByKeyAndVal(xsddmxList,"spdnid","sl");
            salezcmx  = FastJsonUtils.findRecordsByKeyAndValNot(salezcmx,"sellpolicysetid",null);
            if (salezcmx != null){
                main_data.put("salezcmx",salezcmx);
            }*/
            //更新本次应付
            boolean falge = calculateGz();
            //本次抵扣不能大于订单金额
            if(main_data.get("this_deduction") != null){
                Double bcdk =  Double.parseDouble(main_data.get("this_deduction").toString()),
                        ddje = Double.parseDouble(main_data.get("order_money").toString());
                if(bcdk > ddje){
                    Toast.makeText(mContext,mContext.getString(R.string.save_bcdk_text),Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            //本次应付 不能小于0
            if(main_data.get("this_pay_money") != null){
                Double bcyf =  Double.parseDouble(main_data.get("this_pay_money").toString());
                if(bcyf < 0){
                    Toast.makeText(mContext,mContext.getString(R.string.save_bdyf_text),Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return super.saveOrEdit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Map<String,Object> resMap = new HashMap<>(),dataMap = null,contentMap = null;
        int index = 0;
        String res = "";
        if (intent == null){
            return;
        }
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            if (requestCode == request_code_file){
                resMap.putAll(basicMap);
            }else{
                return;
            }

        }else{
            resMap = so.getParms();
            dataMap = FastJsonUtils.strToMap(resMap.get("data").toString());
            index = Integer.parseInt(resMap.get("index").toString());
        }

        
        String type = resMap.get("type").toString(),class_name="",package_name,code;
        package_name = this.getClass().getPackage().getName();
        try {
            code = resMap.get("code").toString();
            //销售订单 字段 更新处理
            if (!type.equals("upload")){
                updateXsddmx(menu_code,dataMap,code);
            }
            //关联档案 套餐 赠品
            if (resMap.get("gldata")!= null){
                List<Map<String,Object>> gldata = FastJsonUtils.getBeanMapList(resMap.get("gldata").toString());
                List<Map<String,Object>> mxdata = FastJsonUtils.getBeanMapList(resMap.get("mxdata").toString());
                List<Map<String,Object>> removedata = FastJsonUtils.getBeanMapList(resMap.get("removedata").toString());
                ActivityController.updateMx(mContext,resultCode,gs_map,type,package_name,this,dataMap,index,gldata,mxdata,removedata);
            }else if (type.equals("upload")){
                //执行上传动作
                resMap.put("field",resMap.get("code"));
                resMap.put("code",main_data.get("code"));
                String tempId = "";
                if (main_data.get("id") == null || TextUtils.isEmpty(main_data.get("id").toString())){
                    tempId = IniUtils.getFixLenthString(5);
                }else{
                    tempId = main_data.get("id").toString();
                }
                resMap.put("id",tempId);
                resMap.put("filePath",FileUtil.getPath(mContext,intent.getData()));
                uploadFile(resMap);
            }else{
                ActivityController.updateMx(mContext,resultCode,gs_map,type,package_name,this,dataMap,index);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算规则
        boolean falge = calculateGz();

    }

    private void uploadFile(final Map<String,Object> resMap) {
        try {
            Net.UPLOAD_URL = AppUtils.app_address+"/menu/uploadFileToQiniu";
            File file = new File(resMap.get("filePath").toString());
            byte[] buffer = FileUtils.File2Bytes(file);
            Net.upload(Net.UPLOAD_URL, file.getName(), buffer, FastJsonUtils.mapTOmapStr(resMap), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responseStr = response.body().string();

                    if(200 <= response.code() && response.code() <= 299){
                        final JSONObject json = JSONObject.parseObject(responseStr);
                        if(json.getString("status").equals("ok")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,mContext.getString(R.string.upload_succeed),Toast.LENGTH_SHORT).show();
                                    if (json.get("fileDetail") != null){
                                        Map<String,Object> restMap1 = (Map)parms.get(resMap.get("field").toString()+"_upload");
                                        if (restMap1 != null){
                                            upLoadMap.putAll(restMap1);
                                            JSONArray jsonArray = (JSONArray) json.get("fileDetail");
                                            if (jsonArray != null && jsonArray.size() == 1){
                                                Map<String,Object> m2 = FastJsonUtils.strToMap(jsonArray.get(0).toString());
                                                FastJsonUtils.mapTOmapByParams(main_data,m2,upLoadMap);
                                                ActivityController.updateBasicItem(tableView,(BasicItem) tableView.getItem(mClickIndex),main_form,mClickIndex,m2.get("filename").toString());
                                            }
                                        }
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(mContext,mContext.getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(mContext,mContext.getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                    }
                    Log.d(getClass().getName(), responseStr);
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean updateXsddmx(String menu_code,Map<String,Object>dataMap,String code){

        boolean state = false;
        switch (menu_code+code){
            case "SaleOrderSaleOrderDetail":
                //更新 presl  predj  prehsdj preje
                dataMap.put("pre_quantity",dataMap.get("quantity"));
                dataMap.put("pre_price",dataMap.get("price"));
                dataMap.put("pre_tax_price",dataMap.get("tax_price"));
                dataMap.put("pr_money",dataMap.get("money"));

                if (dataMap.get("rowid").equals(dataMap.get("dyrowid"))){
                    dataMap.put("dyrowid",0);
                }
                break;
        }
        return  state;
    }

}