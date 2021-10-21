package com.cn.wti.activity.tab;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.activity.rwgl.myassignment.MyAssignment_Activity;
import com.cn.wti.activity.rwgl.notebook.NoteBookActivity;
import com.cn.wti.activity.rwgl.saledaily.SalesdailyActivity;
import com.cn.wti.activity.rwgl.signin.SigninActivity;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.MyGridAdapter;
import com.cn.wti.util.app.MyGridView;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.wticn.wyb.wtiapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Fragment_Bangong extends Fragment {
	private View rootView;
	private MyGridView gridView;
	private String[] img_text;
	private String[] img_rid;
	private String[] img_code;
	private LinearLayout ribao,kaoqin,jishiben,renwu;
	private  MyGridAdapter mga = null;
	private List<String> classList = null;
	private PageDataSingleton _catch = PageDataSingleton.getInstance();
	private Dialog mDialog = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){

			rootView = inflater.inflate(R.layout.activity_main, null);
			TextView title = (TextView) rootView.findViewById(R.id.main_title_name);
			title.setText("OA办公");

			gridView = (MyGridView) rootView.findViewById(R.id.main_gridview);

			/*if (_catch.get("classList")!= null){
				classList = (List<String>) _catch.get("classList");
			}else{
				AssetManager manager = getActivity().getAssets();
				InputStream inputStream = null;
				try {
					inputStream = manager.open("AndroidManifest.xml");
					classList = xmlUtils.read(inputStream);
					if (classList != null){
						_catch.put("classList",classList);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}*/

			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Class testClass = null;
					Intent intent = new Intent();

					String code = "",name = "";

					if (img_text[position].equals("更多")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.MenuActivity");
						if (testClass != null) {
							intent.setClass(rootView.getContext(), testClass);
							startActivityForResult(intent,1);
						}
					}else if (img_text[position].equals("销售日报")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.saledaily.MyFileActivity");
						/*if (testClass != null) {
							intent.setClass(rootView.getContext(), testClass);
							startActivity(intent);
						}*/
					}else if (img_text[position].equals("我的客户")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.myclient.MyClientActivity");
						code = "mycustomer";
						name = "我的客户";
						/*if (testClass != null) {
							intent.setClass(rootView.getContext(), testClass);
							startActivity(intent);
						}*/
					}
					else {
						code = img_code[position];
						name = img_text[position];
						//String name = code + "Activity", className = "";

						String className = "com.cn.wti.activity.common.CommonListActivity";
						testClass = ReflectHelper.getCalss(className);

					}

					if (!img_text[position].equals("更多")){
						//得到菜单权限
						Map<String,Object> qxMap = findMapToListByKey(AppUtils.fwq_menuList,"name",name);
						if (qxMap != null){
							intent.putExtra("qxMap",FastJsonUtils.mapToString(qxMap));
						}else{
							intent.putExtra("qxMap","{}");
						}

						intent.putExtra("menucode",code);
						intent.putExtra("menuname",name);

						if (testClass != null) {
							intent.setClass(rootView.getContext(), testClass);
							startActivity(intent);
						}
					}
				}
			});
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}

		mDialog = WeiboDialogUtils.createLoadingDialog(getActivity(),"加载中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (initData()){
					if (getActivity() != null){
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (gridView != null ){
									gridView.setAdapter(mga);
								}

								if (mDialog != null){
									WeiboDialogUtils.closeDialog(mDialog);
								}
							}
						});
					}
				}
			}
		}).start();
		//日报
		/*ribao = (LinearLayout) rootView.findViewById(R.id.ribao);
		ribao.setOnClickListener(new MyClick());*/
		//记事本
		jishiben = (LinearLayout) rootView.findViewById(R.id.jishiben);
		jishiben.setOnClickListener(new MyClick());
		//任务
		renwu = (LinearLayout) rootView.findViewById(R.id.renwu);
		renwu.setOnClickListener(new MyClick());
		//考勤
		kaoqin = (LinearLayout) rootView.findViewById(R.id.kaoqin);
		kaoqin.setOnClickListener(new MyClick());

		return rootView;
	}

	public boolean initData(){
		final Object res = ActivityController.getDataObjectByPost(getActivity(),"mobilerole","getMenuQxByUser", StringUtils.strTOJsonstr("username:"+AppUtils.app_username));
		if (res != null && !res.toString().contains("(abcdef)")){
			if (res instanceof JSONArray){
				AppUtils.fwq_menuList = (List<Map<String, Object>>) res;
			}else if (res instanceof String){
				final  String finalRes = res.toString();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(),finalRes,Toast.LENGTH_SHORT).show();
						WeiboDialogUtils.closeDialog(mDialog);
					}
				});
				return  false;
			}
		}else{
			final String finalres = res.toString();
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(),finalres.replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
					WeiboDialogUtils.closeDialog(mDialog);
				}
			});
			return  false;
		}

		img_text = new String[]{"更多"};
		img_rid = new String[]{"gengduo"};
		//得到本地 app 菜单
		AppUtils.app_menuList = AppUtils.dbUtils.exec_select("sys_menu",new String[]{"address","menuid","code","name","action_name","ioc_name","sfxs"},"address = ? and sfxs = ? and username = ?",new String[]{AppUtils.app_address,"1",AppUtils.app_username},null,null,null);
		mga = new MyGridAdapter(rootView.getContext());
		if( AppUtils.app_menuList != null && AppUtils.app_menuList.size()>0){

			String[] img_text1 =  FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"name");
			String[] img_rid1 = FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"ioc_name");
			img_code = FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"code");

			img_text = FastJsonUtils.insertStrArrayToArray(img_text,0,img_text1);
			img_rid = FastJsonUtils.insertStrArrayToArray(img_rid,0,img_rid1);
			AppUtils.app_action = FastJsonUtils.ListPdTOmap(AppUtils.app_menuList,"name","action_name");

			int[] imgs = AppUtils.getRids(img_rid);
			mga.setImg_text(img_text);
			mga.setImgs(imgs);
			//gridView.setAdapter(mga);

		}else{
			int[] imgs = AppUtils.getRids(img_rid);
			mga.setImg_text(img_text);
			mga.setImgs(imgs);
			//gridView.setAdapter(mga);
		}

		/*img_text = new String[]{"销售","采购", "客户","仓库", "财务","行政","项目"};
		img_rid = new String[]{"market", "purchase","client", "warehouse", "finance", "administration","project"};

		int[] imgs = AppUtils.getRids(img_rid);
		mga.setImg_text(img_text);
		mga.setImgs(imgs);
		gridView.setAdapter(mga);*/
		return  true;
	}

	private class MyClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()){
				/*case R.id.ribao:

					if (ActivityController.checkQx("SellDaily")){
						intent.setClass(getContext(), MyFileActivity.class);
						startActivity(intent);
					}else{
						Toast.makeText(getActivity(),getString(R.string.error_invalid_user),Toast.LENGTH_SHORT).show();
					}

					break;*/
				case R.id.jishiben:
					intent.setClass(getContext(), NoteBookActivity.class);
					startActivity(intent);
					break;
				case R.id.renwu:
					intent.setClass(getContext(),MyAssignment_Activity.class);
					startActivity(intent);
					break;
				case R.id.kaoqin:
					intent.setClass(getContext(),SigninActivity.class);
					startActivity(intent);
				default:
					break;
			}
		}
	}


	private  Map<String,Object> findMapToListByKey(List<Map<String,Object>> list,String key,String val){

		List<Map<String,Object>> itemslist = null;
		if(list != null && list.size() >0){
			Map<String,Object> map = null;
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				if (map.get("items") instanceof  String){
					itemslist = FastJsonUtils.strToListMap(map.get("items") .toString());
				}else{
					itemslist = (List<Map<String, Object>>) map.get("items");
				}

				for (Map itemMap:itemslist) {
					if(itemMap != null && itemMap.get(key)!= null && itemMap.get(key).toString().equals(val)){
						return  itemMap;
					}
				}
			}
		}
		return  null;
	}

	public void reshData(){
		img_text = new String[]{"更多"};
		img_rid = new String[]{"gengduo"};
		//得到本地 app 菜单
		AppUtils.app_menuList = AppUtils.dbUtils.exec_select("sys_menu",new String[]{"address","menuid","code","name","action_name","ioc_name","sfxs"},"address = ? and sfxs = ? and username = ?",new String[]{AppUtils.app_address,"1",AppUtils.app_username},null,null,null);
		if( AppUtils.app_menuList != null && AppUtils.app_menuList.size()>0){

			String[] img_text1 =  FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"name");
			String[] img_rid1 = FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"ioc_name");
			img_code = FastJsonUtils.ListMapToListStr(AppUtils.app_menuList,"code");

			img_text = FastJsonUtils.insertStrArrayToArray(img_text,0,img_text1);
			img_rid = FastJsonUtils.insertStrArrayToArray(img_rid,0,img_rid1);
			AppUtils.app_action = FastJsonUtils.ListPdTOmap(AppUtils.app_menuList,"name","action_name");

			int[] imgs = AppUtils.getRids(img_rid);
			mga.setImg_text(img_text);
			mga.setImgs(imgs);

		}else{
			int[] imgs = AppUtils.getRids(img_rid);
			mga.setImg_text(img_text);
			mga.setImgs(imgs);
		}

		mga.notifyDataSetChanged();
	}
	
}
