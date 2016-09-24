package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.DrawerItem;

/**
 * Created by Sepebr Behroozi on 9/22/2016 AD.
 */

public class DrawerItemsListAdapter extends BaseAdapter {
    private Context context;

    @NonNull
    private List<DrawerItem> items;

    public DrawerItemsListAdapter(Context context, @NonNull List<DrawerItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();
            holder.imgMenuIcon = (ImageView) convertView.findViewById(R.id.imgMenuIcon);
            holder.tvMenuTitle = (TextView) convertView.findViewById(R.id.tvMenuTitle);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrawerItem item = items.get(position);
        holder.tvMenuTitle.setText(item.getTitle());
        holder.imgMenuIcon.setImageResource(item.getIconResource());


        convertView.setTag(holder);

        return convertView;
    }

    class ViewHolder {
        TextView tvMenuTitle;
        ImageView imgMenuIcon;
    }
}
