package com.apicontrollers.ussd.CommissionEnquiry;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.*;
import com.commons.SystemPreferences;
import java.util.HashMap;



public class USSD_ComEnquiry_DP extends CaseMaster {

    public static String CUCategory = null;
    public static String TCPName = null;
    public static String Domain = null;
    public static String ProductCode = null;
    public static String LoginID = null;

    public static HashMap<String, String> getAPIdata() {


        HashMap<String, String> apiData = new HashMap<String, String>();

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        int dataRowCounter = 0;
        String channelUserCategory = null;


        apiData.put(CE.NO_OF_DAYS,DBHandler.AccessHandler.getSystemPreference("MAX_DATEDIFF"));

       // SystemPreferences.MAX_DATEDIFF = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_DATEDIFF"));

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();

        for (int i = 0; i <= dataRowCounter; i ++) {
            String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
            if (USSDStatus.equalsIgnoreCase("Y")) {
                channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
                break;
            }
        }

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        dataRowCounter = ExcelUtility.getRowCount();

        apiData.put(CE.DATE, _APIUtil.getCurrentTimeStamp());
        for (int i = 0; i <= dataRowCounter; i++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            if (excelCategory.equalsIgnoreCase(channelUserCategory)) {
                apiData.put(CE.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
                apiData.put(CE.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, i));
                break;
            }




        }



        return apiData;
    }
}


