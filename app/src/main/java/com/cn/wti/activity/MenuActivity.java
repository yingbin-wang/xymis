package com.cn.wti.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.util.db.DatabaseUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends Activity {

    private ExpandableListView expandableListView;
    private List<String> selectedList = new ArrayList<String>();
    private List<String> menu_nameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_menu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.layout_title_02);
        //设置标题
        TextView main_title = (TextView) findViewById(R.id.title_name2);
        main_title.setText("菜单列表");

        expandableListView = (ExpandableListView)findViewById(R.id.men_moreList);
        final MyExpandableListAdapter adapter =  new MyExpandableListAdapter();

        //更新 selectedList 值
        if (AppUtils.app_menuList != null && AppUtils.app_menuList.size() >0){
            for (Map<String,Object> map:AppUtils.app_menuList) {
                menu_nameList.add(map.get("name").toString());
            }
        }
        //展开所有组
        expandableListView.setAdapter(adapter);
        for(int i = 0; i < adapter.getGroupCount(); i++){
            expandableListView.expandGroup(i);
        }

        // 返回 与 确认 按钮
        ImageButton ib_back = (ImageButton) findViewById(R.id.title_back2);
        ImageButton ib_ok = (ImageButton) findViewById(R.id.title_ok2);

        ib_back.setOnClickListener(new MyImageBtton1());
        ib_ok.setOnClickListener(new MyImageBtton1());

    }

    public  class MyImageBtton1 implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            int id = v.getId();
            switch (id){
                case R.id.title_back2:
                    MenuActivity.this.finish();
                    break;
                case  R.id.title_ok2:
                    if(menu_nameList!= null && menu_nameList.size() >0){
                        AppUtils.dbUtils = DatabaseUtils.getInstance(MenuActivity.this,"wtmis.db");
                        AppUtils.dbUtils.execSql1("sys_menu",AppUtils.fwq_menuList,menu_nameList,"id,text,code,icon","menuid,name,code,ioc_name,username,sfxs,package,address");
                        Toast.makeText(MenuActivity.this,"操作完成",Toast.LENGTH_LONG).show();
                        setResult(9);
                        MenuActivity.this.finish();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter{

        //设置组视图的图片
        int[] logos = null;
        //设置组视图的显示文字
        private String[] category = null;
        //子视图显示文字
        private String[][] subcategory = null;
        //子视图图片
        public int[][] sublogos = null;

        private Map<String,Object>mCheckbox;

        public MyExpandableListAdapter(){
            setMenu();
        }

        @Override
        public int getGroupCount() {
            return category.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return subcategory[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return category[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return subcategory[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.menu_listview_group,null);
            TextView tv1 = (TextView)view.findViewById(R.id.menu_group_name);
            tv1.setText(category[groupPosition]);
            return  view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.menu_listview_item2,null);
            /*ImageView iv1 = (ImageView) view.findViewById(R.id.menu_item_logo);
            iv1.setImageResource(sublogos[groupPosition][childPosition]);*/

            final TextView tv1 = (TextView)view.findViewById(R.id.menu_item_name);
            tv1.setText(subcategory[groupPosition][childPosition]);

            final CheckBox rb1 = (CheckBox) view.findViewById(R.id.menu_item_select);
            rb1.setButtonDrawable(R.drawable.selector_checkbox1);

            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rb1.isChecked()){
                        selectedList.add(groupPosition+";"+childPosition);
                        menu_nameList.add(tv1.getText().toString());
                    }else{
                        selectedList.remove(groupPosition+";"+childPosition);
                        menu_nameList.remove(tv1.getText().toString());
                    }
                }
            });
            setExpandableListView(tv1.getText().toString(),view);
            return  view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private  void setMenu(){
            if (AppUtils.fwq_menuList != null){

                mCheckbox = new HashMap<String, Object>();
                int i = 0,j=0,menu_size= 0;
                JSONArray jay1 = null;
                List<Map<String,Object>> list = null;
                menu_size = AppUtils.fwq_menuList.size();
                logos = new int[menu_size];
                category = new String[menu_size];

                sublogos = new int[menu_size][];
                subcategory = new String[menu_size][];

                for (Map<String,Object> map1:AppUtils.fwq_menuList) {
                    j = 0;
                    if(map1.get("items")!= null){
                        if (map1.get("items") instanceof  String){
                            list =  FastJsonUtils.strToListMap(map1.get("items").toString());
                        }else{
                            list = (List<Map<String,Object>>)map1.get("items");
                        }
                        subcategory[i] = new String[list.size()];
                        sublogos[i] = new int[list.size()];

                        for (Map<String,Object> map2:list) {
                            //添加子视图文本
                            if (map2 != null){
                                subcategory[i][j] = map2.get("text").toString();
                                sublogos[i][j] = AppUtils.getPic(null);
                                j++;
                            }

                        }
                    }

                    //添加组图标
                    logos[i] = AppUtils.getPic(map1.get("menu_icon"));
                    //添加组文字
                    category[i] = map1.get("text").toString();
                    i++;
                }
            }
        }
    }

    private void setExpandableListView(int groupPosition,int childPosition,View view){

        CheckBox cb1 = (CheckBox) view.findViewById(R.id.menu_item_select);
        if (selectedList.contains(groupPosition+";"+childPosition)){
            cb1.setChecked(true);
        }else{
            cb1.setChecked(false);
        }
    }

    private void setExpandableListView(String name,View view){

        CheckBox cb1 = (CheckBox) view.findViewById(R.id.menu_item_select);
        if (menu_nameList.contains(name)){
            cb1.setChecked(true);
        }else{
            cb1.setChecked(false);
        }
    }
}
