package xyz.narengi.android.armin.view.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.UserModel;
import xyz.narengi.android.armin.presenter.profile.ProfileDetailsPresenter;
import xyz.narengi.android.armin.view.adapters.profile.ProfileDetailsRecyclerViewAdapter;
import xyz.narengi.android.armin.view.interfaces.profile.ProfileDetailsView;

/**
 * Created by arminghm on 1/30/17.
 */

public class ProfileDetailsFragment extends Fragment implements ProfileDetailsView, View.OnClickListener {

    private static final String TAG = ProfileDetailsFragment.class.getSimpleName();

    private RecyclerView recyclerViewProfileDetails;
    private LinearLayoutManager linearLayoutManager;
    private ProfileDetailsRecyclerViewAdapter profileDetailsRecyclerViewAdapter;

    private ImageView imageViewUserAvatar;

    private TextView textViewUserFullName;
    private TextView textViewUserLocation;

    private ImageButton imageButtonBack;
    private ImageButton imageButtonLogout;
    private ImageButton imageButtonEditProfile;

    private ProfileDetailsPresenter profileDetailsPresenter;

    public static ProfileDetailsFragment newInstance() {

        Bundle args = new Bundle();

        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageButtonBack = (ImageButton) view.findViewById(R.id.ImageButtonBack);
        imageButtonEditProfile = (ImageButton) view.findViewById(R.id.ImageButtonEditProfile);
        imageButtonLogout = (ImageButton) view.findViewById(R.id.ImageButtonLogout);

        imageButtonBack.setOnClickListener(this);
        imageButtonLogout.setOnClickListener(this);
        imageButtonEditProfile.setOnClickListener(this);

        imageViewUserAvatar = (ImageView) view.findViewById(R.id.ImageViewUserAvatar);

        textViewUserFullName = (TextView) view.findViewById(R.id.TextViewUserFullName);
        textViewUserLocation = (TextView) view.findViewById(R.id.TextViewUserLocation);

        // TODO: arminghm 1/31/17 Consider that we may need lazy loading for user's houses.
        recyclerViewProfileDetails = (RecyclerView) view
                .findViewById(R.id.RecyclerViewProfileDetails);
        linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false
        );
        recyclerViewProfileDetails.setLayoutManager(linearLayoutManager);

        if (profileDetailsPresenter == null) {
            profileDetailsPresenter = new ProfileDetailsPresenter(getActivity().getApplication());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        profileDetailsPresenter.attachView(this);
        profileDetailsPresenter.loadProfileDataIntoView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileDetailsPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileDetailsPresenter.destroy();
    }

    @Override
    public void showLoading(boolean loading) {
        // TODO: arminghm 1/31/17 How to show a loading?
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("تلاش مجدد", this)
                    .show();
        }
    }

    @Override
    public void loadProfileDetails(UserModel userModel) {
        Log.d(TAG, "loadProfileDetails: called");
        Picasso.with(getContext())
                .load(userModel.getAvatar())
                .error(R.mipmap.ic_launcher)
                .into(imageViewUserAvatar);
        textViewUserFullName.setText(userModel.getFullName());
        textViewUserLocation.setText(userModel.getLocation());
        if (profileDetailsRecyclerViewAdapter == null) {
            profileDetailsRecyclerViewAdapter = new
                    ProfileDetailsRecyclerViewAdapter(userModel.getAboutMe());
            profileDetailsRecyclerViewAdapter.setHouseModels(userModel.getHouseModels());
            recyclerViewProfileDetails.setAdapter(profileDetailsRecyclerViewAdapter);
        }
        profileDetailsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImageButtonBack:
                break;
            case R.id.ImageButtonLogout:
                break;
            case R.id.ImageButtonEditProfile:
                break;
            default:
                // This is for Snackbar action.
                break;
        }
    }
}
