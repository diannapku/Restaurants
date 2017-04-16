package pku.sei.restaurants;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        TextView score = (TextView) convertView.findViewById(R.id.score);
        score.setText("4.5");

        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        distance.setText("test");

        TextView orders = (TextView) convertView.findViewById(R.id.orders);
        orders.setText("test");

        TextView Baidu_info = (TextView) convertView.findViewById(R.id.Baidu_info);
        Baidu_info.setText("test");

        TextView Eleme_info = (TextView) convertView.findViewById(R.id.Eleme_info);
        Eleme_info.setText("test");

        TextView Meituan_info = (TextView) convertView.findViewById(R.id.Meituan_info);
        Meituan_info.setText("test");


        return convertView;

    }



}
