package pku.sei.restaurants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class Model {
    public List<Restaurant> eleme = new ArrayList<>();
    public List<Restaurant> meituan = new ArrayList<>();
    public List<Restaurant> baidu = new ArrayList<>();
    public List<Entry> entries = new ArrayList<>();

    // 从 API 获取信息
    public void setElemeEntryList() {

    }
    public void setMeituanEntryList() {

    }
    public void setBaiduEntryList() {

    }

    public void mergeEntries() {

    }

    public List<Entry> getEntries() {

        return entries;
    }
}
