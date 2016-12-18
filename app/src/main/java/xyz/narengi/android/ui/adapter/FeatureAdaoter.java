package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.ui.widget.CustomTextView;

public class FeatureAdaoter extends RecyclerView.Adapter<FeatureAdaoter.ViewHolder> {
    private HouseFeature[] allItem;
    private Context context;

    public FeatureAdaoter(Context context, HouseFeature[] allItem) {
        this.context = context;
        this.allItem = allItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public CustomTextView text;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (CustomTextView) itemView.findViewById(R.id.text);
        }
    }

    @Override
    public FeatureAdaoter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.feature_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HouseFeature item = allItem[position];

        Picasso.with(context)
                .load("http://api.narengi.xyz/v1/medias/feature/" + item.getKey())
                .into(holder.image);

        holder.text.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return allItem.length;
    }
}
