package com.cn.wti.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.entity.view.ChannelInfoBean;
import com.cn.wti.entity.view.GridViewGallery;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.app.MyGridAdapter;
import com.cn.wti.util.app.MyGridView;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.db.WebServiceHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity{

    private MyGridView gridView = null;
    private ViewPager viewPager = null;

    private List<Map<String,Object>> menu_list = null;
    private String[] img_text;
    private String[] img_rid;
    private List<ChannelInfoBean> list = null;
    private LinearLayout mLayout;
    private GridViewGallery mGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.layout_title_03);
        //设置标题
        TextView main_title = (TextView) findViewById(R.id.main_title_name);
        main_title.setText("首页");

        /*img_text = new String[]{"消息中心", "经营情况", "快速帮助", "更多"};
        img_rid = new String[]{"main_01", "main_02", "main_03", "main_04"};*/

        gridView = (MyGridView) findViewById(R.id.main_gridview);
        //
        MyGridAdapter mga = new MyGridAdapter(this);
        img_text = new String[]{"消息中心", "经营情况", "快速帮助", "更多"};
        img_rid = new String[]{"main_01", "main_02", "main_03", "main_04"};

         Intent intent =  this.getIntent();
        //从服务器得到菜单
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userId",AppUtils.app_username);
        AppUtils.fwq_menuList = WebServiceHelper.getResultListMap(AppUtils.app_address,"getmenuByUserId",map);
        //得到本地 app 菜单
        AppUtils.app_menuList = AppUtils.dbUtils.exec_select("sys_menu",new String[]{"menuid","name","action_name","ioc_name","sfxs"},"sfxs = ?",new String[]{"1"},null,null,null);

        if( AppUtils.app_menuList != null && AppUtils.app_menuList.size()>0){

            String[] img_text1 =  FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"name");
            String[] img_rid1 = FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"ioc_name");

            img_text = FastJsonUtils.insertStrArrayToArray(img_text,2,img_text1);
            img_rid = FastJsonUtils.insertStrArrayToArray(img_rid,2,img_rid1);
            AppUtils.app_action = FastJsonUtils.ListPdTOmap(AppUtils.app_menuList,"name","action_name");

            int[] imgs = AppUtils.getRids(img_rid);
            mga.setImg_text(img_text);
            mga.setImgs(imgs);
            gridView.setAdapter(mga);

        }else{
            int[] imgs = AppUtils.getRids(img_rid);
            mga.setImg_text(img_text);
            mga.setImgs(imgs);
            gridView.setAdapter(mga);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class testClass = null;
                Intent intent = new Intent();

                if (img_text[position].equals("更多")){
                    testClass = ReflectHelper.getCalss("com.cn.wti.activity.MenuActivity");
                }else{
                    testClass = ReflectHelper.getCalss("com.cn.wti.activity.myTask.MyTaskActivity");
                }
                intent.setClass(MainActivity.this,testClass);
                startActivity(intent);
            }
        });
    }

}
