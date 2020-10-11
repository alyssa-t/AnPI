package com.ple2020pi.memoranki;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.ple2020pi.memoranki.ui.cards.GroupsViewModel;

public class ListCardActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private OpenHelper myOpenHelper;
    ListView myListView;

    private boolean editMode = false;
    private String nomeTabelaGrupo = "mygrouptb";
    private String nomeTabelaCard = "mycardtb";
    private long GroupId;
    private Menu myMenu;
    //comit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Intent intent = new Intent(getApplication(), RegisterCardActivity.class);
                intent.putExtra("CARD_ID", id);
                intent.putExtra("GROUP_ID", GroupId);
                startActivity(intent);
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
        //MenuItem menuEditGroup = myMenu.findItem(R.id.menu_deletecard);
        //MenuItem menuAddGroup = myMenu.findItem(R.id.menu_addcard);
        if(id == R.id.menu_addcard){
            Intent intent = new Intent(getApplication(), RegisterCardActivity.class);
            intent.putExtra("GROUP_ID", GroupId);
            intent.putExtra("CARD_ID", "");
            startActivity(intent);
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
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);
        myListView.setItemsCanFocus(false);
        editMode = false;

    }

    public void Return() {
        finish();
    }


}