package xyz.narengi.android.ui.fragment;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.activity.AddHouseActivity;
import xyz.narengi.android.ui.activity.EditHouseDetailActivity;
import xyz.narengi.android.ui.widget.NestedScrollingListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HouseFeaturesEntryFragment extends HouseEntryBaseFragment {


    private Map<String, String>[] houseFeatures;
    private NestedScrollingListView featuresListView;
    private List<String> selectedFeatures;
    private List<HouseFeature> selectedHouseFeatures;

    public HouseFeaturesEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_features_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        featuresListView = (NestedScrollingListView) view.findViewById(R.id.house_features_entry_featuresListView);
        featuresListView.setListener(new NestedScrollingListView.OnTouchListener() {
            @Override
            public void onTouch() {
                if (getActivity() != null) {
                    if (getActivity() instanceof AddHouseActivity) {
                        ((AddHouseActivity) getActivity()).requestDisallowInterceptTouchEvent(true);
                    } else if (getActivity() instanceof EditHouseDetailActivity) {
                        ((EditHouseDetailActivity) getActivity()).requestDisallowInterceptTouchEvent(true);
                    }
                }
            }
        });

        featuresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<HouseFeature> selectedHouseFeatures = getSelectedHouseFeatures();
                if (selectedHouseFeatures != null) {
                    HouseFeature[] houseFeatures = new HouseFeature[selectedHouseFeatures.size()];
                    selectedHouseFeatures.toArray(houseFeatures);
                    getHouse().setFeatureList(houseFeatures);
                }
            }
        });

        featuresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                CheckBox titleCheckBox = (CheckBox)view.findViewById(R.id.house_features_entry_item_title);
//                titleCheckBox.performClick();
//
//                HouseFeature houseFeature = (HouseFeature)adapterView.getAdapter().getItem(position);

//                Drawable rightDrawable = getRightDrawable(houseFeature.getType());
//                Drawable leftDrawable = getResources().getDrawable(R.drawable.ic_action_dash_mark);;
//                int textColor = getResources().getColor(R.color.text_gray_light);

                /*if (titleCheckBox.isChecked()) {
                    if (selectedFeatures == null)
                        selectedFeatures = new ArrayList<String>();
                    selectedFeatures.add(houseFeature.getType());*/

//                    rightDrawable.setColorFilter(getResources().getColor(R.color.orange_light), PorterDuff.Mode.SRC_ATOP);
//                    leftDrawable = getResources().getDrawable(R.drawable.ic_action_check_mark);
//                    textColor = getResources().getColor(R.color.orange_light);

                /*} else {
                    Iterator<String> iterator = selectedFeatures.iterator();
                    while (iterator.hasNext()) {
                        String featureType = iterator.next();
                        if (featureType.equalsIgnoreCase(houseFeature.getType())) {
                            iterator.remove();

//                            rightDrawable.setColorFilter(getResources().getColor(R.color.text_gray_light), PorterDuff.Mode.SRC_ATOP);
//                            leftDrawable = getResources().getDrawable(R.drawable.ic_action_dash_mark);
                        }
                    }
                }*/

//                titleCheckBox.setCompoundDrawablesWithIntrinsicBounds(null,
//                        null, rightDrawable, null);
//                titleCheckBox.setButtonDrawable(leftDrawable);
//                titleCheckBox.setTextColor(textColor);


                List<HouseFeature> selectedHouseFeatures = getSelectedHouseFeatures();
                if (selectedHouseFeatures != null) {
                    HouseFeature[] houseFeatures = new HouseFeature[selectedHouseFeatures.size()];
                    selectedHouseFeatures.toArray(houseFeatures);
                    getHouse().setFeatureList(houseFeatures);
                }
            }
        });

        getHouseFeatures();

        Button nextButton = (Button) view.findViewById(R.id.house_features_entry_nextButton);
        Button previousButton = (Button) view.findViewById(R.id.house_features_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
//                                List<HouseFeature> selectedHouseFeatures = getSelectedHouseFeatures();
//                                if (selectedHouseFeatures != null) {
//                                    HouseFeature[] houseFeatures = new HouseFeature[selectedHouseFeatures.size()];
//                                    selectedHouseFeatures.toArray(houseFeatures);
//                                    getHouse().setFeatureList(houseFeatures);
//                                }
                                getOnInteractionListener().onGoToNextSection(getHouse());
                            }
                        }
                    });
                }

                if (previousButton != null) {
                    previousButton.setVisibility(View.VISIBLE);
                    previousButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (getOnInteractionListener() != null) {
//                                List<HouseFeature> selectedHouseFeatures = getSelectedHouseFeatures();
//                                if (selectedHouseFeatures != null) {
//                                    HouseFeature[] houseFeatures = new HouseFeature[selectedHouseFeatures.size()];
//                                    selectedHouseFeatures.toArray(houseFeatures);
//                                    getHouse().setFeatureList(houseFeatures);
//                                }
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
                            }
                        }
                    });
                }
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                if (previousButton != null)
                    previousButton.setVisibility(View.GONE);

                if (featuresListView.getLayoutParams() != null) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) featuresListView.getLayoutParams();
                    layoutParams.bottomMargin = 10;
                    featuresListView.setLayoutParams(layoutParams);
                }

                break;
        }
    }

    private List<HouseFeature> getSelectedHouseFeatures() {
        selectedHouseFeatures = new ArrayList<HouseFeature>();
        SparseBooleanArray checked = featuresListView.getCheckedItemPositions();
        for (int i = 0; i < featuresListView.getCount(); i++)
            if (checked.get(i)) {
                Map<String, String> selectedFeatureMap = houseFeatures[i];
                HouseFeature houseFeature = new HouseFeature();
				Iterator<Map.Entry<String, String>> iterator = selectedFeatureMap.entrySet().iterator();
                Map.Entry<String, String> entry = iterator.next();
                houseFeature.setType(entry.getValue());

				entry = iterator.next();
                houseFeature.setName(entry.getValue());
                houseFeature.setAvailable(true);
                selectedHouseFeatures.add(houseFeature);
            }

        return selectedHouseFeatures;
    }

    private void getHouseFeatures() {

        final House house = super.getHouse();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<Map<String, String>[]> call = apiEndpoints.getHouseFeatures();

        call.enqueue(new Callback<Map<String, String>[]>() {
            @Override
            public void onResponse(Call<Map<String, String>[]> call, Response<Map<String, String>[]> response) {
				if (isAdded()) {
					int statusCode = response.code();
					houseFeatures = response.body();
					if (houseFeatures != null && houseFeatures.length > 0) {
						setupHouseFeaturesList(houseFeatures);
					}
				}
            }

            @Override
            public void onFailure(Call<Map<String, String>[]> call, Throwable t) {
				if (isAdded()) {
					t.printStackTrace();
				}
            }
        });
    }

    private void setupHouseFeaturesList(Map<String, String>[] houseFeatures) {

        FeatureListArrayAdapter featuresArrayAdapter = new FeatureListArrayAdapter(getActivity(), R.layout.house_features_entry_item,
                buildHouseFeatureArray(houseFeatures));

        HouseFeature[] houseFeatureArray = buildHouseFeatureArray(houseFeatures);
        final String[] titlesArray = new String[houseFeatureArray.length];
        final String[] typesArray = new String[houseFeatureArray.length];
        for (int i = 0; i < houseFeatureArray.length; i++) {
            titlesArray[i] = houseFeatureArray[i].getName();
            typesArray[i] = houseFeatureArray[i].getType();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.simple_list_item_multiple_choice, titlesArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                String type = typesArray[position];
//                Drawable rightDrawable = getRightDrawable(type);
//                SparseBooleanArray sparseBooleanArray = featuresListView.getCheckedItemPositions();
//                int key = sparseBooleanArray.keyAt(position);
//                boolean isChecked = sparseBooleanArray.valueAt(sparseBooleanArray.indexOfKey(key));
//                if (isChecked) {
//                    rightDrawable = getResources().getDrawable(R.drawable.ic_action_about_narengi);
//                }
//                ((CheckedTextView) v).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.house_type_radio_button_bg_left),
//                        null, rightDrawable, null);

                return v;
            }
        };
        featuresListView.setAdapter(adapter);
        featuresListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (getHouse() != null && getHouse().getFeatureList() != null) {
            for (int i = 0; i < houseFeatureArray.length; i++) {
                HouseFeature houseFeature = houseFeatureArray[i];
                for (HouseFeature selectedHouseFeature : getHouse().getFeatureList()) {
                    if (houseFeature.getType().equalsIgnoreCase(selectedHouseFeature.getType())) {
                        featuresListView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        }
    }

    private HouseFeature[] buildHouseFeatureArray(Map<String, String>[] houseFeaturesMapArray) {

        if (houseFeaturesMapArray == null)
            return null;

        HouseFeature[] houseFeatureArray = new HouseFeature[houseFeaturesMapArray.length];
        for (int i = 0; i < houseFeaturesMapArray.length; i++) {
            HouseFeature houseFeature = new HouseFeature();
            houseFeature.setAvailable(true);
            Map<String, String> houseFeatureMap = houseFeaturesMapArray[i];
            if (houseFeatureMap == null || houseFeatureMap.isEmpty())
                continue;
			Iterator<Map.Entry<String, String>> iterator = houseFeatureMap.entrySet().iterator();
            Map.Entry<String, String> entry = iterator.next();
            houseFeature.setType(entry.getValue());

			entry = iterator.next();
            houseFeature.setName(entry.getValue());

            houseFeatureArray[i] = houseFeature;
        }

        return houseFeatureArray;
    }

    private Drawable getRightDrawable(String houseFeatureType) {
        Drawable rightDrawable;
        if (houseFeatureType != null) {
            switch (houseFeatureType) {
                case "24hr-checkin":
                    rightDrawable = getResources().getDrawable(android.R.drawable.ic_menu_call);
                    break;
                case "air-conditioner":
                    rightDrawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                    break;
                case "breakfast":
                    rightDrawable = getResources().getDrawable(android.R.drawable.ic_menu_camera);
                    break;
                default:
//                    rightDrawable = getResources().getDrawable(R.drawable.house_type_radio_button_bg_right);
                    rightDrawable = getResources().getDrawable(android.R.drawable.ic_menu_compass);
                    break;

            }
        } else {
            rightDrawable = getResources().getDrawable(R.drawable.house_type_radio_button_bg_right);
        }

        return rightDrawable;
    }

    public class FeatureListArrayAdapter extends ArrayAdapter<HouseFeature> {

        private Context context;
        private int resource;
        private HouseFeature[] objects;


        public FeatureListArrayAdapter(Context context, int resource, HouseFeature[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            View rowView = convertView;
            //reuse views
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(resource, parent, false);
                //configure view holder

                viewHolder = new ViewHolder();
                viewHolder.titleCheckBox = (CheckBox) rowView.findViewById(R.id.house_features_entry_item_title);

                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }


            if (objects != null && position < objects.length) {
                final HouseFeature houseFeature = objects[position];

                viewHolder.titleCheckBox.setText(houseFeature.getName());
                boolean isChecked = false;

                if (selectedFeatures != null) {
                    for (String featureType : selectedFeatures) {
                        if (featureType.equalsIgnoreCase(houseFeature.getType())) {
                            isChecked = true;
                        }
                    }
                }
                viewHolder.titleCheckBox.setChecked(isChecked);
                viewHolder.titleCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (((CheckBox) view).isChecked()) {
                            if (selectedFeatures == null)
                                selectedFeatures = new ArrayList<String>();
                            selectedFeatures.add(houseFeature.getType());
                        } else {
                            Iterator<String> iterator = selectedFeatures.iterator();
                            while (iterator.hasNext()) {
                                String featureType = iterator.next();
                                if (featureType.equalsIgnoreCase(houseFeature.getType())) {
                                    iterator.remove();

                                }
                            }
                        }
                    }
                });

            }

            return rowView;
        }

        class ViewHolder {
            public CheckBox titleCheckBox;
        }
    }
}
