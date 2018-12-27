package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseFeature;

/**
 * @author Siavash Mahmoudpour
 */
public class FeatureListArrayAdapter extends ArrayAdapter<HouseFeature> {

    private Context context;
    private int resource;
    private HouseFeature[] objects;


    static class ViewHolder {
        public TextView titleTextView;
        public ImageButton iconImageButton;
    }

    public FeatureListArrayAdapter(Context context, int resource, HouseFeature[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View rowView = convertView;
        //reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(resource, parent, false);
            //configure view holder

            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView)rowView.findViewById(R.id.house_features_list_item_title);
            viewHolder.iconImageButton = (ImageButton)rowView.findViewById(R.id.house_features_list_item_icon);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)rowView.getTag();
        }

        //fill data
//        ViewHolder viewHolder = (ViewHolder)rowView.getTag();

        if( objects != null && position < objects.length ) {
            HouseFeature houseFeature = objects[position];

            viewHolder.titleTextView.setText(houseFeature.getTitle());


            Drawable drawable;
            if (houseFeature.getKey() != null) {
                switch (houseFeature.getKey()) {
                    case "furniture":
                        drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_call);
                        drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
                        break;
                    case "oven":
                        drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                        drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
                        break;
                    case "internet":
                        drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_camera);
                        drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
                        break;
                    default:
                        drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_agenda);
                        drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
                        break;

                }
            } else {
                drawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_email);
                drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
            }

            viewHolder.iconImageButton.setImageDrawable(drawable);

        }

        return rowView;
    }
}