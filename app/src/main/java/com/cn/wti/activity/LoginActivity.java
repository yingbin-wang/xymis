package com.cn.wti.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.fwq_address.FwqAddressActivity;
import com.cn.wti.activity.tab.MyFragmentActivity;
import com.cn.wti.entity.Sys_user;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.dialog.AlertDialog;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.app.qx.QxUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.SharePreferencesUtils;
import com.cn.wti.util.number.IpAdressUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.db.DatabaseUtils;
import com.cn.wti.util.other.StringUtils;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView iv_login,iv_xuanzhuan;
    private ImageButton login_show; // 查看按钮
    AlertDialog alertDialog = null;

    private DatabaseUtils dbUtils = null;
    private SQLiteDatabase wtmisdb = null;
    private System_one app_information = null;
    private List<Map<String,Object>> addressList = null;
    private static String APPID = "8588fb85f7ad8e27caa79952";
    private SharedPreferences sp;
    private String username,password;
    private Spinner spinner;
    private Dialog mDialog;
    private RotateAnimation animation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    attemptLogin();
                    WeiboDialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_login);
        AppUtils.setStatusBarColor(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = WeiboDialogUtils.createLoadingDialog(LoginActivity.this, "登陆中...");
                mHandler.sendEmptyMessageDelayed(1,2000);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        iv_login = findViewById(R.id.imageView_login);
        iv_login.setImageDrawable(getResources().getDrawable(R.mipmap.dalogo));

        //测试权限
        //QxUtils.getInstance(this).insertDummyContactWrapper();

        //打开或创建数据库
        dbUtils  = DatabaseUtils.getInstance(LoginActivity.this, "wtmis.db");

        if(!dbUtils.tabIsExist("sys_fwqaddress")){
            dbUtils.execSql("create table sys_fwqaddress(id integer primary key autoincrement,name varchar(40),address varchar(60))");
            //创建默认账套
            addDefaultAccount();

            dbUtils.execSql("create table sys_menu(address varchar(40),code varchar(40),name varchar(40),username varchar(40),package varchar(40),action_name varchar(60),ioc_name varchar(30),menuid integer,sfxs char(1) default 1,primary key (address,code,username))");
            dbUtils.execSql("create table sys_user(address varchar(40),login_name varchar(100),name varchar(40),huanxincode varchar(100),huanxinpassword varchar(100),photo_16 text,pinyin varchar(40),primary key (address,huanxincode))");
        }

        addressList = dbUtils.exec_select("sys_fwqaddress",new String[]{"id","name","address"},null,null,null,null,null);
        if (addressList == null || addressList.size() == 0){
            //创建默认账套
            addDefaultAccount();
        }
        bindSpinner();
        //默认测试用户
        /*mEmailView.setText("yantao.ding");
        mPasswordView.setText("yantao.ding");*/

        login_show = (ImageButton) findViewById(R.id.login_show) ;

        login_show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,FwqAddressActivity.class);
                intent.putExtras(AppUtils.setParms("",addressList));
                startActivityForResult(intent,1);

               /* 测试数据*/
              /* Bundle bundle = new Bundle();
                app_information = new System_one(AppUtils.app_address,AppUtils.app_username);
                bundle.putSerializable("app_information",app_information);
                intent.putExtras(bundle);*/

               /* List<String> list = new ArrayList<String>();
                list.add("1hehe");
                list.add("2hehe");
                mPop=new AbstractChoicePopWindow_simple2(LoginActivity.this,list);
                mPop.setOnItemClickListener(LoginActivity.this);
                mPop.showAtLocation(LoginActivity.this.findViewById(R.id.main_), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);*/
            }
        });


        iv_xuanzhuan = (ImageView) findViewById(R.id.xuanzhuan);

        new Thread(new Runnable() {
            @Override
            public void run() {
                /** 设置旋转动画 */
                animation =new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                        0.5f,Animation.RELATIVE_TO_SELF,0.5f);

                animation.setFillAfter(true);
                animation.setDuration(3000);
                animation.setRepeatCount(-1);
                animation.setInterpolator(new LinearInterpolator());
                iv_xuanzhuan.startAnimation(animation);
            }

        }).start();

        intView();

        //添加 公司logo
        if (sp.getString("app_logo","")!= null && !sp.getString("app_logo","").equals("")){
            iv_login.setImageBitmap(AppUtils.convertStringToIconBase64(sp.getString("app_logo",""),0,0,0));
        }

        Intent data = getIntent();
        String type = data.getStringExtra("type");
        if (type != null && type.equals("tuichu")){
            spinner.setSelection(sp.getInt("postion",0));
            return;
        }else{
            username =  sp.getString("username","");
            //自动登陆
            if (!username.equals("") && spinner.getSelectedItemPosition() >= 0){
                mEmailView.setText(username);
                mPasswordView.setText(sp.getString("password",""));
                spinner.setSelection(sp.getInt("postion",0));
                mDialog = WeiboDialogUtils.createLoadingDialog(LoginActivity.this, "登陆中...");
                mHandler.sendEmptyMessageDelayed(1,1000);
            }else{
                if (!sp.getBoolean("firstUser",false)){
                    if (alertDialog != null){
                        alertDialog.show(getFragmentManager(),"alertDialog");
                    }
                }
            }
        }
    }

    private void addDefaultAccount() {
        ContentValues cv = new ContentValues();
        cv.put("name","上海新屹");
        cv.put("address","cs96.xysoft.xyz");

        dbUtils.exec_insert("sys_fwqaddress", cv);
    }

    private void intView() {

        alertDialog = new AlertDialog(getResources().getString(R.string.user_info_hint),"<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title><style></style></head><body><div data-v-ac5161ac=\"\" style=\"text-align: left;padding:0 20px;\"><div class=\"font-weight\"><div class=\"right-say\"><div class=\"else-page-contentBox\"><p style=\"text-align: center;\">【提示条款】</p></div><div class=\"else-page-contentBox\"><p>欢迎您与新屹（XINYI）共同签署本《用户协议》（下称“本协议”）并使用本网站服务！</p></div><div class=\"else-page-contentBox\"><p>您在申请注册流程中点击同意本协议之前，应当认真阅读本协议。请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。 免除或者限制责任的条款将以粗体下划线标识，您应重点阅读。如您对协议有任何疑问，可向本网站咨询。</p></div><div class=\"else-page-contentBox\"><p>当您按照注册页面提示填写信息、阅读并同意本协议且完成全部注册程序后，即表示您已充分阅读、理解并接受本协议的全部内容，并与本网站达成一致，成为本网站“用户”。阅读本协议的过程中，若您不同意本协议或其中任何条款约定，您应立即停止注册程序。</p></div><div class=\"else-page-contentBox\"><p>若您未申请注册流程，或者在本协议生效前已成为本网站的注册用户，则您通过访问和/或使用本网站，即视为您表示同意接受本协议的全部内容。</p></div></div></div><div class=\"else-page-contentBox\"><p>1. 账户说明</p><p class=\"p-content-this\">当您在新屹（XINYI）进行个人用户注册、参加网上或论坛等各种活动时，在您的同意及确认下，本网站将通过注册表格等形式要求您提供一些个人资料。您有权在任何时候拒绝提供这些信息，您在本网站上所提供的所有信息都是基于自愿的原则。</p></div><div class=\"else-page-contentBox\"><p>2. 用户信息管理</p><p class=\"p-content-this\">（1）在使用本网站服务时，您应当按本网站页面的提示准确完整地提供信息（包括但不限于用户的姓名及电子邮件地址、联系电话等），以便新屹（XINYI）在必要时与您联系。用户应当了解并同意，您有义务保持您提供信息的真实性及有效性。</p><p>用户所设置的账户名不得违反国家法律法规关于账户名的管理规定，否则新屹（XINYI）可对您的账户名进行暂停使用或注销等处理，并向主管机关报告。</p><p>用户应当理解并承诺，您的账户名称、头像和简介等注册信息中不得出现违法和不良信息，没有冒用、关联机构或社会名人，您在账户注册过程中需遵守法律法规、社会主义制度、国家利益、公民合法权益、公共秩序、社会道德风尚和信息真实性等七条底线。</p><p class=\"p-content-this\">（2）企业会员所提供的资料将会被本网站统计、汇总，本网站会不定期地通过企业会员留下的信息资料同该企业会员保持联系。</p><p class=\"p-content-this\">（3） 在未经访问者授权同意的情况下，本网站不会将用户的个人资料、企业资料泄露给第三方。以下情况除外： <span class=\"p-span\">（a）根据执法单位之要求或为公共之目的向相关单位提供个人资料；</span><span class=\"p-span\">（b）由于用户将其账户密码告知他人或与他人共享注册帐户，由此导致的任何个人资料泄露；</span><span class=\"p-span\">（c）由于黑客攻击、计算机病毒侵入或发作、因政府管制而造成的暂时性关闭等影响网络正常经营之不可抗力而造成的个人资料泄露、丢失、被盗用或被窜改等；</span><span class=\"p-span\">（d）由于与本网站链接的其他网站所造成之个人资料泄露及由此而导致的任何法律争议和后果；</span> <span class=\"p-span\">（e）为免除用户或访问者在生命、身体或财产方面之急迫危险。</span></p><p class=\"p-content-this\">（4） 本网站所提供的服务会自动收集有关用户或访问者的信息，这些信息包括访问者人数、访问时间、访问页面、来访地址等，本网站使用这些信息来对我们的服务器进行分析和对网站进行管理。</p></div><div class=\"else-page-contentBox\"><p>3. 服务规范</p><p class=\"p-content-this\">您可通过新屹（XINYI）网站服务在本网站上传、发布或传输相关内容，包括但不限于文字、软件、程序、图形、图片、声音、音乐、视频、音视频、链接等信息或其他资料（下称“内容”），但您需对此内容承担相关的法律责任。</p><p>除非有相反证明，我方将您视为您在新屹（XINYI）网站上传、发布或传输的内容的版权拥有人。您使用本网站上传、发布或传输内容即代表了您有权且同意在全世界范围内，永久性的、不可撤销的、免费的授予我方对该内容的存储、使用、发布、复制、修改、改编、出版、翻译、据以创作衍生作品、传播、表演和展示等权利；将内容的全部或部分编入其他任何形式的作品、媒体、技术中的权利；对您的上传、发布的内容进行商业开发的权利；通过有线或无线网络向您的计算机终端、移动通讯终端（包括但不限于便携式通讯设备如手机和智能平板电脑等）、手持数字影音播放设备、电视接收设备（模拟信号接收设备、数字信号接收设备、数字电视、IPTV、带互联网接入功能的播放设备等）等提供信息的下载、点播、数据传输、移动视频业务（包括但不限于SMS、MMS、WAP、IVR、Streaming、3G、手机视频等无线服务）、及其相关的宣传和推广等服务的权利；以及再授权给其他第三方以上述方式使用的权利。</p><p>您理解并知晓在使用新屹（XINYI）网站服务时，所接触的内容和信息来源广泛，我方无法对该内容和信息的准确性、真实性、可用性、安全性、完整性和正当性负责。您理解并认可您可能会接触到不正确的、令人不快的、不适当的或令人厌恶的内容和信息，您不会以此追究我方的相关责任。我方不对用户在新屹（XINYI）上传、发布或传输的任何内容和信息背书、推荐或表达观点，也不对任何内容和信息的错误、瑕疵及产生的损失或损害承担任何责任，您对内容和信息的任何使用需自行承担相关的风险。</p><p>您同意我方在提供服务的过程中以各种方式投放商业性广告或其他任何类型的商业信息（包括但不限于在我方平台的任何位置上投放广告，在您上传、传播的内容中投放广告），您同意接受我方通过电子邮件、站内短信、手机短信、网站公告或其他方式向您发送促销或其他相关商业信息。</p><p>您同意在使用新屹（XINYI）网站服务过程中，遵守以下法律法规：《中华人民共和国保守国家秘密法》、《中华人民共和国著作权法》、《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》、《互联网电子公告服务管理规定》、《信息网络传播权保护条例》等有关计算机及互联网规定的法律、法规。在任何情况下，我方一旦合理地认为您的行为可能违反上述法律、法规，可以在任何时候，不经事先通知终止向您提供服务。</p></div><div class=\"else-page-contentBox\"><p>4. 侵权责任认定</p><p class=\"p-content-this\">如个人或单位发现新屹（XINYI）上存在侵犯其自身合法权益的内容，请及时与新屹（XINYI）取得联系，并提供具有法律效应的证明材料，以便新屹（XINYI）作出处理。<label class=\"font-weight\">新屹（XINYI）有权根据实际情况删除相关的内容，并追究相关用户的法律责任。给新屹（XINYI）或任何第三方造成损失的，侵权用户应负全部责任。</label></p></div><div class=\"else-page-contentBox\"><p>5. 用户违约及处理</p><p class=\"p-content-this\">发生如下情形之一的，视为您违约：</p><p>（一）使用我方平台服务时违反有关法律法规规定的；</p><p>（二）违反本协议约定的。</p><p class=\"p-content-this\">您在新屹（XINYI）网站上发布的内容和信息构成违约的，我方可根据相应规则立即对相应内容和信息进行删除、屏蔽等处理或对您的账户进行暂停使用、查封、注销等处理。</p><p>您在新屹（XINYI）网站上实施的行为，或虽未在新屹（XINYI）网站上实施但对新屹（XINYI）网站及用户产生影响的行为构成违约的，我方可依据相应规则对您的账户执行限制参加活动、中止向您提供部分或全部服务等处理措施。如您的行为构成根本违约的，我方可查封您的账户，终止向您提供服务。</p><p>如果您在新屹（XINYI）网站上的行为违反相关的法律法规，我方可依法向相关主管机关报告并提交您的使用记录和其他信息。</p><p>如您的行为使我方及/或其关联公司遭受损失（包括自身的直接经济损失、商誉损失及对外支付的赔偿金、和解款、律师费、诉讼费等间接经济损失），您应赔偿我方及/或其关联公司的上述全部损失。</p><p>如您的行为使我方及/或其关联公司遭受第三人主张权利，我方及/或其关联公司可在对第三人承担金钱给付等义务后就全部损失向您追偿。</p></div><div class=\"else-page-contentBox\"><p>6. Cookie的使用</p><p class=\"p-content-this\">Cookie是一个标准的互联网技术，它可以使我们存储和获得用户登录信息。本站点使用Cookie 来确保您不会重复浏览到相同的内容并可以获得最新的信息，并确认您是新屹（XINYI）的成员之一。我方并不使用Cookie来跟踪任何个人信息。</p></div><div class=\"else-page-contentBox\"><p>7. 法律适用及争议解决</p><div class=\"font-weight\"><p class=\"p-content-this\">本协议之订立、生效、解释、修订、补充、终止、执行与争议解决均适用中华人民共和国法律；如法律无相关规定的，参照商业惯例及/或行业惯例。</p><p>您因使用新屹（XINYI）网站服务所产生及与新屹（XINYI）网站服务有关的争议，由我方与您协商解决。协商不成时，任何一方均可向被告所在地人民法院提起诉讼。</p><p>本协议任一条款被视为废止、无效或不可执行，该条应视为可分的且并不影响本协议其余条款的有效性及可执行性。</p></div></div></div></body></html>",
            new AlertDialog.DialogClick() {
            @Override
            public void leftClick() {
                finish();
                System.exit(0);
            }

            @Override
            public void rightCLick() {
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("firstUser",true);
            }
        });
        /*openUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                alertDialog.show(getFragmentManager(),"alertDialog");
            }
        });*/
    }

    //得到返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == 1){
            System_one system_one = (System_one) intent.getSerializableExtra("parms");
            if (system_one.getList()!= null){
                addressList = system_one.getList();
                bindSpinner();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void bindSpinner(){
        spinner = (Spinner) findViewById(R.id.spinner_sysinformation);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.support_simple_spinner_dropdown_item,FastJsonUtils.MapToListByKey(addressList,"name") );
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                String option = spinner.getItemAtPosition(position).toString();
                if(!option.equals("")){
                    Map<String,Object>  map = addressList.get(position);
                    if (map != null){
                        AppUtils.app_name = map.get("name").toString();
                        AppUtils.book_name = map.get("name").toString();
                        AppUtils.app_address = map.get("address").toString();
                        AppUtils.dbUtils = dbUtils;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /*Toast.makeText(getApplicationContext(), "没有改变的处理", Toast.LENGTH_LONG).show();*/
            }

        });
    }

    private void attemptLogin() {

        View focusView = null;
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        boolean cancel = false;

        mEmailView.setError(null);
        mPasswordView.setError(null);

        if (TextUtils.isEmpty(email)){
            mEmailView.setError(getString(R.string.error_empty_eail));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
            return;
        }

        final Map<String,Object> parms = new HashMap<String, Object>();
        parms.put("USERNAME",email);
        parms.put("PASSWORD",password);
        if (TextUtils.isEmpty(AppUtils.book_name) || AppUtils.book_name.equals("bd") || AppUtils.book_name.equals("fwq")){
            AppUtils.book_name = "test融影1";
        }else if (AppUtils.book_name.equals("fwq")){
            AppUtils.book_name = "test融影1";
        }
        parms.put("database",AppUtils.book_name);
        if(AppUtils.app_address == null || AppUtils.app_address.equals("")){
            AppUtils.app_address = getResources().getString(R.string.ip_local);
        }
        AppUtils.app_ip = IpAdressUtils.getIp(LoginActivity.this);
        AppUtils.app_username = email;
        final Map<String,Object> finalparms = parms;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean state = false;
                    if (AppUtils.app_ip == null || AppUtils.app_ip.equals("")){
                       // AppUtils.app_ip = HttpClientUtils.GetNetIp();
                       /* if (AppUtils.app_ip.contains("(abcdef)") || AppUtils.app_ip.equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        AppUtils.app_ip = IpAdressUtils.getIp(LoginActivity.this);
                                    }});
                            return;
                        }*/
                    }
                    while (AppUtils.app_ip != null /*&& !AppUtils.app_ip.equals("")*/){
                        parms.put("ip",AppUtils.app_ip);
                        final String result= HttpClientUtils.webService("login",StringUtils.strTOJsonstr(FastJsonUtils.mapToString(finalparms)));
                        if (result != null && ActivityController.getPostState(result) == 0){
                            if (!result.equals("")) {
                                final JSONObject res = FastJsonUtils.strToJson(result);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!res.get("state").toString().equals("success")) {
                                            Toast.makeText(LoginActivity.this, "登陆失败，" + res.get("msg"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Map<String, Object> map = FastJsonUtils.strToMap(res.get("data").toString());
                                            if (res.get("app_logo") != null) {
                                                AppUtils.app_ioc = res.get("app_logo").toString();
                                                map.put("app_logo", AppUtils.app_ioc);
                                            }

                                            //保存用户
                                            saveUser(map);

                                            //编码规则
                                            final String alias = AppUtils.app_username+StringUtils.getStringRandom(10)/*+AppUtils.app_address.replace(".","_")*/;
                                            //服务器别名
                                            String check_alias = AppUtils.app_address.replace(".","_"),
                                                   fwq_alias = "";
                                            if (map.get("alias") != null){
                                                fwq_alias = map.get("alias").toString();
                                            }

                                            //如果 别名为空 或者 服务器别名 不存在服务器地址 重新绑定设备更新服务器别名
                                            if (map.get("alias") == null || (map.get("alias").equals("")) /*|| (fwq_alias.indexOf(check_alias) < 0)*/) {

                                                final String rid = JPushInterface.getRegistrationID(getApplicationContext());
                                                Set<String> sets = new HashSet<String>();
                                                sets.add(rid);
                                                JPushInterface.setAliasAndTags(getApplicationContext(), alias, sets, new TagAliasCallback() {//回调接口,i=0表示成功,其它设置失败
                                                    @Override
                                                    public void gotResult(int i, String s, Set<String> set) {
                                                        Log.d("alias", "set alias result is" + rid + i);
                                                        String pars = "alias:" + alias + ",username:" + AppUtils.app_username;
                                                        pars = StringUtils.strTOJsonstr(pars);
                                                        final String finalPars = pars;
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ActivityController.execute(LoginActivity.this, "user", "updateAlias", finalPars);
                                                            }
                                                        }).start();
                                                    }
                                                });
                                            }else {
                                                String alias1 = map.get("alias").toString();
                                                String rid = JPushInterface.getRegistrationID(getApplicationContext());
                                                Set<String> sets = new HashSet<String>();
                                                sets.add(rid);
                                                JPushInterface.setAliasAndTags(getApplicationContext(), alias1, sets, new TagAliasCallback() {//回调接口,i=0表示成功,其它设置失败
                                                    @Override
                                                    public void gotResult(int i, String s, Set<String> set) {
                                                        Log.d("alias", "set alias result is");
                                                    }
                                                });
                                            }

                                            if (map.get("username") == null || map.get("staff_name") == null ||
                                                map.get("staffid") == null || map.get("departmentid") == null || map.get("department_name") == null) {
                                                Toast.makeText(LoginActivity.this, getString(R.string.err_data), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            AppUtils.user = new Sys_user(map.get("id").toString(),map.get("username").toString(), map.get("staff_name").toString(),
                                                    map.get("staffid").toString(), map.get("departmentid").toString(),
                                                    map.get("department_name").toString(),map.get("version_").toString(),AppUtils.book_name);

                                            AppUtils.user.set_password(password);
                                            if (res.get("sjjs") != null) {
                                                AppUtils.user.setSjjs(res.get("sjjs").toString());
                                            } else {
                                                AppUtils.user.setSjjs("0");
                                            }

                                            if (res.get("gsxz") != null) {
                                                AppUtils.user.setGsxz(res.get("gsxz").toString());
                                            } else {
                                                AppUtils.user.setGsxz("0");
                                            }

                                            if (res.get("ispricecontroll") != null) {
                                                AppUtils.user.setIspricecontroll(res.get("ispricecontroll").toString());
                                            } else {
                                                AppUtils.user.setIspricecontroll("0");
                                            }

                                            Intent intent = new Intent();
                                            intent.setClass(getApplicationContext(), MyFragmentActivity.class);
                                            (LoginActivity.this).startActivity(intent);

                                            if (animation != null) {
                                                animation.cancel();
                                            }

                                            AppUtils.login_state = true;
                                        }
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),HttpClientUtils.backMessage(ActivityController.getPostState(result)).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*SoapObject test =  WebServiceHelper.getSoapObject(AppUtils.app_address,"execWebService","loginByUernameAndPassword",parms);
        if(test == null){
            Toast.makeText(LoginActivity.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取返回的结果
        String result = test.getProperty(0).toString();*/

        // 将WebService返回的结果显示在TextView中
       /* message_tv.setText(result);*/

    }

    //执行保存数据库动作
    public void saveUser(Map<String,Object> userMap){

        String username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("name", userMap.get("staff_name").toString());
        editor.putString("staffid", userMap.get("staffid").toString());
        editor.putString("v_",userMap.get("version_").toString());
        editor.putInt("postion", spinner.getSelectedItemPosition());
        editor.putBoolean("firstUser",true);

        if (userMap.get("photo_16")!= null){
            editor.putString("photo_16", userMap.get("photo_16").toString().replace("data:image/png;base64,",""));
        }
        if (userMap.get("app_logo")!= null){
            editor.putString("app_logo", userMap.get("app_logo").toString().replace("data:image/png;base64,",""));
        }

        editor.commit();

    }
}

