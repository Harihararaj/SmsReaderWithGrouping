package com.example.smsreaderwithgrouping;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

import java.util.Map;

public class ContactsLoader extends AsyncTaskLoader<ArrayList<NamesAndCountOfMessages>> {

    public ContactsLoader(@NonNull Context context) {
        super(context);

    }

    @Nullable
    @Override
    public ArrayList<NamesAndCountOfMessages> loadInBackground() {
        ArrayList<String> allNamesInContentProvider=new ArrayList<>();
        Cursor cursorToReadSmsFromContentProvider=getContext().getContentResolver().query(Uri.parse("content://sms"),null,null,null,null);
        cursorToReadSmsFromContentProvider.moveToFirst();
        for(int i=0;i<cursorToReadSmsFromContentProvider.getCount();i++){
            allNamesInContentProvider.add(cursorToReadSmsFromContentProvider.getString(cursorToReadSmsFromContentProvider.getColumnIndex("address")));
            cursorToReadSmsFromContentProvider.moveToNext();
        }

        ArrayList<String> uniqueNames=new ArrayList<>();
        for(int i=0;i<allNamesInContentProvider.size();i++){
            if(!uniqueNames.contains(allNamesInContentProvider.get(i))){
                uniqueNames.add(allNamesInContentProvider.get(i));
            }
        }
//        ArrayList<Integer> countOfUnreadSms=new ArrayList<>();
//
//        for(int i=0;i<uniqueNames.size();i++){
//            Cursor cursorForCount=getContext().getContentResolver().query(Uri.parse("content://sms"),new String[]{"_id"},"read=0 AND address=?",new String[]{uniqueNames.get(i)},null);
//            cursorForCount.moveToFirst();
//            countOfUnreadSms.add(cursorForCount.getCount());
//            cursorForCount.close();
//        }
        ArrayList<NamesAndCountOfMessages> namesAndCountOfMessagesList=new ArrayList<>();
        for(int i=0;i<uniqueNames.size();i++){
            cursorToReadSmsFromContentProvider.moveToFirst();
            for(int j=0;j<cursorToReadSmsFromContentProvider.getCount();j++){
                if(cursorToReadSmsFromContentProvider.getString(cursorToReadSmsFromContentProvider.getColumnIndex("address")).equals(uniqueNames.get(i))){
                    namesAndCountOfMessagesList.add(new NamesAndCountOfMessages(uniqueNames.get(i),cursorToReadSmsFromContentProvider.getString(cursorToReadSmsFromContentProvider.getColumnIndex("body")),cursorToReadSmsFromContentProvider.getString(cursorToReadSmsFromContentProvider.getColumnIndex("read"))));
                    break;
                }
                cursorToReadSmsFromContentProvider.moveToNext();
            }

        }
        cursorToReadSmsFromContentProvider.close();


        return namesAndCountOfMessagesList;


    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
