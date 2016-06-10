package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;

/**
 * @author Siavash Mahmoudpour
 */
public class HostHousesContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private House[] houses;


    public HostHousesContentRecyclerAdapter(Context context, House[] houses) {
        this.context = context;
        this.houses = houses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (houses == null || houses.length == 0) {
            View view = inflater.inflate(R.layout.host_houses_empty_data, parent, false);
            viewHolder = new EmptyDataViewHolder(view);
        } else {
            viewHolder = null;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (houses != null && houses.length > 0) {

        } else {
            EmptyDataViewHolder emptyDataViewHolder = (EmptyDataViewHolder)viewHolder;
        }
    }

    @Override
    public int getItemCount() {

        if (houses != null && houses.length > 0)
            return houses.length;
        return 1;
    }


    public class EmptyDataViewHolder extends RecyclerView.ViewHolder {

        public EmptyDataViewHolder(View itemView) {
            super(itemView);
        }
    }
}
