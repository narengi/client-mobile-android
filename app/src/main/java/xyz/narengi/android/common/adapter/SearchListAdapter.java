package xyz.narengi.android.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.model.AroundLocation;
import xyz.narengi.android.common.model.AroundLocationDataAttraction;
import xyz.narengi.android.common.model.AroundLocationDataCity;
import xyz.narengi.android.common.model.AroundLocationDataHouse;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<AroundLocation> locations;


    public SearchListAdapter(Context context, List<AroundLocation> locations) {
        this.context = context;
        this.locations = locations;
    }

    @Override
    public int getCount() {
        return locations == null ? 0 : locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AroundLocation location = locations.get(position);
        if (location.getType() == AroundLocation.Type.CITY) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_search_city_attraction, parent, false);

            TextView tvCityName = (TextView) convertView.findViewById(R.id.tvCityName);
            TextView tvCityDescription = (TextView) convertView.findViewById(R.id.tvCityDescription);
            ImageView imgCityImage = (ImageView) convertView.findViewById(R.id.imgCityImage);

            AroundLocationDataCity cityData = (AroundLocationDataCity) location.getData();

            tvCityName.setText(cityData.getName());
            tvCityDescription.setText(cityData.getDescription());
            if (cityData.getPictures() != null && cityData.getPictures().length > 0) {
                Picasso.with(context).load(cityData.getPictures()[0]).error(R.drawable.city_default_image).into(imgCityImage);
            } else {
                imgCityImage.setImageResource(R.drawable.city_default_image);
            }

        } else if (location.getType() == AroundLocation.Type.HOUSE) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_search_house, parent, false);
            View llNarengiSuggestion = convertView.findViewById(R.id.llNarengiSuggestionContainer);
            ImageView imgHouseImage = (ImageView) convertView.findViewById(R.id.imgHouseImage);
            TextView tvHouseName = (TextView) convertView.findViewById(R.id.tvHouseName);
            TextView tvHouseSummary = (TextView) convertView.findViewById(R.id.tvHouseSummary);
            TextView tvHousePrice = (TextView) convertView.findViewById(R.id.tvHousePricePerNight);

            AroundLocationDataHouse houseData = (AroundLocationDataHouse) location.getData();

            tvHousePrice.setVisibility(View.GONE);
            llNarengiSuggestion.setVisibility(View.GONE);

            tvHouseName.setText(houseData.getName());
            tvHouseSummary.setText(houseData.getSummary());

            if (houseData.getPictures() != null && houseData.getPictures().length > 0) {
                Picasso.with(context).load(houseData.getPictures()[0]).error(R.drawable.house_default_image).into(imgHouseImage);
            } else {
                imgHouseImage.setImageResource(R.drawable.house_default_image);
            }
        } else if (location.getType() == AroundLocation.Type.ATTRACTION) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_search_city_attraction, parent, false);

            TextView tvAttractionName = (TextView) convertView.findViewById(R.id.tvCityName);
            TextView tvAttractionDescription = (TextView) convertView.findViewById(R.id.tvCityDescription);
            ImageView imgAttractionImage = (ImageView) convertView.findViewById(R.id.imgCityImage);

            AroundLocationDataAttraction attractionData = (AroundLocationDataAttraction) location.getData();

            tvAttractionName.setText(attractionData.getName());
            tvAttractionDescription.setText(attractionData.getDescription());
            if (attractionData.getPictures() != null && attractionData.getPictures().length > 0) {
                Picasso.with(context).load(attractionData.getPictures()[0]).error(R.drawable.attraction_default_image).into(imgAttractionImage);
            } else {
                imgAttractionImage.setImageResource(R.drawable.attraction_default_image);
            }
        }


        return convertView;
    }


}
