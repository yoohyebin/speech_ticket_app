package com.example.ambidext.speechai;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.os.SystemClock.sleep;

public class train_re extends AppCompatActivity {
    private MyDBHelper myDBHelper;
    private MyDBTrain myDBTrain;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech tts;
    int i=-1;
    int j=0;
    int k=0;
    int count=1;
    int a=0;
    String price;
    String train_time;
    String s_station;
    String e_station;
    String password;
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("MM월dd일HH시mm분");
    String c_time = sdfNow.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_re);
        myDBHelper=new MyDBHelper(this);
        myDBTrain=new MyDBTrain(this);
        btnSpeak = (ImageButton) findViewById(R.id.btnRe);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeech);

        if(savedInstanceState==null){
            SharedPreferences prefs =getSharedPreferences("card_info",0);
            String card = prefs.getString("card", "0");
            password=prefs.getString("password", "*");
        }
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate(0.8f);
                }
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==-1){
                    tts_text("출발역은 어디입니까");
                }
                else if(i==0)
                    promptSpeechInput();
                else if(i==1){
                    tts_text("출발역이 "+txtSpeechInput.getText().toString()+" 맞습니까 맞다면 클릭을 틀리다면 길게 눌러주세요");
                    s_station=txtSpeechInput.getText().toString();
                }
                else if(i==2){
                    tts_text("도착역이 어디입니까");
                }
                else if(i==3){
                    promptSpeechInput();
                }
                else if(i==4){
                    tts_text("도착역이 "+txtSpeechInput.getText().toString()+" 맞습니까 맞다면 클릭을 틀리다면 길게 눌러주세요");
                    e_station=txtSpeechInput.getText().toString();
                }
                else if(i==5){
                    train_info();
                }
                else if(i==6) {
                    tts_text("예약할 날짜와 시간을 말해주세요");
                }
                else if(i==7)
                    promptSpeechInput();
                else if(i==8){
                    train_time=txtSpeechInput.getText().toString();
                    train_time=train_time.replaceAll(" ","");
                    tts_text("예약하려는 날짜와 시간이"+txtSpeechInput.getText().toString()+" 맞습니까 맞다면 클릭을 틀리다면 길게 눌러주세요");
                }
                else if(i==9){
                    train_time1();
                }
                else if(i==10)
                    tts_text("결제하기 위해 비밀번호를 입력해 주세요");
                else if(i==11) {
                    promptSpeechInput();
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            train_pay();
                        }
                    }, 6000);
                }
                i++;
            }
        });
        btnSpeak.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                i-=2;
                tts_text("다시 말해주세요  ");
                return false;
            }
        });
    }
    public void train_pay(){
       while(k<3){
            String input_passWord=txtSpeechInput.getText().toString();
            input_passWord=input_passWord.replaceAll(" ","");
            if(password.equals(input_passWord)){
                tts_text("비밀번호가 일치 합니다. 결제를 진행합니다.");
                train_insert();
                break;
            }
            else {
                tts_text("비밀번호가 일치하지 않습니다. 재 입력해주세요 ");
                k++;
                sleep(3000);
                i=11;
                break;
            }
        }
    }
    public void train_time1(){
        SQLiteDatabase db;
        String[] projection ={"_id","s_station","e_station","time","price"};
        Cursor cur;
        db = myDBTrain.getReadableDatabase();
        cur = db.query("train_info", projection, null, null, null, null, null);
        showResult1(cur);
        cur.close();
        if(a==0){
            tts_text("예약하려는 시간이 없습니다. 다시 입력해주세요");
            i=6;
        }
    }
    private void showResult1(final Cursor cur){
        int s_station_col=cur.getColumnIndex("s_station");
        int e_station_col=cur.getColumnIndex("e_station");
        int t_time_col=cur.getColumnIndex("time");
        while (cur.moveToNext()) {
            String start_station = cur.getString(s_station_col);
            String end_station = cur.getString(e_station_col);
            String t_time = cur.getString(t_time_col);
            int compare = c_time.compareTo(t_time);
            if(s_station.equals(start_station) && e_station.equals(end_station)){
                if (train_time.equals(t_time)) {
                    a++;
                }
            }
        }
    }
    public void train_insert(){
        SQLiteDatabase db;
        ContentValues values;
        db=myDBHelper.getWritableDatabase();
        values=new ContentValues();
        values.put("s_station",s_station);
        values.put("e_station",e_station);
        values.put("time",train_time);
        values.put("price",price);
        db.insert("train",null,values);
        myDBHelper.close();
        tts_text("예약 완료");
    }

    public void train_info(){
        SQLiteDatabase db;
        String[] projection ={"_id","s_station","e_station","time","price"};
        Cursor cur;
        db = myDBTrain.getReadableDatabase();

        cur = db.query("train_info", projection, null, null, null, null, null);
        if (cur != null) {
            showResult(cur);
            cur.close();
        }
        if(count==1)
            tts_text("출발역"+s_station+"  "+"도착역 "+e_station+" "+"은 정보가 없습니다.");
    }
    private void showResult(final Cursor cur){

                int s_station_col=cur.getColumnIndex("s_station");
                int e_station_col=cur.getColumnIndex("e_station");
                int t_time_col=cur.getColumnIndex("time");
                int price_col=cur.getColumnIndex("price");
                while (cur.moveToNext()) {
                    String start_station = cur.getString(s_station_col);
                    String end_station = cur.getString(e_station_col);
                    String t_time = cur.getString(t_time_col);
                    String p = cur.getString(price_col);
                    int compare = c_time.compareTo(t_time);
                    if(s_station.equals(start_station) && e_station.equals(end_station)){
                        if (compare < 0) {
                            Log.d("LogTest","count="+count+"시간"+t_time);
                            tts_text(count+" 번 "+ start_station + "출발 " +  end_station + "도착 " + t_time + "가격은" + p + "원");
                            sleep(7000);
;                            price=p;
                            count++;
                        }
                    }
                }
            }

    void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void tts_text(String text) {
        //http://stackoverflow.com/a/29777304
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
class MyDBTrain extends SQLiteOpenHelper {
    public MyDBTrain(Context context){
        super(context, "mytrain_info.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE train_info(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "s_station TEXT, e_station TEXT, time TEXT,price TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS train_info;");
        onCreate(db);
    }
}