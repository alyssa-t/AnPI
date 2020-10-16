package com.ple2020pi.memoranki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {

    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private boolean lightMode;
    //comit

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
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sobre");

        TextView txt_about = findViewById(R.id.txt_about);
        txt_about.setText("Esse é o trabalho de Projeto Integrado no Período Letivo Excepcional de 2020.\n" +
                            "Criado por Alyssa Takazume e Matheus Lima.\n");

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view_return);
        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(final MenuItem menuItem) {
                finish();
            }

        });

    }



}
