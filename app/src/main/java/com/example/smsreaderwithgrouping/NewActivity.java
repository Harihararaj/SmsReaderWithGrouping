package com.example.smsreaderwithgrouping;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class NewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Sms>> {
    int newSmsCount = 0;
    int toDisplayDataWhenScreenRotationHappen = 0;
    boolean isPermissionGranted = true;
    private static int loaderId = 0;
    int checkWhetherComesFromResume;
    SmsRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    Handler autoRefresh;
    Snackbar snackbar;
    boolean checkingWhetherComingFromOnCreate;
    private Parcelable stateForRecyclerView;
    String contactName =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contactName = extras.getString("contactName");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        Toolbar toolbar = findViewById(R.id.toolbarnew);
        setSupportActionBar(toolbar);
        toolbar.setElevation(4);
        toolbar.setTitle(contactName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkingWhetherComingFromOnCreate = true;
//        try {
//            toDisplayDataWhenScreenRotationHappen = savedInstanceState.getInt("newSms");
//            Log.d("toDisplay", "++++++++++++++++++" + toDisplayDataWhenScreenRotationHappen);
//        } catch (NullPointerException e) {
//            Log.d("NullPointerException", "InOnCreate");
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (stateForRecyclerView != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(stateForRecyclerView);
        }
//        if(toDisplayDataWhenScreenRotationHappen>0){
//            showSnackbar(toDisplayDataWhenScreenRotationHappen);
//        }
        if (checkingWhetherComingFromOnCreate) {
            checkingForPermission();
        }

    }



    public void checkingForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED && isPermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 4);
            isPermissionGranted = false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED && !isPermissionGranted) {
            displayDialog();

        } else {
            checkWhetherComesFromResume = 0;
            checkingWhetherComingFromOnCreate = false;
            this.autoRefresh = new Handler();
            m_Runnable.run();
        }
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run() {

            ReadSms();
            NewActivity.this.autoRefresh.postDelayed(m_Runnable, 1000);
        }

    };


    public void displayDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(NewActivity.this);
        builder.setMessage("This App cannot be Accessed without Your permission. If you want to continue with the app press 'Yes' or to close app press 'No'");
        builder.setTitle("Permission Denied");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            ActivityCompat.requestPermissions(NewActivity.this, new String[]{Manifest.permission.READ_SMS}, 4);
            dialog.cancel();
        });

        builder.setNegativeButton("No", (dialog, which) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void ReadSms() {
        loaderId++;
        LoaderManager.getInstance(this).initLoader(loaderId, null, this);


    }

    @NonNull
    @Override
    public Loader<ArrayList<Sms>> onCreateLoader(int id, @Nullable Bundle args) {
        return new SmsLoaderFromContentProvider(this, contactName);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Sms>> loader, ArrayList<Sms> data) {
        if (checkWhetherComesFromResume == 0) {
            initializingAdapterAndSettingValueToRecyclerView(data);
            checkWhetherComesFromResume = 1;


        } else {
            int numberOfNewSms = adapter.updatingAlreadyExsistingSmsListWithNewSms(data);
            if (numberOfNewSms > 0) {
                showSnackbar(numberOfNewSms);

            }


        }
        LoaderManager.getInstance(this).destroyLoader(loaderId);
    }

    public void initializingAdapterAndSettingValueToRecyclerView(ArrayList<Sms> smsList) {
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new SmsRecyclerViewAdapter(smsList, this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void showSnackbar(int numberOfNewSms) {
        newSmsCount = newSmsCount + numberOfNewSms;
        RelativeLayout relativeLayout = findViewById(R.id.reletivelayout);
        snackbar = Snackbar.make(relativeLayout, newSmsCount + " new Message", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("See Message", v -> {
            recyclerView.setAdapter(adapter);
            newSmsCount = 0;
        });

        snackbar.show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Sms>> loader) {
    }




}
