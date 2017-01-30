package xyz.narengi.android.armin.view.interfaces.main;

import java.util.ArrayList;

import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.interfaces.BaseView;

/**
 * Created by arminghm on 1/30/17.
 */

public interface UserPlacesForHostingView extends BaseView {
    void showLoadedData(ArrayList<HouseModel> houseModels);
}
