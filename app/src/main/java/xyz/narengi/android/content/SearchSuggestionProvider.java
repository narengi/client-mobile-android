package xyz.narengi.android.content;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.SearchRecentSuggestionsProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.service.SearchServiceAsyncTask;
import xyz.narengi.android.service.SuggestionsServiceAsyncTask;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "xyz.narengi.android.content.SearchSuggestionProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/aroundLocation");
//    public final static int MODE = DATABASE_MODE_QUERIES;

    public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
//    private static final String[] COLUMNS = {
//    "_id", // must include this column
//    SearchManager.SUGGEST_COLUMN_TEXT_1,
//    SearchManager.SUGGEST_COLUMN_TEXT_2,
//    SearchManager.SUGGEST_COLUMN_INTENT_DATA,
//    SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
//    SearchManager.SUGGEST_COLUMN_SHORTCUT_ID };

    private static final String[] COLUMNS = {
            "_id", // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
//            SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID };

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
        setupSuggestions(AUTHORITY, MODE);
    }

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        /*UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "aroundLocation", SEARCH_AROUND_LOCATIONS);
        matcher.addURI(AUTHORITY, "aroundLocation/#", GET_AROUND_LOCATION);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "*//*", SEARCH_SUGGEST);

        *//* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         *//*
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "*//*", REFRESH_SHORTCUT);
        return matcher;*/



        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // For the record
        matcher.addURI(AUTHORITY, "aroundLocation", SEARCH_AROUND_LOCATIONS);
        matcher.addURI(AUTHORITY, "aroundLocation/#", GET_AROUND_LOCATION);
        // For suggestions table
        matcher.addURI(AUTHORITY, "aroundLocation/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, "aroundLocation/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
                SEARCH_SUGGEST);
        return matcher;
    }

//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        // Implement this to handle requests to delete one or more rows.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

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
//                return AROUND_LOCATIONS_MIME_TYPE;
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//        // TODO: Implement this to handle requests to insert a new row.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public boolean onCreate() {
//        // TODO: Implement this to initialize your content provider on startup.
//        return false;
//    }

    private Object[] createRow(Integer id, String text1, String text2) {
        return new Object[] { id, // _id
                text1, // text1
                text2, // text2
                text1, // data to be sent when select from list as query
//                text2, // data to be sent as extra string when select from list as result
                "android.intent.action.SEARCH", // action
//                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID};
                id};
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //        String query = uri.getLastPathSegment().toLowerCase();
        String query;
        if (selectionArgs != null)
            query = selectionArgs[0];
        else
            query = uri.getLastPathSegment().toLowerCase();

//        Cursor recentCursor = super.query(uri, projection, selection, selectionArgs,
//                sortOrder);
        Cursor recentCursor;
//        if (query == null || query.length() == 0)
//            recentCursor = super.query(uri, projection, null, selectionArgs,
//                    sortOrder);
//        else
            recentCursor = super.query(uri, projection, selection, selectionArgs,
                sortOrder);

        if (query == null || query.length() == 0)
            return recentCursor;

        String[] SEARCH_SUGGEST_COLUMNS = {
                SearchManager.SUGGEST_COLUMN_FORMAT,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                BaseColumns._ID };

//        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID});
        MatrixCursor matrixCursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
//        MatrixCursor matrixCursor = new MatrixCursor(COLUMNS);

        SuggestionsResult suggestionsResult = getAroundLocationSuggestions(query);
        if (suggestionsResult != null) {
            List<Cursor> cursorList = new ArrayList<Cursor>();
            int idCol = 0;
            if (suggestionsResult.getCity() != null) {
                MatrixCursor cityCursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
                for (AroundPlaceCity city : suggestionsResult.getCity()) {
                    cityCursor.addRow(new Object[]{"", null, city.getName(), city.getHouseCountText(), null, ++idCol});
                }
                cursorList.add(cityCursor);
            }
            if (suggestionsResult.getAttraction() != null) {
                MatrixCursor attractionCursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
                for (AroundPlaceAttraction attraction : suggestionsResult.getAttraction()) {
                    attractionCursor.addRow(new Object[]{"", null, attraction.getName(), attraction.getAroundHousesText(), null, ++idCol});
                }
                cursorList.add(attractionCursor);
            }
            if (suggestionsResult.getHouse() != null) {
                MatrixCursor houseCursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
                for (AroundPlaceHouse house : suggestionsResult.getHouse()) {
                    houseCursor.addRow(new Object[]{"", null, house.getName(), house.getSummary(), null, ++idCol});
                }
                cursorList.add(houseCursor);
            }
            if (cursorList.size() > 0 ) {
                Cursor[] cursorArray = new Cursor[cursorList.size()];
                cursorList.toArray(cursorArray);
                return new MergeCursor(cursorArray);
            } else {
                return matrixCursor;
            }
        }

//        AroundLocation[] aroundLocations = searchAroundLocations(query);
        //TODO : below codes must be removed.
        AroundLocation[] aroundLocations = null;
        if (aroundLocations != null) {
            for (int i = 0; i < aroundLocations.length; i++) {
                AroundLocation aroundLocation = aroundLocations[i];
                long id = i + 1;
                String type = aroundLocation.getType();
                String name = "";
                String summary = "";
                if (aroundLocation.getData() != null) {
                    name = aroundLocation.getData().toString();

                    if ("House".equals(type)) {
                        AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();
                        name = house.getName();
                        summary = house.getSummary();
                    } else if ("City".equals(type)) {
                        AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();
                        name = city.getName();
                        summary = "";
                    } else if ("Attraction".equals(type)) {
                        AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();
                        name = attraction.getName();
                        summary = "";
                    }
//                    name = type;
//                }

//                    matrixCursor.addRow(new Object[]{id, name, type, id});
                    matrixCursor.addRow(new Object[]{"", null, name, summary, null, id});

//                    matrixCursor.addRow(createRow(new Integer(i), name, summary));

//                    matrixCursor.addRow(new Object[] { new Integer(i+1), // _id
//                            name, // text1
//                            summary, // text2
//                            name, "android.intent.action.SEARCH", // action
//                            id });
//                            SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT });
                }
            }
        }
            Cursor[] cursors = new Cursor[] { recentCursor, matrixCursor};
            MergeCursor mergeCursor = new MergeCursor(cursors);
//        return matrixCursor;
        if (query == null || query.length() == 0)
            return recentCursor;
        else
            return mergeCursor;

//            switch (sURIMatcher.match(uri)) {
//                case SEARCH_SUGGEST:
////                    return matrixCursor;
////                    if (query == null || query.length() == 0)
////                        return recentCursor;
////                    else
//                        return mergeCursor;
////                    return recentCursor;
//                case SEARCH_AROUND_LOCATIONS:
////                    return matrixCursor;
////                    if (query == null || query.length() == 0)
////                        return recentCursor;
////                    else
//                        return mergeCursor;
//            case GET_AROUND_LOCATION:
////                return getAroundLocation(uri);
//                throw new IllegalArgumentException("getAroundLocation, Not implemented yet, uri : " + uri);
//                default:
////                    if (query == null || query.length() == 0)
////                        return recentCursor;
////                    else
////                        return mergeCursor;
//                    return mergeCursor;
////                    return matrixCursor;
////                    throw new IllegalArgumentException("Unknown Uri: " + uri);
//            }



//        return matrixCursor;
//        return recentCursor;
    }


    private SuggestionsResult getAroundLocationSuggestions(String query) {
        SuggestionsServiceAsyncTask suggestionsAsyncTask = new SuggestionsServiceAsyncTask(query);
        Object object = null;
        try {
            object = suggestionsAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (object != null)
            return (SuggestionsResult)object;

        return null;
    }

    public AroundLocation[] searchAroundLocations(String query) {

        SearchServiceAsyncTask searchAsyncTask = new SearchServiceAsyncTask(query);
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

//    @Override
//    public int update(Uri uri, ContentValues values, String selection,
//                      String[] selectionArgs) {
//        // TODO: Implement this to handle requests to update one or more rows.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    class RestDeserializer<T> implements JsonDeserializer<T> {

        private Class<T> mClass;
        private String mKey;

        public RestDeserializer(Class<T> targetClass, String key) {
            mClass = targetClass;
            mKey = key;
        }

        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonElement content = je.getAsJsonObject().get(mKey);
            return new Gson().fromJson(content, mClass);

        }
    }
}
