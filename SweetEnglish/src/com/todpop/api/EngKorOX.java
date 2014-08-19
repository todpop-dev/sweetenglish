package com.todpop.api;

public class EngKorOX {
	public EngKorOX(String aEn,String aKr,String Check)
	{
		isNew = 0;
		en = aEn;
		kr = aKr;
		check =Check;
	}
	public EngKorOX(int aNew, String aEn,String aKr,String Check){
		isNew = aNew;
		en = aEn;
		kr = aKr;
		check =Check;
	}
	public void setIsNew(int isNew){
		this.isNew = isNew;
	}
	public int getIsNew(){
		return isNew;
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
	private int isNew;
	private String en;
	private String kr;
	private String check;
}
