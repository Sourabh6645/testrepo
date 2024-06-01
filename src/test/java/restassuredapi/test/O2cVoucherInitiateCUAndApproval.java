package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.o2cVoucherInitiate.O2cVoucherInitiateApi;
import restassuredapi.api.o2cvoucherapprovalapi.O2CVoucherApprovalAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2CVoucherApprovalRequestPojo.Datum;
import restassuredapi.pojo.o2CVoucherApprovalRequestPojo.O2CVoucherApprovalRequestPojo;
import restassuredapi.pojo.o2CVoucherApprovalRequestPojo.PaymentDetails;
import restassuredapi.pojo.o2CVoucherApprovalRequestPojo.VoucherDetails;
import restassuredapi.pojo.o2CVoucherApprovalResponsePojo.O2CVoucherApprovalResponsePojo;
import restassuredapi.pojo.o2cvoucherinitiaterequestpojo.Data;
import restassuredapi.pojo.o2cvoucherinitiaterequestpojo.O2cVoucherInitiateRequestPojo;
import restassuredapi.pojo.o2cvoucherinitiaterequestpojo.PaymentDetail;
import restassuredapi.pojo.o2cvoucherinitiaterequestpojo.VoucherDetail;
import restassuredapi.pojo.o2cvoucherinitiateresponsepojo.O2cVoucherInitiateResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.testscripts.sit.SIT_VMS;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.O2C_VOUCHER_INI)
public class O2cVoucherInitiateCUAndApproval extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	O2cVoucherInitiateRequestPojo o2cVoucherInitiateRequestPojo = new O2cVoucherInitiateRequestPojo();
	O2cVoucherInitiateResponsePojo o2cVoucherInitiateResponsePojo = new O2cVoucherInitiateResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	O2CVoucherApprovalRequestPojo o2CVoucherApprovalRequestPojo = new O2CVoucherApprovalRequestPojo();
	O2CVoucherApprovalResponsePojo o2CVoucherApprovalResponsePojo= new O2CVoucherApprovalResponsePojo();
	

	Data data = new Data();
	Datum o2cVoucherAprvlData = new Datum();
	
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        String O2CVoucherInititateCode = _masterVO.getProperty("O2CVoucherInitiate");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        
        ArrayList<String> opUserData =new ArrayList<String>();
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));
        

     
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which O2C transfer is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(O2CVoucherInititateCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
            }
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which O2C transfer is allowed
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int chnlCount = ExcelUtility.getRowCount();
        int userCounter = 0;
        for (int i = 1; i <= chnlCount; i++) {
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                userCounter++;
            }
        }

        /*
         * Store required data of 'O2C transfer allowed category' users in Object
         */
        Object[][] Data = new Object[userCounter][6];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                j++;
            }
        }

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		rowCount = ExcelUtility.getRowCount();
		ArrayList<ArrayList<String>> voucherData= new ArrayList<ArrayList<String>>();
		for (int i = 1; i <= rowCount; i++) {
				ArrayList<String> voucherTempData =new ArrayList<>();
				
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				voucherData.add(voucherTempData);		
		}

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = voucherData.size();
        Object[][] o2cData = new Object[countTotal][14];
        for (int i = 0; i < countTotal; i++) {
        	
        	int counter_j=0;
        	for(int j=0;j<opUserData.size();j++) {
        		o2cData[i][counter_j++]=opUserData.get(j);
        	}
        	
        	for(int j=0;j<Data[0].length;j++) {
        		o2cData[i][counter_j++]=Data[0][j];
        	}
        	
        	for(int j=0;j<voucherData.get(i).size();j++) {
        		o2cData[i][counter_j++]=voucherData.get(i).get(j);
        	}
       
        }
        
        return o2cData;
        
    }
	 
	 
	 Map<String, Object> headerMap = new HashMap<String, Object>();
		public void setHeaders() {
			headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
			headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
			headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
			headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
			headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
			headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
			headerMap.put("scope", _masterVO.getProperty("scope"));
			headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
		}
		
		 protected static String accessToken;
		 public void setupAuth(String data1, String data2) {
				oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
				oAuthenticationRequestPojo.setIdentifierValue(data1);
				oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
			}
		 
		    
		 public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
			{
				
				final String methodName = "Test_OAuthenticationTest";
				Log.startTestCase(methodName);
				
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
				currentNode.assignCategory("REST");

				setHeaders();
				setupAuth(data1,data2);
				OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
				oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
				oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
				oAuthenticationAPI.setExpectedStatusCode(200);
				oAuthenticationAPI.perform();
				oAuthenticationResponsePojo = oAuthenticationAPI
						.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
				long statusCode = oAuthenticationResponsePojo.getStatus();

				accessToken = oAuthenticationResponsePojo.getToken();
				Assert.assertEquals(statusCode, 200);
				Assertion.assertEquals(Long.toString(statusCode), "200");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
    
	
	
	public void setupData(String pin,String voucherType,String activeProfile,String mrp,String type) {
		
		String types[] =SIT_VMS.getAllowedVoucherTypesForScreen(PretupsI.O2C);
		List<String> al = Arrays.asList(types);
		if(al.contains(type)) {
			List<VoucherDetail> VoucherDetailsList = new ArrayList<VoucherDetail>();
			VoucherDetail voucherDetail = new VoucherDetail();
			voucherDetail.setVoucherType(voucherType);
			voucherDetail.setDenomination(mrp);
			voucherDetail.setQuantity("1");
			
			String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
			String voucherSegment=DBHandler.AccessHandler.getVoucherSegment(productID);
			voucherDetail.setVouchersegment(voucherSegment);
			VoucherDetailsList.add(voucherDetail);
			data.setVoucherDetails(VoucherDetailsList);
			
			List<PaymentDetail> paymentdetailsList  = new ArrayList<PaymentDetail>();
			PaymentDetail paymentDetails = new PaymentDetail();
			paymentDetails.setPaymentinstnumber(randStr.randomNumeric(5));
			paymentDetails.setPaymentdate(currentDate);
			paymentDetails.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
			paymentDetails.setPaymentgatewaytype("NA");
			paymentdetailsList.add(paymentDetails);
			data.setPaymentDetails(paymentdetailsList);
			
			
			data.setRefnumber("");
			data.setLanguage(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
			data.setRemarks(new RandomGeneration().randomAlphabets(10));
			
//			String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(opCategoryName);
//			
//			if(pinAllowed.equals("Y")){
//				data.setPin(pin);
//			}
			data.setPin(pin);
			o2cVoucherInitiateRequestPojo.setData(data);
		}
		else {
			Assertion.assertSkip("Not a valid case for this scenario");
		}
		
	}
	
	public void setupO2cApproval(String msisdn,int denomination, String activeProfile,String txnId, String voucherType,String voucherSegment,String pin, String type) {
		 
		 List<PaymentDetails> paymentdetails = new ArrayList<>();
		 PaymentDetails paymentdetail = new PaymentDetails();
		 paymentdetail.setPaymentinstnumber(Integer.parseInt(new RandomGeneration().randomNumeric(5)));
		 paymentdetail.setPaymentdate(currentDate);
		 paymentdetail.setPaymentgatewaytype("");
		 paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		 paymentdetails.add(paymentdetail);
		 o2cVoucherAprvlData.setPaymentDetails(paymentdetails);
		 
		 List<VoucherDetails> VoucherDetailsList = new ArrayList<VoucherDetails>();
		 VoucherDetails voucherDetail = new VoucherDetails();
		 
		 String status;
			if(type.equals("D") || type.equals("DT")) {
				status = "GE";
				}
				else {
				status = "WH";
				}
			String productID=DBHandler.AccessHandler.fetchProductID(activeProfile);
			String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
			if(fromSerialNumber==null)
				Assertion.assertSkip("Voucher Serial Number not Found");
			
			String toSerialNumber = fromSerialNumber;
			
		voucherDetail.setFromSerialNo(fromSerialNumber);
		voucherDetail.setToSerialNo(toSerialNumber);
		 voucherDetail.setDenomination(denomination);
		 voucherDetail.setFromSerialNo(fromSerialNumber);
		 voucherDetail.setToSerialNo(toSerialNumber);
		 voucherDetail.setReqQuantity(_masterVO.getProperty("voucherQty"));
		 
		 voucherDetail.setVoucherProfileId(productID);
		 
		 voucherDetail.setVoucherType(voucherType);
		 voucherDetail.setVouchersegment(voucherSegment);
		 VoucherDetailsList.add(voucherDetail);
		 o2cVoucherAprvlData.setVoucherDetails(VoucherDetailsList);
		 
		 o2cVoucherAprvlData.setApprovalLevel(_masterVO.getProperty("voucherAprvLvl1"));
		 o2cVoucherAprvlData.setExternalTxnDate(currentDate);
		 o2cVoucherAprvlData.setExternalTxnNum(new RandomGeneration().randomNumberWithoutZero(6));
		 o2cVoucherAprvlData.setRefrenceNumber(new RandomGeneration().randomNumeric(9));
		 o2cVoucherAprvlData.setRemarks(new RandomGeneration().randomAlphabets(10));
		 o2cVoucherAprvlData.setStatus(_masterVO.getProperty("voucherApprove"));
		 
		 String userId =DBHandler.AccessHandler.getUserIdFromMsisdn(msisdn);
		 o2cVoucherAprvlData.setToUserId(userId);
		 
		 o2cVoucherAprvlData.setTransactionId(txnId);
		 o2cVoucherAprvlData.setTransferDate(currentDate);
		 
		 List<Datum> o2cVoucherAprvlDataList = new ArrayList<Datum>();
		 o2cVoucherAprvlDataList.add(o2cVoucherAprvlData);
			
		 o2CVoucherApprovalRequestPojo.setData(o2cVoucherAprvlDataList);
		 
	 }
	
	public O2CVoucherApprovalResponsePojo performO2CVoucherApproval() throws IOException {
		 
		O2CVoucherApprovalAPI o2CVoucherApprovalAPI = new O2CVoucherApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherApprovalAPI.addBodyParam(o2CVoucherApprovalRequestPojo);
		o2CVoucherApprovalAPI.perform();
		o2CVoucherApprovalResponsePojo = o2CVoucherApprovalAPI
					.getAPIResponseAsPOJO(O2CVoucherApprovalResponsePojo.class);
		 
		 return o2CVoucherApprovalResponsePojo;
	 }

	// Successful data with valid data.
	
	    
	    @Test(dataProvider = "userData")
	    @TestManager(TestKey="PRETUPS-9881")
		public void A_01_Test_success(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "Test_O2CVoucherInitiateAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(200);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 
 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    o2CVoucherApprovalRequestPojo.getData().get(0).setApprovalLevel(_masterVO.getProperty("voucherAprvLvl2"));;
		    		 response = performO2CVoucherApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CVoucherApprovalRequestPojo.getData().get(0).setApprovalLevel(_masterVO.getProperty("voucherAprvLvl3"));
		    	        response = performO2CVoucherApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}


	    
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-9882")
	public void A_02_Test_remarks_blank_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
		final String methodName = "A_02_Test_remarks_blank_VoucherInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(chLoginId, chPassword,chCategoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(chMsisdn, chPin,chCategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI2");
		moduleCode = CaseMaster.getModuleCode();
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
		currentNode.assignCategory("REST");
		setupData(chPin,vouchertype,activeProfile,mrp,type);
		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
		o2cDetailsData.setRemarks("");
		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
		o2CVoucherInitiateAPI.setExpectedStatusCode(241132);
		o2CVoucherInitiateAPI.perform();
		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(241132, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "241132");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-9883")
	public void A_03_Test_blank_paymentdate_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
		final String methodName = "A_03_Test_blank_paymentdate_VoucherInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(chLoginId, chPassword,chCategoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(chMsisdn, chPin,chCategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
		currentNode.assignCategory("REST");
		setupData(chPin,vouchertype,activeProfile,mrp,type);
		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymentdate("");
		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
		o2CVoucherInitiateAPI.setExpectedStatusCode(1004093);
		o2CVoucherInitiateAPI.perform();
		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(1004093, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "1004093");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-9884")
	public void A_04_Test_blank_paymentinsType_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
		final String methodName = "A_04_Test_blank_paymentinsType_VoucherInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(chLoginId, chPassword,chCategoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(chMsisdn, chPin,chCategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
		currentNode.assignCategory("REST");
		setupData(chPin,vouchertype,activeProfile,mrp,type);
		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymenttype("");
		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
		o2CVoucherInitiateAPI.setExpectedStatusCode(8148);
		o2CVoucherInitiateAPI.perform();
		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(8148, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "8148");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-9885")
	public void A_05_Test_blank_paymentinsNum_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
		final String methodName = "A_05_Test_blank_paymentinsNum_VoucherInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(chLoginId, chPassword,chCategoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(chMsisdn, chPin,chCategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
		currentNode.assignCategory("REST");
		setupData(chPin,vouchertype,activeProfile,mrp,type);
		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
		o2cDetailsData.getPaymentDetails().get(0).setPaymentinstnumber("");
		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
		o2CVoucherInitiateAPI.setExpectedStatusCode(11100);
		o2CVoucherInitiateAPI.perform();
		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
		int errorCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode());
		Assert.assertEquals(11100, errorCode);
		Assertion.assertEquals(Integer.toString(errorCode), "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-9886")
	public void A_06_Test_multiple_error_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
		final String methodName = "A_06_Test_multiple_error_VoucherInitiate";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(chLoginId, chPassword,chCategoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(chMsisdn, chPin,chCategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
		currentNode.assignCategory("REST");
		setupData(chPin,vouchertype,activeProfile,mrp,type);
		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
		o2cDetailsData.getVoucherDetails().get(0).setQuantity("");
		o2cDetailsData.getVoucherDetails().get(0).setVoucherType("");;
		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
		o2CVoucherInitiateAPI.setExpectedStatusCode(400);
		o2CVoucherInitiateAPI.perform();
		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
		int arrError[] = {241130,241134};
		for(int i=0;i<arrError.length;i++){
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(i).getErrorCode());
			Assert.assertEquals(arrError[i], statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), Integer.toString(arrError[i]));
			Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
	}

//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-9887")
//	public void A_07_Test_multipleRow_error_VoucherInitiate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
//		final String methodName = "A_07_Test_multipleRow_error_VoucherInitiate";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(chLoginId, chPassword,chCategoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(chMsisdn, chPin,chCategoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI7");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),chCategoryName));
//		currentNode.assignCategory("REST");
//		setupData(chPin,vouchertype,activeProfile,mrp,type);
//		VoucherDetail voucherDetail = new VoucherDetail();
//		voucherDetail.setVoucherType("digital");
//		voucherDetail.setVouchersegment("");
//		voucherDetail.setDenomination("451");
//		voucherDetail.setQuantity("1");
//		Data o2cDetailsData=o2cVoucherInitiateRequestPojo.getData();
//		o2cDetailsData.getVoucherDetails().get(0).setQuantity("");
//		o2cDetailsData.getVoucherDetails().get(0).setVoucherType("");
//		o2cDetailsData.getVoucherDetails().add(voucherDetail);
//		O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
//		o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
//		o2CVoucherInitiateAPI.setExpectedStatusCode(400);
//		o2CVoucherInitiateAPI.perform();
//		o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
//		int arrError[] = {241130,241131};
//		for(int i=0;i<arrError.length;i++){
//			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getErrorMap().getRowErrorMsgLists().get(i).getMasterErrorList().get(0).getErrorCode());
//			Assert.assertEquals(arrError[i], statusCode);
//			Assertion.assertEquals(Integer.toString(statusCode), Integer.toString(arrError[i]));
//			Assertion.completeAssertions();
//		}
//		Log.endTestCase(methodName);
//	}
	
	 @Test(dataProvider = "userData")
		public void B_02_Test_blank_userId_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_02_Test_blank_userId_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setToUserId("");
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(241175, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241175");
			
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
		     
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	 
	  @Test(dataProvider = "userData")
		public void B_03_Test_blank_extTxnNumber_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_03_Test_blank_extTxnNumber_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setToUserId("");
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(241175, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241175");
			
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	  
	  @Test(dataProvider = "userData")
		public void B_04_Test_invalid_extTxnDate_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_04_invalid_blank_extTxnDate_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA6");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
			    Date dateobj = new Date();
			    String date=df.format(dateobj);
			    o2CVoucherApprovalRequestPojo.getData().get(0).setExternalTxnDate(date);

			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(6121, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "6121");
		     
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
				 
		}
	  
	  @Test(dataProvider = "userData")
	 	  public void B_05_Test_blank_extTxnDate_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_05_Test_blank_extTxnDate_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA7");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setExternalTxnDate("");
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(6120, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "6120");
		     
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
				 
		}
	  @Test(dataProvider = "userData")
	  public void B_06_Test_blank_transferDate_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_06_Test_blank_transferDate_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA8");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setTransferDate("");
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(241177, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241177");
		     
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
				 
		}
	  
	  @Test(dataProvider = "userData")
	  public void B_07_Test_blank_txn_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_07_Test_blank_transferDate_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA9");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setTransactionId("");
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(8136, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "8136");
		     
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
				 
		}
	  
	  @Test(dataProvider = "userData")
	  public void B_09_Test_invalid_aprvl_Level_VoucherApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) throws Exception {
			final String methodName = "B_09_Test_invalid_aprvl_Level_VoucherApproval";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(chLoginId, chPassword,chCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(chMsisdn, chPin,chCategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CVOUCHERINI1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chPin,vouchertype,activeProfile,mrp,type);
			O2cVoucherInitiateApi o2CVoucherInitiateAPI = new O2cVoucherInitiateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CVoucherInitiateAPI.addBodyParam(o2cVoucherInitiateRequestPojo);
			o2CVoucherInitiateAPI.setExpectedStatusCode(400);
			o2CVoucherInitiateAPI.perform();
			o2cVoucherInitiateResponsePojo = o2CVoucherInitiateAPI
					.getAPIResponseAsPOJO(O2cVoucherInitiateResponsePojo.class);
			
			int statusCode = Integer.parseInt(o2cVoucherInitiateResponsePojo.getStatus());
			String txnId = o2cVoucherInitiateResponsePojo.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			
			//Channel Admin OAuth
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			
			//Voucher Approval
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA10");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			
			String voucherSegment =o2cVoucherInitiateRequestPojo.getData().getVoucherDetails().get(0).getVouchersegment();
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setApprovalLevel(new RandomGeneration().randomAlphabets(4));
			 
		    O2CVoucherApprovalResponsePojo response = performO2CVoucherApproval();
		    String errorcode = response.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		 
		    Assert.assertEquals(241159, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241159");
		     
			//Voucher Reject
			CaseMaster = _masterVO.getCaseMasterByID("O2CVA3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			
			setupO2cApproval(chMsisdn, Integer.parseInt(mrp), activeProfile,txnId,vouchertype,voucherSegment,opPin,type);
			o2CVoucherApprovalRequestPojo.getData().get(0).setStatus(_masterVO.getProperty("voucherReject"));
			
		    response = performO2CVoucherApproval();
		    String status = response.getStatus();
		    
   		 	Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertEquals(status, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
				 
		}
	
}