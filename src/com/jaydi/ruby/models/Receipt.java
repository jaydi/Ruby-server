package com.jaydi.ruby.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Receipt {
	@Persistent
	@PrimaryKey
	private Long id;

	@Persistent
	private long shopId;

	@Persistent
	private long userId;

	@Persistent
	private int price;

	@Persistent
	private String menus;

	@Persistent
	private long createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

}
