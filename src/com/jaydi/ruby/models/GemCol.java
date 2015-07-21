package com.jaydi.ruby.models;

import java.util.List;

public class GemCol extends BaseModel {
	private List<Gem> gems;

	public GemCol() {
		super();
	}

	public GemCol(List<Gem> gems) {
		super();
		this.gems = gems;
	}

	public List<Gem> getGems() {
		return gems;
	}

	public void setGems(List<Gem> gems) {
		this.gems = gems;
	}

}
