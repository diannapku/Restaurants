package pku.sei.restaurants;

/**
 * Created by xiaohan on 2017/6/4.
 */

public class Dish {
    public double current_price;
    public String item_id = "";
    public String name = "";
    Dish(double current_price, String item_id, String name) {
        this.current_price = current_price;
        this.item_id = item_id;
        this.name = name;
    }
}
