package xyz.narengi.android.content;

import android.content.SearchRecentSuggestionsProvider;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchRecentQuerySuggestionsProvider extends SearchRecentSuggestionsProvider {
//    public final static String AUTHORITY = "com.example.MySuggestionProvider";
    public static final String AUTHORITY = "xyz.narengi.android.content.SearchRecentQuerySuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public SearchRecentQuerySuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
