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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import calendar.CivilDate;
import calendar.DateConverter;
import calendar.PersianDate;
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
    private List<Day> selectedDays;

    private DateSelectionListener dateSelectionListener;

    public CalendarEntryMonthRecyclerAdapter(Context context, CalendarEntryMonthFragment monthFragment, List<Day> days,
                                List<Day> lastMonthDays, List<Day> nextMonthDays,
                                DateSelectionListener dateSelectionListener, List<Day> selectedDays) {
        this.context = context;
        this.days = days;
        this.monthFragment = monthFragment;
        this.lastMonthDays = lastMonthDays;
        this.nextMonthDays = nextMonthDays;
        this.dateSelectionListener = dateSelectionListener;
        this.selectedDays = selectedDays;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DayViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.house_entry_month_day_item, parent, false);
        viewHolder = new DayViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder viewHolder, int position) {

        PersianDate todayDate = Utils.getToday();

        if (position >= 7) {

            if (position - 7 - days.get(0).getDayOfWeek() >= 0 && (position - 7 - days.get(0).getDayOfWeek()) < days.size()) {

                Day day = days.get(position - 7 - days.get(0).getDayOfWeek());
//                int dayPosition = position - 7 - days.get(0).getDayOfWeek();
//                Day day = getSelectedDay(dayPosition);
                viewHolder.dayItemButton.setText(day.getNum());

                if (day.isToday()) {
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(R.color.text_gray_dark));
                }

                if (day.getPersianDate() != null && day.getPersianDate().getYear() == todayDate.getYear() &&
                        day.getPersianDate().getMonth() == todayDate.getMonth() && day.getPersianDate().getDayOfMonth() < todayDate.getDayOfMonth()) {
                    viewHolder.dayItemButton.setClickable(false);
                } else {
                    viewHolder.dayItemButton.setClickable(true);
                }

            } else {

                if (position - 7 - days.get(0).getDayOfWeek() < 0 && lastMonthDays != null && lastMonthDays.size() > 0) {
                    int index = position - 7 - days.get(0).getDayOfWeek();
                    int lastMonthIndex = lastMonthDays.size() + index;

                    Day day = lastMonthDays.get(lastMonthIndex);
                    viewHolder.dayItemButton.setText(day.getNum());
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(R.color.text_gray_light));
                    viewHolder.dayItemButton.setClickable(false);
                }

                if ((position - 7 - days.get(0).getDayOfWeek()) >= days.size() && nextMonthDays != null && nextMonthDays.size() > 0) {
                    int index = position - 7 - days.get(0).getDayOfWeek() - days.size();

                    Day day = nextMonthDays.get(index);
                    viewHolder.dayItemButton.setText(day.getNum());
                    viewHolder.dayItemButton.setTextColor(context.getResources().getColor(R.color.text_gray_light));
                    viewHolder.dayItemButton.setClickable(false);
                }
            }

//            if (position == selectedDay) {
//                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
//            } else {
//                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.bg_gray_light));
//            }

            if (selectedDays != null) {
                Day currentDay = getSelectedDay(position - 7 - days.get(0).getDayOfWeek());
                for (Day day:selectedDays) {
                    if (day.getPersianDate() != null && currentDay.getPersianDate() != null &&
                        day.getPersianDate().getYear() == currentDay.getPersianDate().getYear() &&
                                day.getPersianDate().getMonth() == currentDay.getPersianDate().getMonth() &&
                                day.getPersianDate().getDayOfMonth() == currentDay.getPersianDate().getDayOfMonth()) {

//                        viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.orange_light));

                        if (isPreviousDaySelected(currentDay) && isNextDaySelected(currentDay)) {
                            viewHolder.dayItemButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_day_middle_bg));
                        } else if (!isPreviousDaySelected(currentDay) && !isNextDaySelected(currentDay)) {
                            viewHolder.dayItemButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_day_bg));
                        } else if (isPreviousDaySelected(currentDay)) {
                            viewHolder.dayItemButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_day_end_bg));
                        } else if (isNextDaySelected(currentDay)) {
                            viewHolder.dayItemButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_day_start_bg));
                        }

//                        viewHolder.dayItemButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_day_bg));
                        viewHolder.dayItemButton.setTextColor(context.getResources().getColor(R.color.orange_light));
                    }
                }
            }

//            int dayIndex = position - 7 - days.get(0).getDayOfWeek();
//            if (selectionStart != null && selectionEnd != null && dayIndex >= selectionStart && dayIndex <= selectionEnd) {
//                viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
//            } else {
//                if (position != selectedDay)
//                    viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.bg_gray_light));
//            }

            viewHolder.dayItemButton.setTextSize(16);

        } else {

            viewHolder.dayItemButton.setClickable(false);
            viewHolder.dayItemButton.setText(Utils.firstCharOfDaysOfWeekName[position]);
            viewHolder.dayItemButton.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            viewHolder.dayItemButton.setTextSize(16);
            viewHolder.dayItemButton.setVisibility(View.VISIBLE);
            viewHolder.dayItemButton.setBackgroundColor(context.getResources().getColor(R.color.bg_gray_light));
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

                if (selectedDays == null)
                    selectedDays = new ArrayList<Day>();

                int position = getAdapterPosition() - 7 - days.get(0).getDayOfWeek();
                Day selectedDay = getSelectedDay(position);
                boolean isRedundant = false;

                for (Day day:selectedDays) {
//                    if (day.getPersianDate().equals(selectedDay.getPersianDate())) {
                    if (day.getPersianDate().getYear() == selectedDay.getPersianDate().getYear() &&
                            day.getPersianDate().getMonth() == selectedDay.getPersianDate().getMonth() &&
                            day.getPersianDate().getDayOfMonth() == selectedDay.getPersianDate().getDayOfMonth()) {
                        isRedundant = true;
                        selectedDays.remove(day);
                        break;
                    }
                }
//                if (isRedundant) {
//                    selectedDays.remove(selectedDay);
//                } else {
//                    selectedDays.add(selectedDay);
//                }
                if (!isRedundant)
                    selectedDays.add(selectedDay);

                Collections.sort(selectedDays, new Comparator<Day>() {
                    @Override
                    public int compare(Day day1, Day day2) {
                        return day1.getPersianDate().getDayOfMonth() - day2.getPersianDate().getDayOfMonth();
                    }
                });

                dateSelectionListener.dateSelected(selectedDay, !isRedundant);
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

    private boolean isPreviousDaySelected(Day selectedDay) {
        if (selectedDays == null || selectedDays.size() < 2)
            return false;

        for (Day day:selectedDays) {
            if (selectedDay.getPersianDate().getDayOfMonth() == (day.getPersianDate().getDayOfMonth() + 1))
                return true;
        }
        return false;
    }

    private boolean isNextDaySelected(Day selectedDay) {
        if (selectedDays == null || selectedDays.size() < 2)
            return false;

        for (Day day:selectedDays) {
            if (selectedDay.getPersianDate().getDayOfMonth() == (day.getPersianDate().getDayOfMonth() - 1))
                return true;
        }
        return false;
    }

    public interface DateSelectionListener {

        public void dateSelected(Day day, boolean isSelected);
    }
}
