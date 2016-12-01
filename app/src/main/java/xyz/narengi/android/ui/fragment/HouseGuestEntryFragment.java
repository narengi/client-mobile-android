package xyz.narengi.android.ui.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HousePrice;
import xyz.narengi.android.common.dto.HouseSpec;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseGuestEntryFragment extends HouseEntryBaseFragment {

    private TextView guestCountTextView;
    private TextView maxGuestCountTextView;
    private Button addGuestButton;
    private Button removeGuestButton;
    private Button increaseMaxGuestButton;
    private Button decreaseMaxGuestButton;
    private EditText extraGuestPriceEditText;
    private int guestCount;
    private int maxGuestCount;
    private TextWatcher extraGuestTextWatcher;

    public HouseGuestEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_guest_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {

        guestCountTextView = (TextView)view.findViewById(R.id.house_guest_entry_guestCount);
        maxGuestCountTextView = (TextView)view.findViewById(R.id.house_guest_entry_maxGuestCount);
        addGuestButton = (Button)view.findViewById(R.id.house_guest_entry_addGuestButton);
        removeGuestButton = (Button)view.findViewById(R.id.house_guest_entry_removeGuestButton);
        increaseMaxGuestButton = (Button)view.findViewById(R.id.house_guest_entry_increaseMaxGuestButton);
        decreaseMaxGuestButton = (Button)view.findViewById(R.id.house_guest_entry_decreaseMaxGuestButton);
        extraGuestPriceEditText = (EditText)view.findViewById(R.id.house_guest_entry_extraGuestPrice);

        extraGuestTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setExtraGuestPrice(editable.toString());
            }
        };

        extraGuestPriceEditText.addTextChangedListener(extraGuestTextWatcher);

        if (getHouse() != null) {
            if (getHouse().getSpec() != null) {
                guestCount = getHouse().getSpec().getGuestCount();
                maxGuestCount = getHouse().getSpec().getMaxGuestCount();
            }

            if (getHouse().getPrice() != null && getHouse().getPrice().getExtraGuestPrice() > 0) {
                extraGuestPriceEditText.removeTextChangedListener(extraGuestTextWatcher);
                extraGuestPriceEditText.setText(String.valueOf((long) getHouse().getPrice().getExtraGuestPrice()));
                extraGuestPriceEditText.addTextChangedListener(extraGuestTextWatcher);
            }
        }

		if (guestCount == 0) {
			guestCount = 1;
			maxGuestCount = 1;
		}

        guestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(guestCount)));
        maxGuestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(maxGuestCount)));

        addGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestCount++;
                guestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(guestCount)));
                setGuestCount(guestCount);

                if (guestCount > maxGuestCount) {
                    maxGuestCount++;
                    maxGuestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(maxGuestCount)));
                    setMaxGuestCount(maxGuestCount);
                }
            }
        });

        removeGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if (guestCount > 1) {
					guestCount--;
					if (guestCount < 0) {
						guestCount = 0;
					}
					guestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(guestCount)));
					setGuestCount(guestCount);
				}
			}
        });

        increaseMaxGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxGuestCount++;
                maxGuestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(maxGuestCount)));
                setMaxGuestCount(maxGuestCount);
            }
        });

        decreaseMaxGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if (maxGuestCount > 1) {
					maxGuestCount--;
					if (maxGuestCount < 0) {
						maxGuestCount = 0;
					}
					maxGuestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(maxGuestCount)));
					setMaxGuestCount(maxGuestCount);


					if (guestCount > maxGuestCount) {
						guestCount--;
						if (guestCount < 0) {
							guestCount = 0;
						}
						guestCountTextView.setText(getString(R.string.house_guest_entry_counter_caption, String.valueOf(guestCount)));
						setGuestCount(guestCount);
					}
				}
            }
        });

        Button nextButton = (Button)view.findViewById(R.id.house_guest_entry_nextButton);
        Button previousButton = (Button)view.findViewById(R.id.house_guest_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                setExtraGuestPrice(extraGuestPriceEditText.getText().toString());
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
                                setExtraGuestPrice(extraGuestPriceEditText.getText().toString());
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

    private void setMaxGuestCount(int maxGuestCount) {
        if (getHouse() != null) {
            if (getHouse().getSpec() == null) {
                HouseSpec houseSpec = new HouseSpec();
                getHouse().setSpec(houseSpec);
            }

            getHouse().getSpec().setMaxGuestCount(maxGuestCount);
        }
    }

    private void setGuestCount(int guestCount) {
        if (getHouse() != null) {
            if (getHouse().getSpec() == null) {
                HouseSpec houseSpec = new HouseSpec();
                getHouse().setSpec(houseSpec);
            }

            getHouse().getSpec().setGuestCount(guestCount);
        }
    }

    private void setExtraGuestPrice(String priceText) {

        if (priceText == null || priceText.length() == 0 || getHouse() == null)
            return;

        Double price = Double.parseDouble(priceText);
        if (getHouse().getPrice() == null) {
            HousePrice housePrice = new HousePrice();
            getHouse().setPrice(housePrice);
        }

        getHouse().getPrice().setExtraGuestPrice(price);
    }

	@Override
	protected boolean validate() {
		if (getHouse() == null || getHouse().getSpec() == null || getHouse().getSpec().getGuestCount() == 0) {
			Toast.makeText(getContext(), R.string.invalid_data, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
