  package com.naze.typing_practice;

  import android.app.Activity;
  import android.database.sqlite.SQLiteDatabase;
  import android.graphics.Color;
  import android.graphics.Point;
  import android.graphics.drawable.BitmapDrawable;
  import android.os.Bundle;
  import android.os.Handler;
  import android.os.Message;
  import android.text.Editable;
  import android.text.InputFilter;
  import android.text.Spannable;
  import android.text.SpannableString;
  import android.text.TextWatcher;
  import android.text.style.ForegroundColorSpan;
  import android.util.Log;
  import android.view.Display;
  import android.view.Gravity;
  import android.view.View;
  import android.widget.Button;
  import android.widget.EditText;
  import android.widget.PopupWindow;
  import android.widget.RelativeLayout;
  import android.widget.TextView;
  import android.widget.Toast;

  import androidx.appcompat.app.AppCompatActivity;

  public class LongTestActivity extends AppCompatActivity {

    private static final int MESSAGE_TIMER_START = 1000;
    private static final int MESSAGE_TIMER_PAUSE = 1001;
    private static final int MESSAGE_TIMER_STOP = 1002;

    private long BACK_PRESS_TIME = 0;

    private static boolean GAME_START = false;

    TimerHandler timerHandler = null;

    RelativeLayout main_view;

    TextView tv_main_sentence, tv_sub_sentence;
    TextView tv_txt, tv_time, tv_process,tv_accuracy;
    TextView tv_txt_cnt, tv_time_cnt, tv_process_cnt, tv_accuracy_cnt;

    Button btn_enter;
    Button btn_start, btn_setting;

    EditText et_sentence;

    String title;
    String[] text;

    Boolean timer = false;

    static int sen_num = 0; //문장 번호
    static int txt_cnt = 0; //타수
    static int time = 0; //시간

    String savedWord =""; //글자
    int savedByte = 0; //바이트
    int nextS = 0; //문장 넘어가는 거 체크 - 문장 넘어갈때 savedByte (start-1,start) 오류 땜에 설정

    int type_cnt = 0; //총 글자 개수
    int typo_cnt = 0; //총 오타 개수

    int type_sen_cnt = 0; //문장 별 글자 개수
    int typo_sen_cnt = 0; //문장 별오타 개수

    double accuracy = 0;

    String[] array_substring; //문장 나누기 (계산횟수 최소화하기 위해서)

    int[] array_check; //문장 오타 검사

    View result_view;
    PopupWindow result_popup;

    int standardSize_X, standardSize_Y;
    float density;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_practice);

        main_view = (RelativeLayout)findViewById(R.id.main_view);

        tv_main_sentence = (TextView)findViewById(R.id.tv_main_sentence);
        tv_sub_sentence = (TextView)findViewById(R.id.tv_sub_sentence);

        tv_txt = (TextView)findViewById(R.id.tv_txt);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_process = (TextView)findViewById(R.id.tv_process);
        tv_accuracy= (TextView)findViewById(R.id.tv_accuracy);

        tv_txt_cnt = (TextView)findViewById(R.id.tv_txt_cnt);
        tv_time_cnt = (TextView)findViewById(R.id.tv_time_cnt);
        tv_process_cnt = (TextView)findViewById(R.id.tv_process_cnt);
        tv_accuracy_cnt = (TextView)findViewById(R.id.tv_accuracy_cnt);

        btn_enter = (Button)findViewById(R.id.btn_enter);

        btn_start = (Button)findViewById(R.id.btn_start);
        et_sentence = (EditText)findViewById(R.id.et_sentence);

        getStandardSize();
        getDisplaySize();

        activity_start();

        result_view = View.inflate(LongTestActivity.this,R.layout.activity_long_result,null);
        //popup_1 = new PopupWindow(popup_view_1, standardSize_X * (80/100), standardSize_Y * (60/100), true);
        result_popup = new PopupWindow(result_view, (int)(width * 0.8), (int)(height * 0.3), true);
        Log.d("오류",standardSize_X+"/"+standardSize_Y);
        result_popup.setOutsideTouchable(true); // 다른 부분에 이벤트 주기
        result_popup.setBackgroundDrawable(new BitmapDrawable()); //이벤트 들어오는 부분

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer) { //타이머가 켜져 있음 -> 일시정지
                    timerHandler.removeMessages(MESSAGE_TIMER_START);
                    timer = false;
                    btn_start.setText("시작하기");
                    active_set(false);
                    //Log.d("Time set",timerHandler.time+""); 시간 불러오기
                }
                else { //일시정지 상태 -> 타이머 (재)시작
                    timerHandler.sendEmptyMessage(MESSAGE_TIMER_START);
                    timer = true;
                    btn_start.setText("일시정지");
                    active_set(true);

                    if(!GAME_START){
                        game_reset();
                        tv_main_sentence.setText(text[sen_num]);
                        tv_sub_sentence.setText(text[sen_num+1]);
                        et_sentence.setFilters(new InputFilter[] { new InputFilter.LengthFilter(text[sen_num].length())}); //글자 최대 수 제한
                        tv_process_cnt.setText("1/"+text.length);
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
                //Log.d("TextWatcher",s+"/"+start+"/"+before+"/"+count);

                if(before <= count ) {
                    savedWord = s.toString().substring(start,start+1);
                    sentence_check(start);
                    if(before < count) {
                        savedByte = savedWord.getBytes().length;
                        txt_cnt += savedByte; //타수에 byte 추a가
                    }
                }
                else if(before > count) {
                    sentence_check(start);
                    txt_cnt -= savedByte; //타수에 byte 감소
                    if (nextS == 0) {
                        nextS = 1;
                    } else if (nextS == 1) {
                        if(s.toString().length() > 0) {
                            savedByte = s.toString().substring(start - 1, start).getBytes().length;
                        }
                    }
                    //문제 발생 글자마다 byte 달라서 여러번 줄일 때 오류 발생
                }
                //Log.d("TextWatcher","타수: "+txt_cnt+"/"+savedWord);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

/*        et_sentence.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        nextSentence();
                        break;
                }
                return true;
            }
        });*/

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.length > sen_num) { //텍스트 모든 문장 입력했는지 확인
                    if (text[sen_num].length() <= et_sentence.getText().toString().length()) { //한 문장 끝까지 입력했나 확인
                        nextSentence();
                    } else {
                        Toast.makeText(getApplication(), "문장을 전부 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //결과창 띄우기
                    Toast.makeText(LongTestActivity.this, "결과창 예정", Toast.LENGTH_SHORT).show();
                    timerHandler.removeMessages(MESSAGE_TIMER_START);
                }

            }
        });

    }

    private class TimerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMER_START:
                    time -= 1; //1초 추가
                    if(time > 0) {
                        //Log.d("TimerHandler","Timer Start : " + time);
                        this.sendEmptyMessageDelayed(MESSAGE_TIMER_START, 1000);
                    } else {
                        finish_practice();
                    }
                    time_set();
                    //Log.d("TimerHandler","타수  : "+txt_cnt+" / 시간 : "+time);
                    break;
            }
        }
    }

    private void activity_start(){
        clear(); //시작시 변수 초기화 함수

        timerHandler = new TimerHandler();

        sentence_array(); //문장 설정하는 함수

        sentence_separation(0);

    } //액티비티 시작되었을 때


    private void sentence_array(){
        sentence_test();

        for(int i = 0 ; i<text.length ; i++){
            Log.d("가사 테스트",text[i]);
        }

    } //문장 설정

    private void sentence_test (){ //테스트용
        switch (title) {
            case "아름답고도 아프구나":
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
                break;
            case "발표용 테스트" :
                text = new String[]{"테스트용 문장 1",
                        "테스트용 문장 2",
                        "테스트용 문장 3"
                };

        }


    }

    private void sentence_separation(int num_sentence){ //문장 분리

        String str = text[num_sentence]; //문장 번호 받아오기
        array_substring = new String[str.length()];
        array_check = new int[str.length()]; //오타 검사용

        for(int i = 0 ; i < str.length() ; i++) {
            array_substring[i] = str.substring(i,i+1);
//            Log.d("sentence_separation","substring : "+array_substring[i]);
        }

    }

    private void sentence_check(int num){ //오타검사
        String content = tv_main_sentence.getText().toString();

        SpannableString spannableString = new SpannableString(content);

        type_sen_cnt = 0;
        typo_sen_cnt = 0;

        if(array_substring[num].equals(savedWord)) {
            //Log.d("sentence_check","오타가 없음!"+array_substring[num]+"/"+savedWord);
            array_check[num] = 0; //오타 없음 = 0
        }
        else {
            //Log.d("sentence_check","오타가 있음!"+array_substring[num]+"/"+savedWord);
            array_check[num] = 1; //오타 있음 = 1
        }


        for(int i = 0 ; i < num ; i++ ){
            type_sen_cnt += 1;
            if(array_check[i] == 0) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),i,i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if(array_check[i] == 1) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#CC4444")),i,i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                typo_sen_cnt += 1;
            }
        }
        tv_main_sentence.setText(spannableString);
        // i<=num 이면 즉시 검사
        // i<num 이면 다음 글자 입력시 검사
        if (num >= tv_main_sentence.getText().toString().length()-1){
            typo_cnt += typo_sen_cnt;
            type_cnt += type_sen_cnt;
        }
        Log.d("sentence_check",typo_cnt+"/"+typo_sen_cnt+"/"+type_cnt+"/"+type_sen_cnt);
        accuracy = (double)(typo_cnt + typo_sen_cnt) / (type_cnt + type_sen_cnt);
        Log.d("sentence_check","정확도" +accuracy);
        tv_accuracy_cnt.setText((String.format("%.1f",(1-accuracy)*100))+"%");


    }

    private void game_reset(){
        sen_num = 0;
        txt_cnt = 0;

    }

    private void nextSentence(){
        sen_num += 1;
        if(sen_num < text.length) {
            tv_main_sentence.setText(text[sen_num]);
            if(sen_num + 1 < text.length){
                tv_sub_sentence.setText(text[sen_num+1]);
            } else {
                tv_sub_sentence.setText("");
            }

            et_sentence.setText("");
            et_sentence.setFilters(new InputFilter[] { new InputFilter.LengthFilter(text[sen_num].length())}); //글자 최대 수 제한
            sentence_separation(sen_num);

            nextS = 0;
            tv_process_cnt.setText(sen_num+"/"+text.length);
        } else {
            Log.d("nextSentence","게임 종료");
            finish_practice();
        }

    }

    private void time_set(){ //1초마다 업데이트
        tv_time_cnt.setText(String.format("%02d",time/60)+":"+String.format("%02d",time%60));
        tv_txt_cnt.setText(String.format("%d",(int)((double)txt_cnt/time*60))+"타");
    }

    private void active_set(boolean bool) { //활성화 비활성화
        if(bool) {
            et_sentence.setEnabled(true);
            btn_enter.setEnabled(true);
        } else {
            et_sentence.setEnabled(false);
            btn_enter.setEnabled(false);
        }

    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-BACK_PRESS_TIME >= 2000) {
            BACK_PRESS_TIME = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        } else if(System.currentTimeMillis()-BACK_PRESS_TIME < 2000) {
            finish();
        }
    }

    public void clear(){ //액티비티 실행시 초기화
        GAME_START = false;
        sen_num = 0; //문장 번호
        txt_cnt = 0; //타수
        time = getIntent().getIntExtra("time",0); //시간

        title = getIntent().getStringExtra("title");

        savedWord =""; //글자
        savedByte = 0; //바이트
        nextS = 0;

        type_cnt = 0; //총 글자 개수
        typo_cnt = 0; //총 오류 개수

        accuracy = 0;


    }

    public void finish_practice(){ //게임 종료

        timerHandler.removeMessages(MESSAGE_TIMER_START);

        //disabled 다 박기

        result_popup.showAtLocation(main_view, Gravity.CENTER,0,0);
        result_popup.setAnimationStyle(-1);

        TextView tv_result_title = (TextView)result_view.findViewById(R.id.tv_result_title);
        TextView tv_result_time = (TextView)result_view.findViewById(R.id.tv_result_time);
        TextView tv_result_type = (TextView)result_view.findViewById(R.id.tv_result_type);
        TextView tv_result_acc = (TextView)result_view.findViewById(R.id.tv_result_acc);

        Button btn_result_close = (Button)result_view.findViewById(R.id.btn_result_close);

        tv_result_title.setText(title);
        tv_result_time.setText(String.format("%02d",time/60)+":"+String.format("%02d",time%60));
        tv_result_type.setText(String.format("%d",(int)((double)txt_cnt/time*60))+"타");
        tv_result_acc.setText((String.format("%.1f",(1-accuracy)*100))+"%");

        insertResult(title, (int)((double)txt_cnt/time*60), (int)(1-accuracy)*100);

        btn_result_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_popup.dismiss();
                finish();
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

    void insertResult(String title, int txt, int acc){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();

        String qry ;

        qry = "INSERT INTO RESULT_TEST VALUES(null, '"+ title+ "', '" + txt + "', '" + acc + "')";

        database.execSQL(qry);
    }


}