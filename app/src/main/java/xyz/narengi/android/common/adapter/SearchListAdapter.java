package xyz.narengi.android.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import xyz.narengi.android.R;
import xyz.narengi.android.common.model.AroundLocation;
import xyz.narengi.android.common.model.AroundLocationDataAttraction;
import xyz.narengi.android.common.model.AroundLocationDataCity;
import xyz.narengi.android.common.model.AroundLocationDataHouse;
import xyz.narengi.android.ui.activity.HouseActivity;
import xyz.narengi.android.ui.widget.AutoScrollViewPager;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public class SearchListAdapter extends BaseAdapter {
    private int itemHeght;
    private static final long[] autoScrollIntervals = {
            1500,
            2000,
            3000,
            3500,
            4000,
            2500,
            4500
    };
    private Context context;
    private List<AroundLocation> locations;


    public SearchListAdapter(Activity activity, List<AroundLocation> locations) {
        this.context = activity;
        this.locations = locations;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity. getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        itemHeght = screenWidth*5/8;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false);
            holder = new ViewHolder();
            holder.vpImages = (ViewPager) convertView.findViewById(R.id.vpImages);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);
            holder.tvHousePrice = (TextView) convertView.findViewById(R.id.tvHousePricePerNight);
            holder.llNarengiSuggestion = convertView.findViewById(R.id.llNarengiSuggestionContainer);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeght);
            convertView.setLayoutParams(params);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, HouseActivity.class);
                    intent.putExtra("houseId",  ((AroundLocationDataHouse)locations.get(position).getData()).getId());
                    context.startActivity(intent);
                }
            });


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AroundLocation location = locations.get(position);
//        holder.tvHousePrice.setVisibility(location.getType() == AroundLocation.Type.HOUSE ? View.VISIBLE : View.GONE);
//        holder.llNarengiSuggestion.setVisibility(location.getType() == AroundLocation.Type.HOUSE ? View.VISIBLE : View.GONE);

        if (location.getType() == AroundLocation.Type.HOUSE) {
//            holder.tvHousePrice.setVisibility(View.GONE);
//            holder.llNarengiSuggestion.setVisibility(View.GONE);
            holder.tvName.setText(((AroundLocationDataHouse) location.getData()).getName());
            holder.tvSummary.setText(((AroundLocationDataHouse) location.getData()).getSummary());
            holder.tvHousePrice.setText(((AroundLocationDataHouse) location.getData()).getPrice());

            PicturesPagerAdapter adapter = new PicturesPagerAdapter(((AroundLocationDataHouse) location.getData()).getPictures(), AroundLocation.Type.HOUSE, position);
            holder.vpImages.setAdapter(adapter);
        } else if (location.getType() == AroundLocation.Type.CITY) {
            holder.tvName.setText(((AroundLocationDataCity) location.getData()).getName());
            holder.tvSummary.setText(((AroundLocationDataCity) location.getData()).getDescription());

            PicturesPagerAdapter adapter = new PicturesPagerAdapter(((AroundLocationDataCity) location.getData()).getPictures(), AroundLocation.Type.CITY, position);
            holder.vpImages.setAdapter(adapter);
        } else if (location.getType() == AroundLocation.Type.ATTRACTION) {
            holder.tvName.setText(((AroundLocationDataAttraction) location.getData()).getName());
            holder.tvSummary.setText(((AroundLocationDataAttraction) location.getData()).getDescription());

            PicturesPagerAdapter adapter = new PicturesPagerAdapter(((AroundLocationDataAttraction) location.getData()).getPictures(), AroundLocation.Type.ATTRACTION, position);
            holder.vpImages.setAdapter(adapter);

        }
//        holder.vpImages.setInterval(autoScrollIntervals[new Random().nextInt(autoScrollIntervals.length)]);
//        holder.vpImages.startAutoScroll();
//        holder.vpImages.setStopScrollWhenTouch(false);

        convertView.setTag(holder);
        return convertView;
    }


    class ViewHolder {
        ViewPager vpImages;
        TextView tvName;
        TextView tvSummary;
        TextView tvHousePrice;
        View llNarengiSuggestion;
    }

    class PicturesPagerAdapter extends PagerAdapter {
        private String[] pictures;
        private AroundLocation.Type type;
        private int housePosition;

        public PicturesPagerAdapter(String[] pictures, AroundLocation.Type type, int housePosition) {
            this.pictures = pictures;
            this.type = type;
            this.housePosition = housePosition;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView img = new ImageView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, HouseActivity.class);
                    intent.putExtra("houseId",  ((AroundLocationDataHouse)locations.get(housePosition).getData()).getId());
                    context.startActivity(intent);
                }
            });

//            Picasso
//                    .with(context)
//                    .load(pictures[position])
//                    .error(
//                            type == AroundLocation.Type.HOUSE ? R.drawable.house_default_image :
//                                    type == AroundLocation.Type.ATTRACTION ? R.drawable.attraction_default_image :
//                                            type == AroundLocation.Type.CITY ? R.drawable.city_default_image :
//                                                    R.drawable.city_default_image
//                    )
//                    .into(img);
            try {
                Picasso.with(context).load("http://api.narengi.xyz/api" + pictures[position]).into(img);
            } catch (Exception e){}

            container.addView(img);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pictures.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
