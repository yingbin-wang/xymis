package com.cn.wti.activity.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.rwgl.myassignment.MyAssignment_Activity;
import com.cn.wti.activity.rwgl.notebook.NoteBookActivity;
import com.cn.wti.activity.rwgl.saledaily.SalesdailyActivity;
import com.cn.wti.activity.rwgl.signin.SigninActivity;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.MyGridAdapter;
import com.cn.wti.util.app.MyGridView;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;

public class Fragment3 extends Fragment {
	private View rootView;
	private MyGridView gridView;
	private String[] img_text;
	private String[] img_rid;
	private LinearLayout ribao,kaoqin,jishiben,renwu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){

			rootView = inflater.inflate(R.layout.activity_main, null);
			TextView title = (TextView) rootView.findViewById(R.id.main_title_name);
			title.setText("OA办公");

			gridView = (MyGridView) rootView.findViewById(R.id.main_gridview);
			//
			MyGridAdapter mga = new MyGridAdapter(rootView.getContext());
			img_text = new String[]{"销售","采购", "客户","仓库", "财务","行政","项目"};
			img_rid = new String[]{"market", "purchase","client", "warehouse", "finance", "administration","project"};
			/*img_rid = new String[]{"purchase", "purchase","purchase", "purchase", "purchase", "purchase","purchase"};*/

			int[] imgs = AppUtils.getRids(img_rid);
			mga.setImg_text(img_text);
			mga.setImgs(imgs);
			gridView.setAdapter(mga);

			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Class testClass = null;
					Intent intent = new Intent();
					if(img_text[position].equals("消息")){
						Toast.makeText(getContext(),"敬请期待",Toast.LENGTH_SHORT).show();
						return;
					}else if(img_text[position].equals("日程")) {
						Toast.makeText(getContext(), "敬请期待", Toast.LENGTH_SHORT).show();
						return;
					}else if(img_text[position].equals("销售")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.XsglActivity");
					}else if(img_text[position].equals("采购")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.CgglActivity");
					}else if(img_text[position].equals("客户")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.KhglActivity");
					}else if(img_text[position].equals("仓库")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.CkglActivity");
					}else if(img_text[position].equals("财务")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.CwglActivity");
					}else if(img_text[position].equals("行政")){
						testClass = ReflectHelper.getCalss("com.cn.wti.activity.tab.XzglActivity");
					}else if(img_text[position].equals("项目")) {
						/*testClass = ReflectHelper.getCalss("com.cn.wti.activity.xsgl.xsrb.XsrbActivity");*/
					}

					if(testClass != null){
						intent.setClass(rootView.getContext(),testClass);
						startActivity(intent);
					}
				}
			});
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}
		/*//日报
		ribao = (LinearLayout) rootView.findViewById(R.id.ribao);
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

	private class MyClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()){
				/*case R.id.ribao:

					if (ActivityController.checkQx("SellDaily")){
						intent.setClass(getContext(), SalesdailyActivity.class);
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

	
}
