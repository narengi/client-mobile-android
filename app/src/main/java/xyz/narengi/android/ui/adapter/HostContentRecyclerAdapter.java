package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseReview;
import xyz.narengi.android.ui.widget.HouseLinearLayoutManager;
import xyz.narengi.android.ui.widget.LineDividerItemDecoration;

/**
 * @author Siavash Mahmoudpour
 */
public class HostContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HostProfile hostProfile;
    private Context context;

    public HostContentRecyclerAdapter(Context context, HostProfile hostProfile) {
        this.context = context;
        this.hostProfile = hostProfile;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.host_title_info, parent, false);
                viewHolder = new HostTitleInfoViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.host_summary, parent, false);
                viewHolder = new HostSummaryViewHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.host_job_info, parent, false);
                viewHolder = new HostJobInfoViewHolder(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.host_reviews_caption, parent, false);
                viewHolder = new HostReviewsCaptionViewHolder(view);
                break;
            case 4:
                view = inflater.inflate(R.layout.host_reviews, parent, false);
                viewHolder = new HostUserReviewsViewHolder(view);
                break;
            case 5:
                view = inflater.inflate(R.layout.host_houses_caption, parent, false);
                viewHolder = new HostHousesCaptionViewHolder(view);
                break;
            case 6:
                view = inflater.inflate(R.layout.host_houses, parent, false);
                viewHolder = new HostHousesViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                HostTitleInfoViewHolder titleInfoViewHolder = (HostTitleInfoViewHolder) viewHolder;
                setupTitleInfoLayout(titleInfoViewHolder);
                break;
            case 1:
                HostSummaryViewHolder summaryViewHolder = (HostSummaryViewHolder) viewHolder;
                setupSummaryLayout(summaryViewHolder);
                break;
            case 2:
                HostJobInfoViewHolder jobInfoViewHolder = (HostJobInfoViewHolder) viewHolder;
                setupJobInfoLayout(jobInfoViewHolder);
                break;
            case 3:
                HostReviewsCaptionViewHolder reviewsCaptionViewHolder = (HostReviewsCaptionViewHolder) viewHolder;
                break;
            case 4:
                HostUserReviewsViewHolder reviewsViewHolder = (HostUserReviewsViewHolder) viewHolder;
                setupUserReviewsLayout(reviewsViewHolder);
                break;
            case 5:
                HostHousesCaptionViewHolder housesCaptionViewHolder = (HostHousesCaptionViewHolder) viewHolder;
                break;
            case 6:
                HostHousesViewHolder housesViewHolder = (HostHousesViewHolder) viewHolder;
                setupHousesLayout(housesViewHolder);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setupTitleInfoLayout(HostTitleInfoViewHolder viewHolder) {

        viewHolder.nameTextView.setText(hostProfile.getDisplayName());
        viewHolder.locationTextView.setText(hostProfile.getLocationText());
        viewHolder.memberFromTextView.setText(hostProfile.getMemberFrom());
    }

    private void setupSummaryLayout(HostSummaryViewHolder viewHolder) {

        viewHolder.summaryTextView.setText(hostProfile.getSummary());
    }

    private void setupJobInfoLayout(HostJobInfoViewHolder viewHolder) {

        viewHolder.jobTextView.setText(hostProfile.getJob());
    }

    private void setupUserReviewsLayout(HostUserReviewsViewHolder viewHolder) {

        // use a linear layout manager
//        CustomLinearLayoutManager mLayoutManager = new CustomLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        viewHolder.userReviewsRecyclerView.setLayoutManager(mLayoutManager);

        HouseReviewsRecyclerAdapter recyclerAdapter = new HouseReviewsRecyclerAdapter(context, hostProfile.getReviews());
        viewHolder.userReviewsRecyclerView.setAdapter(recyclerAdapter);

        viewHolder.userReviewsRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
    }

    private void setupHousesLayout(HostHousesViewHolder viewHolder) {

        HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.userHousesRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<House> duplicatedHouseList = new ArrayList<House>();
        for (int i=0 ; i < 3 ; i++) {
            duplicatedHouseList.addAll(Arrays.asList(hostProfile.getHouses()));
        }

        House[] houses = new House[duplicatedHouseList.size()];
        duplicatedHouseList.toArray(houses);

//        HostHousesRecyclerAdapter recyclerAdapter = new HostHousesRecyclerAdapter(context, hostProfile.getHouses());
        HostHousesRecyclerAdapter recyclerAdapter = new HostHousesRecyclerAdapter(context, houses);
        viewHolder.userHousesRecyclerView.setAdapter(recyclerAdapter);

//        viewHolder.userHousesRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
    }

    public class HostTitleInfoViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView locationTextView;
        public TextView memberFromTextView;

        public HostTitleInfoViewHolder(View view) {
            super(view);

            nameTextView = (TextView)view.findViewById(R.id.host_profile_name);
            locationTextView = (TextView)view.findViewById(R.id.host_profile_location);
            memberFromTextView = (TextView)view.findViewById(R.id.host_profile_member_from);
        }
    }

    public class HostSummaryViewHolder extends RecyclerView.ViewHolder {

        public TextView summaryTextView;

        public HostSummaryViewHolder(View view) {
            super(view);

            summaryTextView = (TextView)view.findViewById(R.id.host_profile_summary);
        }
    }

    public class HostJobInfoViewHolder extends RecyclerView.ViewHolder {

        public TextView jobTextView;

        public HostJobInfoViewHolder(View view) {
            super(view);

            jobTextView = (TextView)view.findViewById(R.id.host_profile_job);
        }
    }

    public class HostReviewsCaptionViewHolder extends RecyclerView.ViewHolder {

        public HostReviewsCaptionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HostUserReviewsViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView userReviewsRecyclerView;

        public HostUserReviewsViewHolder(View view) {
            super(view);

            userReviewsRecyclerView = (RecyclerView)view.findViewById(R.id.host_profile_reviewsRecyclerView);
            userReviewsRecyclerView.setHasFixedSize(true);
            userReviewsRecyclerView.setNestedScrollingEnabled(false);

            HouseReview review = new HouseReview();
            review.setDate("");
            review.setImageUrl("");
            review.setRate(20);
            review.setReviewer("علی کريمی");
            review.setMessage("اين یک کامنت صرفا تستی می باشد. همين دیگه! مگه قرار بود چی باشه ها !؟ ای بابا ول مون کن داداش چی ميگی تو!");

//            CustomLinearLayoutManager mLayoutManager = new CustomLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            userReviewsRecyclerView.setLayoutManager(mLayoutManager);

            HouseReviewsRecyclerAdapter recyclerAdapter = new HouseReviewsRecyclerAdapter(context, new HouseReview[]{ review });
            userReviewsRecyclerView.setAdapter(recyclerAdapter);

            userReviewsRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
        }
    }

    public class HostHousesCaptionViewHolder extends RecyclerView.ViewHolder {

        public HostHousesCaptionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HostHousesViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView userHousesRecyclerView;

        public HostHousesViewHolder(View view) {
            super(view);

            userHousesRecyclerView = (RecyclerView)view.findViewById(R.id.host_profile_housesRecyclerView);
            userHousesRecyclerView.setHasFixedSize(true);
            userHousesRecyclerView.setNestedScrollingEnabled(false);

//            HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
////            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            userHousesRecyclerView.setLayoutManager(mLayoutManager);
////
//            HostHousesRecyclerAdapter recyclerAdapter = new HostHousesRecyclerAdapter(context, new House[0]);
//            userHousesRecyclerView.setAdapter(recyclerAdapter);
//
//            userHousesRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
        }
    }
}
