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
