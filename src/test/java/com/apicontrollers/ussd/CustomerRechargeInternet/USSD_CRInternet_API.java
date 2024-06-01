package com.apicontrollers.ussd.CustomerRechargeInternet;

import java.util.HashMap;

import com.utils._APIUtil;

public class USSD_CRInternet_API {

    final String MSISDN1 = "MSISDN1";
	final String PIN = "PIN";
	final String MSISDN2 = "MSISDN2";
	final String AMOUNT = "AMOUNT";
	final String LANGUAGE1 = "LANGUAGE1";
	final String LANGUAGE2 = "LANGUAGE2";
	final String NOTIFICATION_MSISDN = "NOTIFICATION_MSISDN";
	final String SELECTOR = "SELECTOR";
	
	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	
	/**
	 * @category RoadMap Internet Recharge API
	 * @author simarnoor.bains
	 */
	private final String API_InternetRechargeAPI = "<?xml version=\"1.0\"?><COMMAND>"
			+ "<TYPE>PSTNRCTRFREQ</TYPE>"
			+ "<MSISDN1></MSISDN1>"
			+ "<PIN></PIN>"
			+ "<MSISDN2></MSISDN2>"
			+ "<AMOUNT></AMOUNT>"
			+ "<LANGUAGE1></LANGUAGE1>"
			+ "<LANGUAGE2></LANGUAGE2>"
			+ "<NOTIFICATION_MSISDN></NOTIFICATION_MSISDN>"
			+ "<SELECTOR></SELECTOR>"
            + "</COMMAND>";

	/**
	 * Method to handle the Version Based API Handling
	 * @return
	 */
	private String getAPI() {
		return API_InternetRechargeAPI;
	}
	
	public String prepareAPI(HashMap<String, String> dataMap) {
		String API = getAPI();
		return _APIUtil.buildAPI(API, dataMap);
	}
	
	

}
