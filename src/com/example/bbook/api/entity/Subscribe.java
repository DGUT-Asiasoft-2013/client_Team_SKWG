package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.Shop;
import com.example.bbook.api.User;

public class Subscribe implements Serializable {

        public static class Key implements Serializable {
                Shop shop;
                User user;

                public Shop getShop() {
                        return shop;
                }

                public void setShop(Shop shop) {
                        this.shop = shop;
                }

                public User getUser() {
                        return user;
                }

                public void setUser(User user) {
                        this.user = user;
                }
        }

        Key id;
        Date createDate;

        public Key getId() {
                return id;
        }

        public void setId(Key id) {
                this.id = id;
        }

        public Date getCreateDate() {
                return createDate;
        }

        public void setCreateDate(Date createDate) {
                this.createDate = createDate;
        }

        void onPrePersist() {
                createDate = new Date();
        }
}
