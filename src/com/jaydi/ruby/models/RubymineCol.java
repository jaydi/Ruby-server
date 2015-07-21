package com.jaydi.ruby.models;

import java.util.List;

public class RubymineCol extends BaseModel {
	private List<Rubymine> rubymines;

	public RubymineCol() {
		super();
	}

	public RubymineCol(List<Rubymine> rubymines) {
		super();
		this.rubymines = rubymines;
	}

	public List<Rubymine> getRubymines() {
		return rubymines;
	}

	public void setRubymines(List<Rubymine> rubymines) {
		this.rubymines = rubymines;
	}

}
