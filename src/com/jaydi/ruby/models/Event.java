package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Event extends BaseModel {
	public static final int TYPE_AD_FOLLOW = 1;
	public static final int TYPE_SIGN_UP = 2;
	public static final int TYPE_FIRST_VISIT = 3;
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private Long userId;

	@Persistent
	private Long targetId;

	@Persistent
	private String message;

	@Persistent
	private int type;

	@Persistent
	private float ruby;

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

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getRuby() {
		return ruby;
	}

	public void setRuby(float ruby) {
		this.ruby = ruby;
	}

}
