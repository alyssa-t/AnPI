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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ListCardActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private OpenHelper myOpenHelper;
    ListView myListView;

    private boolean editMode = false;
    private boolean deleteMode = false;
    private String nomeTabelaGrupo = "mygrouptb";
    private String nomeTabelaCard = "mycardtb";
    private long GroupId;
    private Menu myMenu;

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
        setContentView(R.layout.activity_list_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myOpenHelper = new OpenHelper(getApplicationContext());
        myListView = findViewById(R.id.listview_gerenciarCard);
        Intent intent = getIntent();
        GroupId = intent.getLongExtra("KBN", -1);
        String groupName = readGroupName(GroupId);
        setTitle(groupName);
        reload();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!deleteMode) {
                    Intent intent = new Intent(getApplication(), RegisterCardActivity.class);
                    intent.putExtra("CARD_ID", id);
                    intent.putExtra("GROUP_ID", GroupId);
                    startActivity(intent);
                }
            }
        });

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1, final int pos, final long id) {
                if (!deleteMode){
                    MenuItem menuAddGroup = myMenu.findItem(R.id.menu_addcard);
                    MenuItem menuDeleteGroup = myMenu.findItem(R.id.menu_deletecard);
                    deleteMode = true;
                    menuAddGroup.setVisible(true);
                    menuAddGroup.setIcon(R.drawable.ic_tick);
                    menuDeleteGroup.setVisible(true);
                    deleteReload();
                    myListView.setChoiceMode(android.widget.ListView.CHOICE_MODE_MULTIPLE);
                    myListView.setItemChecked(pos, true);
                    myListView.setItemsCanFocus(false);
                }
                return true;
            }
        });

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view_return);
        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(final MenuItem menuItem) {
                finish();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        myMenu = menu;
        inflater.inflate(R.menu.listcard_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        AlertDialog.Builder builder;
        if(id == R.id.menu_addcard){
            if(!deleteMode){
                Intent intent = new Intent(getApplication(), RegisterCardActivity.class);
                intent.putExtra("GROUP_ID", GroupId);
                intent.putExtra("CARD_ID", "");
                startActivity(intent);
            }
            else{
                MenuItem menuAddGroup = myMenu.findItem(R.id.menu_addcard);
                menuAddGroup.setIcon(R.drawable.ic_plus);
                deleteMode = false;
                reload();
            }

        }
        else if (id == R.id.menu_deletecard) {
            SparseBooleanArray checked = myListView.getCheckedItemPositions();
            final boolean[] selected = {false};
            int size = checked.size(); // number of name-value pairs in the array
            //se tiver algo selecionado
            final int[] key = new int[1];
            final boolean[] value = new boolean[1];
            for (int i = 0; i < size; i++) {
                key[0] = checked.keyAt(i);
                value[0] = checked.get(key[0]);
                if (value[0]) {
                    selected[0] = true;
                    break;
                }
            }
            if (selected[0]) {
                if (!lightMode)
                    builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                else
                    builder = new AlertDialog.Builder(this);
                builder.setCancelable(true)
                        .setTitle("Apagar grupo")
                        .setMessage("Tem certeza que quer apagar os cartões selecionados?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checked = myListView.getCheckedItemPositions();
                                int finalKey;
                                boolean finalValue;
                                int size = checked.size();
                                for (int i = 0; i < size; i++) {
                                    finalKey = checked.keyAt(i);
                                    finalValue = checked.get(finalKey);
                                    if (finalValue) {
                                        long selected = myListView.getItemIdAtPosition(finalKey);
                                        db.delete(nomeTabelaCard, "_id=?", new String[]{String.valueOf(selected)});
                                    }
                                }
                                toastMake("Apagado com sucesso", 0, 350);
                                deleteReload();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    public String readGroupName(long id){
        db = myOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + nomeTabelaGrupo + " where _id="+id+ ";", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        String groupName=  cursor.getString(cursor.getColumnIndex("groupName"));
        //cursor.close();
        return groupName;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        reload();
    }

    public void reload(){
        db = myOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + nomeTabelaCard + " where cardGroup="+GroupId+ ";", null);
        String[] from = {"cardName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.selection_list, c, from, to, 0);
        myListView.setAdapter(adapter);
        myListView.setItemsCanFocus(false);
        editMode = false;

    }

    public void deleteReload(){
        db = myOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + nomeTabelaCard + " where cardGroup="+GroupId+ ";", null);
        String[] from = {"cardName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.selection_delete, c, from, to, 0);
        myListView.setAdapter(adapter);

    }

    public void Return() {
        finish();
    }


}
