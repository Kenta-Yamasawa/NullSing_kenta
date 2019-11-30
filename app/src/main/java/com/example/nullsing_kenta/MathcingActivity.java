package com.example.nullsing_kenta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;

public class MathcingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        TextView menu_home = (TextView)findViewById(R.id.menu_home);
        menu_home.setOnClickListener(new MathcingActivity.MenuHomeOnClickListener());

        TextView menu_mylist = (TextView)findViewById(R.id.menu_myList);
        menu_mylist.setOnClickListener(new MathcingActivity.MenuMyListOnClickListener());

        TextView menu_addlist = (TextView)findViewById(R.id.menu_addList);
        menu_addlist.setOnClickListener(new MathcingActivity.MenuAddListOnClickListener());

        TextView button_matching_host = (TextView)findViewById(R.id.button_matching_host);
        button_matching_host.setOnClickListener(new MathcingActivity.ButtonMatchingHostOnClickListener());

        TextView button_matching_client = (TextView)findViewById(R.id.button_matching_client);
        button_matching_client.setOnClickListener(new MathcingActivity.ButtonMatchingClientOnClickListener());

        
    }

    private class MenuHomeOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MathcingActivity.this, MainActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MenuMyListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MathcingActivity.this, MyListActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MenuAddListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MathcingActivity.this, AddListActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class ButtonMatchingHostOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MathcingActivity.this, MatchingHostActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class ButtonMatchingClientOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MathcingActivity.this, MatchingClientActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

}
