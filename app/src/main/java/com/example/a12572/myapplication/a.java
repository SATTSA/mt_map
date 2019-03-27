package com.example.a12572.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.MapStatusUpdate;

public class a extends AppCompatActivity {
    public LocationClient mLocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private  boolean isFirstLocate=true;
    private Button myPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
       mLocationClient.registerLocationListener(new MyLocationListener());//注册监听器

        SDKInitializer.initialize(getApplicationContext());
       setContentView(R.layout.activity_main);
    mapView=(MapView) findViewById(R.id.bmapView);
      mapView.removeViewAt(1);
       baiduMap=mapView.getMap();
      baiduMap.setMyLocationEnabled(true);//kaiqiwodeweizi
      positionText = (TextView)findViewById(R.id.textDDH);

        myPosition = (Button) findViewById(R.id.my_positionDDH);
        List<String>  permissionList = new ArrayList<>();

            requestLocation();

    }


    private void navigateTo(BDLocation location){
        if (isFirstLocate) {
            //-----------------------注意-----------------------------//
            //这段代码写了过后不能正常移动到我的位置，原因不明/
            //    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //   MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            //  baiduMap.animateMapStatus(update);
            //   update = MapStatusUpdateFactory.zoomTo(16f);
            //  baiduMap.animateMapStatus(update);
            //  isFirstLocate = false;
            LatLng  ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(12.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            isFirstLocate = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData myLocationData = builder.build();
        baiduMap.setMyLocationData(myLocationData);

    }

    private void requestLocation() {

        initLocation();
        mLocationClient.start();

    }
    private  void initLocation(){
        LocationClientOption option=new  LocationClientOption();

        option.setScanSpan(5000);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int resulet : grantResults) {
                        if (resulet != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                if (isFirstLocate) {
                    navigateTo(location);
                }

            }
            myPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(location.getLatitude(),location.getLongitude())).zoom(20).build();
                    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    //改变地图状态
                    mMapStatusUpdate=MapStatusUpdateFactory.zoomTo(20);
                    baiduMap.animateMapStatus(mMapStatusUpdate);


                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    StringBuilder currentPosition = new StringBuilder();

                    currentPosition.append("经度:").append(location.getLatitude()).append("\n");
                    currentPosition.append("纬度:").append(location.getLongitude()).append("\n");
                    currentPosition.append("国家:").append(location.getCountry()).append("\n");
                    currentPosition.append("省:").append(location.getProvince()).append("\n");
                    currentPosition.append("市:").append(location.getCity()).append("\n");
                    currentPosition.append("区:").append(location.getDistrict()).append("\n");
                    currentPosition.append("街道:").append(location.getStreet()).append("\n");
                    currentPosition.append("定位方式:");
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {
                        currentPosition.append("GPS").append("\n");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        currentPosition.append("网络").append("\n");
                    } else {
                        currentPosition.append("获取不到定位方式").append("\n");
                    }

                    positionText.setText(currentPosition);






                }

            });



        }


    }

}
