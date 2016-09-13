package ir.smartlab.persindatepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * @author Siavash Mahmoudpour
 */
public class CustomNumberPicker extends NumberPicker {

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if(child instanceof EditText) {
            ((EditText) child).setTextSize(16);
        }
    }
}