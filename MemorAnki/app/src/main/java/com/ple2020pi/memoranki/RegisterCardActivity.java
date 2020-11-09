package com.ple2020pi.memoranki;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterCardActivity extends AppCompatActivity {

    private OpenHelper helper;
    private SQLiteDatabase db;
    String kbn = "";
    String toastMessage_added = "Adicionado com sucesso";
    String toastMessage_mod = "Atualizado com sucesso";
    String toastMessage_failed = "Preencha a palavra e o significado";
    private String nomeTabela = "mycardtb";
    private Long groupID;
    private Long cardID;

    private SharedPreferences data;
    private boolean lightMode;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        lightMode = myPreferences.getBoolean("myPreferences_darkmode",true);

        // data = getSharedPreferences( "Config", MODE_PRIVATE);
        // lightMode = data.getBoolean("lightMode", true);
        if (lightMode)
            setTheme(R.style.LightTheme);
        else
            setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_register_card);

        helper = new OpenHelper(getApplicationContext());
        Intent intent = getIntent();

        cardID = intent.getLongExtra("CARD_ID", -1);
        groupID = intent.getLongExtra("GROUP_ID", -1);
        Button btn_confirmGroupName = findViewById(R.id.btn_confirmCardName);
        Button btn_delete = findViewById(R.id.btn_delete);
        ImageButton btn_listen = findViewById(R.id.btn_listen);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) { // <-- I never get into that if statement
                    int result = tts.setLanguage(Locale.getDefault());
                    // Language is not supported
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                }
                else {
                    Log.e("TTS", "" + status); // Returns -1
                    Log.e("TTS", "" + TextToSpeech.SUCCESS); // Returns 0
                }
            }
        });

        if(cardID == -1){
            kbn = "add";
            btn_confirmGroupName.setText(R.string.adicionar);
        }
        else{
            kbn = "atualizar";
            btn_confirmGroupName.setText(R.string.atualizar);
            btn_delete.setVisibility(View.VISIBLE);
            readDate(cardID);
        }
    }

    public void SaveGroupData(View view) {
        db = helper.getWritableDatabase();

        EditText txtCardName = findViewById(R.id.txt_inputCardName);
        EditText txtCardReading = findViewById(R.id.txt_inputCardReading);
        EditText txtCardMeaning = findViewById(R.id.txt_inputCardMeaning);

        String cardName = txtCardName.getText().toString();
        String cardReading = txtCardReading.getText().toString();
        String cardMeaning = txtCardMeaning.getText().toString();

        ContentValues values = new ContentValues();
        values.put("cardName", cardName);
        values.put("cardReading", cardReading);
        values.put("cardMeaning", cardMeaning);
        values.put("cardGroup", groupID);
        values.put("cardType", "");
        values.put("cardRepetition", 0);
        values.put("cardLR", 0);
        values.put("cardLD", getNowDate());

        if (kbn == "add"){
            if (cardName.length() != 0 && cardMeaning.length() != 0){
                db.insert(nomeTabela, null, values);
                toastMake(toastMessage_added, 0 , +350);
                txtCardName.setText("");
                txtCardReading.setText("");
                txtCardMeaning.setText("");
            }else{
                toastMake(toastMessage_failed, 0, +350);
            }
        }
        //atualizarr
        else{
            if (cardName.length() != 0 && cardMeaning.length() != 0){
                UPData(cardID);
                toastMake(toastMessage_mod, 0 , +350);
                finish();
            }else{
                toastMake(toastMessage_failed, 0, +350);
            }

        }
    }
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    public void readDate(Long id){
        db = helper.getReadableDatabase();

        EditText txtCardName = findViewById(R.id.txt_inputCardName);
        EditText txtCardReading = findViewById(R.id.txt_inputCardReading);
        EditText txtCardMeaning = findViewById(R.id.txt_inputCardMeaning);

        Cursor cursor = db.rawQuery("select * from " + nomeTabela + " WHERE _id=" + id + ";", null);
        cursor.moveToFirst();
        for(int i = 0; i< cursor.getCount(); i++){
            txtCardName.setText(cursor.getString(cursor.getColumnIndex("cardName")));
            txtCardReading.setText(cursor.getString(cursor.getColumnIndex("cardReading")));
            txtCardMeaning.setText(cursor.getString(cursor.getColumnIndex("cardMeaning")));
        }

        cursor.close();
    }
    public void UPData(long id){

        db = helper.getReadableDatabase();

        EditText txtCardName = findViewById(R.id.txt_inputCardName);
        EditText txtCardReading = findViewById(R.id.txt_inputCardReading);
        EditText txtCardMeaning = findViewById(R.id.txt_inputCardMeaning);

        String cardName = txtCardName.getText().toString();
        String cardReading = txtCardReading.getText().toString();
        String cardMeaning = txtCardMeaning.getText().toString();

        ContentValues upvalue = new ContentValues();
        upvalue.put("cardName",cardName);
        upvalue.put("cardReading", cardReading);
        upvalue.put("cardMeaning", cardMeaning);

        db.update(nomeTabela,upvalue,"_id=?",new String[]{String.valueOf(id)});
    }

    public void DeleteCard(View view) {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        lightMode = myPreferences.getBoolean("myPreferences_darkmode",true);
        AlertDialog.Builder builder;
        if (!lightMode)
            builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);
        else
            builder = new android.app.AlertDialog.Builder(this);
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Apagar cartão");
        builder.setMessage("Gostaria de apagar esse cartão?");
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.delete(nomeTabela, "_id=?", new String[]{String.valueOf(cardID)});
                        finish();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public void Return(View view) {
        finish();
    }
    public void ListenWord(View view) {
        EditText txtCardName = findViewById(R.id.txt_inputCardName);
        String cardName = txtCardName.getText().toString();
        tts.setLanguage(Locale.US);
        tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null);
    }
}