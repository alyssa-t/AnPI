package com.ple2020pi.memoranki;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class TestActivity extends AppCompatActivity {
    private SharedPreferences data;
    private boolean lightMode;
    private int maxCardQty;
    private String selectedIds[];
    private String nomeTabelaCard = "mycardtb";
    private OpenHelper myOpenHelper;
    private SQLiteDatabase db;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //declarar variaveis utilizados aq
        myOpenHelper = new OpenHelper(getApplicationContext());
        db = myOpenHelper.getWritableDatabase();
        final List<String> words = new ArrayList<String>();
        final List<String> meaning = new ArrayList<String>();
        final List<String> reading = new ArrayList<String>();
        final List<String>  cardId = new ArrayList<String>();
        List<Integer> indices = new ArrayList<Integer>();
        int numCards = 0;
        int counter; //parece que nao tem uso, mas tem !!!

        /*-------retomar configuracoes iniciais da tela do config-----*/
        data = getSharedPreferences( "Config", MODE_PRIVATE);
        lightMode = data.getBoolean("lightMode", true);
        if (lightMode)
            setTheme(R.style.LightTheme);
        else
            setTheme(R.style.DarkTheme);
        maxCardQty = data.getInt("cardQty", 10);
        /*----fim da configuracao----*/

        //monta layout baseado no activity_test.xml
        setContentView(R.layout.activity_test);

        //recebe ids dos grupos selecionados
        Intent intent = getIntent();
        selectedIds = intent.getStringArrayExtra("selectedGroupIds");

        /*declarar variavel que depende do layout*/
        final TextView txt_word = findViewById(R.id.txt_word);
        final TextView txt_meaning = findViewById(R.id.txt_meaning);
        final TextView txt_reading = findViewById(R.id.txt_reading);
        final Button btn_showAnswer = findViewById(R.id.showAnswer);
        progressBar = (ProgressBar)findViewById(R.id.progressBar); // initiate the progress bar

        //confirma que a traducao e a leitura estao invisiveis para o usuario
        txt_reading.setVisibility(View.GONE);
        txt_meaning.setVisibility(View.GONE);

        /*------le os cards do sql coloca num vetor----------*/
        //pega todos os cartoes contidos no grupo selecionado pelo usuario
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
        //se os grupos selecionados tiverem vazios, avisa ao usuario
        if(numCards == 0){
            toastMake("Não há cartoes nos grupos selecionados", 0, 350);
            this.finish();
            return;
        }
        /*------------------*/

        //prepara variaveis para serem usados dentro das funcoes de outras classes;
        final List<Integer> suffleIndices = shuffleCards(indices);
        final int totalNumCards = numCards;
        if (numCards < maxCardQty)
            maxCardQty = numCards;
        progressBar.setMax(maxCardQty*100);

        /*-----Define a primeira palavra que vai aparecer-----*/
        txt_word.setText(words.get(indices.get(0)));

        //Vigia o comportamento dos botoes de classificacao das palavras
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view_return);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            int counter = 1;
            int progress = 0;
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                float lRate;
                int id = menuItem.getItemId();

                if (totalNumCards <= counter || maxCardQty <= counter){
                    counter = 1;
                    getIntent().putExtra("counter", counter);
                    toastMake("Fim do teste", 0, 350);
                    finish();
                }
                //Le o learning rate da palavra a partir do BD
                Cursor c = db.rawQuery("select * from " + nomeTabelaCard + " where _id="+cardId.get(counter-1)+ ";", null);
                c.moveToFirst();
                lRate = c.getFloat(c.getColumnIndex("cardLR"));
                //Prepara uma "caixinha" para atualizar dados de BD
                ContentValues upvalue = new ContentValues();
                switch (id){
                    case R.id.navigation_difficult:
                        lRate = lRate*1;
                        break;
                    case R.id.navigation_medium:
                        lRate = lRate*1;
                        break;
                    case R.id.navigation_easy:
                        lRate = lRate*1;
                        break;
                }
                //Atualiza BD de fato
                upvalue.put("cardLR",lRate);
                upvalue.put("cardLD", getNowDate());
                db.update(nomeTabelaCard,upvalue,"_id=?",new String[]{cardId.get(counter-1)});

                //deixa inivisivel a traducao e leitura, se tiverem visiveis.
                txt_reading.setVisibility(View.GONE);
                txt_meaning.setVisibility(View.GONE);
                //deixa visivel o botao de "mostrar traducao" se tiver invisivel.
                btn_showAnswer.setVisibility(Button.VISIBLE);
                //mostra a proxima palavra da lista e atualiza contador.
                txt_word.setText(words.get(suffleIndices.get(counter)));
                getIntent().putExtra("counter", counter);
                progress+=Math.round(100);
                progressBar.setProgress(progress);
                counter++;
                return true;
            }
        });

        //vigia o botao de "mostrar traducao"
        btn_showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se clicado, mostra a traducao, leitura
                //deixa invisivel o proprio botao
                txt_reading.setText(reading.get(suffleIndices.get(getIntent().getExtras().getInt("counter"))));
                txt_meaning.setText(meaning.get(suffleIndices.get(getIntent().getExtras().getInt("counter"))));
                txt_reading.setVisibility(View.VISIBLE);
                txt_meaning.setVisibility(View.VISIBLE);
                btn_showAnswer.setVisibility(Button.GONE);
            }
        });

    }

    //cria toast -> toastMake("mensagem", 0, 350)
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    //funcao que faz a logica da ordem dos cartoes
    //precisa retornar uma lista de indices ordenados com a logica
    private List<Integer> shuffleCards(List<Integer> indices){
        Collections.shuffle(indices, new Random());
        return indices;
    }

    //funcao que retorna a data e horario de agora.
    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

}
