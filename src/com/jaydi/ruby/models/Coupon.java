package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Coupon extends BaseModel {
	public static final int STATE_UNUSED = 0;
	public static final int STATE_EXPIRED = 1;
	public static final int STATE_USED = 2;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	@Index
	private Long userId;

	@Persistent
	private Long gemId;

	@Persistent
	private String name;

	@Persistent
	private String rubymineName;

	@Persistent
	private String imageKey;

	@Persistent
	private String desc;

	@Persistent
	private long expirationDate;

	@Persistent
	private int state;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGemId() {
		return gemId;
	}

	public void setGemId(Long gemId) {
		this.gemId = gemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRubymineName() {
		return rubymineName;
	}

	public void setRubymineName(String rubymineName) {
		this.rubymineName = rubymineName;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
