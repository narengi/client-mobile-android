package xyz.narengi.android.armin.view.adapters.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;

/**
 * Created by arminghm on 1/30/17.
 */

public class UserPlacesForHostingRecyclerViewAdapter extends
        RecyclerView.Adapter<UserPlacesForHostingRecyclerViewAdapter.ViewHolder> {

    private ArrayList<HouseModel> houseModels;

    public void setHouseModels(ArrayList<HouseModel> houseModels) {
        this.houseModels = houseModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.host_my_houses_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageViewHouseImage.setImageResource(R.mipmap.ic_launcher);

        holder.textViewHouseName.setText("نام خانه");
        holder.textViewHouseFirstAvailableDate.setText("اولین تاریخ آزاد");
        holder.textViewHousePrice.setText("۱۰۰ هزار تومان");
        holder.textViewHouseType.setText("آپارتمان");
        holder.textViewHouseBeds.setText("۱۰ تخت");
        holder.textViewHouseRooms.setText("۵ اتاق");
    }

    @Override
    public int getItemCount() {
        // TODO: arminghm 1/30/17 Get it from an array of places.
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageViewHouseImage;

        private TextView textViewHouseName;
        private TextView textViewHouseFirstAvailableDate;
        private TextView textViewHousePrice;
        private TextView textViewHouseType;
        private TextView textViewHouseBeds;
        private TextView textViewHouseRooms;

        private Button buttonDeleteHouse;
        private Button buttonEditHouse;
        private Button buttonShowHouseDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewHouseImage = (ImageView) itemView.findViewById(R.id.ImageViewHouseImage);

            textViewHouseRooms = (TextView) itemView.findViewById(R.id.TextViewHouseRooms);
            textViewHouseBeds = (TextView) itemView.findViewById(R.id.TextVIewHouseBeds);
            textViewHouseType = (TextView) itemView.findViewById(R.id.TextViewHouseType);
            textViewHousePrice = (TextView) itemView.findViewById(R.id.TextViewHousePrice);
            textViewHouseName = (TextView) itemView.findViewById(R.id.TextViewHouseName);
            textViewHouseFirstAvailableDate = (TextView) itemView
                    .findViewById(R.id.TextViewFirstAvailableDate);

            buttonDeleteHouse = ((Button) itemView.findViewById(R.id.ButtonDeleteHouse));
            buttonEditHouse = (Button) itemView.findViewById(R.id.ButtonEditHouse);
            buttonShowHouseDetail = (Button) itemView.findViewById(R.id.ButtonShowHouseDetail);

            buttonDeleteHouse.setOnClickListener(this);
            buttonEditHouse.setOnClickListener(this);
            buttonShowHouseDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ButtonDeleteHouse:
                    break;
                case R.id.ButtonEditHouse:
                    break;
                case R.id.ButtonShowHouseDetail:
                    break;
            }
        }
    }
}
