package pku.sei.restaurants;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yancloud.android.reflection.get.YanCloudGet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
          如果报错且影响其他部分，请注释掉
         */
        Location location = getLocation();
        String address;
        if (location != null) {
            address = getAddress(location);
        } else {
            Log.d("sxh", "location为空");
            Toast.makeText(this, "location为空", Toast.LENGTH_SHORT).show();
            address = "wrongAddress";
        }
        TextView address_text = (TextView) findViewById(R.id.address_box);
        address_text.setText(address);

        /*
          处理搜索栏
         */
        Button search = (Button) findViewById(R.id.search_btn_back);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editSearch = (EditText) findViewById(R.id.search_box);
                setListView(editSearch.getText().toString());
            }
        });

    }

    private void setListView(String search_str) {
        /*
          处理ListView
         */
        String baidu_str = readStream(getResources().openRawResource(R.raw.baidu_result));
        //初始化一个Adapter
        Model model = new Model();
        Log.v("zsy","*********");
        EntryAdapter entryAdapter = new EntryAdapter(this, R.layout.info_card, model.getEntries(baidu_str,"","",search_str));
        //通过ID获取listView
        ListView listView = (ListView) findViewById(R.id.ListViewId);
        //设置listView的Adapter
        listView.setAdapter(entryAdapter);
    }

    private Location getLocation() {
        String locationProvider;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        //if (providers.contains(LocationManager.GPS_PROVIDER)) {
        //    //如果是GPS
        //    locationProvider = LocationManager.GPS_PROVIDER;
        //} else
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Log.d("sxh", "获取位置提供器失败");
            Toast.makeText(this, "获取位置提供器失败", Toast.LENGTH_SHORT).show();
            return null;
        }

        Location location;
        try {
            location = locationManager.getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {
            Log.d("sxh", "获取经纬度失败");
            Toast.makeText(this, "获取经纬度失败", Toast.LENGTH_SHORT).show();
            return null;
        }
        return location;
    }

    private String getAddress(Location location) {
        Geocoder geocoder = new Geocoder(this);
        if (!Geocoder.isPresent()) {
            Log.d("sxh", "geocoder不存在");
            Toast.makeText(this, "geocoder不存在", Toast.LENGTH_SHORT).show();
            return "wrongAddress";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //根据经纬度获取地理位置信息
            //double latitude，double longitude，int maxResults
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                stringBuilder.append(address.getLocality()).append("_");//市
                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
                stringBuilder.append(address.getThoroughfare()).append("_");//道路
                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
            } else {
                Log.d("sxh", "找不到此地址");
                Toast.makeText(this, "找不到此地址", Toast.LENGTH_LONG).show();
                return "wrongAdress";
            }

        } catch (IOException e) {
            Log.d("sxh", "获取地址失败");
            Toast.makeText(this, "获取地址失败", Toast.LENGTH_LONG).show();
            return "wrongAdreess";
        }
        return stringBuilder.toString();
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
