package xyz.narengi.android.armin.presenter.main;

import android.app.Application;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.interfaces.main.UserPlacesForHostingView;
import xyz.narengi.android.ui.NarengiApplication;

/**
 * Created by arminghm on 1/30/17.
 */

public class UserPlacesForHostingPresenter {

    @Inject
    Retrofit retrofit;
    UserPlacesForHostingView userPlacesForHostingView;
    ArrayList<HouseModel> houseModels;

    public UserPlacesForHostingPresenter(Application application) {
        ((NarengiApplication) application).getNetComponent().inject(this);
    }

    public void attachView(UserPlacesForHostingView userPlacesForHostingView) {
        this.userPlacesForHostingView = userPlacesForHostingView;
    }

    public void detachView() {
        userPlacesForHostingView = null;
    }

    public void destroy() {
        // TODO: arminghm 1/30/17 Cancel API call.
    }

    public void loadDataIntoView() {
        userPlacesForHostingView.showLoadedData(houseModels);
    }
}
