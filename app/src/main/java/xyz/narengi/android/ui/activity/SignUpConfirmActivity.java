package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class SignUpConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 || resultCode == 102) {
            close();
        }

        if (resultCode == 401) {
            setResult(401);
            finish();
        }
    }

    private void close() {
        setResult(101);
        finish();
    }

    private void initViews() {
        Button skipButton = (Button) findViewById(R.id.register_confirm_skipButton);
        Button completeProfileButton = (Button) findViewById(R.id.register_confirm_completeProfileButton);
        skipButton.setOnClickListener(this);
        completeProfileButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_confirm_skipButton:
                close();
                break;
            case R.id.register_confirm_completeProfileButton:
                openEditProfile();
                break;
        }
    }

    private void openEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivityForResult(intent, 103);
    }
}
