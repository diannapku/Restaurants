package pku.sei.restaurants;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.yancloud.android.reflection.get.YanCloudGet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String test;
        String ip = "10.0.92.249";
        int maxPort = 1780;
        String appPakageName = "com.sankuai.meituan.takeoutnew";
        YanCloudGet api = YanCloudGet.fromPackageName(ip, maxPort, appPakageName);
        String result = api.get("comm", "getMyOrders", "{\"pageNum\": \"0\"}");
        System.out.println(result);
    }
    // This is a test!!!
}
