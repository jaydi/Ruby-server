package com.jaydi.ruby.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MineInfo extends BaseModel {
	@Persistent
	@PrimaryKey
	private Long id;

	@Persistent
	private String address;

	@Persistent
	private String menu;

	@Persistent
	private String time;

	@Persistent
	private String price;

	@Persistent
	private String phone;

	@Persistent
	private double lat;

	@Persistent
	private double lng;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void update(MineInfo info) {
		if (info.getAddress() != null && !info.getAddress().isEmpty())
			address = info.getAddress();
		if (info.getMenu() != null && !info.getMenu().isEmpty())
			menu = info.getMenu();
		if (info.getPrice() != null && !info.getPrice().isEmpty())
			price = info.getPrice();
		if (info.getPhone() != null && !info.getPhone().isEmpty())
			phone = info.getPhone();
		if (info.getLat() != 0.0d)
			lat = info.getLat();
		if (info.getLng() != 0.0d)
			lng = info.getLng();
	}

}
