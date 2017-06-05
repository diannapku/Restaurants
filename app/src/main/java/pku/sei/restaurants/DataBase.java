package pku.sei.restaurants;

import com.baidu.location.BDLocation;

import java.util.List;

/**
 * Created by xiaohan on 2017/5/31.
 */

public class DataBase {

    static public BDLocation location = null;
    static public List<Entry> entries = null;

    static public String searchString = null;
    static public Entry result_entry = null;
    static public int count = 0;

    static public Model model = new Model();
}
