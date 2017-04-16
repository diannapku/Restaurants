package pku.sei.restaurants;

import android.content.Context;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.info_card, parent, false);
        }

        //TextView name = (TextView) convertView.findViewById(R.id.);
        //name.setText(entry.name);
        


        return convertView;

    }



}
