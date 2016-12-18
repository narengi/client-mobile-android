package xyz.narengi.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Siavash Mahmoudpour
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (isInEditMode())
            return;
        if (style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Bold.ttf"));
        } else if (style == Typeface.ITALIC) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Medium.ttf"));
        } else {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans.ttf"));
        }
    }

    @Override
    public void setTypeface(Typeface tf) {
        if (isInEditMode())
            return;

        if (tf.isBold() && tf.isItalic()) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Light.ttf"));
        } else if (tf.isBold()) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Bold.ttf"));
        } else if (tf.isItalic()) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans-Medium.ttf"));
        } else {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans.ttf"));
        }
    }
}
