package xyz.narengi.android.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * @author Siavash Mahmoudpour
 */

public class TwoDScrollView extends FrameLayout {
    static final int ANIMATED_SCROLL_GAP = 250;
    static final float MAX_SCROLL_FACTOR = 0.5f;

    private boolean isStarted = true;

    private long mLastScroll;
    private Scroller mScroller;
    private int maxHeight;

    private float mLastMotionY;
    private float mLastMotionX;
    private boolean mIsBeingDragged = false;

    private VelocityTracker mVelocityTracker;

    private int mTouchSlop;
    private int mMinimumVelocity;
    //private int mMaximumVelocity;

    public TwoDScrollView(Context context) {
        super(context);
        initTwoDScrollView();
    }

    public TwoDScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTwoDScrollView();
    }

    public TwoDScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTwoDScrollView();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        if (getScrollY() < length) {
            return getScrollY() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        final int bottomEdge = getHeight() - getPaddingBottom();
        final int span = getChildAt(0).getBottom() - getScrollY() - bottomEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        if (getScrollX() < length) {
            return getScrollX() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        final int rightEdge = getWidth() - getPaddingRight();
        final int span = getChildAt(0).getRight() - getScrollX() - rightEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    private void initTwoDScrollView() {
        mScroller = new Scroller(getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        //mMaximumVelocity = mMinimumVelocity * 5;//configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("TwoDScrollView can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("TwoDScrollView can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("TwoDScrollView can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("TwoDScrollView can host only one direct child");
        }

        super.addView(child, index, params);
    }

    /**
     * @return Returns true this TwoDScrollView can be scrolled
     */
    private boolean canScroll() {
        View child = getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            int childWidth = child.getWidth();
            return (getHeight() < childHeight + getPaddingTop() + getPaddingBottom()) ||
                   (getWidth() < childWidth + getPaddingLeft() + getPaddingRight());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if( !isStarted() ) {
            return false;
        }

        View child = null;
        if( getChildCount() > 0 ) {
            child = getChildAt( 0 );
        }
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            if( child != null && child instanceof RecyclerView)
                ( (RecyclerView)child ).startNestedScroll(1);
            return true;
        }

        if (!canScroll()) {
            mIsBeingDragged = false;
            return false;
        }

        final float y = ev.getY();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:

              final int yDiff = (int) Math.abs(y - mLastMotionY);
              final int xDiff = (int) Math.abs(x - mLastMotionX);
                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                /* Remember location of down touch */
                mLastMotionY = y;
                mLastMotionX = x;

                mIsBeingDragged = !mScroller.isFinished();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                break;
        }

        /*
        * The only time we want to intercept motion events is if we are in the
        * drag mode.
        */
        if( mIsBeingDragged ) {
            if( child != null && child instanceof RecyclerView)
                ( (RecyclerView)child ).startNestedScroll(1);
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }

        if (!canScroll()) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float y = ev.getY();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                * If being flinged and user touches, stop the fling. isFinished
                * will be false if being flinged.
                */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                int deltaX = (int) (mLastMotionX - x);
                int deltaY = (int) (mLastMotionY - y);
                mLastMotionX = x;
                mLastMotionY = y;

                if (deltaX < 0) {
                  if (getScrollX() < 0) {
                      deltaX = 0;
                  }
                } else if (deltaX > 0) {
                    final int rightEdge = getWidth() - getPaddingRight();
                    final int availableToScroll = getChildAt(0).getRight() - getScrollX() - rightEdge;
                    if (availableToScroll > 0) {
                        deltaX = Math.min(availableToScroll, deltaX);
                    } else {
                      deltaX = 0;
                    }
                }
                if (deltaY < 0) {
                    if (getScrollY() < 0) {
                        deltaY = 0;
                    }
                } else if (deltaY > 0) {
                    final int bottomEdge = getHeight() - getPaddingBottom();
                    final int availableToScroll = getChildAt(0).getBottom() - getScrollY() - bottomEdge;
                    if (availableToScroll > 0) {
                        deltaY = Math.min(availableToScroll, deltaY);
                    } else {
                      deltaY = 0;
                    }
                }
                if (deltaY != 0 || deltaX != 0)
                  scrollBy(deltaX, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                //velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialXVelocity = (int) velocityTracker.getXVelocity();
                int initialYVelocity = (int) velocityTracker.getYVelocity();

                if ((Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > mMinimumVelocity) && getChildCount() > 0) {
                    fling(-initialXVelocity, -initialYVelocity);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
        }
        return true;
    }

    public boolean fullScroll(int direction_vert, int direction_horz) {
      int scrollAmountY = 0, scrollAmountX = 0;
        //  vertical
        switch (direction_vert) {
          case View.FOCUS_UP:
            scrollAmountY = -getScrollY();
            break;
          case View.FOCUS_DOWN:
            int count = getChildCount();
            if (count > 0) {
              View view = getChildAt(count - 1);
              scrollAmountY = (view.getBottom() - getHeight()) - getScrollY();
            }
            break;
        }
        // horizontal
        switch (direction_horz) {
          case View.FOCUS_LEFT:
            scrollAmountX = -getScrollX();
            break;
          case View.FOCUS_RIGHT:
            int count = getChildCount();
            if (count > 0) {
              View view = getChildAt(count - 1);
              scrollAmountX = (view.getRight() - getWidth()) - getScrollX();
            }
            break;
      }
      boolean handled = (scrollAmountX != 0) || (scrollAmountY != 0);
      if (handled)
        doScroll(scrollAmountX, scrollAmountY);
      return handled;
    }

    private void doScroll(int deltaX, int deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            smoothScrollBy(deltaX, deltaY);
        }
    }

    public final void smoothScrollBy(int dx, int dy) {
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > ANIMATED_SCROLL_GAP) {
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
            //awakenScrollBars(mScroller.getDuration());
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
    }

    @Override
    protected int computeVerticalScrollRange() {
        int count = getChildCount();
        return count == 0 ? getHeight() : (getChildAt(0)).getBottom();
    }
    @Override
    protected int computeHorizontalScrollRange() {
        int count = getChildCount();
        return count == 0 ? getWidth() : (getChildAt(0)).getRight();
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed, lp.width);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            /*if( getHeight() < maxHeight ) {
                int delta = Math.abs( y - oldY );
                if( ( delta + getHeight() ) >= maxHeight ) {
                    setMinimumHeight( maxHeight );
                    y = getPaddingBottom();
                } else {
                    setMinimumHeight( getHeight() + delta );
                    invalidate();
                    return;
                }
            }*/
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollTo(clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth()),
                         clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight()));
            } else {
                scrollTo(x, y);
            }
            if (oldX != getScrollX() || oldY != getScrollY()) {
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            // Keep on drawing until the animation has finished.
            postInvalidate();
        }
    }

    public void setMaximumHeight( int maxHeight ) {
        this.maxHeight = maxHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // Calling this with the present values causes it to re-clam them
        scrollTo(getScrollX(), getScrollY());
    }

    public void fling(int velocityX, int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int bottom = getChildAt(0).getHeight();
            int width = getWidth() - getPaddingRight() - getPaddingLeft();
            int right = getChildAt(0).getWidth();

            mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, right - width, 0, bottom - height);

            //awakenScrollBars(mScroller.getDuration());
            invalidate();
        }
    }

    public void scrollTo(int x, int y) {
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth());
            y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight());
            if (x != getScrollX() || y != getScrollY()) {
                super.scrollTo(x, y);
            }
        }
    }

    private int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {

            return 0;
        }
        if ((my+n) > child) {

            return child-my;
        }
        return n;
    }
}