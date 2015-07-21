package com.jaydi.ruby.models;

import java.util.List;

public class RubyCol extends BaseModel {
	private List<Ruby> rubies;
	private Rubymine planter;
	private List<Rubymine> givers;

	public RubyCol() {
		super();
	}

	public RubyCol(List<Ruby> rubies) {
		super();
		this.rubies = rubies;
	}

	public List<Ruby> getRubies() {
		return rubies;
	}

	public void setRubies(List<Ruby> rubies) {
		this.rubies = rubies;
	}

	public Rubymine getPlanter() {
		return planter;
	}

	public void setPlanter(Rubymine planter) {
		this.planter = planter;
	}

	public List<Rubymine> getGivers() {
		return givers;
	}

	public void setGivers(List<Rubymine> givers) {
		this.givers = givers;
	}

}
