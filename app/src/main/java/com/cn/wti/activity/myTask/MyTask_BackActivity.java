package com.cn.wti.activity.myTask;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ease.ui.EaseChartActivity.mContext;

public class MyTask_BackActivity extends BaseActivity {

    private ListView lv1;
    private EditText msg/*,qtr_codes*/;
    private ImageButton ibOk,ibCanel;

    private Map<String,Object> processAttrMap; //流程属性
    private List<Map<String,Object>> backActivityList = new ArrayList<Map<String,Object>>(),//可驳回任务
                                      formAttrList = new ArrayList<Map<String,Object>>() ;//表单属性
    private String fqr = "",
                    expression = "",index="";

    private String [] passArray = null;
    int RESULT_OK = 2;
    private ActionBar actionBar;
    private TextView title;
    private Dialog mDialog;
    /*private CheckBox fqrCb,jbrCb;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        setContentView(R.layout.activity_mytask_back);
        AppUtils.setStatusBarColor(this);
        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.layout_title_02);
        //设置标题
        title = (TextView)actionBar.getCustomView().findViewById(R.id.title_name2);
        title.setText("驳回任务");

        //返回按钮
        ibCanel = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_back2);
        ibOk = (ImageButton) actionBar.getCustomView().findViewById(R.id.title_ok2);

        ibCanel.setOnClickListener(new MyOnClickListener());
        ibOk.setOnClickListener(new MyOnClickListener());

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);

        getView();
        reshData();

    }

    /**
     * 得到视图
     */
    private void getView(){
        lv1 = (ListView) findViewById(R.id.myTask_examine_lv);
        msg = (EditText) findViewById(R.id.myTask_back_msg);
        lv1.setAdapter(new MyListAdapter2(MyTask_BackActivity.this));
    }

    /**
     * 得到数据
     */
    private void reshData(){
        Map<String,Object> map = new HashMap<String, Object>();
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        index = String.valueOf(parmsMap.get("index"));

        //可驳回任务
        if(parmsMap.get("backActivity")!= null){
            try {
                backActivityList = FastJsonUtils.getBeanMapList(parmsMap.get("backActivity").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    }

    public  class MyListAdapter2 extends BaseAdapter{

        private Context context;
        HashMap<String,Boolean> states=new HashMap<String,Boolean>();

        public MyListAdapter2(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return backActivityList.size();
        }

        @Override
        public Object getItem(int position) {
            return backActivityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.activity_mytask_back_lvitem, null);
                holder.id = (TextView) convertView.findViewById(R.id.lvItem_id);
                holder.name = (TextView) convertView.findViewById(R.id.lvItem_name);
                holder.type = (TextView) convertView.findViewById(R.id.lvItem_type);
                holder.assignee_code = (TextView)convertView.findViewById(R.id.lvItem_assignee_code);
                holder.assignee_name = (TextView) convertView.findViewById(R.id.lvItem_assignee_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Map<String, Object> map = backActivityList.get(position);
            int nn = backActivityList.size();
            String id = map.get("id").toString(),assignee="";
            if (map.get("assignee") != null){
                assignee = map.get("assignee").toString();
            }
            String type = map.get("type").toString();
            String name = map.get("name").toString();

            if (!assignee.equals("")){
                holder.assignee_code.setText(assignee);
            }
            if(map.get("assignee_name") != null && !map.get("assignee_name").toString().equals("")){
                holder.assignee_name.setText(map.get("assignee_name").toString());
            }

            holder.id.setText(id);
            holder.name.setText(name);
            holder.type.setText(type);


            final RadioButton radio=(RadioButton) convertView.findViewById(R.id.radio_btn);
            holder.rdBtn = radio;
            holder.rdBtn.setButtonDrawable(R.drawable.selector_checkbox1);
            holder.rdBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    //重置，确保最多只有一项被选中
                    for(String key:states.keySet()){
                        states.put(key, false);
                        Map<String, Object> map = backActivityList.get(Integer.parseInt(key));
                        map.put("checked",false);
                    }
                    states.put(String.valueOf(position), radio.isChecked());
                    Map<String, Object> map = backActivityList.get(position);
                    String assignee ="";
                    if (map.get("assignee") != null){
                        assignee = map.get("assignee").toString();
                    }

                    if (!assignee.equals("")){
                        map.put("assignee_code",assignee);
                    }
                    if(map.get("assignee_name") != null && !map.get("assignee_name").toString().equals("")){
                        map.put("assignee_name",map.get("assignee_name").toString());
                    }

                    map.put("checked",radio.isChecked());

                    MyListAdapter2.this.notifyDataSetChanged();
                }
            });

            boolean res=false;
            if(states.get(String.valueOf(position)) == null || states.get(String.valueOf(position))== false){
                res=false;
                states.put(String.valueOf(position), false);
            }
            else
                res = true;

            holder.rdBtn.setChecked(res);

            return convertView;
        }
    }

    private class ViewHolder{
        LinearLayout background;
        TextView id;
        TextView name;
        TextView type;
        TextView assignee_code;
        TextView assignee_name;
        RadioButton rdBtn;
    }

    /**
     * OK 返回 按钮 事件
     */
    public class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.title_back2:
                    MyTask_BackActivity.this.finish();
                    break;
                case  R.id.title_ok2:

                    String taskId = processAttrMap.get("ID_").toString();
                    String processInstanceId =processAttrMap.get("PROC_INST_ID_").toString(),
                            business = processAttrMap.get("BUSINESS_KEY_").toString() ;

                    int num = 0;
                    Map<String,Object> tr,csz = new HashMap<String, Object>();
                    String activityId = "",name,type="",assignee="",assignee_name="",temp,task_name="";
                    if (backActivityList != null && backActivityList.size()>0){
                        for(int i=0,n = backActivityList.size();i<n;i++){
                            tr = backActivityList.get(i);
                            boolean checked = false;
                            if(tr.get("checked")!=null && (boolean)tr.get("checked")){
                                checked = Boolean.parseBoolean(tr.get("checked").toString());
                            }else{
                                checked =false;
                            }
                            if (checked){
                                num+=1;
                                type = tr.get("type").toString();
                                if(tr.get("assignee_code") !=null){
                                    assignee=tr.get("assignee_code").toString();
                                }
                                activityId = tr.get("id").toString();
                            }
                        }

                        if(num > 1){
                            Toast.makeText(MyTask_BackActivity.this,"仅能选择一个节点",Toast.LENGTH_SHORT).show();
                            activityId="";
                            type="";
                            return;
                        }
                    }else{
                        Toast.makeText(MyTask_BackActivity.this,"当前任务节点没有可以驳回的节点",Toast.LENGTH_SHORT).show();
                    }

                    String message_person="",message=msg.getText().toString(),qtr_code;
                    if(type.equals("startEvent")){
                        message_person=fqr;
                    }else{
                        message_person=assignee;
                    }

                    /*//添加消息通知人     发送消息
                    if(fqrCb.isChecked()){
                        if(!fqr.equals("")){
                            message_person+=fqr;
                        }
                    }
                    //其他 人
                    qtr_code = qtr_codes.getText().toString();
                    if(!qtr_code.equals("")){
                        if(!message_person.equals("")){
                            message_person+=","+qtr_code;
                        }else{
                            message_person=qtr_code;
                        }
                    }*/

                    if(!activityId.equals("")){

                        if(activityId !=null){
                            csz.put("taskId",taskId);
                            csz.put("activityId",activityId);
                            csz.put("PROC_INST_ID_", processAttrMap.get("PROC_INST_ID_").toString());
                            csz.put("bussiness",business);
                            csz.put("type",type);
                            csz.put("type",type);
                            csz.put("assignee",assignee);
                            csz.put("message_person",message_person);
                            csz.put("message",message);
                            csz.put("userId",AppUtils.app_username);
                            csz.put("zydnid",AppUtils.user.get_zydnId());
                            csz.put("zydn",AppUtils.user.get_zydnName());
                            //定义流程名称
                            csz.put("PROC_DEF_NAME_",processAttrMap.get("PROC_DEF_NAME_"));
                            csz.put("NAME_",processAttrMap.get("NAME_"));

                            final String finalparms = FastJsonUtils.mapToString(csz);
                            mDialog = WeiboDialogUtils.createLoadingDialog(MyTask_BackActivity.this,"驳回中...");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String res = null;
                                    try {
                                        res = HttpClientUtils.exectePost("process","backProcess",finalparms);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    final String finalRes = res;
                                    final String finalRes1 = res;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String,Object> map = new HashMap<String, Object>();
                                            if(finalRes != null && !finalRes.toString().contains("(abcdef)")){
                                                map = FastJsonUtils.strToMap(finalRes);
                                                if(map != null && map.get("state").toString().equals("success")){
                                                    Intent intent = new Intent();
                                                    intent.putExtra("state","success");
                                                    intent.putExtra("index",index);
                                                    setResult(RESULT_OK,intent);
                                                    MyTask_BackActivity.this.finish();
                                                }else{
                                                    Toast.makeText(MyTask_BackActivity.this,"失败",Toast.LENGTH_SHORT).show();
                                                }

                                            }else{
                                                if (finalRes != null){
                                                    Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(finalRes.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            WeiboDialogUtils.closeDialog(mDialog);
                                        }
                                    });

                                }
                            }).start();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
