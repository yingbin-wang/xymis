package com.cn.wti.activity.base.list;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/11.
 */

public class BaseList_01_updateActivity extends BaseList_02Activity{

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //刷新数据
        try {
            ReflectHelper.callMethod2(frag,"reshData",null,String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if (resultCode== 1){

            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type").toLowerCase();
            System_one one = (System_one) data.getSerializableExtra("parms");
            if (one == null){return;}
            Map<String,Object> dataMap = one.getParms();
            //获取 当前视图
            fragment_view = frag.getView();
            mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();

            //判断单据状态
            if (table_postion == 1){
                if (type.equals("add")){
                    mAdapter2.getDatas().add(0,dataMap);
                }else if (type.equals("edit") || type.equals("update")){
                    if (type.equals("edit")){
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index,dataMap);
                    }else{
                        mAdapter2.getDatas().remove(index);
                    }

                }else if (type.equals("delete")){
                    mAdapter2.getDatas().remove(index);
                }
            }else if (table_postion == 2){

            }

            mAdapter2.notifyDataSetChanged();

            if (get_catch().get(menu_code + "_maindata") != null) {

                if (table_postion == 1){
                    main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());

                    if (get_catch().get(menu_code + "_senconddata") != null) {
                        sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
                        sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());
                    }

                    if (type.equals("add")){
                        main_datalist.add(0,dataMap);
                        recordcount = recordcount + 1;
                    }else if (type.equals("edit") || type.equals("update")){
                        if (type.equals("edit")){
                            main_datalist.remove(index);
                            main_datalist.add(index,dataMap);
                        }else if (type.equals("update")){
                            main_datalist.remove(index);
                            recordcount--;
                            if (sencond_datalist != null){
                                sencond_datalist.add(index,dataMap);
                                sencond_recordcount++;
                            }else {
                                sencond_datalist = new ArrayList<Map<String,Object>>();
                                sencond_datalist.add(0,dataMap);
                                sencond_recordcount = 1;
                            }
                        }

                    }else if (type.equals("delete")){
                        main_datalist.remove(index);
                    }

                    //向 table 2 中添加数据
                    if (get_catch().get(menu_code + "_senconddata") != null){
                        get_catch().put(menu_code + "_senconddata", sencond_datalist);
                        get_catch().put(menu_code + "sencond_recordcount", sencond_recordcount);
                        get_catch().put(menu_code + "sencond_pageIndex", 1);
                    }
                }

                if (main_datalist != null){
                    get_catch().put(menu_code + "_maindata", main_datalist);
                    get_catch().put(menu_code + "main_recordcount", recordcount);
                    get_catch().put(menu_code + "main_pageIndex", 1);
                }
            }
        }

        if (resultCode == 2){
            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type").toLowerCase();
            System_one one = (System_one) data.getSerializableExtra("parms");
            if (one == null){return;}
            Map<String,Object> dataMap = one.getParms();
            //获取 当前视图
            fragment_view = frag.getView();
            mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();

            //判断单据状态
            if (table_postion == 2){
                if (type.equals("add")){
                    mAdapter2.getDatas().add(0,dataMap);
                }else if (type.equals("edit") || type.equals("update")){
                    if (type.equals("edit")){
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index,dataMap);
                    }else{
                        mAdapter2.getDatas().remove(index);
                    }

                }else if (type.equals("delete")){
                    mAdapter2.getDatas().remove(index);
                }
            }

            mAdapter2.notifyDataSetChanged();

            if (get_catch().get(menu_code + "_senconddata") != null) {

                if (table_postion == 2){
                    sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
                    sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());

                    if (get_catch().get(menu_code + "_thirddata") != null) {
                        third_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_thirddata");
                        third_recordcount = Integer.parseInt(get_catch().get(menu_code + "third_recordcount").toString());
                    }

                    if (type.equals("edit") || type.equals("update")){
                        if (type.equals("edit")){
                            sencond_datalist.remove(index);
                            sencond_datalist.add(index,dataMap);
                        }else if (type.equals("update")){
                            sencond_datalist.remove(index);
                            sencond_recordcount--;
                            if (third_datalist != null){
                                third_datalist.add(index,dataMap);
                                third_recordcount++;
                            }
                        }
                    }

                    //向 table 3 中添加数据
                    if (third_datalist != null){
                        get_catch().put(menu_code + "_thirddata", sencond_datalist);
                        get_catch().put(menu_code + "third_recordcount", sencond_recordcount);
                        get_catch().put(menu_code + "third_pageIndex", 1);
                    }
                }

                if (main_datalist != null){
                    get_catch().put(menu_code + "_senconddata", main_datalist);
                    get_catch().put(menu_code + "sencond_recordcount", recordcount);
                    get_catch().put(menu_code + "sencond_pageIndex", 1);
                }
            }
        }*/
    }

    public void clearOneTwo(){
        //one
        get_catch().remove(menu_code + "_maindata");
        get_catch().remove(menu_code + "main_recordcount");
        get_catch().remove(menu_code + "main_pageIndex");
        //two
        get_catch().remove(menu_code + "_senconddata");
        get_catch().remove(menu_code + "sencond_recordcount");
        get_catch().remove(menu_code + "sencond_pageIndex");
    }
}
