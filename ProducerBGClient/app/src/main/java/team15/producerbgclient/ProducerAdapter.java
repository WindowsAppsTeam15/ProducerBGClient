package team15.producerbgclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marina on 09/01/2016.
 */
public class ProducerAdapter extends ArrayAdapter<Producer> {
    public ProducerAdapter(Context context, int resource, List<Producer> producers) {
        super(context, resource, producers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        Producer producer = getItem(position);

        if (producer != null) {
            TextView name = (TextView) v.findViewById(R.id.tv_producer_name);
            TextView type = (TextView) v.findViewById(R.id.tv_producer_type);

            if (name != null){
                name.setText(producer.getName());
            }
            if (type != null){
                type.setText(producer.getType());
            }
        }

        return  v;
    }
}
