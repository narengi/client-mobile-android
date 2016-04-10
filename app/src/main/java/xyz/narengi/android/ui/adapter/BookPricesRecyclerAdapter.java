package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookPriceItem;

/**
 * @author Siavash Mahmoudpour
 */
public class BookPricesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private BookPriceItem[] priceItems;

    public BookPricesRecyclerAdapter(Context context, BookPriceItem[] priceItems) {
        this.context = context;
        this.priceItems = priceItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.book_prices_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        setupPriceItem(viewHolder, position);
    }

    private void setupPriceItem(ViewHolder viewHolder, int position) {
        if (priceItems != null && priceItems.length > position) {

            BookPriceItem priceItem = priceItems[position];
            viewHolder.priceItemTitleTextView.setText(priceItem.getTitle());
            viewHolder.priceItemPriceTextView.setText(priceItem.getPriceText());
        }
    }

    @Override
    public int getItemCount() {

        if (priceItems != null)
            return priceItems.length;

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView priceItemTitleTextView;
        public TextView priceItemPriceTextView;

        public ViewHolder(View view) {
            super(view);

            priceItemTitleTextView = (TextView)view.findViewById(R.id.book_prices_item_title);
            priceItemPriceTextView = (TextView)view.findViewById(R.id.book_prices_item_price);
        }
    }
}
