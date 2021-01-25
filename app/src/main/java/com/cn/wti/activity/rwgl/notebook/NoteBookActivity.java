package com.cn.wti.activity.rwgl.notebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cn.wti.activity.base.BaseList_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.RecyclerViewClickListener;
import com.wticn.wyb.wtiapp.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteBookActivity extends BaseList_NoTable_Activity {


    String pars;
    private Map<String, Object> resMap;
    private TextView title_tv,count_tv;
    private ImageButton btn_back;
    private Button btn_ok;
    private Button btn_selectAll,btn_noelectAll;;
    private LinearLayoutManager mLayoutManager;
    public List<Map<String,Object>> _datalist =  new ArrayList<Map<String,Object>>(); //主格式;
    private ImageView note_bianji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebook_activity);

        //返回 OK 按钮
        btn_back = (ImageButton) findViewById(R.id.title_back2);
        btn_ok = (Button) findViewById(R.id.ok);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (initData1(menu_code, menu_name, "list", pars)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                        }
                    });
                }
                WeiboDialogUtils.closeDialog(mDialog);
            }
        }).start();

    }

    public  void createView(){

        //全选 全不选
        btn_selectAll = (Button) findViewById(R.id.btn_selectAll);
        btn_noelectAll = (Button) findViewById(R.id.btn_noselectAll);

        btn_selectAll.setOnClickListener(this);
        btn_noelectAll.setOnClickListener(this);
        //标题
        title_tv = (TextView) findViewById(R.id.title_name2);
        title_tv.setText(menu_name);
        count_tv = (TextView) findViewById(R.id.count);
        //新增按钮
        note_bianji = (ImageView) findViewById(R.id.note_bianji);
        note_bianji.setOnClickListener(this);

        mRecyclerView1 = (RecyclerView) findViewById(R.id.list_recyclerView);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView1.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView1.setHasFixedSize(true);
        //创建并设置Adapter

        mCheck = new boolean[_datalist.size()];
        mAdapter2 = new MyAdapter2(mContext,_datalist,screenWidth,new String[]{},contents,R.layout.list_item_notebook,mCheck);
        mRecyclerView1.setAdapter(mAdapter2);
        mAdapter2.setViewCount(count_tv);

        // 添加上啦下拉刷新
        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,menu_code,menu_name,"list",pars,mRecyclerView1,1,_datalist));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void initData() {
        menu_code = "notebook";
        menu_name = "记事本";
        mxClass_ = "com.cn.wti.activity.rwgl.notebook.NoteBook_editActivity";
        mContext = NoteBookActivity.this;
        //得到 服务器数据
        parms2 = "initiatorid:" + AppUtils.user.get_zydnId() + ",isDataQxf:1";
        contents= new String[] {"trantime","content"};
    }

    public boolean initData1(String menu_code, String menu_name, String method_name, String pars) {
        pars = new ListParms("0", "0", AppUtils.list_limit, menu_code, parms2,1).getParms();
        this.pars = pars;
        boolean flage = false;

        pars = StringUtils.strTOJsonstr(pars);
        parms = new HashMap<String, Object>();

        Object res = ActivityController.getDataByPost(menu_code, method_name, pars);
        try {
            if (!res.equals("")) {
                resMap = getResMap(res.toString());
                if (resMap.get("results") != null) {
                    recordcount = Integer.parseInt(resMap.get("results").toString());
                    pageIndex = 1;
                    main_datalist = (List<Map<String, Object>>) resMap.get("rows");
                    _datalist.addAll(main_datalist);
                    flage = true;
                }else{
                    if (resMap.get("msg")!= null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    flage = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flage = false;
        }
        return  flage;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back2:
                this.finish();
                break;
            case R.id.ok:
                if (btn_ok.getText().equals("编辑")){
                    btn_noelectAll.setVisibility(View.VISIBLE);
                    btn_selectAll.setVisibility(View.VISIBLE);
                    btn_ok.setText("取消");
                    mAdapter2.setSfVisible(true);
                    mAdapter2.notifyDataSetChanged();
                    note_bianji.setVisibility(View.GONE);
                }else{
                    btn_noelectAll.setVisibility(View.GONE);
                    btn_selectAll.setVisibility(View.GONE);
                    mCheck = new boolean[_datalist.size()];
                    mAdapter2.refreshData(_datalist,mCheck);
                    mAdapter2.setSfVisible(false);
                    mAdapter2.notifyDataSetChanged();
                    btn_ok.setText("编辑");
                    note_bianji.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_selectAll:
                mCheck = new boolean[_datalist.size()];
                for (int i =0,n = mCheck.length;i<n;i++) {
                    mCheck[i] = true;
                }
                mAdapter2.refreshData(_datalist,mCheck);
                break;
            case R.id.btn_noselectAll:

                boolean[] selects =  mAdapter2.getSelectItem();
                String ids = "",veresions="";
                List<Map<String,Object>> deleteList  = new ArrayList<Map<String,Object>>();
                for (int i=0,n=selects.length;i<n;i++){
                    if (selects[i]){
                        deleteList.add(_datalist.get(i));
                        if (ids.equals("")){
                            ids+=_datalist.get(i).get("id");
                        }else{
                            ids+=","+_datalist.get(i).get("id");
                        }

                        if (veresions.equals("")){
                            veresions+=_datalist.get(i).get("version");
                        }else{
                            veresions+=","+_datalist.get(i).get("version");
                        }
                    }
                }

                final List<Map<String,Object>> finaldeleteList = deleteList;
                //执行删除动作
                if (deleteList.size() >0){
                    pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+ids+"\",\"isversion\":\""+1+"\",\"userid\":\""+AppUtils.user.get_id()
                            +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\"}";
                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"删除中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String isDel = ActivityController.executeForResult(mContext,menu_code,"deleteAll",pars);
                            if (!isDel.equals("err")){
                                _datalist.removeAll(finaldeleteList);
                                mCheck = new boolean[_datalist.size()];
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter2.refreshData(_datalist,mCheck);
                                        mAdapter2.setViewCount(count_tv);
                                        WeiboDialogUtils.closeDialog(mDialog);
                                    }
                                });
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(mContext,"请选择要删除的数据！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.note_bianji:
                //执行添加 记事本动作
                Intent intent = new Intent();
                Map<String,Object> parmsMap = new HashMap<String, Object>();
                parmsMap.put("mainData","{}");
                parmsMap.put("index","0");
                parmsMap.put("id","");
                intent.putExtras(AppUtils.setParms("add",parmsMap));
                intent.setClass(NoteBookActivity.this,NoteBook_editActivity.class);
                startActivityForResult(intent,1);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        mAdapter2.setViewCount(count_tv);
    }
}