package com.cn.wti.activity.base.list;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/11.
 */

public class BaseList_02_updateActivity extends BaseList_02Activity{

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 1) {

            int index = Integer.parseInt(data.getStringExtra("index"));
            String type = data.getStringExtra("type").toLowerCase();
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
            //tab 位置 1 "未审核",2"审批中",3"已审核",4"全部"

            //判断单据状态
            if (!type.equals("delete")){

                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)){
                    if (type.equals("edit")){
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index, dataMap);
                    }else if (type.equals("check") && (table_postion == 1 || table_postion == 4)){
                        if (table_postion == 4){
                            mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().add(index, dataMap);
                        }else{
                            mAdapter2.getDatas().remove(index);
                        }
                    }else if (type.equals("uncheck") && (table_postion == 4 || table_postion == 3)){
                        if (table_postion == 4){
                            mAdapter2.getDatas().remove(index);
                            mAdapter2.getDatas().add(index, dataMap);
                        }else{
                            mAdapter2.getDatas().remove(index);
                        }
                    }else if (type.equals("shenpi") && (table_postion == 1)){
                        if (table_postion == 1){
                            mAdapter2.getDatas().remove(index);
                        }
                    }
                }else{

                    if (table_postion == 4 ||
                            (table_postion == 3 && type.equals("check")) ||
                            (table_postion == 2 && type.equals("shenpi")) ||
                            (table_postion == 1 && (type.equals("add") || type.equals("edit") || type.equals("uncheck")))){

                        mAdapter2.getDatas().add(0, dataMap);

                    }
                }
            }else{
                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)){
                    mAdapter2.getDatas().remove(index);
                }
            }

            mAdapter2.refreshData(mAdapter2.getSelectItem());

            //修改缓存数据
            updateOne(type,index,dataMap,id);
            updateTwo(type,index,dataMap,id);
            updateThree(type,index,dataMap,id);
            updateFour(type,index,dataMap,id);

        }
    }

    private void updateOne(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_maindata") != null) {
            main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
            recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());

            if (FastJsonUtils.isExitVal(main_datalist, "id", id)){

                if(type.equals("delete") || type.equals("check") || type.equals("shenpi")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    recordcount -- ;
                }else{
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    main_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                }

            }else{
                if (type.equals("uncheck") || type.equals("add") || type.equals("edit")){
                    main_datalist.add(0,dataMap);
                    recordcount ++;
                }
            }

            get_catch().put(menu_code + "_maindata", main_datalist);
            get_catch().put(menu_code + "main_recordcount", recordcount);
            get_catch().put(menu_code + "main_pageIndex", 1);

        }
    }

    private void updateTwo(String type,int index,Map<String,Object> dataMap,String id) {

        if (get_catch().get(menu_code + "_senconddata") != null && type.equals("shenpi")) {

            if (type.equals("shenpi")){

                sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
                sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());

                sencond_datalist.add(0,dataMap);
                sencond_recordcount ++;

                get_catch().put(menu_code + "_senconddata", sencond_datalist);
                get_catch().put(menu_code + "sencond_recordcount", sencond_recordcount);
                get_catch().put(menu_code + "sencond_pageIndex", 1);
            } else if(type.equals("delete")){
                Map<String,Object> map = FastJsonUtils.getMapExitVal(sencond_datalist, "id", id);
                sencond_datalist.remove(Integer.parseInt(map.get("index").toString()));
                sencond_recordcount -- ;
            }
        }
    }


    private void updateThree(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_thirddata") != null) {
            third_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_thirddata");
            third_recordcount = Integer.parseInt(get_catch().get(menu_code + "third_recordcount").toString());

            if (FastJsonUtils.isExitVal(third_datalist, "id", id)){

                if(type.equals("delete") || type.equals("edit") ||  type.equals("uncheck") || type.equals("shenpi")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(third_datalist, "id", id);
                    third_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    third_recordcount -- ;
                }

            }else{
                if (type.equals("check")){
                    third_datalist.add(0,dataMap);
                    third_recordcount ++;
                }
            }

            get_catch().put(menu_code + "_thirddata", third_datalist);
            get_catch().put(menu_code + "third_recordcount", third_recordcount);
            get_catch().put(menu_code + "third_pageIndex", 1);

        }
    }

    private void updateFour(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_fourthdata") != null) {
            fourth_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_fourthdata");
            fourth_recordcount = Integer.parseInt(get_catch().get(menu_code + "fourth_recordcount").toString());

            if (FastJsonUtils.isExitVal(fourth_datalist, "id", id)){
                if (type.equals("delete")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(fourth_datalist, "id", id);
                    fourth_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    fourth_recordcount -- ;
                }else{
                    if(type.equals("edit") ||  type.equals("check") ||  type.equals("uncheck")){
                        Map<String,Object> map = FastJsonUtils.getMapExitVal(fourth_datalist, "id", id);
                        fourth_datalist.remove(Integer.parseInt(map.get("index").toString()));
                        fourth_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                    }
                }
            }else{
                if (!type.equals("delete")){
                    fourth_datalist.add(0,dataMap);
                    fourth_recordcount ++;
                }
            }

            get_catch().put(menu_code + "_fourthdata", fourth_datalist);
            get_catch().put(menu_code + "fourth_recordcount", fourth_recordcount);
            get_catch().put(menu_code + "fourth_pageIndex", 1);

        }
    }

    /*private void updateOne(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_maindata") != null) {
            main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
            recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());

            if (FastJsonUtils.isExitVal(main_datalist, "id", id)){
                if (type.equals("delete")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    recordcount -- ;
                }else{
                    if(type.equals("edit") ||  type.equals("check") ||  type.equals("uncheck")){
                        Map<String,Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                        main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                        main_datalist.add(Integer.parseInt(map.get("index").toString()),dataMap);
                    }
                }
            }else{
                main_datalist.add(0,dataMap);
                recordcount ++;
            }

            get_catch().put(menu_code + "_maindata", main_datalist);
            get_catch().put(menu_code + "main_recordcount", recordcount);
            get_catch().put(menu_code + "main_pageIndex", 1);

        }
    }*/

    /*private void updateFour(String type,int index,Map<String,Object> dataMap,String id) {
        if (get_catch().get(menu_code + "_fourthdata") != null) {
            fourth_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_fourthdata");
            fourth_recordcount = Integer.parseInt(get_catch().get(menu_code + "fourth_recordcount").toString());

            if (FastJsonUtils.isExitVal(fourth_datalist, "id", id)){

                if(type.equals("delete") || type.equals("check") || type.equals("shenpi")){
                    Map<String,Object> map = FastJsonUtils.getMapExitVal(fourth_datalist, "id", id);
                    fourth_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    fourth_recordcount -- ;
                }

            }else{
                if (type.equals("uncheck") || type.equals("add") || type.equals("edit")){
                    fourth_datalist.add(0,dataMap);
                    fourth_recordcount ++;
                }
            }

            get_catch().put(menu_code + "_fourthdata", fourth_datalist);
            get_catch().put(menu_code + "fourth_recordcount", fourth_recordcount);
            get_catch().put(menu_code + "fourth_pageIndex", 1);

        }
    }*/
}


  /* if (type.equals("add")) {
                if (table_postion == 1 || table_postion == 4){
                    mAdapter2.getDatas().add(0, dataMap);
                }
            } else if (type.equals("edit") || type.equals("update") || type.equals("check") || type.equals("uncheck")) {
                if (type.equals("edit")) {
                    mAdapter2.getDatas().remove(index);
                    mAdapter2.getDatas().add(index, dataMap);
                }else if (type.equals("check") && ( table_postion == 1 || table_postion == 4)){
                    if (table_postion == 1){
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index, dataMap);
                    }else if (table_postion == 4){
                        mAdapter2.getDatas().remove(index);
                    }
                }else if (type.equals("uncheck")){
                    if (table_postion == 1){
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index, dataMap);
                    }else if (table_postion == 4){
                        mAdapter2.getDatas().remove(index);
                    }
                }

            } else if (type.equals("delete")) {
                mAdapter2.getDatas().remove(index);
            }

            mAdapter2.notifyDataSetChanged();
        }*/



            /*if (get_catch().get(menu_code + "_maindata") != null) {

                if (table_postion == 1) {
                    main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
                    recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());

                    if (get_catch().get(menu_code + "_senconddata") != null) {
                        sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
                        sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());
                    }

                    if (type.equals("add")) {
                        main_datalist.add(0, dataMap);
                        recordcount = recordcount + 1;
                    } else if (type.equals("edit") || type.equals("update")) {
                        if (type.equals("edit")) {
                            main_datalist.remove(index);
                            main_datalist.add(index, dataMap);
                        } else if (type.equals("update")) {
                            main_datalist.remove(index);
                            recordcount--;
                            if (sencond_datalist != null) {
                                sencond_datalist.add(index, dataMap);
                                sencond_recordcount++;
                            } else {
                                sencond_datalist = new ArrayList<Map<String, Object>>();
                                sencond_datalist.add(0, dataMap);
                                sencond_recordcount = 1;
                            }
                        }

                    } else if (type.equals("delete")) {
                        main_datalist.remove(index);
                    }

                    //向 table 2 中添加数据
                    if (get_catch().get(menu_code + "_senconddata") != null) {
                        get_catch().put(menu_code + "_senconddata", sencond_datalist);
                        get_catch().put(menu_code + "sencond_recordcount", sencond_recordcount);
                        get_catch().put(menu_code + "sencond_pageIndex", 1);
                    }
                }

                if (main_datalist != null) {
                    get_catch().put(menu_code + "_maindata", main_datalist);
                    get_catch().put(menu_code + "main_recordcount", recordcount);
                    get_catch().put(menu_code + "main_pageIndex", 1);
                }
            }*/


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
        }
    }*/

        /*        get_catch().remove(menu_code + "_thirddata");
                    get_catch().remove(menu_code + "third_recordcount");
                    get_catch().remove(menu_code + "third_pageIndex");

                    get_catch().remove(menu_code + "_fourthdata");
                    get_catch().remove(menu_code + "fourth_recordcount");
                    get_catch().remove(menu_code + "fourth_pageIndex");
                    if (get_catch().get(menu_code + "_senconddata") != null) {
                        sencond_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_senconddata");
                        sencond_recordcount = Integer.parseInt(get_catch().get(menu_code + "sencond_recordcount").toString());
                    }*/
