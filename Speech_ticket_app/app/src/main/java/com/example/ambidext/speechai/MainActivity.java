package com.example.ambidext.speechai;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;

import java.util.HashMap;
import java.util.Locale;
import java.util.ArrayList;

import static android.os.FileObserver.CREATE;

public class MainActivity extends AppCompatActivity {
    private MyDBTrain myDBTrain;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech tts;
    int i=-1;
    String menu;
    int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDBTrain=new MyDBTrain(this);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);

        if(savedInstanceState==null){
            SharedPreferences prefs =getSharedPreferences("card_info",0);
            String card = prefs.getString("card", "0");
            String password=prefs.getString("password", "*");
            Log.d("LogTest","card="+card);
            if(card.equals("0")){
                train_info();
                Join();
            }
        }
        // hide the action bar
        //getActionBar().hide();
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
                    tts_text("1번 기차 예약 2번 기차 예약 확인");
                    i++;
                }
                else if(i==0){
                    promptSpeechInput();

                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            menu_se();
                        }
                    }, 5000);
                    i=-1;
                }
            }
        });
    }

    public void train_info(){
        SQLiteDatabase db;
        ContentValues values;
        db=myDBTrain.getWritableDatabase();
        values = new ContentValues();

        values.put("s_station","천안역");
        values.put("e_station","서울역");
        values.put("time","12월7일08시12분");
        values.put("price","6300");
        db.insert("train_info", null, values);

        values.put("s_station","천안역");
        values.put("e_station","서울역");
        values.put("time","12월7일10시25분");
        values.put("price","6300");
        db.insert("train_info", null, values);



        values.put("s_station","부산역");
        values.put("e_station","서울역");
        values.put("time","12월7일08시17분");
        values.put("price","59000");
        db.insert("train_info", null, values);

        values.put("s_station","부산역");
        values.put("e_station","서울역");
        values.put("time","12월7일09시22분");
        values.put("price","59000");
        db.insert("train_info", null, values);

        values.put("s_station","부산역");
        values.put("e_station","서울역");
        values.put("time","12월7일12시25분");
        values.put("price","59000");
        db.insert("train_info", null, values);

        values.put("s_station","부산역");
        values.put("e_station","서울역");
        values.put("time","12월7일15시45분");
        values.put("price","59000");
        db.insert("train_info", null, values);

        values.put("s_station","부산역");
        values.put("e_station","서울역");
        values.put("time","12월7일20시20분");
        values.put("price","59000");
        db.insert("train_info", null, values);

        values.put("s_station","서대전역");
        values.put("e_station","밀양역");
        values.put("time","12월7일08시45분");
        values.put("price","25400");
        db.insert("train_info", null, values);

        values.put("s_station","서대전역");
        values.put("e_station","밀양역");
        values.put("time","12월7일09시43분");
        values.put("price","25400");
        db.insert("train_info", null, values);

        values.put("s_station","서대전역");
        values.put("e_station","밀양역");
        values.put("time","12월7일12시33분");
        values.put("price","25400");
        db.insert("train_info", null, values);

        values.put("s_station","서대전역");
        values.put("e_station","밀양역");
        values.put("time","12월7일16시45분");
        values.put("price","25400");
        db.insert("train_info", null, values);

        values.put("s_station","서대전역");
        values.put("e_station","밀양역");
        values.put("time","12월7일22시45분");
        values.put("price","25400");
        db.insert("train_info", null, values);

        values.put("s_station","전주역");
        values.put("e_station","아산역");
        values.put("time","12월7일07시12분");
        values.put("price","21100");
        db.insert("train_info", null, values);

        values.put("s_station","전주역");
        values.put("e_station","아산역");
        values.put("time","12월7일12시22분");
        values.put("price","21100");
        db.insert("train_info", null, values);

        values.put("s_station","전주역");
        values.put("e_station","아산역");
        values.put("time","12월7일15시54분");
        values.put("price","21100");
        db.insert("train_info", null, values);

        values.put("s_station","전주역");
        values.put("e_station","아산역");
        values.put("time","12월7일18시07분");
        values.put("price","21100");
        db.insert("train_info", null, values);

        values.put("s_station","전주역");
        values.put("e_station","아산역");
        values.put("time","12월7일22시10분");
        values.put("price","21100");
        db.insert("train_info", null, values);

        myDBTrain.close();
    }

    public void Join(){
        Intent intent;
        intent=new Intent(this,train_pay.class);
        startActivity(intent);
    }
    public void menu_se(){
        Intent intent;
        menu=txtSpeechInput.getText().toString();

        if(menu.equals("이번")||menu.equals("2번")||menu.equals("기차예약확인")){
            intent=new Intent(this,train_reservation.class);
            startActivity(intent);
        }
        else if(menu.equals("일번")||menu.equals("1번")||menu.equals("기차예약")||menu.equals("예약")){
            intent=new Intent(this,train_re.class);
            startActivity(intent);
        }
        else{
            tts_text("다시 말해주세요");
            i=-1;
        }
    }


    /**
     * Showing google speech input dialog
     * */
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
