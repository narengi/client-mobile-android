package xyz.narengi.android.armin.view.adapters.main;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.adapters.ImageAdapter;

/**
 * Created by arminghm on 1/25/17.
 */

public class ExploreRecyclerViewAdapter extends RecyclerView.Adapter<ExploreRecyclerViewAdapter.ViewHolder> {

    private ArrayList<HouseModel> houseModels;

    public void setHouseModels(ArrayList<HouseModel> houseModels) {
        if (this.houseModels == null) {
            this.houseModels = houseModels;
        }
    }

    public ArrayList<HouseModel> getHouseModels() {
        return houseModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewHouseName.setText(houseModels.get(position).getName());
        holder.textViewHouseDescription.setText(houseModels.get(position).getDescription());
        holder.textViewPriceRent.setText(houseModels.get(position).getPriceRent());

        ImageAdapter imageAdapter = new ImageAdapter(houseModels.get(position).getImages());
        holder.images.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewHouseName;
        TextView textViewHouseDescription;
        TextView textViewPriceRent;

        ViewPager images;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewHouseName = (TextView) itemView.findViewById(R.id.TextViewHouseName);
            textViewHouseDescription = (TextView) itemView
                    .findViewById(R.id.TextViewHouseDescription);
            textViewPriceRent = (TextView) itemView.findViewById(R.id.TextViewPriceRent);
            images = (ViewPager) itemView.findViewById(R.id.ViewPagerImages);
        }
    }

}
