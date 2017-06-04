package pku.sei.restaurants;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.yancloud.android.reflection.YanCloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    HashMap<String, Integer> aliveApps;

    public void confirmOrder(String json) {
        for(String appPackages : AppConsts.APP_PACKAGES) {
            int port = 0;
            if(appPackages.equals("com.baidu.lbs.waimai")) {

                if (aliveApps.get(appPackages) != null) {
                    port = aliveApps.get(appPackages);
                }
                YanCloud yanCloud = YanCloud.fromGet(AppConsts.LOCAL_IP, port);
                /*
                --------------------------------------------------
                没确认要吃东西之前不要取消注释！QAQ
                //yanCloud.get("anything", "ConfirmOrder", json);
                --------------------------------------------------
                 */

            }
        }
    }

    private void getRaw(String search_str) {

        aliveApps = Utils.scanPort();

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
            Log.v("sxh", Integer.toString(len));

            for (int i = 0; i < len; i++) {
                JSONObject res = jsonarray.getJSONObject(i);
                Restaurant temp = new Restaurant();
                temp.name = res.getString("shop_name");

                Log.v("sxh", res.getString("shop_name"));

                temp.avgDeliveryTime = res.getString("delivery_time");
                temp.distance = Integer.toString(res.getInt("distance"));
                temp.score = res.getString("average_score");
                temp.monthSaleNum = Integer.toString(res.getInt("saled_month"));
                temp.startPrice = res.getString("takeout_price");
                temp.deliveryPrice = res.getString("takeout_cost");
                temp.shopId = res.getString("shop_id");

                JSONArray dishlist = res.getJSONArray("dish_list");
                for (int j = 0; j < dishlist.length(); j++) {
                    Log.v("dish", dishlist.getJSONObject(j).getString("name"));
                    temp.dishes.add(new Dish(Double.valueOf(dishlist.getJSONObject(j).getString("current_price")), dishlist.getJSONObject(j).getString("item_id"), dishlist.getJSONObject(j).getString("name")));
                }
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
            test.weight = (double) (baidu.size() - i)/ (double) baidu.size();

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
                    temp.weight += (double) (eleme.size() - i)/ (double) eleme.size();
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
                temp.weight = (double) (eleme.size() - i)/ (double) eleme.size();
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
                    temp.weight += (double) (meituan.size() - i)/ (double) meituan.size();
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
                temp.weight = (double) (meituan.size() - i)/ (double) meituan.size();
                temp.meituan = res;
                entries.add(temp);
            }
        }

        Collections.sort(entries, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (((Entry)o2).weight > ((Entry)o1).weight) return 1;
                else if (((Entry)o2).weight < ((Entry)o1).weight) return -1;
                else return 0;
                //return ((Entry)o2).weight - ((Entry)o1).weight;
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





