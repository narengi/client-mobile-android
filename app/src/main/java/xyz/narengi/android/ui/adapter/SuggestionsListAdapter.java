package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.provider.BaseColumns;
import android.widget.SimpleCursorTreeAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class SuggestionsListAdapter extends SimpleCursorTreeAdapter {


    public SuggestionsListAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout, childFrom, childTo);
    }

    public SuggestionsListAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
    }

    public SuggestionsListAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        final long rowId = groupCursor.getLong(groupCursor.getColumnIndex(BaseColumns._ID));

        return groupCursor;
//        return null;
    }
}
