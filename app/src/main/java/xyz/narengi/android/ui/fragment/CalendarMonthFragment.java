package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import calendar.CivilDate;
import calendar.DateConverter;
import calendar.PersianDate;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.ui.adapter.CalendarPagerAdapter;
import xyz.narengi.android.ui.adapter.MonthRecyclerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarMonthFragment extends Fragment implements View.OnClickListener, MonthRecyclerAdapter.ArriveDepartDatesSelectionListener {

    private int offset;
    private TextView monthCaptionTextView;
    private RecyclerView recyclerView;
    private MonthRecyclerAdapter recyclerAdapter;
    private HouseAvailableDates houseAvailableDates;
    private Utils utils = Utils.getInstance();

    private ArriveDepartDatesSelectionListener arriveDepartDatesSelectionListener;

    public CalendarMonthFragment() {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        offset = getArguments().getInt("offset");
        List<Day> days = utils.getDays(getContext(), offset);

        List<Day> lastMonthDays = utils.getDays(getContext(), offset+1);
        List<Day> nextMonthDays = utils.getDays(getContext(), offset-1);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        ImageButton previousMonthImageButton = (ImageButton)view.findViewById(R.id.calendar_header_previous_month);
        previousMonthImageButton.setOnClickListener(this);

        ImageButton nextMonthImageButton = (ImageButton)view.findViewById(R.id.calendar_header_next_month);
        nextMonthImageButton.setOnClickListener(this);

        monthCaptionTextView = (TextView)view.findViewById(R.id.calendar_header_title);

        PersianDate persianDate = Utils.getToday();
        int month = persianDate.getMonth() - offset;
        month -= 1;
        int year = persianDate.getYear();

        year = year + (month / 12);
        month = month % 12;
        if (month < 0) {
            year -= 1;
            month += 12;
        }

        month += 1;
        persianDate.setMonth(month);
        persianDate.setYear(year);
        persianDate.setDayOfMonth(1);

        char[] digits = utils.preferredDigits(getActivity());
        String monthYearTitle = utils.getMonthYearTitle(persianDate, digits);
        if (monthYearTitle != null && monthYearTitle.length() > 0) {
            String monthText = Utils.textShaper(monthYearTitle);
            monthCaptionTextView.setText(monthText);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.monthRecyclerView);
//        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setNestedScrollingEnabled(true);
        recyclerAdapter = new MonthRecyclerAdapter(getActivity(), this, days, lastMonthDays, nextMonthDays, this, houseAvailableDates);
        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }

    private void updateDaysRecyclerView() {
        offset = getArguments().getInt("offset");
        List<Day> days = utils.getDays(getContext(), offset);

        List<Day> lastMonthDays = utils.getDays(getContext(), offset-1);
        List<Day> nextMonthDays = utils.getDays(getContext(), offset+1);

        recyclerAdapter = new MonthRecyclerAdapter(getActivity(), this, days, lastMonthDays, nextMonthDays, this, houseAvailableDates);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.calendar_header_previous_month:
                changeMonth(-1);
                break;
            case R.id.calendar_header_next_month:
                changeMonth(1);
                break;
        }
    }

    private void changeMonth(int position) {
        Fragment calendarFragment = getActivity().getSupportFragmentManager().findFragmentByTag("CalendarFragment");
        if (calendarFragment != null && (calendarFragment instanceof CalendarFragment)) {
            ((CalendarFragment)calendarFragment).changeMonth(position);
        }
    }

    public void dayOnClick(int position, PersianDate persianDate, Day day) {
        char[] digits = utils.preferredDigits(getActivity());
        monthCaptionTextView.setText(utils.dateToString(persianDate, digits));

        CivilDate civilDate = DateConverter.persianToCivil(persianDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());
        Date date = calendar.getTime();
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
}
