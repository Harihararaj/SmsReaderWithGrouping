package com.example.smsreaderwithgrouping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class SmsLoaderFromContentProvider extends AsyncTaskLoader<ArrayList<Sms>> {
    ArrayList<Sms> smsList =new ArrayList<>();
    String contactName;
    Context context;
    public SmsLoaderFromContentProvider(@NonNull Context context,String contactName) {
        super(context);
        this.context=context;
        this.contactName=contactName;
    }

    @Nullable
    @Override
    public ArrayList<Sms> loadInBackground() {
        if(smsList.size()>0){
            smsList.clear();
        }
        Cursor smsContentCursor = getContext().getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        smsContentCursor.moveToFirst();
        String mobile = null,message = null,type = null;
        ArrayList<String> idOfReadMessages=new ArrayList<>();
        for (int i = 0; i < smsContentCursor.getCount(); i++) {
            try {
                mobile = smsContentCursor.getString(smsContentCursor.getColumnIndexOrThrow("address"));
            }
            catch (IllegalStateException e){
                e.getStackTrace();
            }
            try {
                message = smsContentCursor.getString(smsContentCursor.getColumnIndexOrThrow("body"));
            }
            catch (IllegalStateException e) {
                e.getStackTrace();
            }
            try {

                type = smsContentCursor.getString(smsContentCursor.getColumnIndexOrThrow("type"));
            }
            catch (IllegalStateException e){
                e.getStackTrace();
            }
            if(mobile.equals(contactName) || mobile.equals("+91"+contactName) || contactName.equals("+91"+mobile)){
                smsList.add(new Sms(mobile, message,type));
                idOfReadMessages.add(smsContentCursor.getString(smsContentCursor.getColumnIndexOrThrow("_id")));
            }
            smsContentCursor.moveToNext();
        }
        smsContentCursor.close();
        for(int i=0;i<idOfReadMessages.size();i++){
            ContentValues values = new ContentValues();
            values.put("read", true);
            Cursor cursor=context.getContentResolver().query(Uri.parse("content://sms"),null , "_id=?",new String[]{idOfReadMessages.get(i)},null);

            Log.d("id", DatabaseUtils.dumpCursorToString(cursor));
            context.getContentResolver().update(Uri.parse("content://sms"), values, "_id=?",new String[]{idOfReadMessages.get(i)} );
        }
        return smsList;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
