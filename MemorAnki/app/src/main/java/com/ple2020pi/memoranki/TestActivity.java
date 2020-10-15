package com.ple2020pi.memoranki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class TestActivity extends AppCompatActivity {
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private boolean lightMode;
    private String selectedIds[];
    private String nomeTabelaCard = "mycardtb";
    private OpenHelper myOpenHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getSharedPreferences( "Config", MODE_PRIVATE);
        editor = data.edit();
        lightMode = data.getBoolean("lightMode", true);
        if (lightMode)
            setTheme(R.style.LightTheme);
        else
            setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        selectedIds = intent.getStringArrayExtra("selectedGroupIds");

        final TextView txt_word = findViewById(R.id.txt_word);
        final TextView txt_meaning = findViewById(R.id.txt_meaning);
        final TextView txt_reading = findViewById(R.id.txt_reading);
        final Button btn_showAnswer = findViewById(R.id.showAnswer);
        txt_reading.setVisibility(View.GONE);
        txt_meaning.setVisibility(View.GONE);

        /*------le os cards do sql coloca num vetor----------*/
        myOpenHelper = new OpenHelper(getApplicationContext());
        db = myOpenHelper.getWritableDatabase();
        final List<String> words = new ArrayList<String>();
        final List<String> meaning = new ArrayList<String>();
        final List<String> reading = new ArrayList<String>();
        List<String>  cardId = new ArrayList<String>();
        List<Integer> indices = new ArrayList<Integer>();
        int numCards = 0;
        for(int i=0; i<selectedIds.length; i++){
            Cursor c = db.rawQuery("select * from " + nomeTabelaCard + " where cardGroup="+selectedIds[i]+ ";", null);
            c.moveToFirst();
            for(int j = 0; j< c.getCount(); j++) {
                words.add(c.getString(c.getColumnIndex("cardName")));
                meaning.add(c.getString(c.getColumnIndex("cardMeaning")));
                reading.add(c.getString(c.getColumnIndex("cardReading")));
                cardId.add(c.getString(c.getColumnIndex("_id")));
                indices.add(numCards);
                numCards++;
                c.moveToNext();
            }
        }
        /*------------------*/

        final List<Integer> suffleIndices = shuffleCards(indices);
        final int totalNumCards = numCards;
        int counter;
        /*-----Coloca uma das palavras selecionadas-----*/
        txt_word.setText(words.get(indices.get(0)));

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view_return);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            int counter = 1;
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (totalNumCards == counter){
                    counter = 1;
                    getIntent().putExtra("counter", counter);
                    toastMake("Fim do teste", 0, 350);
                    finish();
                }
                switch (id){
                    case R.id.navigation_difficult:
                       // toastMake("MUITO DIFICILLL", 0, 350);
                        break;
                    case R.id.navigation_medium:
                       // toastMake("Meh", 0, 350);
                        break;
                    case R.id.navigation_easy:
                       // toastMake("IZI", 0, 350);
                        break;
                }
                txt_reading.setVisibility(View.GONE);
                txt_meaning.setVisibility(View.GONE);
                btn_showAnswer.setVisibility(Button.VISIBLE);
                txt_word.setText(words.get(suffleIndices.get(counter)));
                getIntent().putExtra("counter", counter);
                counter++;
                return true;
            }
        });


        btn_showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_reading.setText(reading.get(suffleIndices.get(getIntent().getExtras().getInt("counter"))));
                txt_meaning.setText(meaning.get(suffleIndices.get(getIntent().getExtras().getInt("counter"))));
                txt_reading.setVisibility(View.VISIBLE);
                txt_meaning.setVisibility(View.VISIBLE);
                btn_showAnswer.setVisibility(Button.GONE);
            }
        });

    }


    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    private List<Integer> shuffleCards(List<Integer> indices){
        Collections.shuffle(indices, new Random());
        return indices;
    }
}
