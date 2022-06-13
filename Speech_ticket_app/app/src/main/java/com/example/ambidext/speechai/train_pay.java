package com.example.ambidext.speechai;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class train_pay extends AppCompatActivity {
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtSpeechInput;
    private TextToSpeech tts;
    int[] num;
    int[] num1;
    String card;
    String password;
    int i=0;
    int k=0;
    int l=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_pay);
        btnSpeak = (ImageButton) findViewById(R.id.btnPay);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechPay);

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
                if(i==0){
                    tts_text("카드 등록을 위하여 카드번호를 말해주세요");
                }
                else if(i==1) {
                    promptSpeechInput();
                }
                else if(i==2){
                    card=txtSpeechInput.getText().toString();
                    card=card.replaceAll(" ","");
                    num=new int[card.length()];
                    for(int k=0; k<card.length();k++){
                        num[k]=card.charAt(k)-48;
                    }
                    tts_text("카드번호가 "+num[0]+" "+num[1]+" "+num[2]+" "+num[3]+" "+num[4]+" "+num[5]+" "+" 맞습니까 맞다면 클릭을 틀리다면 길게 눌러주세요" );
                }
                else if(i==3){
                    tts_text("비밀번호를 말해주세요");
                }
                else if(i==4) {
                    promptSpeechInput();
                }
                else if(i==5){
                    int[] num1 =new int[4];
                    password=txtSpeechInput.getText().toString();
                    password=password.replaceAll(" ","");
                    for(int k=0;k<4;k++)
                        num1[k]=password.charAt(k)-48;

                    tts_text("비밀번호가 "+num1[0]+" "+num1[1]+" "+num1[2]+" "+num1[3]+" "+" 맞습니까 맞다면 클릭을 틀리다면 길게 눌러주세요" );
                }
                else if(i==6)
                    tts_text("카드등록을 완료했습니다");
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
        SharedPreferences prefs = getSharedPreferences("card_info", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("card", card);
        editor.putString("password", password);
        editor.apply();

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
