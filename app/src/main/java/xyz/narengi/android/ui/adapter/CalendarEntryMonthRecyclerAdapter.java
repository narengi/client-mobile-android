package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import calendar.CivilDate;
import calendar.DateConverter;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.ui.fragment.CalendarMonthFragment;
import xyz.narengi.android.util.DateUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarEntryMonthRecyclerAdapter extends RecyclerView.Adapter<CalendarEntryMonthRecyclerAdapter.DayViewHolder> {

    private Context context;
    private int dayOfWeek = 2;
    private int daysSize = 30;
    private CalendarEntryMonthFragment monthFragment;
    private List<Day> days;
    private List<Day> lastMonthDays;
    private List<Day> nextMonthDays;
    private int selectedDay;
    private Integer selectionStart = null;
    private Integer selectionEnd = null;

    private DateSelectionListener dateSelectionListener;

    public CalendarEntryMonthRecyclerAdapter(Context context, CalendarEntryMonthFragment monthFragment, List<Day> days,
                                List<Day> lastMonthDays, List<Day> nextMonthDays,
                                DateSelectionListener dateSelectionListener) {
        this.context = context;
        this.days = days;
        this.monthFragment = monthFragment;
        this.lastMonthDays = lastMonthDays;
        this.nextMonthDays = nextMonthDays;
        this.dateSelectionListener = dateSelectionListener;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DayViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.month_day_item, parent, false);
        viewHolder = new DayViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder viewHolder, int position) {

        if (position >= 7) {

            if (position - 7 - days.get(0).getDayOfWeek() >= 0 && (position - 7 - days.get(0).getDayOfWeek()) < days.size()) {

                Day day = days.get(position - 7 - days.get(0).getDayOfWeek());

                viewHolder.dayItemButton.setText(day.getNum());

                if (day.isToday()) {
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.black));
                }

            } else {

//                if ((position - 7) < (days.get(0).getDayOfWeek() + 1) && lastMonthDays != null && lastMonthDays.size() > 0) {
                if (position - 7 - days.get(0).getDayOfWeek() < 0 && lastMonthDays != null && lastMonthDays.size() > 0) {
//                    int index = position - 7;
                    int index = position - 7 - days.get(0).getDayOfWeek();
//                    int lastMonthIndex = lastMonthDays.size() - (days.get(0).getDayOfWeek() - index) - 2;
//                    int lastMonthIndex = lastMonthDays.size() - 1 + index;
                    int lastMonthIndex = lastMonthDays.size() + index;

                    Day day = lastMonthDays.get(lastMonthIndex);
                    viewHolder.dayItemButton.setText(day.getNum());
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                }

                if ((position - 7 - days.get(0).getDayOfWeek()) >= days.size() && nextMonthDays != null && nextMonthDays.size() > 0) {
                    int index = position - 7 - days.get(0).getDayOfWeek() - days.size();

                    Day day = nextMonthDays.get(index);
                    viewHolder.dayItemButton.setText(day.getNum());
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                }

//                viewHolder.dayItemButton.setClickable(false);
//                viewHolder.dayItemButton.setTextSize(25);
//                viewHolder.dayItemButton.setText("");
//                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            }

            if (position == selectedDay) {
                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
            } else {
                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            }

            int dayIndex = position - 7 - days.get(0).getDayOfWeek();
            if (selectionStart != null && selectionEnd != null && dayIndex >= selectionStart && dayIndex <= selectionEnd) {
                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
            } else {
                if (position != selectedDay)
                    viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            }

            /*if (houseAvailableDates != null && houseAvailableDates.getDates() != null) {

                Date currentItemDate = null;
                Day day = getSelectedDay(dayIndex);
                if (day != null && day.getPersianDate() != null) {
                    CivilDate civilDate = DateConverter.persianToCivil(day.getPersianDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());
                    currentItemDate = calendar.getTime();
                }

                boolean isAvailable = false;
                for (String dateString : houseAvailableDates.getDates()) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date availableDate = dateFormat.parse(dateString);
                        if (availableDate != null && currentItemDate != null) {

                            Calendar availableDateCalendar = Calendar.getInstance();
                            availableDateCalendar.setTime(availableDate);

                            Calendar currentItemDateCalendar = Calendar.getInstance();
                            currentItemDateCalendar.setTime(currentItemDate);

                            if (availableDateCalendar.get(Calendar.YEAR) == currentItemDateCalendar.get(Calendar.YEAR) &&
                                    availableDateCalendar.get(Calendar.MONTH) == currentItemDateCalendar.get(Calendar.MONTH) &&
                                    availableDateCalendar.get(Calendar.DAY_OF_MONTH) == currentItemDateCalendar.get(Calendar.DAY_OF_MONTH)) {

                                viewHolder.dayItemButton.setClickable(true);
                                isAvailable = true;
                                break;
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        disableDayItemButton(viewHolder);
                    }
                }

                if (isAvailable) {
                    viewHolder.dayItemButton.setClickable(true);
                } else  {
                    disableDayItemButton(viewHolder);
                }
            } else {
                disableDayItemButton(viewHolder);
            }*/

            viewHolder.dayItemButton.setTextSize(25);
//            viewHolder.dayItemButton.setClickable(true);

        } else {

            viewHolder.dayItemButton.setClickable(false);
            viewHolder.dayItemButton.setText(Utils.firstCharOfDaysOfWeekName[position]);
            viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            viewHolder.dayItemButton.setTextSize(20);
            viewHolder.dayItemButton.setVisibility(View.VISIBLE);
            viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

    }

    private void disableDayItemButton(DayViewHolder viewHolder) {
        viewHolder.dayItemButton.setClickable(false);
        viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.bg_gray_light));
    }

    @Override
    public int getItemCount() {
        if (days == null || days.size() == 0)
            return 0;

        if ((days.size() + days.get(0).getDayOfWeek()) <= 35)
            return 42;
        else
            return 49;
    }


    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Button dayItemButton;

        public DayViewHolder(View view) {
            super(view);

            dayItemButton = (Button) view.findViewById(R.id.day_item_button);
            dayItemButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (getAdapterPosition() - 7 >= 0) {
                int position = getAdapterPosition() - 7 - days.get(0).getDayOfWeek();

                if ((selectionStart == null && selectionEnd == null) || (selectionStart != null && selectionEnd != null)) {
                    selectionStart = null;
                    selectionEnd = null;
                    selectionStart = position;
//                    arriveDepartDatesSelectionListener.arriveDateSelected(getSelectedDay(position));
//                    arriveDepartDatesSelectionListener.departDateSelected(null);
                } else {
                    if (selectionStart != null) {
                        if (position <= selectionStart) {
                            selectionStart = position;
                            selectionEnd = null;
//                            arriveDepartDatesSelectionListener.arriveDateSelected(getSelectedDay(position));
//                            arriveDepartDatesSelectionListener.departDateSelected(null);
                        } else {

                            boolean isDatesContinuous = true;

                            /*if (houseAvailableDates != null && houseAvailableDates.getDates() != null) {
                                for (int i = selectionStart; i < position; i++) {
                                    Day day = getSelectedDay(i);
                                    Date date = DateUtils.getInstance(context).getDateOfDay(day);

                                    boolean isDateAvailable = false;
                                    for (String availableDateString : houseAvailableDates.getDates()) {

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                        Date availableDate = null;
                                        try {
                                            availableDate = dateFormat.parse(availableDateString);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        Calendar availableDateCalendar = Calendar.getInstance();
                                        availableDateCalendar.setTime(availableDate);

                                        Calendar currentItemDateCalendar = Calendar.getInstance();
                                        currentItemDateCalendar.setTime(date);

                                        if (availableDateCalendar.get(Calendar.YEAR) == currentItemDateCalendar.get(Calendar.YEAR) &&
                                                availableDateCalendar.get(Calendar.MONTH) == currentItemDateCalendar.get(Calendar.MONTH) &&
                                                availableDateCalendar.get(Calendar.DAY_OF_MONTH) == currentItemDateCalendar.get(Calendar.DAY_OF_MONTH)) {
                                            isDateAvailable = true;
                                        }
                                    }

                                    if (!isDateAvailable) {
                                        isDatesContinuous = false;
                                        break;
                                    }
                                }
                            }*/

                            if (isDatesContinuous) {
                                selectionEnd = position;
//                                arriveDepartDatesSelectionListener.departDateSelected(getSelectedDay(position));
                            } else {
                                selectionStart = position;
                                selectionEnd = null;
//                                arriveDepartDatesSelectionListener.arriveDateSelected(getSelectedDay(position));
                            }
                        }
                    }
                }
            }

            selectedDay = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    private Day getSelectedDay(int position) {

        Day day = null;

        if (position < 0) {
            if (lastMonthDays != null) {
                int lastMonthPosition = lastMonthDays.size() + position;
                day = lastMonthDays.get(lastMonthPosition);
            }
        } else {
            if (position >= days.size()) {

                int nextMonthPosition = position - days.size();
                if (nextMonthDays != null)
                    day = nextMonthDays.get(nextMonthPosition);

            } else {
                day = days.get(position);
            }
        }

        return day;
    }

    public interface DateSelectionListener {

        public void dateSelected(Day arriveDay);
    }
}
