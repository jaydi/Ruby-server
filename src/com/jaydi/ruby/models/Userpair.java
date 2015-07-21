package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Userpair extends BaseModel {
	public static final int STATE_ALIVE = 0;
	public static final int STATE_DEAD = 1;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	@Index
	private Long userIdA;

	@Persistent
	@Index
	private Long userIdB;

	@Persistent
	private String userNameA;

	@Persistent
	private String userNameB;

	@Persistent
	private String userImageKeyA;

	@Persistent
	private String userImageKeyB;

	@Persistent
	private int state;

	public Userpair() {
		super();
	}

	public Userpair(Userpair userpair) {
		super();
		this.id = userpair.getId();
		this.userIdA = userpair.getUserIdA();
		this.userIdB = userpair.getUserIdB();
		this.userNameA = userpair.getUserNameA();
		this.userNameB = userpair.getUserNameB();
		this.userImageKeyA = userpair.getUserImageKeyA();
		this.userImageKeyB = userpair.getUserImageKeyB();
		this.state = userpair.getState();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserIdA() {
		return userIdA;
	}

	public void setUserIdA(Long userIdA) {
		this.userIdA = userIdA;
	}

	public Long getUserIdB() {
		return userIdB;
	}

	public void setUserIdB(Long userIdB) {
		this.userIdB = userIdB;
	}

	public String getUserNameA() {
		return userNameA;
	}

	public void setUserNameA(String userNameA) {
		this.userNameA = userNameA;
	}

	public String getUserNameB() {
		return userNameB;
	}

	public void setUserNameB(String userNameB) {
		this.userNameB = userNameB;
	}

	public String getUserImageKeyA() {
		return userImageKeyA;
	}

	public void setUserImageKeyA(String userImageKeyA) {
		this.userImageKeyA = userImageKeyA;
	}

	public String getUserImageKeyB() {
		return userImageKeyB;
	}

	public void setUserImageKeyB(String userImageKeyB) {
		this.userImageKeyB = userImageKeyB;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void update(Userpair userpair) {
		this.state = userpair.getState();
	}

}
