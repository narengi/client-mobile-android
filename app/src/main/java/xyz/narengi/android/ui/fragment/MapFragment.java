package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class MapFragment extends SupportMapFragment {

    private OnTouchListener onTouchListener;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = super.onCreateView(inflater, container, savedInstanceState);
//        if (view != null && view instanceof ViewGroup && ((ViewGroup)view).getChildCount() > 0 && ((ViewGroup)view).getChildAt(0) instanceof FrameLayout) {
//            FrameLayout mainChild = (FrameLayout) ((ViewGroup)view).getChildAt(0);
//            mainChild.addView(new View(getActivity()));
//        }
//
//        return view;

        View layout = super.onCreateView(inflater, container, savedInstanceState);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());

        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return layout;
    }


    public void setListener(OnTouchListener listener) {
        onTouchListener = listener;
    }

    public interface OnTouchListener {
        public abstract void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    onTouchListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

}
