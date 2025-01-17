package angular.feature.TransferRules;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.classes.BaseTest;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.RechargeBulk.RechargesBulk;
import angular.pageobjects.TranferRule.PromotinalTransferRule_PageObject;

public class PromotionalTransferRule_Feature extends BaseTest {

	public WebDriver driver;
	WebDriverWait wait;
	LoginRevamp login;
	PromotinalTransferRule_PageObject promo;
	RechargesBulk rb;
	RandomGeneration random;
	String uploadPath = null;
	
	public PromotionalTransferRule_Feature(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 20);
		login = new LoginRevamp();
		promo = new PromotinalTransferRule_PageObject(driver);
	}

	String PromotionLevelUser = "User";
	String Domain = "Distributor";
	String Category = "Super Distributor";
	String GeographicalDomainType = "Zone";
	String Geography = "Delhi-Ncr";
	String UserSearch = "qa";
	String RecieverDetailsType = "Prepaid Subscriber";
	String ServiceProviderGroup = "new";
	String GroupSetStatus = "Active";
	String ServiceType = "C2S Gift Recharge";
	String SubService = "CVG";
	String CardGroupSet = "AUT1GFEO5";
	String ModifiedStatus = "Suspended";
	String ModifiedCardGroupSet = " AUT21JhOm";

	public void selectValueFromDropDown(String value) {
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class,'ng-option-label')]")));

		WebElement elementToBeSelected = driver
				.findElement(By.xpath("//span[contains(@class,'ng-option-label')][contains(text(),'" + value + "')]"));

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementToBeSelected);

		elementToBeSelected.click();

		Log.info("Selected value from DD : " + value);

	}

	public void selectDateAndTimeRange() {

		Log.info("Selecting date applicable from");

		WebElement fromDate = driver.findElement(By.xpath("(//span[contains(@class,'ui-calendar')]//input)[1]"));
		fromDate.click();

		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class,'ui-state-highlight')]")))
				.click();

		Log.info("Selecting date applicable to");
		WebElement toDate = driver.findElement(By.xpath("(//span[contains(@class,'ui-calendar')]//input)[2]"));
		toDate.click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//a[contains(@class,'ui-state-highlight')]//following::a[1]")))
				.click();

	}
	
	public void updateTeplateFileToUpload() {
		String filename = null;
		String PathOfFile = _masterVO.getProperty("BulkPromotionalTransferRule");
		filename = rb.getLatestFilefromDir(PathOfFile);
		File originalFile = new File(filename);
		File renamedFile = new File(System.getProperty("user.dir")
				+ "//src//test//resources//UploadDocuments//Bulk_Promotional_Transfer_Rule//" + "userTemplateList"
				+ random.randomAlphaNumeric(12) + ".xls");
		Log.info("Rename File name : "+renamedFile);
		try {
			FileUtils.copyFile(originalFile, renamedFile);
			FileUtils.forceDelete(originalFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		filename = rb.getLatestFileNamefromDir(PathOfFile);
		uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("BulkPromotionalTransferRuleUpload") + filename; //+ _masterVO.getProperty("O2CMultiWalletBulkCommissionPayout")
		
		
		Log.info("Complete File Path : "+uploadPath);
		

	}
	public void CreatePromotionalTransferRule() {

		promo.clickOnPromotionalTransferRuleLink();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

		promo.clickOnAddPromotionalTransferDetails();

		promo.checkVisibilityOfAddPromotionalTransferRuleDetailsPageLoadedSuccessfully();

		promo.clickOnPromotionalLevelDD();

		selectValueFromDropDown(PromotionLevelUser);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		promo.clickOnAddDomainDD();

		selectValueFromDropDown(Domain);

		promo.clickOnAddCategoryDD();

		selectValueFromDropDown(Category);

		promo.clickOnAddGeographicalDomainTypeDD();

		selectValueFromDropDown(GeographicalDomainType);

		promo.clickOnGeographyDD();

		selectValueFromDropDown(Geography);

		promo.insertUserInSearchField(UserSearch);

		promo.clickOnSearchIcon();

		promo.checkSearchUserList();

		promo.clickOnSubmitUserButton();

		promo.clickOnReceiverDetailsTypeDD();

		selectValueFromDropDown(RecieverDetailsType);

		promo.clickOnServiceProviderGroupDD();

		selectValueFromDropDown(ServiceProviderGroup);

		promo.clickOnCardGroupSetStatusDD();

		selectValueFromDropDown(GroupSetStatus);

		promo.clickOnServiceTypeDD();

		selectValueFromDropDown(ServiceType);

		promo.clickOnSubServiceDD();

		selectValueFromDropDown(SubService);

		promo.clickOnCardGroupSetDD();

		selectValueFromDropDown(CardGroupSet);

		selectDateAndTimeRange();

		promo.clickOnSaveRuleButton();

		promo.clickOnConfirmRuleButton();

		promo.clickOnDoneButton();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

	}

	public void selectFilterValueOnPromotionalTransferPage() {

		promo.selectFilterValue(PromotionLevelUser);

		promo.clickOnDomainDDButton();

		selectValueFromDropDown(Domain);

		promo.clickOnCategoryDDButton();

		selectValueFromDropDown(Category);

		promo.clickOnGeographicalDomainTypeDDButton();

		selectValueFromDropDown(GeographicalDomainType);

		promo.clickOnGeographyDDButton();

		selectValueFromDropDown(Geography);

		promo.insertUserInSearchField(UserSearch);

		promo.clickOnSearchIcon();

		promo.checkSearchUserList();

		promo.clickOnSubmitUserButton();

		promo.clickOnProceedButton();
	}

	public void modifyPromotionalTransferRule() {

		promo.clickOnPromotionalTransferRuleLink();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

		selectFilterValueOnPromotionalTransferPage();

		promo.checkSearchResultLoadedSuccessfully();

		promo.clickOnEditIcon();

		promo.clickOnModifyStatusDDButton();

		selectValueFromDropDown(ModifiedStatus);

		promo.clickOnModifyCardGroupSetDDbutton();

		selectValueFromDropDown(ModifiedCardGroupSet);

		promo.clickOnSaveRuleButton();

		promo.checkConfirmationWindowLoadedSuccessfully();

		promo.acceptConfirmation();

		promo.checkSuccessWindow();

		promo.clickOnDoneButton();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();
	}
	
	public void deletePromotionalTransferRule() {
		
		promo.clickOnPromotionalTransferRuleLink();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

		selectFilterValueOnPromotionalTransferPage();

		promo.checkSearchResultLoadedSuccessfully();
		
		promo.clickOnDeleteIcon();
		
		promo.checkConfirmationWindowLoadedSuccessfully();

		promo.acceptConfirmation();

		promo.checkSuccessWindow();

		promo.clickOnDoneButton();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();
	}

	public void performAddModifyDelete_PromotionalTransferRule(String loginId, String password) {

		login.LoginAsUser(driver, loginId, password);

		CreatePromotionalTransferRule();

		modifyPromotionalTransferRule();
		
		deletePromotionalTransferRule();

	}
	
	public void ResetFilterOnPromotionalTransferRulePage(String loginId, String password) {
		
		login.LoginAsUser(driver, loginId, password);
		
		promo.clickOnPromotionalTransferRuleLink();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

		selectFilterValueOnPromotionalTransferPage();

		promo.checkSearchResultLoadedSuccessfully();
		
		promo.clickOnResetButton();
		
		List<WebElement> filterElements = driver.findElements(By.xpath("//h5//following::div[@class='ng-placeholder']"));
		
		int resetCount = 0;
		
		for(WebElement textvalue : filterElements) {
			String text = textvalue.getText();
			System.out.println("Current Filter values : "+text);
			if(text.equals("Select")) {
				resetCount++;
			}
		}
		
		if(resetCount == 4) {
			Log.info("Reset was successful");
		}else {
			Log.info("Reset was not successful");
		}
		
	}
	
public void ResetAddPromotionalTransferRulePage(String loginId, String password) {
		
		login.LoginAsUser(driver, loginId, password);
		
		promo.clickOnPromotionalTransferRuleLink();

		promo.checkPromotionalTransferRulePageLoadedSuccessFully();

		promo.clickOnAddPromotionalTransferDetails();

		promo.checkVisibilityOfAddPromotionalTransferRuleDetailsPageLoadedSuccessfully();

		promo.clickOnPromotionalLevelDD();

		selectValueFromDropDown(PromotionLevelUser);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		promo.clickOnAddDomainDD();

		selectValueFromDropDown(Domain);

		promo.clickOnAddCategoryDD();

		selectValueFromDropDown(Category);

		promo.clickOnAddGeographicalDomainTypeDD();

		selectValueFromDropDown(GeographicalDomainType);

		promo.clickOnGeographyDD();

		selectValueFromDropDown(Geography);

		promo.insertUserInSearchField(UserSearch);

		promo.clickOnSearchIcon();

		promo.checkSearchUserList();

		promo.clickOnSubmitUserButton();

		promo.clickOnReceiverDetailsTypeDD();

		selectValueFromDropDown(RecieverDetailsType);

		promo.clickOnServiceProviderGroupDD();

		selectValueFromDropDown(ServiceProviderGroup);

		promo.clickOnCardGroupSetStatusDD();

		selectValueFromDropDown(GroupSetStatus);

		promo.clickOnServiceTypeDD();

		selectValueFromDropDown(ServiceType);

		promo.clickOnSubServiceDD();

		selectValueFromDropDown(SubService);

		promo.clickOnCardGroupSetDD();

		selectValueFromDropDown(CardGroupSet);

		selectDateAndTimeRange();
		
		promo.clickOnResetButton();
		
		List<WebElement> DDelements = driver.findElements(By.xpath("//label[@for='level']//following::div[@class='ng-placeholder']"));
		
		List<WebElement> emptyDatePicker = driver.findElements(By.xpath("//primeng-datepicker[contains(@class,'ng-untouched')]"));
		
		int resetCount = 0;
		
		for(WebElement textvalue : DDelements) {
			String text = textvalue.getText();
			System.out.println("Current Filter values : "+text);
			if(text.equals("Select")) {
				resetCount++;
			}
		}
		
		for(WebElement dateinput : emptyDatePicker) {
			resetCount++;
		}
		
		
		
		if(resetCount == 14) {
			Log.info("Reset was successful");
		}else {
			Log.info("Reset was not successful");
		}
		
	}
}
