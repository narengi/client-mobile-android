package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookPriceItem;
import xyz.narengi.android.common.dto.BookProperties;
import xyz.narengi.android.common.dto.HouseExtraService;
import xyz.narengi.android.ui.activity.BookServiceFeeActivity;
import xyz.narengi.android.ui.widget.LineDividerItemDecoration;
import xyz.narengi.android.util.DateUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class BookSummaryContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private BookProperties bookProperties;

    public BookSummaryContentRecyclerAdapter(Context context, BookProperties bookProperties) {
        this.context = context;
        this.bookProperties = bookProperties;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.book_summary_house_info, parent, false);
                viewHolder = new HouseInfoViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.book_arrive_depart_dates, parent, false);
                viewHolder = new ArriveDepartDatesViewHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.book_summary_guests, parent, false);
                viewHolder = new GuestsViewHolder(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.book_summary_extra_services, parent, false);
                viewHolder = new ExtraServicesViewHolder(view);
                break;
            case 4:
                view = inflater.inflate(R.layout.book_price_caption, parent, false);
                viewHolder = new PriceCaptionViewHolder(view);
                break;
            case 5:
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
                HouseInfoViewHolder houseInfoViewHolder = (HouseInfoViewHolder)viewHolder;
                setupHouseInfo(houseInfoViewHolder);
                break;
            case 1:
                ArriveDepartDatesViewHolder arriveDepartDatesViewHolder = (ArriveDepartDatesViewHolder) viewHolder;
                setArriveDepartDates(arriveDepartDatesViewHolder);
                break;
            case 2:
                GuestsViewHolder guestsViewHolder = (GuestsViewHolder) viewHolder;
                break;
            case 3:
                ExtraServicesViewHolder extraServicesViewHolder = (ExtraServicesViewHolder) viewHolder;
                setupExtraServices(extraServicesViewHolder);
                break;
            case 4:
                PriceCaptionViewHolder priceCaptionViewHolder = (PriceCaptionViewHolder) viewHolder;
                break;
            case 5:
                PricesViewHolder pricesViewHolder = (PricesViewHolder) viewHolder;
                setupPrices(pricesViewHolder);
                break;
        }
    }

    private void setupHouseInfo(HouseInfoViewHolder viewHolder) {

        if (bookProperties.getHouse() != null) {

            if (bookProperties.getHouse().getImages() != null && bookProperties.getHouse().getImages().length > 0 ) {

                Picasso.with(context).load(bookProperties.getHouse().getImages()[0]).resize(160, 160).into(viewHolder.houseImageView);
            }

            viewHolder.houseTitleTextView.setText(bookProperties.getHouse().getLocation().getCity());
            viewHolder.houseSummaryTextView.setText(bookProperties.getHouse().getSummary());
        }
    }

    private void setArriveDepartDates(ArriveDepartDatesViewHolder viewHolder) {

        if (bookProperties != null) {

            if (bookProperties.getArriveDay() != null) {
                viewHolder.arriveDateTextView.setText(DateUtils.getInstance(context).getSelectedDateString(bookProperties.getArriveDay()));
            } else {
                viewHolder.arriveDateTextView.setText("");
            }

            if (bookProperties.getDepartDay() != null) {
                viewHolder.departDateTextView.setText(DateUtils.getInstance(context).getSelectedDateString(bookProperties.getDepartDay()));
            } else {
                viewHolder.departDateTextView.setText("");
            }
        }
    }

    private void setupExtraServices(ExtraServicesViewHolder viewHolder) {

    }

    private void setupPrices(PricesViewHolder viewHolder) {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.pricesRecyclerView.setLayoutManager(mLayoutManager);

        double totalPrice = 0;
        String currencyText = context.getResources().getString(R.string.currency_rials);
        List<BookPriceItem> bookPriceItems = new ArrayList<BookPriceItem>();

        if (bookProperties == null)
            return;

        if (bookProperties.getDaysCount() > 0 && bookProperties.getHouse() != null && bookProperties.getHouse().getPrice() != null) {
            BookPriceItem daysPriceItem = new BookPriceItem();

            double price = bookProperties.getDaysCount() * bookProperties.getHouse().getPrice().getPrice();
            String priceText = String.valueOf(price);
            if (bookProperties.getHouse().getPrice().getCurrency() != null) {
                switch (bookProperties.getHouse().getPrice().getCurrency()) {
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

            String titleText = String.valueOf(bookProperties.getDaysCount());
            titleText += " ";
            titleText += context.getResources().getString(R.string.night_caption);
            titleText += " ";
            titleText += context.getResources().getString(R.string.multiply_mark);
            titleText += " ";
            titleText += String.valueOf(bookProperties.getHouse().getPrice().getPrice());
            titleText += " ";
            titleText += currencyText;

            daysPriceItem.setTitle(titleText);
            daysPriceItem.setPrice(price);
            totalPrice += price;

            bookPriceItems.add(daysPriceItem);
        }

        if (bookProperties.getHouse() != null && bookProperties.getHouse().getSpec() != null && bookProperties.getHouse().getSpec().getGuestCount() < bookProperties.getGuestsCount()) {

            int extraGuestCount = bookProperties.getGuestsCount() - bookProperties.getHouse().getSpec().getGuestCount();
            BookPriceItem extraGuestsPriceItem = new BookPriceItem();
            double price = extraGuestCount * bookProperties.getHouse().getPrice().getExtraGuestPrice();
            String priceText = String.valueOf(price) + " " + currencyText;
            extraGuestsPriceItem.setPrice(price);
            extraGuestsPriceItem.setPriceText(priceText);

            String titleText = String.valueOf(extraGuestCount);
            titleText += " ";
            titleText += context.getResources().getString(R.string.book_extra_guests_caption);
            titleText += " ";
            titleText += context.getResources().getString(R.string.multiply_mark);
            titleText += " ";
            titleText += String.valueOf(bookProperties.getHouse().getPrice().getExtraGuestPrice());
            titleText += " ";
            titleText += currencyText;

            totalPrice += price;
            extraGuestsPriceItem.setTitle(titleText);
            bookPriceItems.add(extraGuestsPriceItem);
        }

        if (bookProperties.getExtraServices() != null) {
            for (HouseExtraService extraService : bookProperties.getExtraServices()) {
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

        double commission = 0;
        if (bookProperties.getHouse().getCommission() != null) {
            commission = bookProperties.getHouse().getCommission().getFee();

            if (commission <= 0 && bookProperties.getHouse().getCommission().getRate() > 0) {
                commission = totalPrice * bookProperties.getHouse().getCommission().getRate() / 100;
            }
        }

        totalPrice += commission;

        BookPricesRecyclerAdapter recyclerAdapter = new BookPricesRecyclerAdapter(context, priceItems);
        viewHolder.pricesRecyclerView.setAdapter(recyclerAdapter);

        viewHolder.totalPriceTextView.setText(String.valueOf(totalPrice) + " " + currencyText);

        viewHolder.serviceFeePriceTextView.setText(String.valueOf(commission) + " " + currencyText);

        bookProperties.setPriceItems(priceItems);
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void openServiceFee() {
        Intent intent = new Intent(context, BookServiceFeeActivity.class);
        context.startActivity(intent);
    }

    public class ArriveDepartDatesViewHolder extends RecyclerView.ViewHolder {
        public TextView arriveDateTextView;
        public TextView departDateTextView;

        public ArriveDepartDatesViewHolder(View view) {
            super(view);

            arriveDateTextView = (TextView) view.findViewById(R.id.book_arriveDate);
            departDateTextView = (TextView) view.findViewById(R.id.book_departDate);

            if (bookProperties != null) {

                if (bookProperties.getArriveDay() != null) {
                    arriveDateTextView.setText(DateUtils.getInstance(context).getSelectedDateString(bookProperties.getArriveDay()));
                }

                if (bookProperties.getDepartDay() != null) {
                    departDateTextView.setText(DateUtils.getInstance(context).getSelectedDateString(bookProperties.getDepartDay()));
                }
            }
        }
    }

    public class GuestsViewHolder extends RecyclerView.ViewHolder {

        public TextView guestsCountTextView;

        public GuestsViewHolder(View itemView) {
            super(itemView);

            guestsCountTextView = (TextView) itemView.findViewById(R.id.book_summary_guests_count);

            if (bookProperties != null)
                guestsCountTextView.setText(String.valueOf(bookProperties.getGuestsCount()));
        }
    }

    public class ExtraServicesViewHolder extends RecyclerView.ViewHolder {
        private BookExtraServicesRecyclerAdapter extraServicesRecyclerAdapter;
        public RecyclerView extraServicesRecyclerView;
        public View extraServicesDividerView;

        public ExtraServicesViewHolder(View view) {
            super(view);

            extraServicesRecyclerView = (RecyclerView) view.findViewById(R.id.book_summary_extraServicesRecyclerView);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            extraServicesRecyclerView.setLayoutManager(mLayoutManager);

            HouseExtraService[] extraServices = null;
            if (bookProperties != null && bookProperties.getHouse() != null && bookProperties.getHouse().getExtraServices() != null) {
                extraServices = bookProperties.getHouse().getExtraServices();
                BookSummaryExtraServicesRecyclerAdapter extraServicesRecyclerAdapter = new BookSummaryExtraServicesRecyclerAdapter(context, extraServices);
                extraServicesRecyclerView.setAdapter(extraServicesRecyclerAdapter);

                extraServicesRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
            } else {
                extraServicesRecyclerView.setVisibility(View.INVISIBLE);
                extraServicesDividerView.setVisibility(View.INVISIBLE);
            }

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
        private TextView serviceFeeCaptionTextView;
        private TextView serviceFeePriceTextView;

        public PricesViewHolder(View view) {
            super(view);

            pricesRecyclerView = (RecyclerView) view.findViewById(R.id.book_pricesRecyclerView);
            totalPriceTextView = (TextView) view.findViewById(R.id.book_prices_totalPrice);
            serviceFeeCaptionTextView = (TextView) view.findViewById(R.id.book_prices_serviceFeeCaption);
            serviceFeePriceTextView = (TextView) view.findViewById(R.id.book_prices_serviceFeePrice);

            serviceFeeCaptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openServiceFee();
                }
            });

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            pricesRecyclerView.setLayoutManager(mLayoutManager);

            BookPriceItem[] priceItems = new BookPriceItem[0];

            BookPricesRecyclerAdapter recyclerAdapter = new BookPricesRecyclerAdapter(context, priceItems);
            pricesRecyclerView.setAdapter(recyclerAdapter);
        }


    }

    public class HouseInfoViewHolder extends RecyclerView.ViewHolder {
        public ImageView houseImageView;
        private TextView houseTitleTextView;
        private TextView houseSummaryTextView;

        public HouseInfoViewHolder(View view) {
            super(view);

            houseImageView = (ImageView) view.findViewById(R.id.book_summary_house_image);
            houseTitleTextView = (TextView) view.findViewById(R.id.book_summary_house_title);
            houseSummaryTextView = (TextView) view.findViewById(R.id.book_summary_house_summary);
        }
    }
}