package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationField;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import calendar.CivilDate;
import calendar.DateConverter;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookPriceItem;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.HouseExtraService;
import xyz.narengi.android.ui.widget.LineDividerItemDecoration;

/**
 * @author Siavash Mahmoudpout
 */
public class BookContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CalendarPagerAdapter.ArriveDepartDatesSelectionListener, CalendarPagerAdapter.ItemsCountChangeListener {

    private Context context;
    private HouseAvailableDates houseAvailableDates;
    private House house;
    private Day arriveDay;
    private Day departDay;
    private int dayItemsCount;
    public ViewPager viewPager;
    private int daysCount;
    private BookExtraServicesRecyclerAdapter extraServicesRecyclerAdapter;

    public BookContentRecyclerAdapter(Context context, House house, HouseAvailableDates houseAvailableDates) {
        this.context = context;
        this.house = house;
        this.houseAvailableDates = houseAvailableDates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.book_arrive_depart_dates, parent, false);
                viewHolder = new ArriveDepartDatesViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_calendar, parent, false);
                viewHolder = new CalendarViewHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.book_day_night_count, parent, false);
                viewHolder = new DayNightCountViewHolder(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.book_guests, parent, false);
                viewHolder = new GuestsViewHolder(view);
                break;
            case 4:
                view = inflater.inflate(R.layout.book_extra_services, parent, false);
                viewHolder = new ExtraServicesViewHolder(view);
                break;
            case 5:
                view = inflater.inflate(R.layout.book_price_caption, parent, false);
                viewHolder = new PriceCaptionViewHolder(view);
                break;
            case 6:
                view = inflater.inflate(R.layout.book_prices, parent, false);
                viewHolder = new PricesViewHolder(view);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                ArriveDepartDatesViewHolder arriveDepartDatesViewHolder = (ArriveDepartDatesViewHolder) viewHolder;
                setArriveDepartDates(arriveDepartDatesViewHolder);
                break;
            case 1:
                if (arriveDay != null || departDay != null) {
                    //do nothing
                } else {
                    CalendarViewHolder calendarViewHolder = (CalendarViewHolder) viewHolder;
                    setupCalendar(calendarViewHolder);
                }
                break;
            case 2:
                DayNightCountViewHolder dayNightCountViewHolder = (DayNightCountViewHolder) viewHolder;
                setDayNightCount(dayNightCountViewHolder);
                break;
            case 3:
                GuestsViewHolder guestsViewHolder = (GuestsViewHolder) viewHolder;
                break;

            case 4:
                ExtraServicesViewHolder extraServicesViewHolder = (ExtraServicesViewHolder) viewHolder;
                setupExtraServices(extraServicesViewHolder);
                break;
            case 5:
                PriceCaptionViewHolder priceCaptionViewHolder = (PriceCaptionViewHolder) viewHolder;
                break;
            case 6:
                PricesViewHolder pricesViewHolder = (PricesViewHolder) viewHolder;
                setupPrices(pricesViewHolder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    private void setArriveDepartDates(ArriveDepartDatesViewHolder viewHolder) {
        if (arriveDay != null) {
            viewHolder.arriveDateTextView.setText(getSelectedDateString(arriveDay));
        } else {
            viewHolder.arriveDateTextView.setText("");
        }

        if (departDay != null) {
            viewHolder.departDateTextView.setText(getSelectedDateString(departDay));
        } else {
            viewHolder.departDateTextView.setText("");
        }
    }

    private void setupCalendar(final CalendarViewHolder viewHolder) {

        Utils.getInstance().loadLanguageFromSettings(context);
        CalendarPagerAdapter calendarPagerAdapter = new CalendarPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager(), context, this);
        calendarPagerAdapter.setArriveDepartDatesSelectionListener(this);
        calendarPagerAdapter.setHouseAvailableDates(houseAvailableDates);
        viewHolder.viewPager.setAdapter(calendarPagerAdapter);
        viewHolder.viewPager.setCurrentItem(1200 / 2);

        if (viewHolder.viewPager.getCurrentItem() != 1200 / 2) {
            viewHolder.viewPager.setCurrentItem(1200 / 2);
        }


        int height = 400;
        if (dayItemsCount > 0) {
            height = (dayItemsCount / 7) * 56;
        } else {
            List<Day> days = Utils.getInstance().getDays(context, 1200 / 2);
            if (days != null && days.get(0) != null) {
                if ((days.size() + days.get(0).getDayOfWeek()) <= 35)
                    dayItemsCount = 42;
                else
                    dayItemsCount = 49;
            }

            if (dayItemsCount > 0)
                height = (dayItemsCount / 7) * 56;
        }
        ViewGroup.LayoutParams layoutParams = viewHolder.viewPager.getLayoutParams();
        layoutParams.height = height;
        viewHolder.viewPager.setLayoutParams(layoutParams);

    }

    private void setDayNightCount(DayNightCountViewHolder viewHolder) {

        if (arriveDay != null && departDay != null) {

            DateTime arriveDateTime = new DateTime(getDateOfDay(arriveDay));
            DateTime departDateTime = new DateTime(getDateOfDay(departDay));

            Interval interval = new Interval(arriveDateTime, departDateTime);
            Chronology chronology = interval.getChronology();
            DurationField durationField = chronology.days();

            Days days = Days.daysBetween(arriveDateTime.toLocalDate(), departDateTime.toLocalDate());
            daysCount = days.getDays();

            if (daysCount > 0) {
                if (daysCount == 1) {
                    int nightsCount = 1;
                    viewHolder.dayNightCountTextView.setText(context.getResources().getString(
                            R.string.book_day_night_count, String.valueOf(nightsCount), String.valueOf(daysCount)));
                } else {
                    int nightsCount = daysCount - 1;
                    viewHolder.dayNightCountTextView.setText(context.getResources().getString(
                            R.string.book_day_night_count, String.valueOf(nightsCount), String.valueOf(daysCount)));
                }
            } else {
                daysCount = 0;
                viewHolder.dayNightCountTextView.setText("");
            }

        } else {
            daysCount = 0;
            viewHolder.dayNightCountTextView.setText("");
        }
    }

    private void setupExtraServices(ExtraServicesViewHolder viewHolder) {

    }

    private void setupPrices(PricesViewHolder viewHolder) {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.pricesRecyclerView.setLayoutManager(mLayoutManager);

        double totalPrice = 0;
        String currencyText = "";
        List<BookPriceItem> bookPriceItems = new ArrayList<BookPriceItem>();
        if (daysCount > 0 && house.getPrice() != null) {
            BookPriceItem daysPriceItem = new BookPriceItem();

            double price = daysCount * house.getPrice().getPrice();
            String priceText = String.valueOf(price);
            if (house.getPrice().getCurrency() != null) {
                switch (house.getPrice().getCurrency()) {
                    case "IRR":
                        currencyText = context.getResources().getString(R.string.currency_rials);
                        break;
                    default:
                        currencyText = context.getResources().getString(R.string.currency_rials);
                        break;
                }
            }
            priceText += " ";
            priceText += currencyText;

            daysPriceItem.setPriceText(priceText);

            String titleText = String.valueOf(daysCount);
            titleText += " ";
            titleText += context.getResources().getString(R.string.night_caption);
            titleText += " ";
            titleText += context.getResources().getString(R.string.multiply_mark);
            titleText += " ";
            titleText += String.valueOf(house.getPrice().getPrice());
            titleText += " ";
            titleText += currencyText;

            daysPriceItem.setTitle(titleText);
            daysPriceItem.setPrice(price);
            totalPrice += price;

            bookPriceItems.add(daysPriceItem);
        }

        if (extraServicesRecyclerAdapter != null && extraServicesRecyclerAdapter.getSelectedExtraServices() != null) {
            for (HouseExtraService extraService:extraServicesRecyclerAdapter.getSelectedExtraServices()) {
                BookPriceItem extraServicePriceItem = new BookPriceItem();
                extraServicePriceItem.setTitle(extraService.getName());
                extraServicePriceItem.setType(extraService.getType());
                if (extraService.getPrice() != null) {
                    String extraServiceCurrencyText = "";
                    String priceText = String.valueOf(extraService.getPrice().getFee());
                    if (extraService.getPrice().getCurrency() != null) {
                        switch (extraService.getPrice().getCurrency()) {
                            case "IRR":
                                extraServiceCurrencyText = context.getResources().getString(R.string.currency_rials);
                                break;
                            default:
                                extraServiceCurrencyText = context.getResources().getString(R.string.currency_rials);
                                break;
                        }
                    }
                    priceText += " ";
                    priceText += extraServiceCurrencyText;
                    extraServicePriceItem.setPriceText(priceText);
                    extraServicePriceItem.setPrice(extraService.getPrice().getFee());
                    totalPrice += extraServicePriceItem.getPrice();
                    bookPriceItems.add(extraServicePriceItem);
                }
            }
        }

        BookPriceItem[] priceItems = new BookPriceItem[bookPriceItems.size()];
        bookPriceItems.toArray(priceItems);

        BookPricesRecyclerAdapter recyclerAdapter = new BookPricesRecyclerAdapter(context, priceItems);
        viewHolder.pricesRecyclerView.setAdapter(recyclerAdapter);

        viewHolder.totalPriceTextView.setText(String.valueOf(totalPrice) + " " + currencyText);
    }

    private Date getDateOfDay(Day day) {

        Date date = null;

        if (day != null && day.getPersianDate() != null) {
            CivilDate civilDate = DateConverter.persianToCivil(day.getPersianDate());
            Calendar calendar = Calendar.getInstance();
            calendar.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());
            date = calendar.getTime();
        }

        return date;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void arriveDateSelected(Day arriveDay) {
        this.arriveDay = arriveDay;
        notifyDataSetChanged();
    }

    @Override
    public void departDateSelected(Day departDay) {
        this.departDay = departDay;
        notifyDataSetChanged();
    }

    private String getSelectedDateString(Day day) {

        String dateString = "";
        if (day != null) {
            char[] digits = Utils.getInstance().preferredDigits(context);
            dateString = Utils.getInstance().dateToString(day.getPersianDate(), digits);
        }

        return dateString;
    }

    @Override
    public void itemsCountChanges(int itemsCount) {
        dayItemsCount = itemsCount;

        if (viewPager == null || viewPager.getLayoutParams() == null)
            return;

        int height = 400;
        if (dayItemsCount > 0) {
            height = (dayItemsCount / 7) * 56;
        }
        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = height;
        viewPager.setLayoutParams(layoutParams);

    }

    public class ArriveDepartDatesViewHolder extends RecyclerView.ViewHolder {
        public TextView arriveDateTextView;
        public TextView departDateTextView;

        public ArriveDepartDatesViewHolder(View view) {
            super(view);

            arriveDateTextView = (TextView) view.findViewById(R.id.book_arriveDate);
            departDateTextView = (TextView) view.findViewById(R.id.book_departDate);

            if (arriveDay != null) {
                arriveDateTextView.setText(getSelectedDateString(arriveDay));
            }

            if (departDay != null) {
                departDateTextView.setText(getSelectedDateString(departDay));
            }
        }
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        public ViewPager viewPager;

        public CalendarViewHolder(View view) {
            super(view);

            viewPager = (ViewPager) view.findViewById(R.id.calendar_pager);
            BookContentRecyclerAdapter.this.viewPager = viewPager;
//            viewPager.getLayoutParams().height = 300;
        }
    }

    public class DayNightCountViewHolder extends RecyclerView.ViewHolder {
        public TextView dayNightCountTextView;

        public DayNightCountViewHolder(View view) {
            super(view);

            dayNightCountTextView = (TextView) view.findViewById(R.id.book_day_night_count);
        }
    }

    public class GuestsViewHolder extends RecyclerView.ViewHolder {

        public Button guestsAddButton;
        public Button guestsRemoveButton;
        public TextView guestsCountTextView;
        public int guestsCount = 0;

        public GuestsViewHolder(View itemView) {
            super(itemView);

            guestsCountTextView = (TextView) itemView.findViewById(R.id.book_guests_count);
            guestsAddButton = (Button) itemView.findViewById(R.id.book_guests_add);
            guestsRemoveButton = (Button) itemView.findViewById(R.id.book_guests_remove);

            guestsAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guestsCount = guestsCount + 1;
                    guestsCountTextView.setText(String.valueOf(guestsCount));
                    guestsCountTextView.setVisibility(View.VISIBLE);
                }
            });

            guestsRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guestsCount = guestsCount - 1;
                    if (guestsCount < 0) {
                        guestsCountTextView.setVisibility(View.VISIBLE);
                        guestsCount = 0;
                    } else {
                        guestsCountTextView.setText(String.valueOf(guestsCount));
                        guestsCountTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public class ExtraServicesViewHolder extends RecyclerView.ViewHolder {
        private BookExtraServicesRecyclerAdapter extraServicesRecyclerAdapter;
        public RecyclerView extraServicesRecyclerView;

        public ExtraServicesViewHolder(View view) {
            super(view);

            extraServicesRecyclerView = (RecyclerView) view.findViewById(R.id.book_extraServicesRecyclerView);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            extraServicesRecyclerView.setLayoutManager(mLayoutManager);

            HouseExtraService[] extraServices = null;
            if (house != null && house.getExtraServices() != null)
                extraServices = house.getExtraServices();

            extraServicesRecyclerAdapter = new BookExtraServicesRecyclerAdapter(context, extraServices, BookContentRecyclerAdapter.this);
            extraServicesRecyclerView.setAdapter(extraServicesRecyclerAdapter);

            BookContentRecyclerAdapter.this.extraServicesRecyclerAdapter = extraServicesRecyclerAdapter;
            extraServicesRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
        }
    }

    public class PriceCaptionViewHolder extends RecyclerView.ViewHolder {

        public PriceCaptionViewHolder(View view) {
            super(view);
        }
    }

    public class PricesViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView pricesRecyclerView;
        private TextView totalPriceTextView;

        public PricesViewHolder(View view) {
            super(view);

            pricesRecyclerView = (RecyclerView) view.findViewById(R.id.book_pricesRecyclerView);
            totalPriceTextView = (TextView)view.findViewById(R.id.book_prices_totalPrice);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            pricesRecyclerView.setLayoutManager(mLayoutManager);

            BookPriceItem[] priceItems = new BookPriceItem[0];

            BookPricesRecyclerAdapter recyclerAdapter = new BookPricesRecyclerAdapter(context, priceItems);
            pricesRecyclerView.setAdapter(recyclerAdapter);
        }
    }

}
