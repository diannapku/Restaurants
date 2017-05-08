package pku.sei.restaurants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class Restaurant {
    public String name = "";          //店名
    public String distance = "";      //距离
    public String score = "";         //评分
    public String monthSaleNum = "";  //月售
    public String deliveryPrice = ""; //配送费
    public String startPrice = "";    //起送价
    public String avgDeliveryTime = ""; //配送时间
    public List<String> discount = new ArrayList<>(); //折扣
    public List<String> luckyMoney = new ArrayList<>();;    //红包
    public List<String> coupon = new ArrayList<>();;        //优惠券
}
