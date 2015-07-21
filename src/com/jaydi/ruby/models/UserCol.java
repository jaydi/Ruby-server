package com.jaydi.ruby.models;

import java.util.List;

public class UserCol extends BaseModel {
	private List<User> users;

	public UserCol() {
		super();
	}

	public UserCol(List<User> users) {
		super();
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
