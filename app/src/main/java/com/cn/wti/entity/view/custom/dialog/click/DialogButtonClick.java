package com.cn.wti.entity.view.custom.dialog.click;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;

import com.cn.wti.entity.view.custom.WebView_custom;
import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoic_Dn_Adapter;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/5.
 */

public class DialogButtonClick implements View.OnClickListener {

    private Adapter adapter;
    private String cs,service_name,method_name,pars,col_name,title_str;
    private List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
    private EditText eidt_et;
    private Context mContext = null;
    private Dialog mDialog = null;

    public DialogButtonClick(Adapter adapter, View editText_cs, String service_name, String method_name, String pars, String col_name, List<Map<String,Object>> mList,String title_str, Context mContext){
        this.adapter = adapter;
        if (editText_cs instanceof EditText){
            this.eidt_et = (EditText) editText_cs;
        }
        this.service_name = service_name;
        this.method_name = method_name;
        this.pars = pars;
        this.col_name = col_name;
        this.mList.addAll(mList) ;
        this.title_str = title_str;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnShow:
                String parms = "",css="";
                cs = eidt_et.getText().toString();
                if(!cs.equals("")){
                    if (!pars.equals("")){
                        if (col_name.equals("name")){
                            if (service_name.equals("staff")){
                                css = col_name +" like 'aYdF1"+cs+"'bYdF1 or login_name like 'aYdF1"+cs+"'bYdF1";
                            }else{
                                css = col_name +" like 'aYdF1"+cs+"'bYdF1 or code like 'aYdF1"+cs+"'bYdF1";
                            }

                        }else if(col_name.indexOf("~")>=0 && (service_name.equals("customer") ||
                                service_name.equals("account") || service_name.equals("goods") ||
                                service_name.equals("goodsType") || service_name.equals("department"))||
                                service_name.equals("supplier")){
                            String[] keys = col_name.split("~");
                            for (String key_str:keys) {
                                if (css.equals("")){
                                    css += key_str +" like 'aYdF1"+cs+"'bYdF1 or code like 'aYdF1"+cs+"'bYdF1";
                                }else{
                                    css += " or "+key_str +" like 'aYdF1"+cs+"'bYdF1";
                                }
                            }
                        }else if(col_name.indexOf("~")>=0){
                            String[] keys = col_name.split("~");
                            for (String key_str:keys) {
                                if (css.equals("")){
                                    css += key_str +" like 'aYdF1"+cs+"'bYdF1";
                                }else{
                                    css += " or "+key_str +" like 'aYdF1"+cs+"'bYdF1";
                                }
                            }
                        }else{

                            if(service_name.equals("customer") ||
                                service_name.equals("account") || service_name.equals("goods") ||
                                service_name.equals("goodsType") || service_name.equals("department")||
                                    service_name.equals("supplier")){
                                if (css.equals("")){
                                    css += col_name +" like 'aYdF1"+cs+"'bYdF1 or code like 'aYdF1"+cs+"'bYdF1";
                                }else{
                                    css += " or "+col_name +" like 'aYdF1"+cs+"'bYdF1";
                                }
                            }else{
                                css += " or "+col_name +" like 'aYdF1"+cs+"'bYdF1";
                            }
                        }

                        parms = pars.subSequence(0,pars.length()-1) + ",\"cxlx\":\"1\",\"parms\":\""+css+"\"}";
                    }
                }else{
                    parms = pars;
                }

                //执行查询

                final String finalParms = parms;
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"查询中....");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Object res =  ActivityController.getData4ByPost(service_name,method_name, finalParms);
                        if(res != null && !res.toString().contains("(abcdef)")){
                            mList.clear();
                            mList.addAll((List<Map<String, Object>>) res);
                            handler.sendEmptyMessageDelayed(1,1000);
                        }else{
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (adapter instanceof SingleChoicAdapter){
                        SingleChoicAdapter singleChoicAdapter = (SingleChoicAdapter) adapter;
                        singleChoicAdapter.clear();
                        singleChoicAdapter.refreshData(mList);
                        singleChoicAdapter.setResList_(mList);
                    }else if (adapter instanceof MultiChoic_Dn_Adapter){
                        MultiChoic_Dn_Adapter multiChoic_dn_adapter = (MultiChoic_Dn_Adapter) adapter;
                        multiChoic_dn_adapter.refreshData(mList,new boolean[mList.size()]);
                    }
                    WeiboDialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };
}
