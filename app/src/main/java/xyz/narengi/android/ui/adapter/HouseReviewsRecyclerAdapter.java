package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseReview;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseReviewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private HouseReview[] houseReviews;

    public HouseReviewsRecyclerAdapter(Context context, HouseReview[] houseReviews) {
        this.context = context;
        this.houseReviews = houseReviews;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.house_reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;

        if (houseReviews != null && houseReviews.length > position) {
            HouseReview review = houseReviews[position];
//            viewHolder.houseReviewerTextView.setText(review.getReviewer() + ":");
//            viewHolder.houseReviewTextView.setText(review.getMessage());

            Spannable word = new SpannableString(review.getReviewer() + ": ");

            word.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.green_dark)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.houseReviewerTextView.setText(word);
            Spannable wordTwo = new SpannableString(review.getMessage());

            wordTwo.setSpan(new ForegroundColorSpan(context.getResources().getColor(android.R.color.tertiary_text_dark)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.houseReviewerTextView.append(wordTwo);

        }
    }

    @Override
    public int getItemCount() {
        if (this.houseReviews != null)
            return this.houseReviews.length;
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView houseReviewerTextView;
        public TextView houseReviewTextView;

        public ViewHolder(View view) {
            super(view);

            houseReviewerTextView = (TextView)view.findViewById(R.id.house_reviews_item_reviewer);
//            houseReviewTextView = (TextView)view.findViewById(R.id.house_reviews_item_review);
        }
    }
}
