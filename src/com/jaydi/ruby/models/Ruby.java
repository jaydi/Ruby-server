package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Ruby extends BaseModel {
	public static final int EVENT_ZONE_IN = 1;
	public static final int EVENT_MINE_IN = 2;
	public static final int EVENT_PURCHASE = 3;
	public static final int EVENT_SIGN_UP = 11;
	public static final int EVENT_FIRST_VISIT = 12;
	public static final int EVENT_AD_FOLLOW = 13;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private Long userId;

	@Persistent
	@Index
	private Long giverId;

	@Persistent
	@Index
	private Long planterId;

	@Persistent
	private float value;

	@Persistent
	private long createdAt;

	@Persistent
	private int event;

	public Ruby() {
		super();
	}

	public Ruby(Ruby ruby) {
		super();
		id = ruby.getId();
		giverId = ruby.getGiverId();
		planterId = ruby.getPlanterId();
		value = ruby.getValue();
		userId = ruby.getUserId();
	}

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

	public Long getGiverId() {
		return giverId;
	}

	public void setGiverId(Long giverId) {
		this.giverId = giverId;
	}

	public Long getPlanterId() {
		return planterId;
	}

	public void setPlanterId(Long planterId) {
		this.planterId = planterId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}

}
