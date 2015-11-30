package xyz.narengi.android.content;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.service.RetrofitApiEndpoints;

public class SearchSuggestionProvider extends ContentProvider {

    public static String AUTHORITY = "xyz.narengi.android.content.SearchSuggestionProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/aroundLocation");

    // MIME types used for searching words or looking up a single definition
    public static final String AROUND_LOCATIONS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.narengi.android";
    public static final String AROUND_LOCATION_DETAIL_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/vnd.narengi.android";

    // UriMatcher stuff
    private static final int SEARCH_AROUND_LOCATIONS = 0;
    private static final int GET_AROUND_LOCATION = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();


    public SearchSuggestionProvider() {
    }

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "aroundLocation", SEARCH_AROUND_LOCATIONS);
        matcher.addURI(AUTHORITY, "aroundLocation/#", GET_AROUND_LOCATION);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_AROUND_LOCATIONS:
                return AROUND_LOCATIONS_MIME_TYPE;
            case GET_AROUND_LOCATION:
                return AROUND_LOCATION_DETAIL_MIME_TYPE;
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case REFRESH_SHORTCUT:
                return SearchManager.SHORTCUT_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //        String query = uri.getLastPathSegment().toLowerCase();
        String query = "";
        if( selectionArgs != null )
            query = selectionArgs[0];
        else
            query = uri.getLastPathSegment().toLowerCase();

        MatrixCursor matrixCursor = new MatrixCursor( new String[]{ "_id" , SearchManager.SUGGEST_COLUMN_TEXT_1 , SearchManager.SUGGEST_COLUMN_TEXT_2 , SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } );
        AroundLocation[] aroundLocations = searchAroundLocations(query);
        if (aroundLocations != null) {
            for (int i=0 ; i < aroundLocations.length ; i++) {
                AroundLocation aroundLocation = aroundLocations[i];
                long id = i + 1;
                String name = aroundLocation.getType();
                String type = aroundLocation.getType();

                matrixCursor.addRow(new Object[]{id ,name ,type ,id});
            }
        }

        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                return matrixCursor;
            case SEARCH_AROUND_LOCATIONS:
                return matrixCursor;
//            case GET_AROUND_LOCATION:
//                return getAroundLocation( uri );
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

    }


    public AroundLocation[] searchAroundLocations(String query) {

        /*String BASE_URL = "http://149.202.20.233:3500";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AroundLocation[]> call = apiEndpoints.getAroundLocations();
        try {
            AroundLocation[] aroundLocations = call.execute().body();
            if (aroundLocations != null)
                return aroundLocations;
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        SearchServiceAsyncTask searchAsyncTask = new SearchServiceAsyncTask();
        Object object = null;
        try {
            object = searchAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (object != null)
            return (AroundLocation[])object;
//        call.enqueue(new Callback<AroundLocation[]>() {
//            @Override
//            public void onResponse(Response<AroundLocation[]> response, Retrofit retrofit) {
//                int statusCode = response.code();
////                SearchResult searchResult = response.body();
//
////                if (searchResult != null ) {
//                AroundLocation[] aroundLocations = response.body();
//
//                if (aroundLocations != null && aroundLocations.length > 0) {
//
//                    for (AroundLocation location : aroundLocations) {
//                        System.err.println("Around location title : " + location.getTitle());
//                        Log.d("RetrofitService", "Around location title : " + location.getTitle());
//                    }
//                } else
////                    System.err.println("Around locations is empty");
//                    Log.d("RetrofitService", "Around locations is empty");
////                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // Log error here since request failed
//                t.printStackTrace();
//                Log.d("RetrofitService", "onFailure : " + t.getMessage(), t);
//            }
//        });

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class SearchServiceAsyncTask extends AsyncTask {

        public SearchServiceAsyncTask() {}

        @Override
        protected Object doInBackground(Object[] objects) {
            String BASE_URL = "http://149.202.20.233:3500";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
            Call<AroundLocation[]> call = apiEndpoints.getAroundLocations();
            try {
                AroundLocation[] aroundLocations = call.execute().body();
                if (aroundLocations != null)
                    return aroundLocations;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
