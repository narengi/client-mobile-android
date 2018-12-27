package xyz.narengi.android.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.ui.adapter.FeatureListArrayAdapter;


/**
 * @author Siavash Mahmoudpour
 */
public class HouseFeaturesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_features);

        if (getIntent() != null && getIntent().getSerializableExtra("house") != null) {
            Serializable houseSerializable = getIntent().getSerializableExtra("house");
            if (houseSerializable instanceof House) {
                House house = (House) houseSerializable;
                if (house.getFeatureList() != null)
                    setupFeaturesList(house);
            }
        }

        Button closeButton = (Button)findViewById(R.id.house_features_closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setupFeaturesList(House house) {

        ListView featuresListView = (ListView)findViewById(R.id.house_features_list);
        FeatureListArrayAdapter featuresArrayAdapter = new FeatureListArrayAdapter(this, R.layout.house_features_list_item,
                house.getFeatureList());

        featuresListView.setAdapter(featuresArrayAdapter);
        featuresListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

}
