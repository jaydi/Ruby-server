package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Log {
	public static final int KIND_VISIT = 100;
	public static final int KIND_RUBY_GOT = 106;
	public static final int KIND_RUBY = 101;
	public static final int KIND_AD_GOT = 102;
	public static final int KIND_AD_SEEN = 103;
	public static final int KIND_PURCHASE = 104;
	public static final int KIND_COUPON_USE = 105;
	
	public static final int KIND_VIEW_ON = 200;
	public static final int KIND_VIEW_OFF = 201;
	
	public static final int KIND_USER_PAIR = 300;
	public static final int KIND_USER_DEPAIR = 301;
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private int kind;

	@Persistent
	private long userId;

	@Persistent
	private long targetId;

	@Persistent
	private long refer;

	@Persistent
	private int type;

	@Persistent
	@Index
	private long createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	public long getRefer() {
		return refer;
	}

	public void setRefer(long refer) {
		this.refer = refer;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

}
