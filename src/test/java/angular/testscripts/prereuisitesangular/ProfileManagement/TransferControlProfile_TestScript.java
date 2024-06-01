package angular.testscripts.prereuisitesangular.ProfileManagement;

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

import angular.feature.ProfileManagement.TransferControlProfile_Feature;

@ModuleManager(name = Module.PREREQUISITE_TransferControlProfile)
public class TransferControlProfile_TestScript extends BaseTest {

	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_01_Test_TransferControlProfile_UserIsAbleTO_Add_Modify_Delete_NewTransferControlProfile(
			String UserCat, String LoginId, String Password) {
		final String methodName = "A_01_Test_TransferControlProfile_UserIsAbleTO_Add_Modify_Delete_NewTransferControlProfile";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSTransferControlProfile01");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		TransferControlProfile_Feature tcpf = new TransferControlProfile_Feature(driver);

		tcpf.performAddModifyDeleteTransferControlProfile(LoginId, Password);

		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_02_Test_TransferControlProfile_ErrorMessageDisplayedUponNotProvidingDetails(String UserCat,
			String LoginId, String Password) {
		final String methodName = "A_02_Test_TransferControlProfile_ErrorMessageDisplayedUponNotProvidingDetails";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSTransferControlProfile02");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		TransferControlProfile_Feature tcpf = new TransferControlProfile_Feature(driver);

		tcpf.verifyThatProperErrorMessageIsDisplayedUponMissingMandatoryField(LoginId, Password);

		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	// @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE
	public void A_03_Test_TransferControlProfile_ErrorMessageDisplayedUponDailyValueIsGreaterThanMonthlyAndWeeklyValue(
			String UserCat, String LoginId, String Password) {
		final String methodName = "A_03_Test_TransferControlProfile_ErrorMessageDisplayedUponDailyValueIsGreaterThanMonthlyAndWeeklyValue";
		Log.startTestCase(methodName, UserCat, LoginId, Password);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSTransferControlProfile03");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat))
				.assignCategory(TestCategory.PREREQUISITE);

		TransferControlProfile_Feature tcpf = new TransferControlProfile_Feature(driver);

		tcpf.verifyDailyValueIsLessThanWeeklyValues(LoginId, Password);

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

		String[] opCatName = { "Network Admin" };

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