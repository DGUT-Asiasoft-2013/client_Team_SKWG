package com.example.bbook.api;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable{
	Integer id;
	User author;
	Date createDate;
	Date editDate;

	String title;
	String text;
	
	String authorName;
	String authorAvatar;
	String articlesImage;
	
	
	public String getArticlesImage() {
		return articlesImage;
	}

	public void setArticlesImage(String articlesImage) {
		this.articlesImage = articlesImage;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorAvatar() {
		return authorAvatar;
	}

	public void setAuthorAvatar(String authorAvatar) {
		this.authorAvatar = authorAvatar;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
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
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
