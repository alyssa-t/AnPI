package com.ple2020pi.memoranki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
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
    private String nomeTabela = "mycardtb";

    //comit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

        helper = new OpenHelper(getApplicationContext());
        Intent intent = getIntent();

        String KBN = intent.getStringExtra("KBN");
        Button btn_confirmGroupName = findViewById(R.id.btn_confirmGroupName);
        Button btn_delete = findViewById(R.id.btn_delete);

        if(KBN.length() != 0){
            kbn = KBN;
            btn_confirmGroupName.setText(R.string.atualizar);
            btn_delete.setVisibility(View.VISIBLE);
            readDate(KBN);

        }
        else{
            kbn = "add";
            btn_confirmGroupName.setText(R.string.adicionar);
        }
    }

    public void SaveGroupData(View view) {
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText txtGroupName = findViewById(R.id.txt_inputGroupName);

        String groupName = txtGroupName.getText().toString();

        ContentValues values = new ContentValues();
        values.put("groupName", groupName);

        if (kbn == "add"){
            if (groupName.length() != 0){
                db.insert(nomeTabela, null, values);
                toastMake(toastMessage_added, 0 , +350);
                txtGroupName.setText("");
            }else{
                toastMake(toastMessage_failed, 0, +350);
            }
        }
        //atualizarr
        else{
            if (groupName.length() != 0){
                UPData(kbn);
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

    public void readDate(String read){
        SQLiteDatabase db = helper.getReadableDatabase();

        EditText text1 = findViewById(R.id.txt_inputGroupName);
        Cursor cursor = db.query(
                nomeTabela,
                new String[]{"groupName"},
                "groupName = ?",
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


        db.update(nomeTabela,upvalue,"groupName=?",new String[]{read});
    }

    public void DeleteCard(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Apagar grupo");
        builder.setMessage("Gostaria de apagar esse grupo?");
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.delete(nomeTabela, "groupName=?", new String[]{kbn});
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

    public void Return(View view) {
        finish();
    }
}