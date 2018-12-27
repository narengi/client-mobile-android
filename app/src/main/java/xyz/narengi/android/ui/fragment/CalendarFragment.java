package xyz.narengi.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byagowi.persiancalendar.Utils;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.adapter.CalendarPagerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class CalendarFragment extends Fragment {


    TextView monthCaptionTextView;

    private ViewPager viewPager;


    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getInstance().loadLanguageFromSettings(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.calendar_pager);

        viewPager.setAdapter(new CalendarPagerAdapter(getActivity().getSupportFragmentManager(), getContext(), null));
        viewPager.setCurrentItem(1200 / 2);
        bringTodayYearMonth();

        return view;
    }

    private void bringTodayYearMonth() {
        if (viewPager.getCurrentItem() != 1200 / 2) {
            viewPager.setCurrentItem(1200 / 2);
        }
    }


    public void changeMonth(int position) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + position, true);
    }

    public int getMonthPosition() {
        if (viewPager != null)
            return viewPager.getCurrentItem();
        return 1200 / 2;
    }

    public void updateMonthCaption(String monthCaption) {
        if (monthCaptionTextView != null)
            monthCaptionTextView.setText(monthCaption);
    }
}
