package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookProperties;
import xyz.narengi.android.ui.adapter.BookSummaryContentRecyclerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class BookSummaryActivity extends AppCompatActivity {

    private ActionBarRtlizer rtlizer;
    private BookProperties bookProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_summary);
        setupToolbar();
        if (getIntent() != null && getIntent().getSerializableExtra("bookProperties") != null) {
            bookProperties = (BookProperties)getIntent().getSerializableExtra("bookProperties");
            setupContentRecyclerView(bookProperties);

            Button payButton = (Button)findViewById(R.id.book_summary_payButton);
            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BookSummaryActivity.this, BookResultActivity.class);
                    intent.putExtra("bookResult", true);
                    startActivityForResult(intent, 501);
                }
            });
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


    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_summary_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_summary_toolbar);

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
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.book_summary_collapse_toolbar);
//        collapsingToolbarLayout.setTitle(getString(R.string.book_summary_page_title));
        setPageTitle(getString(R.string.book_summary_page_title));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    private void setupContentRecyclerView(BookProperties bookProperties) {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.book_summary_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        BookSummaryContentRecyclerAdapter bookSummaryContentRecyclerAdapter = new BookSummaryContentRecyclerAdapter(this, bookProperties);
        mRecyclerView.setAdapter(bookSummaryContentRecyclerAdapter);
    }
}
