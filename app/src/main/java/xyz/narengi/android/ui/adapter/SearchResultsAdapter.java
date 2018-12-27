package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchResultsAdapter extends SimpleCursorAdapter {
    private static final String tag = SearchResultsAdapter.class.getName();
    private Context context=null;
    public SearchResultsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context=context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        ImageView imageView=(ImageView)view.findViewById(R.id.icon_feed);
        TextView textView=(TextView)view.findViewById(R.id.search_result_item_title);
//        TextView subscribersView=(TextView)view.findViewById(R.id.subscriber_count);
//        ImageTagFactory imageTagFactory = ImageTagFactory.newInstance(context, R.drawable.rss_icon);
//        imageTagFactory.setErrorImageId(R.drawable.rss_icon);
//        ImageTag tag = imageTagFactory.build(cursor.getString(2),context);
//        imageView.setTag(tag);
//        FeedReaderApplication.getImageManager().getLoader().load(imageView);
//        textView.setText(cursor.getString(4) + " : " + cursor.getString(1));
//        subscribersView.setText(cursor.getString(3));

        textView.setText(cursor.getString(1));
    }
}
