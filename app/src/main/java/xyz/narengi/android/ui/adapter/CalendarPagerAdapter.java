package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.Date;
import java.util.List;

import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.ui.fragment.CalendarMonthFragment;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarPagerAdapter extends FragmentStatePagerAdapter implements CalendarMonthFragment.ArriveDepartDatesSelectionListener {

    private final int MONTHS_LIMIT = 1200;
    private ArriveDepartDatesSelectionListener arriveDepartDatesSelectionListener;
    private HouseAvailableDates houseAvailableDates;
    private Context context;
    private ItemsCountChangeListener itemsCountChangeListener;

    public CalendarPagerAdapter(FragmentManager fm, Context context, ItemsCountChangeListener itemsCountChangeListener) {
        super(fm);
        this.context = context;
        this.itemsCountChangeListener = itemsCountChangeListener;
    }

    public ArriveDepartDatesSelectionListener getArriveDepartDatesSelectionListener() {
        return arriveDepartDatesSelectionListener;
    }

    public void setArriveDepartDatesSelectionListener(ArriveDepartDatesSelectionListener arriveDepartDatesSelectionListener) {
        this.arriveDepartDatesSelectionListener = arriveDepartDatesSelectionListener;
    }

    public HouseAvailableDates getHouseAvailableDates() {
        return houseAvailableDates;
    }

    public void setHouseAvailableDates(HouseAvailableDates houseAvailableDates) {
        this.houseAvailableDates = houseAvailableDates;
    }

    @Override
    public Fragment getItem(int position) {
        CalendarMonthFragment fragment = new CalendarMonthFragment();
        fragment.setArriveDepartDatesSelectionListener(this);
        fragment.setHouseAvailableDates(houseAvailableDates);
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
    public void arriveDateSelected(Day arriveDay) {
        arriveDepartDatesSelectionListener.arriveDateSelected(arriveDay);
    }

    @Override
    public void departDateSelected(Day departDay) {
        arriveDepartDatesSelectionListener.departDateSelected(departDay);
    }

    public interface ArriveDepartDatesSelectionListener {

        public void arriveDateSelected(Day arriveDay);

        public void departDateSelected(Day departDay);
    }

    public interface ItemsCountChangeListener {
        public void itemsCountChanges(int itemsCount);
    }
}
