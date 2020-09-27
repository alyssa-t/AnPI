package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterCardActivity extends AppCompatActivity {

    private OpenHelper helper;
    String kbn = "";
    String toastMessage_added = "Adicionado com sucesso";
    String toastMessage_mod = "Atualizado com sucesso";
    String toastMessage_failed = "Preencha o nome do grupo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        helper = new OpenHelper(getApplicationContext());
        Intent intent = getIntent();

        String KBN = intent.getStringExtra("KBN");
        Button btn_confirmGroupName = findViewById(R.id.btn_confirmGroupName);

        if(KBN.length() != 0){
            kbn = KBN;
            btn_confirmGroupName.setText(R.string.atualizar);
            readDate(KBN);

        }
        else{
            kbn = "add";
            btn_confirmGroupName.setText(R.string.adicionar);
        }
    }

    public void Return(View view) {
        finish();
    }

    public void SaveGroupData(View view) {
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText txtGroupName = findViewById(R.id.txt_inputGroupName);

        String groupName = txtGroupName.getText().toString();

        ContentValues values = new ContentValues();
        values.put("groupName", groupName);

        if (kbn == "add"){
            if (groupName.length() != 0){
                db.insert("mycardtb", null, values);
                toastMake(toastMessage_added, 0 , +350);
                txtGroupName.setText("");
            }else{
                toastMake(toastMessage_failed, 0, +350);
            }
        }
        //atualizar
        else{
            if (groupName.length() != 0){
                UPData(kbn);
                toastMake(toastMessage_mod, 0 , +350);
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

    public void readDate(String read){
        SQLiteDatabase db = helper.getReadableDatabase();

        EditText text1 = findViewById(R.id.txt_inputGroupName);
        Cursor cursor = db.query(
                "mycardtb",
                new String[]{"groupName"},
                "_ID = ?",
                new String[]{read},
                null,null,null
        );
        cursor.moveToFirst();

        for(int i = 0; i< cursor.getCount(); i++){
            text1.setText(cursor.getString(0));

        }

        cursor.close();
    }
    public void UPData(String read){
        SQLiteDatabase db = helper.getReadableDatabase();

        EditText txt1 = findViewById(R.id.txt_inputGroupName);

        String groupName = txt1.getText().toString();


        ContentValues upvalue = new ContentValues();
        upvalue.put("groupName",groupName);


        db.update("mycardtb",upvalue,"_id=?",new String[]{read});
    }
}
