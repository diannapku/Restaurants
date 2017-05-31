package pku.sei.restaurants;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.alibaba.idst.nls.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.internal.protocol.NlsRequestProto;

/**
 * Created by xiaohan on 2017/5/25.
 */

public class Speech {
    //语音识别
    static private NlsRequest mNlsRequest;
    static private Context context;

    static public void initalSpeech(Context context) {
        Speech.context = context;
        NlsRequestProto proto = new NlsRequestProto(context);
        proto.setApp_user_id("xxx"); //设置在应用中的用户名，可选
        mNlsRequest = new NlsRequest(proto);

        String appkey = "nls-service";     //请设置简介页面的Appkey
        mNlsRequest.setApp_key(appkey);    //appkey列表中获取
        mNlsRequest.setAsr_sc("opu");      //设置语音格式
        /*热词参数*/
        mNlsRequest.setAsrUserId("userid");
        mNlsRequest.setAsrVocabularyId("vocabid");
        mNlsRequest.authorize("LTAIdi22P8quaCEF", "Zau1ZNsC4YyEKhBAzI7dot1STrHpIe"); //请替换为用户申请到的Access Key ID和Access Key Secret
        /*热词参数*/
        NlsClient.openLog(true);
        NlsClient.configure(context); //全局配置


        //语音合成
        mNlsRequest_fh = new NlsRequest(proto);
        //String appkey = "nls-service";     //请设置简介页面的Appkey
        mNlsRequest_fh.setApp_key(appkey);      //appkey请从 简介页面的appkey列表中获取
        mNlsRequest_fh.initTts();               //初始化tts请求
        mNlsClient_fh = NlsClient.newInstance(context, mRecognizeListener_fh, null ,mNlsRequest_fh);//实例化NlsClient
        mNlsRequest_fh.setTtsEncodeType("pcm"); //返回语音数据格式，支持pcm,wav.alaw
        mNlsRequest_fh.setTtsVolume(50);        //音量大小默认50，阈值0-100
        mNlsRequest_fh.setTtsSpeechRate(0);     //语速，阈值-500~500
        mNlsRequest_fh.authorize("LTAIdi22P8quaCEF", "Zau1ZNsC4YyEKhBAzI7dot1STrHpIe");       //请替换为用户申请到的数加认证key和密钥
        // 语音初始化结束
    }

    static public NlsClient mainNlsClient;
    static public void setMainNlsClient(NlsListener mainRecognizeListener) {
        mainNlsClient = NlsClient.newInstance(context, mainRecognizeListener, mStageListener, mNlsRequest);  //实例化NlsClient
        mainNlsClient.setMaxRecordTime(60000);       //设置最长语音
        mainNlsClient.setMaxStallTime(1000);         //设置最短语音
        mainNlsClient.setMinRecordTime(500);         //设置最大录音中断时间
        mainNlsClient.setRecordAutoStop(false);      //设置VAD
        mainNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长
    }

    static public NlsClient changeNlsClient;
    static public void setChangeNlsClient(NlsListener changeRecognizeListener) {
        changeNlsClient = NlsClient.newInstance(context, changeRecognizeListener, mStageListener, mNlsRequest);  //实例化NlsClient
        changeNlsClient.setMaxRecordTime(60000);       //设置最长语音
        changeNlsClient.setMaxStallTime(1000);         //设置最短语音
        changeNlsClient.setMinRecordTime(500);         //设置最大录音中断时间
        changeNlsClient.setRecordAutoStop(false);      //设置VAD
        changeNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长
    }




//    static private NlsListener mRecognizeListener = new NlsListener() {
//        @Override
//        public void onRecognizingResult(int status, NlsListener.RecognizedResult result) {
//            switch (status) {
//                case NlsClient.ErrorCode.SUCCESS:
//                    entries = null;
//                    Log.i("asr", "[demo]  callback onRecognizResult " + result.asr_out);
//                    try {
//                        org.json.JSONObject jsonObject = new org.json.JSONObject(result.asr_out);
//                        recognizedString = jsonObject.getString("result");
//                        //editSearch.setText(recognizedString);
//                    } catch (org.json.JSONException e) {
//                        Log.v("hyq:", "json error " + e.getMessage());
//                    }
//                    Log.v("hyq:", result.asr_out);
//                    //mFullEdit.setText(result.asr_out);
//
//                    if (recognizedString != null && finishSearch == false) {
//                        Log.v("hyq", "search:" + recognizedString);
//
//                        Runnable r = new Runnable(){
//                            @Override
//                            public void run() {
//                                Model model = new Model();
//                                entries = model.getEntries(recognizedString, location);
//                                searchString = recognizedString;
//                            }
//                        };
//                        new Thread(r).start();
//
//                        //setContentView(R.layout.voice_result);
//
//                        ImageView voice_btn = (ImageView) findViewById(R.id.voice_btn);
////                        ImageButton voice_btn = (ImageButton) findViewById(R.id.voice_btn);
//                        voice_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public  void onClick(View v) {
//                                Log.v("hyq:", "正在录音，请稍候！");
//
//                                mNlsClient.start();
//                                long time = System.currentTimeMillis();
//                                while(true) {
//                                    if(System.currentTimeMillis() - time > 3000) break;
//                                }
//                                mNlsClient.stop();
//                                Log.v("hyq:", "识别结束");
//                            }
//                        });
//
//                        while (entries == null) { }
//
//                        //  初始化一个准备跳转的Intent
//                        //Intent intent = new Intent(context, ChangeActivity.class);
//
//                        // 往Intent中传入Teacher相关的数据，供TeacherDetailActivity使用
//                        //intent.putExtra("mnlsclient", mNlsClient);
//                        //intent.putExtra("teacher_desc", teacher.getDesc());
//
//                        //  初始化一个准备跳转到TeacherDetailActivity的Intent
//                        //context.startActivity(intent);
//
//                        TextView num = (TextView) findViewById(R.id.number);
//                        num.setText(String.valueOf(entries.size()));
//
//                        Entry entry = recommender.firstRecommendation(entries);
//                        temp = entry;
//                        //Entry entry = entries.get(0);
//                        mNlsClient_fh.PostTtsRequest("为您推荐" + entry.dimension  + "的外卖商家。" + entry.name);
//                        TextView dimension = (TextView) findViewById(R.id.dimension);
//                        dimension.setText(entry.dimension);
//                        TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
//                        restaurant_name.setText(entry.name);
//                        finishSearch = true;
//
//                    } else if (recognizedString != null && finishSearch == true) {
//                        if (recognizedString.contains("换")) {
//                            Entry entry = recommender.switchRecommendation();
//                            temp = entry;
//                            if (entry == null) {
//                                entries = null;
//                                finishSearch = false;
//                                setContentView(R.layout.voice_main);
//                                ImageView voice_btn = (ImageView) findViewById(R.id.voice_btn);
////                                ImageButton voice_btn = (ImageButton) findViewById(R.id.voice_btn);
//                                voice_btn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public  void onClick(View v) {
//                                        Log.v("hyq:", "正在录音，请稍候！");
//
//                                        mNlsClient.start();
//                                        long time = System.currentTimeMillis();
//                                        while(true) {
//                                            if(System.currentTimeMillis() - time > 3000) break;
//                                        }
//                                        mNlsClient.stop();
//                                        Log.v("hyq:", "识别结束");
//                                    }
//                                });
//                            } else {
//                                mNlsClient_fh.PostTtsRequest("为您推荐" + entry.dimension + "的外卖商家。" + entry.name);
//                                TextView dimension = (TextView) findViewById(R.id.dimension);
//                                dimension.setText(entry.dimension);
//                                TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
//                                restaurant_name.setText(entry.name);
//                            }
//                        } else if (recognizedString.contains("确认") || recognizedString.contains("点") || recognizedString.contains("不错")) {
//                            setContentView(R.layout.voice_xiadan);
//                            TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
//                            restaurant_name.setText(temp.name);
////                            TextView dish = (TextView) findViewById(R.id.dish);
////                            dish.setText(searchString);
//                            mNlsClient_fh.PostTtsRequest("已为您在美团外卖 下单 一份 "+searchString+"。祝您用餐愉快！");
//                        }
//                    }
//
//
//                    break;
//                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
//                    //PublicAsrActivity.this, "recognizer error", Toast.LENGTH_LONG).show();
//                    Log.v("hyq:", "recognizer error");
//                    break;
//                case NlsClient.ErrorCode.RECORDING_ERROR:
//                    //Toast.makeText(PublicAsrActivity.this,"recording error",Toast.LENGTH_LONG).show();
//                    Log.v("hyq:", "recording error");
//                    break;
//                case NlsClient.ErrorCode.NOTHING:
//                    //Toast.makeText(PublicAsrActivity.this,"nothing",Toast.LENGTH_LONG).show();
//                    Log.v("hyq:", "nothing");
//                    break;
//            }
//        }
//    } ;

    static private StageListener mStageListener = new StageListener() {
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

    static public NlsClient mNlsClient_fh;
    static private NlsRequest mNlsRequest_fh;
    static private int iMinBufSize = AudioTrack.getMinBufferSize(8000,
            AudioFormat.CHANNEL_CONFIGURATION_STEREO,
            AudioFormat.ENCODING_PCM_16BIT);

    static private AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
            AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize, AudioTrack.MODE_STREAM) ; //使用audioTrack播放返回的pcm数据

    static private NlsListener mRecognizeListener_fh = new NlsListener() {
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
                    Log.v("hyq:", "CONNECT ERROR");
                    break;
            }
        }
    };
}
