package xyz.narengi.android.armin.view.interfaces.profile;

import xyz.narengi.android.armin.model.network.pojo.UserModel;
import xyz.narengi.android.armin.view.interfaces.BaseView;

/**
 * Created by arminghm on 1/30/17.
 */

public interface ProfileDetailsView extends BaseView{

    void loadProfileDetails(UserModel userModel);
}
