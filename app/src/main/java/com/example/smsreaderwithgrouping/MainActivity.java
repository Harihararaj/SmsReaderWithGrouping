package com.example.smsreaderwithgrouping;

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

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NamesAndCountOfMessages>> {
    boolean isPermissionGranted=true;
    boolean isItFromOncreate;
    private Handler autoRefresh;
    int loaderId=0;
    boolean isItFromOnResume;
    RecyclerView recyclerView=null;
    ContactsRecyclerAdapter adapter;
    private Parcelable stateForRecyclerView;
    boolean toCheckRecyclerViewIsNotNull=false;
    ArrayList<NamesAndCountOfMessages> tempararyNamesList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        isItFromOncreate=true;

    }

    @Override

    protected void onResume() {
        if (stateForRecyclerView != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(stateForRecyclerView);
        }
        super.onResume();
        if(isItFromOncreate) {
            checkingForPermission();
            isItFromOnResume=true;
        }else {
            this.autoRefresh = new Handler();
            m_Runnable.run();
        }
    }
    public void checkingForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED && isPermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
            isPermissionGranted = false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED && !isPermissionGranted) {
            displayDialog();

        } else {
            isItFromOncreate=false;

            this.autoRefresh = new Handler();
            m_Runnable.run();
        }
    }
    private final Runnable m_Runnable = new Runnable() {
        public void run() {

            readingUniqueNumbers();
            MainActivity.this.autoRefresh.postDelayed(m_Runnable, 1000);
        }

    };
    public void readingUniqueNumbers() {
        loaderId++;

        LoaderManager.getInstance(this).initLoader(loaderId, null, this);
    }
    public void displayDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("This App cannot be Accessed without Your permission. If you want to continue with the app press 'Yes' or to close app press 'No'");
        builder.setTitle("Permission Denied");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 4);
            dialog.cancel();
        });

        builder.setNegativeButton("No", (dialog, which) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @NonNull
    @Override
    public Loader<ArrayList<NamesAndCountOfMessages>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ContactsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<NamesAndCountOfMessages>> loader, ArrayList<NamesAndCountOfMessages> data) {




        if(isItFromOnResume) {
            Log.d("from onresume","numberOfData"+data.size()+"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+loaderId);
            recyclerView = findViewById(R.id.recyclerview);
            adapter = new ContactsRecyclerAdapter(data,this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            toCheckRecyclerViewIsNotNull=true;
            isItFromOnResume=false;
        }
        else{
            Log.d("OnLoadFinished","numberOfData"+data.size()+"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+loaderId);
            adapter.setChangedData(data);
        }

        LoaderManager.getInstance(this).destroyLoader(loaderId);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<NamesAndCountOfMessages>> loader) {

    }

}