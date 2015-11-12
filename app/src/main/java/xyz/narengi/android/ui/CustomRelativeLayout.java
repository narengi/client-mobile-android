package xyz.narengi.android.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Siavash Mahmoudpour
 */
@CoordinatorLayout.DefaultBehavior(CustomScrollBehavior.class)
public class CustomRelativeLayout extends RelativeLayout {
    public CustomRelativeLayout(Context context) {
        super(context);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
