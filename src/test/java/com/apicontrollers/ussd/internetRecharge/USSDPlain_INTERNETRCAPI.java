package com.apicontrollers.ussd.internetRecharge;

import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;

public class USSDPlain_INTERNETRCAPI {
	
	final String EXTNWCODE = "EXTNWCODE";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LOGINID = "LOGINID";
	final String PASSWORD = "PASSWORD";
	final String EXTREFNUM = "EXTREFNUM";
	final String LANGUAGE2 = "LANGUAGE2";
	final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String EXTCODE = "EXTCODE";
	final String AMOUNT = "AMOUNT";
	final String DATE = "DATE";
	final String SELECTOR = "SELECTOR";
	final String NOTIFICATION_MSISDN = "NOTIFICATION_MSISDN";
	
	//Response Parameters
		public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
		public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Internet Recharge API
	 * @author shallu
	 */
	private final String API_INTERNETRC = "TYPE=INTRRCTRFREQ";


	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_INTERNETRC;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return BTSLUtil.getQueryString(API, dataMap);
	}



}
