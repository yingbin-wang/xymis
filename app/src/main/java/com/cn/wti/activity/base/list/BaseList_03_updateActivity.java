package com.cn.wti.activity.base.list;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/11.
 */

public class BaseList_03_updateActivity extends BaseList_02Activity{

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 1) {

            clearOneTwoThree();

            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type").toLowerCase(),menucode="";

            try {
                menucode = data.getStringExtra("menucode");
            }catch (Exception e){}

            System_one one = (System_one) data.getSerializableExtra("parms");
            if (one == null) {
                return;
            }
            Map<String, Object> dataMap = one.getParms();
            //获取 当前视图
            fragment_view = frag.getView();
            mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();

            String id = dataMap.get("id").toString();

            //如果单据为 我的任务 将类型赋值为 delete 操作
            /*if (menucode != null && menucode.equals("Mytaska")){
                type = "delete";
            }*/

            //判断单据状态
            if (!type.equals("delete") && !type.equals("")){

                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)){
                    if (type.equals("edit") || type.equals("update") ){
                        if (table_postion == 3){
                            mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().add(index, dataMap);
                        }else if (table_postion == 1){
                            //mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().add(index, dataMap);
                        }else if (table_postion == 2){
                            //mAdapter2.getDatas().add(index, dataMap);
                            mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().add(index, dataMap);
                        }
                    }
                }else{

                    if (table_postion == 1 ||
                            (table_postion == 2 && (type.equals("update") || type.equals("edit")))){

                        mAdapter2.getDatas().add(0, dataMap);

                    }else if(table_postion == 1 && (type.equals("edit") || type.equals("update"))){
                        mAdapter2.getDatas().remove(index);
                    }
                }
            }else{
                if (!type.equals("")){
                    mAdapter2.getDatas().remove(index);
                }
            }

            mAdapter2.refreshData();
            //刷新数据
            try {
                ReflectHelper.callMethod2(frag,"reshData",null,String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*//修改缓存数据
            updateOne(type,index,dataMap,id);
            updateTwo(type,index,dataMap,id);
            updateThree(type,index,dataMap,id);*/

        }
    }

    public void clearOneTwoThree(){
        //one
        get_catch().remove(menu_code + "_maindata");
        get_catch().remove(menu_code + "main_recordcount");
        get_catch().remove(menu_code + "main_pageIndex");
        //two
        get_catch().remove(menu_code + "_senconddata");
        get_catch().remove(menu_code + "sencond_recordcount");
        get_catch().remove(menu_code + "sencond_pageIndex");
        //three
        get_catch().remove(menu_code + "_thirddata");
        get_catch().remove(menu_code + "third_recordcount");
        get_catch().remove(menu_code + "third_pageIndex");
    }
    //sencond third
    private void updateOne(String type,int index,Map<String,Object> dataMap,String id) {

        if (get_catch().get(menu_code + "_maindata") != null && (type.equals("edit") || type.equals("update"))) {

            main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
            recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());
            Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
            main_datalist.remove(Integer.parseInt(map.get("index").toString()));
            recordcount --;

            get_catch().put(menu_code + "_maindata", main_datalist);
            get_catch().put(menu_code + "main_recordcount", recordcount);
            get_catch().put(menu_code + "main_pageIndex", 1);
        }
    }


    private void updateTwo(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_senconddata") != null) {
            sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
            sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());

            if (FastJsonUtils.isExitVal(sencond_datalist, "id", id)){

                if(type.equals("edit") ||  type.equals("update")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(sencond_datalist, "id", id);
                    sencond_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    sencond_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                }

            }else{
                if (type.equals("edit") ||  type.equals("update")){
                    sencond_datalist.add(0,dataMap);
                    sencond_recordcount ++;
                }
            }

            get_catch().put(menu_code + "_senconddata", sencond_datalist);
            get_catch().put(menu_code + "sencond_recordcount", sencond_recordcount);
            get_catch().put(menu_code + "sencond_pageIndex", 1);

        }
    }

    private void updateThree(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_thirddata") != null) {
            third_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_thirddata");
            third_recordcount = Integer.parseInt(get_catch().get(menu_code + "third_recordcount").toString());

            if (FastJsonUtils.isExitVal(third_datalist, "id", id)){
                if (type.equals("delete")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(third_datalist, "id", id);
                    third_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    third_recordcount -- ;
                }else{
                    if(type.equals("edit") ||  type.equals("update")){
                        Map<String,Object> map = FastJsonUtils.getMapExitVal(third_datalist, "id", id);
                        third_datalist.remove(Integer.parseInt(map.get("index").toString()));
                        third_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                    }
                }
            }else{
                third_datalist.add(0,dataMap);
                third_recordcount ++;
            }

            get_catch().put(menu_code + "_thirddata", third_datalist);
            get_catch().put(menu_code + "third_recordcount", third_recordcount);
            get_catch().put(menu_code + "third_pageIndex", 1);

        }
    }
}