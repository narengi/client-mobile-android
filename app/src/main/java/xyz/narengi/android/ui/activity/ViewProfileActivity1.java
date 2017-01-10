package xyz.narengi.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountProfile1;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.adapter.ViewProfileAdapter;
import xyz.narengi.android.ui.widget.CustomTextView;
import xyz.narengi.android.ui.widget.ScrollUtils;

public class ViewProfileActivity1 extends AppCompatActivity  {
    private ViewProfileAdapter adapter;
    private List<House> houses;
    private ImageView image;
    private View progressBar;
    private RecyclerView recyclerView;
    private CustomTextView tvBio;
    private CustomTextView tvLocation;
    private CustomTextView tvName;
    private View llErrorContainer;
    private View logOut;
    private View edit;
    private View btnRetry;
    private View currentUserOption;
    private String id = "";
    private int mParallaxImageHeight;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_profile1);
        houses = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        logOut = findViewById(R.id.logOut);
        edit = findViewById(R.id.edit);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity1.this, EditProfileActivity.class);
                intent.putExtra("openedFromViewProfile", true);
                startActivityForResult(intent, 103);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountProfile.logout(ViewProfileActivity1.this);

                Intent intent = new Intent(ViewProfileActivity1.this, ExploreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        currentUserOption = findViewById(R.id.currentUserOption);
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                Log.e("tets", dy + "");
//            }
//        });

        View header = LayoutInflater.from(this).inflate(R.layout.profile_header, recyclerView, false);
        tvName = (CustomTextView) header.findViewById(R.id.tvName);
        tvLocation = (CustomTextView) header.findViewById(R.id.tvLocation);
        tvBio = (CustomTextView) header.findViewById(R.id.tvBio);
        image = (ImageView) header.findViewById(R.id.image);

        adapter = new ViewProfileAdapter(header, houses, this);
        recyclerView.setAdapter(adapter);

        llErrorContainer = findViewById(R.id.llErrorContainer);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(id);
            }
        });

        if (getIntent() != null && getIntent().getStringExtra("id") != null) {
            id = getIntent().getStringExtra("id");
//            getData(id);

            try {
                if (id.equals(AccountProfile.getLoggedInAccountProfile(this).getId())) {
                    currentUserOption.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        getData(id);
    }

    private void getData(String id) {
        llErrorContainer.setVisibility(View.GONE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        recyclerView.setVisibility(View.GONE);

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();
        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<AccountProfile1> call = apiEndpoints.getAccountProfile(id);

        call.enqueue(new Callback<AccountProfile1>() {
            @Override
            public void onResponse(Call<AccountProfile1> call, Response<AccountProfile1> response) {
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    showData(response.body());
                } else {

                    llErrorContainer.setVisibility(View.VISIBLE);
//                    Toast.makeText(ViewProfileActivity1.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AccountProfile1> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                llErrorContainer.setVisibility(View.VISIBLE);
//                Toast.makeText(ViewProfileActivity1.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showData(AccountProfile1 accountProfile1) {
        if (accountProfile1.getFullName() == null || accountProfile1.getFullName().isEmpty()) {
            tvName.setText("نام و نام خوانوادگی");
        } else {
            tvName.setText(accountProfile1.getFullName());
        }

        String city = "";
        if (accountProfile1.getCity() == null || accountProfile1.getCity().isEmpty()) {
            city = "شهر";
        } else {

            city = accountProfile1.getCity();
        }

        String province = "";
        if (accountProfile1.getProvince() == null || accountProfile1.getProvince().isEmpty()) {
            province = "استان";
        } else {
            province = accountProfile1.getProvince();
        }

        tvLocation.setText(city + ", " + province);

        if (accountProfile1.getBio() == null || accountProfile1.getBio().isEmpty()) {
            tvBio.setText("توضیحات ندارد");
        } else {
            tvBio.setText(accountProfile1.getBio());
        }

        Picasso.with(this).load("https://api.narengi.xyz/v1" + accountProfile1.getAvatar())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(image);

        try {
            houses.clear();
            Collections.addAll(houses, accountProfile1.getHouses());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {}
    }

//    @Override
//    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//        int baseColor = getResources().getColor(R.color.primary);
//        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
//        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
//
//    }
}
