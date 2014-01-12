package com.asa.imhere;

import android.content.SearchRecentSuggestionsProvider;

/**
 * ContentProvider that handles suggestions. See:
 * http://developer.android.com/guide
 * /topics/search/adding-recent-query-suggestions.html
 */
public class SearchSuggestionContentProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "com.asa.imhere.SearchSuggestionContentProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public SearchSuggestionContentProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

}
