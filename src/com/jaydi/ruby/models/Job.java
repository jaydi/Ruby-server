package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Job {
	public static final int TYPE_ERASE_PHONE = 1;
	public static final int TYPE_ERASE_EVENT = 2;
	public static final int TYPE_SIGNUP_REWARD = 3;
	public static final int TYPE_EXPIRE_COUPON = 4;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private int type;

	@Persistent
	private Long targetId;

	@Persistent
	@Index
	private long scheduledTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public long getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(long scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

}
