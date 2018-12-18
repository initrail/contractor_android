package com.integrail.networkers.data_representations.message_representations;

/**
 * Created by Integrail on 7/22/2016.
 */

public class LatestConversationData {
    private long conversationId;
    private String ogSender;
    private String senderName;
    private String ogReceiver;
    private String receiverName;
    private long count;
    private long oldCount;
    private long userId;
    public String getSenderName() {
        return senderName;
    }
    public long getUserId(){
        return userId;
    }
    public void setUserId(long userId){
        this.userId = userId;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public String getOgSender() {
        return ogSender;
    }

    public void setOgSender(String ogSender) {
        this.ogSender = ogSender;
    }

    public String getOgReceiver() {
        return ogReceiver;
    }

    public void setOgReceiver(String ogReceiver) {
        this.ogReceiver = ogReceiver;
    }

    public LatestConversationData(long c, long  i){
        conversationId = c;
        count = i;
    }
    public long getConversationId(){
        return conversationId;
    }
    public long getCount(){
        return count;
    }
    public long getOldCount(){return oldCount;}
    public void setCount(long count){
        this.count = count;
    }
    public void swap(){
        long copy = oldCount;
        oldCount = count;
        count = copy;
    }
    public void setOldCount(long count){
        this.oldCount = oldCount;
    }
}
