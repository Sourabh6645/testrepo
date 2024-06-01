package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTxnReverseDetailsPage {
	
	@FindBy (name = "remarks")
	private WebElement remarks;
	
	@FindBy (name = "revTrx")
	private WebElement reverseTransaction;
	
	@FindBy (name = "backButton")
	private WebElement backButton;
	
	WebDriver driver = null;

	public O2CTxnReverseDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks: "+Remarks);
	}
	
	public void clickReverseTransaction(){
		reverseTransaction.click();
	}
	
	

}
