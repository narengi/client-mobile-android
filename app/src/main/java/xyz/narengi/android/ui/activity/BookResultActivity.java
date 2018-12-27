package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.narengi.android.R;

public class BookResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_result);

        ImageView resultImageView = (ImageView)findViewById(R.id.book_result_image);
        TextView resultMessageTextView = (TextView)findViewById(R.id.book_result_message);
        TextView resultDescriptionTextView = (TextView)findViewById(R.id.book_result_description);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("bookResult", false)) {
                //success

                resultImageView.setImageDrawable(getResources().getDrawable(R.drawable.signup_successful));

                resultMessageTextView.setText(getString(R.string.book_result_success_message));
                resultMessageTextView.setTextColor(getResources().getColor(R.color.green));

                resultDescriptionTextView.setText(getString(R.string.book_result_success_description));
                resultDescriptionTextView.setTextColor(getResources().getColor(R.color.text_gray_dark));

            } else {
                //failure

//                resultImageView.setImageDrawable(getResources().getDrawable(R.drawable.signup_successful));

                resultMessageTextView.setText(getString(R.string.book_result_failure_message));
                resultMessageTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                resultDescriptionTextView.setText(getString(R.string.book_result_failure_message));
                resultDescriptionTextView.setTextColor(getResources().getColor(R.color.text_gray_dark));
            }
        }

        Button closeButton = (Button)findViewById(R.id.book_result_closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 501) {
            setResult(501);
            finish();
        }
    }

    private void close() {
        setResult(501);
        finish();
    }
}
