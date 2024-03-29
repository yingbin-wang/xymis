package com.cn.wti.activity.rwgl.myfile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.activity.dialog.FileSelectDialog;
import com.cn.wti.activity.rwgl.saledaily.Salesdaily_editActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.RecyclerViewUtils;
import com.cn.wti.util.app.ToastUtils;
import com.cn.wti.util.app.dialog.DatePickDialogUtil;
import com.cn.wti.util.app.qx.QxUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.file.FileUtil;
import com.cn.wti.util.net.Net;
import com.cn.wti.util.number.FileUtils;
import com.cn.wti.util.number.IniUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.model.BasicItem;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wticn.wyb.wtiapp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyFileActivity extends BaseEdit_NoTable_Activity{

    private Map<String,Object> parmsMap,main_data = new HashMap<String, Object>(),resMap = new HashMap<>(),map;
    private List<Map<String,Object>> fileList = new ArrayList<Map<String,Object>>();
    private String pars;
    private RecyclerView fileListRview;
    private MyAdapter1 mAdapter1;
    private ScrollView scrollView;
    private LinearLayout rqxz;
    private TextView_custom fileName;
    private ImageView openFile;
    RelativeLayout main_form;
    private FileSelectDialog fileSelectDialog;
    private String id,code,fileName1="";
    private int request_code_file = 100981;
    private int CAMERA_RESULT_CODE = 1004,CROP_RESULT_CODE=1005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_myfile;
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomActionBarTheme);
        //加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isInit = initData();
                if (isInit){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                        }
                    });

                }
            }
        }).start();

    }

    public boolean initData(){
        mContext = MyFileActivity.this;
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return  false;
        }
        parmsMap = so.getParms();
        menu_code = parmsMap.get("menucode").toString();
        id = parmsMap.get("id").toString();
        code = parmsMap.get("code").toString();
        menu_name="附件";
        main_data.putAll(parmsMap);

        pars = new ListParms(menu_code,"id:"+id+",code:"+code+",name:"+menu_code).getParms();
        Object res = ActivityController.getData2ByPost(mContext,"menu","findClUploadFilesByIdAndName", StringUtils.strTOJsonstr(pars));
        if (res != null && res instanceof JSONArray){
            List<Map<String,Object>> res_array = (List<Map<String, Object>>) res;
            if (res_array.size() >0){
                fileList.clear();;
                fileList.addAll(res_array);
            }
            return  true;

        }else{
            return false;
        }
    }

   public void createView(){
       main_form = (RelativeLayout) findViewById(R.id.main_form);
       main_title.setText(menu_name);
       openFile = (ImageView) findViewById(R.id.openFile);
       openFile.setOnClickListener(this);
       scrollView = (ScrollView) findViewById(R.id.scrollView);

       //显示文件列表
       fileListRview = (RecyclerView)findViewById(R.id.fileListRview);
       RecyclerViewUtils.setLayoutManagerHeight(mContext,fileListRview,1);
       //文件名称
       fileName = (TextView_custom) findViewById(R.id.fileName);

       //创建并设置Adapter
       mAdapter1 = new MyAdapter1(fileList);
       fileListRview.setAdapter(mAdapter1);
       reshDataUI();
   }

    @Override
    public void onClick(View v) {
        Object res;
        switch (v.getId()){
            case R.id.openFile:
                if (QxUtils.getInstance(MyFileActivity.this).lacksPermissions(mContext)){//读写权限没开启
                    ActivityCompat.requestPermissions(this,QxUtils.getInstance(MyFileActivity.this).permissions,0);
                }else {
                    //读写权限已开启 是打开相机还是选择文件
                    showLoginDialog();
                }
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    public void showLoginDialog() {
        fileSelectDialog = new FileSelectDialog(null, new FileSelectDialog.DialogClick() {

            @Override
            public void photographClick() {
                if (QxUtils.getInstance(MyFileActivity.this).cameraIsCanUse()) {
                    openSysCamera();
                } else {
                    ToastUtils.showToast(mContext,"您未打开相机权限");
                }
            }

            @Override
            public void selectCLick() {
                openSelectFile();
            }

            @Override
            public void deleteItem() {
                closeDialog();
            }
        });

        fileSelectDialog.show(getFragmentManager(), "fileSelectDialog");
    }

    /**
     * 打开系统相机
     */
    private void openSysCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileName.setText(DateUtil.getNowStamp()+".png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.wticn.wyb.wtiapp.fileprovider", new File(Environment.getExternalStorageDirectory(), fileName.getText().toString()));
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                    new File(Environment.getExternalStorageDirectory(), fileName.getText().toString())));
        }

        startActivityForResult(cameraIntent, CAMERA_RESULT_CODE);
    }

    private void openSelectFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/doc|image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES,
                    new String[]{Constant.IMAGE,Constant.PDF});
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "请选择要上传的文件"), request_code_file);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        File tempFile = null;
        if (requestCode == request_code_file){
            //执行上传动作
            resMap.put("field","");
            resMap.put("code",main_data.get("code"));
            String tempId = "";
            if (main_data.get("id") == null || TextUtils.isEmpty(main_data.get("id").toString())){
                tempId = IniUtils.getFixLenthString(5);
            }else{
                tempId = main_data.get("id").toString();
            }
            if (intent != null && intent.getData() != null){
                resMap.put("id",tempId);
                resMap.put("filePath",FileUtil.getPath(mContext,intent.getData()));
                resMap.put("menucode",main_data.get("menucode"));
                resMap.put("name",main_data.get("menucode"));
                ActivityController.uploadFile(mContext,resMap);
            }
        }else if (requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK) {
            tempFile = new File(Environment.getExternalStorageDirectory(), fileName.getText().toString());
            if (tempFile != null) {
                String cropName = fileName.getText().toString().replace(".png","crop.png");
                File cropFile= FileUtils.bitmapCompress(getContentResolver(),FileUtil.getUriFromFile(mContext,tempFile),cropName);
                resMap.put("field","");
                resMap.put("code",main_data.get("code"));
                String tempId = "";
                if (main_data.get("id") == null || TextUtils.isEmpty(main_data.get("id").toString())){
                    tempId = IniUtils.getFixLenthString(5);
                }else{
                    tempId = main_data.get("id").toString();
                }
                resMap.put("id",tempId);
                resMap.put("filePath",cropFile.getAbsolutePath());
                resMap.put("menucode",main_data.get("menucode"));
                resMap.put("name",main_data.get("menucode"));
                ActivityController.uploadFile(mContext,resMap);
                deleteFile(cropName);
            }
        }

        closeDialog();
    }

    public void closeDialog() {
        if (fileSelectDialog != null) fileSelectDialog.dismiss();
    }

    /**
     * 执行上传成功方法
     * @param map
     */
    public void uploadSuccess(final Map<String,Object> map){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,mContext.getString(R.string.upload_succeed),Toast.LENGTH_SHORT).show();
                if (map.get("fileDetail") != null){
                    Map<String,Object> tempMap = ActivityController.uploadMap;
                    JSONArray jsonArray = (JSONArray) map.get("fileDetail");
                    if (jsonArray != null && jsonArray.size() == 1){
                        Map<String,Object> m2 = FastJsonUtils.strToMap(jsonArray.get(0).toString());
                        FastJsonUtils.mapTOmapByParams(main_data,m2,tempMap);
                        fileName.setText(m2.get("filename").toString());
                        fileList.add(m2);
                        mAdapter1.notifyDataSetChanged();
                    }
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==0){
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i]!=-1){
                    //T.showShort(mContext,"权限设置成功");
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);
                }
            }
        }
    }

    public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ViewHolder> {
        public List<Map<String,Object>> datas = null;
        Map<String,Object> data_map = null;
        public MyAdapter1(List<Map<String,Object>> datas) {
            this.datas = datas;
        }

        public List<Map<String,Object>> getDatas(){
            return datas;
        }

        public void reshData(List<Map<String,Object>> datas){
            this.datas = datas;
            notifyDataSetChanged();
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_myfile,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            data_map = datas.get(position);
            viewHolder.itemFileName.setText(FastJsonUtils.getVal(data_map,"filename"));
            viewHolder.itemContainer.setTag(position);
            viewHolder.itemFileName.setOnClickListener(viewHolder);
            viewHolder.itemFileName.setTag(position);
            viewHolder.itemFileDelete.setOnClickListener(viewHolder);
            viewHolder.itemFileDelete.setTag(position);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView_custom itemFileName;
            public ImageView itemFileDelete;
            public LinearLayout itemContainer;

            public ViewHolder(View view) {
                super(view);
                itemFileName = (TextView_custom) view.findViewById(R.id.item_file_name);
                itemFileDelete = (ImageView) view.findViewById(R.id.item_file_delete);
                itemContainer = (LinearLayout) view;
            }

            @Override
            public void onClick(View v) {
                int index = 0;
                String main_str = "";
                switch (v.getId()) {
                    case R.id.item_file_delete:
                        index = (int) v.getTag();
                        data_map = datas.get(index);

                        final int finalIndex = index;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,String> map = new HashMap<>();
                                map.put("filename",data_map.get("filename").toString());
                                map.put("name",data_map.get("name").toString());
                                map.put("newfilename",data_map.get("newfilename").toString());
                                map.put("id",data_map.get("id").toString());
                                try {
                                    Net.post("menu/deletefile", map, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT);
                                                }
                                            });

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mContext,mContext.getString(R.string.success_operation),Toast.LENGTH_SHORT);
                                                    fileList.remove(finalIndex);
                                                    mAdapter1.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                        break;
                    //查看
                    case R.id.item_file_name:
                        index = (int) v.getTag();
                        data_map = datas.get(index);
                        Map<String,Object> map = new HashMap<>();
                        map.put("menucode",menu_code);
                        map.put("fileName",data_map.get("filename"));
                        map.put("filePath",data_map.get("newfilename") == null ? "" :data_map.get("newfilename") );
                        ActivityController.showFile(mContext,map);
                        break;
                }

            }
        }
    }

}
