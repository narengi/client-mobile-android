package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseExtraService;

/**
 * @author Siavash Mahmoudpour
 */
public class BookExtraServicesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private HouseExtraService[] extraServices;
    private List<HouseExtraService> selectedExtraServices;
    private BookContentRecyclerAdapter bookContentRecyclerAdapter;

    public BookExtraServicesRecyclerAdapter(Context context, HouseExtraService[] extraServices, BookContentRecyclerAdapter bookContentRecyclerAdapter) {
        this.context = context;
        this.extraServices = extraServices;
        this.bookContentRecyclerAdapter = bookContentRecyclerAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.book_extra_services_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder)holder;
        setupExtraService(viewHolder, position);
    }

    @Override
    public int getItemCount() {

        if (extraServices != null)
            return extraServices.length;

        return 0;
    }

    public List<HouseExtraService> getSelectedExtraServices() {
        return selectedExtraServices;
    }

    private void setupExtraService(final ViewHolder viewHolder, int position) {
        if (extraServices != null && extraServices.length > position) {
            final HouseExtraService extraService = extraServices[position];

            viewHolder.extraServiceTitleTextView.setText(extraService.getName());
            if (extraService.getPrice() != null) {
                String priceText = "";
                if (extraService.getPrice().getFee() >= 0)
                    priceText = String.valueOf(extraService.getPrice().getFee());
                if (extraService.getPrice().getCurrency() != null) {
                    priceText += " ";

                    switch (extraService.getPrice().getCurrency()) {
                        case "IRR":
                            priceText += context.getResources().getString(R.string.currency_rials);
                            break;
                        default:
                            priceText += context.getResources().getString(R.string.currency_rials);
                            break;
                    }
                }
                viewHolder.extraServicePriceTextView.setText(priceText);
            }

            if (extraService.getType() != null && extraService.getType().length() > 0) {

                switch (extraService.getType()) {

                    case "wifi":
                        break;
                    case "pool":
                        break;
                    default:
                        viewHolder.extraServiceTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                context.getResources().getDrawable(R.drawable.ic_action_filter_results), null);
                        break;
                }
            }

            viewHolder.extraServiceAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedExtraServices == null) {
                        selectedExtraServices = new ArrayList<HouseExtraService>();
                    }

                    boolean alreadyAdded = false;
                    Iterator<HouseExtraService> extraServicesIterator = selectedExtraServices.iterator();
                    while (extraServicesIterator.hasNext()) {
                        HouseExtraService houseExtraService = extraServicesIterator.next();
                        if (houseExtraService.getName() != null && houseExtraService.getName().equalsIgnoreCase(extraService.getName())) {
                            alreadyAdded = true;
                            extraServicesIterator.remove();
                            break;
                        }
                    }

                    if (alreadyAdded) {
                        viewHolder.extraServiceAddButton.setBackgroundResource(R.drawable.circle_bg_gray_border);
                    } else {
                        selectedExtraServices.add(extraService);
                        viewHolder.extraServiceAddButton.setBackgroundResource(R.drawable.circle_bg_orange);
                    }
                    if (bookContentRecyclerAdapter != null) {
                        bookContentRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView extraServiceTitleTextView;
        public TextView extraServicePriceTextView;
        public Button extraServiceAddButton;

        public ViewHolder(View view) {
            super(view);

            extraServiceTitleTextView = (TextView)view.findViewById(R.id.book_extra_services_item_title);
            extraServicePriceTextView = (TextView)view.findViewById(R.id.book_extra_services_item_price);
            extraServiceAddButton = (Button)view.findViewById(R.id.book_extra_services_item_addButton);
        }
    }
}
