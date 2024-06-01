package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.batcho2ctransfer.BatchO2cTransferApi;
import restassuredapi.api.o2cpurchaseorwithdrawtemplateapi.O2cPurchaseOrWithdrawTemplateApi;
import restassuredapi.api.o2cpurchaseorwithdrawuserlistapi.O2cPurchaseOrWithdrawUserListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.batcho2ctrfrequestpojo.BatchO2CTransferRequestVO;
import restassuredapi.pojo.batcho2ctrfrequestpojo.Data;
import restassuredapi.pojo.batcho2ctrfresponsepojo.BatchO2CTransferResponseVO;
import restassuredapi.pojo.o2cpurchaseorwithdrawtemplateresponsepojo.O2cPurchaseOrWithdrawTemplateResponsePojo;
import restassuredapi.pojo.o2cpurchaseorwithdrawuserlistresponsepojo.O2cPurchaseOrWithdrawUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.BATCH_O2C_TRF)
public class BatchO2CTransfer extends BaseTest 
	{
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	Date tomorrow = DateUtils.addDays(new Date(), 3);
	String tomorrowdate = df.format(tomorrow);
	Date prior = DateUtils.addDays(new Date(), -1);
	String PriorDate = df.format(prior);
	String currentDate = df.format(dateobj);
	
	static String moduleCode;
	O2cPurchaseOrWithdrawUserListResponsePojo o2CBatchUsersListDownloadResponsePojo = new O2cPurchaseOrWithdrawUserListResponsePojo();
	BatchO2CTransferRequestVO batchO2CTransferRequestVO = new BatchO2CTransferRequestVO();
	BatchO2CTransferResponseVO batchO2CTransferResponseVO = new BatchO2CTransferResponseVO();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	O2cPurchaseOrWithdrawTemplateResponsePojo o2cPurchaseOrWithdrawTemplateResponsePojo = new O2cPurchaseOrWithdrawTemplateResponsePojo();
	Data data = new Data();
	RandomGeneration rnd = new RandomGeneration();
	//Data data = new Data();
	Login login = new Login();
	String searchCategoryCode = null;
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		searchCategoryCode = _masterVO.getProperty("categoryCode");
	    String MasterSheetPath = _masterVO.getProperty("DataProvider");
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	    int rowCount = ExcelUtility.getRowCount();
	    int user = 0;
	    String GeoDomainCode = null;
	    for (int i = 1; i <= rowCount; i++) {
	        if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
	            user++;
	            GeoDomainCode = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
	        }
	    }
	    String GeoDomainName = null;
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
	    int rowCount1 = ExcelUtility.getRowCount();
	    for (int i = 1; i <= rowCount1; i++) {
	        if (ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i).equals(GeoDomainCode)) {
	            GeoDomainName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, i);
	        }
	    }
	    String domainCode = null;
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
	    int rowCount2 = ExcelUtility.getRowCount();
	    for (int i = 1; i <= rowCount2; i++) {
	        if (ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i).equals(GeoDomainName))
	            domainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i).toUpperCase();
	    }
	   
	//    ArrayList<String> opUserData =new ArrayList<String>();
	//    Map<String, String> userInfo = UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.O2C_TRANSFER_REVAMP,searchCategoryCode);
	//    opUserData.add(userInfo.get("CATEGORY_NAME"));
	//    opUserData.add(userInfo.get("LOGIN_ID"));
	//    opUserData.add(userInfo.get("MSISDN"));
	//    opUserData.add(userInfo.get("USER_NAME"));
	    Object[][] Data = new Object[user][12];
	    int j = 0;
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	    for (int i = 1; i <= rowCount; i++) {
	        if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
	            Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	            Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
	            Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	            Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	            Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
	            Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	            Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	            Data[j][7] = domainCode;
	//            Data[j][8] = opUserData.get(0);
	//            Data[j][9] = opUserData.get(1);
	//            Data[j][10] = opUserData.get(2);
	//            Data[j][11] = opUserData.get(3);
	           
	            j++;
	        }
	    }
	    // Getting Channel User category hierarchy
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USER_CATEGORY_SHEET);
	    int rowCount3 = ExcelUtility.getRowCount();
	    int k=0;
	    for(int i=1, dataLen =1; (i<= rowCount3 && dataLen<= Data.length); i++, dataLen++) {
	    	Data[k][8] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i);
	    	Data[k][9] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	    	k++;
	    }
	    
	    // Getting Product details
	    ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
	    int rowCount4 = ExcelUtility.getRowCount();
	    int l=0;
	    for(int i=1, dataLen =1; (i<= rowCount4 && dataLen<= Data.length); i++, dataLen++) {
	    	Data[l][10] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
	    	l++;
	    }
	    int w = 0;
	    for(int i=1; i<= Data.length; i++) {
	    	Data[w][11] = _masterVO.getProperty("paymentInstrumentTypeCash"); //talk to Chetan
	    	w++;
	    }
	    return Data;
	}
	
	
	public Map<String,String> downloadc2cBatchTemplate(String purchaseOrWithdraw) throws IOException{
		
		O2cPurchaseOrWithdrawTemplateApi o2cPurchaseOrWithdrawTemplateApi = new O2cPurchaseOrWithdrawTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cPurchaseOrWithdrawTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2cPurchaseOrWithdrawTemplateApi.setPurchaseOrWithdraw(purchaseOrWithdraw);
		o2cPurchaseOrWithdrawTemplateApi.setExpectedStatusCode(200);
		o2cPurchaseOrWithdrawTemplateApi.perform();
		
		o2cPurchaseOrWithdrawTemplateResponsePojo = o2cPurchaseOrWithdrawTemplateApi
							.getAPIResponseAsPOJO(O2cPurchaseOrWithdrawTemplateResponsePojo.class);
	
		Map<String,String> response = new HashMap<String,String>();
		response.put("fileName", o2cPurchaseOrWithdrawTemplateResponsePojo.getFileName().toString());
		response.put("fileType",o2cPurchaseOrWithdrawTemplateResponsePojo.getFileType().toString());
		response.put("fileAttachment", o2cPurchaseOrWithdrawTemplateResponsePojo.getFileattachment().toString());
		
		return response;
		
	}
	public String genExcelData(String categoryName,String productCode,String domainCode,String geoDomainCode,String purchaseOrWithdraw,String paymentMode) throws IOException
	{
		O2cPurchaseOrWithdrawUserListApi c2CBatchUsersListDownloadAPI = new O2cPurchaseOrWithdrawUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	
		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBatchUsersListDownloadAPI.setCategoryCode(categoryName);
		c2CBatchUsersListDownloadAPI.setDomainCode(domainCode);
		c2CBatchUsersListDownloadAPI.setGeoDomainCode(geoDomainCode);
		c2CBatchUsersListDownloadAPI.setProductCode(productCode);
		c2CBatchUsersListDownloadAPI.setPurchaseOrWithdraw(purchaseOrWithdraw);
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		
		o2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(O2cPurchaseOrWithdrawUserListResponsePojo.class);
		
	
		String fileName= o2CBatchUsersListDownloadResponsePojo.getFileName().toString();
		String fileAttachment= o2CBatchUsersListDownloadResponsePojo.getFileattachment().toString();
		
		String dirPath=_masterVO.getProperty("O2CBatchUserList");
		String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
		ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
		
		RandomGeneration randomGeneration = new RandomGeneration();
		
	
		for(int a=0;a<ExcelUtility.getRowCount();a++) {
			for(int b=0;b<10;b++) {
				System.out.print(ExcelUtility.getCellDataHSSF(a,b)+" ");
			}
			System.out.println();
		}
		int noOfRows = ExcelUtility.getRowCount();
	
		int counter_msisdn;
	
		String[][] arr = new String[noOfRows][3] ;
		for(int i=1; i<noOfRows; i++)
		{
			counter_msisdn = 0 ;
			arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;
			if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
			arr[i][1] = ExcelUtility.getCellDataHSSF(i,6) ;
			if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_msisdn+=1 ;}
			
			if(counter_msisdn ==2)
			{
				Log.info("\n\n COUNTER_MSISDN = "+counter_msisdn) ;
				arr[0][0] = arr[i][0] ;
				arr[0][1] = arr[i][1] ;
				Log.info(" UNEMPTY STORED IN ARRAY TO WRITE IN EXCEL");
				Log.info("arr[0][0] "+arr[0][0]) ;
				break ;
			}
		}
	
		String msisdn= arr[1][0];
		String qty= randomGeneration.randomNumberWithoutZero(2);
		String remarks =randomGeneration.randomAlphabets(15);
		String extTxnDate = "";
		String paymentType = paymentMode;
		String excelData=msisdn + "," + extTxnDate + "," + paymentType + "," + qty + "," + remarks;
		
		return excelData;
	}
	
	public void setupData(String pin,String categoryName,String productCode,String domainCode,String geoDomainCode,String purchaseOrWithdraw,String paymentMode) throws IOException {
	
	List<String> excelData =new ArrayList<String>();
	excelData.add(genExcelData(categoryName,productCode,domainCode,geoDomainCode,purchaseOrWithdraw,paymentMode));
	Map<String,String> response = downloadc2cBatchTemplate(purchaseOrWithdraw);
	String fileName= response.get("fileName");
	String fileType= response.get("fileType");
	String fileAttachment= response.get("fileAttachment");
	
	String dirPath=_masterVO.getProperty("C2CBulkRecharge");
	String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
	ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
	String[] row = excelData.get(0).split(",");
	ExcelUtility.setCellDataXLS(row[0],1,0);
	ExcelUtility.setCellDataXLS("",1,1);
	ExcelUtility.setCellDataXLS(row[1],1,2);
	ExcelUtility.setCellDataXLS(row[2],1,3);
	ExcelUtility.setCellDataXLS(row[3],1,5);
	//for(int i=0;i<excelData.size();i++) {
	//	String[] row = excelData.get(i).split(",");
	//	for(int j=0;j<row.length;j++) {
	//		ExcelUtility.setCellDataXLS(row[j], 2+i,j);
	//	}	
	//}
	String base64file = ExcelUtility.excelToBase64(filepath);
	Log.info("Base 64 file with data:");
	Log.info(base64file);
	data.setBatchName(new RandomGeneration().randomAlphaNumeric(8));
	data.setFileName(fileName);
	data.setFileType(fileType);
	data.setFileAttachment(base64file);
	data.setLanguage1("english");
	data.setLanguage2("spanish");
	data.setPin(pin);
	data.setGeographicalDomain(geoDomainCode);
	data.setUsercategory(categoryName);
	data.setChannelDomain(domainCode);
	data.setProduct(productCode);
	batchO2CTransferRequestVO.setData(data);
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
	
	public void setupAuth(String data1, String data2) {
	oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
	oAuthenticationRequestPojo.setIdentifierValue(data1);
	oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}
	
	
	protected static String accessToken;
	
	
	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
	//if(accessToken==null) {
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
	
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode,String paymentMode) throws Exception {
	final String methodName = "A_01_Test_success";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBTRF1");
	moduleCode = CaseMaster.getModuleCode();
	
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, _masterVO.getProperty("purchase").toUpperCase(),paymentMode);
	
	BatchO2cTransferApi batchO2cTransferApi = new BatchO2cTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	batchO2cTransferApi.setContentType(_masterVO.getProperty("contentType"));
	batchO2cTransferApi.addBodyParam(batchO2CTransferRequestVO);
	batchO2cTransferApi.setExpectedStatusCode(201);
	batchO2cTransferApi.perform();
	batchO2CTransferResponseVO = batchO2cTransferApi
			.getAPIResponseAsPOJO(BatchO2CTransferResponseVO.class);
	String status = batchO2CTransferResponseVO.getStatus();
	
	
	if(status == "200")
	Assert.assertEquals(200, status);
	Assertion.assertEquals(status, "200");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	
	}
	
	
	@Test(dataProvider = "userData")
	public void A_02_Invalid_Category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String paymentMode) throws Exception {
	final String methodName = "A_02_Invalid_Category";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBTRF2");
	moduleCode = CaseMaster.getModuleCode();
	
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, _masterVO.getProperty("purchase").toUpperCase(),paymentMode);
	
	BatchO2cTransferApi batchO2cTransferApi = new BatchO2cTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

	batchO2cTransferApi.setContentType(_masterVO.getProperty("contentType"));
	batchO2CTransferRequestVO.getData().setUsercategory(new RandomGeneration().randomAlphabets(5));
	batchO2cTransferApi.addBodyParam(batchO2CTransferRequestVO);
	batchO2cTransferApi.perform();
	batchO2CTransferResponseVO = batchO2cTransferApi
			.getAPIResponseAsPOJO(BatchO2CTransferResponseVO.class);
	String message = batchO2CTransferResponseVO.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	Assert.assertEquals(message,"Invalid category");
	Assertion.assertEquals(message, "Invalid category");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	
	}
	@Test(dataProvider = "userData")
	public void A_03_Invalid_Geography(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String paymentMode) throws Exception {
	final String methodName = "A_03_Invalid_Geography";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBTRF3");
	moduleCode = CaseMaster.getModuleCode();
	
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, _masterVO.getProperty("purchase").toUpperCase(),paymentMode);
	
	BatchO2cTransferApi batchO2cTransferApi = new BatchO2cTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	batchO2cTransferApi.setContentType(_masterVO.getProperty("contentType"));
	batchO2CTransferRequestVO.getData().setGeographicalDomain(new RandomGeneration().randomAlphabets(5));
	batchO2cTransferApi.addBodyParam(batchO2CTransferRequestVO);
	batchO2cTransferApi.perform();
	batchO2CTransferResponseVO = batchO2cTransferApi
			.getAPIResponseAsPOJO(BatchO2CTransferResponseVO.class);
	String message = batchO2CTransferResponseVO.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	Assert.assertEquals(message,"Invalid geography");
	Assertion.assertEquals(message, "Invalid geography");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	
	}
	
	
	@Test(dataProvider = "userData")
	public void A_04_Invalid_Domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String paymentMode) throws Exception {
	final String methodName = "A_04_Invalid_Domain";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBTRF4");
	moduleCode = CaseMaster.getModuleCode();
	
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, _masterVO.getProperty("purchase").toUpperCase(),paymentMode);
	
	BatchO2cTransferApi batchO2cTransferApi = new BatchO2cTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	batchO2cTransferApi.setContentType(_masterVO.getProperty("contentType"));
	batchO2CTransferRequestVO.getData().setChannelDomain("");
	batchO2cTransferApi.addBodyParam(batchO2CTransferRequestVO);
	batchO2cTransferApi.perform();
	batchO2CTransferResponseVO = batchO2cTransferApi
			.getAPIResponseAsPOJO(BatchO2CTransferResponseVO.class);
	String message = batchO2CTransferResponseVO.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	Assert.assertEquals(message,"Channel Domain can not be blank.");
	Assertion.assertEquals(message, "Channel Domain can not be blank.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	
	}
	
	@Test(dataProvider = "userData")
	public void A_05_BatchName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String paymentMode) throws Exception {
	final String methodName = "A_05_BatchName";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBTRF5");
	moduleCode = CaseMaster.getModuleCode();
	
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, _masterVO.getProperty("purchase").toUpperCase(),paymentMode);
	
	BatchO2cTransferApi batchO2cTransferApi = new BatchO2cTransferApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	batchO2cTransferApi.setContentType(_masterVO.getProperty("contentType"));
	batchO2CTransferRequestVO.getData().setBatchName("");
	batchO2cTransferApi.addBodyParam(batchO2CTransferRequestVO);
	batchO2cTransferApi.perform();
	batchO2CTransferResponseVO = batchO2cTransferApi
			.getAPIResponseAsPOJO(BatchO2CTransferResponseVO.class);
	String message = batchO2CTransferResponseVO.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	Assert.assertEquals(message,"Batch name is empty.");
	Assertion.assertEquals(message, "Batch name is empty.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	
	}

}

