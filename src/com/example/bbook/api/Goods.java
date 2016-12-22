package com.example.bbook.api;


import java.io.Serializable;
import java.util.Date;


public class Goods   implements Serializable {

	
	Integer id;
    Date createDate;
    Date editDate;
	String goodsName;
	String goodsType;
	String goodsPrice;
	String goodsCount;
	String goodsImage;
	String publisher;
	String author;
	String pubDate;
	String pritime;
//	User seller;
//	User buyer;
	
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

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}
	public String getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public String getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(String goodsCount) {
		this.goodsCount = goodsCount;
	}
	public String getGoodsImage() {
		return goodsImage;
	}
	public void setGoodsImage(String goodsImage) {
		this.goodsImage = goodsImage;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getPritime() {
		return pritime;
	}
	public void setPritime(String pritime) {
		this.pritime = pritime;
	}
//	public User getSeller() {
//		return seller;
//	}
//	public void setSeller(User seller) {
//		this.seller = seller;
//	}
//	public User getBuyer() {
//		return buyer;
//	}
//	public void setBuyer(User buyer) {
//		this.buyer = buyer;
//	}
}
