package com.jaydi.ruby.models;

import java.util.List;

public class LogCol {
	private List<Log> logs;

	public LogCol() {
		super();
	}

	public LogCol(List<Log> logs) {
		super();
		this.logs = logs;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

}
