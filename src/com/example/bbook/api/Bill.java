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
	
	
}
