package xyz.narengi.android.armin.presenter.profile;

import android.app.Application;
import android.os.Handler;

import java.util.ArrayList;
import java.util.TimerTask;

import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.model.network.pojo.UserModel;
import xyz.narengi.android.armin.view.interfaces.profile.ProfileDetailsView;
import xyz.narengi.android.ui.NarengiApplication;

/**
 * Created by arminghm on 1/30/17.
 */

public class ProfileDetailsPresenter {

    private ProfileDetailsView profileDetailsView;
    private UserModel userModel;
    private boolean loading;
    public ProfileDetailsPresenter(Application application) {
        ((NarengiApplication) application).getNetComponent().inject(this);
    }


    public void attachView(ProfileDetailsView profileDetailsView) {
        this.profileDetailsView = profileDetailsView;
    }

    public void detachView() {
        profileDetailsView = null;
    }

    public void destroy() {

    }

    public void loadProfileDataIntoView() {
        if (userModel != null && profileDetailsView != null) {
            profileDetailsView.showLoading(loading);
            profileDetailsView.loadProfileDetails(userModel);
        } else {
            loadData();
        }
    }

    public void loadData() {
        // TODO: arminghm 1/31/17 Get profile details from an API call.
        //region Temporary loading
        // TODO: arminghm 1/29/17 Remove this block.
        loading = true;
        profileDetailsView.showLoading(true);
        userModel = new UserModel();
        userModel.setFullName("آرمین قریشی");
        userModel.setAboutMe("من بر روی برنامه اندروید نارنگی کار می کنم");
        userModel.setLocation("ایران");
        userModel.setAvatar("http://android.suvenconsultants.com/newimage/android-developer2.png");
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                if (userModel.getHouseModels() == null) {
                    userModel.setHouseModels(new ArrayList<HouseModel>(20));
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
                    userModel.getHouseModels().add(houseModel);
                }
                loading = false;
                if (profileDetailsView != null) {
                    profileDetailsView.showLoading(loading);
                    profileDetailsView.loadProfileDetails(userModel);
                }
            }
        }, 3000);
        //endregion
    }
}
