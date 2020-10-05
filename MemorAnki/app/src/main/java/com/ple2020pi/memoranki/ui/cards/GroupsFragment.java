package com.ple2020pi.memoranki.ui.cards;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.EditText;
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
    private boolean editMode = false;
    private android.widget.ListView myListView = null;
    private SQLiteDatabase db;
    private OpenHelper myOpenHelper;
    private Menu myMenu;

    //FUNCAO RELACIONADO A COMPOSICAO DA TELA
    //gerencia listview tbm
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editMode = false;
        setHasOptionsMenu(true);
        groupsViewModel = ViewModelProviders.of(this).get(GroupsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_cards, container, false);
        myOpenHelper = new OpenHelper(getActivity());
        myListView = root.findViewById(R.id.listview_gerenciarGrupo);

        reload();

        //vigia se clicaram em um item especifico da lista
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                if (!editMode){
                    String s1 = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                    Intent intent = new Intent(getActivity().getApplication(), RegisterCardActivity.class);
                    intent.putExtra("KBN", s1);
                    //startActivity(intent);
                }

            }
        });

        //Se clicarem longo, edita o nome dos grupos.
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1, final int pos, final long id) {
                //if (editMode){
                //pega quem foi clicado
                final String groupName = ((TextView) arg1.findViewById(android.R.id.text1)).getText().toString();
                //cria campo de texto
                final EditText input = new EditText(getActivity());
                input.setText(groupName);
                //cria dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true)
                        .setTitle("Renomear Grupo")
                        .setView(input)
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newGroupName = input.getText().toString();
                                    UPData(id, newGroupName);
                                    toastMake("Atualizado com sucesso", 0, 350);
                                    if(editMode)
                                        editReload();
                                    else
                                        reload();
                                }
                            })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                AlertDialog dialog = builder.create();
                dialog.show();
                //}

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

    //FUNCOES RELACIONADAS AO MENU
    //cria menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group_option_menu, menu);
        myMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }
    //trata acoes quando itens do menu forem selecionadas
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        MenuItem menuEditGroup = myMenu.findItem(R.id.menu_editGroup);
        MenuItem menuAddGroup = myMenu.findItem(R.id.menu_addGroup);
        MenuItem menuDeleteGroup = myMenu.findItem(R.id.menu_deleteGroup);

        if(id == R.id.menu_addGroup){
            Intent intent = new Intent(getActivity().getApplication(), RegisterCardActivity.class);
            intent.putExtra("KBN", "");
            startActivity(intent);
        }
        else if (id == R.id.menu_editGroup){
            if (editMode == false) {
                editMode = true;
                menuAddGroup.setVisible(false);
                menuDeleteGroup.setVisible(true);
                menuEditGroup.setIcon(R.drawable.ic_tick);
                editReload();
                myListView.setChoiceMode(android.widget.ListView.CHOICE_MODE_MULTIPLE);
                myListView.setItemsCanFocus(false);
            }else {
                editMode = false;
                menuAddGroup.setVisible(true);
                menuDeleteGroup.setVisible(false);
                menuEditGroup.setIcon(R.drawable.ic_ink);
                reload();
            }
        }
        else if (id == R.id.menu_deleteGroup){
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
            if (selected[0]){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true)
                        .setTitle("Apagar grupo")
                        .setMessage("Tem certeza que quer apagar os grupos selecionados?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checked = myListView.getCheckedItemPositions();
                                int finalKey ;
                                boolean finalValue;
                                int size = checked.size();
                                for (int i = 0; i < size; i++) {
                                    finalKey = checked.keyAt(i);
                                    finalValue = checked.get(finalKey);
                                    if (finalValue){
                                        long selected = myListView.getItemIdAtPosition(finalKey);
                                        db.delete("mycardtb", "_id=?", new String[]{String.valueOf(selected)});
                                    }
                                }
                                toastMake("Apagado com sucesso", 0, 350);
                                editReload();
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
            else{
                toastMake("Selecione pelo menos um item a ser removido", 0, 350);
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //FUNCAO RELACIONADA POR DETECTAR SE FRAGMENTO FOI RETOMADO
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

    //FUNCOES RELACIONADAS A EXIBICAO DA LISTA
    public void reload(){
        db = myOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);
        myListView.setItemsCanFocus(false);
        editMode = false;

    }
    public void editReload(){
        db = myOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.selection_delete, c, from, to, 0);
        myListView.setAdapter(adapter);
    }

    //FUNCOES PARA ORGANIZAR O CODIGO
    //Funcao para criar toast
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }
    //Funcao para atualizar o nome do grupo no bd
    public void UPData(long id, String newGroupName){
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        ContentValues upvalue = new ContentValues();
        upvalue.put("groupName",newGroupName);
        db.update("mycardtb",upvalue,"_id=?",new String[]{String.valueOf(id)});
    }

}