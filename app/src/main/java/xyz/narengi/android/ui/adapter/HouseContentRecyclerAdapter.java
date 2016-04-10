package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.common.dto.HouseReview;
import xyz.narengi.android.ui.activity.HostActivity;
import xyz.narengi.android.ui.activity.HouseFeaturesActivity;
import xyz.narengi.android.ui.widget.LineDividerItemDecoration;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private House house;
    private Context context;

    public HouseContentRecyclerAdapter(Context context, House house) {
        this.context = context;
        this.house = house;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.house_title_info, viewGroup, false);
                viewHolder = new TitleInfoViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.house_map_image, viewGroup, false);
                viewHolder = new MapImageViewHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.house_specs, viewGroup, false);
                viewHolder = new HouseSpecsViewHolder(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.house_description, viewGroup, false);
                viewHolder = new DescriptionViewHolder(view);
                break;
            case 4:
                view = inflater.inflate(R.layout.house_features, viewGroup, false);
                viewHolder = new FeaturesViewHolder(view);
                break;
            case 5:
                view = inflater.inflate(R.layout.house_user_ratings, viewGroup, false);
                viewHolder = new UserRatingViewHolder(view);
                break;
            case 6:
                view = inflater.inflate(R.layout.house_reviews, viewGroup, false);
                viewHolder = new UserReviewsViewHolder(view);
                break;
            case 7:
                view = inflater.inflate(R.layout.house_buttons, viewGroup, false);
                viewHolder = new HouseButtonsViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                TitleInfoViewHolder titleInfoViewHolder = (TitleInfoViewHolder) viewHolder;
                setupTitleInfoLayout(titleInfoViewHolder);
                break;
            case 1:
                MapImageViewHolder mapImageViewHolder = (MapImageViewHolder) viewHolder;
                setHouseMapImage(mapImageViewHolder);
                break;
            case 2:
                HouseSpecsViewHolder specsViewHolder = (HouseSpecsViewHolder) viewHolder;
                setupSpecsLayout(specsViewHolder);
                break;
            case 3:
                DescriptionViewHolder descriptionViewHolder = (DescriptionViewHolder) viewHolder;
                setHouseDescription(descriptionViewHolder);
                break;
            case 4:
                FeaturesViewHolder featuresViewHolder = (FeaturesViewHolder) viewHolder;
                setupFeaturesLayout(featuresViewHolder);
                break;
            case 5:
                UserRatingViewHolder ratingViewHolder = (UserRatingViewHolder) viewHolder;
                setupUserRatingLayout(ratingViewHolder);
                break;
            case 6:
                UserReviewsViewHolder reviewsViewHolder = (UserReviewsViewHolder) viewHolder;
                setupUserReviewsLayout(reviewsViewHolder);
                break;
            case 7:
                HouseButtonsViewHolder buttonsViewHolder = (HouseButtonsViewHolder) viewHolder;
                setupButtonsLayout(buttonsViewHolder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setupTitleInfoLayout(TitleInfoViewHolder viewHolder) {

//        viewHolder.priceTextView.setText(house.getCost());
        viewHolder.cityTextView.setText(house.getCityName());
        viewHolder.summaryTextView.setText(house.getSummary());
        viewHolder.ratingTextView.setText("(" + house.getRating() + " رای" + ")");

        Drawable drawable = viewHolder.houseRatingBar.getProgressDrawable();
        drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);
    }

    private void setHouseMapImage(MapImageViewHolder viewHolder) {

        Picasso.with(context).load(buildMapImageUrl()).into(viewHolder.mapImageView);
    }

    private String buildMapImageUrl() {
        String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=";
        if (house.getPosition() != null) {
            house.getPosition().setLat(35.710139);
            house.getPosition().setLng(51.418049);
            String latLngString = String.valueOf(house.getPosition().getLat()) + "," + String.valueOf(house.getPosition().getLng());
            mapUrl = mapUrl + latLngString;
            mapUrl = mapUrl + "&zoom=14&size=";
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            mapUrl = mapUrl + String.valueOf(width) + "x" + String.valueOf(120);
            mapUrl = mapUrl + "&markers=color:red%7Clabel:C%7C";
            mapUrl = mapUrl + latLngString;
        }

        return mapUrl;
    }

    private void setupSpecsLayout(HouseSpecsViewHolder viewHolder) {

        if (house.getSpec() != null) {
            viewHolder.bedCountTextView.setText(context.getString(R.string.house_bed_count, house.getSpec().getBedCount()));
            viewHolder.guestCountTextView.setText(context.getString(R.string.house_guest_count, house.getSpec().getGuestCount()));
            viewHolder.bedroomCountTextView.setText(context.getString(R.string.house_bedroom_count, house.getSpec().getBedroomCount()));
        }

        if (house.getType() != null) {
            if (house.getType().equals("apartment")) {
                viewHolder.typeTextView.setText(context.getString(R.string.house_type_apartment));
            } else if (house.getType().equals("villa")) {
                viewHolder.typeTextView.setText(context.getString(R.string.house_type_villa));
            } else if (house.getType().equals("house")) {
                viewHolder.typeTextView.setText(context.getString(R.string.house_type_house));
            } else {
                viewHolder.typeTextView.setText(context.getString(R.string.house_type_house));
            }
        } else {
            viewHolder.typeTextView.setText(context.getString(R.string.house_type_house));
        }

        viewHolder.bedCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_action_maps_hotel), null, null);
        viewHolder.guestCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_action_social_person_outline), null, null);
        viewHolder.bedroomCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_action_maps_store_mall_directory), null, null);
        viewHolder.typeTextView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_action_action_account_balance), null, null);
    }

    private void setHouseDescription(DescriptionViewHolder viewHolder) {

        String description = house.getName() + ", " + house.getSummary() + ", " + house.getFeatureSummary();
        description += description;

        viewHolder.descriptionTextView.setText(description);
    }

    private void setupFeaturesLayout(FeaturesViewHolder viewHolder) {

        if (house.getFeatureList() != null) {

            LinearLayout featuresLayout = viewHolder.featuresLayout;
            featuresLayout.removeAllViews();

            if (house.getFeatureList().length <= 5) {

                float layoutWeight = featuresLayout.getWeightSum() / house.getFeatureList().length;
                for (HouseFeature houseFeature:house.getFeatureList()) {
                    TextView featureTextView = createHouseFeatureTextView(houseFeature, layoutWeight);
                    featuresLayout.addView(featureTextView);
                }
            } else {

                float layoutWeight = featuresLayout.getWeightSum() / 5;

                int extraFeatures = house.getFeatureList().length - 4;
                String buttonText = String.valueOf(extraFeatures) + "+";
                Button moreFeaturesButton = createMoreFeaturesButton(buttonText, layoutWeight);
                featuresLayout.addView(moreFeaturesButton);

                moreFeaturesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, HouseFeaturesActivity.class);
                        intent.putExtra("house", house);
                        context.startActivity(intent);
                    }
                });

                for (int i=0 ; i < 4 ; i++) {

                    HouseFeature houseFeature = house.getFeatureList()[i];
                    TextView featureTextView = createHouseFeatureTextView(houseFeature, layoutWeight);
                    featuresLayout.addView(featureTextView);
                }
            }
        }
    }

    private TextView createHouseFeatureTextView(HouseFeature houseFeature, float layoutWeight) {

        TextView featureTextView = new TextView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, layoutWeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(8, 8, 8, 8);

        featureTextView.setPadding(4, 4, 4, 4);
        featureTextView.setTextAppearance(context, android.R.style.TextAppearance_Small);
//        featureTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_size_small));
        featureTextView.setTextColor(Color.BLACK);
        featureTextView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        featureTextView.setGravity(Gravity.CENTER);
        featureTextView.setTypeface(featureTextView.getTypeface(), Typeface.BOLD);
        featureTextView.setText(houseFeature.getName());
        featureTextView.setLayoutParams(params);

        if (houseFeature.getType() != null) {
            switch (houseFeature.getType()) {
                case "furniture":
                    Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
                    drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case "oven":
                    drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                    drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case "internet":
                    drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_camera);
                    drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                default:
                    drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_agenda);
                    drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
            }
        } else {
            Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_email);
            drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
            featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }

        return featureTextView;
    }

    private Button createMoreFeaturesButton(String text, float layoutWeight) {

        Button moreFeaturesButton = new Button(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, layoutWeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(8, 8, 8, 8);

        moreFeaturesButton.setPadding(4, 4, 4, 4);
//        moreFeaturesButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_size_normal));
        moreFeaturesButton.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        moreFeaturesButton.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        moreFeaturesButton.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        moreFeaturesButton.setGravity(Gravity.CENTER);
        moreFeaturesButton.setTypeface(moreFeaturesButton.getTypeface(), Typeface.BOLD);
        moreFeaturesButton.setText(text);
        moreFeaturesButton.setLayoutParams(params);

        return moreFeaturesButton;
    }

    private void setupUserRatingLayout(UserRatingViewHolder viewHolder) {
        viewHolder.userRatingTextView.setText("بر اساس ۹ رای");
        viewHolder.userRatingTextView.setText(context.getString(R.string.house_user_rating, house.getRating()));
        Drawable drawable = viewHolder.userRatingBar.getProgressDrawable();
        drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);
    }

    private void setupUserReviewsLayout(UserReviewsViewHolder viewHolder) {

        // use a linear layout manager

//        HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        viewHolder.userReviewsRecyclerView.setLayoutManager(mLayoutManager);

        HouseReviewsRecyclerAdapter recyclerAdapter = new HouseReviewsRecyclerAdapter(context, house.getReviews());
        viewHolder.userReviewsRecyclerView.setAdapter(recyclerAdapter);

        viewHolder.userReviewsRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
    }

    private void setupButtonsLayout(HouseButtonsViewHolder viewHolder) {

        //TODO : initialize on click listeners
    }

    private void openHostActivity(String hostUrl) {
        Intent intent = new Intent(context, HostActivity.class);

        intent.putExtra("hostUrl", hostUrl);
        context.startActivity(intent);
    }

    public class TitleInfoViewHolder extends RecyclerView.ViewHolder {

//        public TextView priceTextView;
        public TextView cityTextView;
        public TextView summaryTextView;
        public TextView ratingTextView;
        public RatingBar houseRatingBar;

        public TitleInfoViewHolder(View view) {
            super(view);

//            priceTextView = (TextView)view.findViewById(R.id.house_price);
            cityTextView = (TextView)view.findViewById(R.id.house_city);
            summaryTextView = (TextView)view.findViewById(R.id.house_summary);
            ratingTextView = (TextView)view.findViewById(R.id.house_rating);
            houseRatingBar = (RatingBar)view.findViewById(R.id.house_ratingBar);
        }
    }

    public class MapImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView mapImageView;

        public MapImageViewHolder(View view) {
            super(view);

            mapImageView = (ImageView)view.findViewById(R.id.house_mapImage);
        }
    }

    public class HouseSpecsViewHolder extends RecyclerView.ViewHolder {

        public TextView bedCountTextView;
        public TextView guestCountTextView;
        public TextView bedroomCountTextView;
        public TextView typeTextView;


        public HouseSpecsViewHolder(View view) {
            super(view);

            bedCountTextView = (TextView)view.findViewById(R.id.house_bedCount);
            guestCountTextView = (TextView)view.findViewById(R.id.house_guestCount);
            bedroomCountTextView = (TextView)view.findViewById(R.id.house_bedroomCount);
            typeTextView = (TextView)view.findViewById(R.id.house_type);
        }
    }

    public class DescriptionViewHolder extends RecyclerView.ViewHolder {

        public TextView descriptionTextView;

        public DescriptionViewHolder(View view) {
            super(view);

            descriptionTextView = (TextView)view.findViewById(R.id.house_description);
        }
    }

    public class FeaturesViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout featuresLayout;

        public FeaturesViewHolder(View view) {
            super(view);

//            featuresLayout = (LinearLayout)view.findViewById(R.id.house_featuresLayout);
            if (view instanceof LinearLayout)
                featuresLayout = (LinearLayout)view;
        }
    }

    public class UserRatingViewHolder extends RecyclerView.ViewHolder {

        public TextView userRatingTextView;

        public RatingBar userRatingBar;

        public UserRatingViewHolder(View view) {
            super(view);

            userRatingTextView = (TextView)view.findViewById(R.id.house_userRating);
            userRatingBar = (RatingBar)view.findViewById(R.id.house_userRatingBar);
        }
    }

    public class UserReviewsViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView userReviewsRecyclerView;

        public UserReviewsViewHolder(View view) {
            super(view);

            userReviewsRecyclerView = (RecyclerView)view.findViewById(R.id.house_reviewsRecyclerView);
            userReviewsRecyclerView.setHasFixedSize(true);
            userReviewsRecyclerView.setNestedScrollingEnabled(false);

            HouseReview review = new HouseReview();
            review.setDate("");
            review.setImageUrl("");
            review.setRate(20);
            review.setReviewer("علی کريمی");
            review.setMessage("اين یک کامنت صرفا تستی می باشد. همين دیگه! مگه قرار بود چی باشه ها !؟ ای بابا ول مون کن داداش چی ميگی تو!");

//            HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            userReviewsRecyclerView.setLayoutManager(mLayoutManager);

            HouseReviewsRecyclerAdapter recyclerAdapter = new HouseReviewsRecyclerAdapter(context, new HouseReview[]{ review });
            userReviewsRecyclerView.setAdapter(recyclerAdapter);

            userReviewsRecyclerView.addItemDecoration(new LineDividerItemDecoration(context.getResources()));
        }
    }

    public class HouseButtonsViewHolder extends RecyclerView.ViewHolder {

        public Button hostProfileButton;
        public Button hostContactButton;
        public Button extraServicesButton;
        public Button termsButton;

        public HouseButtonsViewHolder(View view) {
            super(view);

            hostProfileButton = (Button)view.findViewById(R.id.house_hostProfileButton);
            hostContactButton = (Button)view.findViewById(R.id.house_hostContactButton);
            extraServicesButton = (Button)view.findViewById(R.id.house_extraServicesButton);
            termsButton = (Button)view.findViewById(R.id.house_termsButton);

            hostProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openHostActivity(house.getHost().getHostURL());
                }
            });
        }
    }
}
