package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.Location;
import xyz.narengi.android.common.dto.ProvinceCity;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.SpinnerArrayAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseInfoEntryFragment extends HouseEntryBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText titleEditText;
    private EditText summaryEditText;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private EditText addressEditText;

    private Map<String,ProvinceCity[]> provincesMap;

    public HouseInfoEntryFragment() {
        // Required empty public constructor
        setEntryMode(EntryMode.ADD);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HouseInfoEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseInfoEntryFragment newInstance(String param1, String param2) {
        HouseInfoEntryFragment fragment = new HouseInfoEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_info_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view == null)
            return;

        initViews(view);
        getProvinces();
        if (super.getHouse() != null) {
            titleEditText.setText(super.getHouse().getName());
            summaryEditText.setText(super.getHouse().getSummary());
            addressEditText.setText(super.getHouse().getAddress());
        }

        TextView titleTextView = (TextView)view.findViewById(R.id.house_info_entry_title);
        if (titleTextView != null)
            titleTextView.requestFocus();

        Button nextButton = (Button)view.findViewById(R.id.house_info_entry_nextButton);

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
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                break;
        }
    }

    private void initViews(View view) {

        titleEditText = (EditText) view.findViewById(R.id.house_info_entry_title);
        summaryEditText = (EditText) view.findViewById(R.id.house_info_entry_summary);
        provinceSpinner = (Spinner) view.findViewById(R.id.house_info_entry_province);
        citySpinner = (Spinner) view.findViewById(R.id.house_info_entry_city);
        addressEditText = (EditText) view.findViewById(R.id.house_info_entry_address);

//        SpinnerArrayAdapter<String> provinceArrayAdapter = new SpinnerArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
//                getResources().getStringArray(R.array.house_entry_province_array)) {
        SpinnerArrayAdapter<String> provinceArrayAdapter = new SpinnerArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.house_entry_province_array)) {

            public View getView(int position, View convertView,ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_province_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
//
//                ((TextView)v.findViewById(android.R.id.text1)).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                ((TextView) v).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_province_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                    ((TextView) v).setHint(R.string.house_info_entry_province_hint);
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
                return v;
            }
        };

//        SpinnerArrayAdapter<CharSequence> provinceArrayAdapter = new SpinnerArrayAdapter<CharSequence>(getActivity(),
//                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.house_entry_province_array));
        provinceArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
        provinceSpinner.setAdapter(provinceArrayAdapter);
        provinceSpinner.setPromptId(R.string.house_info_entry_province_hint);

//        SpinnerArrayAdapter<String> cityArrayAdapter = new SpinnerArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
//                getResources().getStringArray(R.array.house_entry_city_array)) {
        SpinnerArrayAdapter<String> cityArrayAdapter = new SpinnerArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.house_entry_city_array)) {

            public View getView(int position, View convertView,ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_city_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
//
//                ((TextView)v.findViewById(android.R.id.text1)).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                ((TextView) v).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_city_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                    ((TextView) v).setHint(R.string.house_info_entry_city_hint);
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
                return v;
            }
        };

//        SpinnerArrayAdapter<CharSequence> cityArrayAdapter = new SpinnerArrayAdapter<CharSequence>(getActivity(),
//                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.house_entry_city_array));
        cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
        citySpinner.setAdapter(cityArrayAdapter);
        citySpinner.setPromptId(R.string.house_info_entry_city_hint);
    }

    @Override
    public void setHouse(House house) {
        super.setHouse(house);
    }

    @Override
    public House getHouse() {
        House house = super.getHouse();
        if (house == null)
            house = new House();

        house.setName(titleEditText.getText().toString());
        house.setSummary(summaryEditText.getText().toString());
        house.setAddress(addressEditText.getText().toString());
        Location location = new Location();
        location.setCity(citySpinner.getSelectedItem().toString());
        location.setProvince(provinceSpinner.getSelectedItem().toString());
        house.setLocation(location);

        return house;
    }

    private void getProvinces() {

        Gson gson = new GsonBuilder().create();

        final House house = super.getHouse();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<Map<String,ProvinceCity[]>> call = apiEndpoints.getProvinces();
        call.enqueue(new Callback<Map<String, ProvinceCity[]>>() {
            @Override
            public void onResponse(Response<Map<String, ProvinceCity[]>> response, Retrofit retrofit) {
                int statusCode = response.code();
                provincesMap = response.body();
                if (provincesMap != null && !provincesMap.isEmpty()) {
                    initProvinceSpinner(provincesMap, house);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initProvinceSpinner(final Map<String,ProvinceCity[]> provincesMap, final House house) {

        if (provinceSpinner == null || citySpinner == null || provincesMap == null || provincesMap.isEmpty())
            return;

        final String[] provinceArray = new String[provincesMap.keySet().size()];
        provincesMap.keySet().toArray(provinceArray);

        SpinnerArrayAdapter<String> provinceArrayAdapter = new SpinnerArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,
                provinceArray) {

            public View getView(int position, View convertView,ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_province_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
//
//                ((TextView)v.findViewById(android.R.id.text1)).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                ((TextView) v).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
//                if (position == getCount()) {
//                    ((TextView) v).setText(R.string.house_info_entry_province_hint);
//                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                    ((TextView) v).setHint(R.string.house_info_entry_province_hint);
//                } else {
//                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                }
                return v;
            }
        };

//        SpinnerArrayAdapter<CharSequence> provinceArrayAdapter = new SpinnerArrayAdapter<CharSequence>(getActivity(),
//                android.R.layout.simple_spinner_item, provinceArray);
        provinceArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
        provinceSpinner.setAdapter(provinceArrayAdapter);
        provinceSpinner.setPromptId(R.string.house_info_entry_province_hint);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (provinceArray.length > position) {
                    String province = provinceArray[position];
                    ProvinceCity[] cityArray = provincesMap.get(province);
                    if (cityArray != null && cityArray.length > 0) {

                        SpinnerArrayAdapter<ProvinceCity> cityArrayAdapter = new SpinnerArrayAdapter<ProvinceCity>(getActivity(), R.layout.simple_spinner_item,
                                cityArray) {

                            public View getView(int position, View convertView, ViewGroup parent) {

                                View v = super.getView(position, convertView, parent);
//                                if (position == getCount()) {
//                                    ((TextView) v).setText(R.string.house_info_entry_city_hint);
//                                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                                } else {
//                                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                                }
//
//                                ((TextView)v.findViewById(android.R.id.text1)).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                                ((TextView) v).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                                return v;

                            }

                            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                                View v = super.getDropDownView(position, convertView, parent);
                                ((TextView) v).setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//                                if (position == getCount()) {
//                                    ((TextView) v).setText(R.string.house_info_entry_city_hint);
//                                    ((TextView) v).setTextColor(getResources().getColor(android.R.color.darker_gray));
//                                    ((TextView) v).setHint(R.string.house_info_entry_city_hint);
//                                } else {
//                                    ((TextView) v).setTextColor(getResources().getColor(R.color.text_gray_dark));
//                                }
                                return v;
                            }
                        };

//                        SpinnerArrayAdapter<ProvinceCity> cityArrayAdapter = new SpinnerArrayAdapter<ProvinceCity>(getActivity(),
//                                android.R.layout.simple_spinner_item, cityArray);
                        cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_rtl);
                        citySpinner.setAdapter(cityArrayAdapter);
                        citySpinner.setPromptId(R.string.house_info_entry_city_hint);

                        if (house != null && house.getLocation() != null && house.getLocation().getCity() != null &&
                                house.getLocation().getCity().length() > 0 ) {
                            for (int i=0 ; i < cityArray.length ; i++) {
                                ProvinceCity provinceCity = cityArray[i];
                                if (house.getLocation().getCity().equalsIgnoreCase(provinceCity.getCity())) {
                                    citySpinner.setSelection(i);
                                    citySpinner.invalidate();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!provincesMap.isEmpty() && house != null && house.getLocation() != null && (house.getLocation().getCity() != null ||
            house.getLocation().getProvince() != null)) {

            ProvinceCity[] cityArray = null;

            if (house.getLocation().getProvince() != null && house.getLocation().getProvince().length() > 0) {
                if (provincesMap.containsKey(house.getLocation().getProvince())) {
                    provincesMap.keySet().toArray(provinceArray);
                    for (int i=0 ; i < provinceArray.length ; i++) {
                        if (house.getLocation().getProvince().equalsIgnoreCase(provinceArray[i])) {
                            provinceSpinner.setSelection(i);
                            provinceSpinner.invalidate();
                            cityArray = provincesMap.get(house.getLocation().getProvince());
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean validate() {
        return super.validate();
    }
}
