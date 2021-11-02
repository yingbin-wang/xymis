package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ObjectArrayCodec;
import com.cn.wti.activity.common.BadgeActionProvider;
import com.cn.wti.activity.dialog.PopWinCommonEdit;
import com.cn.wti.activity.dialog.PopWinShare;
import com.cn.wti.activity.myTask.MyTask_edit_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.model.BasicItem;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.UITableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public abstract class BaseEdit_01Activity extends BaseEditActivity{

    protected  Map<String,String> initMap;
    private String pars,version="",status="";
    private ActionBar actionBar;
    protected String[] contents;
    protected Map<String,Object> qxMap;
    private ImageButton title_back2,title_ok2 = null;
    private TextView title_name2 = null;
    private PopWinCommonEdit popWinShare = null;
    int height=0;
    protected List<String> qxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*actionBar = this.getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setLogo(R.mipmap.navigationbar_back);*/

        title_back2 = findViewById(R.id.title_back2);
        title_ok2 = findViewById(R.id.title_ok2);
        title_back2.setOnClickListener(new ViewOnClick());
        title_ok2.setOnClickListener(new ViewOnClick());
        title_ok2.setBackgroundResource(R.mipmap.documentmore);
        title_name2 = findViewById(R.id.title_name2);

        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        //获取权限
        if (parmsMap.get("qxMap") != null){
            qxMap = FastJsonUtils.strToMap(parmsMap.get("qxMap").toString());
        }
        if (parmsMap.get("menucode") != null){
            menu_code = parmsMap.get("menucode").toString();
            menu_name = parmsMap.get("menuname").toString();
            ywtype = parmsMap.get("type").toString();
            //actionBar.setTitle(menu_name);
            title_name2.setText(menu_name);
        }
        //20171226 如果 有 serviceName 直接用serviceName
        if (parmsMap.get("serviceName")!= null){
            serviceName = parmsMap.get("serviceName").toString();
        }
        if (parmsMap.get("isEdit")!= null){
            isEdit = (int) parmsMap.get("isEdit");
            JSONObject maninData = null;
            if (parmsMap.get("mainData") instanceof HashMap){
                maninData = FastJsonUtils.mapToJson((Map<String, Object>) parmsMap.get("mainData"));
            }else{
                maninData = (JSONObject) parmsMap.get("mainData");
            }

            approvalstatus = maninData.getString("approvalstatus");
        }
        initData();
        //创建 操作handler
        myHandler= new MyHandler(mContext);
        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
        myHandler.sendEmptyMessageDelayed(1,1000);
    }

    public void initData() { }

    public boolean deleteAll(){
        //审批状态
        if (main_data !=null && main_data.get("approvalstatus")!=null){
            approvalstatus  = main_data.get("approvalstatus").toString();
        }
        if(approvalstatus !=null && !approvalstatus.equals("0") && !approvalstatus.equals("") ){
            Toast.makeText(mContext,"单据在流程审批中不允许此动作！",Toast.LENGTH_SHORT).show();
            return false;
        }else if( main_data.get("estatus").toString().equals("7") ){
            Toast.makeText(mContext,"单据在审核中不允许此动作！",Toast.LENGTH_SHORT).show();
            return false;
        }

        //版本号
        if (main_data.get("version")!= null){
            version = main_data.get("version").toString();
        }
        /*pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+id+"\",\"version\":\""+version
                +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"
                +"\"}";*/

        pars = new ListParms(menu_code,"DATA_IDS:"+id+",version:"+version,"deleteAll").getParms();

        //20171226 wang 如果存在 serviceName 否则 取 menucode
        if (serviceName.equals("")){
            serviceName = menu_code;
        }

        new Thread(){
            @Override
            public void run() {
                boolean isdelete = ActivityController.execute(mContext,serviceName,"deleteAll",pars);
                if(isdelete){
                    //addAction();
                    current_type = "delete";
                    if (current_type.equals("delete")){
                        Intent intent = new Intent();
                        intent.putExtra("index",index);
                        intent.putExtra("type",current_type);
                        intent.putExtras(AppUtils.setParms("",main_data));
                        setResult(1,intent);
                        ((Activity)mContext).finish();
                    }
                    WeiboDialogUtils.closeDialog(mDialog);
                }
            }
        }.start();
        return  true;
    }

    /**
     * 新增动作
     */
    public  void addAction(){
        //添加主数据
        if(maings_list != null && maings_list.size()>0){
            tableView = (UITableView) findViewById(R.id.tableView);
            tableView.clear();
            CustomClickListener listener = new CustomClickListener(getApplicationContext(),maings_list,tableView,"");
            createList(tableView,listener,"add",initMap);
            ywtype = "add";
            id = "";
        }
        //添加明细数据
        addMxAction();
    }

    public  void addMxAction(){

        Map<String,Object> gs_map =  (Map<String, Object>) get_catch().get(menu_code+"_mapAll"),res_map;
        Object res;
        String package_name="",code="",class_name="";
        String[] contents;
        try {
            Set<String>  sets = gs_map.keySet();
            int i = 1;
            for (String key:sets) {
                if(key.equals("add")){
                    continue;
                }

                i = Integer.parseInt(key.replace("mx","").replace("gs_list",""));

                switch (i){
                    case 1:
                        //添加明细1
                        myAdapter1.getDatas().clear();
                        myAdapter1.notifyDataSetChanged();
                        break;
                    case 2:
                        //添加明细2
                        myAdapter2.getDatas().clear();
                        myAdapter2.notifyDataSetChanged();
                        break;
                    case 3:
                        //添加明细3
                        myAdapter3.getDatas().clear();
                        myAdapter3.notifyDataSetChanged();
                        break;
                    case 4:
                        //添加明细4
                        myAdapter4.getDatas().clear();
                        myAdapter4.notifyDataSetChanged();
                        break;
                    case 5:
                        //添加明细5
                        myAdapter5.getDatas().clear();
                        myAdapter5.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存和编辑方法
     */

    public boolean saveOrEdit() {

        if (main_data.get("estatus") != null && main_data.get("estatus").equals("7")){
            Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
            return false;
        }else if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
            Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
            return false;
        }
        //20171229 WANG 修改 先赋值 再验证
        List<IListItem> main_list = tableView.getIListItem();
        BasicItem item = null;
        String code,val,method,resid="";
        for (int i=0,n = main_list.size();i< n ;i++){
            item = (BasicItem) main_list.get(i);
            code = item.getCode();
            if (item.getVal()!= null){
                val = item.getVal();
            }else{
                val = item.getSubtitle();
            }

            if(val != null){
                main_data.put(code,val);
            }

            //清除 不要字段
            clearColumn(main_data);

        }
        //验证 有问题 20171229 wang 以后处理
        if(!tableView.getEditTextValidator().validate()){
            return false;
        }

        // 保存验证
        if(!saveValidate()){return false;};

        //保存再次执行规则
        calculateGz();

        Map<String,Object> mxMap = new HashMap<String, Object>();
        Map<String,Object> gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
        if (gs_map != null){
            Set<String> sets = gs_map.keySet();
            for (String key:sets) {
                String index = key.replace("mx","").replace("gs_list","");
                if (key.equals("add")){continue;}
                Map<String,Object> mx_map = (Map<String, Object>) gs_map.get(key);
                code = StringUtils.firstLowerStr(mx_map.get("code").toString());
                switch (index){
                    case "1":
                        mxMap.put(code,myAdapter1.getDatas());
                        break;
                    case "2":
                        mxMap.put(code,myAdapter2.getDatas());
                        break;
                    case "3":
                        mxMap.put(code,myAdapter3.getDatas());
                        break;
                    case "4":
                        mxMap.put(code,myAdapter4.getDatas());
                        break;
                    case "5":
                        mxMap.put(code,myAdapter5.getDatas());
                        break;
                    default:
                        break;
                }
            }
        }
        id = super.saveOrEdit(mContext,main_list,mxMap);
        if (!id.equals("") && id.indexOf("err")< 0){
            return  true;
        }
        return  false;
    }

    private boolean saveValidate(){
        Map<String,Object> rule_map = (Map<String, Object>) get_catch().get(menu_code+"_rule");
        Map<String,Object> gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
        if(rule_map != null){
            Object save_rule = rule_map.get("save");
            if(save_rule != null){
                Map<String,Object> save_map = null;
                if (save_rule instanceof JSONObject){
                    save_map = (Map<String, Object>) save_rule;
                }else{
                    Toast.makeText(mContext,getString(R.string.err_saverule_notfound),Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(save_map.get("count")!= null){
                    List<Map<String,Object>> count_list = (List<Map<String, Object>>) save_map.get("count");
                    for (Map<String,Object> count_map:count_list) {
                        if(count_map.get("from") != null){
                            String from = count_map.get("from").toString();
                            Map<String,Object> mx_map = ActivityController.getMxOptions(from,gs_map);
                            String name = (String) mx_map.get("gl_name");
                            if (name == null || name.equals("")){
                                Toast.makeText(mContext,getString(R.string.err_mxdata_notfound),Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            int index = Integer.parseInt(name.replace("mx","").replace("gs_list",""));
                            switch (index){
                                case 1:
                                    if (myAdapter1.getDatas() == null || myAdapter1.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 2:
                                    if (myAdapter2.getDatas() == null || myAdapter2.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 3:
                                    if (myAdapter3.getDatas() == null || myAdapter3.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 4:
                                    if (myAdapter4.getDatas() == null || myAdapter4.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 5:
                                    if (myAdapter5.getDatas() == null || myAdapter5.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                            }
                        }
                    }
                }
                if(save_map.get("validate")!= null){
                    List<Map<String,Object>> validate_list = (List<Map<String, Object>>) save_map.get("validate");
                    for (Map<String,Object> validate_map:validate_list) {
                        String expression = validate_map.get("expression").toString();

                        //增加 post 请求验证方式
                        Object fromObject = validate_map.get("from");
                        if (fromObject != null){
                            if (fromObject instanceof  JSONObject){
                                Map<String,Object> fromMap = (Map<String, Object>) fromObject;
                                final String serviceName = fromMap.get("serviceName").toString();
                                final String methodName = fromMap.get("href").toString();
                                String parms = fromMap.get("parms").toString(),test_parms="";
                                if (!parms.equals("")){
                                    test_parms = ActivityController.setParms(fromMap,mContext);
                                }

                                final String finalTest_parms = test_parms;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Object res = ActivityController.getDataByPost(mContext,serviceName,methodName, finalTest_parms);
                                        if (res != null && !res.toString().contains("(abcdef)")){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                }
                                            });
                                        }
                                    }
                                }).start();

                            }else if (fromObject instanceof String){
                                try {
                                    String expn = ActivityController.replaceExpression(mContext,expression);
                                    String res = SizheTool.eval2(expn,"2");
                                    if (res == null){Toast.makeText(mContext,"表达式异常",Toast.LENGTH_SHORT).show();return  false;}
                                    if (res.equals("1.0") || res.equals("1.00")||res.equals("1")){
                                        if(!ActivityController.validateCount(mContext,validate_map,gs_map)){
                                            Toast.makeText(mContext,validate_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                            return  false;
                                        };
                                    }
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
        }
        return true;
    }

    public  void createView(){

        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        if (parmsMap!= null){
            try {
                maings_list = FastJsonUtils.getBeanMapList(parmsMap.get("mainGs").toString()) ;
                if (parmsMap.get("mainData") instanceof Map){
                    main_data = (Map<String, Object>) parmsMap.get("mainData");
                }else{
                    main_data = FastJsonUtils.strToMap(parmsMap.get("mainData").toString());
                }

                id = parmsMap.get("id").toString();
                ywtype = parmsMap.get("type").toString();
                table_postion = parmsMap.get("table_postion").toString();
                index = parmsMap.get("index").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //添加 主数据
            if(maings_list != null && maings_list.size()>0){
                tableView = (UITableView) findViewById(R.id.tableView);
                CustomClickListener listener = new CustomClickListener(mContext,maings_list,tableView,"");
                if(ywtype.equals("add")){
                    createList(tableView,listener,ywtype,initMap);
                }else{
                    if (isEdit == 0 || isEdit ==1){
                        createList(tableView,listener,ywtype,null);
                    }else if (isEdit == 2){
                        createList(tableView,null,"2",null);
                    }else{
                        createList(tableView,listener,ywtype,null);
                    }
                }

                tableView.setTag(this);
            }
        }

        initStatus();
    }

    void initStatus(){
        if (main_data == null){return;}
        int estatus = 0,approvalstatus=0;
        if (main_data.get("estatus") != null && !main_data.get("estatus") .equals("")){
            estatus = Integer.parseInt(main_data.get("estatus").toString());
        }else{
            estatus =1;
        }

        if(main_data.get("approvalstatus") != null){
            approvalstatus = Integer.parseInt(main_data.get("approvalstatus").toString());
        }else{
            approvalstatus = 0;
        }

        if(main_data.get("estatus") != null && estatus == 7){
            status = "check";
        }else if(main_data.get("estatus") != null && estatus == 1 && main_data.get("approvalstatus")!= null && approvalstatus == 1){
            status = "sp";
        }else if(main_data.get("estatus") != null && estatus == 1){
            status = "save";
        }else if(main_data.get("estatus") == null){
            status = "add";
        }
    }

    public class ViewOnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.title_back2:
                    finish();
                    break;
                case R.id.title_ok2:
                    showPopWindow();
                    break;
                case android.R.id.home:
                    if (!current_type.equals("")){
                        Intent intent = new Intent();
                        intent.putExtra("index",index);
                        intent.putExtra("type",current_type);
                        if (main_data != null){
                            intent.putExtras(AppUtils.setParms("",main_data));
                        }else{
                            intent.putExtras(AppUtils.setParms("",new HashMap<String,Object>()));
                        }
                        setResult(1,intent);
                    }
                    finish();
                    break;
                case R.id.layout_add:
                    addAction();
                    closePopWin();
                    status = "add";
                    break;
                case R.id.layout_save:
                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "保存中...");
                    myHandler.sendEmptyMessageDelayed(2,1000);
                    closePopWin();
                    status = "save";
                    break;
                case R.id.layout_delete:
                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "删除中...");
                    myHandler.sendEmptyMessageDelayed(3,1000);
                    closePopWin();
                    status = "delete";
                    break;
                case R.id.layout_check:
                    if (main_data.get("estauts") != null && main_data.get("estauts").equals("7")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }
                    pars = new ListParms(menu_code,"DATA_IDS:"+id+",version:"+version+",estatus:7","check").getParms();
                    auditAll(mContext,"auditAll",pars);
                    closePopWin();
                    status = "check";
                    break;
                case R.id.layout_uncheck:
                    if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }
                    pars = new ListParms(menu_code,"DATA_IDS:"+id+",version:"+version+",estatus:1","check").getParms();
                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "撤审中...");
                    HandlerThread handlerThread = new HandlerThread("handlerThread");
                    handlerThread.start();
                    MyHandler handler = new MyHandler(handlerThread.getLooper(),mContext);
                    Message msg = handler.obtainMessage();
                    //利用bundle对象来传值
                    Bundle b = new Bundle();
                    b.putString("pars", pars);
                    b.putString("method", "auditAll");
                    msg.what = 5;
                    msg.setData(b);
                    msg.sendToTarget();
                    closePopWin();
                    status = "uncheck";
                    break;
                case R.id.layout_fujian:
                    main_data.put("menucode",menu_code);
                    ActivityController.startMyFileActivity(mContext,main_data);
                    closePopWin();
                    break;
            }
        }
    }

    void showPopWindow(){

        main_data.put("menu_code",menu_code);
        if (!menu_code.equals("mycustomer")){
            if (status.equals("add")){
                height = AppUtils.dp2pxInt(mContext,64);
            }else if (status.equals("save")){
                height = AppUtils.dp2pxInt(mContext,160);
            }else if (status.equals("sp")){
                height = AppUtils.dp2pxInt(mContext,64);
            }else if (status.equals("check")){
                height = AppUtils.dp2pxInt(mContext,128);
            }else{
                height = AppUtils.dp2pxInt(mContext,160);
            }
        }else{
            if (ywtype.equals("add")){
                height = AppUtils.dp2pxInt(mContext,32);
            }else{
                height = AppUtils.dp2pxInt(mContext,64);
            }
        }

        if (popWinShare == null) {
            checkQx();
            //自定义的单击事件
            ViewOnClick paramOnClickListener = new ViewOnClick();
            popWinShare = new PopWinCommonEdit(BaseEdit_01Activity.this, paramOnClickListener,260,height,qxList);
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        popWinShare.dismiss();
                    }
                }
            });
        }else{
            popWinShare.setHeight(height);
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(title_ok2, 0, 0);
        //如果窗口存在，则更新
        popWinShare.update();
        popWinShare.setEstatus(main_data,isUpdate);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pars = new ListParms(menu_code,"id:"+main_data.get("id")+",code:"+main_data.get("code")+",name:"+menu_code).getParms();
                List<Map<String,Object>> res_array = null;
                Object res = ActivityController.getData2ByPost(mContext,"menu","findClUploadFilesByIdAndName", StringUtils.strTOJsonstr(pars));
                if (res != null && res instanceof JSONArray){
                    res_array = (List<Map<String, Object>>) res;

                }
                final List<Map<String, Object>> finalRes_array = res_array;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (finalRes_array.size() >0){
                            popWinShare.setBadgeText(finalRes_array.size());
                        }else{
                            popWinShare.setBadgeText(0);
                        }

                    }
                });
            }
        }).start();
    }

    void closePopWin(){
        if (popWinShare != null){
            popWinShare.dismiss();
        }
    }

    public void checkQx(){
        if (qxMap != null){
            if(qxMap.get("addQx") != null && qxMap.get("addQx").toString().equals("true")){
                qxList.add("add");
            }
            if (qxMap.get("modQx")!= null && qxMap.get("modQx").toString().equals("true")){
                qxList.add("save");
            }
            if (qxMap.get("delQx")!= null && qxMap.get("delQx").toString().equals("true")){
                qxList.add("delete");
            }
            if (qxMap.get("checkQx")!= null && qxMap.get("checkQx").toString().equals("true")){
                qxList.add("check");
            }
            if (qxMap.get("uncheckQx")!= null && qxMap.get("uncheckQx").toString().equals("true")){
                qxList.add("uncheck");
            }

            qxList.add("fj");
        }
    }


    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       if ((isEdit == 0 || isEdit == 1)){
           if (approvalstatus != null && approvalstatus.equals("1")){
               return  false;
           }
           menu = checkQx(menu);
           return super.onCreateOptionsMenu(menu);
       }else{
           return true;
       }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (main_data == null){return  true;}
        int estatus = 0,approvalstatus=0;
        if (main_data.get("estatus") != null && !main_data.get("estatus") .equals("")){
            estatus = Integer.parseInt(main_data.get("estatus").toString());
        }else{
            estatus =1;
        }

        if(main_data.get("approvalstatus") != null){
            approvalstatus = Integer.parseInt(main_data.get("approvalstatus").toString());
        }else{
            approvalstatus = 0;
        }

        setMenuVisible(menu.findItem(R.id.upload),true);
        if(main_data.get("estatus") != null && estatus == 7){
            //setMenuVisible(menu.findItem(R.id.add),true);
            setMenuVisible(menu.findItem(R.id.save),true);
            setMenuVisible(menu.findItem(R.id.uncheck),true);
            //隐藏
            setMenuVisible(menu.findItem(R.id.delete),false);
            setMenuVisible(menu.findItem(R.id.check),false);

        }else if(main_data.get("estatus") != null && estatus == 1 && main_data.get("approvalstatus")!= null && approvalstatus == 1){
            //setMenuVisible(menu.findItem(R.id.add),true);
            //隐藏
            setMenuVisible(menu.findItem(R.id.save),false);
            setMenuVisible(menu.findItem(R.id.delete),false);
            setMenuVisible(menu.findItem(R.id.check),false);
            setMenuVisible(menu.findItem(R.id.uncheck),false);

        }else if(main_data.get("estatus") != null && estatus == 1){
            //setMenuVisible(menu.findItem(R.id.add),true);
            setMenuVisible(menu.findItem(R.id.save),true);
            setMenuVisible(menu.findItem(R.id.delete),true);
            if (isUpdate){
                setMenuVisible(menu.findItem(R.id.check),false);
            }else{
                setMenuVisible(menu.findItem(R.id.check),true);
            }
            //隐藏
            setMenuVisible(menu.findItem(R.id.uncheck),false);

        }else if(main_data.get("estatus") == null){
            //setMenuVisible(menu.findItem(R.id.add),true);
            setMenuVisible(menu.findItem(R.id.save),true);
            //隐藏
            setMenuVisible(menu.findItem(R.id.delete),false);
            setMenuVisible(menu.findItem(R.id.check),false);
            setMenuVisible(menu.findItem(R.id.uncheck),false);
            setMenuVisible(menu.findItem(R.id.upload),false);
        }

        this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!current_type.equals("")){
                    Intent intent = new Intent();
                    intent.putExtra("index",index);
                    intent.putExtra("type",current_type);
                    if (main_data != null){
                        intent.putExtras(AppUtils.setParms("",main_data));
                    }else{
                        intent.putExtras(AppUtils.setParms("",new HashMap<String,Object>()));
                    }

                    setResult(1,intent);
                }
                this.finish();
                break;
            case R.id.add:
                addAction();
                break;
            case R.id.save:
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "保存中...");
                myHandler.sendEmptyMessageDelayed(2,1000);
                //saveOrEdit();
                break;
            case R.id.delete:
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "删除中...");
                myHandler.sendEmptyMessageDelayed(3,1000);

                break;
            case R.id.check:
                if (main_data.get("estauts") != null && main_data.get("estauts").equals("7")){
                    Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
                    return false;
                }else if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
                    Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
                    return false;
                }
                //版本号
                if (main_data.get("version")!= null){
                    version = main_data.get("version").toString();
                }
                pars = new ListParms(menu_code,"DATA_IDS:"+id+",version:"+version+",estatus:7","check").getParms();
                auditAll(mContext,"auditAll",pars);
                break;
            case R.id.uncheck:
                if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
                    Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
                    return false;
                }

                //版本号
                if (main_data.get("version")!= null){
                    version = main_data.get("version").toString();
                }

                pars = new ListParms(menu_code,"DATA_IDS:"+id+",version:"+version+",estatus:1","check").getParms();
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "撤审中...");
                HandlerThread handlerThread = new HandlerThread("handlerThread");
                handlerThread.start();
                MyHandler handler = new MyHandler(handlerThread.getLooper(),mContext);
                Message msg = handler.obtainMessage();
                //利用bundle对象来传值
                Bundle b = new Bundle();
                b.putString("pars", pars);
                b.putString("method", "auditAll");
                msg.what = 5;
                msg.setData(b);
                msg.sendToTarget();
                break;
            case R.id.upload:
                main_data.put("menucode",menu_code);
                ActivityController.startMyFileActivity(mContext,main_data);
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    */

    /*public Menu checkQx(Menu menu){
        if (qxMap != null){
            if(qxMap.get("addQx") != null && qxMap.get("addQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.add,1,"新增");
            }
            if (qxMap.get("modQx")!= null && qxMap.get("modQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.save,2,"保存");
            }
            if (qxMap.get("delQx")!= null && qxMap.get("delQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.delete,3,"删除");
            }
            if (qxMap.get("checkQx")!= null && qxMap.get("checkQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.check,4,"审核");
            }
            if (qxMap.get("uncheckQx")!= null && qxMap.get("uncheckQx").toString().equals("true")){
                menu.addSubMenu(0,R.id.uncheck,5,"撤审");
            }

            menu.addSubMenu(0,R.id.upload,6,"附件");
        }
        return  menu;
    }

    private void setMenuVisible(MenuItem view,boolean visibile) {
        if (view != null) {
            view.setVisible(visibile);
        }
    }*/

}
