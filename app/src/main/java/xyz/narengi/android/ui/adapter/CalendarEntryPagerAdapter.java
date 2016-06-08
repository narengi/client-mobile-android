package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.List;

import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.ui.fragment.CalendarMonthFragment;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarEntryPagerAdapter extends FragmentStatePagerAdapter implements CalendarEntryMonthFragment.DateSelectionListener {

    private final int MONTHS_LIMIT = 1200;
    private DateSelectionListener dateSelectionListener;
    private HouseAvailableDates houseAvailableDates;
    private Context context;
    private ItemsCountChangeListener itemsCountChangeListener;

    public CalendarEntryPagerAdapter(FragmentManager fm, Context context, ItemsCountChangeListener itemsCountChangeListener) {
        super(fm);
        this.context = context;
        this.itemsCountChangeListener = itemsCountChangeListener;
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
        Bundle args = new Bundle();
        int offset = position - MONTHS_LIMIT / 2;
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
        return MONTHS_LIMIT;
    }

    @Override
    public void dateSelected(Day arriveDay) {
        dateSelectionListener.dateSelected(arriveDay);
    }

    public interface DateSelectionListener {

        public void dateSelected(Day arriveDay);
    }

    public interface ItemsCountChangeListener {
        public void itemsCountChanges(int itemsCount);
    }
}
