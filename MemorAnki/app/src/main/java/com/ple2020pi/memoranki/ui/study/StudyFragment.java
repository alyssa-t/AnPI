package com.ple2020pi.memoranki.ui.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ple2020pi.memoranki.OpenHelper;
import com.ple2020pi.memoranki.R;
import com.ple2020pi.memoranki.RegisterGroupActivity;
import com.ple2020pi.memoranki.ui.cards.GroupsViewModel;

import java.util.Vector;

public class StudyFragment extends Fragment {

    private StudyViewModel studyViewModel;
    private android.widget.ListView myListView = null;
    private SQLiteDatabase db;
    private OpenHelper myOpenHelper;
    private Menu myMenu;
    private Vector<Long> selectedIds = new Vector<>();
    private String nomeTabela = "mygrouptb";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studyViewModel =
                ViewModelProviders.of(this).get(StudyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_study, container, false);
        setHasOptionsMenu(true);
        myOpenHelper = new OpenHelper(getActivity());
        myListView = root.findViewById(R.id.listview_gerenciarEstudo);
        myListView.setChoiceMode(android.widget.ListView.CHOICE_MODE_MULTIPLE);
        myListView.setItemsCanFocus(false);

        reload();
        return root;
    }

    //FUNCOES RELACIONADAS AO MENU
    //cria menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.study_option_menu, menu);
        myMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle("Estudar");
    }
    //trata acoes quando itens do menu forem selecionadas
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        MenuItem menuEditGroup = myMenu.findItem(R.id.menu_start);

        if(id == R.id.menu_start) {
            SparseBooleanArray checked = myListView.getCheckedItemPositions();
            boolean selected = false;
            int size = checked.size(); // number of name-value pairs in the array
            //se tiver algo selecionado
            int key ;
            boolean value ;
            for (int i = 0; i < size; i++) {
                key = checked.keyAt(i);
                value = checked.get(key);
                if (value) {
                    selected = true;
                    selectedIds.add(myListView.getItemIdAtPosition(key));
                }
            }
            if (selected){
                toastMake("Itens selecionados" , 0, 350);
            }
            else{
                toastMake("Selecione pelo menos um grupo a ser estudado", 0, 350);
            }
            selectedIds.clear();

        }
        return super.onOptionsItemSelected(item);
    }

    public void reload(){
        db = myOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + nomeTabela, null);
        String[] from = {"groupName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.selection_study, c, from, to, 0);
        myListView.setAdapter(adapter);

    }

    //FUNCOES PARA ORGANIZAR O CODIGO
    //Funcao para criar toast
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }
}
