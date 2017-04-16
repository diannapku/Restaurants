package pku.sei.restaurants;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yancloud.android.reflection.get.YanCloudGet;

import java.io.IOException;
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
        Location location = getLocation();


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


    }

    private Location getLocation() {
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
        }
        Location location;

        try {
            location = locationManager.getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            location = null;
        }
        return location;
    }

    private String getAddress(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        boolean flag = geocoder.isPresent();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //根据经纬度获取地理位置信息
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                stringBuilder.append(address.getCountryName()).append("_");//国家
                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
                stringBuilder.append(address.getLocality()).append("_");//市
                stringBuilder.append(address.getPostalCode()).append("_");
                stringBuilder.append(address.getCountryCode()).append("_");//国家编码
                stringBuilder.append(address.getAdminArea()).append("_");//省份
                stringBuilder.append(address.getSubAdminArea()).append("_");
                stringBuilder.append(address.getThoroughfare()).append("_");//道路
                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
                stringBuilder.append(address.getLatitude()).append("_");//经度
                stringBuilder.append(address.getLongitude());//维度
                System.out.println(stringBuilder.toString());
            }
        } catch (IOException e) {
            Toast.makeText(this, "报错", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return stringBuilder.toString();

    }

}
