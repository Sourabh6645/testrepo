package com.apicontrollers.extgw.LastTransactionDetails;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_LastXTransaction extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TCA_NegativewithBlankEXTNWCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT01");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.EXTNWCODE, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCB_Positive() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT02");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCC_PositivewithMSISDNandPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT03");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.LOGINID, "");
		apiData.put(lastTransactionAPI.PASSWORD, "");
		apiData.put(lastTransactionAPI.EXTCODE, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCD_PositivewitLoginIDandPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT04");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.MSISDN, "");
		apiData.put(lastTransactionAPI.PIN, "");
		apiData.put(lastTransactionAPI.EXTCODE, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCE_PositivewithEXTCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT05");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.MSISDN, "");
		apiData.put(lastTransactionAPI.PIN, "");
		apiData.put(lastTransactionAPI.LOGINID, "");
		apiData.put(lastTransactionAPI.PASSWORD, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCF_PositivewithDateBlank() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT06");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.DATE, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TCG_PositivewithEXTREFNMBlank() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT07");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.EXTREFNUM, "");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCH_Negative_InvalidPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT08");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.PIN,RandomGeneration.randomNumeric(4));
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCI_Negative_InvalidPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT09");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.PASSWORD,RandomGeneration.randomNumeric(4));
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCJ_Negative_InvalidExternalCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT10");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.EXTCODE,RandomGeneration.randomAlphaNumeric(4));
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCK_Negative_InvalidLoginID() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT11");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.LOGINID,RandomGeneration.randomAlphaNumeric(4));
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL_Negative_BlankPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT12");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.PASSWORD,"");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCM_Negative_BlankPin() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLXT13");
		EXTGW_LastXTransactionAPI lastTransactionAPI = new EXTGW_LastXTransactionAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LastTransactionDP.getAPIdata();
		apiData.put(lastTransactionAPI.PIN,"");
		String API = lastTransactionAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(lastTransactionAPI.REQSTATUS).toString(), CaseMaster.getErrorCode());
	}
	

}