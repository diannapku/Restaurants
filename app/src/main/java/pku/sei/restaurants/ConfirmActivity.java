package pku.sei.restaurants;

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
            dish_name.setText(dish.name);
            TextView price = (TextView) findViewById(R.id.price);
            price.setText(Double.toString(dish.current_price + Double.valueOf(DataBase.result_entry.baidu.deliveryPrice)));
            TextView time = (TextView) findViewById(R.id.time);
            time.setText(DataBase.result_entry.baidu.avgDeliveryTime);
            TextView address = (TextView) findViewById(R.id.address);
            address.setText(AppConsts.userAddress);
            Speech.mNlsClient_fh.PostTtsRequest("已为您在百度外卖 下单 一份 " + dish.name + "。祝您用餐愉快！");
            final String confirm_str = "{\"userPhone\":\""+AppConsts.userPhone+"\",\"userName\":\""+AppConsts.userName+"\",\"userAddress\":\""+AppConsts.userAddress+"\",\"shopid\":\""+DataBase.result_entry.baidu.shopId+"\",\"content\":\"{\\\"products\\\":[{\\\"cart_id\\\":\\\"0\\\",\\\"product_id\\\":\\\""+dish.item_id+"\\\",\\\"product_quantity\\\":1,\\\"product_name\\\":\\\""+dish.name+"\\\",\\\"cart_type\\\":\\\"cater\\\"}]}\"}";

            Runnable r = new Runnable(){
                @Override
                public void run() {
                    DataBase.model.confirmOrder(confirm_str);
                }
            };
            new Thread(r).start();

        } else {
            TextView dish_name = (TextView) findViewById(R.id.dish);
            dish_name.setText(DataBase.searchString);
            TextView price = (TextView) findViewById(R.id.price);
            price.setText("");
            TextView time = (TextView) findViewById(R.id.time);
            time.setText("");
            TextView address = (TextView) findViewById(R.id.address);
            address.setText("");
            Speech.mNlsClient_fh.PostTtsRequest("已为您在百度外卖 下单 一份 " + DataBase.searchString + "。祝您用餐愉快！");
        }

    }
}
