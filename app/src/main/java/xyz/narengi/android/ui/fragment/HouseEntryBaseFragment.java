package xyz.narengi.android.ui.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import xyz.narengi.android.common.dto.House;

/**
 * @author Siavash Mahmoudpour
 */
public abstract class HouseEntryBaseFragment extends Fragment {

    private House house;
    private EntryMode entryMode;
    private OnInteractionListener onInteractionListener;

    public enum EntryMode {
        ADD, EDIT , NONE
    }

    public HouseEntryBaseFragment() {
        // Required empty public constructor
    }

    protected boolean validate() {
        return true;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public EntryMode getEntryMode() {
        if (entryMode == null) return EntryMode.NONE;
        return entryMode;
    }

    public void setEntryMode(EntryMode entryMode) {
        this.entryMode = entryMode;
    }

    public OnInteractionListener getOnInteractionListener() {
        return onInteractionListener;
    }

    public void setOnInteractionListener(OnInteractionListener onInteractionListener) {
        this.onInteractionListener = onInteractionListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractionListener) {
            setOnInteractionListener((OnInteractionListener)context);
        } /*else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setOnInteractionListener(null);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

    public interface OnInteractionListener {
        void onGoToNextSection(House house);
        void onBackToPreviousSection(House house);
    }
}
