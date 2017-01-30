package xyz.narengi.android.armin.view.fragments.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.presenter.main.UserPlacesForHostingPresenter;
import xyz.narengi.android.armin.view.adapters.main.UserPlacesForHostingRecyclerViewAdapter;
import xyz.narengi.android.armin.view.interfaces.main.UserPlacesForHostingView;

/**
 * Created by arminghm on 1/30/17.
 */

public class UserPlacesForHostingFragment extends Fragment implements UserPlacesForHostingView,
        View.OnClickListener {


    private FloatingActionButton fabAddingHouseForHosting;
    private ImageButton imageButtonBack;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewPlaces;
    private UserPlacesForHostingRecyclerViewAdapter userPlacesForHostingRecyclerViewAdapter;

    private UserPlacesForHostingPresenter userPlacesForHostingPresenter;

    public static UserPlacesForHostingFragment newInstance() {

        Bundle args = new Bundle();

        UserPlacesForHostingFragment fragment = new UserPlacesForHostingFragment();
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
        return inflater.inflate(R.layout.fragment_user_places_for_hosting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.SwipeRefreshLayoutUserPlacesForHosting);

        linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false
        );
        recyclerViewPlaces = (RecyclerView) view
                .findViewById(R.id.RecyclerViewUserPlacesForHosting);
        recyclerViewPlaces.setLayoutManager(linearLayoutManager);

        fabAddingHouseForHosting = (FloatingActionButton) view
                .findViewById(R.id.FabAddingPlaceForHosting);
        imageButtonBack = (ImageButton) view.findViewById(R.id.ImageButtonBack);
        imageButtonBack.setOnClickListener(this);
        fabAddingHouseForHosting.setOnClickListener(this);

        if (userPlacesForHostingPresenter == null) {
            userPlacesForHostingPresenter = new
                    UserPlacesForHostingPresenter(getActivity().getApplication());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userPlacesForHostingPresenter.attachView(this);
        userPlacesForHostingPresenter.loadDataIntoView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userPlacesForHostingPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userPlacesForHostingPresenter.destroy();
    }

    @Override
    public void showLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void showLoadedData(ArrayList<HouseModel> houseModels) {
        if (userPlacesForHostingRecyclerViewAdapter == null) {
            userPlacesForHostingRecyclerViewAdapter = new UserPlacesForHostingRecyclerViewAdapter();
            userPlacesForHostingRecyclerViewAdapter.setHouseModels(houseModels);
            recyclerViewPlaces.setAdapter(userPlacesForHostingRecyclerViewAdapter);
        }
        userPlacesForHostingRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FabAddingPlaceForHosting:
                break;
            case R.id.ImageButtonBack:
                getActivity().onBackPressed();
                break;
        }
    }
}
