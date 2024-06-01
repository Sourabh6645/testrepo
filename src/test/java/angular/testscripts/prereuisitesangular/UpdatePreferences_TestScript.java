package angular.testscripts.prereuisitesangular;

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

import angular.feature.PreferencesFeature;

@ModuleManager(name = Module.PREREQUISITE_Preferences)
public class UpdatePreferences_TestScript extends BaseTest{
	
	
	@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_UserShouleBe_AbleTo_UpdateAndDelete_Preferences(String UserCat, String LoginId,
			String Password, String prefType) {
		final String methodName = "A_01_Test_UserShouleBe_AbleTo_UpdateAndDelete_Preferences";
		Log.startTestCase(methodName, UserCat,LoginId,Password,prefType);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSPreferences01");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat,prefType))
				.assignCategory(TestCategory.PREREQUISITE);

		PreferencesFeature preferenc = new PreferencesFeature(driver);
		Log.info("Voucher Category Being Passed : " + prefType);
		
		preferenc.performModifyAndDeletePreferencesData(LoginId,Password,prefType);
		

		Assertion.completeAssertions();
	}
	
	
	@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_UserPreferences_CancelAndReset(String UserCat, String LoginId,
			String Password, String prefType) {
		final String methodName = "A_01_Test_UserPreferences_CancelAndReset";
		Log.startTestCase(methodName, UserCat,LoginId,Password,prefType);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PreTUPSPreferences02");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), UserCat,prefType))
				.assignCategory(TestCategory.PREREQUISITE);

		PreferencesFeature preferenc = new PreferencesFeature(driver);
		Log.info("Voucher Category Being Passed : " + prefType);
		
		preferenc.performCancelAndResetPreferencesData(LoginId,Password,prefType);
		

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

		Object[][] Data = new Object[3][4];

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

			String[] voucherType = { "NetworkPreferences", "ServiceClassPreferences", "ControlPreferences" };
			for (int excelCounter = 0; excelCounter < voucherType.length; excelCounter++) {

				Data[k][0] = UserCat;
				Data[k][1] = UserLoginId;
				Data[k][2] = UserPassword;
				Data[k][3] = voucherType[excelCounter];

				Log.info("Data" + Data[k][0]);
				Log.info("Data" + Data[k][1]);
				Log.info("Data" + Data[k][2]);
				Log.info("Data" + Data[k][3]);
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
