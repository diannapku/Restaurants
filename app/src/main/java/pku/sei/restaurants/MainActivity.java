package pku.sei.restaurants;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yancloud.android.reflection.get.YanCloudGet;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         activity_main 里面有:
         地址栏
         搜索栏
         一个ListView
          */
        setContentView(R.layout.activity_main);

        /*
          处理地址栏
         */
        String locationProvider = "";
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            //不为空,显示地理位置经纬度
            showLocation(location);
        }



        /*
          处理搜索栏
         */


        /*
          处理ListView
         */
        //初始化一个Adapter
        Model model = new Model();

        EntryAdapter entryAdapter = new EntryAdapter(this, R.layout.info_card, model.getEntries());

        //通过ID获取listView
        ListView listView = (ListView) findViewById(R.id.ListViewId);
        //设置listView的Adapter
        listView.setAdapter(entryAdapter);


        //String test;
//        String ip = "10.0.92.249";
//        int maxPort = 1780;
//        String appPakageName = "com.sankuai.meituan.takeoutnew";
//        YanCloudGet api = YanCloudGet.fromPackageName(ip, maxPort, appPakageName);
//        String result = api.get("comm", "getMyOrders", "{\"pageNum\": \"0\"}");
//        System.out.println(result);

    }

}
