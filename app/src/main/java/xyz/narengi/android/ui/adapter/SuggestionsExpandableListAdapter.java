package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class SuggestionsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Object>> listDataChild;

    public SuggestionsExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Object>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Object childObject = getChild(groupPosition, childPosition);
        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (childObject instanceof String) {
            convertView = infalInflater.inflate(R.layout.search_history_item, null);
            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.search_history_item_title);
            titleTextView.setText(childObject.toString());
        } else {

            convertView = infalInflater.inflate(R.layout.search_result_item, null);

            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.search_result_item_title);

//            ImageButton imageView = (ImageButton) convertView
//                    .findViewById(R.id.search_result_item_icon);
            ImageView imageView = (ImageView) convertView
                    .findViewById(R.id.search_result_item_icon);

            if (childObject instanceof AroundPlaceHouse) {
                AroundPlaceHouse house = (AroundPlaceHouse)childObject;
                titleTextView.setText(house.getName() + ", " + house.getCityName());
                imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_agenda));
//                summaryTextView.setText(house.getSummary());

            } else if (childObject instanceof AroundPlaceAttraction) {
                AroundPlaceAttraction attraction = (AroundPlaceAttraction)childObject;
                titleTextView.setText(attraction.getName() + ", " + attraction.getCityName());
                imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_camera));
//                summaryTextView.setText(attraction.getAroundHousesText());
            } else if (childObject instanceof AroundPlaceCity) {
                AroundPlaceCity city = (AroundPlaceCity)childObject;
                titleTextView.setText(city.getName() + ", " + city.getHouseCountText());
                imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
//                summaryTextView.setText(city.getSummary());

            }
        }

//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.search_result_item, null);
//        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (headerTitle == null || headerTitle.length() == 0) {

            convertView = infalInflater.inflate(R.layout.empty_layout, null);
        } else {
            convertView = infalInflater.inflate(R.layout.suggestions_header, null);

            TextView headerTitleTextView = (TextView) convertView
                    .findViewById(R.id.suggestion_header_title);
            headerTitleTextView.setTypeface(null, Typeface.BOLD);
            headerTitleTextView.setText(headerTitle);
        }

        ExpandableListView expandableListView = (ExpandableListView) parent;
        expandableListView.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}