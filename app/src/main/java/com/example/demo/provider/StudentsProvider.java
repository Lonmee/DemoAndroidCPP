package com.example.demo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class StudentsProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.demo.provider.College";
    static final String URL = "content://" + PROVIDER_NAME + "/students";

    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String GRADE = "grade";

    private SQLiteDatabase writableDatabase;

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int STUDENTS = 1;
    static final int STUDENT_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID);
    }

    private static final String DATABASE_NAME = "College";
    private static final int DATABASE_VERSION = 1;
    private static final String STUDENTS_TABLE_NAME = "students";
    private static final String CREATE_DB_TABLE = " CREATE TABLE " + STUDENTS_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " name TEXT NOT NULL, " +
            " grade TEXT NOT NULL);";

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        writableDatabase = databaseHelper.getWritableDatabase();
        return writableDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(STUDENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                sqLiteQueryBuilder.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;
            case STUDENT_ID:
                sqLiteQueryBuilder.appendWhere(_ID + "=" + uri.getPathSegments() + uri);
                break;
            default:
                try {
                    throw new IllegalAccessException("Unknown URI" + uri);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = NAME;
        }

        Cursor query = sqLiteQueryBuilder.query(writableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                return "vnd.android.cursor.dir/com.example.demo.students";
            case STUDENT_ID:
                return "vnd.android.cursor.item/com.example.demo.students";
            default:
                try {
                    throw new IllegalAccessException("Unsupported URI: " + uri);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = writableDatabase.insert(STUDENTS_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = writableDatabase.delete(STUDENTS_TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                String id = uri.getPathSegments().get(1);
                count = writableDatabase.delete(STUDENTS_TABLE_NAME, _ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                try {
                    throw new IllegalAccessException("Unknown URI " + uri);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = writableDatabase.update(STUDENTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case STUDENT_ID:
                count = writableDatabase.update(STUDENTS_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                try {
                    throw new IllegalAccessException("Unknown URI " + uri);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
            onCreate(db);
        }

    }

}
