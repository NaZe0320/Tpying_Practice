package com.naze.typing_practice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    
    int standardSize_X, standardSize_Y;
    float density;
    int width, height;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View longPracticeView, longTestView;
        PopupWindow longPracticePopup, longTestPopup;

        Button btn_menu_1, btn_menu_2, btn_menu_3, btn_menu_4;

        btn_menu_1 = (Button)findViewById(R.id.btn_menu_1);
        btn_menu_2 = (Button)findViewById(R.id.btn_menu_2);
        btn_menu_3 = (Button)findViewById(R.id.btn_menu_3);
        btn_menu_4 = (Button)findViewById(R.id.btn_menu_4);

        getStandardSize();
        getDisplaySize();

        longPracticeView = View.inflate(MainActivity.this,R.layout.activity_long_practice_enter,null);
        longPracticePopup = new PopupWindow(longPracticeView, (int)(width * 0.8), (int)(height * 0.3), true);
        longPracticePopup.setOutsideTouchable(true); // 다른 부분에 이벤트 주기
        longPracticePopup.setBackgroundDrawable(new BitmapDrawable());

        longTestView = View.inflate(MainActivity.this,R.layout.activity_long_test_enter,null);
        longTestPopup = new PopupWindow(longTestView, (int)(width * 0.8), (int)(height * 0.3), true);
        longTestPopup.setOutsideTouchable(true); // 다른 부분에 이벤트 주기
        longTestPopup.setBackgroundDrawable(new BitmapDrawable());



        btn_menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShortPracticeActivity.class);
                startActivity(intent);
            }
        });

        btn_menu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longPracticePopup.showAtLocation(v, Gravity.CENTER,0,0);
                longPracticePopup.setAnimationStyle(-1);

                Spinner spinner = (Spinner)longPracticeView.findViewById(R.id.title_spinner);
                Button btn = (Button)longPracticeView.findViewById(R.id.btn_enter_LP);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LongPracticeActivity.class);
                        intent.putExtra("title",spinner.getSelectedItem().toString());
                        startActivity(intent);
                    }
                });
            }
        });


        btn_menu_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longTestPopup.showAtLocation(v, Gravity.CENTER,0,0);
                longTestPopup.setAnimationStyle(-1);

                Spinner spinner = (Spinner)longTestView.findViewById(R.id.title_spinner);
                Spinner spinner2 = (Spinner)longTestView.findViewById(R.id.time_spinner);
                Button btn = (Button)longTestView.findViewById(R.id.btn_enter_LP);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LongTestActivity.class);
                        intent.putExtra("title",spinner.getSelectedItem().toString());
                        switch (spinner2.getSelectedItemPosition()) {
                            case 0:
                                intent.putExtra("time",180);
                                break;
                            case 1:
                                intent.putExtra("time",300);
                                break;
                            case 2:
                                intent.putExtra("time",480);
                                break;
                        }
                        startActivity(intent);
                    }
                });
            }
        });


        btn_menu_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

    }


    public Point getScreenSize(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void getStandardSize() {
        Point ScreenSize = getScreenSize(this);
        density = getResources().getDisplayMetrics().density;

        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }

    public void getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        width = size.x;
        height = size.y;
    }
}//