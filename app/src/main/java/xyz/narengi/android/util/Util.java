package xyz.narengi.android.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Sepebr Behroozi on 9/22/2016 AD.
 */

public class Util {
    public static void hideSoftKeyboard(Context context, View view) {
        if(view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
