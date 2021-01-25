package com.cn.wti.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseEdit_01Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

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
        Map<String,Object> resMap = null,dataMap = null,contentMap = null;
        int index;
        String res = "";
        if (intent == null){
            return;
        }
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        resMap = so.getParms();
        dataMap = FastJsonUtils.strToMap(resMap.get("data").toString());
        index = Integer.parseInt(resMap.get("index").toString());
        String type = resMap.get("type").toString(),class_name="",package_name,code;
        package_name = this.getClass().getPackage().getName();
        try {
            code = resMap.get("code").toString();
            //销售订单 字段 更新处理
            updateXsddmx(menu_code,dataMap,code);
            //关联档案 套餐 赠品
            if (resMap.get("gldata")!= null){
                List<Map<String,Object>> gldata = FastJsonUtils.getBeanMapList(resMap.get("gldata").toString());
                List<Map<String,Object>> mxdata = FastJsonUtils.getBeanMapList(resMap.get("mxdata").toString());
                List<Map<String,Object>> removedata = FastJsonUtils.getBeanMapList(resMap.get("removedata").toString());
                ActivityController.updateMx(mContext,resultCode,gs_map,type,package_name,this,dataMap,index,gldata,mxdata,removedata);
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