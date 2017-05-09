package pku.sei.restaurants;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Zhengshuyu on 2017/5/9.
 */

public class Recommender {
    private Map<String, Entry> recommend_map;

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

    public Entry startRecommendation() {
        int len = recommend_map.size();
        Random ran = new Random();
        while (len > 0) {
            int index = ran.nextInt(len);
            String dimension = AppConsts.DIMENSIONS[index];
            if (recommend_map.get(dimension) != null) {
                Entry result = recommend_map.get(dimension);
                result.dimension = dimension;
                recommend_map.remove(dimension);
                return result;
            }
        }
        return null;
    }
}
