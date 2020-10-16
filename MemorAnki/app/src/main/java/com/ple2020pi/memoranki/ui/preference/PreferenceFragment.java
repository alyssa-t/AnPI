package com.ple2020pi.memoranki.ui.preference;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ple2020pi.memoranki.ListCardActivity;
import com.ple2020pi.memoranki.MainActivity;
import com.ple2020pi.memoranki.OpenHelper;
import com.ple2020pi.memoranki.R;

import static android.content.Context.MODE_PRIVATE;


public class PreferenceFragment extends Fragment {
    private PreferenceViewModel preferenceViewModel;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private boolean lightMode;
    private int cardQty;

    //FUNCAO RELACIONADO A COMPOSICAO DA TELA
    //gerencia listview tbm
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        preferenceViewModel = ViewModelProviders.of(this).get(PreferenceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_preference, container, false);

        final Button btnChangeTheme = root.findViewById(R.id.btn_changeTheme);
        final Button btnChangeCardQty = root.findViewById(R.id.btn_changeCardQty);

        data = getActivity().getSharedPreferences( "Config", MODE_PRIVATE);
        editor = data.edit();
        lightMode = data.getBoolean("lightMode", true);
        //editor.putInt("cardQty", 10);
        //editor.apply();
        cardQty = data.getInt("cardQty", 10);

        btnChangeCardQty.setText("Mudar quantidade máxima de cartões para teste");

        if (lightMode)
            btnChangeTheme.setText("Mudar para tema escuro");
        else
            btnChangeTheme.setText("Mudar para tema claro");

        btnChangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lightMode){
                    editor.putBoolean("lightMode", false);
                    editor.apply();
                    btnChangeTheme.setText("Mudar para tema escuro");

                }
                else{
                    editor.putBoolean("lightMode", true);
                    editor.apply();
                    btnChangeTheme.setText("Mudar para tema claro");
                }
                Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        btnChangeCardQty.setOnClickListener(new View.OnClickListener() {
            int newCardQty;
            AlertDialog.Builder builder;
            @Override
            public void onClick(View view) {
                if (!lightMode)
                    builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                else
                    builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Digite a quantidade de cartões para teste");
                final EditText input = new EditText(getContext());
                builder.setView(input);
                input.setText(String.valueOf(cardQty));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            newCardQty = Integer.parseInt(input.getText().toString());
                            editor.putInt("cardQty", newCardQty);
                            editor.apply();
                            cardQty = data.getInt("cardQty", 10);
                        }
                        catch (Exception e) {
                            toastMake("Coloque um número inteiro válido", 0, 350);
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
        return root;
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.options);
    }

    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }


}