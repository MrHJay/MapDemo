package com.hejie.mapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.hejie.mapdemo.util.GPXUtils;

import java.io.IOException;
import java.util.List;

/**
 * https://github.com/MrHJay/MapDemo.git
 *
 */
public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    /**
     * 路径所有点
     */
    private List<LatLng> points;
    /**
     * 地图显示区域
     */
    private LatLngBounds latLngBounds;

    private static final String EXTRA_MAP_STATUS = "MapStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.baiduMap);
        if (mapView != null) {
            mBaiduMap = mapView.getMap();
            if (savedInstanceState != null) { // 读取地图状态
                MapStatus mapStatus = savedInstanceState.getParcelable(EXTRA_MAP_STATUS);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (points == null)
                        parseLine();

                    if (mBaiduMap == null)
                        return;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // 画路径
                            OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                            mBaiduMap.addOverlay(ooPolyline);

                            Log.d("set center", latLngBounds.getCenter().toString());

                            // 设置比例尺
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
                            // 设置中心点
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLngBounds.getCenter()));
                            Toast.makeText(MainActivity.this, "load finished !", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 解析路线数据，设置轮廓
     *
     * @throws IOException
     */
    private void parseLine() throws IOException {
        points = GPXUtils.parseLoc(getResources().getAssets().open("demo.gpx"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        latLngBounds = builder.build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_MAP_STATUS, mBaiduMap.getMapStatus());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
