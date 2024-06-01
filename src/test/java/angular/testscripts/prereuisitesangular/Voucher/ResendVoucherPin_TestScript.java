package angular.testscripts.prereuisitesangular.Voucher;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import angular.feature.Voucher.VocuherPinResend_Feature;


@ModuleManager(name = Module.PREREQUISITE_Voucher_Pin_Resend)
public class ResendVoucherPin_TestScript extends BaseTest{
	
	
	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_01_Test_UserShouleBeAbleTo_ResendVoucherPin_UsingMobileNumber(String UserCat, String LoginId,
			String Password) {
		final String methodName = "A_01_Test_UserShouleBeAbleTo_ResendVoucherPin_UsingMobileNumber";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSResendVoucherPin01");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		VocuherPinResend_Feature voucherPinResend = new VocuherPinResend_Feature(driver);
		
		voucherPinResend.perfromVoucherPinResend_Via_MobileNumber(LoginId, Password);

		Assertion.completeAssertions();
	}
	
	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_02_Test_UserShouleBeAbleTo_ResendVoucherPin_UsingTransactionID(String UserCat, String LoginId,
			String Password) {
		final String methodName = "A_02_Test_UserShouleBeAbleTo_ResendVoucherPin_UsingTransactionID";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSResendVoucherPin02");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		VocuherPinResend_Feature voucherPinResend = new VocuherPinResend_Feature(driver);
		
		voucherPinResend.perfromVoucherPinResend_Via_TransactionID(LoginId, Password);

		Assertion.completeAssertions();
	}
	
	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_03_Test_UserShould_ReceiveErrorMessage_UponProceedingWithout_MobileNumber(String UserCat, String LoginId,
			String Password) {
		final String methodName = "A_03_Test_UserShould_ReceiveErrorMessage_UponProceedingWithout_MobileNumber";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSResendVoucherPin03");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		VocuherPinResend_Feature voucherPinResend = new VocuherPinResend_Feature(driver);
		
		voucherPinResend.errorMessageUponProceedingWithoutMobileNumber(LoginId, Password);

		Assertion.completeAssertions();
	}
	
	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_04_Test_UserShould_ReceiveErrorMessage_UponProceedingWithout_TransactionId(String UserCat, String LoginId,
			String Password) {
		final String methodName = "A_04_Test_UserShould_ReceiveErrorMessage_UponProceedingWithout_TransactionId";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSResendVoucherPin04");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		VocuherPinResend_Feature voucherPinResend = new VocuherPinResend_Feature(driver);
		
		voucherPinResend.errorMessageUponProceedingWithoutTransactionId(LoginId, Password);

		Assertion.completeAssertions();
	}
	
	
	
	/* ----------------------- D A T A P R O V I D E R ---------------------- */
	/*
	 * -----------------------------------------------------------------------------
	 * --------------------
	 */

	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet of
		 * Categories for which C2C Withdraw is allowed
		 */

		Object[][] Data = new Object[1][3];

		String[] opCatName = { "Super Channel Admin" };

		for (int j = 0, k = 0; j < opCatName.length; j++) {

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			int excelRowSize = ExcelUtility.getRowCount();
			String UserLoginId = null;
			String UserPassword = null;
			String UserCat = null;
			for (int i = 1; i <= excelRowSize; i++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(opCatName[j])) {
					UserCat = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
					UserLoginId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
					UserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
					break;
				}
			}

			for (int excelCounter = 0; excelCounter < 1; excelCounter++) {

				Data[k][0] = UserCat;
				Data[k][1] = UserLoginId;
				Data[k][2] = UserPassword;

				Log.info("Data" + Data[k][0]);
				Log.info("Data" + Data[k][1]);
				Log.info("Data" + Data[k][2]);
				Log.info("Value of k" + k);
				k++;

			}

		}

		return Data;
	}
	/*
	 * -----------------------------------------------------------------------------
	 * -------------------
	 */

}
