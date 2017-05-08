package pku.sei.restaurants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.yancloud.android.reflection.YanCloud;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class Model {

    private String rawFromBaidu = new String();
    private String rawFromMeituan = new String();
    private String rawFromEleme = new String();

    public List<Restaurant> eleme = new ArrayList<>();
    public List<Restaurant> meituan = new ArrayList<>();
    public List<Restaurant> baidu = new ArrayList<>();
    public List<Entry> entries = new ArrayList<>();

    private void getRaw(String search_str) {

        HashMap<String, Integer> aliveApps = Utils.scanPort();

        for(String appPackages : AppConsts.APP_PACKAGES) {
            // 查询美团
            int port = 0;
            if(appPackages.equals("com.sankuai.meituan.takeoutnew")) {

                if(aliveApps.get(appPackages) != null) {
                    port = aliveApps.get(appPackages);
                    Log.d("ZSY", Integer.toString(port));
                }

                //  修改于2017.4.20, 用反射自动查找端口
//                try {
//                    Class clz = Class.forName("cn.edu.pku.apiminier.debug.TraceStarter");
//                    Object cons = clz.newInstance();
//
//                    Method met = clz.getDeclaredMethod("getPortByPKgName",String.class);
//                    port = (int)met.invoke(cons,appPackages);
//                }catch(Exception e){
//                    Log.d("getPortException::", e.getMessage());
//                }

                String testSearch = "{\"lat\":\"39986316\",\"lng\":\"116304664\",\"keyword\":\" "+ search_str +" \",\"pageNum\":\"0\"}";
                YanCloud yanCloud = YanCloud.fromGet(AppConsts.LOCAL_IP, port);
                rawFromMeituan = yanCloud.get("com.sankuai.meituan.takeoutnew", "getSearch", testSearch);
                Log.d("美团外卖搜索结果:", rawFromMeituan);
            }
            if(appPackages.equals("me.ele")) {

                if(aliveApps.get(appPackages) != null) {
                    port = aliveApps.get(appPackages);
                    Log.d("ZSY", Integer.toString(port));
                }

                //  修改于2017.4.20, 用反射自动查找端口
//                try {
//                    Class clz = Class.forName("cn.edu.pku.apiminier.debug.TraceStarter");
//                    Object cons = clz.newInstance();
//
//                    Method met = clz.getDeclaredMethod("getPortByPKgName",String.class);
//                    port = (int)met.invoke(cons,appPackages);
//                }catch(Exception e){
//                    Log.d("getPortException::", e.getMessage());
//                }

                String json = "{\"latitude\": \"39.966714\",\"longitude\": \"116.306533\",\"keyword\": \"" + search_str + "\"}";
                YanCloud yanCloud = YanCloud.fromGet(AppConsts.LOCAL_IP, port);
                rawFromEleme = yanCloud.get("me.ele", "searchRestaurant", json);
                Log.d("饿了么外卖搜索结果:", rawFromEleme);
            }
            if(appPackages.equals("com.baidu.lbs.waimai")) {

                if(aliveApps.get(appPackages) != null) {
                    port = aliveApps.get(appPackages);
                    Log.d("ZSY", Integer.toString(port));
                }
                //  修改于2017.4.20, 用反射自动查找端口
//                try {
//                    Class clz = Class.forName("cn.edu.pku.apiminier.debug.TraceStarter");
//                    Object cons = clz.newInstance();
//
//                    Method met = clz.getDeclaredMethod("getPortByPKgName",String.class);
//                    port = (int)met.invoke(cons,appPackages);
//                }catch(Exception e){
//                    Log.d("getPortException::", e.getMessage());
//                }

                YanCloud yanCloud = YanCloud.fromGet(AppConsts.LOCAL_IP, port);
                String json = "{\"keyword\": \"" + search_str + "\"}";
                rawFromBaidu = (String) yanCloud.get("comm", "queryRestaurant", json);
                Log.d("百度外卖搜索结果:", rawFromBaidu);
            }
        }
    }

    // 从 API 获取信息
    private void setElemeEntryList() {

        String eleme_str = ""; //改为str，防止与List<Restaurant>概念冲突

        if(rawFromEleme != null && rawFromEleme != "failed") {
            eleme_str = rawFromEleme;
        } else {
            Log.v("setEntryList::Ele::", eleme_str);
            return;
        }

        try{
            JSONObject jsonObject = new JSONObject(eleme_str);
            JSONArray jsonarray = jsonObject.getJSONArray("restaurantInfo");
            int len = jsonarray.length();
            for (int i = 0; i < len; i++) {
                JSONObject res = jsonarray.getJSONObject(i).getJSONObject("restaurant");
                Restaurant temp = new Restaurant();
                temp.name = res.getString("name");
                temp.avgDeliveryTime = Integer.toString(res.getInt("avgDeliveryTime"));
                temp.distance = Integer.toString(res.getInt("distance"));
                temp.score = Double.toString(res.getDouble("score"));
                temp.monthSaleNum = Integer.toString(res.getInt("monthSaleNum"));
                temp.startPrice = Double.toString(res.getDouble("startPrice"));
                temp.deliveryPrice = Double.toString(res.getDouble("deliveryPrice"));
                JSONObject discount = res.getJSONObject("discount");
                temp.discount.add(discount.getString("discountInfo"));
                eleme.add(temp);
            }
        } catch (JSONException e) {
            Log.v("setEntryList::EleJson::", e.getMessage());
        }
    }

    private void setMeituanEntryList() {
        String meituan_str = "";

        if(rawFromMeituan != null && rawFromMeituan != "failed") {
            meituan_str = "{\"list\":" + rawFromMeituan + "}";
        } else {
            Log.v("setEntryList::Mei::", meituan_str);
            return;
        }

        try{
            JSONObject jsonObject = new JSONObject(meituan_str);
            JSONArray jsonarray = jsonObject.getJSONArray("list");
            int len = jsonarray.length();
            String regex = "\\d*";
            Pattern p = Pattern.compile(regex);
            Matcher m;
            for (int i = 0; i < len; i++) {
                JSONObject res = jsonarray.getJSONObject(i);
                Restaurant temp = new Restaurant();
                temp.name = res.getString("name");
                temp.distance = res.getString("distance").replace("m", "");
                temp.score = Double.toString(res.getDouble("score"));
                temp.monthSaleNum = Integer.toString(res.getInt("monthSaleNum"));
                m = p.matcher(res.getString("avgDeliveryTime"));
                if (m.find())
                    temp.avgDeliveryTime = m.group();
                m = p.matcher(res.getString("startPrice"));
                if (m.find())
                    temp.startPrice = m.group();
                m = p.matcher(res.getString("deliveryPrice"));
                if (m.find())
                    temp.deliveryPrice = m.group();
                temp.discount.add(res.getString("discount"));
                meituan.add(temp);
            }
        } catch (JSONException e) {
            Log.v("setEntryList::MeiJson::", e.getMessage());
        }
    }

    private void setBaiduEntryList() {
        String baidu_str = "";

        if(rawFromBaidu != null && rawFromBaidu != "failed") {
            baidu_str = "{\"list\":" + rawFromBaidu + "}";
        } else {
            Log.v("setEntryList::Bai::", baidu_str);
            return;
        }

        try{
            JSONObject jsonObject = new JSONObject(baidu_str);
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
            Log.v("setEntryList::BaiJson::", e.getMessage());
        }

    }


//    private void setElemeEntryList(String eleme_list, String search_str) {
//        eleme_list = "{\"list\":" + eleme_list + "}";
//        try{
//            JSONObject jsonObject = new JSONObject(eleme_list);
//            JSONArray jsonarray = jsonObject.getJSONArray("list");
//            int len = jsonarray.length();
//            for (int i = 0; i < len; i++) {
//                JSONObject res = jsonarray.getJSONObject(i);
//                Restaurant temp = new Restaurant();
//                temp.name = res.getString("name");
//                temp.avgDeliveryTime = res.getString("avgDeliveryTime");
//                temp.distance = Integer.toString(res.getInt("distance"));
//                temp.score = res.getString("score");
//                temp.monthSaleNum = Integer.toString(res.getInt("monthSaleNum"));
//                temp.startPrice = res.getString("startPrice");
//                temp.deliveryPrice = res.getString("deliveryPrice");
//                JSONArray discount = res.getJSONArray("discount");
//                for (int j = 0; j < discount.length(); j++)
//                    temp.discount.add(discount.getJSONObject(j).getString("discountInfo"));
//                eleme.add(temp);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    private void setMeituanEntryList(String meituan_list, String search_str) {
//
//    }
//    private void setBaiduEntryList(String baidu_list, String search_str) {
//        baidu_list = "{\"list\":" + baidu_list + "}";
//        try{
//            JSONObject jsonObject = new JSONObject(baidu_list);
//            JSONArray jsonarray = jsonObject.getJSONArray("list");
//            int len = jsonarray.length();
//            for (int i = 0; i < len; i++) {
//                JSONObject res = jsonarray.getJSONObject(i);
//                Restaurant temp = new Restaurant();
//                temp.name = res.getString("name");
//                temp.avgDeliveryTime = res.getString("avgDeliveryTime");
//                temp.distance = Integer.toString(res.getInt("distance"));
//                temp.score = res.getString("score");
//                temp.monthSaleNum = Integer.toString(res.getInt("monthSaleNum"));
//                temp.startPrice = res.getString("startPrice");
//                temp.deliveryPrice = res.getString("deliveryPrice");
//                JSONArray discount = res.getJSONArray("discount");
//                for (int j = 0; j < discount.length(); j++)
//                    temp.discount.add(discount.getJSONObject(j).getString("discountInfo"));
//                baidu.add(temp);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void mergeEntries() {
        int len;
        for (int i = 0; i < baidu.size(); i++) {
            Entry test = new Entry();
            test.baidu = baidu.get(i);
            test.name = test.baidu.name;
            test.hasEleme = false;
            test.hasMeituan = false;
            test.hasBaidu = true;
            test.count = 1;
            test.weight = baidu.size() - i;

            entries.add(test);
        }
        len = entries.size();
        Log.d("zsy", Integer.toString(len));
        for (int i = 0; i < eleme.size(); i++) {
            boolean flag = false;
            Restaurant res = eleme.get(i);
            Log.d("zsy", res.name);
            for (int j = 0; j < len; j++) {
                Entry temp = entries.get(j);
                if (temp.name.compareTo(res.name) == 0) {
                    temp.eleme = res;
                    temp.hasEleme = true;
                    temp.count ++;
                    temp.weight += eleme.size() - i;
                    flag = true;
                    entries.set(j, temp);
                    break;
                }
            }
            if (!flag) {
                Entry temp = new Entry();
                temp.name = res.name;
                temp.hasEleme = true;
                temp.hasBaidu = false;
                temp.hasMeituan = false;
                temp.count = 1;
                temp.weight = eleme.size() - i;
                temp.eleme = res;
                entries.add(temp);
            }
        }
        len = entries.size();
        Log.d("zsy", Integer.toString(len));
        for (int i = 0; i < meituan.size(); i++) {
            boolean flag = false;
            Restaurant res = meituan.get(i);
            for (int j = 0; j < len; j++) {
                Entry temp = entries.get(j);
                if (temp.name.compareTo(res.name) == 0) {
                    temp.meituan = res;
                    temp.hasMeituan = true;
                    temp.count ++;
                    temp.weight += meituan.size() - i;
                    flag = true;
                    entries.set(j, temp);
                    break;
                }
            }
            if (!flag) {
                Entry temp = new Entry();
                temp.name = res.name;
                temp.hasEleme = false;
                temp.hasBaidu = false;
                temp.hasMeituan = true;
                temp.count = 1;
                temp.weight = eleme.size() - i;
                temp.meituan = res;
                entries.add(temp);
            }
        }

        Collections.sort(entries, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Entry)o2).weight - ((Entry)o1).weight;
            }
        });
    }

    public List<Entry> getEntries(String search_str, BDLocation location) {

        Log.v("sxh", "纬度" + location.getLatitude());
        Log.v("sxh", "经度" + location.getLongitude());

        getRaw(search_str);

        setElemeEntryList();
        setMeituanEntryList();
        setBaiduEntryList();

        mergeEntries();


        //测试用，API弄好了以后请删除
        //testData();

        return entries;
    }
}


//    public List<Entry> getEntries(String baidu_str, String meituan_str, String eleme_str, String search_str) {
//
//        setElemeEntryList(eleme_str, search_str);
//        setMeituanEntryList(meituan_str, search_str);
//        setBaiduEntryList(baidu_str, search_str);
//        mergeEntries();
//
////        for (int i = 0; i < baidu.size(); i++) {
////            Entry test = new Entry();
////
////            test.baidu = baidu.get(i);
////            test.baidu.picture = null;       //图片
////
////            test.name = test.baidu.name;
////            test.hasEleme = false;
////            test.hasMeituan = false;
////            test.hasBaidu = true;
////            test.count = 1;
////
////            Log.d("zsy",test.name);
////            entries.add(test);
////        }
//        return entries;
//    }

    //测试用，手动填写一个Entry
//    private void testData() {
//        Entry test = new Entry();
//        test.name = "一品生煎";
//        test.hasEleme = true;
//        test.hasMeituan = false;
//        test.hasBaidu = true;
//        test.count = 2;
//
//        test.eleme = new Restaurant();
//        test.eleme.name = "一品生煎";
//        test.eleme.distance = "802";      //距离
//        test.eleme.score = "4.5";         //评分
//        test.eleme.monthSaleNum = "5810";  //月售
//        test.eleme.deliveryPrice = "5"; //配送费
//        test.eleme.startPrice = "20";    //起送价
//        test.eleme.avgDeliveryTime = "35"; //配送时间
//        test.eleme.discount.add("满25减12，满60减18，满88减21"); //折扣
//        test.eleme.discount.add("满60元赠送乌镇酸梅汤1份");
//        test.eleme.coupon.add("满35减2");
//        test.eleme.coupon.add("满75减15");
//        test.eleme.picture = null;       //图片
//
//        test.baidu = new Restaurant();
//        test.baidu.name = "一品生煎";
//        test.baidu.distance = "802";      //距离
//        test.baidu.score = "4.5";         //评分
//        test.baidu.monthSaleNum = "5810";  //月售
//        test.baidu.deliveryPrice = "5"; //配送费
//        test.baidu.startPrice = "20";    //起送价
//        test.baidu.avgDeliveryTime = "35"; //配送时间
//        test.baidu.discount.add("满25减12，满60减18，满88减21"); //折扣
//        test.baidu.discount.add("满60元赠送乌镇酸梅汤1份");
//        test.baidu.coupon.add("满35减2");
//        test.baidu.coupon.add("满75减15");
//        test.baidu.picture = null;       //图片
//
//        entries.add(test);
//
//        test.hasBaidu = false;
//        entries.add(test);
//
//    }
//
//}
