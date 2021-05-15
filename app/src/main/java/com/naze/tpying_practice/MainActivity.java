package com.naze.tpying_practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_menu_1;

        btn_menu_1 = (Button)findViewById(R.id.btn_menu_1);

        btn_menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LongPracticeActivity.class);

                intent.putExtra("num",1); //글 번호
               startActivity(intent);
            }
        });
    }
}//