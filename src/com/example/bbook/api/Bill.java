package com.example.bbook.api;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
/**
 * @author 刘世杰
 * 每次进行一匹交易时都要存入账单(付款,收款,充值....)
 * 传入收支类型(支出传个state=0,收入则传1)
 * 传入收支的金额是多少  item
 * 以及备注从哪里来的钱/花哪去了   detial
 */

public class Bill implements Serializable {

	Integer id;
	
	User user; 						// 账单对应用户
	UUID billNumber; 				// 账单流水号  会自动生成
	int billState; 					// 收支类型		1.收入    0.支出
	Date createDate; 				// 创建时间
	String detial; 					// 收支备注
	Double item;   					//收支金额
	Double money; 					// 余额
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public UUID getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(UUID billNumber) {
		this.billNumber = billNumber;
	}
	public int getBillState() {
		return billState;
	}
	public void setBillState(int billState) {
		this.billState = billState;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getDetial() {
		return detial;
	}
	public void setDetial(String detial) {
		this.detial = detial;
	}
	public Double getItem() {
		return item;
	}
	public void setItem(Double item) {
		this.item = item;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	
	
}
