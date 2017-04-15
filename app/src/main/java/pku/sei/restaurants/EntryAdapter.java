package pku.sei.restaurants;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by xiaohan on 2017/4/15.
 */

public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, int resource, List<Entry> objects) {
        super(context, resource, objects);
    }

}
