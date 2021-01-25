package com.cn.wti.activity.base.list;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.cn.wti.activity.base.BaseList_02Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/6/11.
 */

public class BaseList_save_updateActivity extends BaseList_02Activity {

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
            if (!type.equals("delete")) {

                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                    if (type.equals("edit")) {
                        mAdapter2.getDatas().remove(index);
                        mAdapter2.getDatas().add(index, dataMap);
                    }
                } else {

                    if (table_postion == 1 && (type.equals("add") || type.equals("edit"))) {
                        mAdapter2.getDatas().add(0, dataMap);
                    }
                }
            } else {
                if (FastJsonUtils.isExitVal(mAdapter2.getDatas(), "id", id)) {
                    mAdapter2.getDatas().remove(index);
                }
            }

            mAdapter2.refreshData(mAdapter2.getSelectItem());

            //修改缓存数据
            updateOne(type, index, dataMap, id);

        }
    }

    private void updateOne(String type, int index, Map<String, Object> dataMap, String id) {
        if (get_catch().get(menu_code + "_maindata") != null) {
            main_datalist = (List<Map<String, Object>>) get_catch().get(menu_code + "_maindata");
            recordcount = Integer.parseInt(get_catch().get(menu_code + "main_recordcount").toString());

            if (FastJsonUtils.isExitVal(main_datalist, "id", id)) {

                if (type.equals("delete")) {
                    Map<String, Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    recordcount--;
                } else {
                    Map<String, Object> map = FastJsonUtils.getMapExitVal(main_datalist, "id", id);
                    main_datalist.remove(Integer.parseInt(map.get("index").toString()));
                    main_datalist.add(Integer.parseInt(map.get("index").toString()), dataMap);
                }

            } else {
                if (type.equals("add") || type.equals("edit")) {
                    main_datalist.add(0, dataMap);
                    recordcount++;
                }
            }

            get_catch().put(menu_code + "_maindata", main_datalist);
            get_catch().put(menu_code + "main_recordcount", recordcount);
            get_catch().put(menu_code + "main_pageIndex", 1);

        }
    }
}