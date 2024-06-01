package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportssuser.ChannelUserOperatorUserRolesSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class ExternalUserRolesReportSpring {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	
	ChannelUserOperatorUserRolesSpring extUserRole;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	
	
	
	public ExternalUserRolesReportSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
	//	CU_O2CTransfer = new O2CTransferSubLink(driver);
		
		extUserRole = new ChannelUserOperatorUserRolesSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkExternaluserRoleReport(String userType,String domainCode, String... data){
		final String methodname = "checkExternaluserRoleReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.EXT_USER_REPRT,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickChannelReportsUser();
		
		extUserRole.clickExternalUsersReportlink();
		
		if(!(userType.equalsIgnoreCase("CHANNEL"))){
		if(!data[0].equals("")&&data[0]!=null)
		{extUserRole.selectZone(data[0]);}
		}
		if(!data[1].equals("")&&data[1]!=null)
		{extUserRole.selectDomain(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{extUserRole.selectCategory(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{extUserRole.enterUserName(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{extUserRole.selectuserStatus(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{extUserRole.selectsortType(data[5]);}
		
		if(extUserRole.submitBtnenabled()){
			extUserRole.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		
		if(extUserRole.inetBtnenabled()){
			extUserRole.clickInetBtn();
		ResultMap.put("inetEnabled", "true");}
		else{ResultMap.put("inetEnabled", "false");}	
		
		
		
		
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
	
	
}
	
	
	
	
