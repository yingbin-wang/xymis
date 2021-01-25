package com.cn.wti.activity.rwgl.signin;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SigninActivity extends BaseEdit_NoTable_Activity  implements LocationSource,AMapLocationListener,AMap.OnCameraChangeListener {


    private Map<String, Object> parmsMap, main_data = new HashMap<String, Object>(), select_map, map;
    private String pars, type = "",address,city;
    LinearLayout main_form;
    private MapView mapView;;
    private TextView_custom shangwuqiandaoshijian,xiawuqiandaoshijian,shangwuqiandaodidian,xiawuqiandaodidian;
    private ImageView qiandao,qiantui;
    static View mPopView = null; // 点击mark时弹出的气泡View
    private AMap aMap;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private AMapLocation aMapLocation1;
    private LatLng myLocation = null;
    private Marker centerMarker;
    private Handler handler = new Handler();
    private GeocodeSearch geocodeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_signin;
        super.onCreate(savedInstanceState);

        mContext = SigninActivity.this;
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map_view);
        //必须回调MapView的onCreate()方法
        mapView.onCreate(savedInstanceState);

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isInit = initData();
                if (isInit) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                        }
                    });
                }
                WeiboDialogUtils.closeDialog(mDialog);
            }
        }).start();

        String sing1 = AppUtils.sHA1(getApplicationContext());
        Log.i("dd",sing1);
    }

    public boolean initData() {
        menu_code = "signin";
        menu_name = "考勤";
        mContext = SigninActivity.this;

        //获取 考勤信息
        String date = DateUtil.getDay();
        pars = new ListParms(menu_code,"staffid:"+ AppUtils.user.get_zydnId()+",day:"+ date).getParms();
        Object res =  ActivityController.getData2ByPost(mContext,menu_code,"signinOnday", StringUtils.strTOJsonstr(pars));
        if (res != null && !res.toString().contains("(abcdef)")){
            if (!res.equals("")){
                main_data = (Map<String, Object>) res;
            }
        }else{
            return  false;
        }
        return true;
    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }

    public void createView() {

        if(aMap ==null){
            aMap = mapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        }

        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        location();

        main_form = (LinearLayout) findViewById(R.id.main_form);

        main_title.setText(menu_name);
        shangwuqiandaoshijian = (TextView_custom) findViewById(R.id.signinofftime);
        shangwuqiandaodidian = (TextView_custom) findViewById(R.id.offtimeaddress);

        xiawuqiandaodidian = (TextView_custom) findViewById(R.id.signinontime);
        xiawuqiandaoshijian = (TextView_custom) findViewById(R.id.ontimeaddress);

        //签到
        qiandao = (ImageView) findViewById(R.id.qiandao);
        qiandao.setOnClickListener(this);
        //签退
        qiantui = (ImageView) findViewById(R.id.qiantui);
        qiantui.setOnClickListener(this);
        reshDataUI();
    }

    public void reshDataUI() {
        //上午签到
        updateOneUI(main_data, "signinontime");
        updateOneUI(main_data, "ontimeaddress");
        //下午签退
        updateOneUI(main_data, "signinofftime");
        updateOneUI(main_data, "offtimeaddress");

        if (main_data.get("signinontime") != null && !main_data.get("signinontime").equals("")){
            qiandao.setEnabled(false);
            qiandao.setBackground(getResources().getDrawable(R.mipmap.qiandaob));
        }else{
            qiandao.setEnabled(true);
        }

        if (main_data.get("signinofftime") != null && !main_data.get("signinofftime").equals("")){
            qiantui.setEnabled(false);
            qiantui.setBackground(getResources().getDrawable(R.mipmap.qiantuib));
        }else{
            qiantui.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        Object res;
        String isOk = "";
        StringBuffer buffer = null;
        switch (v.getId()) {

            case R.id.qiandao:
                if (aMapLocation1 != null){
                    //获取定位信息
                    buffer =  new StringBuffer();
                    buffer.append(aMapLocation1.getAddress());
                }else{
                    //Toast.makeText(mContext,"定位失败，未开启定位权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                pars = new ListParms(menu_code,"staffid:"+AppUtils.user.get_zydnId()+",ip:"+AppUtils.app_ip+",address:"+buffer.toString()).getParms();
                final String finalbuffer = buffer.toString();

                mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"签到中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String isOk = ActivityController.executeForResult(mContext,menu_code,"signinonday1_click",StringUtils.strTOJsonstr(pars));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isOk != null &&!isOk.contains("(abcdef)")){
                                    Toast.makeText(mContext,"签到成功",Toast.LENGTH_SHORT).show();
                                    main_data.put("signinontime",DateUtil.getHourAndMin(DateUtil.createDate()));
                                    main_data.put("ontimeaddress",finalbuffer);
                                    reshDataUI();
                                }else{
                                    Toast.makeText(mContext,isOk.replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                }

                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                        });

                    }
                }).start();


                break;
            case R.id.qiantui:
                if (aMapLocation1 != null){
                    //获取定位信息
                    buffer =  new StringBuffer();
                    buffer.append(aMapLocation1.getAddress());
                }else{
                    //Toast.makeText(mContext,"定位失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                pars = new ListParms(menu_code,"staffid:"+AppUtils.user.get_zydnId()+",ip:"+AppUtils.app_ip+",address:"+buffer.toString()).getParms();

                final String finalbuffer1 = buffer.toString();
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"签退中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String isOk = ActivityController.executeForResult(mContext,menu_code,"signinoffday1_click",StringUtils.strTOJsonstr(pars));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isOk != null && !isOk.contains("(abcdef)")){
                                    main_data.put("signinofftime",DateUtil.getHourAndMin(DateUtil.createDate()));
                                    main_data.put("offtimeaddress",finalbuffer1.toString());
                                    reshDataUI();
                                }else{
                                    Toast.makeText(mContext,isOk.replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        WeiboDialogUtils.closeDialog(mDialog);
                    }
                }).start();

                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        // 释放资源
        super.onDestroy();
        mapView.onDestroy();
        if (mLocationClient != null){
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                aMapLocation1 = aMapLocation;

                LatLng myLocation = new LatLng(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude());
                fixedMarker();

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    /*//获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();*/
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                //Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    private void setMovingMarker() {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLonPoint point = new LatLonPoint(cameraPosition.target.latitude,
                cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    private void fixedMarker() {
        MarkerOptions centerMarkerOption = new MarkerOptions().position(
                myLocation)/*.icon(chooseDescripter)*/;
        centerMarker = aMap.addMarker(centerMarkerOption);
        centerMarker.setPositionByPixels(mapView.getWidth() / 2,
                mapView.getHeight() / 2);
        //获取定位信息
        StringBuffer buffer = new StringBuffer();
        buffer.append(aMapLocation1.getAddress());
        centerMarker.setTitle(buffer.toString());
        centerMarker.showInfoWindow();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraUpdate update = CameraUpdateFactory.zoomTo(17f);
                aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        aMap.setOnCameraChangeListener(SigninActivity.this);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }, 1000);
    }

}