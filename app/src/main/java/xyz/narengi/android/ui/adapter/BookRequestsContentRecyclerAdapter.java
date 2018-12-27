package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookRequest;
import xyz.narengi.android.common.dto.BookRequestDTO;

/**
 * @author Siavash Mahmoudpour
 */
public class BookRequestsContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BookRequest[] bookRequests;
    private Context context;

    public BookRequestsContentRecyclerAdapter(Context context, BookRequest[] bookRequests) {
        this.bookRequests = bookRequests;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.book_requests_item, parent, false);
        viewHolder = new BookRequestsItemViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            default:
                BookRequestsItemViewHolder bookRequestsItemViewHolder = (BookRequestsItemViewHolder)viewHolder;
                setupBookRequestsItem(bookRequestsItemViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {

        if (bookRequests != null)
            return bookRequests.length;

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setupBookRequestsItem(BookRequestsItemViewHolder viewHolder, int position) {

        switch (position) {
            case 0:
                viewHolder.titleTextView.setText("علیرضا محمودی");
                viewHolder.locationTextView.setText("از اصفهان ، کاشان");
                viewHolder.nightsCountTextView.setText("۴ شب");
                viewHolder.priceTextView.setText("۹۸۰،۰۰۰ تومان");
                viewHolder.datesTextView.setText("۹۵/۳/۱۵ - ۹۵/۳/۱۹");
                break;
            case 1:
                viewHolder.titleTextView.setText("یاسمن امیدوار");
                viewHolder.locationTextView.setText("از تهران ، تهران");
                viewHolder.nightsCountTextView.setText("۵ شب");
                viewHolder.priceTextView.setText("۱،۲۰۰،۰۰۰ تومان");
                viewHolder.datesTextView.setText("۹۵/۴/۵ - ۹۵/۴/۱۰");
                break;
            default:
                viewHolder.titleTextView.setText("علیرضا محمودی");
                viewHolder.locationTextView.setText("از اصفهان ، کاشان");
                viewHolder.nightsCountTextView.setText("۴ شب");
                viewHolder.priceTextView.setText("۹۸۰،۰۰۰ تومان");
                viewHolder.datesTextView.setText("۹۵/۳/۱۵ - ۹۵/۳/۱۹");
                break;
        }
    }

    public class BookRequestsItemViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView locationTextView;
        public TextView nightsCountTextView;
        public TextView priceTextView;
        public TextView datesTextView;


        public BookRequestsItemViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.book_requests_item_title);
            locationTextView = (TextView)itemView.findViewById(R.id.book_requests_item_location);
            nightsCountTextView = (TextView)itemView.findViewById(R.id.book_requests_item_nightsCount);
            priceTextView = (TextView)itemView.findViewById(R.id.book_requests_item_price);
            datesTextView = (TextView)itemView.findViewById(R.id.book_requests_item_dates);
        }
    }
}
