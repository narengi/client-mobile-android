package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Siavash Mahmoudpour
 */
public class SpinnerArrayAdapter<CharSequence> extends ArrayAdapter<CharSequence> {


    public SpinnerArrayAdapter(Context context, int resourceId, CharSequence[] objects) {
        super(context,resourceId,objects );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
        if( view instanceof TextView) {
//            Typeface typeface = Typeface.createFromAsset(context.getAssets(), Font.B_NAZANIN.getFileName());
            TextView textView = (TextView) view;
//            textView.setText(CharacterUtil.reshape(textView.getText().toString()));
//            textView.setTypeface(typeface);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position,convertView,parent);
        if( view instanceof TextView ) {
//            Typeface typeface = Typeface.createFromAsset(context.getAssets(), Font.B_NAZANIN.getFileName());
            TextView textView = (TextView) view;
//            textView.setText(CharacterUtil.reshape(textView.getText().toString()));
//            textView.setTypeface(typeface);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        return view;
    }

}
