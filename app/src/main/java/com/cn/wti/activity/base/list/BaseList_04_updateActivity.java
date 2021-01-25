package com.cn.wti.activity.base.list;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/11.
 */

public class BaseList_04_updateActivity extends BaseList_02Activity{

    private String menucode = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== 1){

            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type").toLowerCase();
            System_one one = (System_one) data.getSerializableExtra("parms");

            if (data.getStringExtra("menucode")!= null){
                menucode = data.getStringExtra("menucode").toString();
            }else{
                menucode = menu_code;
            }

            if (one == null){return;}
            Map<String,Object> dataMap = one.getParms();
            //获取 当前视图
            fragment_view = frag.getView();
            mRecyclerView1 = (RecyclerView) fragment_view.findViewById(R.id.list_recyclerView);
            mAdapter2 = (MyAdapter2) mRecyclerView1.getAdapter();

            //判断单据状态
            String id = dataMap.get("id").toString();
            if (!type.equals("delete")){

                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)){

                    if (type.equals("edit") || type.equals("update") ){
                        if (table_postion == 1){
                            if (type.equals("edit")){
                                mAdapter2.getDatas().remove(index);
                                mAdapter2.getDatas().add(index, dataMap);
                            }else{
                                if (type.equals("update") && dataMap.get("estatus")!= null && dataMap.get("estatus").equals("1")){
                                    mAdapter2.getDatas().remove(index);
                                    mAdapter2.getDatas().add(index, dataMap);
                                }else{
                                    mAdapter2.getDatas().remove(index);
                                }
                            }

                        }else if (table_postion == 2){
                            mAdapter2.getDatas().remove(index);
                        }
                    }
                }else{

                    if (type.equals("add") && table_postion == 1){
                        mAdapter2.getDatas().add(0,dataMap);
                    }else if(table_postion == 2 && (type.equals("edit") || type.equals("update"))){
                        mAdapter2.getDatas().remove(index);
                    }
                }
            }else{
                mAdapter2.getDatas().remove(index);
            }

            mAdapter2.refreshData(mAdapter2.getSelectItem());

            //修改缓存数据
            updateOne(type,index,dataMap,id);
            updateTwo(type,index,dataMap,id);

            //刷新数据
            try {
                ReflectHelper.callMethod2(frag,"reshData",null,String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       /* if (resultCode == 2){
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
                        get_catch().put(menucode + "_thirddata", sencond_datalist);
                        get_catch().put(menucode + "third_recordcount", sencond_recordcount);
                        get_catch().put(menucode + "third_pageIndex", 1);
                    }
                }

                if (main_datalist != null){
                    get_catch().put(menucode + "_senconddata", main_datalist);
                    get_catch().put(menucode + "sencond_recordcount", recordcount);
                    get_catch().put(menucode + "sencond_pageIndex", 1);
                }
            }
        }*/
    }

    private void updateOne(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menucode + "_maindata") != null) {
            main_datalist = (List<Map<String, Object>>) get_catch().get(menucode + "_maindata");
            recordcount = Integer.parseInt(get_catch().get(menucode + "main_recordcount").toString());

            if (FastJsonUtils.isExitVal(main_datalist, "id", id)){
                if (type.equals("delete")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    recordcount -- ;
                }else{
                    if(type.equals("edit") ||  type.equals("update")){
                        if (type.equals("edit")){
                            Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                            main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                            main_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                        }else{
                            Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                            main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                        }
                    }
                }
            }else{
                main_datalist.add(0,dataMap);
                recordcount ++;
            }

            get_catch().put(menucode + "_maindata", main_datalist);
            get_catch().put(menucode + "main_recordcount", recordcount);
            get_catch().put(menucode + "main_pageIndex", 1);

        }
    }

    private void updateTwo(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menucode + "_senconddata") != null) {
            sencond_datalist = (List<Map<String, Object>>) get_catch().get(menucode + "_senconddata");
            sencond_recordcount = Integer.parseInt(get_catch().get(menucode + "sencond_recordcount").toString());

            if (FastJsonUtils.isExitVal(sencond_datalist, "id", id)){

                if(type.equals("edit") ||  type.equals("update")){
                    if (type.equals("edit")){
                        Map<String,Object> map = FastJsonUtils.getMapExitVal(sencond_datalist, "id", id);
                        sencond_datalist.remove(Integer.parseInt(map.get("index").toString()));
                        sencond_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                    }else{
                        Map<String,Object> map = FastJsonUtils.getMapExitVal(sencond_datalist, "id", id);
                        sencond_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    }

                }

            }else{
                if (type.equals("edit") ||  type.equals("update")){
                    sencond_datalist.add(0,dataMap);
                    sencond_recordcount ++;
                }
            }

            get_catch().put(menucode + "_senconddata", sencond_datalist);
            get_catch().put(menucode + "sencond_recordcount", sencond_recordcount);
            get_catch().put(menucode + "sencond_pageIndex", 1);

        }
    }


    public void clearOneTwo(){
        //one
        get_catch().remove(menucode + "_maindata");
        get_catch().remove(menucode + "main_recordcount");
        get_catch().remove(menucode + "main_pageIndex");
        //two
        get_catch().remove(menucode + "_senconddata");
        get_catch().remove(menucode + "sencond_recordcount");
        get_catch().remove(menucode + "sencond_pageIndex");
    }

}
