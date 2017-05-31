package pku.sei.restaurants;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;


public class ChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_result);

        TextView num = (TextView) findViewById(R.id.number);
        num.setText(String.valueOf(DataBase.entries.size()));

        Entry entry = recommender.firstRecommendation(this, DataBase.entries);
        Speech.mNlsClient_fh.PostTtsRequest("为您推荐" + entry.dimension + "的外卖商家。" + entry.name);
        TextView dimension = (TextView) findViewById(R.id.dimension);
        dimension.setText(entry.dimension);
        TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
        restaurant_name.setText(entry.name);

        Speech.setChangeNlsClient(changeRecognizeListener);

        ImageView voice_btn = (ImageView) findViewById(R.id.voice_btn);
        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("hyq:", "正在录音，请稍候！");

                recognizedString = null;
                Speech.changeNlsClient.start();
                long time = System.currentTimeMillis();
                while (true) {
                    if (System.currentTimeMillis() - time > 3000) break;
                }
                Speech.changeNlsClient.stop();
                Log.v("hyq:", "识别结束");
            }
        });

    }

    private String recognizedString = null;
    private Recommender recommender = new Recommender();
    Entry entry;

    public NlsListener changeRecognizeListener = new NlsListener() {
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
                        if (recognizedString.contains("换")) {
                            entry = recommender.switchRecommendation();
                            if (entry == null) {
                                finish();
                            } else {
                                Speech.mNlsClient_fh.PostTtsRequest("为您推荐" + entry.dimension + "的外卖商家。" + entry.name);
                                TextView dimension = (TextView) findViewById(R.id.dimension);
                                dimension.setText(entry.dimension);
                                TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
                                restaurant_name.setText(entry.name);
                            }
                        } else if (recognizedString.contains("确认") || recognizedString.contains("点") || recognizedString.contains("不错")) {
                            recommender.Update(entry.dimension);
                            setContentView(R.layout.voice_xiadan);
                            TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
                            restaurant_name.setText(DataBase.result_entry.name);
                            Speech.mNlsClient_fh.PostTtsRequest("已为您在美团外卖 下单 一份 " + DataBase.searchString + "。祝您用餐愉快！");
                        } else {
                            Speech.mNlsClient_fh.PostTtsRequest("我没有听懂您的意思。");
                        }
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

}
