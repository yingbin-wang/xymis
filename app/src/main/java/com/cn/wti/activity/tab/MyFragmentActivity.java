package com.cn.wti.activity.tab;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ObbInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.activity.LoginActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.badger.BadgeView;
import com.cn.wti.entity.view.custom.dialog.AlertDialog;
import com.cn.wti.entity.view.custom.menu.SlidingMenu;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.version.UpdateService;
import com.cn.wti.util.db.DatabaseUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.db.SharePreferencesUtils;
import com.cn.wti.util.number.FileUtils;
import com.cn.wti.util.number.ZipUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.page.FormDataSingleton;
import com.cn.wti.util.page.PageDataSingleton;
import com.ease.ui.EaseChartActivity;
import com.ease.ui.EaseConversationListFragment;
import com.ease.utils.HuanxinUtils;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.easeui.Constant;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.wticn.wyb.wtiapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import dalvik.system.DexFile;

public class MyFragmentActivity extends FragmentActivity {
	private FragmentManager manager;
	private FragmentTransaction tran;
	private RadioGroup radioGroup;
	private StateListDrawable drawable;
	private RadioButton table;
	private SlidingMenu mMenu;
	private ListView lv1;
	private List<Map<String,Object>> lv_listMap;
	private ImageView touxiang;
	private static PageDataSingleton _catch = PageDataSingleton.getInstance();
	private static FormDataSingleton _data = FormDataSingleton.getInstance();
	private Context mContext;
	private Fragment bangong_Fragment;
	private boolean state = false,updateUserState = false;
	private BadgeView badge;
	LinearLayout mainView;
	private TextView number_tv,version_,userAgreement;
	private  int table_index = 0;
	private EaseConversationListFragment conversationListFragment;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	public static PageDataSingleton get_catch() {
		return _catch;
	}

	public static void set_catch(PageDataSingleton _catch) {
		MyFragmentActivity._catch = _catch;
	}

	private Dialog mDialog;

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK)
			return true;//不执行父类点击事件
		return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
	}

	@Override
	protected void onDestroy() {
		if (_catch != null){
			_catch.clear();
		}

		if (_data != null){
			_data.clear();
		}

		super.onDestroy();
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_fragment);
		AppUtils.setStatusBarColor(this);

		mContext = this;

		manager = getSupportFragmentManager();
		tran = manager.beginTransaction();
		//主页
		updateRadioButtonDrawable(R.id.tab1,R.mipmap.home_selected,R.mipmap.home);
		//消息
		updateRadioButtonDrawable(R.id.tab2,R.mipmap.messagehome_selected,R.mipmap.messagehome);
		//OA
		updateRadioButtonDrawable(R.id.tab3,R.mipmap.oa_selected,R.mipmap.oa);
		//报表
		updateRadioButtonDrawable(R.id.tab4,R.mipmap.report_selected,R.mipmap.report);

		mMenu = (SlidingMenu) findViewById(R.id.id_menu);

		Fragment1 f1 = new Fragment1();
		_catch.put("menu",mMenu);
		tran.replace(R.id.content,f1);
		tran.commit();
		
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				tran = manager.beginTransaction();

				switch(checkedId){
				case R.id.tab1:
					table_index = 1;
					Fragment1 f1 = new Fragment1();
					_catch.put("menu",mMenu);
					tran.replace(R.id.content,f1);
					tran.commit();
					break;
				case R.id.tab2:
						//消息界面获取用户数据
					    mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "数据准备中...");
					    updateUser();
						table_index = 2;

					break;
				case R.id.tab3:
					bangong_Fragment = new Fragment_Bangong();
					tran.replace(R.id.content, bangong_Fragment);
					tran.commit();
					table_index = 3;
					break;
				case R.id.tab4:
					tran.replace(R.id.content, new ReportFragment());
					tran.commit();
					table_index = 4;
					break;
				}

				mMenu.setIsOpen(true);
				mMenu.closeMenu();
			}
		});

		TextView yhm = (TextView) findViewById(R.id.yhm);
		yhm.setTextColor(getResources().getColor(R.color.colorWhite));
		yhm.setTextSize(18);
		yhm.setText(AppUtils.user.get_zydnName());
		//版本号
		version_ = (TextView) findViewById(R.id.version_);
		version_.setText("版本 "+AppUtils.getVerName(getApplicationContext()));

		userAgreement = findViewById(R.id.userAgreement);
		initData();
		//布局 ListView
		lv1 = (ListView) findViewById(R.id.page01_lv);
		lv1.setAdapter(new MyLisViewAdapter());
		lv1.setOnItemClickListener(new MyOnClickListener());

		userAgreement.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog alertDialog = new AlertDialog(getResources().getString(R.string.user_info_hint),
						"<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title><style></style></head><body><div data-v-ac5161ac=\"\" style=\"text-align: left;padding:0 20px;\"><div class=\"font-weight\"><div class=\"right-say\"><div class=\"else-page-contentBox\"><p style=\"text-align: center;\">【提示条款】</p></div><div class=\"else-page-contentBox\"><p>欢迎您与新屹（XINYI）共同签署本《用户协议》（下称“本协议”）并使用本网站服务！</p></div><div class=\"else-page-contentBox\"><p>您在申请注册流程中点击同意本协议之前，应当认真阅读本协议。请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。 免除或者限制责任的条款将以粗体下划线标识，您应重点阅读。如您对协议有任何疑问，可向本网站咨询。</p></div><div class=\"else-page-contentBox\"><p>当您按照注册页面提示填写信息、阅读并同意本协议且完成全部注册程序后，即表示您已充分阅读、理解并接受本协议的全部内容，并与本网站达成一致，成为本网站“用户”。阅读本协议的过程中，若您不同意本协议或其中任何条款约定，您应立即停止注册程序。</p></div><div class=\"else-page-contentBox\"><p>若您未申请注册流程，或者在本协议生效前已成为本网站的注册用户，则您通过访问和/或使用本网站，即视为您表示同意接受本协议的全部内容。</p></div></div></div><div class=\"else-page-contentBox\"><p>1. 账户说明</p><p class=\"p-content-this\">当您在新屹（XINYI）进行个人用户注册、参加网上或论坛等各种活动时，在您的同意及确认下，本网站将通过注册表格等形式要求您提供一些个人资料。您有权在任何时候拒绝提供这些信息，您在本网站上所提供的所有信息都是基于自愿的原则。</p></div><div class=\"else-page-contentBox\"><p>2. 用户信息管理</p><p class=\"p-content-this\">（1）在使用本网站服务时，您应当按本网站页面的提示准确完整地提供信息（包括但不限于用户的姓名及电子邮件地址、联系电话等），以便新屹（XINYI）在必要时与您联系。用户应当了解并同意，您有义务保持您提供信息的真实性及有效性。</p><p>用户所设置的账户名不得违反国家法律法规关于账户名的管理规定，否则新屹（XINYI）可对您的账户名进行暂停使用或注销等处理，并向主管机关报告。</p><p>用户应当理解并承诺，您的账户名称、头像和简介等注册信息中不得出现违法和不良信息，没有冒用、关联机构或社会名人，您在账户注册过程中需遵守法律法规、社会主义制度、国家利益、公民合法权益、公共秩序、社会道德风尚和信息真实性等七条底线。</p><p class=\"p-content-this\">（2）企业会员所提供的资料将会被本网站统计、汇总，本网站会不定期地通过企业会员留下的信息资料同该企业会员保持联系。</p><p class=\"p-content-this\">（3） 在未经访问者授权同意的情况下，本网站不会将用户的个人资料、企业资料泄露给第三方。以下情况除外： <span class=\"p-span\">（a）根据执法单位之要求或为公共之目的向相关单位提供个人资料；</span><span class=\"p-span\">（b）由于用户将其账户密码告知他人或与他人共享注册帐户，由此导致的任何个人资料泄露；</span><span class=\"p-span\">（c）由于黑客攻击、计算机病毒侵入或发作、因政府管制而造成的暂时性关闭等影响网络正常经营之不可抗力而造成的个人资料泄露、丢失、被盗用或被窜改等；</span><span class=\"p-span\">（d）由于与本网站链接的其他网站所造成之个人资料泄露及由此而导致的任何法律争议和后果；</span> <span class=\"p-span\">（e）为免除用户或访问者在生命、身体或财产方面之急迫危险。</span></p><p class=\"p-content-this\">（4） 本网站所提供的服务会自动收集有关用户或访问者的信息，这些信息包括访问者人数、访问时间、访问页面、来访地址等，本网站使用这些信息来对我们的服务器进行分析和对网站进行管理。</p></div><div class=\"else-page-contentBox\"><p>3. 服务规范</p><p class=\"p-content-this\">您可通过新屹（XINYI）网站服务在本网站上传、发布或传输相关内容，包括但不限于文字、软件、程序、图形、图片、声音、音乐、视频、音视频、链接等信息或其他资料（下称“内容”），但您需对此内容承担相关的法律责任。</p><p>除非有相反证明，我方将您视为您在新屹（XINYI）网站上传、发布或传输的内容的版权拥有人。您使用本网站上传、发布或传输内容即代表了您有权且同意在全世界范围内，永久性的、不可撤销的、免费的授予我方对该内容的存储、使用、发布、复制、修改、改编、出版、翻译、据以创作衍生作品、传播、表演和展示等权利；将内容的全部或部分编入其他任何形式的作品、媒体、技术中的权利；对您的上传、发布的内容进行商业开发的权利；通过有线或无线网络向您的计算机终端、移动通讯终端（包括但不限于便携式通讯设备如手机和智能平板电脑等）、手持数字影音播放设备、电视接收设备（模拟信号接收设备、数字信号接收设备、数字电视、IPTV、带互联网接入功能的播放设备等）等提供信息的下载、点播、数据传输、移动视频业务（包括但不限于SMS、MMS、WAP、IVR、Streaming、3G、手机视频等无线服务）、及其相关的宣传和推广等服务的权利；以及再授权给其他第三方以上述方式使用的权利。</p><p>您理解并知晓在使用新屹（XINYI）网站服务时，所接触的内容和信息来源广泛，我方无法对该内容和信息的准确性、真实性、可用性、安全性、完整性和正当性负责。您理解并认可您可能会接触到不正确的、令人不快的、不适当的或令人厌恶的内容和信息，您不会以此追究我方的相关责任。我方不对用户在新屹（XINYI）上传、发布或传输的任何内容和信息背书、推荐或表达观点，也不对任何内容和信息的错误、瑕疵及产生的损失或损害承担任何责任，您对内容和信息的任何使用需自行承担相关的风险。</p><p>您同意我方在提供服务的过程中以各种方式投放商业性广告或其他任何类型的商业信息（包括但不限于在我方平台的任何位置上投放广告，在您上传、传播的内容中投放广告），您同意接受我方通过电子邮件、站内短信、手机短信、网站公告或其他方式向您发送促销或其他相关商业信息。</p><p>您同意在使用新屹（XINYI）网站服务过程中，遵守以下法律法规：《中华人民共和国保守国家秘密法》、《中华人民共和国著作权法》、《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》、《互联网电子公告服务管理规定》、《信息网络传播权保护条例》等有关计算机及互联网规定的法律、法规。在任何情况下，我方一旦合理地认为您的行为可能违反上述法律、法规，可以在任何时候，不经事先通知终止向您提供服务。</p></div><div class=\"else-page-contentBox\"><p>4. 侵权责任认定</p><p class=\"p-content-this\">如个人或单位发现新屹（XINYI）上存在侵犯其自身合法权益的内容，请及时与新屹（XINYI）取得联系，并提供具有法律效应的证明材料，以便新屹（XINYI）作出处理。<label class=\"font-weight\">新屹（XINYI）有权根据实际情况删除相关的内容，并追究相关用户的法律责任。给新屹（XINYI）或任何第三方造成损失的，侵权用户应负全部责任。</label></p></div><div class=\"else-page-contentBox\"><p>5. 用户违约及处理</p><p class=\"p-content-this\">发生如下情形之一的，视为您违约：</p><p>（一）使用我方平台服务时违反有关法律法规规定的；</p><p>（二）违反本协议约定的。</p><p class=\"p-content-this\">您在新屹（XINYI）网站上发布的内容和信息构成违约的，我方可根据相应规则立即对相应内容和信息进行删除、屏蔽等处理或对您的账户进行暂停使用、查封、注销等处理。</p><p>您在新屹（XINYI）网站上实施的行为，或虽未在新屹（XINYI）网站上实施但对新屹（XINYI）网站及用户产生影响的行为构成违约的，我方可依据相应规则对您的账户执行限制参加活动、中止向您提供部分或全部服务等处理措施。如您的行为构成根本违约的，我方可查封您的账户，终止向您提供服务。</p><p>如果您在新屹（XINYI）网站上的行为违反相关的法律法规，我方可依法向相关主管机关报告并提交您的使用记录和其他信息。</p><p>如您的行为使我方及/或其关联公司遭受损失（包括自身的直接经济损失、商誉损失及对外支付的赔偿金、和解款、律师费、诉讼费等间接经济损失），您应赔偿我方及/或其关联公司的上述全部损失。</p><p>如您的行为使我方及/或其关联公司遭受第三人主张权利，我方及/或其关联公司可在对第三人承担金钱给付等义务后就全部损失向您追偿。</p></div><div class=\"else-page-contentBox\"><p>6. Cookie的使用</p><p class=\"p-content-this\">Cookie是一个标准的互联网技术，它可以使我们存储和获得用户登录信息。本站点使用Cookie 来确保您不会重复浏览到相同的内容并可以获得最新的信息，并确认您是新屹（XINYI）的成员之一。我方并不使用Cookie来跟踪任何个人信息。</p></div><div class=\"else-page-contentBox\"><p>7. 法律适用及争议解决</p><div class=\"font-weight\"><p class=\"p-content-this\">本协议之订立、生效、解释、修订、补充、终止、执行与争议解决均适用中华人民共和国法律；如法律无相关规定的，参照商业惯例及/或行业惯例。</p><p>您因使用新屹（XINYI）网站服务所产生及与新屹（XINYI）网站服务有关的争议，由我方与您协商解决。协商不成时，任何一方均可向被告所在地人民法院提起诉讼。</p><p>本协议任一条款被视为废止、无效或不可执行，该条应视为可分的且并不影响本协议其余条款的有效性及可执行性。</p></div></div></div></body></html>",
					     1,
						new AlertDialog.DialogClick() {
						@Override
						public void leftClick() {

						}

						@Override
						public void rightCLick() {

						}
					});
				alertDialog.show(getFragmentManager(),"alert");
			}
		});
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					qihuanTab2();
					break;
			}
		}
	};

	public void qihuanTab2(){
		//加载消息界面
		conversationListFragment = new EaseConversationListFragment();
		getLxrList();
		getSupportFragmentManager().beginTransaction().replace(R.id.content, conversationListFragment).commit();
		mMenu.setIsOpen(true);
		mMenu.closeMenu();
	}

	public void updateUser(){

		new Thread(new Runnable() {
			@Override
			public void run() {
				if(getLxrList()!= null){

					if (state){
						AppUtils.dbUtils.execSql2("sys_user", (List<Map<String, Object>>) get_catch().get("lxrList"),"login_name,name,huanxincode,huanxinpassword,photo_16,"+AppUtils.app_address+",8594584","login_name,name,huanxincode,huanxinpassword,photo_16,address,pinyin");
						_catch.remove("lxrList");
					}
					updateUserState = true;

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							WeiboDialogUtils.closeDialog(mDialog);
							mHandler.sendEmptyMessageDelayed(1,500);
						}
					});
				}else{
					updateUserState = true;
				}
			}
		}).start();
	}

	public Map<String,Object> getLxrList(){
		List<Map<String,Object>> lxrList = null,bmList = null;

		if (_catch != null && _catch.get("lxrList") == null){
			AppUtils.dbUtils = DatabaseUtils.getInstance(MyFragmentActivity.this,"wtmis.db");
			lxrList = AppUtils.dbUtils.exec_select("sys_user",new String[]{"address","login_name","name","huanxincode","huanxinpassword","photo_16"},"address = ?",new String[]{AppUtils.app_address},null,null,"pinyin");
			if (lxrList == null || lxrList.size() == 0){
				state = true;
				String pars = new ListParms("0","0","10000","zydn","isDataQxf:1",1).getParms();
				Object res = ActivityController.getObjectData2ByPost(mContext,"zydn","list", StringUtils.strTOJsonstr(pars));

				if (res != null && res instanceof JSONArray){
					lxrList = (List<Map<String, Object>>) res;
					_catch.remove("lxrList");
					_catch.put("lxrList",lxrList);
				}
			}else{
				_catch.remove("lxrList");
				_catch.put("lxrList",lxrList);
			}
		}

		if (_catch != null && _catch.get("bmList") == null){
			String pars = new ListParms("0","0","10000","bmdn","isDataQxf:1",1).getParms();
			Object res = ActivityController.getObjectData2ByPost(mContext,"bmdn","list", StringUtils.strTOJsonstr(pars));

			if (res != null && res instanceof JSONArray){
				lxrList = (List<Map<String, Object>>) res;
				_catch.remove("bmList");
				_catch.put("bmList",lxrList);
			}
		}

		Map<String,Object> resMap = new HashMap<String, Object>();

		if (_catch.get("lxrList") != null &&  _catch.get("bmList") != null){
			resMap.put("lxrList",_catch.get("lxrList"));
			resMap.put("bmList",_catch.get("bmList"));
		}

		return  resMap;
	}

	public void initData(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("img",R.mipmap.shouye);
		map.put("text","首页");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.refresh);
		map.put("text","清空缓存");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.tuichu);
		map.put("text","退出");
		lv_listMap.add(map);

		//解析图片资源
		Bitmap touxiang_map = AppUtils.getBitmap(this);
		if (touxiang_map != null){
			touxiang = (ImageView) findViewById(R.id.touxiang);
			touxiang.setBackground(FileUtils.bitmapToDrawable(getResources(),touxiang_map));
		}
	}

	public class MyLisViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return lv_listMap.size();
		}

		@Override
		public Object getItem(int position) {
			return lv_listMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.page01_listview_item, parent, false);

			ImageView iv1 = (ImageView)convertView.findViewById(R.id.page01_lv_iv_);
			TextView tv1 = (TextView)convertView.findViewById(R.id.page01_lv_item_text_);
			ImageView youjiantou = (ImageView) convertView.findViewById(R.id.youjiantou);
			youjiantou.setVisibility(View.GONE);
			tv1.setTextColor(getResources().getColor(R.color.colorWhite));
			Map map = lv_listMap.get(position);
			iv1.setImageResource((Integer) map.get("img"));
			tv1.setText((String)map.get("text"));
			return convertView;
		}

	}

	public class MyOnClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map map = lv_listMap.get(position);
			String name = (String)map.get("text");

			Class testClass = null;

			switch (name){
				case "首页":
					mMenu.toggle();
					break;
				case "清空缓存":
					_data.clear();
					_catch.clear();
					break;
				case "退出":
					ActivityController.finishAll();
					try {
						EMClient.getInstance().logout(true);
						JPushInterface.setAlias(getApplicationContext(),"",null);
					}catch (Exception e){
						e.printStackTrace();
					}

					Intent intent = new Intent(MyFragmentActivity.this, LoginActivity.class);
					intent.putExtra("type","tuichu");
					startActivity(intent);
					_data.clear();
					_catch.clear();
					if (AppUtils.fwq_reportmenuList != null){
						AppUtils.fwq_reportmenuList.clear();
					}
					AppUtils.login_state = false;
					AppUtils.app_ip = null;
					//清空缓存
					SharePreferencesUtils.clear((Activity) mContext);
					break;
				default:
					break;
			}
		}
	}

	private void updateRadioButtonDrawable(int table_id,int selected_id,int empty_id){
		drawable = new StateListDrawable();
		drawable.addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_selected, -android.R.attr.state_pressed},
				getResources().getDrawable(selected_id));
		drawable.addState(new int[]{-android.R.attr.state_empty},
				getResources().getDrawable(empty_id));
		table = (RadioButton) findViewById(table_id);
		table.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 9){
			if (bangong_Fragment != null){
				try {
					ReflectHelper.callMethod2(bangong_Fragment,"reshData",null,String.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private EMMessageListener msgListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			//收到消息
			Log.v("test","文本消息来了！");
			if (AppUtils.isApplicationInBackground(getApplicationContext())) {
				EMMessage message =  messages.get(0);
				EMMessageBody body = message.getBody();
				String title = message.getUserName(),
				       id = "",name = "";

				Map<String,Object> lxrMap = null;

				if (get_catch().get("lxrList") != null){
					lxrMap = FastJsonUtils.findMapToListByKey((List<Map<String, Object>>) get_catch().get("lxrList"),"huanxincode",title);
				}

				if (lxrMap == null){
					return;
				}else{
					name = lxrMap.get("name").toString();
					id = title;
					String time = DateUtil.stampToDate(String.valueOf(message.getMsgTime()));
					title = name + "      "+time;
				}
				String content = body.toString().replace("txt:","");
				String to = message.getTo();
				EaseUser user = HuanxinUtils.getUserByCode(to);
				if (user == null){

				}
				ActivityController.createNotification((Activity)mContext,title,content,id,name,to);
			}

			//消息状态变动
			//updateXiaohongdian();

			if (table_index == 2 && conversationListFragment != null){
				conversationListFragment.reshView();
				conversationListFragment.getConversationListView().refresh();
			}

		}

		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			//收到透传消息
			Log.v("test","文本消息来了！1");
		}

		@Override
		public void onMessageRead(List<EMMessage> messages) {
			//收到已读回执
			Log.v("test","收到已读回执");
		}

		@Override
		public void onMessageDelivered(List<EMMessage> message) {
			//收到已送达回执
			Log.v("test","文本消息来了！3");
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {
			//消息状态变动
			updateXiaohongdian();

			if (table_index == 2 && conversationListFragment != null){
				conversationListFragment.getConversationListView().refresh();
			}
		}
	};


	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
		intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);

		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.v("test","通知消息来了！");
				String action = intent.getAction();
				if(action.equals(Constant.ACTION_GROUP_CHANAGED)){

				}

			}
		};
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}

	private void unregisterBroadcastReceiver(){
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	public void updateXiaohongdian(){
		Map<String,EMConversation>  conversations = EMClient.getInstance().chatManager().getAllConversations()/*Conversation(AppUtils.huanxincode,null,true)*/;
		int count = 0;
		//获取此会话在本地的所有的消息数量
		Set<String> sets =  conversations.keySet();
		EMConversation conversation = null;
		for (String key:sets) {
			conversation = conversations.get(key);
			if (conversation != null){
				count += conversation.getUnreadMsgCount();
			}else{
				count = 0;
			}
		}

		/*if (count != 0){
			final int finalCount = count;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					badge.setText(String.valueOf(finalCount));
					badge.show();
				}
			});
		}else{
			badge.hide();
		}*/
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//updateXiaohongdian();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//EMClient.getInstance().chatManager().addMessageListener(msgListener);
		registerBroadcastReceiver();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//EMClient.getInstance().chatManager().removeMessageListener(msgListener);
		unregisterBroadcastReceiver();
	}
}
