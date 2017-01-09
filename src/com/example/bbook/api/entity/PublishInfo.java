package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.User;

public class PublishInfo implements Serializable {
	String goodsPublisher;
    String goodsAuthor;
    String goodsPubDate;
    String goodsPritime;
	public String getGoodsPublisher() {
		return goodsPublisher;
	}
	public void setGoodsPublisher(String goodsPublisher) {
		this.goodsPublisher = goodsPublisher;
	}
	public String getGoodsAuthor() {
		return goodsAuthor;
	}
	public void setGoodsAuthor(String goodsAuthor) {
		this.goodsAuthor = goodsAuthor;
	}
	public String getGoodsPubDate() {
		return goodsPubDate;
	}
	public void setGoodsPubDate(String goodsPubDate) {
		this.goodsPubDate = goodsPubDate;
	}
	public String getGoodsPritime() {
		return goodsPritime;
	}
	public void setGoodsPritime(String goodsPritime) {
		this.goodsPritime = goodsPritime;
	}
    
}
