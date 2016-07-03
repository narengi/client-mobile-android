package xyz.narengi.android.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class AddHouseConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_confirm);

        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(android.R.color.black));
        colorDrawable.setAlpha(80);
        getWindow().setBackgroundDrawable(colorDrawable);

        View contentView = findViewById(android.R.id.content);
        contentView.setBackgroundDrawable(colorDrawable);
        contentView.setBackgroundDrawable(colorDrawable);

        Button closeButton = (Button)findViewById(R.id.add_house_confirm_closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(2001);
        finish();
    }
}
