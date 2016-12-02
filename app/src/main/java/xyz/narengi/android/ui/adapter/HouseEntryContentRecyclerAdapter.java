package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.List;
import java.util.Map;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.ExpandableDataProvider;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseEntryTitleItem;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseEntryContentRecyclerAdapter extends AbstractExpandableItemAdapter<HouseEntryContentRecyclerAdapter.GroupViewHolder, HouseEntryContentRecyclerAdapter.ChildViewHolder>
    implements CalendarEntryPagerAdapter.DateSelectionListener, CalendarEntryPagerAdapter.ItemsCountChangeListener {

    private Context context;
    private House house;
    private String[] itemTitles;
    //    private List<HouseEntryTitleItem> titleItems;
    private ExpandableDataProvider expandableDataProvider;
    private int dayItemsCount;

    @Override
    public void itemsCountChanges(int itemsCount) {
        dayItemsCount = itemsCount;
    }

    @Override
    public void dateSelected(Map<String, List<Day>> selectedDaysMap) {

    }

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {

    }

    //    public HouseEntryContentRecyclerAdapter(Context context, House house, ExpandableDataProvider expandableDataProvider) {
//        this.context = context;
//        this.house = house;
//        this.expandableDataProvider = expandableDataProvider;
//        setHasStableIds(true);
//    }
    public HouseEntryContentRecyclerAdapter(Context context, House house, String[] itemTitles) {
        this.context = context;
        this.house = house;
        this.itemTitles = itemTitles;
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {

        if (itemTitles != null)
            return itemTitles.length;
        return 0;
//        return expandableDataProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return 1;
//        return expandableDataProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        if (itemTitles != null && itemTitles.length > groupPosition)
            return groupPosition;
//            return itemTitles.get(groupPosition).getId();

        return groupPosition;
//        return expandableDataProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((groupPosition + 10) * 10 + childPosition + 1);
//        return expandableDataProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.house_entry_title_item, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ChildViewHolder viewHolder;
        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.house_entry_main_info, parent, false);
                viewHolder = new CalendarViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.house_entry_title_item, parent, false);
                viewHolder = new ChildViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition, int viewType) {
        // child item
//        final AbstractExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);
//        Object titleItem = getTitleItem();

        // set text
//        holder.mTextView.setText(item.getText());
        holder.numberTextView.setText(String.valueOf(groupPosition + 1));
        holder.titleTextView.setText(itemTitles[groupPosition]);

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.circle_bg_red_border;
                isExpanded = true;
            } else {
                bgResId = R.drawable.circle_bg_gray_border;
                isExpanded = false;
            }

            holder.numberTextView.setBackgroundResource(bgResId);
//            holder.mContainer.setBackgroundResource(bgResId);
//            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder viewHolder, int groupPosition, int childPosition, int viewType) {

        switch (groupPosition) {
            case 0:
                CalendarViewHolder calendarViewHolder = (CalendarViewHolder) viewHolder;
                setupCalendar(calendarViewHolder);
                break;
            default:
//                viewHolder.mTextView.setText("Fuck you bitch");
                break;
        }

//        holder.numberTextView.setText("Child " + String.valueOf(childPosition));
//        holder.titleTextView.setText(holder.toString());

//        // group item
//        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);
//
//        // set text
//        holder.mTextView.setText(item.getText());
//
//        // set background resource (target view ID: container)
//        int bgResId;
//        bgResId = R.drawable.bg_item_normal_state;
//        holder.mContainer.setBackgroundResource(bgResId);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
//        if (mProvider.getGroupItem(groupPosition).isPinned()) {
//            // return false to raise View.OnClickListener#onClick() event
//            return false;
//        }
//
//        // check is enabled
//        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
//            return false;
//        }
//
//        return true;
        return true;
    }

    private void setupCalendar(final CalendarViewHolder viewHolder) {

        Utils.getInstance().loadLanguageFromSettings(context);
        CalendarEntryPagerAdapter calendarEntryPagerAdapter = new CalendarEntryPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager(), context, this, null, viewHolder.viewPager);
        calendarEntryPagerAdapter.setDateSelectionListener(this);

        viewHolder.viewPager.setAdapter(calendarEntryPagerAdapter);
        viewHolder.viewPager.setCurrentItem(1200 / 2);

        if (viewHolder.viewPager.getCurrentItem() != 1200 / 2) {
            viewHolder.viewPager.setCurrentItem(1200 / 2);
        }

        // Calculate ActionBar height
        int actionBarHeight = 56;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        int height = 400;
        if (dayItemsCount > 0) {
            height = (dayItemsCount / 7) * actionBarHeight;
        } else {
            List<Day> days = Utils.getInstance().getDays(context, 1200 / 2);
            if (days != null && days.get(0) != null) {
                if ((days.size() + days.get(0).getDayOfWeek()) <= 35)
                    dayItemsCount = 42;
                else
                    dayItemsCount = 49;
            }

            if (dayItemsCount > 0)
                height = (dayItemsCount / 7) * actionBarHeight;
        }
        ViewGroup.LayoutParams layoutParams = viewHolder.viewPager.getLayoutParams();
        layoutParams.height = height;
        viewHolder.viewPager.setLayoutParams(layoutParams);

    }

//    public HouseEntryContentRecyclerAdapter(Context context, List<ParentObject> parentItemList, House house) {
//        super(context, parentItemList);
//
//        this.context = context;
//        this.house = house;
//    }
//
//
//    @Override
//    public ItemTitleViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
//        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//        View view = inflater.inflate(R.layout.house_entry_title_item, viewGroup, false);
//        return new ItemTitleViewHolder(view);
//    }
//
//    @Override
//    public ChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
//        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//        View view = inflater.inflate(R.layout.house_entry_title_item, viewGroup, false);
//        return new CalendarViewHolder(view);
//    }
//
//    @Override
//    public void onBindParentViewHolder(ItemTitleViewHolder itemTitleViewHolder, int position, Object parentObject) {
//        HouseEntryTitleItem titleItem = (HouseEntryTitleItem)parentObject;
//        itemTitleViewHolder.itemNumberTextView.setText(String.valueOf(titleItem.getNumber()));
//        itemTitleViewHolder.itemTitleTextView.setText(titleItem.getTitle());
//    }
//
//    @Override
//    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int position, Object childObject) {
////        if (house != null) {
//            switch (position) {
//                case 0:
//                    CalendarViewHolder calendarViewHolder = (CalendarViewHolder)childViewHolder;
//                    calendarViewHolder.itemNumberTextView.setText(String.valueOf(position+1));
//                    calendarViewHolder.itemTitleTextView.setText(house.getName());
//                    break;
//                default:
//                    calendarViewHolder = (CalendarViewHolder)childViewHolder;
//                    calendarViewHolder.itemNumberTextView.setText(String.valueOf(position+1));
//                    calendarViewHolder.itemTitleTextView.setText(childObject.toString());
//                    break;
//            }
////        } else {
//
////        }
//
//    }

    public abstract class BaseViewHolder extends AbstractExpandableItemViewHolder {
        public FrameLayout mContainer;
        public TextView mTextView;

        public BaseViewHolder(View v) {
            super(v);
//            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public class GroupViewHolder extends BaseViewHolder {
        //        public ExpandableItemIndicator mIndicator;
        public TextView numberTextView;
        public TextView titleTextView;

        public GroupViewHolder(View v) {
            super(v);
//            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
            numberTextView = (TextView) v.findViewById(R.id.house_entry_title_item_number);
            titleTextView = (TextView) v.findViewById(R.id.house_entry_title_item_title);
        }
    }

    public class ChildViewHolder extends BaseViewHolder {

//        public TextView numberTextView;
//        public TextView titleTextView;

        public ChildViewHolder(View v) {
            super(v);

//            numberTextView = (TextView) v.findViewById(R.id.house_entry_title_item_number);
//            titleTextView = (TextView) v.findViewById(R.id.house_entry_title_item_title);
        }
    }


    public class ItemTitleViewHolder /*extends ParentViewHolder*/ {

        public TextView itemNumberTextView;
        public TextView itemTitleTextView;


        public ItemTitleViewHolder(View itemView) {
//            super(itemView);

            itemNumberTextView = (TextView) itemView.findViewById(R.id.house_entry_title_item_number);
            itemTitleTextView = (TextView) itemView.findViewById(R.id.house_entry_title_item_title);
        }
    }

//    public class CalendarViewHolder /*extends ChildViewHolder*/ {
    public class CalendarViewHolder extends ChildViewHolder {

        public ViewPager viewPager;

        public CalendarViewHolder(View itemView) {
            super(itemView);

            viewPager = (ViewPager) itemView.findViewById(R.id.house_entry_main_calendarPager);
        }
    }


}
