package team15.producerbgclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marina on 09/01/2016.
 */
public class ProducerAdapter extends ArrayAdapter<ContractProducer> {
    public ProducerAdapter(Context context, int resource, List<ContractProducer> producers) {
        super(context, resource, producers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        ContractProducer producer = getItem(position);

        if (producer != null) {
            ImageView logo = (ImageView) v.findViewById(R.id.iv_producer_logo);
            TextView name = (TextView) v.findViewById(R.id.tv_producer_name);
            TextView type = (TextView) v.findViewById(R.id.tv_producer_type);

            if (logo != null) {
                if (producer.getLogo().length != 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(producer.getLogo(), 0, producer.getLogo().length);
                    logo.setImageBitmap(bitmap);
                } else {
                    logo.setImageResource(R.drawable.no_logo_available);
                }
            }
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
