package com.asa.imhere.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.asa.imhere.model.ImHereContract.FavoriteEntry;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Aaron on 1/13/14.
 */
public class IHContentPrvoider extends ContentProvider {

    public static String AUTHORITY = "nl.qbusict.cupboard.example.provider";
    public static final Uri AUTHOR_URI = Uri.parse("content://" + AUTHORITY + "/author");
    public static final Uri BOOKS_URI = Uri.parse("content://" + AUTHORITY + "/book");

    private IHSqlOpenHelper mDbHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int LIST_FAVORITE = 0;
    private static final int ITEM_FAVORITE = 1;


    static {
        sUriMatcher.addURI(AUTHORITY, FavoriteEntry.TABLE_NAME, LIST_FAVORITE);
        sUriMatcher.addURI(AUTHORITY, FavoriteEntry.TABLE_NAME + "/#", ITEM_FAVORITE);
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new IHSqlOpenHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (db == null) {
            return null;
        }

        long insertedId = -1;
        switch (sUriMatcher.match(uri)) {
            case LIST_FAVORITE:
            case ITEM_FAVORITE:
                insertedId = cupboard().withDatabase(db).put(Favorite.class, values);
                break;
        }

        if (insertedId > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, insertedId);
            notifyChange(itemUri);
            return itemUri;
        }

        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case LIST_FAVORITE:
                // this is the full query syntax, most of the time you can leave out projection etc
                // if the content provider returns a fixed set of data
                cursor = cupboard().withDatabase(db).query(Favorite.class).
                        withProjection(projection).
                        withSelection(selection, selectionArgs).
                        orderBy(sortOrder).
                        getCursor();
                break;
            case ITEM_FAVORITE:
                cursor = cupboard().withDatabase(db).query(Favorite.class).
                        byId(ContentUris.parseId(uri)).
                        getCursor();
                break;
        }
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updateCount = 0;
        switch (sUriMatcher.match(uri)) {
            case LIST_FAVORITE:
            case ITEM_FAVORITE:
                updateCount = cupboard().withDatabase(db).update(Favorite.class, values, selection, selectionArgs);
                break;
        }

        if (updateCount > 0) {
            notifyChange(uri);
        }
        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deleteCount = 0;
        switch (sUriMatcher.match(uri)) {
            case LIST_FAVORITE:
            case ITEM_FAVORITE:
                deleteCount = cupboard().withDatabase(db).delete(Favorite.class, selection, args);
                break;
        }

        if (deleteCount > 0) {
            notifyChange(uri);
        }
        return deleteCount;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
