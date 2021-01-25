package com.cn.wti.activity.fwq_address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cn.wti.entity.MyApplication;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.SlideView;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.DatabaseUtils;

import java.util.List;
import java.util.Map;

public class FwqAddressActivity extends Activity {

    private ImageButton ib_back,ib_ok;
    private TextView tv_title;
    private ListView lv;
    private List<Map<String,Object>> fwqAddressList = null;
    private  System_one system_one = null;

    int REQUEST_CODE = 1;

    private SlideView mLastSlideViewWithStatusOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_fwq_address);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.layout_title_01);

        ib_back = (ImageButton)findViewById(R.id.title_back);
        ib_ok = (ImageButton)findViewById(R.id.title_ok);
        tv_title = (TextView)findViewById(R.id.title_name);
        ib_back.setOnClickListener(new ImageButtonClickListener1());
        ib_ok.setOnClickListener(new ImageButtonClickListener1());

        tv_title.setText("服务器地址");

        /**
         * 从 父 窗体 得到参数 address
         * 如果 不为空直接 赋值 给当前 地址list 否则 从数据库重新读取
         */
        system_one = (System_one)getIntent().getSerializableExtra("parms");

        AppUtils.dbUtils  = DatabaseUtils.getInstance(FwqAddressActivity.this, "wtmis.db");

        if (system_one != null){

            if (system_one.getList()!= null && system_one.getList().size() >0){
                fwqAddressList = system_one.getList();
            }
        }

        if (fwqAddressList == null){
            fwqAddressList = AppUtils.dbUtils.exec_select("sys_fwqaddress",new String[]{"id","name","address"},null,null,null,null,null);
        }

        lv = (ListView) findViewById(R.id.fwq_address_lv);
        lv.setAdapter(new FwqAddress_listAdapter());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FwqAddressActivity.this,FwqAddress_addActivity.class);
                Map<String,Object> map = fwqAddressList.get(position);
                intent.putExtras(AppUtils.setParms("edit",map));
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == 1){
            fwqAddressList =  AppUtils.dbUtils.exec_select("sys_fwqaddress",new String[]{"id","name","address"},null,null,null,null,null);
            ((FwqAddress_listAdapter)lv.getAdapter()).notifyDataSetChanged();
            lv.invalidate();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private  class ImageButtonClickListener1 implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.title_back:
                    Intent intent1 = new Intent();
                    intent1.putExtras(AppUtils.setParms("",fwqAddressList));
                    setResult(1, intent1);
                    FwqAddressActivity.this.finish();
                    break;
                case  R.id.title_ok:
                    //执行添加动作
                    Intent intent = new Intent(FwqAddressActivity.this,FwqAddress_addActivity.class);
                    startActivityForResult(intent,REQUEST_CODE);
                    break;
                default:
                    break;
            }
        }
    }

    public class  FwqAddress_listAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fwqAddressList.size();
        }

        @Override
        public Object getItem(int position) {
            return fwqAddressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            SlideView slideView = (SlideView) convertView;
            if (slideView == null) {
                View itemView = getLayoutInflater().inflate(R.layout.actuvity_fwqaddress_listview_item, null);

                slideView = new SlideView(FwqAddressActivity.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(new SlideView.OnSlideListener() {
                    @Override
                    public void onSlide(View view, int status) {
                        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
                            mLastSlideViewWithStatusOn.shrink();
                        }

                        if (status == SLIDE_STATUS_ON) {
                            mLastSlideViewWithStatusOn = (SlideView) view;
                        }
                    }
                });
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
           final Map<String,Object> map = fwqAddressList.get(position);
            map.put("slideView",slideView);
            slideView.shrink();

            holder.fwq_address_lv_item_name_.setText(map.get("name").toString());
            holder.fwq_address_lv_item_address_.setText(map.get("address").toString());

            holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.delete_holder) {
                        Toast.makeText(MyApplication.getContextObject(),"test",Toast.LENGTH_LONG);
                        //执行数据库删除动作
                        String  where_str =  map.get("name").toString();
                        if (where_str != null && !where_str.equals("")){
                            where_str = where_str.replace(",","<dh>");
                        }
                        int count =  AppUtils.dbUtils.exec_delete("sys_fwqaddress"," name = ?",where_str);
                        if (count >0){
                            fwqAddressList.remove(position);
                            ((FwqAddress_listAdapter)lv.getAdapter()).notifyDataSetChanged();
                            lv.invalidate();
                        }
                    }
                }
            });

            return slideView;
        }
    }

    private static class ViewHolder {
        public TextView fwq_address_lv_item_name_;
        public TextView fwq_address_lv_item_address_;
        public ViewGroup deleteHolder;


        ViewHolder(View view) {
            //名称
            fwq_address_lv_item_name_ = (TextView) view.findViewById(R.id.fwq_address_lv_item_name_);
            //地址
            fwq_address_lv_item_address_ = (TextView) view.findViewById(R.id.fwq_address_lv_item_address_);
            deleteHolder = (ViewGroup)view.findViewById(R.id.delete_holder);
        }
    }

}
