package pku.sei.restaurants;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BDLocation location = null;
    private List<Entry> entries = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 文字版UI
        setContentView(R.layout.activity_main);

        // 语音版UI
        //setContentView();


        // 获取地址

        // 文字版UI
        TextView address_text = (TextView) findViewById(R.id.address_box);
        address_text.setText("waiting");

        // 语音版UI


        // 获取地址开始
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        //mLocationClient.stop();
        // 获取地址结束，地址在location里面。具体获取时间未知，所以使用Location之前需要判断是不是null。


        // 搜索栏
        // 文字版UI
        Button search = (Button)findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editSearch = (EditText)findViewById(R.id.search_box);
                if (editSearch.length() > 0)
                    setListView(editSearch.getText().toString());
            }
        });

        // 语音版UI？


    }

    Handler h =  new Handler();
    private void setListView(final String search_str) {

        String baidu_str = readStream(getResources().openRawResource(R.raw.baidu_result));
        String eleme_str = readStream(getResources().openRawResource(R.raw.eleme_result));

        Log.v("zsy","*********");
        Runnable r = new Runnable(){
            @Override
            public void run() {
                Model model = new Model();
                entries = model.getEntries(search_str, location);
                h.post(new Runnable(){
                    @Override
                    public void run() {
                        EntryAdapter entryAdapter = new EntryAdapter(MainActivity.this, R.layout.info_card, entries);

                        // 文字版UI
                        ListView listView = (ListView) findViewById(R.id.ListViewId);
                        listView.setAdapter(entryAdapter);


                    }
                });
            }
        };
        new Thread(r).start();
    }


    // 初始化地址查询方式
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        //option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        //int span=1000;
        //option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        //option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        //option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    // 地址监听器
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

        @Override
        public void onReceiveLocation(BDLocation tlocation) {

            location = tlocation;
            TextView address_text = (TextView) findViewById(R.id.address_box);
            address_text.setText(location.getAddrStr());

        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


}
