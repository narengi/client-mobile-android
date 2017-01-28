package xyz.narengi.android.armin.view.fragments.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import xyz.narengi.android.R;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.presenter.main.ExplorePresenter;
import xyz.narengi.android.armin.utils.DividerItemDecoration;
import xyz.narengi.android.armin.utils.EndlessScrollingListener;
import xyz.narengi.android.armin.view.adapters.main.ExploreRecyclerViewAdapter;
import xyz.narengi.android.armin.view.interfaces.main.ExploreView;

/**
 * Created by arminghm on 1/25/17.
 */

public class ExploreFragment extends Fragment implements ExploreView, SwipeRefreshLayout
        .OnRefreshListener, View.OnClickListener, EndlessScrollingListener.OnLoadingListener {

    private static final String TAG = ExploreFragment.class.getSimpleName();

    private RecyclerView recyclerViewExplore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private ExploreRecyclerViewAdapter exploreRecyclerViewAdapter;
    private ImageButton imageButtonNavigationView;
    private ExplorePresenter explorePresenter;

    private ExplorerFragmentClickListener clickListener;

    public interface ExplorerFragmentClickListener {
        void onImageButtonNavigationViewClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.clickListener = (ExplorerFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() +
                    "must implement ExplorerFragmentClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageButtonNavigationView = (ImageButton) view.findViewById(R.id.ImageButtonNavigationView);
        imageButtonNavigationView.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayoutExplore);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerViewExplore = (RecyclerView) view.findViewById(R.id.RecyclerViewExplore);
        recyclerViewExplore.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewExplore.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        recyclerViewExplore.addItemDecoration(dividerItemDecoration);
        recyclerViewExplore.addOnScrollListener(new EndlessScrollingListener(layoutManager,this));

        if (explorePresenter == null) {
            explorePresenter = new ExplorePresenter(getActivity().getApplication());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        explorePresenter.attachView(this);
        explorePresenter.loadDataIntoView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        explorePresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        explorePresenter.destroy();
    }

    @Override
    public void showLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction("تلاش مجدد", this)
                    .setDuration(4000)
                    .show();
        }
    }

    @Override
    public void showLoadedData(ArrayList<HouseModel> houseModels) {
        if (exploreRecyclerViewAdapter == null) {
            exploreRecyclerViewAdapter = new ExploreRecyclerViewAdapter();
            exploreRecyclerViewAdapter.setHouseModels(houseModels);
            recyclerViewExplore.setAdapter(exploreRecyclerViewAdapter);
        }
        exploreRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        explorePresenter.loadData(true);
    }

    @Override
    public void onLoadMore() {
        // TODO: arminghm 1/29/17 We must test this process.
        explorePresenter.loadData(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImageButtonNavigationView:
                clickListener.onImageButtonNavigationViewClick();
                break;
            default:
                // This is for SnackBar reload action.
                // The false value determines that we want to reload
                // data from the current page.
                explorePresenter.loadData(false);
                break;
        }
    }
}
