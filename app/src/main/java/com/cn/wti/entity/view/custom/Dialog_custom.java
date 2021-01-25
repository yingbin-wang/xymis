package com.cn.wti.entity.view.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/10/24.
 */
public class Dialog_custom extends Dialog{

    private static List<Map<String,Object>> resList = null;
    private Context context;
    private int img_id;
    private EditText csEditText;
    private ImageButton showImb;
    private ListView item_list;
    private String[] items;

    private String service_name,method_name,pars,key;

    protected Dialog_custom(Context context) {
        super(context);
    }

    protected Dialog_custom(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected Dialog_custom(Context context, int themeResId) {
        super(context, themeResId);
    }

/*    public Dialog_custom(Context context,String service_name1,String method_name1,String pars1,String key1,String[] items){
        this.context = context;
        this.service_name = service_name1;
        this.method_name = method_name1;
        this.pars = pars1;
        this.key = key1;
        this.items = items;
    }*/

/*    public class Builder_test extends Builder{

        public Builder_test(Context context) {
            super(context);
        }

        public Builder_test(Context context, int themeResId) {
            super(context, themeResId);
        }
    }*/


    public void create(){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.activity_dialog, null);
        csEditText = (EditText) layout.findViewById(R.id.showcs);
        showImb = (ImageButton) layout.findViewById(R.id.showImg);
        item_list = (ListView) layout.findViewById(R.id.list);

        item_list.setAdapter(new SimpleAdapter(context));
        showImb.setOnClickListener(new ShowImageB());
        new AlertDialog.Builder(context).setTitle("自定义布局").setView(layout);
     }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public class ShowImageB implements View.OnClickListener{

        private String cs = "";

        public ShowImageB(){
            this.cs = csEditText.getText().toString();
        }

        @Override
        public void onClick(View view) {
            if (!cs.equals("")){
                Object res =  ActivityController.getData4ByPost(service_name,method_name,pars);
                if(res != null){
                    resList = (List<Map<String, Object>>) res;
                    items = FastJsonUtils.ListMapToListStr(resList,key);
                    ((SimpleAdapter)item_list.getAdapter()).notifyDataSetChanged();
                    item_list.invalidate();
                }
            }
        }
    }

    public class SimpleAdapter extends BaseAdapter{

        public Context context;

        public  SimpleAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(context);
            textView.setText(items[i]);
            return textView;
        }
    }
}
