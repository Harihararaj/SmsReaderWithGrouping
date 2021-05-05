package com.example.smsreaderwithgrouping;

public class NamesAndCountOfMessages {
    String contactName;
    String lastMessage;
    String readStatus;
    NamesAndCountOfMessages(String contactName, String lastMessage, String readStatus){
        this.contactName=contactName;
        this.lastMessage=lastMessage;
        this.readStatus=readStatus;
    }

}
