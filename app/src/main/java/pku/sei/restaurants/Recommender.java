package pku.sei.restaurants;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static pku.sei.restaurants.AppConsts.DIMENSIONS;

/**
 * Created by Zhengshuyu on 2017/5/9.
 */

public class Recommender {
    private Map<String, Entry> recommend_map = new HashMap<>();
    private Map<String, Integer> flag = new HashMap<>();
    File cacheDir;
    String history;

    public void listRecommendation(List<Entry> entries) {
        recommend_map.put(AppConsts.WEIGHT, entries.get(0));
        int len = entries.size();
        int minTime = 10000;
        int minDistance = 100000;
        double maxScore = 0;
        int maxOrders = 0;
        int tmpTime, tmpDistance, tmpOrder;
        double tmpScore;
        for (int i = 0; i < len; i++) {
            Entry entry = entries.get(i);
            tmpTime = getMinTime(entry);
            tmpDistance = getAvgDistance(entry);
            tmpOrder = getTotalOrders(entry);
            tmpScore = getAvgScore(entry);
            if (tmpTime < minTime) {
                minTime = tmpTime;
                recommend_map.put(AppConsts.DTIME, entry);
            }
            if (tmpDistance < minDistance) {
                minDistance = tmpDistance;
                recommend_map.put(AppConsts.DISTANCE, entry);
            }
            if (tmpOrder > maxOrders) {
                maxOrders = tmpOrder;
                recommend_map.put(AppConsts.ORDERS, entry);
            }
            if (tmpScore > maxScore) {
                maxScore = tmpScore;
                recommend_map.put(AppConsts.SCORE, entry);
            }
        }
        for (int i = 0; i < DIMENSIONS.length; i++) {
            flag.put(DIMENSIONS[i], 0);
        }
    }

    private double getAvgScore(Entry entry) {
        double dScore = 0.0;
        if (entry.hasBaidu) {
            dScore += Double.valueOf(entry.baidu.score);
        }
        if (entry.hasEleme) {
            dScore += Double.valueOf(entry.eleme.score);
        }
        if (entry.hasMeituan) {
            dScore += Double.valueOf(entry.meituan.score);
        }
        dScore /= (double) entry.count;
        return dScore;
    }

    private int getAvgDistance(Entry entry) {
        int nDistance = 0;
        if (entry.hasBaidu) {
            nDistance += Integer.valueOf(entry.baidu.distance);
        }
        if (entry.hasEleme) {
            nDistance += Integer.valueOf(entry.eleme.distance);
        }
        if (entry.hasMeituan) {
            if (entry.meituan.distance.indexOf("k") < 0) {
                nDistance += Integer.valueOf(entry.meituan.distance);
            }
            else {
                String temp = entry.meituan.distance.replace("k", "");
                nDistance += Double.valueOf(temp) * 1000;
            }
        }
        nDistance /= entry.count;
        return nDistance;
    }

    private int getTotalOrders(Entry entry) {
        int nOrders = 0;
        if (entry.hasBaidu) {
            nOrders += Integer.valueOf(entry.baidu.monthSaleNum);
        }
        if (entry.hasEleme) {
            nOrders += Integer.valueOf(entry.eleme.monthSaleNum);
        }
        if (entry.hasMeituan) {
            nOrders += Integer.valueOf(entry.meituan.monthSaleNum);
        }
        return nOrders;
    }

    private int getMinTime(Entry entry) {
        int minTime = 100000;
        if (entry.hasBaidu) {
            minTime = Math.min(minTime, Integer.valueOf(entry.baidu.avgDeliveryTime));
        }
        if (entry.hasEleme) {
            minTime = Math.min(minTime, Integer.valueOf(entry.eleme.avgDeliveryTime));
        }
        if (entry.hasMeituan) {
            minTime = Math.min(minTime, Integer.valueOf(entry.meituan.avgDeliveryTime));
        }
        return minTime;
    }

    // 换一家（换一个维度推荐）
    public Entry switchRecommendation() {
//        int len = DIMENSIONS.length;
        int len = 5;
        Random ran = new Random();
        while (len > 0) {
            int index = ran.nextInt(DIMENSIONS.length);
            String dimension = DIMENSIONS[index];
            if (flag.get(dimension) == 0) {
                Entry result = recommend_map.get(dimension);
                result.dimension = dimension;
                flag.put(dimension, 1);
                DataBase.result_entry = result;
                return result;
            }
            len --;
        }
        DataBase.result_entry = null;
        return null;
    }

    public void showRecommendation(Entry entry) {

    }

    // 第一次推荐，将燕云获取到的entry列表输入，按综合排序推荐
    public Entry firstRecommendation(Context context, List<Entry> entries) {
        listRecommendation(entries);
        cacheDir = context.getExternalFilesDir(null);
        File file=new File(cacheDir.getPath(),"history.txt");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("zsy","CreateFileErr");
                e.printStackTrace();
            }
        }
        else {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                history = (String) in.readObject();
                in.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Entry result = recommend_map.get(history);
        result.dimension = history;
        flag.put(history, 1);
        DataBase.result_entry = result;
        return result;
    }

    public void Update(String dimension) {
        history = dimension;
        File file=new File(cacheDir.getPath(),"history.txt");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(history);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
