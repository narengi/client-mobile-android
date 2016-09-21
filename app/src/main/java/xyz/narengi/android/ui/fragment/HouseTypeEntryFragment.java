package xyz.narengi.android.ui.fragment;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseTypeEntryFragment extends HouseEntryBaseFragment {

    private Map<String, String>[] houseTypes;
    private RadioGroup houseTypeRadioGroup;


    public HouseTypeEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_type_entry, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        houseTypeRadioGroup = (RadioGroup) view.findViewById(R.id.house_type_entry_radioGroup);

        getHouseTypes();

        Button nextButton = (Button) view.findViewById(R.id.house_type_entry_nextButton);
        Button previousButton = (Button) view.findViewById(R.id.house_type_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
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
                            if (validate() && getOnInteractionListener() != null) {
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
                break;
        }
    }

    private void getHouseTypes() {
        final House house = super.getHouse();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<Map<String, String>[]> call = apiEndpoints.getHouseTypes();

        call.enqueue(new Callback<Map<String, String>[]>() {
            @Override
            public void onResponse(Call<Map<String, String>[]> call, Response<Map<String, String>[]> response) {
                int statusCode = response.code();
                houseTypes = response.body();
                if (houseTypes != null && houseTypes.length > 0) {
                    initHouseTypeRadioGroup(houseTypes, house);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>[]> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initHouseTypeRadioGroup(final Map<String, String>[] houseTypes, House house) {
        if (houseTypes == null || houseTypes.length == 0)
            return;

        int counter = 1;
        for (Map<String, String> houseTypeMap : houseTypes) {
            if (houseTypeMap == null || houseTypeMap.isEmpty())
                continue;

            String[] keysArray = new String[houseTypeMap.keySet().size()];
            houseTypeMap.keySet().toArray(keysArray);
            String houseTypeKey = keysArray[0];
            String houseTypeValue = houseTypeMap.get(houseTypeKey);
            int id = 100000 + counter;
            RadioButton houseTypeRadioButton = createHouseTypeRadioButton(houseTypeKey, houseTypeValue, id);
            if (houseTypeRadioButton != null)
                houseTypeRadioGroup.addView(houseTypeRadioButton);

            View lineDividerView = createLineDividerView();
            if (lineDividerView != null)
                houseTypeRadioGroup.addView(lineDividerView);
            if (counter == 1 && (getHouse() == null || getHouse().getType() == null) && houseTypeRadioButton != null)
                houseTypeRadioGroup.check(id);
            counter++;
        }

        houseTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                RadioButton checkedRadioButton = null;

                for (int i = 0; i < houseTypeRadioGroup.getChildCount(); i++) {
                    View view = houseTypeRadioGroup.getChildAt(i);
                    if (view != null && view instanceof RadioButton && view.getId() == id) {
                        checkedRadioButton = (RadioButton) view;
                    }
                }

                if (checkedRadioButton != null) {
                    for (Map<String, String> houseTypeMap : houseTypes) {

                        String[] keysArray = new String[houseTypeMap.keySet().size()];
                        houseTypeMap.keySet().toArray(keysArray);
                        String houseTypeKey = keysArray[0];
                        String houseTypeValue = houseTypeMap.get(houseTypeKey);

                        String radioButtonText = (String) checkedRadioButton.getText();
                        if (houseTypeValue != null && houseTypeValue.equalsIgnoreCase(radioButtonText) && getHouse() != null) {
                            getHouse().setType(houseTypeKey);
                        }
                    }
                }
            }
        });

        if (getHouse() != null && getHouse().getType() != null && getHouse().getType().length() > 0) {

            for (Map<String, String> houseTypeMap : houseTypes) {

                String[] keysArray = new String[houseTypeMap.keySet().size()];
                houseTypeMap.keySet().toArray(keysArray);
                String houseTypeKey = keysArray[0];

                if (houseTypeKey != null && houseTypeKey.equalsIgnoreCase(house.getType())) {
                    String houseTypeValue = houseTypeMap.get(houseTypeKey);
                    if (houseTypeValue != null) {
                        for (int i = 0; i < houseTypeRadioGroup.getChildCount(); i++) {
                            View view = houseTypeRadioGroup.getChildAt(i);
                            if (view != null && view instanceof RadioButton && ((RadioButton) view).getText() != null &&
                                    ((RadioButton) view).getText().equals(houseTypeValue)) {
                                houseTypeRadioGroup.check(view.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    private RadioButton createHouseTypeRadioButton(String houseTypeKey, String houseTypeValue, int id) {
        if (getActivity() == null)
            return null;

        RadioButton radioButton = new RadioButton(getActivity());

        radioButton.setId(id);

        radioButton.setBackgroundDrawable(null);
        radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
        radioButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.house_type_radio_button_bg_left),
                null, getResources().getDrawable(R.drawable.house_type_radio_button_bg_right), null);

        radioButton.setText(houseTypeValue);
        radioButton.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
        radioButton.setTextColor(getResources().getColorStateList(R.color.house_type_radio_button_text_color));
        radioButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        int marginDpValue = 10; // margin in dips
        float density = getActivity().getResources().getDisplayMetrics().density;
        int marginPxValue = (int) (marginDpValue * density); // margin in pixels

//        radioButton.setPadding(marginPxValue, marginPxValue, marginPxValue, marginPxValue);
        layoutParams.setMargins(marginPxValue, marginPxValue, marginPxValue, marginPxValue);
//        layoutParams.rightMargin = marginPxValue;
//        layoutParams.topMargin = marginPxValue;
//        layoutParams.leftMargin = marginPxValue;
//        layoutParams.bottomMargin = marginPxValue;
        radioButton.setLayoutParams(layoutParams);

        return radioButton;
    }

    private View createLineDividerView() {
        if (getActivity() == null)
            return null;

        View lineDividerView = new View(getActivity());
        int heightDpValue = 1; // margin in dips
        float density = getActivity().getResources().getDisplayMetrics().density;
        int heightPxValue = (int) (heightDpValue * density); // margin in pixels
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPxValue);
        lineDividerView.setLayoutParams(layoutParams);

        lineDividerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        return lineDividerView;
    }
}
