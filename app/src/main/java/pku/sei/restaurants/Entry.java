package pku.sei.restaurants;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class Entry {

    public String name = "";
    public boolean hasEleme = false;
    public boolean hasMeituan = false;
    public boolean hasBaidu = false;
    public Restaurant eleme = new Restaurant();
    public Restaurant meituan = new Restaurant();
    public Restaurant baidu = new Restaurant();

}
