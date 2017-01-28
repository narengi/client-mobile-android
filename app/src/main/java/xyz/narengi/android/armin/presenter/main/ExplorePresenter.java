package xyz.narengi.android.armin.presenter.main;


import android.app.Application;
import android.os.Handler;

import java.util.ArrayList;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.RetrofitApiEndpoints;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.interfaces.main.ExploreView;
import xyz.narengi.android.ui.NarengiApplication;

/**
 * Created by arminghm on 1/25/17.
 */

public class ExplorePresenter {

    @Inject
    Retrofit retrofit;
    private Call<ArrayList<HouseModel>> callGetHouses;
    private ExploreView exploreView;
    private ArrayList<HouseModel> houseModels;
    private boolean loading;
    private int curentPage = 0;

    public ExplorePresenter(Application application) {
        ((NarengiApplication) application).getNetComponent().inject(this);
    }

    public void attachView(ExploreView exploreView) {
        this.exploreView = exploreView;
    }

    public void detachView() {
        exploreView = null;
    }

    public void destroy() {
        //callGetHouses.cancel();
    }

    public void loadDataIntoView() {
        if (houseModels != null && exploreView != null) {
            exploreView.showLoading(loading);
            exploreView.showLoadedData(houseModels);
        } else {
            // HouseModels array is not instantiated, we should reload data from net.
            loadData(true);
        }
    }

    public void loadData(boolean freshLoading) {

        if (freshLoading) {
            // It means that we must reload data.
            curentPage = 0;
        }

        //region Network Api
        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        // TODO: arminghm 1/27/17 This should be explore API not search :|
        Call<ArrayList<HouseModel>> call = apiEndpoints.search("", 20, 1);
        call.enqueue(new Callback<ArrayList<HouseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<HouseModel>> call, Response<ArrayList<HouseModel>> response) {
                ArrayList<HouseModel> houseModels = response.body();
            }

            @Override
            public void onFailure(Call<ArrayList<HouseModel>> call, Throwable t) {
                exploreView.showError("NetworkError");
            }
        });
        //endregion

        //region Temporary loading
        // TODO: arminghm 1/29/17 Remove this block.
        loading = true;
        exploreView.showLoading(true);
        if (houseModels != null && houseModels.size() != 0) {
            // this line makes the recyclerview scrolling ArrayOutOfBoundException
            // when you clear the array, and recyclerview is showing the items, when
            // you scroll, some items at the end of the array is not yet available.
            houseModels.clear();
        }
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                if (houseModels == null) {
                    houseModels = new ArrayList<>(20);
                }
                ArrayList<String> images = new ArrayList<String>(3);
                images.add("/medias/get/e048535a-408e-4ea4-9a32-9a0c1d131f1f");
                images.add("/medias/get/e048535a-408e-4ea4-9a32-9a0c1d131f1f");
                images.add("/medias/get/e048535a-408e-4ea4-9a32-9a0c1d131f1f");
                HouseModel houseModel;
                for (int i = 0; i < 20; i++) {
                    houseModel = new HouseModel();
                    houseModel.setName("خانه ی من ");
                    houseModel.setDescription("این خانه خیلی خوب است و من دارم به فنا میرم با این کد چرااا خب :))");
                    houseModel.setPriceRent("۴۵۵۰۰۰ هزار تومان");
                    houseModel.setImages(images);
                    houseModels.add(houseModel);
                }
                loading = false;
                loadDataIntoView();
            }
        }, 3000);
        //endregion
    }

}
