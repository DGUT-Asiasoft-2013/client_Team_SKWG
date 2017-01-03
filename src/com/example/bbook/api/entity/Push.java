package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.Shop;
import com.example.bbook.api.User;

public class Push implements Serializable {
        int id;
        Shop shop;
        User receiver;
        String content;
        Date createDate;
        Date editDate;
        
        public int getId() {
                return id;
        }
        public void setId(int id) {
                this.id = id;
        }
        public Shop getShop() {
                return shop;
        }
        public void setShop(Shop shop) {
                this.shop = shop;
        }
        public User getReceiver() {
                return receiver;
        }
        public void setReceiver(User receiver) {
                this.receiver = receiver;
        }
        public String getContent() {
                return content;
        }
        public void setContent(String content) {
                this.content = content;
        }
        public Date getCreateDate() {
                return createDate;
        }
        public void setCreateDate(Date createDate) {
                this.createDate = createDate;
        }
        public Date getEditDate() {
                return editDate;
        }
        public void setEditDate(Date editDate) {
                this.editDate = editDate;
        }
        
}
