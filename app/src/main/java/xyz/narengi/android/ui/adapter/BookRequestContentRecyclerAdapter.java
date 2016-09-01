package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookRequest;

/**
 * @author Siavash Mahmoudpour
 */
public class BookRequestContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BookRequest bookRequest;
    private Context context;

    public BookRequestContentRecyclerAdapter(Context context, BookRequest bookRequest) {
        this.context = context;
        this.bookRequest = bookRequest;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}