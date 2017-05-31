package pku.sei.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private String recognizedString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 语音版UI
        setContentView(R.layout.voice_main);

        // 获取地址开始
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        //mLocationClient.stop();
        // 获取地址结束，地址在location里面。具体获取时间未知，所以使用Location之前需要判断是不是null。


        Runnable r = new Runnable(){
            @Override
            public void run() {


            }
        };
        new Thread(r).start();




        // 语音初始化
        //initalSpeech();

        Speech.initalSpeech(getApplicationContext());
        Speech.setMainNlsClient(mainRecognizeListener);
        Speech.mNlsClient_fh.PostTtsRequest("您要吃点什么嘛？");

        ImageView voice_btn = (ImageView) findViewById(R.id.voice_btn);
        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                Log.v("hyq:", "正在录音，请稍候！");
                DataBase.entries = null;
                recognizedString = null;
                DataBase.searchString = null;
                Speech.mainNlsClient.start();
                long time = System.currentTimeMillis();
                while(true) {
                    if(System.currentTimeMillis() - time > 3000) break;
                }
                Speech.mainNlsClient.stop();
                Log.v("hyq:", "识别结束");
            }
        });
    }

    private NlsListener mainRecognizeListener = new NlsListener() {
        @Override
        public void onRecognizingResult(int status, NlsListener.RecognizedResult result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    Log.i("asr", "[demo]  callback onRecognizResult " + result.asr_out);
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(result.asr_out);
                        recognizedString = jsonObject.getString("result");
                    } catch (org.json.JSONException e) {
                        Log.v("hyq:", "json error " + e.getMessage());
                    }
                    Log.v("hyq:", result.asr_out);

                    if (recognizedString != null) {
                        Log.v("hyq", "search:" + recognizedString);
                        Runnable r = new Runnable(){
                            @Override
                            public void run() {
                                Model model = new Model();
                                DataBase.entries = model.getEntries(recognizedString, DataBase.location);
                            }
                        };
                        new Thread(r).start();
                        DataBase.searchString = recognizedString;
                        while(DataBase.entries == null);
                        Intent intent =new Intent(MainActivity.this, ChangeActivity.class);
                        startActivity(intent);
                    }

                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    //PublicAsrActivity.this, "recognizer error", Toast.LENGTH_LONG).show();
                    Log.v("hyq:", "recognizer error");
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    //Toast.makeText(PublicAsrActivity.this,"recording error",Toast.LENGTH_LONG).show();
                    Log.v("hyq:", "recording error");
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    //Toast.makeText(PublicAsrActivity.this,"nothing",Toast.LENGTH_LONG).show();
                    Speech.mNlsClient_fh.PostTtsRequest("我没有听清楚。");
                    Log.v("hyq:", "nothing");
                    break;
            }
        }

    };

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

            DataBase.location = tlocation;
            TextView address_text = (TextView) findViewById(R.id.address_box);
            address_text.setText(DataBase.location.getAddrStr());

        }
    }


}
