package com.jaydi.ruby.models;

import java.util.List;

public class UserpairCol extends BaseModel {
	private List<Userpair> userpairs;

	public UserpairCol() {
		super();
	}

	public UserpairCol(List<Userpair> userpairs) {
		super();
		this.userpairs = userpairs;
	}

	public List<Userpair> getUserpairs() {
		return userpairs;
	}

	public void setUserpairs(List<Userpair> userpairs) {
		this.userpairs = userpairs;
	}

}
