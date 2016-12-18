package xyz.narengi.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountProfile1;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.adapter.ViewProfileAdapter;
import xyz.narengi.android.ui.widget.CustomTextView;

public class ViewProfileActivity1 extends AppCompatActivity {
    private ViewProfileAdapter adapter;
    private List<House> houses;
    private ImageView image;
    private View progressBar;
    private RecyclerView recyclerView;
    private CustomTextView tvBio;
    private CustomTextView tvLocation;
    private CustomTextView tvName;
    private View llErrorContainer;
    private View btnRetry;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_profile1);
        houses = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                finish();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            getData(id);
        }
    }

    private void getData(String id) {
        llErrorContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();
        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<AccountProfile1> call = apiEndpoints.getAccountProfile(id);

        call.enqueue(new Callback<AccountProfile1>() {
            @Override
            public void onResponse(Call<AccountProfile1> call, Response<AccountProfile1> response) {
                progressBar.setVisibility(View.GONE);
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
                llErrorContainer.setVisibility(View.VISIBLE);
//                Toast.makeText(ViewProfileActivity1.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showData(AccountProfile1 accountProfile1) {
        tvName.setText(accountProfile1.getFullName());
        tvLocation.setText(accountProfile1.getCity() + ", " + accountProfile1.getProvince());
        tvBio.setText(accountProfile1.getBio());
        Picasso.with(this).load("https://api.narengi.xyz/v1" + accountProfile1.getPicture().getUrl()).into(image);
        Collections.addAll(houses, accountProfile1.getHouses());
        adapter.notifyDataSetChanged();
    }
}
