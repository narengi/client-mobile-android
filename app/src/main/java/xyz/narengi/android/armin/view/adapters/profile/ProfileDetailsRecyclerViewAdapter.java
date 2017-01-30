package xyz.narengi.android.armin.view.adapters.profile;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.adapters.ImageAdapter;

/**
 * Created by arminghm on 1/31/17.
 */

public class ProfileDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ProfileDetailsRecyclerViewAdapter.class.getSimpleName();

    private static final int HEADER_ITEM_TYPE = 0;
    private String aboutMe;
    private ArrayList<HouseModel> houseModels;

    public void setHouseModels(ArrayList<HouseModel> houseModels) {
        this.houseModels = houseModels;
    }

    public ProfileDetailsRecyclerViewAdapter(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return HEADER_ITEM_TYPE;
            default:
                return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER_ITEM_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_profile_details_recycler_view_header,
                            parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search, parent, false);
            return new HousesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textViewAboutMe.setText(aboutMe);
        } else {
            ImageAdapter imageAdapter = new ImageAdapter(houseModels.get(position).getImages());
            ((HousesViewHolder) holder).viewPager.setAdapter(imageAdapter);
            ((HousesViewHolder) holder).textViewHouseName
                    .setText(houseModels.get(position).getName());
            ((HousesViewHolder) holder).textViewHouseDescription.
                    setText(houseModels.get(position).getDescription());
            ((HousesViewHolder) holder).textViewPriceRent
                    .setText(houseModels.get(position).getPriceRent());
        }
    }

    @Override
    public int getItemCount() {
        return houseModels.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewAboutMe;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textViewAboutMe = (TextView) itemView.findViewById(R.id.TextViewAboutMe);
        }
    }

    public class HousesViewHolder extends RecyclerView.ViewHolder {

        private ViewPager viewPager;
        private TextView textViewHouseName;
        private TextView textViewHouseDescription;
        private TextView textViewPriceRent;

        public HousesViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.ViewPagerImages);
            textViewHouseName = (TextView) itemView.findViewById(R.id.TextViewHouseName);
            textViewHouseDescription = (TextView) itemView.findViewById(R.id.TextViewHouseDescription);
            textViewPriceRent = (TextView) itemView.findViewById(R.id.TextViewPriceRent);
        }
    }
}
