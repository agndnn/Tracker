package com.example.tracker;
//import static com.example.tracker.Params.usersOut;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "params.db";
    private static final int DATABASE_VERSION = 2;

    // Таблица для параметров
    private static final String TABLE_NAME = "params";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_CODE = "userCode";
    private static final String COLUMN_USER_PHONE = "userPhone";
    private static final String COLUMN_USER_NAME = "userName";

    // Таблица для пользователей
    private static final String USERS_TABLE_NAME = "users";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_PHONE = "phone";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Создание таблицы параметров
        String createParamsTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_CODE + " TEXT, " +
                COLUMN_USER_PHONE + " TEXT, " +
                COLUMN_USER_NAME + " TEXT)";
        db.execSQL(createParamsTable);

        // Создание таблицы пользователей
        String createUsersTable = "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                COLUMN_CODE + " TEXT PRIMARY KEY, " +
                COLUMN_PHONE + " TEXT)";
        db.execSQL(createUsersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> getNumberList(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(USERS_TABLE_NAME, null, null, null, null, null, null);
        ArrayList <String> phones = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                phones.add(cursor.getString(1));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return phones;
    }
    public void insertParams() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Удаляем существующие параметры, если они есть
        db.delete(TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_CODE, Params.userCode);
        values.put(COLUMN_USER_PHONE, Params.userPhone);
        values.put(COLUMN_USER_NAME, Params.userName);

        db.insert(TABLE_NAME, null, values);

        //пользователи
        db.delete(USERS_TABLE_NAME, null, null);

        Log.debug("Params.usersOut.size="+Params.usersOut.size());
        for (User user : Params.usersOut) {
            values = new ContentValues();
            values.put(COLUMN_CODE, user.getCode());
            values.put(COLUMN_PHONE, user.getPhone());

            db.insert(USERS_TABLE_NAME, null, values);
        }

        db.close();

    }

    @SuppressLint("Range")
    public void getParams() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Params.userCode = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CODE));
            Params.userPhone = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE));
            Params.userName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
            cursor.close();
        }


        Params.usersOut.clear();
        cursor = db.query(USERS_TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndex(COLUMN_CODE));
                String userPhone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                Params.usersOut.add(new User(code, userPhone));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

    }

}