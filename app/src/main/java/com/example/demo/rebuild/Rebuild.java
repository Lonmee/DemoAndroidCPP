package com.example.demo.rebuild;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.provider.StudentsProvider;

public class Rebuild extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 0;
    private static final int DAY_VIEW_MODE = 0;
    private static final int WEEK_VIEW_MODE = 1;
    private int view_mode;
    private SharedPreferences here;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebuild);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        here = getSharedPreferences("here", MODE_PRIVATE);
        view_mode = here.getInt("view_mode", DAY_VIEW_MODE);
        Log.i("view mode :", String.valueOf(view_mode));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor edit = here.edit();
        edit.putInt("view_mode", WEEK_VIEW_MODE);
        edit.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("pressed:", String.valueOf(keyCode));
        if (keyCode == KeyEvent.KEYCODE_C) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.i("result", data.toString());
                startActivity(new Intent(Intent.ACTION_VIEW));
            }
        }
    }

    public void onClickAddName(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StudentsProvider.NAME,
                ((EditText) findViewById(R.id.editText_name)).getText().toString());

        contentValues.put(StudentsProvider.GRADE,
                ((EditText) findViewById(R.id.editText_grade)).getText().toString());

        Uri uri = getContentResolver().insert(StudentsProvider.CONTENT_URI, contentValues);

        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

    public void onClickRetrieveStudents(View view) {
        Cursor cursor = managedQuery(StudentsProvider.CONTENT_URI, null, null, null, "_id");
        if (cursor.moveToFirst()) {
            do {
                Log.i("data: ", cursor.getString(cursor.getColumnIndex(StudentsProvider._ID)) +
                        ", " + cursor.getString(cursor.getColumnIndex(StudentsProvider.NAME)) +
                        ", " + cursor.getString(cursor.getColumnIndex(StudentsProvider.GRADE)));
            } while (cursor.moveToNext());
        }
    }
}
