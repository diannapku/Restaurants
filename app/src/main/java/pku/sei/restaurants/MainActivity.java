package pku.sei.restaurants;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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


        /*
          处理搜索栏
         */


        /*
          处理ListView
         */
        //初始化一个Adapter
        Model model = new Model();
        ArrayAdapter<String> entryAdapter = new ArrayAdapter<String>(this, R.layout.ListViewName, model.getEntries());
        //通过ID获取listView
        ListView listView = (ListView) findViewById(R.id.ListViewId);
        //设置listView的Adapter
        listView.setAdapter(entryAdapter);

    }

}
