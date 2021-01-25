package com.cn.wti.activity.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.dialog.OnePromptMessageDialog;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.TableUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.app.qx.BussinessUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.UITableView;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonMxEditActivity extends BaseEdit_02Activity implements DialogInterface.OnClickListener{

    private Map<String,Object> initMap,showMap,mxMap = new HashMap<>();
    int RESULT_OK = 1,REQUEST_CODE =1,MXREQUEST_CODE =2;
    private  String pars,id,dyrowid="";
    private List<Map<String,Object>> mx_data;

    Object res;

    private UITableView tableView,mx1_tableView,mx2_tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        createView();
    }

    public void initData() {
        mContext = CommonMxEditActivity.this;
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();
        menu_code = parmsMap.get("menucode").toString();
        menu_name=parmsMap.get("menuname").toString();

        if (parmsMap.get("isEdit") != null){
            isEdit = (int) parmsMap.get("isEdit");
        }

        RESULT_OK = Integer.parseInt(parmsMap.get("RESULT_OK").toString());
        super.initData(parmsMap.get("code").toString());
    }

    public  void createView(){

       /* Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();*/

        if (parmsMap!= null){
            try {
                maings_list = FastJsonUtils.getBeanMapList(parmsMap.get("mainGs").toString()) ;
                modifyGsList(maings_list);
                main_data = FastJsonUtils.strToMap(parmsMap.get("mainData").toString());
                mx_data = FastJsonUtils.getBeanMapList(parmsMap.get("mxData").toString());
                ywtype = parmsMap.get("type").toString();
                //pars = parmsMap.get("parms").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //添加 主数据
            if(maings_list != null && maings_list.size()>0){
                tableView = (UITableView) findViewById(R.id.tableView);
                if (main_data.get("dyrowid")!= null && !main_data.get("dyrowid").toString().equals("0")){
                    dyrowid = main_data.get("dyrowid").toString();
                }else{
                    if (main_data.get("rowid")!= null){
                        dyrowid = main_data.get("rowid").toString();
                    }
                }
                Map<String,Object> resMap = new HashMap<String,Object>();
                resMap.putAll(main_data);
                tableView.set_zhuData(resMap);
                CustomClickListener listener = new CustomClickListener(CommonMxEditActivity.this,maings_list,tableView,"",dyrowid);

                if (isEdit == 0 || isEdit ==1){
                    createList(tableView,listener,ywtype,null);
                    btn_ok.setVisibility(View.VISIBLE);
                }else if (isEdit == 2){
                    createList(tableView,null,"2",null);
                    btn_ok.setVisibility(View.GONE);
                }else{
                    createList(tableView,listener,ywtype,null);
                    btn_ok.setVisibility(View.VISIBLE);
                }
                /*createList(tableView,listener,ywtype,null);*/
                tableView.setButton_ok(btn_ok);
                //添加明细数据
                tableView.set_dataList(mx_data);
            }
        }

        // 主界面控件
        btn_back.setOnClickListener(new MyOnCliclListener(CommonMxEditActivity.this,tableView,RESULT_OK,"",null,null,""));
        btn_ok.setOnClickListener(new MyOnCliclListener(CommonMxEditActivity.this,tableView,RESULT_OK,parmsMap.get("code").toString(),null,null,"",ywtype));
    }

    private List<Map<String,Object>> modifyGsList(List<Map<String,Object>> gsList){
        String[] jssjContents = Constant.taxrate_array;
        List<String> list = FastJsonUtils.arrayToListStr(jssjContents),resList = new ArrayList<String>();
        if (AppUtils.user.getSjjs().equals("2") ) return  gsList;

        String code="";
        for (Map<String,Object> contentMap:gsList ) {
            code = contentMap.get("code").toString();
            if (list.contains(code)){
                contentMap.put("is_visible_phone",false);
            }
        }
        return  gsList;
    }

    @Override
    public boolean checkXsddmx() {
        mxMap = tableView.get_zhuData();
        boolean state = true;
        switch (menu_code+parmsMap.get("code").toString()){
            case "SaleOrderSaleOrderDetail":
                //验证 单价 与 政策最小单价
                if (mxMap.get("sellpolicysetid") != null && !BussinessUtils.sellpolicysetid.equals("0")
                        &&  mxMap.get("tax_price") != null && mxMap.get("member_price") != null && Double.parseDouble(mxMap.get("tax_price").toString()) < Double.parseDouble(mxMap.get("member_price").toString())){
                    mDialog = OnePromptMessageDialog.getInstance(mContext,"提示信息","此价格低于销售政策中的最低销售单价，将不享受其政策！若想继续享受政策,需要重新选择商品,确认要更改么？");
                    if (mDialog!= null){
                        mDialog.show();
                    }
                    state = false;
                }else{
                    state = true;
                }
                break;
        }
        return  state;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //执行 确定 和 取消 操作
        switch (menu_code+parmsMap.get("code").toString()){
            case "SaleOrderSaleOrderDetail":
                if (which == -1){
                    //清除赠品
                    List<Map<String,Object>> gxdataList = tableView.get_dataList();
                    List<Map<String,Object>> zpMapList = FastJsonUtils.ListPdTOListByKeys(gxdataList,"dyrowid,mainid".split(","),tableView.getMxIndex()+","+mxMap.get("goodsid").toString());
                    if (tableView.get_gxdataList()!= null){
                        tableView.get_gxdataList().clear();
                    }
                    tableView.set_removeList(zpMapList);
                    setOnlyCheck(false);
                    setCheck(false);
                    BussinessUtils.sellpolicysetid = "0";
                    WeiboDialogUtils.closeDialog(mDialog);

                    //验证价格
                    checkJg();

                    //重新提交
                    btn_ok.performClick();

                }else if (which == -2){
                    //重新计算价格 含税单价 和 税额
                    //20171219 wang 记录含税单价
                    if (mxMap.get("pre_tax_price")!= null && mxMap.get("pre_tax_price").toString().equals("0")){
                        mxMap.put("pre_tax_price",mxMap.get("member_price").toString());
                    }
                    update_colval_Main("tax_price",mxMap.get("pre_tax_price").toString(),tableView,main_data);
                    update_colval_Main("sellpolicysetid",BussinessUtils.sellpolicysetid,tableView,main_data);
                    WeiboDialogUtils.closeDialog(mDialog);
                }
                break;
        }
    }

    private void checkJg(){
        //mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"验证价格处理中...");
        if(mxMap.get("ispricesys").equals("1") && AppUtils.user.getIspricecontroll().equals("1")){
            if (Double.parseDouble(mxMap.get("tax_price").toString()) < Double.parseDouble(mxMap.get("min_price").toString())){
                mxMap.put("pre_tax_price",mxMap.get("min_price").toString());
                update_colval_Main("tax_price",mxMap.get("pre_tax_price").toString(),tableView,main_data);
                update_colval_Main("sellpolicysetid",BussinessUtils.sellpolicysetid,tableView,main_data);
            }
           /* Map<String,Object> parmsMap = new HashMap<String, Object>(),parms_map = null;
            parmsMap.put("seviceName", "spdn");
            parmsMap.put("methodName", "findSpdn_priceByIdAndRq");
            parmsMap.put("parms", "{\"main\":\"khdnid:khdnid,trantime:trantime,jgdjid:jgdjid,ywlx:xsckd_val\",\"this\":\"sl:sl,spdnid:spdnid\"}");
            parms_map = FastJsonUtils.strToMap(parmsMap.get("parms").toString());
            Set<String> parmsSet = parms_map.keySet();
            String parms_test ="",main_parms="",mx_parms="",who_parm="";
            for (String parm_key :parmsSet){
                switch (parm_key){
                    case "main":
                        parms_test = parms_map.get("main").toString();
                        try {
                            main_parms = (String) ReflectHelper.callMethod2(mContext,"setDialogParms",new Object[]{parms_test,null},String.class,UITableView.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "this":
                        parms_test = parms_map.get("this").toString();
                        mx_parms = TableUtils.getParms(parms_test.split(","),tableView);
                        break;
                }
            }

            if (!main_parms.equals("") || !mx_parms.equals("")){
                if (who_parm.equals("")){
                    who_parm+= main_parms+mx_parms;
                }else{
                    who_parm+= ","+main_parms+mx_parms;
                }
            }

            who_parm = StringUtils.strTOJsonstr(who_parm);
            //取得返回数据
            final String finalWho_parm = who_parm;
            final  Map<String,Object> copy_parmsMap = parmsMap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String dj = ActivityController.getDataByPost(mContext,copy_parmsMap.get("seviceName").toString(),copy_parmsMap.get("methodName").toString(), finalWho_parm).toString();
                    if (dj != null && !dj.equals("")){
                        FastJsonUtils.strToMap(dj)
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                    }
                }
            }).start();*/

        }
    }
}
