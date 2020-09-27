package com.example.myapplication.ui.notifications;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.MainActivity;
import com.example.myapplication.OpenHelper;
import com.example.myapplication.R;
import com.example.myapplication.RegisterCardActivity;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private boolean allowRefresh = false;
    android.widget.ListView myListView ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        OpenHelper myOpenHelper = new OpenHelper(getActivity());

        /*final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        myListView = root.findViewById(R.id.listview_gerenciarGrupo);
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};

        int[] to = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);

        myListView.setItemsCanFocus(false);

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

        return root;
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

        OpenHelper myOpenHelper = new OpenHelper(getActivity());
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from mycardtb", null);
        String[] from = {"groupName"};

        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, c, from, to, 0);
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


}