package com.naze.tpying_practice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LongPracticeActivity extends AppCompatActivity {

    private static final int MESSAGE_TIMER_START = 1000;
    private static final int MESSAGE_TIMER_PAUSE = 1001;
    private static final int MESSAGE_TIMER_STOP = 1002;

    private static boolean GAME_START = false;

    TimerHandler timerHandler = null;

    TextView tv_main_sentence, tv_sub_sentence;
    TextView tv_txt, tv_time, tv_process, tv_error;
    TextView tv_txt_cnt, tv_time_cnt, tv_process_cnt, tv_error_cnt;

    Button btn_enter;
    Button btn_start, btn_setting;

    EditText et_sentence;

    String[] text;

    Boolean timer = false;

    static int sen_num = 0; //문장 번호
    static int txt_cnt = 0; //타수
    static int time = 0; //시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_practice);

        tv_main_sentence = (TextView)findViewById(R.id.tv_main_sentence);
        tv_sub_sentence = (TextView)findViewById(R.id.tv_sub_sentence);

        tv_txt = (TextView)findViewById(R.id.tv_txt);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_process = (TextView)findViewById(R.id.tv_process);
        tv_error = (TextView)findViewById(R.id.tv_error);   

        tv_txt_cnt = (TextView)findViewById(R.id.tv_txt_cnt);
        tv_time_cnt = (TextView)findViewById(R.id.tv_time_cnt);
        tv_process_cnt = (TextView)findViewById(R.id.tv_process_cnt);
        tv_error_cnt = (TextView)findViewById(R.id.tv_error_cnt);

        btn_enter = (Button)findViewById(R.id.btn_enter);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_setting = (Button)findViewById(R.id.btn_setting);
        et_sentence = (EditText)findViewById(R.id.et_sentence);

        timerHandler = new TimerHandler();

        sentence_array(); //문장 설정하는 함수

        sentence_separation(0);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer) { //타이머가 켜져 있음 -> 일시정지
                    timerHandler.removeMessages(MESSAGE_TIMER_START);
                    timer = false;
                    btn_start.setText("시작하기");
                    //Log.d("Time set",timerHandler.time+""); 시간 불러오기
                }
                else { //일시정지 상태 -> 타이머 (재)시작
                    timerHandler.sendEmptyMessage(MESSAGE_TIMER_START);
                    timer = true;
                    btn_start.setText("일시정지");
                    if(!GAME_START){
                        game_reset();
                        sentence_view(sen_num); //게임의 시작
                        GAME_START = true;
                    }

                }
            }
        });

        et_sentence.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private class TimerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMER_START:
                    time += 1; //1초 추가
                    Log.d("TimerHandler","Timer Start : " + time);
                    this.sendEmptyMessageDelayed(MESSAGE_TIMER_START, 1000);
                    tv_time_cnt.setText(String.format("%02d",time/60)+":"+String.format("%02d",time%60));

                    tv_txt_cnt.setText(String.format("%d",txt_cnt/time));
                    Log.d("TimerHandler","타수  : "+txt_cnt+" / 시간 : "+time);
                    break;
            }
        }
    }

    private void sentence_array(){
        sentence_test();

        for(int i = 0 ; i<text.length ; i++){
            Log.d("가사 테스트",text[i]);
        }

    } //문장 설정

    private void sentence_test (){ //테스트용
        text = new String[]{"사랑을 만나 이별을 하고",
                "수없이 많은 날을 울고 웃었다",
                "시간이란 건 순간이란 게",
                "아름답고도 아프구나",
                "낭만 잃은 시인 거의 시체 같아",
                "바라고 있어 막연한 보답",
                "아픔을 피해 또 다른 아픔을 만나",
                "옆에 있던 행복을 못 찾았을까",
                "너를 보내고 얼마나",
                "나 많이 후회했는지 몰라",
                "지금 이 순간에도 많은 걸",
                "놓치고 있는데 말이야",
                "시간은 또 흘러 여기까지 왔네요",
                "지금도 결국 추억으로 남겠죠",
                "다시 시작하는 게 이젠 두려운걸요",
                "이별을 만나 아플까 봐",
                "사랑을 만나 이별을 하고",
                "수없이 많은 날을 울고 웃었다",
                "시간이란 건 순간이란 게",
                "아름답고도 아프구나",
                "Yeah love then pain love then pain",
                "Yeah let’s learn from our mistakes",
                "우린 실패로부터 성장해",
                "사랑은 하고 싶지만",
                "Nobody wants to deal with the pain that follows, no",
                "I understand them though",
                "Yeah 이해돼 이해돼 사랑이라는 게",
                "매일 웃게 하던 게 이제는 매일 괴롭게 해",
                "아픈 건 없어지겠지만 상처들은 영원해",
                "But that’s why it’s called beautiful pain",
                "시간은 슬프게 기다리질 않네요",
                "오늘도 결국 어제가 되겠죠",
                "다시 시작하는 게",
                "너무나 힘든걸요",
                "어김없이 끝이 날까 봐",
                "사랑을 만나 이별을 하고",
                "수없이 많은 날을 울고 웃었다",
                "시간이란 건 순간이란 게",
                "아름답고도 아프구나",
                "사랑이란 건 멈출 수 없다 아픔은 반복돼",
                "이렇게도 아픈데 또 찾아와 사랑은 남몰래",
                "우린 누구나가 바보가 돼",
                "무기력하게도 한순간에",
                "오래도록 기다렸다는 듯",
                "아픈 사랑 앞에 물들어가",
                "그대를 만나 사랑을 하고",
                "그 어떤 순간보다 행복했었다",
                "그대는 부디 아프지 말고",
                "아름다웠길 바란다",
                "사랑을 만나 이별을 하고",
                "수없이 많은 날을 울고 웃었다",
                "시간이란 건 순간이란 게",
                "아름답고도 아프구나"
        };

    }

    private void sentence_separation(int num_sentence){ //문장 번호를 받음
        String str = text[num_sentence];
        String[] array_split = str.split(" ");
        String[] array_substring;

        for(int i = 0 ; i < array_split.length ; i++){
            Log.d("sentence_separation","split : "+array_split[i]);
            String str2 = array_split[i];

        }
/*        String str2 = array_split[0];
        int cnt = 0;
        for(int i = 0 ; i < str2.length() ; i++){
            String str3 = str2.substring(i,i+1);
            Log.d("sentence_separation","substring : "+str3+" : "+str2.length());
        }*/

    }

    private void sentence_view(int n){
        tv_main_sentence.setText(text[n]);
        tv_sub_sentence.setText(text[n+1]);
    }

    private void game_reset(){
        sen_num = 0;
        txt_cnt = 0;
        time = 0;
    }
}