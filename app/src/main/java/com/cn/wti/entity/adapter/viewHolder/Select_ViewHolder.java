package com.cn.wti.entity.adapter.viewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/10/28.
 */
public  class Select_ViewHolder {

    public static TextView_custom title;
    public static EditText_custom content;
    public static ImageButton btnxz;
    private Context context;
    private View view = null;
    TextView_custom tv1 ;
    EditText_custom ed2 ;
    ImageButton ib1;
    private Map<String,Object> currentmap,selectMap;
    private List<Map<String,Object>> list;
    private List<View> views;


    public Select_ViewHolder(View view,Context context){
        this.view = view;
        this.context = context;
    }

    public void reshData() {
        tv1 = (TextView_custom) view.findViewById(R.id.item03_title);
        ed2 = (EditText_custom) view.findViewById(R.id.item03_content);
        ib1 = (ImageButton) view.findViewById(R.id.item03_select);
        tv1.setText(title.getText());
        ed2.setText(content.getText());

        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentmap.get("gldn") != null) {
                    String[] items = null;

                    final String gldn = currentmap.get("gldn").toString();
                    if (gldn.equals("")) {
                        return;
                    } else if (gldn.contains(",")) {
                        items = gldn.split(",");
                    } else {
                        String[] gldns = gldn.split("~");

                        if (selectMap.get(gldns[0]) != null) {
                            items = FastJsonUtils.ListMapToListStr((List<Map<String, Object>>) selectMap.get(gldns[0]), "name");
                        } else {
                            Map<String, Object> testMap = new HashMap<String, Object>();
                            testMap.put("serviceName", gldns[0].toLowerCase());
                            testMap.put("methodName", "list");
                            testMap.put("parameters", "{pageIndex:0,start:0,limit:10}");
                            Object res = WebServiceHelper.getResult(AppUtils.app_address, "execute", testMap);
                            if (res != null) {
                                Map<String, Object> resMap = FastJsonUtils.strToMap(res.toString());
                                if (resMap.get("state").toString().equals("success")) {
                                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) (((Map<String, Object>) resMap.get("data")).get("rows"));
                                    selectMap.put(gldns[0], dataList);
                                    items = FastJsonUtils.ListMapToListStr((List<Map<String, Object>>) selectMap.get(gldns[0]), "name");
                                }
                            }
                        }
                    }

                    final String[] finalItems = items;
                    new AlertDialog.Builder(context).setTitle("test").setIcon(
                            android.R.drawable.ic_dialog_info).setSingleChoiceItems(items, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (gldn.contains(",")) {
                                        content.setText(finalItems[which]);
                                    } else {
                                        List<String> testList = FastJsonUtils.ListPdTOListStr(list, "gldn", gldn, "code");
                                        for (String aa : testList) {
                                            View v = AppUtils.findViewByCode(views, aa);
                                            if (v instanceof TextView_custom) {
                                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) selectMap.get(gldn.split("~")[0]);
                                                TextView_custom tvs = (TextView_custom) v;
                                                tvs.setText(dataList.get(which).get("id").toString());
                                            } else if (v instanceof EditText_custom) {
                                                content.setText(finalItems[which]);
                                            }
                                        }
                                    }
                                    reshData();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            }
        });
    }

    public void setCurrentmap(Map<String, Object> currentmap) {
        this.currentmap = currentmap;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public void setSelectMap(Map<String, Object> selectMap) {
        this.selectMap = selectMap;
    }
}
