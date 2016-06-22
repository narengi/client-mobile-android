package xyz.narengi.android.ui.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.List;
import java.util.Map;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.activity.AddHouseActivity;
import xyz.narengi.android.ui.adapter.CalendarEntryPagerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseDatesEntryFragment extends HouseEntryBaseFragment
        implements CalendarEntryPagerAdapter.DateSelectionListener, CalendarEntryPagerAdapter.ItemsCountChangeListener {


    private ViewPager viewPager;
    private int dayItemsCount;
    private Map<String,List<Day>> selectedDaysMap;

    public HouseDatesEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_dates_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof AddHouseActivity)
            selectedDaysMap = ((AddHouseActivity)getActivity()).getSelectedDaysMap();
        setupCalendar(view);

        Button nextButton = (Button)view.findViewById(R.id.house_dates_entry_finishButton);
        Button previousButton = (Button)view.findViewById(R.id.house_dates_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                if (getActivity() instanceof AddHouseActivity)
                                    ((AddHouseActivity)getActivity()).setSelectedDaysMap(selectedDaysMap);
                                getOnInteractionListener().onGoToNextSection(getHouse());
                            }
                        }
                    });
                }

                if (previousButton != null) {
                    previousButton.setVisibility(View.VISIBLE);
                    previousButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                if (getActivity() instanceof AddHouseActivity)
                                    ((AddHouseActivity)getActivity()).setSelectedDaysMap(selectedDaysMap);
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
                            }
                        }
                    });
                }
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                break;
        }
    }


    private void setupCalendar(View view) {
        viewPager = (ViewPager)view.findViewById(R.id.house_dates_entry_calendarViewPager);
        Utils.getInstance().loadLanguageFromSettings(getContext());
        CalendarEntryPagerAdapter calendarEntryPagerAdapter = new CalendarEntryPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), this, selectedDaysMap, viewPager);
        calendarEntryPagerAdapter.setDateSelectionListener(this);

        viewPager.setAdapter(calendarEntryPagerAdapter);
        viewPager.setCurrentItem(1200 / 2);

        if (viewPager.getCurrentItem() != 1200 / 2) {
            viewPager.setCurrentItem(1200 / 2);
        }

        // Calculate ActionBar height
        int actionBarHeight = 56;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        int height = 400;
        if (dayItemsCount > 0) {
            height = (dayItemsCount / 7) * actionBarHeight;
        } else {
            List<Day> days = Utils.getInstance().getDays(getActivity(), 1200 / 2);
            if (days != null && days.get(0) != null) {
                if ((days.size() + days.get(0).getDayOfWeek()) <= 35)
                    dayItemsCount = 42;
                else
                    dayItemsCount = 49;
            }

            if (dayItemsCount > 0)
                height = (dayItemsCount / 7) * actionBarHeight;
        }
        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = height;
        viewPager.setLayoutParams(layoutParams);

    }

    @Override
    public void dateSelected(Map<String,List<Day>> selectedDaysMap) {
        this.selectedDaysMap = selectedDaysMap;
    }

    @Override
    public void itemsCountChanges(int itemsCount) {
        dayItemsCount = itemsCount;
    }

}
