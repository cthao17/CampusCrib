package com.cs407.campuscrib;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;
    public DBHelper(SQLiteDatabase sqLiteDatabase) { this.sqLiteDatabase = sqLiteDatabase; }
    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users " + "(id INTEGER PRIMARY KEY, username TEXT, password TEXT)");
    }
    public void addUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long newRowId = sqLiteDatabase.insert("users", null, values);
    }
    public boolean isUserValid(String username, String password) {
        String[] projection = {"username", "password"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = sqLiteDatabase.query(
                "users",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid = cursor.moveToFirst();
        cursor.close();

        return isValid;
    }
}
