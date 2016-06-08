package xyz.narengi.android.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HousePrice;
import xyz.narengi.android.common.dto.HouseSpec;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseRoomEntryFragment extends HouseEntryBaseFragment {

    private TextView roomsCountTextView;
    private TextView bedsCountTextView;
    private Button addRoomButton;
    private Button removeRoomButton;
    private Button addBedButton;
    private Button removeBedButton;
    private EditText priceEditText;
    private int roomCount;
    private int bedCount;

    public HouseRoomEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_room_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {

        roomsCountTextView = (TextView)view.findViewById(R.id.house_room_entry_roomsCount);
        bedsCountTextView = (TextView)view.findViewById(R.id.house_room_entry_bedsCount);
        addRoomButton = (Button)view.findViewById(R.id.house_room_entry_addRoomButton);
        removeRoomButton = (Button)view.findViewById(R.id.house_room_entry_removeRoomButton);
        addBedButton = (Button)view.findViewById(R.id.house_room_entry_addBedButton);
        removeBedButton = (Button)view.findViewById(R.id.house_room_entry_removeBedButton);
        priceEditText = (EditText)view.findViewById(R.id.house_room_entry_price);

        if (getHouse() != null) {
            if (getHouse().getSpec() != null) {
                roomCount = getHouse().getSpec().getBedroomCount();
                bedCount = getHouse().getSpec().getBedCount();
            }

            if (getHouse().getPrice() != null && getHouse().getPrice().getPrice() > 0) {
                priceEditText.setText(String.valueOf((long)getHouse().getPrice().getPrice()));
            }
        }

        roomsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(roomCount)));
        bedsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(bedCount)));

        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCount++;
                roomsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(roomCount)));
                setRoomCount(roomCount);
            }
        });

        removeRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCount--;
                if (roomCount < 0) {
                    roomCount = 0;
                }
                roomsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(roomCount)));
                setRoomCount(roomCount);
            }
        });

        addBedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bedCount++;
                bedsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(bedCount)));
                setBedCount(bedCount);
            }
        });

        removeBedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bedCount--;
                if (bedCount < 0) {
                    bedCount = 0;
                }
                bedsCountTextView.setText(getString(R.string.house_room_entry_counter_caption, String.valueOf(bedCount)));
                setBedCount(bedCount);
            }
        });

        Button nextButton = (Button)view.findViewById(R.id.house_room_entry_nextButton);
        Button previousButton = (Button)view.findViewById(R.id.house_room_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                setPrice(priceEditText.getText().toString());
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
                                setPrice(priceEditText.getText().toString());
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
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

    private void setBedCount(int bedCount) {
        if (getHouse() != null) {
            if (getHouse().getSpec() == null) {
                HouseSpec houseSpec = new HouseSpec();
                getHouse().setSpec(houseSpec);
            }

            getHouse().getSpec().setBedCount(bedCount);
        }
    }

    private void setRoomCount(int roomCount) {
        if (getHouse() != null) {
            if (getHouse().getSpec() == null) {
                HouseSpec houseSpec = new HouseSpec();
                getHouse().setSpec(houseSpec);
            }

            getHouse().getSpec().setBedroomCount(roomCount);
        }
    }

    private void setPrice(String priceText) {

        if (priceText == null || priceText.length() == 0 || getHouse() == null)
            return;

        Double price = Double.parseDouble(priceText);
        if (getHouse().getPrice() == null) {
            HousePrice housePrice = new HousePrice();
            getHouse().setPrice(housePrice);
        }

        getHouse().getPrice().setPrice(price);
    }
}
