package xyz.narengi.android.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.ui.activity.HouseActivity;
import xyz.narengi.android.ui.widget.CustomTextView;

public class ViewProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private Activity activity;
    private final View header;
    private List<House> houses;

    public ViewProfileAdapter(View header, List<House> houses, Activity activity) {
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.houses = houses;
        this.activity = activity;
    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    public int getItemCount() {
        return this.houses.size() + 1;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        if (!isHeader(position)) {
            return new ViewHolderItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_search, viewGroup, false));
        } else {
            return new ViewHolderHeader(header);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        House house;
        if (!isHeader(position)) {
            ViewHolderItem viewHolderItem = (ViewHolderItem) viewHolder;
            house = houses.get(position - 1);
            viewHolderItem.tvName.setText(house.getName());
            viewHolderItem.tvSummary.setText(house.getSummary());
            ((PicturesPagerAdapter) viewHolderItem.vpImages.getAdapter()).update(position - 1);

            try {
                viewHolderItem.tvHousePricePerNight.setText(house.getPrice().getPrice());
            } catch (Exception localException) {
                viewHolderItem.tvHousePricePerNight.setText(R.string.free);
            }
        }
    }


    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        return ITEM_VIEW_TYPE_ITEM;
    }

    private class ViewHolderItem
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        CustomTextView tvHousePricePerNight = (CustomTextView) itemView.findViewById(R.id.tvHousePricePerNight);
        CustomTextView tvName = (CustomTextView) itemView.findViewById(R.id.tvName);
        CustomTextView tvSummary = (CustomTextView) itemView.findViewById(R.id.tvSummary);
        ViewPager vpImages = (ViewPager) itemView.findViewById(R.id.vpImages);

        ViewHolderItem(View view) {
            super(view);
            vpImages.setAdapter(new PicturesPagerAdapter(vpImages));
            view.setOnClickListener(this);
        }

        public void onClick(View paramView) {
        }
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder {
        ViewHolderHeader(View view) {
            super(view);
        }
    }

    private class PicturesPagerAdapter extends PagerAdapter {
        private int housePosition;
        private String[] pictures = new String[0];
        private View vpImages;

        PicturesPagerAdapter(View paramView) {
            this.vpImages = paramView;
        }

        public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {
            paramViewGroup.removeView((View) paramObject);
        }

        public int getCount() {
            return this.pictures.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int paramInt) {
            ImageView localImageView = new ImageView(activity);
            localImageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            localImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            localImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Intent intent = new Intent(activity, HouseActivity.class);
                        String transitionName = activity.getString(R.string.transition_string);
                        intent.putExtra("images", pictures);
                        intent.putExtra("houseId", houses.get(housePosition).getId());
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, vpImages, transitionName);
                        ActivityCompat.startActivity(activity, intent, options.toBundle());
                        return;
                    }

                    Intent intent = new Intent(activity, HouseActivity.class);
                    intent.putExtra("houseId", houses.get(housePosition).getId());
                    intent.putExtra("images", houses.get(housePosition).getPictures());
                    activity.startActivity(intent);
                }
            });
            Picasso.with(activity).load("https://api.narengi.xyz/v1" + this.pictures[paramInt]).into(localImageView);
            viewGroup.addView(localImageView);
            return localImageView;
        }

        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject;
        }

        void update(int paramInt) {
            this.housePosition = paramInt;
            this.pictures = new String[houses.get(paramInt).getPictures().length];
            int i = 0;
            while (i < houses.get(paramInt).getPictures().length) {
                this.pictures[i] = houses.get(paramInt).getPictures()[i].getUrl();
                i += 1;
            }
            notifyDataSetChanged();
        }
    }
}