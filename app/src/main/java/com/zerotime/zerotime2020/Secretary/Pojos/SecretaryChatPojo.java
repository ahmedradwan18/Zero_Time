package com.zerotime.zerotime2020.Secretary.Pojos;

public class SecretaryChatPojo {
    private String Message,Sender,Receiver,Type;
    public boolean isSeen;


    public SecretaryChatPojo() {
    }

    public SecretaryChatPojo(String message, String sender, String receiver, String type, boolean isSeen) {
        Message = message;
        Sender = sender;
        Receiver = receiver;
        Type = type;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
