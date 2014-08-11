package com.todpop.api;

public class EngKorOX {
	public EngKorOX(String aEn,String aKr,String Check)
	{
		en = aEn;
		kr = aKr;
		check =Check;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getKr() {
		return kr;
	}
	public void setKr(String kr) {
		this.kr = kr;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	private String en;
	private String kr;
	private String check;
}
