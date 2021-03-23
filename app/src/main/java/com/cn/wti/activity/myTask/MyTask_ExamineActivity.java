package com.cn.wti.activity.myTask;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.TaskAdapter;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.other.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyTask_ExamineActivity extends BaseActivity {

    private RecyclerView myTask_examine_lv;
    private EditText msg;
    private ImageButton ibOk,ibCanel,jbr_linear;
    private TextView myTask_examine_lvItem_jbrxz,title;
    private ActionBar actionBar;

    private Map<String,Object> processAttrMap,//流程属性
                                 exportationActivityMap, //下一步任务
                                 notPassActivityMap, //不通过Map
                                 formMap
                                  ;
    private List<Map<String,Object>> backActivityList = new ArrayList<Map<String,Object>>(),//可驳回任务
                                      historyCommentList = new ArrayList<Map<String,Object>>(), //历史信息
                                      exportationActivityList = new ArrayList<Map<String,Object>>(),
                                      copyexportationActivityList = new ArrayList<Map<String,Object>>(),
                                      formAttrList = new ArrayList<Map<String,Object>>() ;//表单属性
    private String fqr = "",expression = "",sftg = "",BUSINESS_KEY_,ID_;

    private String [] passArray = null;
    int RESULT_OK = 2;
    private LinearLayout main_form;
    private Dialog mDialog;
    private HandlerThread handlerThread;
    private Context mContext;
    private boolean [] mCheck;
    private TaskAdapter taskAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (mDialog != null) {
                        WeiboDialogUtils.closeDialog(mDialog);
                    }
                    break;
                case 2:
                    setResult();
                    break;
                case 3:
                    send();
                    break;
            }
        }
    };

    private String way_type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        setContentView(R.layout.activity_mytask_examine);
        AppUtils.setStatusBarColor(this);
        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.layout_title_02);
        //设置标题
        title = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
        title.setText("任务办理");

        //返回按钮
        ibCanel = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);

        ibOk = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_ok2);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);

        mContext = MyTask_ExamineActivity.this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (reshData()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getView();
                        }
                    });
                }
            }
        }).start();

    }

    /**
     * 得到视图
     */
    private void getView(){
        msg = (EditText) findViewById(R.id.myTask_examine_msg);

        passArray = getResources().getStringArray(R.array.sys_pass);

        ibCanel = (ImageButton) findViewById(R.id.title_back2);
        ibOk = (ImageButton) findViewById(R.id.title_ok2);

        ibOk.setOnClickListener(new MyOnClickListener());
        ibCanel.setOnClickListener(new MyOnClickListener());

        main_form = (LinearLayout) findViewById(R.id.main_form);


        myTask_examine_lv = (RecyclerView) findViewById(R.id.myTask_examine_lv);
        ActivityController.setLayoutManager(myTask_examine_lv,mContext);
        //创建并设置Adapter
        mCheck = new boolean[exportationActivityList.size()];
        taskAdapter = new TaskAdapter(this,exportationActivityList,AppUtils.getScreenWidth(mContext),main_form,R.layout.activity_mytask_examine_lvitem,mCheck);
        myTask_examine_lv.setAdapter(taskAdapter);

    }

    public void setResult(){
        if (mDialog != null){
            WeiboDialogUtils.closeDialog(mDialog);
        }

        Intent intent = new Intent();
        intent.putExtra("state","success");
        setResult(RESULT_OK,intent);
        MyTask_ExamineActivity.this.finish();
    }

    /**
     * 得到数据
     */
    private boolean reshData(){
        Map<String,Object> map = new HashMap<String, Object>();
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return false;
        }
        parmsMap = so.getParms();

        formMap = FastJsonUtils.strToMap(parmsMap.get("formData").toString());

        if (parmsMap.get("sftg")!= null){
            sftg = parmsMap.get("sftg").toString();
            BUSINESS_KEY_ = parmsMap.get("BUSINESS_KEY_").toString().replace(":","<m>");
            ID_ = parmsMap.get("ID_").toString();
            String pars = "{ID_:"+ID_+",BUSINESS_KEY_:"+BUSINESS_KEY_+",sftg:"+sftg+",username:"+AppUtils.app_username+"}";
            pars = StringUtils.strTOJsonstr(pars);
            final Object res = ActivityController.getData4ByPost("process","getExportationActivity",pars);
            if(res != null && !res.toString().contains("(abcdef)")){
                Map<String,Object> resMap = (Map<String, Object>) res;
                exportationActivityMap = new HashMap<String, Object>();
                exportationActivityMap.putAll(resMap);
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res != null){
                            Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                MyTask_ExamineActivity.this.finish();
            }
        }
        //下一步任务
        if(exportationActivityMap!= null){

            if(exportationActivityMap.get("exportation")!=null){
                exportationActivityList.clear();
                List<Map<String,Object>> exportationList = (List<Map<String,Object>>)exportationActivityMap.get("exportation");
                if (sftg.equals("不通过") && exportationList.size() == 0){
                    Map<String,Object> exportationMap = new HashMap<>();
                    exportationMap.put("name","不通过结束");
                    exportationMap.put("type","endEvent");
                    exportationActivityList.add(exportationMap);
                }else{
                    exportationActivityList.addAll((List<Map<String,Object>>)exportationActivityMap.get("exportation"));
                }
            }

            if (exportationActivityMap.get("fqr")!=null){
                fqr = exportationActivityMap.get("fqr").toString();
            }
        }

        if (exportationActivityList != null && exportationActivityList.size() ==0 ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyTask_ExamineActivity.this,getString(R.string.err_notnextdata),Toast.LENGTH_SHORT).show();
                }
            });

            this.finish();
            return false;
        }

        //发起人
        if(parmsMap.get("fqr")!= null){
            fqr = parmsMap.get("fqr").toString();
        }
        //流程信息
        if(parmsMap.get("processAttr")!= null){
            processAttrMap = FastJsonUtils.strToMap(parmsMap.get("processAttr").toString());
        }
        //表单属性
        if(parmsMap.get("formAttr")!= null){
            try {
                formAttrList = FastJsonUtils.getBeanMapList(parmsMap.get("formAttr").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public Map<String,Object> getResMap(String res){
        Map<String,Object>resMap = FastJsonUtils.strToMap(res.toString());
        if(resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
            return (Map<String, Object>) resMap.get("data");
        }
        return  null;
    }

    /**
     * OK 返回 按钮 事件
     */
    public class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.title_back2:
                    MyTask_ExamineActivity.this.finish();
                    break;
                case  R.id.title_ok2:
                    mDialog = WeiboDialogUtils.createLoadingDialog(MyTask_ExamineActivity.this, "审批中...");
                    mHandler.sendEmptyMessageDelayed(3,100);
                    break;
                default:
                    break;
            }
        }
    }

    public void  send(){
        String taskId = processAttrMap.get("ID_").toString();
        String processInstanceId =processAttrMap.get("PROC_INST_ID_").toString(),
                business = processAttrMap.get("BUSINESS_KEY_").toString(),
                current_jbr = processAttrMap.get("USERNAME") == null ? "" : processAttrMap.get("USERNAME").toString(),
                current_task_name = processAttrMap.get("NAME_").toString(),
                PROC_DEF_NAME_ = processAttrMap.get("PROC_DEF_NAME_").toString();
        String spyj = msg.getText().toString();

        // 任务集合
        String activitis ="[";
        Map<String,Object> tr = null;
        String activityId = "", name = "",type = "",assignee="",
                assignee_name="",
                temp="",
                task_name = "",
                groups = "",checkedType="";

        int checkcount = 0;

        for(int i=0,n = exportationActivityList.size();i<n;i++){

            tr = exportationActivityList.get(i);

            boolean checked = false;
            if (tr.get("checked") != null){
                checked = Boolean.parseBoolean(tr.get("checked").toString());
            }

            activityId = tr.get("id").toString();
            name = tr.get("name").toString();
            type = tr.get("type").toString();

            if (tr.get("assignee") != null){
                assignee=tr.get("assignee").toString();
            }
            if(tr.get("assignee_name") != null){
                assignee_name=tr.get("assignee_name").toString();
            }

            temp="";
            task_name = "";

            // 判断是 用户任务 还是 组任务
            if ( tr.get("groups") != null){
                groups = tr.get("groups").toString();
            }

            //如果为结束节点
            if(!type.equals("endEvent")){
                if(!groups.equals("")){
                    assignee = tr.get("assignee").toString();
                    if (!assignee.equals("")){
                        if (tr.get("assignee_name") != null){
                            assignee_name = tr.get("assignee_name").toString();
                        }else{
                            if (assignee.indexOf(";")>=0){
                                String[] assignee_s =  assignee.split(";");
                                assignee = assignee_s[0];
                                assignee_name = assignee_s[1];
                            }
                        }
                    }

                }else{
                    assignee_name="";
                }
            }else{
                assignee="";
            }

            if(checked){
                checkcount++;
                checkedType = type;
            }

            if (tr.get("ywtype") != null){
                temp = tr.get("ywtype").toString();
            }

            //验证当前任务是否存在经办人
            if(checked && (assignee.equals("") || assignee.indexOf(",")>=0 ) && !type.equals("endEvent")){
                Toast.makeText(MyTask_ExamineActivity.this,"任务："+name+",未指定经办人",Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(1, 10);
                return;
            }

            if(checked && name.equals("异常结束") ){
                task_name = "异常结束";
            }else if(checked && name.equals("不通过结束")){
                task_name = "不通过结束";
            }

            if( n==1 ){
                checked = true;
            }

            if(i==n-1){
                activitis += "{\"activityId\":\""+activityId+"\",\"assignee\":\""+assignee+"\",\"assignee_name\":\""+assignee_name+"\",\"type\":\""+temp+"\",\"checked\":\""+checked+"\",\"name\":\""+name+"\"}";

            }else{
                activitis += "{\"activityId\":\""+activityId+"\",\"assignee\":\""+assignee+"\",\"assignee_name\":\""+assignee_name+"\",\"type\":\""+temp+"\",\"checked\":\""+checked+"\",\"name\":\""+name+"\"},";
            }
        }

        activitis+="]";

        if(checkcount == 0){
            Toast.makeText(MyTask_ExamineActivity.this,"至少选中一个步骤！",Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(1, 10);
            return;
        }

        // 封装参数
        if(activitis !=null) {

            Map<String, Object> csz = new HashMap<String, Object>(),
                    cs = new HashMap<String, Object>();

            if (!expression.equals("")) {

                String[] expressions = null;
                String csOption = "";
                if (expression.indexOf('~') > 0) {
                    expressions = expression.split("~");
                }

                if (expressions != null && expressions.length > 0) {
                    for (int i = 0; i < expressions.length; i++) {

                        csOption = expressions[i];
                        if (csOption == "") {
                            break;
                        }
                        if (i == expressions.length - 1) {
                            if (formMap.get(csOption) != null) {
                                cs.put(csOption, formMap.get(csOption));
                            }
                        } else {
                            String col = formMap.get(csOption).toString();
                            if (col.indexOf('.') > 0) {
                                cs.put(csOption, Float.parseFloat(col));
                            } else {
                                cs.put(csOption, col);
                            }
                        }
                    }
                } else {
                    csOption = expression;
                }
            }

            temp = "{";
            Set<String> keySet = cs.keySet();
            for (String key : keySet) {

                temp += key + ":" + cs.get(key) + ",";

            }

            if (temp.length() > 1) {
                temp = temp.substring(0, temp.length() - 1);
            }

            temp += "}";

            csz.put("cs", temp);
            csz.put("processInstanceId", processInstanceId);
            csz.put("spyj", spyj);
            csz.put("userid",AppUtils.user.get_id());
            csz.put("ip",AppUtils.app_ip);
            csz.put("device","PHONE");
            //获取表单数据

            String data = FastJsonUtils.mapToStringByfor(formMap);

            csz.put("formData", FastJsonUtils.mapToString(formMap));
            csz.put("activitis", activitis);
            csz.put("business", business);

            int n = exportationActivityList.size();

            if (n == 1) {
                type = checkedType;
            }else if(type.equals("")) {
                type = "endEvent";
            }else{
                type = "userTask";
            }

            if (type.equals("endEvent")) {
                assignee = "";
            }

            csz.put("PROC_DEF_NAME_", task_name);
            csz.put("task_name", task_name);
            csz.put("jbr",current_jbr);
            csz.put("current_task",current_task_name);
            csz.put("state", type);
            csz.put("way_type",way_type);
            csz.put("sftg", sftg);
            csz.put("taskId", taskId);
            csz.put("userId", AppUtils.app_username);
            csz.put("zydnid", AppUtils.user.get_zydnId());
            csz.put("bmdnid", AppUtils.user.get_bmdnId());
            csz.put("zydn", AppUtils.app_username);
            csz.put("isdqfbr","1");
            csz.put("message_person",fqr);
            csz.put("message","");

            final  String finalparms = FastJsonUtils.mapToString(csz);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        final String res =  HttpClientUtils.exectePost("process","saveOrauditFrom", finalparms);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg2="";
                                Map<String, Object> map = new HashMap<String, Object>();
                                if (res != null && !res.equals("") && !res.contains("(abcdef)") && res.contains("{")&& res.contains("}")) {
                                    map = FastJsonUtils.strToMap(res);
                                    if (map == null) {
                                        Toast.makeText(MyTask_ExamineActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                                        mHandler.sendEmptyMessageDelayed(1, 10);
                                        return;
                                    }

                                    if (map.get("data") != null){
                                        map = (Map<String, Object>) map.get("data");
                                    }

                                    if (map.get("msg")!= null){
                                        msg2 = map.get("msg").toString();
                                    }else{
                                        msg2 = "失败";
                                    }

                                    if (map.get("state") != null && map.get("state").toString().equals("success")) {
                                        mHandler.sendEmptyMessageDelayed(2, 1000);
                                    } else {
                                        Toast.makeText(MyTask_ExamineActivity.this,msg2, Toast.LENGTH_SHORT).show();
                                        mHandler.sendEmptyMessageDelayed(1, 10);
                                    }
                                } else {
                                    Toast.makeText(MyTask_ExamineActivity.this,msg2, Toast.LENGTH_SHORT).show();
                                    mHandler.sendEmptyMessageDelayed(1, 10);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }
}
