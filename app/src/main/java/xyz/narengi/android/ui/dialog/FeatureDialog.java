package xyz.narengi.android.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.ui.adapter.FeatureAdaoter;

public class FeatureDialog extends AppCompatDialog {
    private RecyclerView recyclerView;
    private HouseFeature[] houseFeature;
    private Activity activity;

    public FeatureDialog(Activity activity, HouseFeature[] houseFeature) {
        super(activity);
        this.houseFeature = houseFeature;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.feayure_dialog);

        recyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FeatureAdaoter  adapter = new FeatureAdaoter(getContext(), houseFeature);
        recyclerView.setAdapter(adapter);

        View vi = findViewById(R.id.close);
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
