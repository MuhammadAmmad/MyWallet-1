package com.example.vlad.personalwallet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vlad on 13.03.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "walletdatabase.db"; // name database
    private static final int DATABASE_VERSION = 1; // version database

    // names of database tables
    public static final String DATABASE_TABLE_WALLET = "wallet_table";

    //  table column names
    public static final String COLUMN_ID = "_id";
    public static final String DATE_COLUMN = "date";
    public static final String TIME_COLUMN = "time";
    public static final String CASH_COLUMN = "cash";
    public static final String CATEGORY_SPEND_COLUMN = "category_spend";
    public static final String CATEGORY_INCOME_COLUMN = "category_income";
    public static final String PHOTO_COLUMN = "photo";
    public static final String COMMENT_COLUMN = "comment";

    // constants for creating tables
    public static final String CREATE_TABLE_PROFITS = "create table "
            + DATABASE_TABLE_WALLET + " (" + COLUMN_ID + " integer primary key autoincrement, "
            + DATE_COLUMN + " text, " + TIME_COLUMN + " text, " + CASH_COLUMN + " real, "
            + CATEGORY_SPEND_COLUMN + " text default NULL, " + CATEGORY_INCOME_COLUMN + " text default NULL, "
            + PHOTO_COLUMN + " text, " + COMMENT_COLUMN + " text);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create database
        db.execSQL(CREATE_TABLE_PROFITS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // delete old table
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_WALLET);

        // create new table
        onCreate(db);
    }

}
