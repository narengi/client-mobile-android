package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class SuggestionsRecyclerAdapter extends RecyclerView.Adapter<SuggestionsRecyclerAdapter.ViewHolder> {

    private Context context;
    private Object[] suggestions;

    public SuggestionsRecyclerAdapter(Context context, Object[] suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SuggestionsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your suggestions at this position
        // - replace the contents of the view with that suggestions

        if (suggestions == null || suggestions.length <= position)
            return;

        Object suggestion = suggestions[position];
        if (suggestion instanceof String) {
            viewHolder.txtViewTitle.setText((String)suggestions[position]);
            viewHolder.iconImageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_recent_history));
        } else if (suggestion instanceof AroundPlaceCity) {
            AroundPlaceCity city = (AroundPlaceCity)suggestion;
            viewHolder.txtViewTitle.setText(city.getName() + ", " + city.getHouseCountText());
            viewHolder.iconImageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
        } else if (suggestion instanceof AroundPlaceAttraction) {
            AroundPlaceAttraction attraction = (AroundPlaceAttraction)suggestion;
            viewHolder.txtViewTitle.setText(attraction.getName() + ", " + attraction.getCityName());
            viewHolder.iconImageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_camera));
        } else if (suggestion instanceof AroundPlaceHouse) {
            AroundPlaceHouse house = (AroundPlaceHouse)suggestion;
            viewHolder.txtViewTitle.setText(house.getName() + ", " + house.getCityName());
            viewHolder.iconImageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_agenda));
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView iconImageView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.search_result_item_title);
            iconImageView = (ImageView) itemLayoutView.findViewById(R.id.search_result_item_icon);
        }
    }


    // Return the size of your suggestions (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return suggestions.length;
    }
}