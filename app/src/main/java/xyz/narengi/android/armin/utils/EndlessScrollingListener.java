package xyz.narengi.android.armin.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * This class takes care about {@link RecyclerView} scrolling.
 *<p>
 *     It has an interface, {@link OnLoadingListener} which will be called
 *     when the {@link RecyclerView} riches its end and loading flag is false.
 *</p>
 *
 * Created by arminghm on 1/28/17.
 */

public class EndlessScrollingListener extends RecyclerView.OnScrollListener {
    private static final String TAG = EndlessScrollingListener.class.getSimpleName();

    public interface OnLoadingListener {
        void onLoadMore();
    }

    private OnLoadingListener onLoadingListener;
    private LinearLayoutManager layoutManager;

    private boolean loading = false;

    public EndlessScrollingListener(LinearLayoutManager layoutManager,
                                    OnLoadingListener onLoadingListener) {
        super();
        this.layoutManager = layoutManager;
        this.onLoadingListener = onLoadingListener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        // 0 is idle, 1 is dragging, 2 is settling
        //Log.d(TAG, "onScrollStateChanged: " + newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (loading &&
                layoutManager.findLastVisibleItemPosition() != layoutManager.getItemCount() - 1) {
            loading = false;
        } else if (!loading &&
                layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) {
            onLoadingListener.onLoadMore();
            loading = true;
        }

    }
}
