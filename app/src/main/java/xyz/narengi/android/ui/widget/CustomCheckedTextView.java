package xyz.narengi.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

/**
 * @author Siavash Mahmoudpour
 */
public class CustomCheckedTextView extends CheckedTextView {

    public CustomCheckedTextView(Context context) {
        super(context);
    }

    public CustomCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Bold.ttf"));
        } else {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans.ttf"));
        }
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans.ttf"));
    }
}
