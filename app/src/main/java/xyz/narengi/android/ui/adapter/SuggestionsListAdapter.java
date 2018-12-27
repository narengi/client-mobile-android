package xyz.narengi.android.ui.adapter;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
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

        /*final long rowId = groupCursor.getLong(groupCursor.getColumnIndex(BaseColumns._ID));

        String[] SEARCH_SUGGEST_COLUMNS = {
                SearchManager.SUGGEST_COLUMN_FORMAT,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                BaseColumns._ID };

//        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID});
        MatrixCursor matrixCursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
        matrixCursor.addRow(new Object[]{"", null, "A Name", "A Summary", null, rowId});*/

        return groupCursor;
//        return matrixCursor;
    }
}
