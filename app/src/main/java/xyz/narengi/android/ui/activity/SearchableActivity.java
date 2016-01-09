package xyz.narengi.android.ui.activity;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.narengi.android.R;
import xyz.narengi.android.content.SearchSuggestionProvider;
import xyz.narengi.android.ui.adapter.SuggestionsListAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchableActivity extends ActionBarActivity {

    private TextView mTextView;
//    private ListView listView;
    private ExpandableListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

//        listView = (ListView) findViewById(R.id.list);
        listView = (ExpandableListView) findViewById(R.id.list);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show around location detail

            Toast.makeText(this, "A suggestion item clicked", Toast.LENGTH_LONG).show();

//            Intent detailIntent = new Intent(this, DetailActivity.class);
//            Uri data = intent.getData();
//            detailIntent.setData(data);
//            startActivity(detailIntent);
//            finish();
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, "");
            Toast.makeText(this, "Searchable Activity, this is query : " + query, Toast.LENGTH_LONG).show();
            showResults(query);
        }
    }

    private void showResults(String query) {

        ContentResolver contentResolver = getApplicationContext().getContentResolver();

//        String contentUri = "content://" + SearchSuggestionProvider.AUTHORITY + '/' + SearchManager.SUGGEST_URI_PATH_QUERY;
        String contentUri = "content://" + SearchSuggestionProvider.AUTHORITY + '/' + SearchManager.SUGGEST_URI_PATH_QUERY;
        Uri uri = Uri.parse(contentUri);

//        Cursor cursor = contentResolver.query(/*SearchSuggestionProvider.CONTENT_URI*/uri, null, null, new String[] { query }, null);


//        Cursor cursor = managedQuery(SearchSuggestionProvider.CONTENT_URI, null, null,
//                new String[] {query}, null);
        Cursor cursor = managedQuery(uri, null, null,
                new String[] {query}, null);

        if (cursor == null) {
            // There are no results
        } else {

            // Specify the columns we want to display in the result
            String[] from = new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1/*,
                    SearchManager.SUGGEST_COLUMN_TEXT_2 */};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] {R.id.search_result_item_title/*,
                    R.id.search_result_item_summary*/};
//            int[] to = new int[] {R.id.search_result_item_title};


            // Set up our adapter
            SuggestionsListAdapter mAdapter = new SuggestionsListAdapter(
                    this, cursor,
                    android.R.layout.simple_expandable_list_item_1,
                    new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 }, // Name for group layouts
                    new int[] { android.R.id.text1 },
                    R.layout.search_result_item,//android.R.layout.simple_expandable_list_item_1, //child layout
                    from, // Number for child layouts
                    to);

//            SuggestionsListAdapter suggestionsAdapter = new SuggestionsListAdapter(this, cursor, R.layout.suggestions_header,
//                    from, to, R.layout.search_result_item, from, to);
            listView.setAdapter(mAdapter);

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            /*SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                    R.layout.search_result_item, cursor, from, to);
            listView.setAdapter(words);

            // Define the on-click listener for the list items
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific word Uri
//                    Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
//                    Uri data = Uri.withAppendedPath(SearchSuggestionProvider.CONTENT_URI,
//                            String.valueOf(id));
//                    detailIntent.setData(data);
//                    startActivity(detailIntent);
                }
            });*/

            cursor.close();
        }
    }
}
