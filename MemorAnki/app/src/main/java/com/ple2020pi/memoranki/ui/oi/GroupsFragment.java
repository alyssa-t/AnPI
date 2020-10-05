package com.ple2020pi.memoranki.ui.oi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ple2020pi.memoranki.OpenHelper;
import com.ple2020pi.memoranki.R;
import com.ple2020pi.memoranki.RegisterCardActivity;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private boolean allowRefresh = false;
    private ListView myListView = null;
    private SQLiteDatabase db;
    private OpenHelper myOpenHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        groupsViewModel =
                ViewModelProviders.of(this).get(GroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        myOpenHelper = new OpenHelper(getActivity());
        myListView = root.findViewById(R.id.listview_gerenciarGrupo);
        db = myOpenHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};
        int[] to = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);
        myListView.setItemsCanFocus(false);

        //vigia se clicaram em um item especifico da listaa
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String s1 = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                //String s2  = ((TextView)view.findViewById(android.R.id.text2)).getText().toString();

                Intent intent = new Intent(getActivity().getApplication(), RegisterCardActivity.class);
                intent.putExtra("KBN", s1);
                startActivity(intent);
            }
        });

        //função para o botao de (+) para add grupo

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                toastMake("LongClickkkk", 0, 350);

                return true;
            }
        });

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Initialize();
        if (allowRefresh) {
            allowRefresh = false;
            reload();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!allowRefresh)
            allowRefresh = true;
    }

    public void reload(){
        db = myOpenHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_first, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menu_first_a){
            Intent intent = new Intent(getActivity().getApplication(), RegisterCardActivity.class);
            intent.putExtra("KBN", "");
            startActivity(intent);
        }
        else{
            toastMake("Eu ainda sou inutil", 0, 350);
        }
        return super.onOptionsItemSelected(item);
    }

}