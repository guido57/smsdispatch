package com.guido.smsdispatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ProfileItem> myDataset ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Create the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get data from SharedPrefs (if any) to Dataset
        myDataset = getMyDataset(getApplicationContext());
        // if empty or not containing mandatory items
        if(myDataset.size()==0

                  || searchMyDataset(new ProfileItem(true,ItemType.ButtonAddFrom,".*Receive SMS From.*")).size()!=1
                  || searchMyDataset(new ProfileItem(true,ItemType.ButtonAddTo,".*Send SMS To.*")).size()!=1
                )
          initMyDataset();

        // save myDataset to shared prefs
        saveMyDataset(myDataset, getApplicationContext());

        // Create the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyProfileAdapter(myDataset, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        // Create the FloatingActionBar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void initMyDataset(){

        myDataset.clear();


        // create Button Add From:
        ProfileItem btnAddFrom = new ProfileItem();
        btnAddFrom.Text ="Receive SMS From (click to add):";
        btnAddFrom.Type = ItemType.ButtonAddFrom;
        myDataset.add(btnAddFrom);

        // create temporary item
        ProfileItem pi = new ProfileItem();
        pi.Enabled = true;
        pi.Text ="riga di testo";
        pi.Type = ItemType.From;
                myDataset.add(pi);


        // create Button Add To:
        ProfileItem btnAddTo = new ProfileItem();
        btnAddTo.Text ="Send SMS To (click to add):";
        btnAddTo.Type = ItemType.ButtonAddTo;
                myDataset.add(btnAddTo);
    }

    ArrayList<ProfileItem> searchMyDataset(ProfileItem piTemplate){
        // search inside myDataset
        ArrayList<ProfileItem> result = new ArrayList<ProfileItem>();

        for(ProfileItem pi: myDataset){
            if(pi.Type == piTemplate.Type &&
               pi.Text.matches(piTemplate.Text)
            )
                result.add(pi);
        }
        return result;
    }


    static ArrayList<ProfileItem> getMyDataset(Context context){

        String key = "ArrayList_ProfileItems_Key";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String response=prefs.getString(key , "");

        ArrayList<ProfileItem> lstArrayList = gson.fromJson(response,
                new TypeToken<ArrayList<ProfileItem>>(){}.getType());

        if(lstArrayList == null)
            return new ArrayList<ProfileItem>();
        else
            return lstArrayList;
    }

    static void saveMyDataset(ArrayList<ProfileItem> alpi, Context context ){

        String key = "ArrayList_ProfileItems_Key";

        Gson gson = new Gson();

        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(context);

        String json = gson.toJson(alpi);

        editor = shref.edit();
        editor.remove(key).commit();
        editor.putString(key, json);
        editor.commit();
    }
}






