package com.jaydi.ruby.models;

import java.util.List;

public class CouponCol extends BaseModel {
	private List<Coupon> coupons;

	public CouponCol() {
		super();
	}

	public CouponCol(List<Coupon> coupons) {
		super();
		this.coupons = coupons;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

}
