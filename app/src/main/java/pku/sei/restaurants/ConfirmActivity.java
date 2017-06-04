package pku.sei.restaurants;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_xiadan);
        TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
        restaurant_name.setText(DataBase.result_entry.name);
        if (DataBase.result_entry.hasBaidu == true) {
            TextView dish_name = (TextView) findViewById(R.id.dish);
            Dish dish = DataBase.result_entry.baidu.dishes.get(0);
            dish_name.setText(dish.name + "￥" + Double.toString(dish.current_price));
            Speech.mNlsClient_fh.PostTtsRequest("已为您在百度外卖 下单 一份 " + dish.name + "。祝您用餐愉快！");
        } else {
            TextView dish_name = (TextView) findViewById(R.id.dish);
            dish_name.setText(DataBase.searchString);
            Speech.mNlsClient_fh.PostTtsRequest("已为您在百度外卖 下单 一份 " + DataBase.searchString + "。祝您用餐愉快！");
        }




    }
}
