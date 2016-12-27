package com.example.bbook.api;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Chat implements Serializable{
        User sender;
        User receiver;
        Date createDate;
        int id;
        String  content;
        
        public User getSender() {
                return sender;
        }
        public void setSender(User sender) {
                this.sender = sender;
        }
        public User getReceiver() {
                return receiver;
        }
        public void setReceiver(User receiver) {
                this.receiver = receiver;
        }
        public Date getCreateDate() {
                return createDate;
        }
        public void setCreateDate(Date createDate) {
                this.createDate = createDate;
        }
        public int getId() {
                return id;
        }
        public void setId(int id) {
                this.id = id;
        }
        public String getContent() {
                return content;
        }
        public void setContent(String content) {
                this.content = content;
        }
        
}
