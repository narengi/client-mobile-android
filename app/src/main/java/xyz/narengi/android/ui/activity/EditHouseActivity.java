package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byagowi.persiancalendar.Entity.Day;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.HouseEntryStep;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.ui.adapter.EditHouseContentRecyclerAdapter;
import xyz.narengi.android.ui.widget.LineDividerItemDecoration;

/**
 * @author Siavash Mahmoudpour
 */
public class EditHouseActivity extends AppCompatActivity {

    private ActionBarRtlizer rtlizer;
    private House house;
    private boolean isHouseUpdated = false;
    private ImageInfo[] imageInfoArray;
//    private HouseAvailableDates houseAvailableDates;
    private HashMap<String,List<Day>> selectedDaysMap;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);

        if (getIntent() != null) {
            if (getIntent().getSerializableExtra("house") != null) {
                house = (House) getIntent().getSerializableExtra("house");
            }

            if (getIntent().getSerializableExtra("imageInfoArray") != null) {
                imageInfoArray = (ImageInfo[]) getIntent().getSerializableExtra("imageInfoArray");
            }

            if (getIntent().getSerializableExtra("selectedDaysMap") != null) {
                selectedDaysMap = (HashMap<String,List<Day>>) getIntent().getSerializableExtra("selectedDaysMap");
            }
        }

        setupToolbar();
        setupContentRecyclerView();
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_house_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (isHouseUpdated) {
            setResult(2002);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2002) {
            Serializable updatedHouse = data.getSerializableExtra("updatedHouse");
            if (updatedHouse != null && updatedHouse instanceof House) {
                house = (House)updatedHouse;
                isHouseUpdated = true;
            }

            Serializable updatedImageInfoArray = data.getSerializableExtra("updatedImageInfoArray");
            if (updatedImageInfoArray != null)
                imageInfoArray = (ImageInfo[])updatedImageInfoArray;
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


    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_house_toolbar);

        /*Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backButtonDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        if (toolbar != null) {
            ImageButton backButton = (ImageButton)toolbar.findViewById(R.id.icon_toolbar_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//            actionBar.setTitle(getString(R.string.edit_house_page_title));
//            actionBar.setWindowTitle(getString(R.string.edit_house_page_title));
            setPageTitle(getString(R.string.edit_house_page_title));

        }
    }

    private void setupContentRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.edit_house_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        EditHouseContentRecyclerAdapter contentRecyclerAdapter = new EditHouseContentRecyclerAdapter(this, house);
        recyclerView.setAdapter(contentRecyclerAdapter);

        recyclerView.addItemDecoration(new LineDividerItemDecoration(getResources()));

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {


                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(childView);
                    if (recyclerView.getAdapter().getItemCount() > position) {
                        openEditHouseDetail(position);
                    }

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void openEditHouseDetail(int position) {
        if (house == null) {
            Toast.makeText(this, "Empty house data!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, EditHouseDetailActivity.class);
        intent.putExtra("house", house);
        intent.putExtra("houseEntrySection", String.valueOf(position));
        if (position == HouseEntryStep.HOUSE_IMAGES.ordinal()) {
            if (imageInfoArray != null && imageInfoArray.length > 0)
                intent.putExtra("imageInfoArray", imageInfoArray);
        }

        if (position == HouseEntryStep.HOUSE_DATES.ordinal()) {

            if (selectedDaysMap != null)
                intent.putExtra("selectedDaysMap", selectedDaysMap);
        }
        startActivityForResult(intent, 2002);
    }
}
