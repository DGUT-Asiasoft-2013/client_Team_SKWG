package com.example.bbook.api;

import java.io.Serializable;

public class User implements Serializable {
	Integer id;
	String isStore;

	String account;
	String name;
	String passwordHash;
	String email;
	String address;
	String phoneNum;
	String avatar;
	String payPassword;
	double money;
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getIsStore() {
		return isStore;
	}
	public void setIsStore(String isStore) {
		this.isStore = isStore;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public String getName() {
		return name;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public void setName(String name) {
		this.name = name;
	}
}
