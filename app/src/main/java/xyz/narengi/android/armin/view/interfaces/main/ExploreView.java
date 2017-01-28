package xyz.narengi.android.armin.view.interfaces.main;

import java.util.ArrayList;

import xyz.narengi.android.armin.model.network.pojo.HouseModel;
import xyz.narengi.android.armin.view.interfaces.BaseView;

/**
 * Created by arminghm on 1/25/17.
 */

public interface ExploreView extends BaseView {

    void showLoadedData(ArrayList<HouseModel> houseModelsw);
}
