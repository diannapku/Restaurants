package pku.sei.restaurants;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.alibaba.idst.nls.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.internal.protocol.NlsRequestProto;
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
    private EditText editSearch;
    private Button search;
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
        context = getApplicationContext();
        mNlsRequest = initNlsRequest();
        String appkey = "nls-service"; //请设置简介页面的Appkey
        mNlsRequest.setApp_key(appkey);    //appkey列表中获取
        mNlsRequest.setAsr_sc("opu");      //设置语音格式
        /*热词参数*/
        mNlsRequest.setAsrUserId("userid");
        mNlsRequest.setAsrVocabularyId("vocabid");
        /*热词参数*/
        NlsClient.openLog(true);
        NlsClient.configure(getApplicationContext()); //全局配置
        mNlsClient = NlsClient.newInstance(this, mRecognizeListener, mStageListener,mNlsRequest);                          //实例化NlsClient
        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长

        //语音合成
        mNlsRequest_fh = initNlsRequest();
        //String appkey = "nls-service";     //请设置简介页面的Appkey
        mNlsRequest_fh.setApp_key(appkey);    //appkey请从 简介页面的appkey列表中获取
        mNlsRequest_fh.initTts();               //初始化tts请求

        mNlsClient_fh = NlsClient.newInstance(this, mRecognizeListener_fh, null ,mNlsRequest_fh);//实例化NlsClient


        mNlsRequest_fh.setTtsEncodeType("pcm"); //返回语音数据格式，支持pcm,wav.alaw
        mNlsRequest_fh.setTtsVolume(50);   //音量大小默认50，阈值0-100
        mNlsRequest_fh.setTtsSpeechRate(0);//语速，阈值-500~500
        mNlsRequest_fh.authorize("LTAIdi22P8quaCEF", "Zau1ZNsC4YyEKhBAzI7dot1STrHpIe");       //请替换为用户申请到的数加认证key和密钥

        // 获取地址开始
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
        //mLocationClient.stop();
        // 获取地址结束，地址在location里面。具体获取时间未知，所以使用Location之前需要判断是不是null。


        // 搜索栏
        // 文字版UI
        search = (Button)findViewById(R.id.search_btn);
        editSearch = (EditText)findViewById(R.id.search_box);
        editSearch.setInputType(InputType.TYPE_NULL);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText editSearch = (EditText)findViewById(R.id.search_box);
                if (editSearch.length() > 0)
                    setListView(editSearch.getText().toString());
            }
        });

        // 语音版UI
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecognizing = true;
                Log.v("hyq:", "正在录音，请稍候！");
                mNlsRequest.authorize("LTAIdi22P8quaCEF", "Zau1ZNsC4YyEKhBAzI7dot1STrHpIe"); //请替换为用户申请到的Access Key ID和Access Key Secret
                mNlsClient.start();
                Log.v("hyq:","录音中。。。");

                long time = System.currentTimeMillis();
                while(true) {
                    if(System.currentTimeMillis() - time > 3000)
                        break;
                }

                isRecognizing = false;
                Log.v("hyq:", "");
                mNlsClient.stop();
                Log.v("hyq:", "识别 结束");
            }
        });


    }

    //语音识别
    private boolean isRecognizing = false;
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    private Context context;
    private String recognizedString;


    private NlsRequest initNlsRequest(){
        NlsRequestProto proto = new NlsRequestProto(context);
        proto.setApp_user_id("xxx"); //设置在应用中的用户名，可选
        return new NlsRequest(proto);
    }

    private NlsListener mRecognizeListener = new NlsListener() {
        @Override
        public void onRecognizingResult(int status, NlsListener.RecognizedResult result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    Log.i("asr", "[demo]  callback onRecognizResult " + result.asr_out);
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(result.asr_out);
                        recognizedString = jsonObject.getString("result");
                        editSearch.setText(recognizedString);
                    } catch (org.json.JSONException e) {
                        Log.v("hyq:", "json error " + e.getMessage());
                    }
                    Log.v("hyq:", result.asr_out);
                    //mFullEdit.setText(result.asr_out);


                    editSearch.setSelection(editSearch.getText().length());
                    if (editSearch.length() > 0)
                        setListView(editSearch.getText().toString());

                    mNlsClient_fh.PostTtsRequest("为您推荐"); //用户输入文本
                    audioTrack.play();

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
                    Log.v("hyq:", "nothing");
                    break;
            }
            isRecognizing = false;
        }
    } ;

    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }
        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }
        @Override
        public void onStartRecording(NlsClient recognizer) {
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }
        @Override
        public void onStopRecording(NlsClient recognizer) {
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }
        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }
    };


    //语音识别结束

    //语音合成

    private NlsClient mNlsClient_fh;
    private NlsRequest mNlsRequest_fh;

    private NlsListener mRecognizeListener_fh = new NlsListener() {
        @Override
        public void onTtsResult(int status, byte[] ttsResult){
            switch (status) {
                case NlsClient.ErrorCode.TTS_BEGIN :
                    audioTrack.play();
                    Log.v("hyq:", "tts begin");
                    audioTrack.write(ttsResult, 0, ttsResult.length);
                    break;
                case NlsClient.ErrorCode.TTS_TRANSFERRING :
                    Log.v("hyq:","tts transferring"+ttsResult.length);
                    audioTrack.write(ttsResult, 0, ttsResult.length);
                    break;
                case NlsClient.ErrorCode.TTS_OVER :
                    audioTrack.stop();
                    Log.v("hyq:","tts over");
                    break;
                case NlsClient.ErrorCode.CONNECT_ERROR :
                    //Toast.makeText(PublicTtsActivity.this, "CONNECT ERROR", Toast.LENGTH_LONG).show();
                    Log.v("hyq:", "CONNECT ERROR");
                    break;
            }
        }
    } ;
    @Override
    protected void onDestroy() {
        audioTrack.release();
        super.onDestroy();
    }

    int iMinBufSize = AudioTrack.getMinBufferSize(8000,
            AudioFormat.CHANNEL_CONFIGURATION_STEREO,
            AudioFormat.ENCODING_PCM_16BIT);
    AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
            AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize, AudioTrack.MODE_STREAM) ; //使用audioTrack播放返回的pcm数据

    //语音合成结束

    Handler h =  new Handler();
    private void setListView(final String search_str) {

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
