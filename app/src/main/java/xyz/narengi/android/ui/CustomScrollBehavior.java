package xyz.narengi.android.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Siavash Mahmoudpour
 */
public class CustomScrollBehavior extends CoordinatorLayout.Behavior<View> {

    private int toolbarHeight;

    public CustomScrollBehavior() {
    }

    public CustomScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

//        if (dependency instanceof AppBarLayout) {
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//
//            int fabBottomMargin = lp.bottomMargin;
//            int distanceToScroll = child.getHeight() + fabBottomMargin;
//            float ratio = (float)dependency.getY()/(float)toolbarHeight;
//            child.setTranslationY(-distanceToScroll * ratio);
//        }
//        return true;

        if (dependency instanceof Toolbar) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.topMargin = 0;
        }

        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
