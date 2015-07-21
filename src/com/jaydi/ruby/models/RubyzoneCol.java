package com.jaydi.ruby.models;

import java.util.List;

public class RubyzoneCol extends BaseModel {
	private List<Rubyzone> rubyzones;

	public RubyzoneCol() {
		super();
	}

	public RubyzoneCol(List<Rubyzone> rubyzones) {
		super();
		this.rubyzones = rubyzones;
	}

	public List<Rubyzone> getRubyzones() {
		return rubyzones;
	}

	public void setRubyzones(List<Rubyzone> rubyzones) {
		this.rubyzones = rubyzones;
	}

}
