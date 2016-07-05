package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byagowi.persiancalendar.Utils;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;

/**
 * @author Siavash Mahmoudpour
 */
public class EditHouseContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private House house;

    public EditHouseContentRecyclerAdapter(Context context, House house) {
        this.context = context;
        this.house = house;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.edit_house_menu_item, parent, false);
        viewHolder = new MenuItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setupMenuItem((MenuItemViewHolder)holder, position);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    private void setupMenuItem(MenuItemViewHolder viewHolder, int position) {

        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        if (viewHolder.menuItemTitleTextView.getLayoutParams() != null)
            viewHolder.menuItemTitleTextView.getLayoutParams().height = actionBarHeight * 8 / 10;

        if (viewHolder.menuItemIndicatorTextView.getLayoutParams() != null) {
            viewHolder.menuItemIndicatorTextView.getLayoutParams().height = actionBarHeight / 2;
            viewHolder.menuItemIndicatorTextView.getLayoutParams().width = actionBarHeight / 2;
        }

        switch (position) {
            case 0:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_info_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 1:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_map_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 2:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_type_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 3:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_room_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 4:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_guest_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 5:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_features_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 6:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_images_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
            case 7:
                viewHolder.menuItemTitleTextView.setText(context.getString(R.string.house_dates_entry_page_title));
                viewHolder.menuItemTitleTextView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_my_house_dates),
                        null, null, null);
                break;
        }

        char[] digits = Utils.getInstance().preferredDigits(context);
        viewHolder.menuItemIndicatorTextView.setText(Utils.formatNumber(position + 1, digits));

    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {

        public TextView menuItemTitleTextView;
        public TextView menuItemIndicatorTextView;

        public MenuItemViewHolder(View itemView) {
            super(itemView);

            menuItemTitleTextView = (TextView)itemView.findViewById(R.id.edit_house_menu_item_title);
            menuItemIndicatorTextView = (TextView)itemView.findViewById(R.id.edit_house_menu_item_indicator);
        }
    }
}
