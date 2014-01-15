package com.asa.imhere.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Aaron on 1/13/14.
 */
public class IHSqlOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int VERSION = 1;

    static {
        // register the models with cupboard. A model should be registered before it can be
        // used in any way. There are a few options to make sure the models are registered:
        // 1. In a static block like this in a SQLiteOpenHelper or ContentProvider
        // 2. In a custom Application class either form a static block or onCreate
        // 3. By creating your own factory class and have the static block there.

        cupboard().register(Favorite.class);
    }

    public IHSqlOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables won't upgrade tables, unlike upgradeTables() below.
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
