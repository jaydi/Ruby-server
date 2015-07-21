package com.jaydi.ruby.models;

import java.util.List;

public class MineInfoCol {
	private List<MineInfo> mineInfos;

	public MineInfoCol() {
		super();
	}

	public MineInfoCol(List<MineInfo> mineInfos) {
		super();
		this.mineInfos = mineInfos;
	}

	public List<MineInfo> getMineInfos() {
		return mineInfos;
	}

	public void setMineInfos(List<MineInfo> mineInfos) {
		this.mineInfos = mineInfos;
	}

}
