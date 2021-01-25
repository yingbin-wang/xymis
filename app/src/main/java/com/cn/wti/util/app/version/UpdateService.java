package com.cn.wti.util.app.version;

/**
 * Created by Kodulf on 2017/5/8.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.cn.wti.util.app.AppUtils;
import com.wticn.wyb.wtiapp.R;

/**
 * http://blog.csdn.net/rodulf/article/details/51706788
 */

public class UpdateService extends Service{

    private String version,downUrl="";

    public UpdateService() {

    }

    /** 安卓系统下载类 **/
    DownloadManager manager;

    /** 接收下载完的广播 **/
    DownloadCompleteReceiver receiver;
    private Dialog mDialog;
    DownloadManager.Request down = null;
    Uri parse = null;

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showDialog();
                    break;
            }
        }
    };

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.setTitle("新屹软件")
                .setIcon(R.mipmap.xuanzhuan)
                .setMessage("发现新的版本，现在下载更新吗？")
                //相当于点击确认按钮
                .setPositiveButton("更新",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                        // 显示下载界面
                        down.setVisibleInDownloadsUi(true);
                        // 设置下载后文件存放的位置
                        String apkName = parse.getLastPathSegment();
                        down.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, apkName);
                        // 将下载请求放入队列
                        manager.enqueue(down);
                    }
                })
                //相当于点击取消按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialog != null){
                            mDialog.dismiss();
                        }
                    }
                }).create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    /** 初始化下载器 **/
    private void initDownManager() {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        if (!downUrl.equals("")){
            parse = Uri.parse(downUrl);
            down = new DownloadManager.Request(parse);

            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            // 下载时，通知栏显示途中
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                if (version != null && Integer.parseInt(AppUtils.version ) > Integer.parseInt(version)){
                    AppUtils.update_state = true;
                    myHandler.sendEmptyMessageDelayed(1,1000);
                }

            }
            //注册下载广播
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                version = String.valueOf(intent.getIntExtra("version",0));
                String str = HttpUtils.getVersion();
                if (str.indexOf(";")>=0){
                    String[] versionArray = str.split(";");
                    if (versionArray.length == 2){
                        AppUtils.version = str.split(";")[0];
                        downUrl = str.split(";")[1];
                    }
                }

                if (!AppUtils.version.equals("")){
                    // 调用下载
                    initDownManager();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d("kodulf","id="+downId);

                //自动安装apk
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Uri uriForDownloadedFile = manager.getUriForDownloadedFile(downId);
                    Log.d("kodulf","uri="+uriForDownloadedFile);

                    installApkNew(uriForDownloadedFile);
                }

                //停止服务并关闭广播
                UpdateService.this.stopSelf();

            }
        }

        //安装apk
        protected void installApkNew(Uri uri) {
            Intent intent = new Intent();
            //执行动作
            intent.setAction(Intent.ACTION_VIEW);
            //执行的数据类型
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            //不加下面这句话是可以的，查考的里面说如果不加上这句的话在apk安装完成之后点击单开会崩溃
            // android.os.Process.killProcess(android.os.Process.myPid());
            try {
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
