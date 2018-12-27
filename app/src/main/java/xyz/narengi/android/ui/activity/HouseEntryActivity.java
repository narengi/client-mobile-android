package xyz.narengi.android.ui.activity;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.ArrayList;
import java.util.List;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseEntryTitleItem;
import xyz.narengi.android.ui.adapter.HouseEntryContentRecyclerAdapter;


/**
 * @author Siavash Mahmoudpour
 */
public class HouseEntryActivity extends AppCompatActivity
        implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {

    private ActionBarRtlizer rtlizer;

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_entry);
        setupContentRecyclerView();
        setPageTitle(getString(R.string.house_entry_add_page_title));
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.house_entry_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    protected void rtlizeActionBar() {
        if (getSupportActionBar() != null) {
//            rtlizer = new ActionBarRtlizer(this, "toolbar_actionbar");
            rtlizer = new ActionBarRtlizer(this);;
            ViewGroup homeView = (ViewGroup) rtlizer.getHomeView();
            RtlizeEverything.rtlize(rtlizer.getActionBarView());
            if (rtlizer.getHomeViewContainer() instanceof ViewGroup) {
                RtlizeEverything.rtlize((ViewGroup) rtlizer.getHomeViewContainer());
            }
            RtlizeEverything.rtlize(homeView);
            rtlizer.flipActionBarUpIconIfAvailable(homeView);
        }
    }


    private void setupContentRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.house_entry_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);




//        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
//        RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        //adapter
//        final ExpandableExampleAdapter myItemAdapter = new ExpandableExampleAdapter(getDataProvider());

//        final HouseEntryContentRecyclerAdapter contentRecyclerAdapter = new HouseEntryContentRecyclerAdapter(this,
//                null, getResources().getStringArray(R.array.house_entry_item_title_array));

//        final HouseEntryContentRecyclerAdapter contentRecyclerAdapter = new HouseEntryContentRecyclerAdapter(this,
//                null, createHouseEntryTitleItems());

        final HouseEntryContentRecyclerAdapter contentRecyclerAdapter = new HouseEntryContentRecyclerAdapter(this,
                null, getResources().getStringArray(R.array.house_entry_item_title_array));

        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(contentRecyclerAdapter);       // wrap for expanding

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.drawable.list_divider_h), true));

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

//        HouseEntryContentRecyclerAdapter contentRecyclerAdapter = new HouseEntryContentRecyclerAdapter(this, createHouseEntryTitleItems(), null);
//
//        contentRecyclerAdapter.setCustomParentAnimationViewId(R.id.house_entry_title_item_number);
//        contentRecyclerAdapter.setParentClickableViewAnimationDefaultDuration();
//        contentRecyclerAdapter.setParentAndIconExpandOnClick(true);

//        mRecyclerView.setAdapter(contentRecyclerAdapter);
    }

    private List<HouseEntryTitleItem> createHouseEntryTitleItems() {

        List<HouseEntryTitleItem> houseEntryTitleItems = new ArrayList<HouseEntryTitleItem>();
        String[] titles = getResources().getStringArray(R.array.house_entry_item_title_array);
        int index = 0;
        if (titles != null && titles.length > 0) {
            for (String title:titles) {
                HouseEntryTitleItem titleItem = new HouseEntryTitleItem();
                titleItem.setId(index + 1);
                titleItem.setTitle(title);
                titleItem.setNumber(++index);
                houseEntryTitleItems.add(titleItem);
            }
        }

        return houseEntryTitleItems;
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {

    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getResources().getDimensionPixelSize(R.dimen.expandable_list_item_height);
        int topMargin = (int) (getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin, bottomMargin);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}
