package com.todpop.api;

public class KakaoObject {
	public KakaoObject(){
	}
	
	public String getMent() {
		return ment;
	}
	public void setMent(String ment) {
		this.ment = ment;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public int getImgWidth() {
		return imgWidth;
	}
	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
	public int getImgHeight() {
		return imgHeight;
	}
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}
	public String getBtnText() {
		return btnText;
	}
	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}
	
	String ment;
	String imgSrc;
	int imgWidth;
	int imgHeight;
	String btnText;
}
