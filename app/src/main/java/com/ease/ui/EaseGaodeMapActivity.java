/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ease.ui;


import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
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
import com.baidu.mapapi.model.inner.GeoPoint;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.wticn.wyb.wtiapp.R;

import java.sql.Date;

public class EaseGaodeMapActivity extends EaseBaseActivity  implements LocationSource,AMapLocationListener,AMap.OnCameraChangeListener {

	private MapView mapView;
	private Context instance;
	private AMap aMap;
	//声明AMapLocationClient类对象，定位发起端
	private AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象，定位参数
	public AMapLocationClientOption mLocationOption = null;
	//声明mListener对象，定位监听器
	private LocationSource.OnLocationChangedListener mListener = null;
	//标识，用于判断是否只显示一次定位信息和用户重新定位
	private boolean isFirstLoc = true;
	private AMapLocation aMapLocation1;
	private LatLng myLocation = null;
	private Marker centerMarker;
	private Handler handler = new Handler();
	private GeocodeSearch geocodeSearch;
	private StringBuffer buffer;
	private Button btn_location_send;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ease_activity_gaodemap);
		//获取地图控件引用
		mapView = (MapView) findViewById(R.id.map_view);
		//必须回调MapView的onCreate()方法
		mapView.onCreate(savedInstanceState);

		boolean isInit = initData();
		if (isInit) {
			//createView();
		}
	}


	public boolean initData() {

		instance = this;

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
		btn_location_send = (Button) findViewById(R.id.btn_location_send);

		//显示气泡 和定位
		new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = getIntent();
				final double latitude = intent.getDoubleExtra("latitude", 0);

				if (latitude == 0) {
					location(true);
				} else {
					final double longtitude = intent.getDoubleExtra("longitude", 0);
					final String address = intent.getStringExtra("address");
					GeoPoint p = new GeoPoint(latitude, longtitude);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
							aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude,longtitude)));
							//显示气泡
							myLocation = new LatLng(latitude,longtitude);
							createMaket(myLocation,address);
						}
					});

				}
			}
		}).start();

		return true;
	}

	private void location(boolean state) {
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
		if (state){
			//启动定位
			mLocationClient.startLocation();
		}

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

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null) {
			if (aMapLocation.getErrorCode() == 0) {

				btn_location_send.setEnabled(true);

				//定位成功回调信息，设置相关消息
				aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
				aMapLocation.getLatitude();//获取纬度
				aMapLocation.getLongitude();//获取经度
				aMapLocation.getAccuracy();//获取精度信息
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
		buffer.append(aMapLocation1.getCountry() + ""
				+ aMapLocation1.getProvince() + ""
				+ aMapLocation1.getCity() + ""
				+ aMapLocation1.getProvince() + ""
				+ aMapLocation1.getDistrict() + ""
				+ aMapLocation1.getStreet() + ""
				+ aMapLocation1.getStreetNum());
		centerMarker.setTitle(buffer.toString());
		centerMarker.showInfoWindow();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				CameraUpdate update = CameraUpdateFactory.zoomTo(17f);
				aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
					@Override
					public void onFinish() {
						aMap.setOnCameraChangeListener(EaseGaodeMapActivity.this);
					}

					@Override
					public void onCancel() {
					}
				});
			}
		}, 1000);
	}


	public void back(View v) {
		finish();
	}

	public void sendLocation(View view) {
		Intent intent = this.getIntent();
		buffer =  new StringBuffer();
		buffer.append(aMapLocation1.getCountry() + ""
				+ aMapLocation1.getProvince() + ""
				+ aMapLocation1.getCity() + ""
				+ aMapLocation1.getProvince() + ""
				+ aMapLocation1.getDistrict() + ""
				+ aMapLocation1.getStreet() + ""
				+ aMapLocation1.getStreetNum());

		intent.putExtra("latitude", aMapLocation1.getLatitude());
		intent.putExtra("longitude", aMapLocation1.getLongitude());
		intent.putExtra("address", buffer.toString());
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	public void createMaket(LatLng myLocation,String address){
		MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation)/*.icon(chooseDescripter)*/;
		centerMarker = aMap.addMarker(centerMarkerOption);
		centerMarker.setPositionByPixels(AppUtils.getScreenWidth(getApplicationContext()) / 2,
				AppUtils.getScreenHeight(getApplicationContext()) / 2);
		centerMarker.setTitle(address);
		centerMarker.showInfoWindow();
	}

}
