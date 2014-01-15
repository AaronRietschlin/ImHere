package com.asa.imhere.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.asa.imhere.AppData;

/**
 * Created by Aaron on 1/13/14.
 */
public class ImHereContract  {


    public static abstract class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "Favorite";
        public static final Uri CONTENT_URI = getUri(TABLE_NAME);
        public static final String CONTENT_TYPE = getContentListType(TABLE_NAME);
        public static final String CONTENT_TYPE_ITEM = getContentItemType(TABLE_NAME);

    }

    private static Uri getUri(String tableName) {
        return Uri.parse("content://" + AppData.AUTHORITY + "/" + tableName);
    }

    private static String getContentListType(String name) {
        return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd." + AppData.AUTHORITY + "." + name;
    }

    private static String getContentItemType(String name) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd." + AppData.AUTHORITY + "." + name;
    }
}
