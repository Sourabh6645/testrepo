package com.apicontrollers.extgw.VMS;

import java.util.HashMap;

import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class EXTGW_VoucherChangeStatus_DP {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherChangeStatus_API voucherChangeStatusAPI = new EXTGW_VoucherChangeStatus_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(voucherChangeStatusAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		return apiData;
	}
}
