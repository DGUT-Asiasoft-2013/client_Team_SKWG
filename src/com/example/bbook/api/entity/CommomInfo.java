package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.User;

public class CommomInfo implements Serializable {
	Integer id;
	String name;
	String address;
	String postCode;
	String tel;
	Date createDate;
	Date editDate;
	boolean isDefaultInfo;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
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
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public boolean isDefaultInfo() {
		return isDefaultInfo;
	}
	public void setDefaultInfo(boolean isDefaultInfo) {
		this.isDefaultInfo = isDefaultInfo;
	}
	


}
