package kz.nicnbk.service.impl.bloomberg.dto;

import com.bloomberglp.blpapi.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageList {
    private List<Message> messageList;

    public MessageList() {
        messageList = new ArrayList<>();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
