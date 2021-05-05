package com.example.smsreaderwithgrouping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder>{
    ArrayList<NamesAndCountOfMessages> contactName;
    Context context;
    ContactsRecyclerAdapter(ArrayList<NamesAndCountOfMessages> contactName,Context context){
        this.contactName=contactName;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder1,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contactNames.setText(contactName.get(position).contactName);
        holder.lastMessage.setText(contactName.get(position).lastMessage);

        if(contactName.get(position).readStatus.equals("0")){
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
            holder.contactNames.setTypeface(null, Typeface.BOLD);
            holder.contactNames.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        else{
            holder.lastMessage.setTypeface(null, Typeface.NORMAL);
            holder.contactNames.setTypeface(null, Typeface.NORMAL);
            holder.contactNames.setTextColor(ContextCompat.getColor(context, R.color.light_black));
            holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.light_black));
        }
        holder.oneViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,NewActivity.class);
                intent.putExtra("contactName",contactName.get(position).contactName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView contactNames, lastMessage, readStaus;
        RelativeLayout oneViewHolder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNames=itemView.findViewById(R.id.contact_name);
            lastMessage =itemView.findViewById(R.id.sms_message);
            readStaus =itemView.findViewById(R.id.read_status);
            oneViewHolder=itemView.findViewById(R.id.parent_viewholder);
        }
    }
    public void setChangedData(ArrayList<NamesAndCountOfMessages> newNames){
        contactName.clear();
        contactName.addAll(newNames);
        notifyDataSetChanged();
    }
}
