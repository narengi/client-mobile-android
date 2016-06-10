package xyz.narengi.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author Siavash Mahmoudpour
 */
public class NestedScrollingListView extends ListView {

    private OnTouchListener onTouchListener;

    public NestedScrollingListView(Context context) {
        super(context);
    }

    public NestedScrollingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollingListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnTouchListener listener) {
        onTouchListener = listener;
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

    public interface OnTouchListener {
        public abstract void onTouch();
    }
}
