package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.ui.fragment.CalendarMonthFragment;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarEntryPagerAdapter extends FragmentStatePagerAdapter implements CalendarEntryMonthFragment.DateSelectionListener {

    private DateSelectionListener dateSelectionListener;
    private HouseAvailableDates houseAvailableDates;
    private Context context;
    private ItemsCountChangeListener itemsCountChangeListener;
    private Map<String,List<Day>> selectedDaysMap;
    private ViewPager viewPager;

    public CalendarEntryPagerAdapter(FragmentManager fm, Context context, ItemsCountChangeListener itemsCountChangeListener, Map<String,List<Day>> selectedDaysMap, ViewPager viewPager) {
        super(fm);
        this.context = context;
        this.itemsCountChangeListener = itemsCountChangeListener;
        this.selectedDaysMap = selectedDaysMap;
        this.viewPager = viewPager;
    }

    public DateSelectionListener getDateSelectionListener() {
        return dateSelectionListener;
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener) {
        this.dateSelectionListener = dateSelectionListener;
    }

    @Override
    public Fragment getItem(int position) {
        CalendarEntryMonthFragment fragment = new CalendarEntryMonthFragment();
        fragment.setDateSelectionListener(this);
        if (selectedDaysMap != null && selectedDaysMap.get(String.valueOf(position)) != null)
            fragment.setSelectedDays(selectedDaysMap.get(String.valueOf(position)));
        Bundle args = new Bundle();
        int offset = position - Constants.CALENDAR_MONTHS_LIMIT / 2;
//        args.putInt("offset", (position - MONTHS_LIMIT / 2));
        args.putInt("offset", offset);
        fragment.setArguments(args);

        List<Day> days = Utils.getInstance().getDays(context, offset+1);

        int dayItemsCount = 0;
        if (days != null && days.get(0) != null) {
            if ((days.size() + days.get(0).getDayOfWeek()) <= 35)
                dayItemsCount = 42;
            else
                dayItemsCount = 49;
        }

        if (itemsCountChangeListener != null) {
            itemsCountChangeListener.itemsCountChanges(dayItemsCount);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return Constants.CALENDAR_MONTHS_LIMIT;
    }

    @Override
    public void dateSelected(Day day, boolean isSelected) {
//        dateSelectionListener.dateSelected(arriveDay);

        if (selectedDaysMap == null)
            selectedDaysMap = new HashMap<String,List<Day>>();
        if (viewPager == null)
            return;

        int currentItemPosition = viewPager.getCurrentItem();
        List<Day> selectedDays;
        if (selectedDaysMap.get(String.valueOf(currentItemPosition)) == null)
            selectedDays = new ArrayList<Day>();
        else
            selectedDays = selectedDaysMap.get(String.valueOf(currentItemPosition));

        if (isSelected)
            selectedDays.add(day);
        else {
            if (selectedDays.size() > 0)
                selectedDays.remove(day);
        }

        selectedDaysMap.put(String.valueOf(currentItemPosition), selectedDays);
        dateSelectionListener.dateSelected(selectedDaysMap);
    }

    public interface DateSelectionListener {

        public void dateSelected(Map<String,List<Day>> selectedDaysMap);
    }

    public interface ItemsCountChangeListener {
        public void itemsCountChanges(int itemsCount);
    }
}
