package com.asa.imhere.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.asa.imhere.model.ImHereContract.FavoriteEntry;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DatabaseQueries {

    public static Uri saveFavorite(Context context, Favorite favorite) {
        return cupboard().withContext(context).put(FavoriteEntry.CONTENT_URI, favorite);
    }

    public static Cursor getFavoritesCursor(Context context) {
        return cupboard().withContext(context).query(FavoriteEntry.CONTENT_URI, Favorite.class).getCursor();
    }

    public static Favorite getFavoriteById(Context context, long id) {
        Uri uri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, id);
        return cupboard().withContext(context).get(uri, Favorite.class);
    }

    public static Favorite getFavoriteByVenueId(Context context, String venueId) {
        return cupboard().withContext(context).query(FavoriteEntry.CONTENT_URI, Favorite.class).withSelection("venue_id=?", venueId).get();
    }

    public static boolean isInDatabase(Context context, String venueId) {
        return getFavoriteByVenueId(context, venueId) != null;
    }

}
