package xyz.narengi.android.ui.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class AlertUtils {

    private static AlertUtils instance;

    private AlertUtils() {
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
}
