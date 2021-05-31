package com.naze.typing_practice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RecordActivity extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        list = (ListView)findViewById(R.id.listView);

        displayList();
    }

    public void displayList(){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM RESULT_TEST", null);

        ListViewAdapter adapter = new ListViewAdapter();

        while(cursor.moveToNext()) {
            adapter.addItem(cursor.getString(1),cursor.getInt(2),cursor.getInt(3));
        }

        list.setAdapter(adapter);
    }
}