package pku.sei.restaurants;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.content.Context;
import android.util.Log;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class Model {
    public List<Restaurant> eleme = new ArrayList<>();
    public List<Restaurant> meituan = new ArrayList<>();
    public List<Restaurant> baidu = new ArrayList<>();
    public List<Entry> entries = new ArrayList<>();

    // 从 API 获取信息
    private void setElemeEntryList() {

    }
    private void setMeituanEntryList() {

    }
    private void setBaiduEntryList() {

    }
    private void setElemeEntryList(String eleme_str) {

    }
    private void setMeituanEntryList(String meituan_str) {

    }
    private void setBaiduEntryList(String baidu_list) {
        baidu_list = "{\"list\":" + baidu_list + "}";
        try{
            JSONObject jsonObject = new JSONObject(baidu_list);
            JSONArray jsonarray = jsonObject.getJSONArray("list");
            int len = jsonarray.length();
            for (int i = 0; i < len; i++) {
                JSONObject res = jsonarray.getJSONObject(i);
                Restaurant temp = new Restaurant();
                temp.name = res.getString("name");
                temp.avgDeliveryTime = res.getString("avgDeliveryTime");
                temp.distance = Integer.toString(res.getInt("distance"));
                temp.score = res.getString("score");
                temp.monthSaleNum = Integer.toString(res.getInt("monthSaleNum"));
                temp.startPrice = res.getString("startPrice");
                temp.deliveryPrice = res.getString("deliveryPrice");
                JSONArray discount = res.getJSONArray("discount");
                for (int j = 0; j < discount.length(); j++)
                    temp.discount.add(discount.getJSONObject(j).getString("discountInfo"));
                baidu.add(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void mergeEntries() {

    }

    public List<Entry> getEntries() {

        setElemeEntryList();
        setMeituanEntryList();
        setBaiduEntryList();
        mergeEntries();


        //测试用，API弄好了以后请删除
        testData();

        return entries;
    }

    public List<Entry> getEntries(String baidu_str, String meituan_str, String eleme_str) {

        setElemeEntryList(eleme_str);
        setMeituanEntryList(meituan_str);
        setBaiduEntryList(baidu_str);
        mergeEntries();

        for (int i = 0; i < baidu.size(); i++) {
            Entry test = new Entry();

//            test.eleme = new Restaurant();
//            test.eleme.name = "一品生煎";
//            test.eleme.distance = "802";      //距离
//            test.eleme.score = "4.5";         //评分
//            test.eleme.monthSaleNum = "5810";  //月售
//            test.eleme.deliveryPrice = "5"; //配送费
//            test.eleme.startPrice = "20";    //起送价
//            test.eleme.avgDeliveryTime = "35"; //配送时间
//            test.eleme.discount.add("满25减12，满60减18，满88减21"); //折扣
//            test.eleme.discount.add("满60元赠送乌镇酸梅汤1份");
//            test.eleme.coupon.add("满35减2");
//            test.eleme.coupon.add("满75减15");
//            test.eleme.picture = null;       //图片

            test.baidu = baidu.get(i);
            test.baidu.coupon.add("满35减2");
            test.baidu.coupon.add("满75减15");
            test.baidu.picture = null;       //图片

            test.name = test.baidu.name;
            test.hasEleme = false;
            test.hasMeituan = false;
            test.hasBaidu = true;
            test.count = 1;

            Log.d("zsy",test.name);
            entries.add(test);
        }
        return entries;
    }

    //测试用，手动填写一个Entry
    private void testData() {
        Entry test = new Entry();
        test.name = "一品生煎";
        test.hasEleme = true;
        test.hasMeituan = false;
        test.hasBaidu = true;
        test.count = 2;

        test.eleme = new Restaurant();
        test.eleme.name = "一品生煎";
        test.eleme.distance = "802";      //距离
        test.eleme.score = "4.5";         //评分
        test.eleme.monthSaleNum = "5810";  //月售
        test.eleme.deliveryPrice = "5"; //配送费
        test.eleme.startPrice = "20";    //起送价
        test.eleme.avgDeliveryTime = "35"; //配送时间
        test.eleme.discount.add("满25减12，满60减18，满88减21"); //折扣
        test.eleme.discount.add("满60元赠送乌镇酸梅汤1份");
        test.eleme.coupon.add("满35减2");
        test.eleme.coupon.add("满75减15");
        test.eleme.picture = null;       //图片

        test.baidu = new Restaurant();
        test.baidu.name = "一品生煎";
        test.baidu.distance = "802";      //距离
        test.baidu.score = "4.5";         //评分
        test.baidu.monthSaleNum = "5810";  //月售
        test.baidu.deliveryPrice = "5"; //配送费
        test.baidu.startPrice = "20";    //起送价
        test.baidu.avgDeliveryTime = "35"; //配送时间
        test.baidu.discount.add("满25减12，满60减18，满88减21"); //折扣
        test.baidu.discount.add("满60元赠送乌镇酸梅汤1份");
        test.baidu.coupon.add("满35减2");
        test.baidu.coupon.add("满75减15");
        test.baidu.picture = null;       //图片

        entries.add(test);

        test.hasBaidu = false;
        entries.add(test);

    }


}
