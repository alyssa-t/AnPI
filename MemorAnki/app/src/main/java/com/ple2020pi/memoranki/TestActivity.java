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

import java.util.Vector;

public class TestActivity extends AppCompatActivity {
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private boolean lightMode;
    private String selectedIds[];
    private SQLiteDatabase db;
    private String nomeTabelaCard = "mycardtb";
    private OpenHelper myOpenHelper;

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

        final TextView txt_meaning = findViewById(R.id.txt_meaning);
        final TextView txt_reading = findViewById(R.id.txt_reading);
        txt_reading.setVisibility(View.GONE);
        txt_meaning.setVisibility(View.GONE);


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view_return);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.navigation_difficult:
                        toastMake("MUITO DIFICILLL", 0, 350);
                        break;
                    case R.id.navigation_medium:
                        toastMake("Meh", 0, 350);
                        break;
                    case R.id.navigation_easy:
                        toastMake("IZI", 0, 350);
                        break;
                }
                txt_reading.setVisibility(View.GONE);
                txt_meaning.setVisibility(View.GONE);
                return true;
            }

        });

        Button btn_showAnswer = findViewById(R.id.showAnswer);
        btn_showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_reading.setVisibility(View.VISIBLE);
                txt_meaning.setVisibility(View.VISIBLE);
            }
        });

    }

    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    private void loadCards(){
        db = myOpenHelper.getWritableDatabase();
        for(int i=0; i<selectedIds.length; i++){
            Cursor c = db.rawQuery("select * from " + nomeTabelaCard + " where cardGroup="+selectedIds[i]+ ";", null);

        }

    }
}
