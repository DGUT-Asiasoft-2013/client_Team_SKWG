package com.example.bbook.api.entity;

import java.io.Serializable;
import java.util.Date;

import com.example.bbook.api.Goods;



public class Orders implements Serializable {
    String ordersID;                                     // 订单号
    int ordersState;                              // 订单状态，  0：已取消订单  2：已下单   3：已付款   4：已发货  5：已收货 1完成订单
    Goods goods;                                        // 商品
    String goodsQTY;                                //购买数量
    double goodsSum;                               //商品总额
    String buyerName;                            //客户姓名
    String buyerPhoneNum;                 //联系方式
    String buyerAddress;                       //客户地址
    String postCode;		// 邮政编码
    String paySum;  
    Date createDate;
    Date editDate;
    Integer id;
	public String getOrdersID() {
		return ordersID;
	}
	public void setOrdersID(String ordersID) {
		this.ordersID = ordersID;
	}
	public int getOrdersState() {
		return ordersState;
	}
	public void setOrdersState(int ordersState) {
		this.ordersState = ordersState;
	}
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public String getGoodsQTY() {
		return goodsQTY;
	}
	public void setGoodsQTY(String goodsQTY) {
		this.goodsQTY = goodsQTY;
	}
	public double getGoodsSum() {
		return goodsSum;
	}
	public void setGoodsSum(double goodsSum) {
		this.goodsSum = goodsSum;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerPhoneNum() {
		return buyerPhoneNum;
	}
	public void setBuyerPhoneNum(String buyerPhoneNum) {
		this.buyerPhoneNum = buyerPhoneNum;
	}
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPaySum() {
		return paySum;
	}
	public void setPaySum(String paySum) {
		this.paySum = paySum;
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
    
    
}
