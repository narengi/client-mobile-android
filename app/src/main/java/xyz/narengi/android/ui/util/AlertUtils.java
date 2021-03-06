package xyz.narengi.android.ui.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.activity.ExploreActivity;
import xyz.narengi.android.ui.widget.CustomTextView;

/**
 * @author Siavash Mahmoudpour
 */
public class AlertUtils {

    private static AlertUtils instance;
    private Handler handler;

    private AlertUtils() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
    }

    public static AlertUtils getInstance() {
        if (instance == null)
            instance = new AlertUtils();

        return instance;
    }

    public AlertDialog createModelProgress(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_modal_progress, null, false);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public void showNetworkErrorDialog(final Context context, final String errorMessage) {
        handler.post(new Runnable() {
            public void run() {
                CustomTextView view = new CustomTextView(context);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setView(view);
                builder.setMessage(errorMessage);
                builder.setTitle(context.getResources().getString(R.string.error_alert_title));
                builder.setPositiveButton(context.getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                try {
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
