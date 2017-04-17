package pku.sei.restaurants;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, int resource, List<Entry> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Entry entry = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.info_card, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.restaurant_name);
        name.setText(entry.name);

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
        TextView score = (TextView) convertView.findViewById(R.id.score);
        score.setText(Double.toString(dScore));

        int nDistance = 0;
        if (entry.hasBaidu) {
            nDistance += Integer.valueOf(entry.baidu.distance);
        }
        if (entry.hasEleme) {
            nDistance += Integer.valueOf(entry.eleme.distance);
        }
        if (entry.hasMeituan) {
            nDistance += Integer.valueOf(entry.meituan.distance);
        }
        nDistance /= entry.count;
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        distance.setText("距离" + Integer.toString(nDistance) + "米");

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
        TextView orders = (TextView) convertView.findViewById(R.id.orders);
        orders.setText("月售" + Integer.toString(nOrders) + "单");


        if (entry.hasBaidu) {
            StringBuilder text = new StringBuilder();
            text.append("￥" + entry.baidu.startPrice + "起送  ￥" + entry.baidu.deliveryPrice + "配送   " + entry.baidu.avgDeliveryTime + "分钟\n");
            for (String s : entry.baidu.discount) {
                text.append(s + "\n");
            }
            for (String s : entry.baidu.luckyMoney) {
                text.append(s + "\n");
            }
            for (String s : entry.baidu.coupon) {
                text.append(s + "\n");
            }
            text.deleteCharAt(text.length()-1);
            TextView Baidu_info = (TextView) convertView.findViewById(R.id.Baidu_info);
            Baidu_info.setText(text);

            LinearLayout baidu = (LinearLayout) convertView.findViewById(R.id.baidu_layout);
            baidu.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v) {
                   try {
                       PackageManager packageManager = getContext().getPackageManager();
                       Intent intent = packageManager.getLaunchIntentForPackage("com.baidu.lbs.waimai");
                       getContext().startActivity(intent);
                   } catch (Exception e) {
                       Log.d("sxh", "跳转到百度外卖失败");
                       Toast.makeText(getContext(), "跳转到百度外卖失败", Toast.LENGTH_SHORT).show();
                   }
               }
            });

        } else {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.restaurant_detail);
            LinearLayout baidu = (LinearLayout) convertView.findViewById(R.id.baidu_layout);
            layout.removeView(baidu);
        }

        if (entry.hasEleme) {
            StringBuilder text = new StringBuilder();
            text.append("￥" + entry.eleme.startPrice + "起送  ￥" + entry.eleme.deliveryPrice + "配送   " + entry.eleme.avgDeliveryTime + "分钟\n");
            for (String s : entry.eleme.discount) {
                text.append(s + "\n");
            }
            for (String s : entry.eleme.luckyMoney) {
                text.append(s + "\n");
            }
            for (String s : entry.eleme.coupon) {
                text.append(s + "\n");
            }
            text.deleteCharAt(text.length()-1);
            TextView Eleme_info = (TextView) convertView.findViewById(R.id.Eleme_info);
            Eleme_info.setText(text);

            LinearLayout eleme = (LinearLayout) convertView.findViewById(R.id.eleme_layout);
            eleme.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        PackageManager packageManager = getContext().getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage("me.ele");
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.d("sxh", "跳转到饿了么失败");
                        Toast.makeText(getContext(), "跳转到饿了么失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.restaurant_detail);
            LinearLayout eleme = (LinearLayout) convertView.findViewById(R.id.eleme_layout);
            layout.removeView(eleme);
        }

        if (entry.hasMeituan) {
            StringBuilder text = new StringBuilder();
            text.append("￥" + entry.meituan.startPrice + "起送  ￥" + entry.meituan.deliveryPrice + "配送   " + entry.meituan.avgDeliveryTime + "分钟\n");
            for (String s : entry.meituan.discount) {
                text.append(s + "\n");
            }
            for (String s : entry.meituan.luckyMoney) {
                text.append(s + "\n");
            }
            for (String s : entry.meituan.coupon) {
                text.append(s + "\n");
            }
            text.deleteCharAt(text.length()-1);
            TextView Meituan_info = (TextView) convertView.findViewById(R.id.Meituan_info);
            Meituan_info.setText(text);

            LinearLayout meituan = (LinearLayout) convertView.findViewById(R.id.meituan_layout);
            meituan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        PackageManager packageManager = getContext().getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage("com.sankuai.meituan.takeoutnew");
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.d("sxh", "跳转到美团外卖失败");
                        Toast.makeText(getContext(), "跳转到美团外卖失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.restaurant_detail);
            LinearLayout meituan = (LinearLayout) convertView.findViewById(R.id.meituan_layout);
            layout.removeView(meituan);
        }

        return convertView;

    }



}
