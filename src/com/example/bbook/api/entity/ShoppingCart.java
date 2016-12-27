package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.Goods;
import com.example.bbook.api.User;

public class ShoppingCart {
	public static class Key implements Serializable {
		User buyer;
		Goods goods;
		public User getBuyer() {
			return buyer;
		}
		public void setBuyer(User buyer) {
			this.buyer = buyer;
		}
		public Goods getGoods() {
			return goods;
		}
		public void setGoods(Goods goods) {
			this.goods = goods;
		}
	}

	Key id;
	int quantity;	// 数量
	Date createDate;
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
