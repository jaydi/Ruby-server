package com.jaydi.ruby.models;

public class ReceiptResponse {
	private String result;
	private int code;
	private String message;
	private String codeInfo;
	private int numCheckin;
	private String infoTitle;
	private String infoPostScript;
	private String infoReward;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCodeInfo() {
		return codeInfo;
	}

	public void setCodeInfo(String codeInfo) {
		this.codeInfo = codeInfo;
	}

	public int getNumCheckin() {
		return numCheckin;
	}

	public void setNumCheckin(int numCheckin) {
		this.numCheckin = numCheckin;
	}

	public String getInfoTitle() {
		return infoTitle;
	}

	public void setInfoTitle(String infoTitle) {
		this.infoTitle = infoTitle;
	}

	public String getInfoPostScript() {
		return infoPostScript;
	}

	public void setInfoPostScript(String infoPostScript) {
		this.infoPostScript = infoPostScript;
	}

	public String getInfoReward() {
		return infoReward;
	}

	public void setInfoReward(String infoReward) {
		this.infoReward = infoReward;
	}

}
