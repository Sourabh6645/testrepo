package com.dbrepository;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.businesscontrollers.UserTransferCountsVO;
import com.classes.CONSTANT;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.pretupsControllers.BTSLUtil;
import com.pretupsControllers.commissionprofile.CommissionProfileDetailsVO;
import com.pretupsControllers.commissionprofile.UserOTFCountsVO;
import com.sun.rowset.CachedRowSetImpl;
import com.utils.BTSLDateUtil;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.OneWayHashingAlgoUtil;
import com.utils._masterVO;
import com.utils._parser;

import restassuredapi.pojo.addCateogryPojo.CategoryVO;
import restassuredapi.pojo.addpromoRuleRespPojos.CardGroupVO;
import restassuredapi.pojo.userprofilethresholdresponsepojo.UserVO;

/**
 * @author krishan.chawla This is a SQL Query Repository class. All the SQL
 *         Queries are executed and the results are returned in this class only.
 *         The class is dependent on DBUtil class.
 */

@SuppressWarnings({ "unused" })
public class OracleRepository implements DBInterface {

	static CachedRowSetImpl ReturnCachedResult = null;

	public ResultSet fetchVoucherBundleDetails(String VBName) {
		final String methodName = "fetchVoucherBundleDetails";
		Log.info("Entered " + methodName + "(" + VBName + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT vbd.PROFILE_ID, vp.PRODUCT_NAME ");
		sqlSelectBuff.append("FROM VOMS_BUNDLE_DETAILS vbd INNER JOIN VOMS_PRODUCTS vp ");
		sqlSelectBuff.append("ON vbd.PROFILE_ID = vp.PRODUCT_ID ");
		sqlSelectBuff.append("WHERE vbd.VOMS_BUNDLE_NAME = ?");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, VBName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	public String fetchMRPforBundle(String VBName) {
		final String methodName = "fetchMRPforBundle";
		Log.info("Entered " + methodName + "(" + VBName + ")");
		String MRP = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select RETAIL_PRICE from VOMS_BUNDLE_MASTER ");
		sqlSelectBuff.append("where BUNDLE_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, VBName);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MRP = QueryResult.getString("RETAIL_PRICE");
		} catch (Exception e) {
			Log.info("Error while fetching MRP : ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MRP (" + MRP + ")");
		Log.info("Exiting " + methodName + "()");
		return MRP;
	}

	// jj
	public String checkForUniqueVBPrefix(String VBPrefix) {
		final String methodName = "checkForUniqueVBPrefix";
		Log.info("Entered " + methodName + "(" + VBPrefix + ")");
		String VBPrefixStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_BUNDLE_MASTER where BUNDLE_PREFIX = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, VBPrefix);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			VBPrefixStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching CommissionProfileName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VBPrefix Status (" + VBPrefixStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return VBPrefixStatus;
	}

	// jj
	public String checkForUniqueVBName(String VBName) {
		final String methodName = "checkForUniqueVBName";
		Log.info("Entered " + methodName + "(" + VBName + ")");
		String VBNameStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_BUNDLE_MASTER where BUNDLE_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, VBName);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			VBNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching CommissionProfileName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VBName Status (" + VBNameStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return VBNameStatus;
	}

	/**
	 * Query for fetching Operator Users & Their respective parents
	 *
	 * @return: Parent Category Name, Category Name, Category Code, Domain Name,
	 *          Domain Code, Geographical Domain Type, Sequence Number
	 * @author krishan.chawla Scope: PreRequisiteQueryBuilder.java
	 **/

	public ResultSet fetchOperatorUsers() {
		final String methodName = "fetchOperatorUsers";
		Log.debug("Entered " + methodName + "()");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT p.category_code as parent_category_code, p.category_name as parent_name,c.category_code, ");
		sqlSelectBuff.append("c.category_name, c.domain_code, c.sequence_no, c.grph_domain_type ");
		sqlSelectBuff.append("FROM categories c, lookups l, geographical_domain_types gdt, categories p ");
		sqlSelectBuff.append("WHERE c.domain_code = 'OPT' AND l.lookup_code = c.category_code ");
		sqlSelectBuff.append("AND c.status <> 'N' and p.status <> 'N' AND l.status = 'Y' ");
		sqlSelectBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ");
		sqlSelectBuff.append("AND p.category_code = l.lookup_type ORDER BY p.sequence_no asc ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(ReturnCachedResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	public ResultSet fetchOperatorUsersVMSNetworkAdmin() {
		final String methodName = "fetchOperatorUsers";
		Log.debug("Entered " + methodName + "()");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT p.category_code as parent_category_code, p.category_name as parent_name,c.category_code, ");
		sqlSelectBuff.append("c.category_name, c.domain_code, c.sequence_no, c.grph_domain_type ");
		sqlSelectBuff.append("FROM categories c, lookups l, geographical_domain_types gdt, categories p ");
		sqlSelectBuff.append("WHERE c.domain_code = 'OPT' AND l.lookup_code = c.category_code ");
		sqlSelectBuff.append("AND c.status <> 'N' and p.status <> 'N' AND l.status = 'Y' ");
		sqlSelectBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ");
		sqlSelectBuff.append(
				"AND p.category_code = l.lookup_type AND c.category_code ='NWADM' AND p.category_code = 'SUADM' ORDER BY c.sequence_no asc");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(ReturnCachedResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/*
	 * Added By Krishan to Fix Claro Colombia Spanish Language Issue
	 */
	public String getCategoryName(String CategoryCode) {
		final String methodName = "getCategoryName";
		Log.info("Entered " + methodName + "(" + CategoryCode + ")");
		String categoryName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select CATEGORY_NAME from Categories where CATEGORY_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			categoryName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Category Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CATEGORY_NAME (" + categoryName + ")");
		Log.info("Exiting " + methodName + "()");
		return categoryName;
	}

	public String getDomainCodeCatgories(String CategoryCode) {
		final String methodName = "getDomainCodeCatgories";
		Log.info("Entered " + methodName + "(" + CategoryCode + ")");
		String categoryName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select DOMAIN_CODE from Categories where CATEGORY_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			categoryName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Category Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CATEGORY_NAME (" + categoryName + ")");
		Log.info("Exiting " + methodName + "()");
		return categoryName;
	}

	public String getUserIdLoginID(String loginID) {
		final String methodName = "getUserIdLoginID";
		Log.info("Entered " + methodName + "(" + loginID + ")");
		String userID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select user_id from users where login_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			userID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching user id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: UserID (" + userID + ")");
		Log.info("Exiting " + methodName + "()");
		return userID;
	}

	public String getGrpDomainCode(String userID) {
		final String methodName = "getGrpDomainCode";
		Log.info("Entered " + methodName + "(" + userID + ")");
		String domainCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select grph_domain_code from user_geographies where user_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domainCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching domain code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: domain code (" + domainCode + ")");
		Log.info("Exiting " + methodName + "()");
		return domainCode;
	}

	public String getGrpDomainName(String getGrpDomainCode) {
		final String methodName = "getGrpDomainName";
		Log.info("Entered " + methodName + "(" + getGrpDomainCode + ")");
		String grpDomainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select grph_domain_name from geographical_domains where grph_domain_code = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, getGrpDomainCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			grpDomainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching domain name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: domain name (" + grpDomainName + ")");
		Log.info("Exiting " + methodName + "()");
		return grpDomainName;
	}

	public String getGrpDomainCodeFromName(String getGrpDomainCode) {
		final String methodName = "getGrpDomainName";
		Log.info("Entered " + methodName + "(" + getGrpDomainCode + ")");
		String grpDomainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select grph_domain_code from geographical_domains where grph_domain_name = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, getGrpDomainCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			grpDomainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching domain name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: domain name (" + grpDomainName + ")");
		Log.info("Exiting " + methodName + "()");
		return grpDomainName;
	}

	/**
	 * Query for fetching System Preferences
	 *
	 * @param: DEFAULT_VALUE
	 * @return: Default Value
	 * @author krishan.chawla Scope: NetworkStockCreation.java
	 **/

	public String getSystemPreference(String Preference_Code) {
		final String methodName = "getSystemPreference";
		Log.info("Entered " + methodName + "(" + Preference_Code + ")");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select DEFAULT_VALUE from system_preferences where PREFERENCE_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Preference_Code);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			prefVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULT_VALUE (" + prefVal + ")");
		Log.info("Exiting " + methodName + "()");
		return prefVal;
	}

	/**
	 * @author ashmeet.saggu : MVDRechargeRevamp
	 */
	public String getMVDTransactionID(String serialNumber) {
		final String methodName = "getMVDTransactionID";
		Log.info("Entered " + methodName + "(" + serialNumber + ")");
		String MVDTransactionID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select ext_transaction_id from system_preferences where serial_no = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, serialNumber);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MVDTransactionID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MVDTransactionID (" + MVDTransactionID + ")");
		Log.info("Exiting " + methodName + "()");
		return MVDTransactionID;
	}

	/**
	 * @author ashmeet.saggu : MVDRechargeRevamp
	 */
	public String getSystemPreferenceDefaultValue(String Preference_Code) {
		final String methodName = "getSystemPreference";
		Log.info("Entered " + methodName + "(" + Preference_Code + ")");
		String defaultVoucherNo = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select DEFAULT_VALUE from system_preferences where PREFERENCE_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Preference_Code);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			defaultVoucherNo = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULT_VALUE (" + defaultVoucherNo + ")");
		Log.info("Exiting " + methodName + "()");
		return defaultVoucherNo;
	}

	/**
	 * @author ashmeet.saggu : MVDRechargeRevamp
	 */
	public String getProductIDOfVoucherProfile(String voucherProfile) {
		final String methodName = "getProductIDOfVoucherProfile";
		Log.info("Entered " + methodName + "(" + voucherProfile + ")");
		String productID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("PRODUCT_ID from voms_products where PRODUCT_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, voucherProfile);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			productID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: productID (" + productID + ")");
		Log.info("Exiting " + methodName + "()");
		return productID;
	}

	/**
	 * Query for fetching Network Preferences according to the selected Network
	 *
	 * @throws SQLException
	 * @param: Network Code, Preference_Code
	 * @return: Default Value
	 * @author krishan.chawla Scope: NetworkStockApprovalPage.java
	 */
	public String getNetworkPreference(String Network_Code, String Preference_Code) {
		final String methodName = "getNetworkPreference";
		Log.info("Entered " + methodName + "(" + Network_Code + " , " + Preference_Code + ")");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlNetworkSelectBuff = new StringBuilder("select VALUE from NETWORK_PREFERENCES where ");
		sqlNetworkSelectBuff.append("NETWORK_CODE = ? AND PREFERENCE_CODE = ?");
		String sqlNetworkSelect = sqlNetworkSelectBuff.toString();

		StringBuilder sqlSystemSelectBuff = new StringBuilder(
				"select DEFAULT_VALUE from SYSTEM_PREFERENCES where PREFERENCE_CODE = ?");
		String sqlSystemSelect = sqlSystemSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlNetworkSelect);
			pstmt.setString(1, Network_Code);
			pstmt.setString(2, Preference_Code);
			Log.info(methodName + "() :: select query: " + sqlNetworkSelect);
			QueryResult = pstmt.executeQuery();
			if (!QueryResult.isBeforeFirst()) {
				Log.info("Network Preference Not Found");
				Log.info("Trying to get Preference value from System Preferences");
				if (pstmt != null)
					try {
						pstmt.close();
					} catch (SQLException e) {
						Log.writeStackTrace(e);
					}
				pstmt = connection.prepareStatement(sqlSystemSelect);
				pstmt.setString(1, Preference_Code);
				Log.info(methodName + "() :: select query: " + sqlSystemSelect);
				if (QueryResult != null)
					try {
						QueryResult.close();
					} catch (SQLException e) {
						Log.writeStackTrace(e);
					}
				QueryResult = pstmt.executeQuery();
			}
			QueryResult.next();
			prefVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Preference");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULT_VALUE (" + prefVal + ")");
		Log.info("Exiting " + methodName + "()");
		return prefVal;
	}

	/**
	 * Query for fetching Geographical Domain Types
	 *
	 * @return: Geographical Domain Type, Geographical Domain Type Name,
	 *          Geographical Domain Parent, Sequence Number
	 * @author ayush.abhijeet Scope: PreRequisiteQueryBuilder.java
	 */
	public ResultSet getGeographicalDomainTypes() {
		final String methodName = "getGeographicalDomainTypes";
		Log.debug("Entered " + methodName + "()");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select GRPH_DOMAIN_TYPE, GRPH_DOMAIN_TYPE_NAME, GRPH_DOMAIN_PARENT, ");
		sqlSelectBuff.append("SEQUENCE_NO from GEOGRAPHICAL_DOMAIN_TYPES order by SEQUENCE_NO");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(QueryResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/**
	 * Query for fetching Channel Domain & Categories
	 *
	 * @return: Pending
	 * @author tinky.sharma Scope: DataBuilder.java
	 */
	public ResultSet getDomainandCategories() {
		final String methodName = "getDomainandCategories";
		Log.debug("Entered " + methodName + "()");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT domains.domain_code, categories.category_code, categories.category_name, ");
		sqlSelectBuff.append("domains.domain_name FROM domains INNER JOIN categories ON ");
		sqlSelectBuff.append("(domains.domain_code = categories.domain_code) WHERE ");
		sqlSelectBuff.append("domains.status = 'Y' AND domains.domain_code NOT LIKE 'AUT%' ");
		sqlSelectBuff.append("AND domains.domain_code NOT LIKE 'OPT%'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(QueryResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/**
	 * Query for checking Unique Login ID and returning the result
	 *
	 * @return: LOGIN_ID
	 * @author lokesh.kontey Scope: UniqueChecker.java
	 */
	public String checkForUniqueLoginID(String LoginID) {
		final String methodName = "checkForUniqueLoginID";
		Log.info("Entered " + methodName + "(" + LoginID + ")");
		String LoginIDStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from users where Login_ID = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LoginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			LoginIDStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching LoginID status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LoginIDStatus (" + LoginIDStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return LoginIDStatus;
	}

	/**
	 * Query for checking Unique MSISDN and returning the result
	 *
	 * @return: MSISDN
	 * @author lokesh.kontey Scope: UniqueChecker.java
	 */
	public String checkForUniqueMSISDN(String MSISDN) {
		final String methodName = "checkForUniqueMSISDN";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String MSISDNStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists (");
		sqlSelectBuff.append(
				"select 1 from users, user_phones where users.USER_ID = user_phones.USER_ID AND user_phones.MSISDN = ? OR users.MSISDN = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			pstmt.setString(2, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDNStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDNStatus (" + MSISDNStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return MSISDNStatus;
	}

	/**
	 * Query for checking Unique External Transaction Number for Operator to Channel
	 * Transfers
	 *
	 * @return: externalTxnNum
	 * @author krishan.chawla Scope: UniqueChecker.java
	 */
	public String checkForUniqueExternalTxnNum(String externalTxnNum) {
		final String methodName = "checkForUniqueExternalTxnNum";
		Log.info("Entered " + methodName + "(" + externalTxnNum + ")");
		String ExtTxnNoStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from channel_transfers where EXT_TXN_NO = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, externalTxnNum);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ExtTxnNoStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: External Transaction No. Status (" + ExtTxnNoStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return ExtTxnNoStatus;
	}

	/**
	 * Query for checking Unique External Code and returning the result
	 *
	 * @return: EXTCODE
	 * @author lokesh.kontey Scope: UniqueChecker.java
	 */
	public String checkForUniqueEXTCODE(String EXTCODE) {
		final String methodName = "checkForUniqueEXTCODE";
		Log.info("Entered " + methodName + "(" + EXTCODE + ")");
		String ExternalCodeStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from users where EXTERNAL_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, EXTCODE);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ExternalCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching External Code Status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: ExternalCodeStatus (" + ExternalCodeStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return ExternalCodeStatus;
	}

	/**
	 * Query for fetching Password of User using Login_ID
	 *
	 * @return: Password
	 * @author krishan.chawla Scope: OperatorUserCreation.java
	 */
	public String fetchUserPassword(String Login_ID) {
		final String methodName = "fetchUserPassword";
		Log.info("Entered " + methodName + "(" + Login_ID + ")");
		String Password = null, DecryptedPassword = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select Password from Users where Login_ID= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Login_ID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			Password = QueryResult.getString(1);

			if (DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE)
					.equalsIgnoreCase("SHA")) {
				String en_password1 = OneWayHashingAlgoUtil.getInstance().encrypt(_masterVO.getProperty("Password"));
				String en_password2 = OneWayHashingAlgoUtil.getInstance().encrypt(_masterVO.getProperty("NewPassword"));
				String en_password3 = OneWayHashingAlgoUtil.getInstance()
						.encrypt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_PASSWORD));
				String en_password4 = OneWayHashingAlgoUtil.getInstance()
						.encrypt(_masterVO.getProperty("ResetPassword"));
				String en_password5 = OneWayHashingAlgoUtil.getInstance().encrypt(CONSTANT.CHANGING_PASSWORD);
				if (DBHandler.AccessHandler.getSystemPreference(CONSTANT.AUTO_PWD_GENERATE_ALLOW)
						.equalsIgnoreCase("true")
						&& (!Password.equals(en_password1) && !Password.equals(en_password2)
								&& !Password.equals(en_password3) && !Password.equals(en_password4)
								&& !Password.equals(en_password5))) {
					updateAnyColumnValue(CONSTANT.USERS, CONSTANT.PASSWORD, en_password1, CONSTANT.LOGIN_ID, Login_ID);
					DecryptedPassword = _masterVO.getProperty("Password");
				} else if (Password.equals(en_password1)) {
					DecryptedPassword = _masterVO.getProperty("Password");
				} else if (Password.equals(en_password2)) {
					DecryptedPassword = _masterVO.getProperty("NewPassword");
				} else if (Password.equals(en_password3)) {
					DecryptedPassword = DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_PASSWORD);
				} else if (Password.equals(en_password4)) {
					DecryptedPassword = _masterVO.getProperty("ResetPassword");
				} else if (Password.equals(en_password5)) {
					DecryptedPassword = CONSTANT.CHANGING_PASSWORD;
				}
			} else {
				DecryptedPassword = Decrypt.decryption(Password);
			}
		} catch (Exception e) {
			Log.info("Error while fetching Password: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DecryptedPassword (" + DecryptedPassword + ")");
		Log.info("Exiting " + methodName + "()");
		return DecryptedPassword;
	}

	/**
	 * Query for fetching Card Group Set ID Using Card Group Name
	 *
	 * @return: CardGroupSetID
	 * @author krishan.chawla
	 */
	public String getCardGroupSetID(String cardGroupName) {
		final String methodName = "getCardGroupSetID";
		Log.info("Entered " + methodName + "(" + cardGroupName + ")");
		String cardGroupSetID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select CARD_GROUP_SET_ID from CARD_GROUP_SET where CARD_GROUP_SET_NAME= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, cardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			cardGroupSetID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Card Group Set ID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CardGroup Set ID (" + cardGroupSetID + ")");
		Log.info("Exiting " + methodName + "()");
		return cardGroupSetID;
	}

	/**
	 * Query for fetching TCP ID of provided Transfer Control Profile Name
	 *
	 * @return: TCPID
	 * @author tinky.sharma Scope: CategoryLevelTCPCreation.java
	 */
	public String fetchTCPID(String TCPName) {
		final String methodName = "fetchTCPID";
		Log.info("Entered " + methodName + "(" + TCPName + ")");
		String TCPID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select * from TRANSFER_PROFILE where PROFILE_NAME= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TCPName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			TCPID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Transfer Control ProfileID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Transfer Control Profile ID (" + TCPID + ")");
		Log.info("Exiting " + methodName + "()");
		return TCPID;
	}

	/**
	 * Query for checking if provided Domain Code / Name / Shortname is unique &
	 * does not exist in system.
	 *
	 * @return: REC_EXIST
	 * @author ayush.abhijit Scope: UniqueChecker.java
	 */
	public String checkUniqueDomain(String DomainCode, String DomainName, String DomainShortName) {
		final String methodName = "checkUniqueDomain";
		Log.info("Entered " + methodName + "(" + DomainCode + "," + DomainName + "," + DomainShortName + ")");
		String REC_EXIST = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_CODE =? ");
		sqlSelectBuff.append("or GRPH_DOMAIN_NAME=? or GRPH_DOMAIN_SHORT_NAME=? ) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, DomainCode);
			pstmt.setString(2, DomainName);
			pstmt.setString(3, DomainShortName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			REC_EXIST = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Unique Domain Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Unique Domain Code / Name / ShortName Status (" + REC_EXIST + ")");
		Log.info("Exiting " + methodName + "()");
		return REC_EXIST;
	}

	/**
	 * Query for fetching Default Card Group Name
	 *
	 * @return: Card Group Name
	 * @author krishan.chawla Scope: GradeCreation.java
	 */
	public String getGradeName(String CategoryName) {
		final String methodName = "getGradeName";
		Log.info("Entered " + methodName + "(" + CategoryName + ")");
		String GradeName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT grade_name FROM channel_grades INNER JOIN ");
		sqlSelectBuff.append("categories ON channel_grades.category_code = categories.category_code ");
		sqlSelectBuff
				.append("WHERE channel_grades.is_default_grade = 'Y' and category_name=? and categories.status= 'Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			GradeName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GradeName: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GradeName (" + GradeName + ")");
		Log.info("Exiting " + methodName + "()");
		return GradeName;
	}

	public String getGradeCode(String GradeName) {
		final String methodName = "getGradeCode";
		Log.info("Entered " + methodName + "(" + GradeName + ")");
		String GradeCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT grade_code FROM channel_grades ");
		sqlSelectBuff.append("WHERE grade_name =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, GradeName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			GradeCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GradeCode: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GradeCode (" + GradeCode + ")");
		Log.info("Exiting " + methodName + "()");
		return GradeCode;
	}

	/**
	 * Query for fetching Default Geographical Domain Data
	 *
	 * @return: GeographicalDomainData[]
	 * @author krishan.chawla Scope: PreRequisite_GeographicalDomainManagement
	 */
	public String[] getDefaultGeographicalDomain(String NetworkCode, String domainTypeName) {
		final String methodName = "getDefaultGeographicalDomain";
		Log.info("Entered " + methodName + "(" + NetworkCode + ", " + domainTypeName + ")");
		String geographicalDomainDetails[] = new String[4];
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT d.grph_domain_code, d.grph_domain_name, d.grph_domain_short_name, dt.grph_domain_type_name ");
		sqlSelectBuff.append(
				"FROM geographical_domains d, geographical_domain_types dt WHERE d.grph_domain_type = dt.grph_domain_type ");
		sqlSelectBuff.append(
				"AND dt.grph_domain_type_name = ? AND d.network_code = ? AND d.is_default = 'Y' AND d.status = 'Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, domainTypeName);
			pstmt.setString(2, NetworkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			geographicalDomainDetails[0] = QueryResult.getString(1);
			geographicalDomainDetails[1] = QueryResult.getString(2);
			geographicalDomainDetails[2] = QueryResult.getString(3);
			geographicalDomainDetails[3] = QueryResult.getString(4);
		} catch (Exception e) {
			Log.info("Error while fetching Default Geographical Domain Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Geographical Domain Details (" + geographicalDomainDetails[0] + ", "
				+ geographicalDomainDetails[1] + ", " + geographicalDomainDetails[2] + ", "
				+ geographicalDomainDetails[3] + ")");
		Log.info("Exiting " + methodName + "()");
		return geographicalDomainDetails;
	}

	/**
	 * Query for Category Code for the Provided Category Name
	 *
	 * @return: Category Code
	 * @author krishan.chawla Scope: BuilderLogic.java
	 */
	public String[] fetchCategoryCodeAndGeographicalDomainType(String CategoryName) {
		final String methodName = "fetchCategoryCodeAndGeographicalDomainType";
		Log.info("Entered " + methodName + "(" + CategoryName + ")");
		String CategoryCode[] = new String[2];
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select category_code, grph_domain_type from categories where category_name=? and status='Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			CategoryCode[0] = QueryResult.getString(1);
			CategoryCode[1] = QueryResult.getString(2);
		} catch (Exception e) {
			Log.info("Error while fetching CategoryCode / Geographical Domain Type: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CategoryCode / Geographical Domain Type (" + CategoryCode[0] + " / "
				+ CategoryCode[1] + ")");
		Log.info("Exiting " + methodName + "()");
		return CategoryCode;
	}

	/**
	 * Query for fetching C2S Services and sub services
	 *
	 * @return: Service Type, service name and sub services
	 * @author tinky.sharma Scope: PreRequisiteQueryBuilder.java
	 **/
	public ResultSet fetchC2SServicesAndSubServices(String NetworkCode) {
		final String methodName = "fetchC2SServicesAndSubServices";
		Log.info("Entered " + methodName + "(" + NetworkCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DISTINCT st.service_type, st.NAME,stsm.selector_name ");
		sqlSelectBuff.append("FROM service_type st, network_services ns,service_type_selector_mapping stsm ");
		sqlSelectBuff.append("WHERE st.service_type = ns.service_type AND ");
		sqlSelectBuff.append("st.service_type =stsm.service_type AND ");
		sqlSelectBuff.append("ns.status <> 'N' AND st.status <> 'N' AND stsm.status <> 'N' AND ");
		sqlSelectBuff.append("ns.sender_network = ? AND st.module = 'C2S' ");
		sqlSelectBuff.append("AND st.external_interface = 'Y' ORDER BY st.NAME,stsm.selector_name");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, NetworkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/**
	 * Query for fetching P2P Services and sub services
	 *
	 * @return: Service Type, service name and sub services
	 * @author tinky.sharma Scope: PreRequisiteQueryBuilder.java
	 **/
	public ResultSet fetchP2PServicesAndSubServices(String NetworkCode) {
		final String methodName = "fetchP2PServicesAndSubServices";
		Log.info("Entered " + methodName + "(" + NetworkCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DISTINCT st.service_type, st.NAME,stsm.selector_name ");
		sqlSelectBuff.append("FROM service_type st, network_services ns,service_type_selector_mapping stsm ");
		sqlSelectBuff.append("WHERE st.service_type = ns.service_type AND st.service_type =stsm.service_type ");
		sqlSelectBuff.append("AND ns.status <> 'N'AND st.status <> 'N'AND stsm.status <> 'N' ");
		sqlSelectBuff.append("AND ns.sender_network = ? AND st.module = 'P2P' ");
		sqlSelectBuff.append("AND st.external_interface = 'Y' ORDER BY st.NAME,stsm.selector_name");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, NetworkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	public ResultSet fetchP2PServicesAndSubServicesforVoucher(String NetworkCode) {
		final String methodName = "fetchP2PServicesAndSubServices";
		Log.info("Entered " + methodName + "(" + NetworkCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DISTINCT st.service_type, st.NAME,stsm.selector_name ");
		sqlSelectBuff.append("FROM service_type st, network_services ns,service_type_selector_mapping stsm ");
		sqlSelectBuff.append("WHERE st.service_type = ns.service_type AND st.service_type =stsm.service_type ");
		sqlSelectBuff.append("AND ns.status <> 'N'AND st.status <> 'N'AND stsm.status <> 'N' ");
		sqlSelectBuff.append("AND ns.sender_network = ? AND st.module = 'P2P'  AND st.SERVICE_TYPE = 'VCN' ");
		sqlSelectBuff.append("AND st.external_interface = 'Y' ORDER BY st.NAME,stsm.selector_name");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, NetworkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/**
	 * Query for fetching ProductType
	 *
	 * @return: Product Code, Product type, Module Code, Product name, Short Name
	 * @author tinky.sharma Scope: BuilderLogic.java
	 **/
	public ResultSet fetchProductType() {
		final String methodName = "fetchProductType";
		Log.debug("Entered " + methodName + "()");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		ArrayList<String> VMSLookup = getVoucherProductCodeList();

		StringBuffer sqlSelectBuff = new StringBuffer(
				"SELECT PRODUCT_CODE, PRODUCT_TYPE, MODULE_CODE, PRODUCT_NAME, SHORT_NAME, PRODUCT_SHORT_CODE ");
		sqlSelectBuff.append("from products where product_code NOT IN (" + arrayListToStringList(VMSLookup)
				+ ") and module_code <> 'P2P' and status='Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(QueryResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/* ------------------ H E L P E R M E T H O D ---------------------- */
	private String arrayListToStringList(ArrayList<String> list) {

		if (list.size() == 0) {
			return "'VOUCHTRACK'";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append("'" + list.get(i) + "',");
		}

		String filter = sb.toString();
		filter = filter.substring(0, filter.length() - 1);

		return filter;
	}
	/* ------------------------------------------------------------------ */

	/**
	 * Query for fetching PIN of user
	 *
	 * @return: PIN
	 * @author lokesh.kontey Scope: add channel user
	 **/
	public String fetchUserPIN(String Login_ID, String msisdn) {
		final String methodName = "fetchUserPIN";
		Log.info("Entered " + methodName + "(" + Login_ID + ", " + msisdn + ")");
		String PIN = null, DecryptedPIN = null, UserID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff1 = null;
		String sqlSelect1 = null;
		PreparedStatement pstmt1 = null;
		ResultSet QueryResult1 = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT sms_pin FROM user_phones ");
		if (Login_ID != null && !Login_ID.equals(""))
			sqlSelectBuff.append("WHERE user_id IN (SELECT user_id FROM users WHERE login_id = ?)");
		else
			sqlSelectBuff.append(
					"WHERE user_id IN (SELECT user_id FROM users WHERE MSISDN = ? AND user_type='CHANNEL' AND Status!='N')");
		String sqlSelect = sqlSelectBuff.toString();

		String encryption = DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE);

		if (encryption.equalsIgnoreCase("SHA")) {
			sqlSelectBuff1 = new StringBuilder();
			if (Login_ID != null && !Login_ID.equals(""))
				sqlSelectBuff1.append("SELECT user_id FROM users WHERE login_id = ? ");
			else
				sqlSelectBuff1
						.append("SELECT user_id FROM users WHERE MSISDN = ? AND user_type='CHANNEL' AND Status!='N' ");
			sqlSelect1 = sqlSelectBuff1.toString();
		}

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);

			if (Login_ID != null && !Login_ID.equals("")) // added just to check whether it work or not on 13 Dec
				pstmt.setString(1, Login_ID);
			else
				pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			PIN = QueryResult.getString(1);
			Log.info("fetched PIN from DB: " + PIN);

			if (encryption.equalsIgnoreCase("SHA")) {
				String en_PIN1 = OneWayHashingAlgoUtil.getInstance().encrypt(_masterVO.getProperty("PIN"));
				String en_PIN2 = OneWayHashingAlgoUtil.getInstance().encrypt(_masterVO.getProperty("NewPIN"));
				String en_PIN3 = OneWayHashingAlgoUtil.getInstance()
						.encrypt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_SMSPIN));
				String en_PIN4 = OneWayHashingAlgoUtil.getInstance().encrypt(_masterVO.getProperty("ResetPIN"));
				if (DBHandler.AccessHandler.getSystemPreference(CONSTANT.AUTO_PIN_GENERATE_ALLOW)
						.equalsIgnoreCase("true")
						&& (!PIN.equals(en_PIN1) && !PIN.equals(en_PIN2) && !PIN.equals(en_PIN3)
								&& !PIN.equals(en_PIN4))) {
					pstmt1 = connection.prepareStatement(sqlSelect1);
					if (Login_ID != null && !Login_ID.equals("")) // added just to check whether it work or not on 13
																	// Dec
						pstmt1.setString(1, Login_ID);
					else
						pstmt1.setString(1, msisdn);
					Log.info(methodName + "() :: select query: " + sqlSelect1);
					QueryResult1 = pstmt1.executeQuery();
					QueryResult1.next();
					UserID = QueryResult1.getString(1);

					updateAnyColumnValue(CONSTANT.USER_PHONES, CONSTANT.PIN, en_PIN1, CONSTANT.USER_ID, UserID);
					DecryptedPIN = _masterVO.getProperty("PIN");
				} else if (PIN.equals(en_PIN1)) {
					DecryptedPIN = _masterVO.getProperty("PIN");
				} else if (PIN.equals(en_PIN2)) {
					DecryptedPIN = _masterVO.getProperty("NewPIN");
				} else if (PIN.equals(en_PIN3)) {
					DecryptedPIN = DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_SMSPIN);
				} else if (PIN.equals(en_PIN4)) {
					DecryptedPIN = _masterVO.getProperty("ResetPIN");
				}
			} else {
				DecryptedPIN = Decrypt.decryption(PIN);
			}
		} catch (Exception e) {
			Log.info("Error while fetching PIN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (QueryResult1 != null)
				try {
					QueryResult1.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt1 != null)
				try {
					pstmt1.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DecryptedPIN (" + DecryptedPIN + ")");
		Log.info("Exiting " + methodName + "()");
		return DecryptedPIN;
	}

	/**
	 * Query for fetching MSISDN for a category.
	 *
	 * @return: MSISDN
	 * @author tinky.sharma Scope: BarUser.java, UnBarUser.java
	 */
	public String getMSISDN(String CategoryName) {
		final String methodName = "getMSISDN";
		Log.info("Entered " + methodName + "(" + CategoryName + ")");
		String msisdn = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select MSISDN from users where CATEGORY_CODE= ? and status='Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			msisdn = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDN (" + msisdn + ")");
		Log.info("Exiting " + methodName + "()");
		return msisdn;
	}

	/**
	 * Query for fetching Card Group status for the specified Range.
	 *
	 * @return: Card group status
	 * @author tinky.sharma Scope: CardGroup.java
	 */
	/*
	 * public String getCardGroupStatus(String CardGroupName, int StartRange, int
	 * EndRange) { String status = null; Connection connection = null; Statement
	 * statement = null; ResultSet QueryResult = null; try { connection =
	 * DBConnectionPool.getInstance().getConnection(); statement =
	 * connection.createStatement(); Log.info("Entered :: " +
	 * Thread.currentThread().getStackTrace()[1].getMethodName() + "()");
	 * StringBuffer QueryBuffer = new
	 * StringBuffer("SELECT status FROM card_group_details WHERE end_range = " +
	 * EndRange + " "); QueryBuffer.append("AND start_range = " + StartRange +
	 * " AND "); QueryBuffer.
	 * append("card_group_set_id = (select CARD_GROUP_SET_ID from CARD_GROUP_SET where CARD_GROUP_SET_NAME='"
	 * + CardGroupName + "')"); QueryResult =
	 * statement.executeQuery(QueryBuffer.toString()); QueryResult.next(); status =
	 * QueryResult.getString(1); } catch (Exception e) {
	 * Log.info("Error while fetching Status: "); Log.writeStackTrace(e); } finally
	 * { if (QueryResult != null) try { QueryResult.close(); } catch (SQLException
	 * e) {Log.writeStackTrace(e);} if (statement != null) try { statement.close();
	 * } catch (SQLException e) {Log.writeStackTrace(e);} if (connection != null)
	 * try { connection.close(); } catch (SQLException e) {Log.writeStackTrace(e);}
	 * } Log.info("Query Repository Returns: Status (" + status + ")"); return
	 * status; }
	 */

	/**
	 * @author: Shallu
	 * @see com.dbrepository.DBInterface#getCardGroupStatus(java.lang.String, int,
	 *      int)
	 */
	public String getCardGroupStatus(String CardGroupName, int StartRange, int EndRange) {
		final String methodName = "getCardGroupStatus";
		Log.info("Entered " + methodName + "(" + CardGroupName + ", " + StartRange + ", " + EndRange + ")");
		String status = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT status, card_group_id, version FROM card_group_details ");
		sqlSelectBuff.append("WHERE end_range = ? AND start_range = ? AND ");
		sqlSelectBuff.append(
				"card_group_set_id = (select CARD_GROUP_SET_ID from CARD_GROUP_SET where CARD_GROUP_SET_NAME= ?) order by version desc");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setInt(1, EndRange);
			pstmt.setInt(2, StartRange);
			pstmt.setString(3, CardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			status = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + status + ")");
		Log.info("Exiting " + methodName + "()");
		return status;
	}

	public String getCardGroupName(String ModifiedBy, String SubService, String ServiceType) {
		final String methodName = "getCardGroupStatus";
		Log.info("Entered " + methodName + "(" + ModifiedBy + ", " + SubService + ", " + ServiceType + ")");
		String CardGroupName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT CARD_GROUP_SET_NAME FROM CARD_GROUP_SET ");
		sqlSelectBuff.append("WHERE MODIFIED_BY = ? AND SUB_SERVICE = ? AND SERVICE_TYPE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, ModifiedBy);
			pstmt.setString(2, SubService);
			pstmt.setString(3, ServiceType);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			CardGroupName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + CardGroupName + ")");
		Log.info("Exiting " + methodName + "()");
		return CardGroupName;
	}

	/**
	 * Query for fetching Network Stock Product Size
	 *
	 * @param: COUNT
	 * @return: COUNT
	 * @author krishan.chawla Scope: InitiateNetworkStockPage.java
	 **/
	public int getNetworkProductSize(String NetworkCode) {
		final String methodName = "getNetworkProductSize";
		Log.info("Entered " + methodName + "(" + NetworkCode + ")");
		int NetworkProductSize = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT count(p.product_code) FROM products p, network_product_mapping npm, network_stocks ns ");
		sqlSelectBuff.append("WHERE p.status = 'Y' AND p.product_code = npm.product_code ");
		sqlSelectBuff.append(
				"AND p.module_code = DECODE ('C2S', 'ALL', 'C2S', 'C2S') AND npm.product_code = ns.product_code(+) ");
		sqlSelectBuff.append("AND npm.network_code = ? AND npm.status = 'Y' ");
		sqlSelectBuff.append("AND npm.network_code = ns.network_code(+) AND ");
		sqlSelectBuff.append("ns.network_code_for(+) = ? AND ns.wallet_type = " + PretupsI.SALE_WALLET_LOOKUP);
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, NetworkCode);
			pstmt.setString(2, NetworkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			NetworkProductSize = Integer.parseInt(QueryResult.getString(1));
		} catch (Exception e) {
			Log.info("Exception while fetching Network Product Size: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Product Size (" + NetworkProductSize + ")");
		Log.info("Exiting " + methodName + "()");
		return NetworkProductSize;
	}

	/**
	 * Query for checking if the Network Stock Transaction ID entry exists in
	 * NETWORK_STOCK_TRANSACTIONS
	 *
	 * @param: COUNT
	 * @return: COUNT
	 * @author krishan.chawla Scope: InitiateNetworkStockPage.java
	 **/
	public String checkNetworkStockTransactionsForNetworkStockID(String TransactionID) {
		final String methodName = "checkNetworkStockTransactionsForNetworkStockID";
		Log.info("Entered " + methodName + "(" + TransactionID + ")");
		String checkIfTransactionIDExists = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists (");
		sqlSelectBuff.append("select 1 from NETWORK_STOCK_TRANSACTIONS where TXN_NO = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TransactionID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			checkIfTransactionIDExists = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching availablity of TransactionID in Network Stock Transactions Table: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Transaction Status (" + checkIfTransactionIDExists + ")");
		Log.info("Exiting " + methodName + "()");
		return checkIfTransactionIDExists;
	}

	/**
	 * Query to fetch existing login id randomly
	 *
	 * @author lokesh.kontey
	 * @return: Login_ID
	 */

	public String existingLoginID() {
		final String methodName = "existingLoginID";
		Log.info("Entered " + methodName + "()");
		String loginID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select Login_ID from ");
		sqlSelectBuff.append(
				"(select * from users where login_id is not NULL and status='Y' order by dbms_random.value()) ");
		sqlSelectBuff.append("where rownum='1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			loginID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LoginID (" + loginID + ")");
		Log.info("Exiting " + methodName + "()");
		return loginID;
	}

	/**
	 * Query to fetch existing MSISDN randomly
	 *
	 * @author lokesh.kontey
	 * @return: MSISDN
	 */
	public String existingMSISDN() {
		final String methodName = "existingMSISDN";
		Log.info("Entered " + methodName + "()");
		String MSISDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select MSISDN from ");
		sqlSelectBuff.append("(select us.MSISDN from user_phones up, users us where up.user_id=us.user_id AND "
				+ "us.user_type <> 'OPERATOR' and us.status='Y' and us.network_code='"
				+ _masterVO.getMasterValue(MasterI.NETWORK_CODE) + "' order by dbms_random.value()) ");
		sqlSelectBuff.append("where rownum='1'");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDN (" + MSISDN + ")");
		Log.info("Exiting " + methodName + "()");
		return MSISDN;
	}

	/**
	 * Query for fetching Network Name.
	 *
	 * @return: Network Name
	 * @author Ayush Abhijeet
	 */
	public String getNetworkName(String network) {
		final String methodName = "getNetworkName";
		Log.info("Entered " + methodName + "(" + network + ")");
		String network_name = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select network_name from networks where network_code = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, network);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			network_name = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + network_name + ")");
		Log.info("Exiting " + methodName + "()");
		return network_name;
	}

	/**
	 * Query for fetching Module Name.
	 *
	 * @return: Module
	 * @author Ayush Abhijeet
	 */
	public List<String> getModuleList() {
		final String methodName = "getModuleList";
		Log.info("Entered " + methodName + "()");
		List<String> modules = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select distinct module from service_type WHERE ");
		sqlSelectBuff.append("external_interface='Y' AND status ='Y' and module!='OPT'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			modules = new ArrayList<String>();
			while (QueryResult.next()) {
				modules.add(QueryResult.getString("module"));
			}
		} catch (Exception e) {
			Log.info("Error while fetching Module Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Module Name (" + modules + ")");
		Log.info("Exiting " + methodName + "()");
		return modules;
	}

	/**
	 * Query for fetching Service Type of modules.
	 *
	 * @return: Module
	 * @author Ayush Abhijeet
	 */
	public List<String> getModuleDescription(String module) {
		final String methodName = "getModuleDescription";
		Log.info("Entered " + methodName + "(" + module + ")");
		List<String> description = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT description FROM service_type WHERE ");
		sqlSelectBuff.append("external_interface='Y' AND status ='Y' AND ");
		sqlSelectBuff.append("module= ? order by description");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, module);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			description = new ArrayList<String>();
			while (QueryResult.next()) {
				description.add(QueryResult.getString("description"));
			}
		} catch (Exception e) {
			Log.info("Error while fetching Module Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Module Name (" + description + ")");
		Log.info("Exiting " + methodName + "()");
		return description;
	}

	/**
	 * This query fetches Category Name according to Provided Domain whose O2C
	 * Transfer Rule does not exists in system
	 *
	 * @param DOMAIN
	 * @return Scope: UAP_NetworkStock.java
	 */
	public ResultSet getCategoriesWithNoO2CTransferRules(String Domain) {
		final String methodName = "getCategoriesWithNoO2CTransferRules";
		Log.info("Entered " + methodName + "(" + Domain + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT category_name FROM categories ");
		sqlSelectBuff.append("WHERE category_code IN (SELECT b.category_code AS category_code FROM ");
		sqlSelectBuff.append("categories b WHERE b.status = 'Y' AND b.domain_code = (SELECT domain_code ");
		sqlSelectBuff.append("FROM domains WHERE domain_name = ?) ");
		sqlSelectBuff.append("MINUS SELECT c.to_category AS category_code FROM chnl_transfer_rules c ");
		sqlSelectBuff.append("WHERE from_category = 'OPT' AND to_domain_code = (SELECT domain_code ");
		sqlSelectBuff.append("FROM domains WHERE domain_name = ?))");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Domain);
			pstmt.setString(2, Domain);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	/**
	 * Query to fetch Maximum Password block count
	 *
	 * @author lokesh.kontey
	 * @return: Integer value
	 */
	public int maxPasswordBlockCount(String ControlCode) {
		final String methodName = "maxPasswordBlockCount";
		Log.info("Entered " + methodName + "(" + ControlCode + ")");
		int nValue = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff_CP = new StringBuilder(
				"select VALUE from control_preferences where CONTROL_CODE = ? and PREFERENCE_CODE = 'MAX_PWD_BLOCK_COUNT'");
		String sqlSelect_CP = sqlSelectBuff_CP.toString();

		StringBuilder sqlSelectBuff_NP = new StringBuilder(
				"select default_value from system_preferences where preference_code='MAX_PWD_BLOCK_COUNT'");
		String sqlSelect_NP = sqlSelectBuff_NP.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect_CP);
			pstmt.setString(1, ControlCode);
			Log.info(methodName + "() :: select query: " + sqlSelect_CP);
			QueryResult = pstmt.executeQuery();
			boolean exist = QueryResult.next();
			if (exist)
				nValue = QueryResult.getInt(1);
			else if (!exist) {
				if (QueryResult != null)
					try {
						QueryResult.close();
					} catch (SQLException e) {
						Log.writeStackTrace(e);
					}
				pstmt = connection.prepareStatement(sqlSelect_NP);
				Log.info(methodName + "() :: select query: " + sqlSelect_NP);
				QueryResult = pstmt.executeQuery();
				QueryResult.next();
				nValue = QueryResult.getInt(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching Value: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Value (" + nValue + ")");
		Log.info("Exiting " + methodName + "()");
		return nValue;
	}

	/**
	 * This query fetches Product List visible during Network Stock Initiation
	 *
	 * @param NetworkCode, WalletType
	 * @throws SQLException
	 * @returns Object
	 */
	public Object[][] getProductsDetails(String NetworkCode, String WalletCode) {
		final String methodName = "getProductsDetails";
		Log.info("Entered " + methodName + "(" + NetworkCode + ", " + WalletCode + ")");
		Object[][] resultObj = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff = null;

		if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("0")) {
			sqlSelectBuff = new StringBuilder(
					"SELECT p.product_code, p.product_name, ns.stock as sal_stock, foc_stock, inc_stock ");
			sqlSelectBuff.append("FROM products p, network_product_mapping npm, network_stocks ns ");
			sqlSelectBuff.append("WHERE p.status = 'Y' AND p.product_code = npm.product_code ");
			sqlSelectBuff.append("AND p.module_code = DECODE ('C2S', 'ALL', 'C2S', 'C2S') ");
			sqlSelectBuff.append("AND npm.product_code = ns.product_code(+) AND npm.network_code = ? ");
			sqlSelectBuff.append("AND npm.status = 'Y' AND npm.network_code = ns.network_code(+) ");
			sqlSelectBuff.append("AND ns.network_code_for(+) = ? ORDER BY product_name");
		} else if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("1")) {
			sqlSelectBuff = new StringBuilder("SELECT p.product_code, p.product_name, ns.wallet_balance ");
			sqlSelectBuff.append("FROM products p, network_product_mapping npm, network_stocks ns ");
			sqlSelectBuff.append("WHERE p.status = 'Y' AND p.product_code = npm.product_code ");
			sqlSelectBuff.append("AND p.module_code = DECODE ('C2S', 'ALL', 'C2S', 'C2S') ");
			sqlSelectBuff.append("AND npm.product_code = ns.product_code(+) AND npm.network_code = ? ");
			sqlSelectBuff.append("AND npm.status = 'Y' AND npm.network_code = ns.network_code(+) ");
			sqlSelectBuff.append("AND ns.network_code_for(+) = ? AND ns.wallet_type = ? ");
			sqlSelectBuff.append("ORDER BY product_name");
		}
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("0")) {
				pstmt.setString(1, NetworkCode);
				pstmt.setString(2, NetworkCode);
			} else if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("1")) {
				pstmt.setString(1, NetworkCode);
				pstmt.setString(2, NetworkCode);
				pstmt.setString(3, WalletCode);
			}
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.last();
			int rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();
			ResultSetMetaData meta = QueryResult.getMetaData();
			resultObj = new Object[rowCount][3];
			if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("1")) {
				int i = 0;
				while (QueryResult.next()) {
					for (int j = 0, k = 1; k <= 3; j++, k++) {
						resultObj[i][j] = QueryResult.getObject(k);
					}
					i++;
				}
			} else if (_masterVO.getClientDetail("NETWORKSTOCKS_DBVER").equalsIgnoreCase("0")) {

				int i = 0;
				while (QueryResult.next()) {
					resultObj[i][0] = QueryResult.getString("product_code");
					resultObj[i][1] = QueryResult.getString("product_name");
					if (WalletCode.equalsIgnoreCase(PretupsI.SALE_WALLET_LOOKUP))
						resultObj[i][2] = QueryResult.getString("sal_stock");
					else if (WalletCode.equalsIgnoreCase(PretupsI.FOC_WALLET_LOOKUP))
						resultObj[i][2] = QueryResult.getString("foc_stock");
					else if (WalletCode.equalsIgnoreCase(PretupsI.INCENTIVE_WALLET_LOOKUP))
						resultObj[i][2] = QueryResult.getString("inc_stock");
					i++;
				}
			}
		} catch (Exception e) {
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(resultObj);
		Log.info("Exiting " + methodName + "()");
		return resultObj;
	}

	/**
	 * Query to fetch User Name of a User from users table
	 *
	 * @author krishan.chawla
	 * @return: USERNAME
	 */
	public String getUserNameByLogin(String LoginID) {
		final String methodName = "getUserNameByLogin";
		Log.info("Entered " + methodName + "(" + LoginID + ")");
		String UserName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select USER_NAME from users where LOGIN_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LoginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			UserName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching UserName: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: USER_NAME (" + UserName + ")");
		Log.info("Exiting " + methodName + "()");
		return UserName;
	}

	/**
	 * Query for fetching Product Name using Product Code
	 *
	 * @author krishan.chawla
	 */
	public String getProductNameByCode(String ProductCode) {
		final String methodName = "getProductNameByCode";
		Log.info("Entered " + methodName + "(" + ProductCode + ")");
		String ProductName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select LOOKUP_NAME from LOOKUPS where LOOKUP_CODE= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, ProductCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ProductName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching UserName: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PRODUCT_NAME (" + ProductName + ")");
		Log.info("Exiting " + methodName + "()");
		return ProductName;
	}

	public String getProductCodeByShortCode(String shortCode) {
		final String methodName = "getProductCodeByShortCode";
		Log.info("Entered " + methodName + "(" + shortCode + ")");
		String ProductCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select PRODUCT_CODE from PRODUCTS where PRODUCT_SHORT_CODE= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, shortCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ProductCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching UserName: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PRODUCT_Code (" + ProductCode + ")");
		Log.info("Exiting " + ProductCode + "()");
		return ProductCode;
	}

	/**
	 * Query for checking if provided Domain Code / Name / Shortname is unique &
	 * does not exist in system.
	 *
	 * @return: REC_EXIST
	 * @author ayush.abhijit Scope: UniqueChecker.java
	 */
	public String checkUniquePrefix(String prefix) {
		final String methodName = "checkUniquePrefix";
		Log.info("Entered " + methodName + "(" + prefix + ")");
		String REC_EXIST = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from NETWORK_PREFIXES where SERIES= ? and STATUS='Y') "); // issue found while
																									// Argentina
																									// execution
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, prefix);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			REC_EXIST = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Unique Prefix Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Unique Prefix (" + REC_EXIST + ")");
		Log.info("Exiting " + methodName + "()");
		return REC_EXIST;
	}

	public List<String> getInterfaceList(String network) {
		final String methodName = "getInterfaceList";
		Log.info("Entered " + methodName + "(" + network + ")");
		List<String> interfaces = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT i.interface_description, l.lookup_name ");
		sqlSelectBuff.append("FROM interfaces i, interface_types it, lookups l WHERE i.status != 'N' ");
		sqlSelectBuff.append(
				"AND i.interface_id NOT IN (SELECT interface_id FROM interface_network_mapping WHERE network_code = ?) ");
		sqlSelectBuff.append("AND i.interface_type_id = it.interface_type_id ");
		sqlSelectBuff.append("AND it.interface_type_id = l.lookup_code AND l.lookup_type = 'INTCT' ");
		sqlSelectBuff.append("AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, network);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			interfaces = new ArrayList<String>();
			while (QueryResult.next()) {
				interfaces.add(
						QueryResult.getString("lookup_name") + ",," + QueryResult.getString("interface_description"));
			}
			Log.info("Database Returns: Interfaces (" + interfaces + ")");
			Log.info("Exiting " + methodName + "()");
			return interfaces;
		} catch (Exception e) {
			Log.info("Error while fetching Unique Interface Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Interfaces (" + interfaces + ")");
		Log.info("Exiting " + methodName + "()");
		return interfaces;
	}

	public String pinPreferenceForTXN(String categoryName) {
		final String methodName = "pinPreferenceForTXN";
		Log.info("Entered " + methodName + "(" + categoryName + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String smsInterfaceAllowed = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select SMS_INTERFACE_ALLOWED from categories where category_name= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, categoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			smsInterfaceAllowed = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching allowed status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SMS_INTERFACE_ALLOWED (" + smsInterfaceAllowed + ")");
		Log.info("Exiting " + methodName + "()");
		return smsInterfaceAllowed;
	}

	/**
	 * O2C approval limits
	 */
	public String[] o2cApprovalLimits(String categoryName, String networkCode) {
		final String methodName = "o2cApprovalLimits";
		Log.info("Entered " + methodName + "(" + categoryName + ", " + networkCode + ")");
		String[] resultObj = new String[2];
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String smsInterfaceAllowed = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT first_approval_limit, second_approval_limit FROM chnl_transfer_rules ");
		sqlSelectBuff.append(
				"WHERE from_category = 'OPT' AND to_category = (Select category_code from categories where category_name= ?)");
		sqlSelectBuff.append(" AND network_code = ? ");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, categoryName);
			pstmt.setString(2, networkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				// Retrieve by column name
				resultObj[0] = QueryResult.getString("first_approval_limit");
				resultObj[1] = QueryResult.getString("second_approval_limit");
			}
			QueryResult.close();
		} catch (Exception e) {
			Log.info("Error while fetching O2C approval limits: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: FirstApprovalLimit (" + resultObj[0] + ") and SecondApprovalLimit (" + resultObj[1]
				+ ")");
		Log.info("Exiting " + methodName + "()");
		return resultObj;
	}

	/**
	 * Query to fetch CategoryDetails
	 */
	public String getCategoryDetail(String ColumnName, String categoryCode) {
		final String methodName = "getCategoryDetail";
		Log.info("Entered " + methodName + "(" + ColumnName + ", " + categoryCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String PreferenceVal = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select " + ColumnName + " from categories where category_code = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			// pstmt.setString(1, ColumnName);
			pstmt.setString(1, categoryCode);
			Log.info("Select " + ColumnName + " from categories where category_code = " + categoryCode + "");
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			PreferenceVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while category detail value");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns Returns: " + ColumnName + "(" + PreferenceVal + ")");
		Log.info("Exiting " + methodName + "()");
		return PreferenceVal;
	}

	public String getCategoryDetails(String ColumnName, String CategoryName) {
		final String methodName = "getCategoryDetail";
		Log.info("Entered " + methodName + "(" + ColumnName + ", " + CategoryName + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String PreferenceVal = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select grph_domain_type from categories where category_code = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			PreferenceVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Preference Value");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns Returns: " + ColumnName + "(" + PreferenceVal + ")");
		Log.info("Exiting " + methodName + "()");
		return PreferenceVal;
	}

	/**
	 * Unique name check for TCP
	 *
	 * @author lokesh.kontey
	 */
	public String checkForUniqueTCPName(String TCPNAME) {
		final String methodName = "checkForUniqueTCPName";
		Log.info("Entered " + methodName + "(" + TCPNAME + ")");
		String TCPNAMEStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from TRANSFER_PROFILE where PROFILE_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TCPNAME);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			TCPNAMEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching TCPNAME status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TCPNAMEStatus (" + TCPNAMEStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return TCPNAMEStatus;
	}

	public String checkForUniqueCardGroupName(String cardGroupName) {
		final String methodName = "checkForUniqueCardGroupName";
		Log.info("Entered " + methodName + "(" + cardGroupName + ")");
		String CardGroupNameStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from CARD_GROUP_SET where CARD_GROUP_SET_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, cardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			CardGroupNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching CardGroupName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CardGroupNameStatus (" + CardGroupNameStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return CardGroupNameStatus;
	}

	/**
	 * Unique commission profile name
	 *
	 * @param COMMNAME
	 * @return
	 * @author lokesh.kontey
	 */

	public String checkForUniqueCommProfileName(String COMMNAME) {
		final String methodName = "checkForUniqueCommProfileName";
		Log.info("Entered " + methodName + "(" + COMMNAME + ")");
		String COMNAMEStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from COMMISSION_PROFILE_SET where COMM_PROFILE_SET_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, COMMNAME);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			COMNAMEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching CommissionProfileName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CPName Status (" + COMNAMEStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return COMNAMEStatus;
	}

	/**
	 * Query for fetching Commission Profile details
	 *
	 * @throws Exception
	 * @return: ResultCache
	 * @author krishan.chawla
	 * @Scope: ChannelTransfersVO
	 */
	public ResultSet getCommissionProfileDetails(String MSISDN, String ProductCode, String requestedQuantity) {
		final String methodname = "getCommissionProfileDetails";
		Log.debug("Entered " + methodname + "(" + MSISDN + ", " + ProductCode + ", " + requestedQuantity + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder();
		strBuffSelectCProfileProdDetail
				.append("SELECT u.user_id, cpd.tax1_type, cpd.tax1_rate, cpd.tax2_type, cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail
				.append("cpd.tax3_type, cpd.tax3_rate, cpd.commission_type, cpd.commission_rate, ");
		strBuffSelectCProfileProdDetail.append(
				"cpd.comm_profile_detail_id,cpp.discount_type, cpp.discount_rate, cpp.MIN_TRANSFER_VALUE, cpp.MAX_TRANSFER_VALUE, cpp.TAXES_ON_CHANNEL_TRANSFER, ");
		strBuffSelectCProfileProdDetail.append(
				"cpp.TAXES_ON_FOC_APPLICABLE FROM commission_profile_details cpd LEFT JOIN commission_profile_products cpp ");
		strBuffSelectCProfileProdDetail.append(
				"ON cpd.comm_profile_products_id = cpp.comm_profile_products_id INNER JOIN commission_profile_set cps ");
		strBuffSelectCProfileProdDetail.append(
				"ON cpp.comm_profile_set_id = cps.comm_profile_set_id AND cpp.comm_profile_set_version = cps.comm_last_version ");
		strBuffSelectCProfileProdDetail
				.append("RIGHT JOIN channel_users cu ON cpp.comm_profile_set_id = cu.comm_profile_set_id ");
		strBuffSelectCProfileProdDetail.append("RIGHT JOIN users u ON cu.user_id = u.user_id WHERE u.msisdn = ? ");
		strBuffSelectCProfileProdDetail
				.append("AND u.status != ? AND cpp.product_code = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		String sqlSelect = strBuffSelectCProfileProdDetail.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, MSISDN);
			pstmt.setString(2, "N");
			pstmt.setString(3, ProductCode);
			pstmt.setLong(4, _parser.getSystemAmount(requestedQuantity));
			pstmt.setLong(5, _parser.getSystemAmount(requestedQuantity));
			Log.debug(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(ReturnCachedResult);
		Log.debug("Exited " + methodname + "()");
		return ReturnCachedResult;
	}

	/**
	 * Check for Web Access for categories
	 *
	 * @param categoryName
	 * @return
	 * @author lokesh.kontey
	 */

	public String webInterface(String categoryName) {
		final String methodname = "webInterface";
		Log.info("Entered " + methodname + "(" + categoryName + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String webInterfaceAllowed = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select WEB_INTERFACE_ALLOWED from categories where category_name= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, categoryName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			webInterfaceAllowed = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching allowed status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: WEB_INTERFACE_ALLOWED (" + webInterfaceAllowed + ")");
		Log.info("Exited " + methodname + "()");
		return webInterfaceAllowed;
	}

	/**
	 * Unique Grade name
	 *
	 * @param GRADENAME
	 * @return
	 * @author lokesh.kontey
	 */

	public String checkForUniqueGradeName(String GRADENAME) {
		final String methodname = "checkForUniqueGradeName";
		Log.info("Entered " + methodname + "(" + GRADENAME + ")");
		String GRADENAMEStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from CHANNEL_GRADES where GRADE_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, GRADENAME);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			GRADENAMEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GradeName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GradeName Status (" + GRADENAMEStatus + ")");
		Log.info("Exited " + methodname + "()");
		return GRADENAMEStatus;
	}

	/**
	 * Unique Grade code
	 *
	 * @param GRADECODE
	 * @return
	 * @author lokesh.kontey
	 */
	public String checkForUniqueGradeCode(String GRADECODE) {
		final String methodname = "checkForUniqueGradeCode";
		Log.info("Entered " + methodname + "(" + GRADECODE + ")");
		String GRADECODEStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from CHANNEL_GRADES where GRADE_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, GRADECODE);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			GRADECODEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GradeCode status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GradeCode Status (" + GRADECODEStatus + ")");
		Log.info("Exited " + methodname + "()");
		return GRADECODEStatus;
	}

	/**
	 * Query for fetching Product Unit value
	 *
	 * @return: unitValue
	 * @author krishan.chawla
	 * @Scope: ChannelTransfersVO
	 */
	public Long getProductUnitValue(String ProductCode) {
		final String methodname = "getProductUnitValue";
		Log.debug("Entered " + methodname + "(" + ProductCode + ")");
		long unitValue = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		final StringBuilder sqlSelectBuff = new StringBuilder("SELECT unit_value from PRODUCTS where product_code = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, ProductCode);
			Log.debug(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			unitValue = Long.parseLong(QueryResult.getString(1));
		} catch (Exception e) {
			Log.info("Exception while fetching Unit Value for Product: " + ProductCode);
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug("Database Returns: Unit Value (" + unitValue + ")");
		Log.debug("Exited " + methodname + "()");
		return unitValue;
	}

	/**
	 * Query for fetching Ambiguous Transfers
	 *
	 * @return:
	 * @author simarnoor.bains
	 */
	public String fetchAmbiguousTransactions(String fromDate, String toDate, String selectorType) {
		final String methodname = "fetchAmbiguousTransactions";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + selectorType + ")");
		String transactionID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		String DATE_FORMAT = "dd/MM/yy";
		/*
		 * if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
		 * DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA; }
		 */

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT * FROM (Select transfer_id, TRANSFER_DATE_TIME from c2s_transfers where transfer_status IN ('205', '250') ");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?,'nls_calendar=gregorian') ");
		sqlSelectBuff.append(
				"and transfer_Date>=TO_DATE(?, ?,'nls_calendar=gregorian') and service_type = ? ORDER BY TRANSFER_DATE_TIME DESC) WHERE  ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			pstmt.setString(5, selectorType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transactionID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransactionID(" + transactionID + ")");
		Log.info("Exited " + methodname + "()");
		return transactionID;
	}

	/**
	 * Query for fetching Ambiguous Transfers
	 *
	 * @return:
	 * @author chetan.chawla
	 */
	public String fetchAmbiguousO2CPendingTransactions(String fromDate, String toDate) {
		final String methodname = "fetchAmbiguousO2CPendingTransactions";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ")");
		String transactionID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		String DATE_FORMAT = "dd/mm/yy";
		/*
		 * if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
		 * DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA; }
		 */

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT * FROM (Select TRANSFER_ID, TRANSFER_DATE from CHANNEL_TRANSFERS where STATUS LIKE 'PENDING' ");
		sqlSelectBuff.append("and PMT_INST_TYPE ='ONLINE' ");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?,'nls_calendar=gregorian') ");
		sqlSelectBuff.append(
				"and transfer_Date>=TO_DATE(?, ?,'nls_calendar=gregorian') ORDER BY TRANSFER_DATE DESC) WHERE  ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transactionID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_status");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransactionID(" + transactionID + ")");
		Log.info("Exited " + methodname + "()");
		return transactionID;
	}

	/**
	 * Query for checking Ambiguous Transfers
	 *
	 * @return:
	 * @author simarnoor.bains
	 */
	public boolean checkAmbiguousTransactions(String fromDate, String toDate, String service) {
		final String methodname = "checkAmbiguousTransactions";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + service + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		int result = 0;

		String DATE_FORMAT = "dd/MM/yy";
		/*
		 * if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
		 * DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA; }
		 */

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT count(*) FROM c2s_transfers WHERE transfer_status IN ('205', '250') ");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?,'nls_calendar=gregorian') ");
		sqlSelectBuff.append("and transfer_Date>=TO_DATE(?, ?,'nls_calendar=gregorian') and service_type = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			pstmt.setString(5, service);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			result = Integer.parseInt(QueryResult.getString(1));
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status(" + result + ")");
		Log.info("Exited " + methodname + "()");
		if (result == 0)
			return true;
		else
			return false;
	}

	/**
	 * Query for checking Ambiguous o2c Transfers in pending state
	 *
	 * @return:
	 * @author chetan.chawla
	 */
	public boolean checkAmbiguousO2CPendingTransactions(String fromDate, String toDate) {
		final String methodname = "checkAmbiguousO2CPendingTransactions";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		int result = 0;

		String DATE_FORMAT = "dd/MM/yy";
		/*
		 * if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
		 * DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA; }
		 */

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT count(*) FROM CHANNEL_TRANSFERS WHERE STATUS LIKE 'PENDING' ");
		sqlSelectBuff.append("and PMT_INST_TYPE ='ONLINE'");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?,'nls_calendar=gregorian') ");
		sqlSelectBuff.append("and transfer_Date>=TO_DATE(?, ?,'nls_calendar=gregorian') ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			result = Integer.parseInt(QueryResult.getString(1));
		} catch (Exception e) {
			Log.info("Error while fetching transfer_status");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status(" + result + ")");
		Log.info("Exited " + methodname + "()");
		if (result == 0)
			return true;
		else
			return false;
	}

	/**
	 * Query for fetching transferStatus
	 *
	 * @return:
	 * @author simarnoor.bains
	 */
	public String fetchTransferStatus(String transactionID) {
		final String methodname = "fetchTransferStatus";
		Log.info("Entered " + methodname + "(" + transactionID + ")");
		String transferStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select transfer_status from c2s_transfers where transfer_id = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transferStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransferStatus(" + transferStatus + ")");
		Log.info("Exited " + methodname + "()");
		return transferStatus;
	}

	/**
	 * Query for fetching transferStatus
	 *
	 * @return:
	 * @author yogesh.dixit
	 */
	public String fetchTransferStatusO2C(String transactionID) {
		final String methodname = "fetchTransferStatus";
		Log.info("Entered " + methodname + "(" + transactionID + ")");
		String transferStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select STATUS from CHANNEL_TRANSFERS where TRANSFER_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transferStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransferStatus(" + transferStatus + ")");
		Log.info("Exited " + methodname + "()");
		return transferStatus;
	}

	public String fetchTransferIdWithStatus(String status, String subType, String type) {
		final String methodname = "fetchTransferIdWithStatus";
		Log.info("Entered " + methodname + "(" + status + ")");
		String transferID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select TRANSFER_ID from CHANNEL_TRANSFERS where STATUS = ? AND TRANSFER_SUB_TYPE = ? AND TYPE = ? ORDER BY TRANSFER_DATE DESC");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			pstmt.setString(2, subType);
			pstmt.setString(3, type);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transferID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transaction_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransferID(" + transferID + ")");
		Log.info("Exited " + methodname + "()");
		return transferID;
	}

	public String getCommProfileVersion(String profileName) {
		final String methodname = "getCommProfileVersion";
		Log.info("Entered " + methodname + "(" + profileName + ")");
		String version = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT Max(To_number(COMM_PROFILE_SET_VERSION)) FROM COMMISSION_PROFILE_SET_VERSION WHERE COMM_PROFILE_SET_ID =");
		sqlSelectBuff
				.append("(select COMM_PROFILE_SET_ID from COMMISSION_PROFILE_SET where COMM_PROFILE_SET_NAME= ? )");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, profileName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			version = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + version + ")");
		Log.info("Exited " + methodname + "()");
		return version;
	}

	/**
	 * Query to fetch deleted MSISDN randomly
	 *
	 * @author lokesh.kontey
	 * @return: MSISDN
	 */
	public String deletedMSISDN() {
		final String methodname = "deletedMSISDN";
		Log.info("Entered " + methodname + "()");
		String MSISDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select MSISDN from ");
		sqlSelectBuff.append("(SELECT u.msisdn, u.status ");
		sqlSelectBuff.append(
				"FROM users u, (SELECT * FROM (SELECT   COUNT (msisdn) AS counter, msisdn FROM users WHERE user_type <> 'OPERATOR' AND network_code='"
						+ _masterVO.getMasterValue("Network Code") + "' GROUP BY msisdn) WHERE counter = 1) wer ");
		sqlSelectBuff.append("WHERE u.msisdn = wer.msisdn AND u.status = 'N' order by dbms_random.value()) ");
		sqlSelectBuff.append("where rownum='1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDN (" + MSISDN + ")");
		Log.info("Exited " + methodname + "()");
		return MSISDN;
	}

	/**
	 * Query for fetching Product Name using Product Type
	 *
	 * @author simarnoor.bains
	 */
	public ResultSet getProductNameByType(String lookupType) {
		final String methodname = "getProductNameByType";
		Log.info("Entered " + methodname + "(" + lookupType + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select LOOKUP_NAME from LOOKUPS where LOOKUP_TYPE= ? and status = 'Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, lookupType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(ReturnCachedResult);
		Log.info("Exited " + methodname + "()");
		return ReturnCachedResult;
	}

	public ArrayList<String> getVoucherProductCodeList() {
		final String methodName = "getVoucherProductCodeList";
		Log.info("Entered " + methodName + "()");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		ArrayList<String> productList = new ArrayList<>();

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select LOOKUP_CODE from LOOKUPS where LOOKUP_TYPE= 'VMSPT' and status = 'Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				productList.add(QueryResult.getString("LOOKUP_CODE"));
			}

		} catch (Exception e) {
			Log.info("Exception while populating Voucher Product List: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Exited " + methodName + "() :: productList= " + Arrays.asList(productList));
		return productList;
	}

	/**
	 * Query for fetching Control Preferences according to the selected Network
	 *
	 * @throws SQLException
	 * @param: Network Code, Preference_Code, control_code
	 * @return: Default Value
	 * @author lokesh.kontey
	 */
	@SuppressWarnings("resource")
	public String getPreference(String Control_Code, String Network_Code, String Preference_Code) {
		String methodname = "getPreference";
		Log.info("Entered " + methodname + "(" + Control_Code + ", " + Network_Code + ", " + Preference_Code + ")");

		String prefVal = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();

			StringBuffer QueryBuffer = new StringBuffer("select VALUE from SERVICE_CLASS_PREFERENCES where ");
			QueryBuffer.append("NETWORK_CODE = '" + Network_Code + "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
			QueryResult = statement.executeQuery(QueryBuffer.toString());

			if (!QueryResult.isBeforeFirst()) {
				Log.info("Service Class Preference Not Found");
				Log.info("Trying to get Preference value from Control Preferences");

				QueryBuffer = new StringBuffer("select VALUE from CONTROL_PREFERENCES where ");
				QueryBuffer.append("CONTROL_CODE = '" + Control_Code + "' AND NETWORK_CODE = '" + Network_Code
						+ "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
				QueryResult = statement.executeQuery(QueryBuffer.toString());

				if (!QueryResult.isBeforeFirst()) {
					Log.info("Control Preference Not Found");
					Log.info("Trying to get Preference value from Network Preferences");
					QueryBuffer = new StringBuffer("select VALUE from NETWORK_PREFERENCES where ");
					QueryBuffer.append(
							"NETWORK_CODE = '" + Network_Code + "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
					QueryResult = statement.executeQuery(QueryBuffer.toString());
					if (!QueryResult.isBeforeFirst()) {
						Log.info("Network Preference Not Found");
						Log.info("Trying to get Preference value from System Preferences");
						QueryBuffer = new StringBuffer("select DEFAULT_VALUE from SYSTEM_PREFERENCES where ");
						QueryBuffer.append("PREFERENCE_CODE = '" + Preference_Code + "'");
						QueryResult = statement.executeQuery(QueryBuffer.toString());
					}
				}
			}
			QueryResult.next();
			prefVal = QueryResult.getString(1).toString();
		} catch (Exception e) {
			Log.info("Error while fetching Preference");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: DEFAULT_VALUE (" + prefVal + ")");
		return prefVal;
	}

	/**
	 * Query for fetching Domain Name
	 *
	 * @return:
	 * @author simarnoor.bains
	 */
	public String fetchDomainName(String domain, String networkCode, String parentGeography) {
		final String methodname = "fetchDomainName";
		Log.info("Entered " + methodname + "(" + domain + ", " + networkCode + ", " + parentGeography + ")");
		String domainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select GRPH_DOMAIN_NAME from GEOGRAPHICAL_DOMAINS where IS_DEFAULT = 'N' AND STATUS = 'Y' AND NETWORK_CODE = ? ");
		sqlSelectBuff.append(
				"AND parent_grph_domain_code = (select GRPH_DOMAIN_CODE from GEOGRAPHICAL_DOMAINS where grph_domain_name = ?) AND rownum = '1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, networkCode);
			pstmt.setString(2, parentGeography);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Domain Name(" + domainName + ")");
		Log.info("Exited " + methodname + "()");
		return domainName;
	}

	/**
	 * Query for fetching Domain Name Code from Domain Name
	 *
	 * @return:
	 * @author simarnoor.bains
	 */
	public String fetchDomainNameCode(String domainType) {
		final String methodname = "fetchDomainNameCode";
		Log.info("Entered " + methodname + "(" + domainType + ")");
		String domainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT GRPH_DOMAIN_TYPE FROM GEOGRAPHICAL_DOMAIN_TYPES WHERE GRPH_DOMAIN_TYPE_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, domainType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Domain Name Code(" + domainName + ")");
		Log.info("Exited " + methodname + "()");
		return domainName;
	}

	/**
	 * Query for fetching user balance
	 *
	 * @return: balance
	 * @author lokesh.kontey
	 */
	public String getUserBalance(String productCode, String LoginID_OR_MSISDN) {
		final String methodname = "getUserBalance";
		Log.info("Entered " + methodname + "(" + productCode + ", " + LoginID_OR_MSISDN + ")");
		String usrBalance = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT balance as balance FROM user_balances WHERE product_code = ? ");
		sqlSelectBuff.append("AND user_id = (SELECT user_id FROM users WHERE login_id = ? OR MSISDN = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productCode);
			pstmt.setString(2, LoginID_OR_MSISDN);
			pstmt.setString(3, LoginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			usrBalance = QueryResult.getString("balance");
		} catch (Exception e) {
			Log.info("Error while fetching balance");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: User Balance(" + usrBalance + ")");
		Log.info("Exited " + methodname + "()");
		return usrBalance;
	}

	public String getUserBalanceWithLoginID(String LoginID_OR_MSISDN) {
		final String methodname = "getUserBalance";
		Log.info("Entered " + methodname + "(" + LoginID_OR_MSISDN + ")");
		String usrBalance = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT balance as balance FROM user_balances WHERE ");
		sqlSelectBuff.append(" user_id = (SELECT user_id FROM users WHERE login_id = ? OR MSISDN = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LoginID_OR_MSISDN);
			pstmt.setString(2, LoginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			usrBalance = QueryResult.getString("balance");
		} catch (Exception e) {
			Log.info("Error while fetching balance");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: User Balance(" + usrBalance + ")");
		Log.info("Exited " + methodname + "()");
		return usrBalance;
	}

	/**
	 * Query for fetching user balances for all available products
	 *
	 * @return: balances
	 * @author krishan.chawla
	 */
	public HashMap<String, String> getUserBalances(String LoginID_OR_MSISDN) {
		final String methodname = "getUserBalances";
		Log.info("Entered " + methodname + "(" + LoginID_OR_MSISDN + ")");
		HashMap<String, String> usrBalances = new HashMap<String, String>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT ub.product_code as product_code, ub.balance as balance FROM user_balances ub ");
		sqlSelectBuff.append("WHERE network_code = ? AND network_code_for = ? ");
		sqlSelectBuff.append("AND user_id = (select user_id from users where MSISDN = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
			pstmt.setString(2, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
			pstmt.setString(3, LoginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				System.out.println("ProductCode: " + QueryResult.getString("product_code") + " | Balance: "
						+ QueryResult.getString("balance"));
				usrBalances.put(QueryResult.getString("product_code"), QueryResult.getString("balance"));
			}
		} catch (Exception e) {
			Log.info("Error while fetching balances");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: User Balances(" + Arrays.asList(usrBalances) + ")");
		Log.info("Exited " + methodname + "()");
		return usrBalances;
	}

	public Object[][] getProductDetails(String NetworkCode, String DomainCode, String fromCategoryCode,
			String toCategoryCode, String type) {
		Object[][] resultObj = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Log.info("Entered :: " + Thread.currentThread().getStackTrace()[1].getMethodName() + "()");
			StringBuffer QueryBuffer = new StringBuffer(
					"SELECT prod.product_code, prod.product_name, prod.SHORT_NAME,prod.PRODUCT_TYPE ");
			QueryBuffer.append(
					"FROM products prod, chnl_transfer_rules_products trfprod, network_product_mapping npm, chnl_transfer_rules ctr ");
			QueryBuffer.append("WHERE trfprod.transfer_rule_id = (SELECT transfer_rule_id ");
			QueryBuffer.append("FROM chnl_transfer_rules chnltrf, categories cat1, categories cat2 ");
			QueryBuffer.append("WHERE chnltrf.network_code = '" + NetworkCode + "' AND chnltrf.domain_code = '"
					+ DomainCode + "' AND from_category = '" + fromCategoryCode + "' AND to_category = '"
					+ toCategoryCode + "' ");
			QueryBuffer.append("AND chnltrf.status = 'Y' AND TYPE = '" + type
					+ "' AND chnltrf.from_category = cat1.category_code ");
			QueryBuffer.append("AND chnltrf.to_category = cat2.category_code) ");
			QueryBuffer.append(
					"AND trfprod.product_code = prod.product_code AND ctr.transfer_rule_id = trfprod.transfer_rule_id ");
			QueryBuffer.append(
					"AND ctr.network_code = npm.network_code AND npm.product_code = trfprod.product_code AND npm.status = 'Y' ");
			QueryBuffer.append("ORDER BY prod.product_name");

			Log.info(QueryBuffer.toString());
			QueryResult = statement.executeQuery(QueryBuffer.toString());

			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				for (int j = 0, k = 1; k <= columnCount; j++, k++) {
					resultObj[i][j] = QueryResult.getObject(k);
				}
				i++;
			}
		} catch (Exception e) {
			Log.info("Exception while populating Query Result: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return resultObj;
	}

	/**
	 * Query for fetching domaincode
	 *
	 * @return: domainCode
	 * @author lokesh.kontey
	 */
	public String getDomainCode(String domainName) {
		final String methodname = "getDomainCode";
		Log.info("Entered " + methodname + "(" + domainName + ")");
		String domainCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select domain_code from domains where domain_name=?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, domainName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domainCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching domain code.");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Domain Code(" + domainCode + ")");
		Log.info("Exited " + methodname + "()");
		return domainCode;
	}

	public String getLookUpCode(String LookUpName) {
		final String methodname = "getLookUpCode";
		Log.info("Entered " + methodname + "(" + LookUpName + ")");
		String LookUpCode = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select LOOKUP_CODE from LOOKUPS where LOOKUP_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, LookUpName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			LookUpCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Look Up Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: LOOKUP_CODE (" + LookUpCode + ")");
		Log.info("Exited " + methodname + "()");
		return LookUpCode;
	}

	public String getLookUpCodeFromType(String LookUpName) {
		final String methodname = "getLookUpCode";
		Log.info("Entered " + methodname + "(" + LookUpName + ")");
		String LookUpCode = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select LOOKUP_CODE from LOOKUPS where LOOKUP_TYPE = ? and status = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, LookUpName);
			statement.setString(2, "Y");
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			LookUpCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Look Up Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: LOOKUP_CODE (" + LookUpCode + ")");
		Log.info("Exited " + methodname + "()");
		return LookUpCode;
	}

	public int getLookUpSize(String LookupType) {
		final String methodname = "getLookUpSize";
		Log.info("Entered " + methodname + "(" + LookupType + ")");
		int LookUpCode = 0;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select count(*) from LOOKUPS where LOOKUP_TYPE = ? AND STATUS = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, LookupType);
			statement.setString(2, "Y");
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			LookUpCode = QueryResult.getInt(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Look Up Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: LOOKUP Size(" + LookUpCode + ")");
		Log.info("Exited " + methodname + "()");
		return LookUpCode;
	}

	public String getLookUpName(String LookUpCode, String LookupType) {
		final String methodname = "getLookUpName";
		Log.info("Entered " + methodname + "(" + LookUpCode + ", " + LookupType + ")");
		String LookUpName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select LOOKUP_NAME from LOOKUPS where LOOKUP_CODE = ? and LOOKUP_TYPE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LookUpCode);
			pstmt.setString(2, LookupType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			LookUpName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Look Up Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: LOOKUP_NAME (" + LookUpName + ")");
		Log.info("Exited " + methodname + "()");
		return LookUpName;
	}

	/**
	 * @param SID
	 * @return
	 * @author lokesh.kontey
	 */
	public String checkForUniqueSubsSID(String SID) {
		final String methodname = "checkForUniqueSubsSID";
		Log.info("Entered " + methodname + "(" + SID + ")");
		String SIDStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from SUBSCRIBER_MSISDN_ALIAS where USER_SID = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, SID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			SIDStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching SID status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SIDStatus (" + SIDStatus + ")");
		Log.info("Exited " + methodname + "()");
		return SIDStatus;
	}

	/**
	 * get SID using MSISDN of subsriber
	 *
	 * @param subsMSISDN
	 * @return
	 * @author lokesh.kontey
	 */
	public String getsubscriberSIDviaMSISDN(String subsMSISDN) {
		final String methodname = "getsubscriberSIDviaMSISDN";
		Log.info("Entered " + methodname + "(" + subsMSISDN + ")");
		String subsSID = null;
		String sysSID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT USER_SID FROM subscriber_msisdn_alias WHERE msisdn = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, subsMSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();

			sysSID = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"),
					CONSTANT.SID_ENCRYPTION_ALLOWED);

			if (sysSID != null && sysSID.equalsIgnoreCase("TRUE")) {
				subsSID = Decrypt.decryption(QueryResult.getString(1).toString());
			} else {
				subsSID = QueryResult.getString(1);
			}

			Log.info("SID_ENCRYPTION_ALLOWED:[" + sysSID + "], SUBSCRIBER_SID:[" + subsSID + "]");
		} catch (Exception e) {
			Log.info("Exception while fetching subscriber SID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: USER_SID (" + subsSID + ")");
		Log.info("Exited " + methodname + "()");
		return subsSID;
	}

	public String fetchSubscriberMSISDNRandomAlias(String msisdnType) {
		final String methodname = "fetchSubscriberMSISDNRandomAlias";
		Log.info("Entered " + methodname + "()");
		String subsMSISDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = null;
		if (msisdnType.equalsIgnoreCase("PRE")) {
			sqlSelectBuff = new StringBuilder(
					"SELECT MSISDN FROM (Select MSISDN from subscriber_msisdn_alias where MSISDN like '"
							+ _masterVO.getMasterValue("Subscriber Prepaid Prefix")
							+ "%'order by dbms_random.value) WHERE rownum = '1' ");
		} else if (msisdnType.equalsIgnoreCase("POST")) {
			sqlSelectBuff = new StringBuilder(
					"SELECT MSISDN FROM (Select MSISDN from subscriber_msisdn_alias where MSISDN like '"
							+ _masterVO.getMasterValue("Subscriber Postpaid Prefix")
							+ "%'order by dbms_random.value) WHERE rownum = '1' ");
		}
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			subsMSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching subscriber MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: subscriberMSISDN (" + subsMSISDN + ")");
		Log.info("Exited " + methodname + "()");
		return subsMSISDN;

	}

	public Object[][] getProductDetailsForC2S(String login_id, String service_type) {
		final String methodname = "getProductDetailsForC2S";
		Log.info("Entered " + methodname + "(" + login_id + ", " + service_type + ")");
		Object[][] resultObj = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT PS.SERVICE_TYPE,UB.BALANCE,P.PRODUCT_CODE,UB.balance_type,UB.network_code,P.PRODUCT_NAME, P.SHORT_NAME ");
		sqlSelectBuff.append("FROM USER_BALANCES UB, PRODUCT_SERVICE_TYPE_MAPPING PS,PRODUCTS P, USER_SERVICES US ");
		sqlSelectBuff.append(
				"WHERE UB.user_id=(SELECT user_id from users where login_id= ? ) AND UB.user_id=US.user_id AND US.SERVICE_TYPE=PS.SERVICE_TYPE ");
		sqlSelectBuff.append("AND PS.PRODUCT_TYPE=P.PRODUCT_TYPE AND P.PRODUCT_CODE=UB.PRODUCT_CODE ");
		sqlSelectBuff.append("AND US.service_type= ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, login_id);
			pstmt.setString(2, service_type);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				for (int j = 0, k = 1; k <= columnCount; j++, k++) {
					resultObj[i][j] = QueryResult.getObject(k);
				}
				i++;
			}
		} catch (Exception e) {
			Log.info("Exception while populating Query Result: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(resultObj);
		Log.info("Exited " + methodname + "()");
		return resultObj;
	}

	/**
	 * To get Name from systemPreference
	 *
	 * @param preferenceCode
	 * @return
	 */
	public String getNamefromSystemPreference(String preferenceCode) {
		final String methodname = "getNamefromSystemPreference";
		Log.info("Entered " + methodname + "(" + preferenceCode + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select NAME from SYSTEM_PREFERENCES where PREFERENCE_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, preferenceCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			preferenceName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PREFERENCE_NAME (" + preferenceName + ")");
		Log.info("Exited " + methodname + "()");
		return preferenceName;
	}

	/**
	 * to get transfer profile details
	 */
	public Object[][] getTransferProfileDetails() {
		final String methodname = "getTransferProfileDetails";
		Log.info("Entered " + methodname + "()");
		Object[][] resultObj = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT tpp.profile_id,tpp.product_code,GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
		sqlSelectBuff.append("GREATEST(tpp.c2s_min_txn_amt,catpp.c2s_min_txn_amt) c2s_min_txn_amt, ");
		sqlSelectBuff.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance, ");
		sqlSelectBuff.append(
				"LEAST(tpp.c2s_max_txn_amt,catpp.c2s_max_txn_amt) c2s_max_txn_amt,tpp.alerting_balance, LEAST(tpp.max_pct_transfer_allowed,catpp.max_pct_transfer_allowed) max_pct_transfer_allowed ");
		sqlSelectBuff.append(
				" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
		sqlSelectBuff.append(" WHERE  tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		sqlSelectBuff.append(
				" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				for (int j = 0, k = 1; k <= columnCount; j++, k++) {
					resultObj[i][j] = QueryResult.getObject(k);
				}
				i++;
			}
		} catch (Exception e) {
			Log.info("Exception while populating Query Result: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(resultObj);
		Log.info("Exited " + methodname + "()");
		return resultObj;
	}

	public String getChannelUserStatus(String userID) {
		final String methodName = "getChannelUserStatus";
		Log.info("Entered " + methodName + "(" + userID + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select AUTO_O2C_ALLOW from CHANNEL_USERS where USER_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			userID = QueryResult.getString("AUTO_O2C_ALLOW");
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: User ID(" + userID + ")");
		Log.info("Exiting " + methodName + "()");
		return userID;
	}

	public String getUserThresholdStatus(String userID, String type, String productCode) {
		final String methodName = "getUserThresholdStatus";
		Log.info("Entered " + methodName + "(" + userID + ", " + type + ", " + productCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String recordType = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select RECORD_TYPE from USER_THRESHOLD_COUNTER where USER_ID = ?");
		sqlSelectBuff.append(" AND TYPE = ? AND PRODUCT_CODE = ? order by ENTRY_DATE desc ");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			pstmt.setString(2, type);
			pstmt.setString(3, productCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			recordType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Threshold Status(" + recordType + ")");
		Log.info("Exiting " + methodName + "()");
		return recordType;
	}

	public String getCategoryCode(String categoryName) {
		final String methodName = "getCategoryCode";
		Log.info("Entered " + methodName + "(" + categoryName + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String categoryCode = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select CATEGORY_CODE from CATEGORIES where CATEGORY_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, categoryName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			categoryCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CategoryCode(" + categoryCode + ")");
		Log.info("Exiting " + methodName + "()");
		return categoryCode;
	}

	public String getExecutedDate(String processID) {
		final String methodName = "getExecutedDate";
		Log.info("Entered " + methodName + "(" + processID + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String date = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EXECUTED_ON from PROCESS_STATUS where PROCESS_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, processID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			date = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Date(" + date + ")");
		Log.info("Exiting " + methodName + "()");
		return date;
	}

	public HashMap<String, String> getTCPDetails(String profileID, String parentProfileID, String productCode,
			String... details) {
		final String methodName = "getTCPDetails";
		Log.info("Entered " + methodName + "(" + profileID + ", " + parentProfileID + ", " + productCode + ", "
				+ Arrays.toString(details) + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String date = null;
		HashMap<String, String> hashmap = new HashMap<>();

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT tpp.product_code,prod.product_short_code,prod.product_name,");
		sqlSelectBuff.append("GREATEST( tpp.min_residual_balance, ctpp.min_residual_balance) min_residual_balance,");
		sqlSelectBuff.append("GREATEST(  tpp.c2s_min_txn_amt,ctpp.c2s_min_txn_amt)c2s_min_txn_amt,");
		sqlSelectBuff.append("LEAST(tpp.max_balance,ctpp.max_balance)max_balance,");
		sqlSelectBuff.append("LEAST(tpp.c2s_max_txn_amt,ctpp.c2s_max_txn_amt)c2s_max_txn_amt,");
		sqlSelectBuff.append("LEAST(tpp.alerting_balance,ctpp.alerting_balance)alerting_balance,");
		sqlSelectBuff
				.append("LEAST(tpp.max_pct_transfer_allowed,ctpp.max_pct_transfer_allowed)max_pct_transfer_allowed ");
		sqlSelectBuff.append(
				"FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products ctpp,products prod ");
		sqlSelectBuff
				.append("WHERE tpp.profile_id=? AND tpp.profile_id=tp.profile_id AND catp.profile_id=ctpp.profile_id ");
		sqlSelectBuff.append("AND tpp.product_code=ctpp.product_code AND tp.category_code=catp.category_code ");
		sqlSelectBuff
				.append("AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code ");
		sqlSelectBuff.append(
				"AND tpp.product_code=prod.product_code AND ctpp.product_code=prod.product_code and tpp.product_code= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, profileID);
			pstmt.setString(2, parentProfileID);
			pstmt.setString(3, productCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			if (QueryResult != null) {
				for (int i = 0; i < details.length; i++) {
					String columnName = details[i];
					hashmap.put(details[i], QueryResult.getString(columnName));
				}
			}
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns : " + Arrays.asList(hashmap));
		Log.info("Exiting " + methodName + "()");
		return hashmap;
	}

	public String getTransactionStatusByKey(String Key, String Type) {
		final String methodName = "getTransactionStatusByKey";
		Log.info("Entered " + methodName + "(" + Key + ", " + Type + ")");
		String value = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select VALUE from KEY_VALUES where TYPE = ? AND KEY = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Type);
			pstmt.setString(2, Key);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Transaction Status as (" + value + ")");
		Log.info("Exiting " + methodName + "()");
		return value;
	}

	public String checkDateExistinCurrentmonth(String date) {
		final String methodName = "checkDateExistinCurrentmonth";
		Log.info("Entered " + methodName + "(" + date + ")");
		String validation = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff = new StringBuilder();
		String tempDate = BTSLDateUtil.getSystemLocaleDate(date);
		sqlSelectBuff.append("SELECT CASE WHEN TO_DATE(TRUNC (sysdate, 'mm')) <= TO_DATE(?,'");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(tempDate) + "')");
//	    sqlSelectBuff.append(BTSLDateUtil.getDateFormat(BTSLDateUtil.getDateStringInFormat(date, PretupsI.DATE_FORMAT)) + "')");
		sqlSelectBuff.append(" AND TO_DATE(?,'");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(tempDate) + "')");
//	    sqlSelectBuff.append(BTSLDateUtil.getDateFormat(BTSLDateUtil.getDateStringInFormat(date, PretupsI.DATE_FORMAT)) + "')");
		sqlSelectBuff.append(" <= TO_DATE(LAST_DAY (TRUNC (sysdate, 'mm')) + 1 - 1/86400)");
		sqlSelectBuff.append("THEN 'TRUE' ELSE 'FALSE' END EXISTINCURRENTMONTH FROM DUAL");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, tempDate);
			pstmt.setString(2, tempDate);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			validation = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Checking for date: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Validation as (" + validation + ")");
		Log.info("Exiting " + methodName + "()");
		return validation;
	}

	public String checkDateExistinCurrentweek(String date) {
		final String methodName = "checkDateExistinCurrentweek";
		Log.info("Entered " + methodName + "(" + date + ")");
		String validation = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String tempDate = BTSLDateUtil.getSystemLocaleDate(date);
		StringBuilder sqlSelectBuff = new StringBuilder();
		sqlSelectBuff.append("SELECT CASE WHEN TO_DATE(TRUNC (sysdate, 'iw')) <= TO_DATE(?,'");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(tempDate) + "')");
		sqlSelectBuff.append("AND TO_DATE(?,'");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(tempDate) + "')");
		sqlSelectBuff.append(" <= TO_DATE (TRUNC (TRUNC (SYSDATE, 'iw') + 7 - 1 / 86400))");
		sqlSelectBuff.append("THEN 'TRUE' ELSE 'FALSE' END EXISTINCURRENTWEEK FROM DUAL");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, tempDate);
			pstmt.setString(2, tempDate);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			validation = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Checking for date: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Validation as (" + validation + ")");
		Log.info("Exiting " + methodName + "()");
		return validation;
	}

	public String checkDateIsCurrentdate(String date) {
		final String methodName = "checkDateIsCurrentdate";
		Log.info("Entered " + methodName + "(" + date + ")");
		String validation = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		PreparedStatement pstmt1 = null;
		ResultSet QueryResult1 = null;
		Date date1 = null;
		date = BTSLDateUtil.getSystemLocaleDate(date);
		StringBuilder sqlSelectBuff = new StringBuilder();
		sqlSelectBuff.append("SELECT (CASE WHEN (TRUNC (SYSDATE)) - TO_DATE (?, '");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(date));
		sqlSelectBuff.append("') = 0 ");
		sqlSelectBuff.append("THEN 'TRUE' ELSE 'FALSE' END), (TRUNC(SYSDATE)) , TO_DATE(?,'");
		sqlSelectBuff.append(BTSLDateUtil.getDateFormat(date));
		sqlSelectBuff.append("') FROM DUAL");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, date);
			pstmt.setString(2, date);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			validation = QueryResult.getString(1);
			System.out.println(QueryResult.getString(2));
			System.out.println(QueryResult.getString(3));
			Log.info("Validation: " + validation);

		} catch (Exception e) {
			Log.info("Exception while fetching Checking for date: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Validation as (" + validation + ")");
		Log.info("Exiting " + methodName + "()");
		return validation;
	}

	/**
	 * Method to get values from user_transfer_counts table
	 *
	 * @param username : name given to the user
	 * @param type     : "in" or "out"
	 * @return String[]
	 */
	public String[] getusertransfercountvalues(String username, String type) {
		final String methodName = "getusertransfercountvalues";
		Log.info("Entered " + methodName + "(" + username + "," + type + ")");
		String[] values = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff = new StringBuilder("SELECT last_" + type + "_time, daily_" + type
				+ "_count, weekly_" + type + "_count, monthly_" + type + "_count FROM user_transfer_counts ");
		sqlSelectBuff
				.append("WHERE user_id IN (SELECT user_id FROM users WHERE user_name = ? and user_type='CHANNEL')");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, username);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			values = new String[] { QueryResult.getDate("last_" + type + "_time").toString(),
					QueryResult.getString("daily_" + type + "_count"),
					QueryResult.getString("weekly_" + type + "_count"),
					QueryResult.getString("monthly_" + type + "_count") };
		} catch (Exception e) {
			Log.info("Exception while fetching data from user_transfer_counts: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns : " + Arrays.toString(values));
		Log.info("Exiting " + methodName + "()");
		return values;
	}

	/**
	 * Method to get TCP Thresholds from user_transfer_counts table
	 *
	 * @return UserTransferCountsVO
	 */
	public UserTransferCountsVO getUserTransferCounts(String username_loginid_msisdn) {
		final String methodName = "getUserTransferCounts";
		Log.info("Entered " + methodName + "(" + username_loginid_msisdn + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		UserTransferCountsVO UserTransferCountsVO = new UserTransferCountsVO();

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT daily_in_count, daily_in_value, daily_out_count, daily_out_value, daily_subscriber_in_count, ");
		sqlSelectBuff.append("daily_subscriber_in_value, daily_subscriber_out_count, daily_subscriber_out_value, ");
		sqlSelectBuff.append(
				"weekly_in_count, weekly_in_value, weekly_out_count, weekly_out_value, weekly_subscriber_in_count, weekly_subscriber_in_value, ");
		sqlSelectBuff.append(
				"weekly_subscriber_out_count, weekly_subscriber_out_value, monthly_in_count, monthly_in_value, monthly_out_count, monthly_out_value, ");
		sqlSelectBuff.append(
				"monthly_subscriber_in_count, monthly_subscriber_in_value, monthly_subscriber_out_count, monthly_subscriber_out_value, last_in_time, last_out_time, ");
		sqlSelectBuff.append("last_transfer_id, last_transfer_date ");
		sqlSelectBuff.append(
				"FROM user_transfer_counts WHERE user_id IN (SELECT user_id FROM users WHERE (USER_NAME = ? OR LOGIN_ID = ? OR MSISDN = ?) and user_type='CHANNEL')");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, username_loginid_msisdn);
			pstmt.setString(2, username_loginid_msisdn);
			pstmt.setString(3, username_loginid_msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();

			UserTransferCountsVO.setDailyTransferInCount(QueryResult.getLong("daily_in_count"));
			UserTransferCountsVO.setDailyTransferInValue(QueryResult.getLong("daily_in_value"));
			UserTransferCountsVO.setDailyTransferOutCount(QueryResult.getLong("daily_out_count"));
			UserTransferCountsVO.setDailyTransferOutValue(QueryResult.getLong("daily_out_value"));
			UserTransferCountsVO.setDailySubscriberTransferInCount(QueryResult.getLong("daily_subscriber_in_count"));
			UserTransferCountsVO.setDailySubscriberTransferInValue(QueryResult.getLong("daily_subscriber_in_value"));
			UserTransferCountsVO.setDailySubscriberTransferOutCount(QueryResult.getLong("daily_subscriber_out_count"));
			UserTransferCountsVO.setDailySubscriberTransferOutValue(QueryResult.getLong("daily_subscriber_out_value"));

			UserTransferCountsVO.setWeeklyTransferInCount(QueryResult.getLong("weekly_in_count"));
			UserTransferCountsVO.setWeeklyTransferInValue(QueryResult.getLong("weekly_in_value"));
			UserTransferCountsVO.setWeeklyTransferOutCount(QueryResult.getLong("weekly_out_count"));
			UserTransferCountsVO.setWeeklyTransferOutValue(QueryResult.getLong("weekly_out_value"));
			UserTransferCountsVO.setWeeklySubscriberTransferInCount(QueryResult.getLong("weekly_subscriber_in_count"));
			UserTransferCountsVO.setWeeklySubscriberTransferInValue(QueryResult.getLong("weekly_subscriber_in_value"));
			UserTransferCountsVO
					.setWeeklySubscriberTransferOutCount(QueryResult.getLong("weekly_subscriber_out_count"));
			UserTransferCountsVO
					.setWeeklySubscriberTransferOutValue(QueryResult.getLong("weekly_subscriber_out_value"));

			UserTransferCountsVO.setMonthlyTransferInCount(QueryResult.getLong("monthly_in_count"));
			UserTransferCountsVO.setMonthlyTransferInValue(QueryResult.getLong("monthly_in_value"));
			UserTransferCountsVO.setMonthlyTransferOutCount(QueryResult.getLong("monthly_out_count"));
			UserTransferCountsVO.setMonthlyTransferOutValue(QueryResult.getLong("monthly_out_value"));
			UserTransferCountsVO
					.setMonthlySubscriberTransferInCount(QueryResult.getLong("monthly_subscriber_in_count"));
			UserTransferCountsVO
					.setMonthlySubscriberTransferInValue(QueryResult.getLong("monthly_subscriber_in_value"));
			UserTransferCountsVO
					.setMonthlySubscriberTransferOutCount(QueryResult.getLong("monthly_subscriber_out_count"));
			UserTransferCountsVO
					.setMonthlySubscriberTransferOutValue(QueryResult.getLong("monthly_subscriber_out_value"));

			UserTransferCountsVO.setLastInTime(String.valueOf(QueryResult.getDate("last_in_time")));
			UserTransferCountsVO.setLastOutTime(String.valueOf(QueryResult.getDate("last_out_time")));
			UserTransferCountsVO.setLastTransferID(QueryResult.getString("last_transfer_id"));
			UserTransferCountsVO.setLastTransferDate(String.valueOf(QueryResult.getDate("last_transfer_date")));

		} catch (Exception e) {
			Log.info("Exception while fetching data from user_transfer_counts: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Exiting " + methodName + "() :: " + UserTransferCountsVO.toString());
		return UserTransferCountsVO;
	}

	public CommissionProfileDetailsVO loadCommissionProfileDetailsForOTF(String commProfileDetailID) {
		final String methodName = "loadCommissionProfileDetailsForOTF";
		Log.debug("Entered " + methodName + "(" + commProfileDetailID + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		CommissionProfileDetailsVO commissionProfileDetailsVO = null;

		final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder(
				"SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail.append(
				"cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id,cpd.otf_applicable_from,");
		strBuffSelectCProfileProdDetail.append(
				"cpd.otf_applicable_to,cpd.otf_time_slab,cpd.comm_profile_detail_id FROM commission_profile_details cpd ");
		strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_detail_id = ? ");
		String sqlSelect = strBuffSelectCProfileProdDetail.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, commProfileDetailID);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			if (QueryResult.next()) {
				commissionProfileDetailsVO = new CommissionProfileDetailsVO();
				commissionProfileDetailsVO.setOtfApplicableFrom(QueryResult.getDate("otf_applicable_from"));
				commissionProfileDetailsVO.setOtfApplicableTo(QueryResult.getDate("otf_applicable_to"));
				commissionProfileDetailsVO.setOtfTimeSlab(QueryResult.getString("otf_time_slab"));
				commissionProfileDetailsVO.setBaseCommProfileDetailID(QueryResult.getString("comm_profile_detail_id"));
			}
		} catch (Exception e) {
			Log.info("Exception while fetching data from user_transfer_counts: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug("Exiting " + methodName + "()");
		return commissionProfileDetailsVO;
	}

	/**
	 * Method loadUserOTFCounts() This method Load the Transfer Counts by the
	 * userID.
	 *
	 * @param p_con
	 * @param p_userId
	 * @return UserOTFCountsVO
	 * @throws BTSLBaseException
	 */
	public UserOTFCountsVO loadUserOTFCounts(String receivermsisdn, String detailId, Boolean addnl) {
		final String methodname = "loadUserOTFCounts";
		Log.debug("Entered " + methodname + "(" + receivermsisdn + ", " + detailId + ", " + addnl + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		UserOTFCountsVO countsVO = null;
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(
				" SELECT user_id, prfle_otf_detail_id, otf_count, to_number(otf_value, '9999999999') AS otf_value, comm_type");
		strBuff.append(
				" from user_transfer_otf_count where user_id= (select user_id from users where msisdn = ? ) and comm_type=? and prfle_otf_detail_id in (select prfle_otf_detail_id from profile_otf_details where profile_detail_id=?)");
		strBuff.append(" order by otf_value asc");
		final String sqlSelect = strBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, receivermsisdn);
			if (addnl)
				pstmt.setString(2, PretupsI.COMM_TYPE_ADNLCOMM);
			else
				pstmt.setString(2, PretupsI.COMM_TYPE_BASECOMM);
			pstmt.setString(3, detailId);
			Log.debug(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				countsVO = new UserOTFCountsVO();
				countsVO.setUserID(QueryResult.getString("user_id"));
				if (addnl)
					countsVO.setAdnlComOTFDetailId(QueryResult.getString("prfle_otf_detail_id"));
				else
					countsVO.setBaseComOTFDetailId(QueryResult.getString("prfle_otf_detail_id"));

				countsVO.setOtfCount(QueryResult.getInt("otf_count"));
				countsVO.setOtfValue(QueryResult.getLong("otf_value"));
			}
		} catch (Exception e) {
			Log.info("Exception while fetching data from user_transfer_counts: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug("Exiting " + methodname + "()");
		return countsVO;
	}

	/**
	 * Method for select OTF details.
	 *
	 * @param p_con                         java.sql.Connection
	 * @param p_commissionProfileProductsVO CommissionProfileProductsVO
	 * @return insertCount int
	 * @throws BTSLBaseException
	 */
	public List<CommissionProfileDetailsVO> getBaseCommOtfDetails(String baseComProDetailId, boolean order) {
		final String methodname = "getBaseCommOtfDetails";
		Log.debug("Entered " + methodname + "(" + baseComProDetailId + ", " + order + ")");

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		CommissionProfileDetailsVO commissionProfileDetailsVO = null;
		final List<CommissionProfileDetailsVO> list = new ArrayList<>();

		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(
				"select pod.prfle_otf_detail_id,pod.profile_detail_id, to_number(pod.otf_value, '99999999999999') AS otf_value , pod.otf_type, pod.otf_rate from profile_otf_details pod, commission_profile_details cpd ");
		if (order) {
			strBuff.append(
					"where pod.profile_detail_id =? and cpd.comm_profile_detail_id=pod.profile_detail_id order by otf_value asc ");
		} else {
			strBuff.append(
					"where pod.profile_detail_id =? and cpd.comm_profile_detail_id=pod.profile_detail_id order by otf_value desc ");
		}
		final String sqlSelect = strBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, baseComProDetailId);
			Log.debug(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				commissionProfileDetailsVO = new CommissionProfileDetailsVO();
				commissionProfileDetailsVO.setBaseCommProfileOTFDetailID(QueryResult.getString("prfle_otf_detail_id"));
				commissionProfileDetailsVO.setBaseCommProfileDetailID(QueryResult.getString("profile_detail_id"));
				commissionProfileDetailsVO.setOtfValue(QueryResult.getLong("otf_value"));
				commissionProfileDetailsVO.setOtfTypePctOrAMt(QueryResult.getString("otf_type"));
				commissionProfileDetailsVO.setOtfRate(QueryResult.getDouble("otf_rate"));
				list.add(commissionProfileDetailsVO);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching data from user_transfer_counts: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug("Exiting " + methodname + "() :: ListSize " + list.size());
		return list;
	}

	/*
	 * Verifying the Existence of C2S Transfer ID in Adjustments Table
	 */

	public String checkForC2STRANSFER_ID(String TRANSFER_ID) {
		final String methodname = "checkForC2STRANSFER_ID";
		Log.info("Entered " + methodname + "(" + TRANSFER_ID + ")");
		String TRANSFER_IDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from ADJUSTMENTS where REFERENCE_ID = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, TRANSFER_ID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			TRANSFER_IDStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching TRANSFER_ID status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TRANSFER_ID Status (" + TRANSFER_IDStatus + ")");
		Log.info("Exited " + methodname + "()");
		return TRANSFER_IDStatus;
	}

	/* ---- Author Shubham Yadav ---- */
	public int getC2STransferRuleDeleteCount() {

		final String methodname = "getC2STransferRuleDeleteCount";
		Log.info("Entered " + methodname);
		int totalDeleteCount = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select count(*) from transfer_rules_history");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			totalDeleteCount = QueryResult.getInt(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DAILY_COUNT_LIMIT (" + totalDeleteCount + ")");
		Log.info("Exited " + methodname + "()");
		return totalDeleteCount;
	}

	/*
	 * Check Tax1 value in Adjustments table
	 */

	public long getAdditionalTax1Value(String TRANSFER_ID) {
		final String methodname = "getAdditionalTax1Value";
		Log.info("Entered " + methodname + "(" + TRANSFER_ID + ")");
		long Tax1Value = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT TAX1_VALUE FROM ADJUSTMENTS WHERE REFERENCE_ID = ? ");
		sqlSelectBuff.append("and ENTRY_TYPE = 'DR'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TRANSFER_ID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			Tax1Value = QueryResult.getLong(1);
		} catch (Exception e) {
			Log.info("Error while fetching TAX1Value: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + Tax1Value + ")");
		Log.info("Exited " + methodname + "()");
		return Tax1Value;
	}

	public HashMap<String, String> getTransactionCRDRDetails(String transactionid) {
		final String methodName = "getTransactionCRDRDetails";
		Log.info("Entered " + methodName + "(" + transactionid + ")");
		HashMap<String, String> detailsMap = new HashMap<String, String>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT cti.SENDER_DEBIT_QUANTITY, cti.RECEIVER_CREDIT_QUANTITY, cti.PAYABLE_AMOUNT, cti.NET_PAYABLE_AMOUNT ");
		sqlSelectBuff.append("FROM channel_transfers ct, channel_transfers_items cti ");
		sqlSelectBuff.append("WHERE ct.transfer_id = cti.transfer_id AND ct.transfer_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionid);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			detailsMap.put("SENDER_DEBIT_QUANTITY", QueryResult.getString("SENDER_DEBIT_QUANTITY"));
			detailsMap.put("RECEIVER_CREDIT_QUANTITY", QueryResult.getString("RECEIVER_CREDIT_QUANTITY"));
			detailsMap.put("PAYABLE_AMOUNT", QueryResult.getString("PAYABLE_AMOUNT"));
			detailsMap.put("NET_PAYABLE_AMOUNT", QueryResult.getString("NET_PAYABLE_AMOUNT"));
		} catch (Exception e) {
			Log.debug("Exception while populating HashMap for getTransactionCRDRDetails: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug("Database Returned: " + Arrays.asList(detailsMap));
		Log.debug("Exiting " + methodName + "()");
		return detailsMap;
	}

	public HashMap<String, String> getNetworkPrefixDetails(String series, String series_type) {
		final String methodname = "getAdditionalTax1Value";
		Log.info("Entered " + methodname + "(" + series + ")");
		String Tax1Value = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		HashMap<String, String> prefixDataMap = new HashMap<String, String>();
		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT * FROM network_prefixes WHERE series = ? and series_type = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, series);
			pstmt.setString(2, series_type);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			while (QueryResult.next()) {
				for (int i = 1; i <= columnCount; i++) {
					prefixDataMap.put(metadata.getColumnName(i).toLowerCase(), QueryResult.getString(i));
				}
			}
		} catch (Exception e) {
			Log.info("Error while fetching prefix details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + prefixDataMap + ")");
		Log.info("Exited " + methodname + "()");
		return prefixDataMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dbrepository.DBInterface#checkForUniqueSubLookUpName(java.lang.String)
	 */

	public String checkForUniqueSubLookUpName(String SubLookUpName) {
		final String methodname = "checkForUniqueSubLookUpName";
		Log.info("Entered " + methodname + "(" + SubLookUpName + ")");
		String SubLookUpNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from SUB_LOOKUPS where SUB_LOOKUP_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, SubLookUpName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			SubLookUpNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching SubLookUpName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SubLookUpName Status (" + SubLookUpNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return SubLookUpNameStatus;
	}

	public String getInterfaceID(String extID, String interfaceName) {
		final String methodname = "getInterfaceID";
		Log.info("Entered " + methodname + "(" + extID + "and" + interfaceName + ")");
		String InterfaceID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT INTERFACE_ID FROM INTERFACES WHERE EXTERNAL_ID = ? ");
		sqlSelectBuff.append("and INTERFACE_DESCRIPTION = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, extID);
			pstmt.setString(2, interfaceName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			InterfaceID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Interface Id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Interface ID (" + InterfaceID + ")");
		Log.info("Exited " + methodname + "()");
		return InterfaceID;
	}

	public String checkForUniqueGroupRoleName(String GroupRoleName) {
		final String methodname = "checkForUniqueGroupRoleName";
		Log.info("Entered " + methodname + "(" + GroupRoleName + ")");
		String GROUPROLENAMEStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from GROUP_ROLES where ROLE_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, GroupRoleName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			GROUPROLENAMEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GroupRoleName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GroupRoleName Status (" + GROUPROLENAMEStatus + ")");
		Log.info("Exited " + methodname + "()");
		return GROUPROLENAMEStatus;
	}

	public String checkForUniqueGroupRoleCode(String GroupRoleCode) {
		final String methodname = "checkForUniqueGroupRoleName";
		Log.info("Entered " + methodname + "(" + GroupRoleCode + ")");
		String GroupRoleCodeStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from GROUP_ROLES where GROUP_ROLE_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, GroupRoleCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			GroupRoleCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GroupRoleCode status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GroupRoleCode Status (" + GroupRoleCode + ")");
		Log.info("Exited " + methodname + "()");
		return GroupRoleCode;
	}

	public Object[][] getChnlUserDetailsForRolecode(String rolecode, String domaincode) {
		final String methodname = "getuserdetailsforrolecode";
		Log.info("Entered " + methodname + "(" + rolecode + " and " + domaincode + ")");

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select u.login_id,u.password, u.category_code,c.sequence_no from users u , categories c ");
		sqlSelectBuff.append("where u.category_code = c.category_code ");
		sqlSelectBuff.append("and u.user_id in (select user_id from user_roles where role_code=?) ");
		sqlSelectBuff.append("and u.Status='Y' and c.domain_code= ? ");
		String sqlSelect = sqlSelectBuff.toString();
		Object[][] dataOb1 = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, rolecode);
			pstmt.setString(2, domaincode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			Object[][] dataOb = new Object[allrecords][columnCount];

			while (QueryResult.next()) {
				dataOb[row][0] = QueryResult.getString(1);
				dataOb[row][1] = QueryResult.getString(2);
				dataOb[row][2] = QueryResult.getString(3);
				dataOb[row][3] = QueryResult.getString(4);
				row++;
			}

			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int newObvar = 0, records = 0;
			for (int p = 0; p < allrecords; p++) {
				for (int excelRow = 1; excelRow <= ExcelUtility.getRowCount(); excelRow++) {
					if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelRow).equals(dataOb[p][0])) {
						records++;
					}
				}
			}

			Log.info("Total records matched: " + records);
			dataOb1 = new Object[records][4];
			for (int recItrator = 0; recItrator < allrecords; recItrator++) {
				for (int excelRow = 1; excelRow <= ExcelUtility.getRowCount(); excelRow++) {
					if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelRow).equals(dataOb[recItrator][0])) {
						dataOb1[newObvar][0] = dataOb[recItrator][0];
						dataOb1[newObvar][1] = Decrypt.decryption(String.valueOf(dataOb[recItrator][1]));
						dataOb1[newObvar][2] = dataOb[recItrator][2];
						dataOb1[newObvar][3] = dataOb[recItrator][3];
						newObvar++;
					}
				}
			}
		} catch (Exception e) {
			Log.info("Error while fetching data: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Users with rolecode (" + ReflectionToStringBuilder.toString(dataOb1) + ")");
		Log.info("Exited " + methodname + "()");
		return dataOb1;
	}

	public Object[][] getChnlUserDetailsForRolecode(String rolecode, String domaincode, String categorycode) {
		final String methodname = "getuserdetailsforrolecode";
		Log.info("Entered " + methodname + "(" + rolecode + " and " + domaincode + ")");

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select u.login_id,u.password, u.category_code,c.sequence_no from users u , categories c ");
		sqlSelectBuff.append("where u.category_code = c.category_code ");
		sqlSelectBuff.append("and u.user_id in (select user_id from user_roles where role_code=?) ");
		sqlSelectBuff.append("and u.Status='Y' and c.domain_code= ? and c.category_code = ?");
		String sqlSelect = sqlSelectBuff.toString();
		Object[][] dataOb1 = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, rolecode);
			pstmt.setString(2, domaincode);
			pstmt.setString(3, categorycode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			Object[][] dataOb = new Object[allrecords][columnCount];

			while (QueryResult.next()) {
				dataOb[row][0] = QueryResult.getString(1);
				dataOb[row][1] = QueryResult.getString(2);
				dataOb[row][2] = QueryResult.getString(3);
				dataOb[row][3] = QueryResult.getString(4);
				row++;
			}

			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int newObvar = 0, records = 0;
			for (int p = 0; p < allrecords; p++) {
				for (int excelRow = 1; excelRow <= ExcelUtility.getRowCount(); excelRow++) {
					if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelRow).equals(dataOb[p][0])) {
						records++;
					}
				}
			}

			Log.info("Total records matched: " + records);
			dataOb1 = new Object[records][4];
			for (int recItrator = 0; recItrator < allrecords; recItrator++) {
				for (int excelRow = 1; excelRow <= ExcelUtility.getRowCount(); excelRow++) {
					if (ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelRow).equals(dataOb[recItrator][0])) {
						dataOb1[newObvar][0] = dataOb[recItrator][0];
						dataOb1[newObvar][1] = Decrypt.decryption(String.valueOf(dataOb[recItrator][1]));
						dataOb1[newObvar][2] = dataOb[recItrator][2];
						dataOb1[newObvar][3] = dataOb[recItrator][3];
						newObvar++;
					}
				}
			}
		} catch (Exception e) {
			Log.info("Error while fetching data: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Users with rolecode (" + ReflectionToStringBuilder.toString(dataOb1) + ")");
		Log.info("Exited " + methodname + "()");
		return dataOb1;
	}

	public String getCurrentServerDate(String dateFormat) {

		final String methodName = "getCurrentServerDate";
		Log.info("Entered " + methodName + "(" + dateFormat + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String serverDate = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT TO_CHAR(CURRENT_DATE, ?) FROM dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, dateFormat);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serverDate = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching server date: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Server Date(" + serverDate + ")");
		Log.info("Exiting " + methodName + "()");
		return serverDate;
	}

	public String checkForUniqueinterfaceName(String interfaceName) {
		final String methodname = "checkForUniqueInterfaceName";
		Log.info("Entered " + methodname + "(" + interfaceName + ")");
		String interfaceNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from INTERFACES where INTERFACE_DESCRIPTION = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, interfaceName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			interfaceNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching interfaceName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: interfaceName Status (" + interfaceNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return interfaceNameStatus;
	}

	public String checkForUniqueinterfaceExtID(String interfaceExtID) {
		final String methodname = "checkForUniqueinterfaceExtID";
		Log.info("Entered " + methodname + "(" + interfaceExtID + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from INTERFACES where EXTERNAL_ID = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, interfaceExtID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			interfaceExtIDStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching interfaceName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: interfaceExtID Status (" + interfaceExtIDStatus + ")");
		Log.info("Exited " + methodname + "()");
		return interfaceExtIDStatus;
	}

	// Unique NetworkName
	public String checkForUniqueNetworkName(String NetworkName) {
		final String methodname = "checkForUniqueNetworkName";
		Log.info("Entered " + methodname + "(" + NetworkName + ")");
		String NetworkNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from NETWORKS where NETWORK_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, NetworkName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			NetworkNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name Status (" + NetworkNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return NetworkNameStatus;
	}

	// Unique NetworkCode

	public String checkForUniqueNetworkCode(String NetworkCode) {
		final String methodname = "checkForUniqueNetworkCode";
		Log.info("Entered " + methodname + "(" + NetworkCode + ")");
		String NetworkCodeStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from NETWORKS where NETWORK_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, NetworkCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			NetworkCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching NetworkCode status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: NetworkCode Status (" + NetworkCodeStatus + ")");
		Log.info("Exited " + methodname + "()");
		return NetworkCodeStatus;
	}

	public String[] getUserDetails(String loginID_OR_MSISDN, String... columnNames) {
		final String methodname = "getUserDetails";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + loginID_OR_MSISDN + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select * from users, user_phones where users.user_id = user_phones.user_id ");
		sqlSelectBuff.append("and (users.login_id = ? OR users.MSISDN = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, loginID_OR_MSISDN);
			statement.setString(2, loginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	// get details from user_phones
	public String[] getUserDetailsFromUserPhones(String loginID_OR_MSISDN, String... columnNames) {
		final String methodname = "getUserDetailsFromUserPhones";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + loginID_OR_MSISDN + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select * from user_phones where MSISDN = ? ");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, loginID_OR_MSISDN);

			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	// Deleting the Network added via Automation suite from Database and verifying
	// it is deleted

	public String DeleteNetwork(String NetworkCode) {
		final String methodname = "DeleteNetwork";

		Log.info("Entered " + methodname + "(" + NetworkCode + ")");
		String NetworkCodeStatus = null;
		Connection connection = null;
		PreparedStatement statement = null, statement1 = null, statement2 = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("DELETE from NETWORKS WHERE NETWORK_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		StringBuilder sqlSelectBuff1 = new StringBuilder("COMMIT");
		String sqlSelect1 = sqlSelectBuff1.toString();

		StringBuilder sqlSelectBuff2 = new StringBuilder("select case when exists ");
		sqlSelectBuff2.append("(select 1 from NETWORKS where NETWORK_CODE = ?) ");
		sqlSelectBuff2.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect2 = sqlSelectBuff2.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement1 = connection.prepareStatement(sqlSelect1);
			statement2 = connection.prepareStatement(sqlSelect2);

			statement.setString(1, NetworkCode);
			statement2.setString(1, NetworkCode);

			Log.info(methodname + "() :: select query1: " + sqlSelect);
			Log.info(methodname + "() :: select query1: " + sqlSelect1);
			Log.info(methodname + "() :: select query1: " + sqlSelect2);
			statement.executeQuery();
			statement1.executeQuery();
			QueryResult = statement2.executeQuery();
			QueryResult.next();
			NetworkCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching NetworkCode status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement1 != null)
				try {
					statement1.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement2 != null)
				try {
					statement2.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: NetworkCode Status (" + NetworkCodeStatus + ")");
		Log.info("Exited " + methodname + "()");
		return NetworkCodeStatus;
	}

	// Unique ServiceClassName

	public String checkForUniqueServiceClassName(String ServiceClassName) {
		final String methodname = "checkForUniqueServiceClassName";
		Log.info("Entered " + methodname + "(" + ServiceClassName + ")");
		String ServiceClassNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from SERVICE_CLASSES where SERVICE_CLASS_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, ServiceClassName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			ServiceClassNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching ServiceClassName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: NetworkCode Status (" + ServiceClassNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return ServiceClassNameStatus;
	}

	// get Service Class Id

	public String getServiceClassID(String ServiceClassName) {
		final String methodname = "getServiceClassID";
		Log.info("Entered " + methodname + "(" + ServiceClassName + ")");
		String ServiceClassId = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT SERVICE_CLASS_ID from SERVICE_CLASSES where SERVICE_CLASS_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, ServiceClassName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ServiceClassId = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Service Class Id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Service Class ID (" + ServiceClassId + ")");
		Log.info("Exited " + methodname + "()");
		return ServiceClassId;
	}

	public String[][] getO2CTransferDetails(String fromDate, String toDate, String dateformat, String domainCode,
			String geodomainCode, String... columnNames) {
		final String methodname = "getO2CTransferDetails";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + dateformat + ", " + domainCode + ", "
				+ geodomainCode + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT " + stringBuilder.toString().replaceAll(", $", "") + "  FROM channel_transfers ");
		sqlSelectBuff.append("WHERE TYPE = 'O2C' AND status = 'CLOSE' ");
		sqlSelectBuff.append("AND transfer_date>=TO_timestamp(?,?) and transfer_date<=TO_timestamp(?,?) ");
		sqlSelectBuff.append("AND (domain_code= ? or domain_code='OPT') AND grph_domain_code=?");
		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, fromDate);
			statement.setString(2, dateformat);
			statement.setString(3, toDate);
			statement.setString(4, dateformat);
			statement.setString(5, domainCode);
			statement.setString(6, geodomainCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

//Unique MessageGatewayCode

	public String checkForUniqueGatewayCode(String GatewayCode) {
		final String methodname = "checkForUniqueGatewayCode";
		Log.info("Entered " + methodname + "(" + GatewayCode + ")");
		String GatewayCodeStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from MESSAGE_GATEWAY where GATEWAY_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, GatewayCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			GatewayCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching GatewayCode status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GatewayCode Status (" + GatewayCodeStatus + ")");
		Log.info("Exited " + methodname + "()");
		return GatewayCodeStatus;
	}

	public String[][] getAddCommDetailRpt(String fromDate, String toDate, String networkCode, String userID,
			String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode,
			String... columnNames) {
		final String methodname = "getAddCommDetailRpt";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + networkCode + ", " + userID + ", "
				+ geodomainCode + ", " + loggedInUserID + ", " + ", " + parentCat + ", " + ", " + userDomainCode + ", "
				+ Arrays.toString(columnNames) + ")");

		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder selectQueryBuff = new StringBuilder(
				"SELECT " + stringBuilder.toString().replaceAll(", $", "") + "  FROM adjustments ");
		selectQueryBuff.append(
				"FROM ADJUSTMENTS ADJ, USERS U, C2S_TRANSFERS CST, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,CATEGORIES C, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,SERVICE_TYPE ST,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");
		selectQueryBuff.append(
				"WHERE ADJ.ADJUSTMENT_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.created_on >= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.created_on <= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.network_code=? ");
		selectQueryBuff.append(
				"AND ADJ.user_id =  CASE ?  WHEN 'ALL' THEN ADJ.user_id ELSE  ? END AND C.category_code = CASE ?  WHEN 'ALL' THEN C.category_code ELSE  ? END AND ADJ.user_category = C.category_code AND C.domain_code IN(?) AND ADJ.user_id=U.user_id AND CST.transfer_id=SUBSTR(ADJ.adjustment_id, 1,LENGTH(ADJ.adjustment_id)-1) ");
		selectQueryBuff.append(
				"AND UG.grph_domain_code = GD.grph_domain_code AND UG.user_id=U.user_id  AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code ");
		selectQueryBuff.append(
				"AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND ADJ.SERVICE_TYPE=ST.SERVICE_TYPE AND CST.REVERSAL_ID is null AND TXN_TYPE='T' AND UG.grph_domain_code IN ( ");
		selectQueryBuff.append(
				"SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
		selectQueryBuff.append(
				"(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?)) ");
		selectQueryBuff.append("ORDER BY 27 Desc");
		String sqlSelect = selectQueryBuff.toString();
		String[][] dataOb = null;
		PreparedStatement pstmt = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			int i = 1;
			pstmt.setString(i++, fromDate);
			pstmt.setString(i++, toDate);
			pstmt.setString(i++, fromDate);
			pstmt.setString(i++, toDate);
			pstmt.setString(i++, networkCode);
			pstmt.setString(i++, userID);
			pstmt.setString(i++, userID);
			pstmt.setString(i++, parentCat);
			pstmt.setString(i++, parentCat);
			pstmt.setString(i++, userDomainCode);
			pstmt.setString(i++, geodomainCode);
			pstmt.setString(i++, geodomainCode);
			pstmt.setString(i, loggedInUserID);

			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int j = 0; j < columns; j++) {
					dataOb[row][j] = QueryResult.getString(j + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	public String checkForUniqueSubscriberAliasMSISDN(String MSISDN) {
		final String methodName = "checkForUniqueSubscriberAliasMSISDN";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String MSISDNStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists (");
		sqlSelectBuff.append("select 1 from subscriber_msisdn_alias where MSISDN = ? ) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDNStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDNStatus (" + MSISDNStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return MSISDNStatus;
	}

	public String[][] getchnnlChnnlTrfDetailRpt(String fromDate, String toDate, String dateformat, String msisdn,
			String to_msisdn, String... columnNames) {
		final String methodname = "getchnnlChnnlTrfDetailRpt";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + dateformat + ", " + msisdn + ", "
				+ to_msisdn + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT " + stringBuilder.toString().replaceAll(", $", "") + "  FROM channel_transfers ");
		sqlSelectBuff.append("WHERE TYPE = 'O2C' AND status = 'CLOSE' ");
		sqlSelectBuff.append("AND transfer_date>=TO_timestamp(?,?) and transfer_date<=TO_timestamp(?,?) ");
		sqlSelectBuff.append("AND msisdn = ? AND to_msisdn = ? ");
		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, fromDate);
			statement.setString(2, dateformat);
			statement.setString(3, toDate);
			statement.setString(4, dateformat);
			statement.setString(5, msisdn);
			statement.setString(6, to_msisdn);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	/**
	 * @param timeFormat
	 * @return
	 */
	public String getCurrentServerTime(String timeFormat) {

		final String methodName = "getCurrentServerTime";
		Log.info("Entered " + methodName + "(" + timeFormat + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String serverTime = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select to_char(current_timestamp, ?) FROM dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, timeFormat);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serverTime = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching server Time: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Server Time(" + serverTime + ")");
		Log.info("Exiting " + methodName + "()");
		return serverTime;
	}

	public Object[][] getGatewayDetails(String gateways) {
		final String methodName = "getGatewayDetails";
		Log.info("Entered " + methodName + "(" + gateways + ")");
		Object[][] gatewayObj = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT rmg.gateway_code, mg.gateway_type, rmg.login_id, rmg.PASSWORD, ");
		sqlSelectBuff.append(
				"rmg.content_type, rmg.service_port, mg.req_password_plain FROM message_gateway mg, req_message_gateway rmg ");
		sqlSelectBuff.append("WHERE mg.gateway_code = rmg.gateway_code AND rmg.gateway_code IN (" + gateways + ")");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.last();
			int rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();
			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			gatewayObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				for (int j = 0, k = 1; k <= columnCount; j++, k++) {
					gatewayObj[i][j] = QueryResult.getObject(k);
				}
				i++;
			}
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(gatewayObj);
		Log.info("Exiting " + methodName + "()");
		return gatewayObj;
	}

	/**
	 * To get Value from controlPreference
	 *
	 * @param preferenceCode
	 * @return
	 */
	public String getValuefromControlPreference(String preferenceCode) {
		final String methodname = "getValuefromControlPreference";
		Log.info("Entered " + methodname + "(" + preferenceCode + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select VALUE from CONTROL_PREFERENCES where PREFERENCE_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, preferenceCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			preferenceName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PREFERENCE_NAME (" + preferenceName + ")");
		Log.info("Exited " + methodname + "()");
		return preferenceName;
	}

	/**
	 * To check is Multiple Network supported
	 *
	 * @return
	 */
	public Boolean isMultipleNetworkEnabled() {
		final String methodname = "isMultipleNetworkEnabled";
		Log.info("Entered " + methodname);
		int NetworkCount = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select count(*) num from NETWORKS where status = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.YES);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			NetworkCount = QueryResult.getInt(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: NetworkCount (" + NetworkCount + ")");
		Log.info("Exited " + methodname + "()");

		if (NetworkCount > 1)
			return true;
		else
			return false;
	}

	/**
	 * To get type from systemPreference
	 *
	 * @param preferenceCode
	 * @return
	 */
	public String getTypefromSystemPreference(String preferenceCode) {
		final String methodname = "getTypefromSystemPreference";
		Log.info("Entered " + methodname + "(" + preferenceCode + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select TYPE from SYSTEM_PREFERENCES where PREFERENCE_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, preferenceCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			preferenceName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PREFERENCE_NAME (" + preferenceName + ")");
		Log.info("Exited " + methodname + "()");
		return preferenceName;
	}

	/**
	 * To get Value from networkPreference
	 *
	 * @param preferenceCode
	 * @return
	 */
	public String getValuefromNetworkPreference(String preferenceCode) {
		final String methodname = "getValuefromNetworkPreference";
		Log.info("Entered " + methodname + "(" + preferenceCode + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select VALUE from NETWORK_PREFERENCES where PREFERENCE_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, preferenceCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			preferenceName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PREFERENCE_NAME (" + preferenceName + ")");
		Log.info("Exited " + methodname + "()");
		return preferenceName;
	}

	/**
	 * To get Value from networkPreference
	 *
	 * @param preferenceCode
	 * @return
	 */
	public String getDailyLimitForAutoO2C(String userID) {
		final String methodname = "getDailyLimitForAutoO2C";
		Log.info("Entered " + methodname + "(" + userID + ")");
		String dailyCountLimit = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select AUTO_DAILY_IN_COUNT from USER_TRANSFER_COUNTS where USER_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			dailyCountLimit = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DAILY_COUNT_LIMIT (" + dailyCountLimit + ")");
		Log.info("Exited " + methodname + "()");
		return dailyCountLimit;
	}

	public String[][] getZeroBalSummRpt(java.sql.Date fromDate, java.sql.Date toDate, String networkCode, String userID,
			String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode,
			String... columnNames) {
		final String methodname = "getZeroBalSummRpt";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + userDomainCode + ", " + geodomainCode
				+ ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder("SELECT " + "u.msisdn ");
		sqlSelectBuff.append(
				" FROM USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT,PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD");
		sqlSelectBuff.append(" Where UTC.entry_date>=?AND UTC.entry_date<=? AND UTC.network_code=?");
		sqlSelectBuff.append(
				" AND UTC.user_id IN(SELECT U11.user_id FROM users U11 WHERE U11.user_id=U11.user_id CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?)");
		sqlSelectBuff.append(
				" AND U.user_id=UTC.user_id AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END AND L1.lookup_type='THRTP'AND L1.lookup_code=UTC.RECORD_TYPE");
		sqlSelectBuff.append(
				" AND UTC.category_code = CASE ?  WHEN 'ALL' THEN UTC.category_code ELSE ? END AND CAT.category_code=UTC.category_code AND CAT.domain_code = ?");
		sqlSelectBuff.append(
				" AND P.PRODUCT_CODE=UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id AND L2.lookup_type='URTYP' AND L2.lookup_code=U.status");
		sqlSelectBuff.append(
				" AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN (SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1");
		sqlSelectBuff.append(
				" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN( ");
		sqlSelectBuff.append(
				" SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END");
		sqlSelectBuff.append(
				" AND UG1.user_id=?))group by U.user_name,U.msisdn ,L2.lookup_name,CAT.category_name,UTC.ENTRY_DATE,P.product_name,L1.lookup_name");

		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setDate(1, (java.sql.Date) fromDate);

			statement.setDate(2, (java.sql.Date) toDate);
			statement.setString(3, networkCode);
			statement.setString(4, loggedInUserID);
			statement.setString(5, "ALL");
			statement.setString(6, "ALL");
			statement.setString(7, parentCat);
			statement.setString(8, parentCat);
			statement.setString(9, userDomainCode);
			statement.setString(10, geodomainCode);
			statement.setString(11, geodomainCode);
			statement.setString(12, loggedInUserID);

			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	public String[][] getExternalRolesRpt(String networkCode, String userID, String loggedInUserID, String parentCat,
			String userDomainCode, String geodomainCode, String... columnNames) {
		final String methodname = "getExternalRolesRpt";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + userDomainCode + ", " + geodomainCode + ", "
				+ Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder("SELECT " + "u.msisdn ");
		sqlSelectBuff.append("FROM (SELECT U1.user_id FROM USERS U1 CONNECT BY PRIOR U1.user_id = U1.parent_id");
		sqlSelectBuff.append(
				"START WITH U1.user_id ='{?userid}') X,USERS u,CATEGORIES c,DOMAINS d,GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG WHERE u.CATEGORY_CODE=c.CATEGORY_CODE");
		sqlSelectBuff.append("AND c.DOMAIN_CODE=d.DOMAIN_CODE AND u.USER_ID=ug.USER_ID AND u.USER_ID=X.USER_ID");
		sqlSelectBuff.append(
				"AND ug.GRPH_DOMAIN_CODE=gd.GRPH_DOMAIN_CODE AND UG.grph_domain_code IN (SELECT grph_domain_code ");
		sqlSelectBuff.append(
				"FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S')CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code");
		sqlSelectBuff.append("START WITH grph_domain_code IN(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
		sqlSelectBuff.append(
				"WHERE UG1.grph_domain_code = CASE '{?zonecode}'  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  '{?zonecode}' END");
		sqlSelectBuff.append(
				"AND UG1.user_id='{?loginid}' ))AND U.STATUS = CASE 'ALL' WHEN  'ALL' THEN U.STATUS ELSE 'ALL' END");
		sqlSelectBuff.append(
				"AND c.DOMAIN_CODE IN ('{?domainlist}') AND C.CATEGORY_CODE = CASE '{?categorycode}' WHEN  'ALL' THEN C.CATEGORY_CODE ELSE '{?categorycode}' END");

		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, userID);
			statement.setString(2, geodomainCode);
			statement.setString(3, geodomainCode);

			statement.setString(4, loggedInUserID);

			statement.setString(5, userDomainCode);
			statement.setString(6, "ALL");
			statement.setString(7, "ALL");

			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	public String[][] getStaffSelfC2CRpt(java.sql.Date fromDate, java.sql.Date toDate, String networkCode,
			String userID, String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode,
			String... columnNames) {
		final String methodname = "getStaffSelfC2CRpt";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + userDomainCode + ", " + geodomainCode + ", "
				+ Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder("SELECT " + "CTRF.transfer_id ");
		sqlSelectBuff.append(
				" FROM USERS X,CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES REC_CAT");
		sqlSelectBuff.append(
				" WHERE CTRF.TYPE = 'C2C' AND CTRF.close_date >=TO_DATE('{?fromDateTime}','{?dateTimeFormat}') AND CTRF.close_date <=TO_DATE('{?toDateTime}','{?dateTimeFormat}') AND CTRF.network_code = '{?networkCode}");
		sqlSelectBuff.append(
				" AND CTRF.control_transfer<>'A' AND CTRF.receiver_category_code = REC_CAT.category_code AND CTRF.ACTIVE_USER_ID='{?searchUserID}' AND CTRF.ACTIVE_USER_ID = X.user_id ");
		sqlSelectBuff.append(
				" AND CTRF.transfer_sub_type = CASE '{?transferSubType}' WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE '{?transferSubType}' END AND U.user_id = CTRF.ACTIVE_USER_ID AND U2.user_id =CTRF.to_user_id");
		sqlSelectBuff.append(
				" AND CTRF.transfer_id = CTI.transfer_id AND CTI.product_code = P.product_code AND L.lookup_type ='TRFT'");
		sqlSelectBuff.append(
				" AND CTRF.transfer_id = CTI.transfer_id AND CTI.product_code = P.product_code AND L.lookup_type ='TRFT'");
		sqlSelectBuff.append(
				" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id,L.lookup_name , CTRF.TYPE, CTRF.close_date,P.product_name, L1.lookup_name,CTRF.sender_category_code, CTRF.receiver_category_code , REC_CAT.category_name,CTRF.SOURCE");

		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setDate(1, fromDate);
			statement.setString(2, "dd/MM/yy");
			statement.setDate(3, toDate);
			statement.setString(4, "dd/MM/yy");

			statement.setString(5, networkCode);
			statement.setString(6, userID);

			statement.setString(7, "ALL");
			statement.setString(8, "ALL");

			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	@Override
	public String[][] getC2STransfer(String fromDate, String toDate, String dateformat, String domainCode,
			String geodomainCode, String... columnNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] getZeroBalanceCounterDetails(String thresholdType, String fromDate, String toDate, String msisdn,
			String dateformat, String geodomainCode, String domainCode, String parentCategoryCode, String userName,
			String[] columnNames) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProductName(String serviceType) {
		final String methodname = "getProductCode";
		Log.info("Entered " + methodname + "(" + serviceType + ")");
		String productName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT UNIQUE (pr.product_name) FROM product_service_type_mapping pst JOIN products pr ");
		sqlSelectBuff.append("ON pst.product_type = pr.product_type WHERE pst.service_type = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, serviceType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			productName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Product Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Product Code (" + productName + ")");
		Log.info("Exited " + methodname + "()");
		return productName;
	}

	public String checkForLangCode(String LANGCODE) {

		final String methodName = "checkForLangCode";
		Log.info("Entered " + methodName + "(" + LANGCODE + ")");
		String LanguageCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT LANGUAGE_CODE from LOCALE_MASTER where NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LANGCODE);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			LanguageCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Language Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LanguageCode (" + LanguageCode + ")");
		Log.info("Exiting " + methodName + "()");
		return LanguageCode;
	}

	public String getSelectorCode(String selectorName, String serviceType) {

		final String methodName = "getSelectorCode";
		Log.info("Entered " + methodName + "(" + selectorName + ",serviceType=" + serviceType + ")");
		String selectorCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT SELECTOR_CODE from SERVICE_TYPE_SELECTOR_MAPPING where SELECTOR_NAME = ? and service_type =? and status =? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, selectorName);
			pstmt.setString(2, serviceType);
			pstmt.setString(3, PretupsI.YES);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			selectorCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Selector Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: selectorCode (" + selectorCode + ")");
		Log.info("Exiting " + methodName + "()");
		return selectorCode;
	}

	public String checkForCountry(String language) {

		final String methodName = "checkForCountry";
		Log.info("Entered " + methodName + "(" + language + ")");
		String Country = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT country from LOCALE_MASTER where language = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, language);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			Country = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Country: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Country (" + Country + ")");
		Log.info("Exiting " + methodName + "()");
		return Country;
	}

	public String checkForUniqueCommProfileShortCode(String ShortCode) {
		final String methodName = "checkForUniqueCommProfileShortCode";
		Log.info("Entered " + methodName + "(" + ShortCode + ")");
		String ShortCodeStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from COMMISSION_PROFILE_SET where SHORT_CODE = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, ShortCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ShortCodeStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Short Code status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Short Code Status (" + ShortCodeStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return ShortCodeStatus;
	}

	public boolean deletionfrombarredMSISDN(String MSISDN) {
		final String methodname = "deletionfrombarredMSISDN";
		Log.info("Entered " + methodname + "(" + MSISDN + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		boolean deleteExecuted = false;

		StringBuilder sqlSelectBuff = new StringBuilder("DELETE from BARRED_MSISDNS where MSISDN=? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			deleteExecuted = true;
		} catch (Exception e) {
			Log.info("Exception while fetching Product Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: deleteExecuted (" + deleteExecuted + ")");
		Log.info("Exited " + methodname + "()");
		return deleteExecuted;
	}

	@Override
	public String[][] getUserBalanceMovementSummary(String parentCode, String zoneCode, String networkCode,
			String loginUserID, String domainCode, String fromDate, String toDate, String msisdn,
			String... columnNames) {
		final String methodname = "getUserBalanceMovementSummary";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + msisdn + ","
				+ Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String colNames = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < columnNames.length; x++) {
			stringBuilder.append(columnNames[x] + ", ");
		}
		StringBuilder sqlSelectBuff = new StringBuilder("SELECT " + stringBuilder.toString().replaceAll(", $", ""));
		sqlSelectBuff.append("FROM (SELECT user_id,parent_id,owner_id ");
		sqlSelectBuff.append("FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=?) X, ");
		sqlSelectBuff.append("DAILY_CHNL_TRANS_MAIN CT, USERS U,CATEGORIES CAT, USER_GEOGRAPHIES UG, ");
		sqlSelectBuff.append(
				"GEOGRAPHICAL_DOMAINS GD,PRODUCTS P,USERS UP,USERS GP,USERS OU,USER_GEOGRAPHIES UGG,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS GD1, ");
		sqlSelectBuff.append("GEOGRAPHICAL_DOMAINS GD2 ");
		sqlSelectBuff.append("WHERE  X.user_id = CT.user_id ");
		sqlSelectBuff.append("AND CT.user_id = U.user_id ");
		sqlSelectBuff.append("AND P.product_code=CT.product_code ");
		sqlSelectBuff.append("AND CAT.category_code = U.category_code ");
		sqlSelectBuff.append("AND U.user_id = UG.user_id ");
		sqlSelectBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		sqlSelectBuff.append("AND UP.USER_ID=CASE X.parent_id WHEN 'ROOT' THEN X.user_id ELSE X.parent_id END ");
		sqlSelectBuff.append("AND GP.USER_ID=CASE UP.parent_id WHEN 'ROOT' THEN UP.user_id ELSE UP.parent_id END ");
		sqlSelectBuff.append("AND OU.USER_ID=X.OWNER_ID ");
		sqlSelectBuff.append("AND UGG.user_id=GP.USER_ID ");
		sqlSelectBuff.append("AND UGG.GRPH_DOMAIN_CODE=GD1.GRPH_DOMAIN_CODE ");
		sqlSelectBuff.append("AND UGW.USER_ID=OU.USER_ID ");
		sqlSelectBuff.append("AND UGW.GRPH_DOMAIN_CODE=GD2.GRPH_DOMAIN_CODE ");
		sqlSelectBuff.append("AND UG.grph_domain_code IN ( ");
		sqlSelectBuff.append("SELECT grph_domain_code ");
		sqlSelectBuff.append("FROM ");
		sqlSelectBuff.append("GEOGRAPHICAL_DOMAINS GD1 ");
		sqlSelectBuff.append("WHERE status IN('Y', 'S') ");
		sqlSelectBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		sqlSelectBuff.append("START WITH grph_domain_code IN ");
		sqlSelectBuff.append("(SELECT grph_domain_code ");
		sqlSelectBuff.append("FROM USER_GEOGRAPHIES ug1 ");
		sqlSelectBuff.append("WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
		sqlSelectBuff.append("AND UG1.user_id=?)) ");
		sqlSelectBuff.append("AND CT.network_code = ? ");
		sqlSelectBuff.append("AND CAT.domain_code = ? ");
		sqlSelectBuff.append("AND CAT.category_code = CASE  ?   WHEN 'ALL' THEN CAT.category_code ELSE   ?  END ");
		sqlSelectBuff.append("AND CT.user_id =  CASE ? WHEN 'ALL' THEN CT.user_id ELSE ?  END ");
		sqlSelectBuff.append("AND CT.trans_date >= ? ");
		sqlSelectBuff.append("AND CT.trans_date <= ? ");
		sqlSelectBuff.append("group by ");
		sqlSelectBuff.append(
				" CT.trans_date,U.user_name,U.msisdn,U.external_code,GD.grph_domain_name,GP.user_name,GP.msisdn,GD1.GRPH_DOMAIN_NAME  ");
		sqlSelectBuff.append(
				" ,GD2.GRPH_DOMAIN_NAME,P.product_name,CT.opening_balance,CT.o2c_transfer_in_amount,CT.closing_balance, ");
		sqlSelectBuff.append(
				"CT.c2s_transfer_out_amount,CT.o2c_return_out_amount,CT.o2c_withdraw_out_amount,CT.c2c_transfer_out_amount,CT.c2c_withdraw_out_amount,CT.c2c_return_out_amount,  ");
		sqlSelectBuff.append(
				" CT.c2c_withdraw_in_amount,CT.c2c_return_in_amount, CT.c2c_transfer_in_amount, CT.trans_date,UP.user_name,UP.msisdn ");
		String sqlSelect = sqlSelectBuff.toString();
		String[][] dataOb = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, loginUserID);
			statement.setString(2, zoneCode);
			statement.setString(3, zoneCode);
			statement.setString(4, loginUserID);
			statement.setString(5, networkCode);
			statement.setString(6, domainCode);
			statement.setString(7, parentCode);
			statement.setString(8, parentCode);
			// statement.setString(9, userId);
			// statement.setString(10, userId);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData metadata = QueryResult.getMetaData();
			int columnCount = metadata.getColumnCount();

			QueryResult.last();
			int allrecords = QueryResult.getRow();
			QueryResult.beforeFirst();
			int row = 0;
			int columns = columnNames.length;
			dataOb = new String[allrecords][columns];

			while (QueryResult.next()) {
				for (int i = 0; i < columns; i++) {
					dataOb[row][i] = QueryResult.getString(i + 1);
				}
				row++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.deepToString(dataOb));
		Log.info("Exited " + methodname + "()");
		return dataOb;
	}

	public String fetchDomainName(String domainCode) {
		final String methodname = "fetchDomainName";
		Log.info("Entered " + methodname + "(" + domainCode + ")");
		String domainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DOMAIN_NAME FROM DOMAINS WHERE DOMAIN_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, domainCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Domain Name");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Domain Name (" + domainName + ")");
		Log.info("Exited " + methodname + "()");
		return domainName;
	}

	public String getDefaultGroupRoleName(String CategoryCode) {
		final String methodName = "getDefaultGroupRoleName";
		Log.info("Entered " + methodName + "(" + CategoryCode + ")");
		String GroupRoleName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT DISTINCT(r.role_code) FROM roles r, group_roles gr, category_roles cr ");
		sqlSelectBuff.append("WHERE r.IS_DEFAULT = 'Y' AND r.group_role='Y' AND r.role_code=gr.group_role_code ");
		sqlSelectBuff.append("AND cr.role_code=r.role_code AND cr.category_code= ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CategoryCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			GroupRoleName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Default Group Role Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Default Group Role Name (" + GroupRoleName + ")");
		Log.info("Exiting " + methodName + "()");
		return GroupRoleName;
	}

	public String getDefaultCardGroupStatus(String cardGroupName) {
		final String methodName = "getCardGroupDefaultStatus";
		Log.info("Entered " + methodName + "(" + cardGroupName + ")");
		String IS_DEFAULT = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT IS_DEFAULT FROM CARD_GROUP_SET WHERE CARD_GROUP_SET_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, cardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			IS_DEFAULT = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Default Card Group Status: ");

			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Card Group Default Status (" + IS_DEFAULT + ")");
		Log.info("Exiting " + methodName + "()");
		return IS_DEFAULT;
	}

	public String getOTP(String MSISDN) {
		final String methodname = "getOTP";
		Log.info("Entered " + methodname + "(" + MSISDN + ")");
		String OTP = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT OTP from USER_PHONES where MSISDN = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			OTP = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Service Class Id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: OTP (" + OTP + ")");
		Log.info("Exited " + methodname + "()");
		return OTP;
	}

	public String getLatestOTP(String MSISDN) {
		final String methodname = "getOTP";
		Log.info("Entered " + methodname + "(" + MSISDN + ")");
		String OTP = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT OTP_PIN from USER_OTP where MSISDN = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			OTP = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Service Class Id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: OTP (" + OTP + ")");
		Log.info("Exited " + methodname + "()");
		return OTP;
	}

	/*
	 * Channel Domain Management
	 */
	public String fetchdomainTypeName(String domainName) {
		final String methodname = "fetchdomainTypeName";
		Log.info("Entered " + methodname + "(" + domainName + ")");
		String domaintypename = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select domain_type_name from domain_types ");
		sqlSelectBuff.append("where domain_type_code =(Select domain_type_code from domains where domain_name=?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, domainName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			domaintypename = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching domain_type_name.");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Domain_type_name(" + domaintypename + ")");
		Log.info("Exited " + methodname + "()");
		return domaintypename;
	}

	public String getServiceClass() {
		final String methodname = "getServiceClass";
		Log.info("Entered " + methodname);
		String ServiceClassId = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT SERVICE_CLASS_ID from SERVICE_CLASSES where SERVICE_CLASS_NAME = 'ALL' AND SERVICE_CLASS_CODE = 'ALL' AND STATUS = 'Y' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ServiceClassId = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Service Class Id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Service Class ID (" + ServiceClassId + ")");
		Log.info("Exited " + methodname + "()");
		return ServiceClassId;
	}

	public String[] getTypeOFPreference(String Control_Code, String Network_Code, String Preference_Code) {
		String[] prefVal = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		String preferenceType = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();
			Log.info("Entered :: " + Thread.currentThread().getStackTrace()[1].getMethodName() + "()");

			StringBuffer QueryBuffer = new StringBuffer("select VALUE from SERVICE_CLASS_PREFERENCES where ");
			QueryBuffer.append("NETWORK_CODE = '" + Network_Code + "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
			QueryResult = statement.executeQuery(QueryBuffer.toString());
			preferenceType = PretupsI.SERVICE_CLASS_PREFERENCE_TYPE;
			if (!QueryResult.isBeforeFirst()) {
				Log.info("Service Class Preference Not Found");
				Log.info("Trying to get Preference value from Control Preferences");

				QueryBuffer = new StringBuffer("select VALUE from CONTROL_PREFERENCES where ");
				QueryBuffer.append("CONTROL_CODE = '" + Control_Code + "' AND NETWORK_CODE = '" + Network_Code
						+ "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
				QueryResult = statement.executeQuery(QueryBuffer.toString());
				preferenceType = PretupsI.CONTROL_PREFERENCE_TYPE;
				if (!QueryResult.isBeforeFirst()) {
					Log.info("Control Preference Not Found");
					Log.info("Trying to get Preference value from Network Preferences");
					QueryBuffer = new StringBuffer("select VALUE from NETWORK_PREFERENCES where ");
					QueryBuffer.append(
							"NETWORK_CODE = '" + Network_Code + "' AND PREFERENCE_CODE = '" + Preference_Code + "'");
					QueryResult = statement.executeQuery(QueryBuffer.toString());
					preferenceType = PretupsI.NETWORK_PREFERENCE_TYPE;
					if (!QueryResult.isBeforeFirst()) {
						Log.info("Network Preference Not Found");
						Log.info("Trying to get Preference value from System Preferences");
						QueryBuffer = new StringBuffer("select DEFAULT_VALUE from SYSTEM_PREFERENCES where ");
						QueryBuffer.append("PREFERENCE_CODE = '" + Preference_Code + "'");
						QueryResult = statement.executeQuery(QueryBuffer.toString());
						preferenceType = PretupsI.SYSTEM_PREFERENCE_TYPE;
					}
				}
			}
			QueryResult.next();
			prefVal = new String[] { QueryResult.getString(1).toString(), preferenceType };
		} catch (Exception e) {
			Log.info("Error while fetching Preference");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: TYPE returned as  (" + prefVal[0] + ", " + prefVal[1] + ")");
		return prefVal;
	}

	public String getEmpCode(String loginID) {
		final String methodname = "getEmpCode";
		Log.info("Entered " + methodname + "(" + loginID + ")");
		String empCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EMPLOYEE_CODE from USERS where LOGIN_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			empCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: EMPLOYEE CODE (" + empCode + ")");
		Log.info("Exited " + methodname + "()");
		return empCode;
	}

	public int getSubServiceCount(String voucherType, String service) {
		final String methodname = "getSubServiceCount";
		Log.info("Entered " + methodname + "(" + voucherType + ", " + service + ")");
		int subServiceCount = 0;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT count(*) ");
		sqlSelectBuff.append("from VOMS_VTYPE_SERVICE_MAPPING vvsm  ");
		sqlSelectBuff.append("where vvsm.voucher_type = ? AND vvsm.service_type = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, voucherType);
			statement.setString(2, service);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			subServiceCount = QueryResult.getInt(1);
		} catch (Exception e) {
			Log.info("Error while fetching Sub Service Count: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SubServiceCount (" + subServiceCount + ")");
		Log.info("Exited " + methodname + "()");
		return subServiceCount;
	}

	public String checkForUniqueDenominationName(String denominationName) {
		final String methodname = "checkForUniqueDenominationName";
		Log.info("Entered " + methodname + "(" + denominationName + ")");
		String denominationNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_CATEGORIES where CATEGORY_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, denominationName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			denominationNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Denomination status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Denomination Name Status (" + denominationNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return denominationNameStatus;
	}

	public String checkForShortName(String shortName) {
		final String methodname = "checkForShortName";
		Log.info("Entered " + methodname + "(" + shortName + ")");
		String shortNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_CATEGORIES where CATEGORY_SHORT_NAME = ?) ");
		sqlSelectBuff.append(
				"then 'Y' else (select case when exists (select 1 from VOMS_PRODUCTS where SHORT_NAME = ? ) then 'Y' else 'N' end as rec_exist from dual) end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, shortName);
			statement.setString(2, shortName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			shortNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Short Name status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Short Name Status (" + shortNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return shortNameStatus;
	}

	public String checkForMRP(String MRP) {
		final String methodname = "checkForMRP";
		Log.info("Entered " + methodname + "(" + MRP + ")");
		String MRPStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_CATEGORIES where MRP = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, MRP);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			MRPStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MRP status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MRP Status (" + MRPStatus + ")");
		Log.info("Exited " + methodname + "()");
		return MRPStatus;
	}

	public String checkForMRPFromProduct(String MRP) {
		final String methodname = "checkForMRP";
		Log.info("Entered " + methodname + "(" + MRP + ")");
		String MRPStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_PRODUCTS where MRP = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, MRP);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			MRPStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MRP status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MRP Status (" + MRPStatus + ")");
		Log.info("Exited " + methodname + "()");
		return MRPStatus;
	}

	public String checkForProfileName(String profileName) {
		final String methodname = "checkForProfileName";
		Log.info("Entered " + methodname + "(" + profileName + ")");
		String profileNameStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from VOMS_PRODUCTS where PRODUCT_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, profileName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			profileNameStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Profile Name status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Profile Name Status (" + profileNameStatus + ")");
		Log.info("Exited " + methodname + "()");
		return profileNameStatus;
	}

	public Object[][] getVOMSDetailsC2C(List l1) {
		final String methodname = "getVOMSDetails";
		Log.info("Entered " + methodname + "()");
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		Object[][] resultObj = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT vt.voucher_type, vt.type, vvsm.service_type, ");
		sqlSelectBuff.append(
				"stsm.selector_name FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE_SELECTOR_MAPPING stsm ");
		sqlSelectBuff.append(
				"where vvsm.voucher_type=vt.voucher_type and vvsm.service_type=stsm.service_type and vvsm.sub_service=stsm.selector_code ");
		if (l1 != null && l1.size() != 0) {
			sqlSelectBuff.append("and vt.type NOT IN (");
			int i = 0;
			for (i = 0; i < l1.size() - 1; i++) {
				sqlSelectBuff.append("'").append(l1.get(i).toString()).append("',");
			}
			sqlSelectBuff.append("'").append(l1.get(i).toString()).append("')");
		}
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				resultObj[i][0] = QueryResult.getString("voucher_type");
				resultObj[i][1] = QueryResult.getString("type");
				resultObj[i][2] = QueryResult.getString("service_type");
				resultObj[i][3] = QueryResult.getString("selector_name");
				i++;
			}
		} catch (Exception e) {
			Log.info("Error while fetching preparing Result Object: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return resultObj;
	}

	public Object[][] getVOMSDetails() {
		final String methodname = "getVOMSDetails";
		Log.info("Entered " + methodname + "()");
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		Object[][] resultObj = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT vt.voucher_type, vt.type, vvsm.service_type, ");
		sqlSelectBuff.append(
				"stsm.selector_name FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE_SELECTOR_MAPPING stsm ");
		sqlSelectBuff.append(
				"where vvsm.voucher_type=vt.voucher_type and vvsm.service_type=stsm.service_type and vvsm.sub_service=stsm.selector_code ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				resultObj[i][0] = QueryResult.getString("voucher_type");
				resultObj[i][1] = QueryResult.getString("type");
				resultObj[i][2] = QueryResult.getString("service_type");
				resultObj[i][3] = QueryResult.getString("selector_name");
				i++;
			}
		} catch (Exception e) {
			Log.info("Error while fetching preparing Result Object: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return resultObj;
	}

	public Object[][] getServiceClassID(String serviceClassName, String serviceClassCode) {
		final String methodName = "getServiceClassID";
		Log.info("Entered " + methodName + "(" + serviceClassName + ", " + serviceClassCode + ")");
		Object[][] serviceClassIDs = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT sc.service_class_id  FROM interface_network_mapping inm, service_classes sc, interfaces it ");
		sqlSelectBuff.append(
				"WHERE inm.interface_id = sc.interface_id   AND sc.status <> 'N'   AND sc.interface_id = it.interface_id   AND it.status <> 'N'   AND sc.service_class_name = ?   AND sc.service_class_code = ? ");// "SELECT
																																																					// SERVICE_CLASS_ID
																																																					// from
																																																					// SERVICE_CLASSES
																																																					// where
																																																					// SERVICE_CLASS_NAME
																																																					// =
																																																					// ?
																																																					// and
																																																					// SERVICE_CLASS_CODE
																																																					// =
																																																					// ?
																																																					// and
																																																					// STATUS='Y'
																																																					// ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, serviceClassName);
			pstmt.setString(2, serviceClassCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.last();
			int rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();
			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			serviceClassIDs = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				for (int j = 0, k = 1; k <= columnCount; j++, k++) {
					serviceClassIDs[i][j] = QueryResult.getObject(k);
				}
				i++;
			}
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(serviceClassIDs);
		Log.info("Exiting " + methodName + "()");
		return serviceClassIDs;
	}

	public String fetchUserGeographyCount(String login_id) {
		final String methodName = "fetchUserGeographyCount";
		Log.info("Entered " + methodName + "(" + login_id + ")");
		String geocount = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select count(*) from user_geographies where user_id = (Select user_id from users where login_id=? ) ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, login_id);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			geocount = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info(geocount);
		Log.info("Exiting " + methodName + "()");
		return geocount;
	}

	public String getCardGroupStartRange(String CardGroupSetID) {
		final String methodname = "getCardGroupMinRange";
		Log.info("Entered " + methodname + "(" + CardGroupSetID + ")");
		String StartRange = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select Min(START_RANGE) from CARD_GROUP_DETAILS where CARD_GROUP_SET_ID = ? ");
		sqlSelectBuff.append("and VERSION = (Select Max(VERSION) from CARD_GROUP_DETAILS where CARD_GROUP_SET_ID = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, CardGroupSetID);
			statement.setString(2, CardGroupSetID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			StartRange = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Min Range of Card Group ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Card Group Start Range is (" + StartRange + ")");
		Log.info("Exited " + methodname + "()");
		return StartRange;
	}

	public String getCardGroupEndRange(String CardGroupSetID) {
		final String methodname = "getCardGroupEndRange";
		Log.info("Entered " + methodname + "(" + CardGroupSetID + ")");
		String EndRange = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select Max(END_RANGE) from CARD_GROUP_DETAILS where CARD_GROUP_SET_ID = ? ");
		sqlSelectBuff.append("and VERSION = (Select Max(VERSION) from CARD_GROUP_DETAILS where CARD_GROUP_SET_ID = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, CardGroupSetID);
			statement.setString(2, CardGroupSetID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			EndRange = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching End Range of Card Group ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Card Group Start Range is (" + EndRange + ")");
		Log.info("Exited " + methodname + "()");
		return EndRange;
	}

	public String getNetworkCode(String network) {
		final String methodName = "getNetworkCode";
		Log.info("Entered " + methodName + "(" + network + ")");
		String network_name = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select NETWORK_CODE from NETWORKS where NETWORK_CODE <> ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, network);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			network_name = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + network_name + ")");
		Log.info("Exiting " + methodName + "()");
		return network_name;

	}

	/**
	 * Query to fetch existing Product ID
	 *
	 * @author simarnoor.bains
	 * @return: product_id
	 */

	public String fetchProductID(String productName) {
		final String methodName = "fetchProductID";
		Log.info("Entered " + methodName + "()");
		String ProductID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select PRODUCT_ID from ");
		sqlSelectBuff.append("VOMS_PRODUCTS WHERE PRODUCT_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			ProductID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: ProductID (" + ProductID + ")");
		Log.info("Exiting " + methodName + "()");
		return ProductID;
	}

	/**
	 * Query to fetch existing Voucher Batch Status
	 *
	 * @author simarnoor.bains
	 * @return: batch_type
	 */

	public String fetchBatchType(String productID) {
		final String methodName = "fetchBatchType";
		Log.info("Entered " + methodName + "()");
		String batchType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT BATCH_TYPE FROM ");
		sqlSelectBuff.append("VOMS_BATCHES WHERE PRODUCT_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			batchType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: batchType (" + batchType + ")");
		Log.info("Exiting " + methodName + "()");
		return batchType;
	}

	public String[] getVoucherBatchDetails(String productID) {
		final String methodname = "getVoucherBatchDetails";
		Log.info("Entered " + methodname + "()");
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String[] resultObj = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT from_serial_no, to_serial_no, ");
		sqlSelectBuff.append("total_no_of_vouchers FROM VOMS_BATCHES WHERE PRODUCT_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, productID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			QueryResult.next();
			resultObj = new String[columnCount];
			resultObj[0] = QueryResult.getString("from_serial_no");
			resultObj[1] = QueryResult.getString("to_serial_no");
			resultObj[2] = QueryResult.getString("total_no_of_vouchers");

		} catch (Exception e) {
			Log.info("Error while fetching preparing Result Object: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return resultObj;

	}

	public String getTransactionID(String MSISDN) {
		final String methodName = "getTransactionID";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String Transaction_ID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select TRANSFER_ID from C2S_TRANSFERS where SENDER_MSISDN <> ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			Transaction_ID = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + Transaction_ID + ")");
		Log.info("Exiting " + methodName + "()");
		return Transaction_ID;
	}

	public String getTransactionIDStatus(String MSISDN) {
		final String methodName = "getTransactionIDStatus";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String Transaction_ID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select transfer_status from C2S_TRANSFERS where transfer_id like ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			Transaction_ID = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + Transaction_ID + ")");
		Log.info("Exiting " + methodName + "()");
		return Transaction_ID;
	}

	public String fetchBatchTypeFromSerialNo(String productID, String fromSerialNo, String toSerialNo) {
		final String methodName = "fetchBatchType";
		Log.info("Entered " + methodName + "()");
		String batchType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT BATCH_TYPE FROM ");
		sqlSelectBuff.append("VOMS_BATCHES WHERE PRODUCT_ID = ? AND FROM_SERIAL_NO = ? AND TO_SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, fromSerialNo);
			pstmt.setString(3, toSerialNo);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			batchType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: batchType (" + batchType + ")");
		Log.info("Exiting " + methodName + "()");
		return batchType;
	}

	public String getMinSerialNumber(String productID, String status) {
		final String methodName = "getSerialNumber";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MIN(SERIAL_NO) FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ? AND CURRENT_STATUS = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Min Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String getMinSerialNumberuserID(String productID, String status, String userID) {
		final String methodName = "getSerialNumber";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MIN(SERIAL_NO) FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ? AND CURRENT_STATUS = ? AND USER_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, status);
			pstmt.setString(3, userID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Min Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String getMaxSerialNumberWithuserid(String productID, String status, String userid) {

		final String methodName = "getSerialNumber";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MAX(SERIAL_NO) FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ? AND CURRENT_STATUS = ? AND USER_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, status);
			pstmt.setString(3, userid);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Max Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;

	}

	public String getMaxSerialNumber(String productID, String status) {
		final String methodName = "getSerialNumber";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MAX(SERIAL_NO) FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ? AND CURRENT_STATUS = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Max Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public void changeStatusSerialNumber(String serialNumber, String status) {
		final String methodName = "changeStatusSerialNumber";
		Log.info("Entered " + methodName + "()");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String result = null;

		StringBuilder sqlSelectBuff = new StringBuilder("UPDATE VOMS_VOUCHERS SET ");
		sqlSelectBuff.append("CURRENT_STATUS = ? WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			pstmt.setString(2, serialNumber);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			result = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: (" + result + ")");
		Log.info("Exiting " + methodName + "()");

	}

	public String getExpiryDate(String fromSerialNo) {
		final String methodName = "getExpiryDate";
		Log.info("Entered " + methodName + "()");
		String expiryDate = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT EXPIRY_DATE FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, fromSerialNo);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			expiryDate = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Expiry Date: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Expiry Date (" + expiryDate + ")");
		Log.info("Exiting " + methodName + "()");
		return expiryDate;
	}

	public String getPinFromSerialNumber(String serialNumber) {
		final String methodName = "getPinFromSerialNumber";
		Log.info("Entered " + methodName + "()");
		String pin = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT PIN_NO FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, serialNumber);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			pin = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PIN (" + pin + ")");
		Log.info("Exiting " + methodName + "()");
		return pin;
	}

	public String getVoucherStatus(String serialNumber) {
		final String methodName = "getVoucherStatus";
		Log.info("Entered " + methodName + "()");
		String currentStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT CURRENT_STATUS FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, serialNumber);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			currentStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Current Status (" + currentStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return currentStatus;
	}

	public String getVoucherSummaryDate() {
		final String methodName = "getVoucherSummaryDate";
		Log.info("Entered " + methodName + "()");
		String summaryDate = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select SUMMARY_DATE from (SELECT SUMMARY_DATE FROM  VOMS_DAILY_BURNED_VOUCHERS ORDER BY SUMMARY_DATE DESC) WHERE ROWNUM = '1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			summaryDate = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Summary Date(" + summaryDate + ")");
		Log.info("Exiting " + methodName + "()");
		return summaryDate;
	}

	public String getcontroltransferlevel(String type, String fromCategoryCode, String toCategoryCode) {
		final String methodName = "getcontroltransferlevel";
		Log.info("Entered " + methodName + "(" + type + "," + fromCategoryCode + "," + toCategoryCode + ")");
		String controlLevel = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT cntrl_" + type + "_level FROM ");
		sqlSelectBuff.append("chnl_transfer_rules WHERE from_category = ? AND to_category = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, fromCategoryCode);
			pstmt.setString(2, toCategoryCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			controlLevel = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching control Level: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Current Status (" + controlLevel + ")");
		Log.info("Exiting " + methodName + "()");
		return controlLevel;
	}

	public String[] getParentUserDetails(String childmsisdn, String... columnNames) {
		final String methodname = "getParentUserDetails";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + childmsisdn + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select * from users where user_id in (select parent_id from users where MSISDN=? and parent_id!='ROOT' and status<>'N')");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, childmsisdn);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching parent user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String[] getOwnerUserDetails(String childmsisdn, String... columnNames) {
		final String methodname = "getOwnerUserDetails";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + childmsisdn + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select * from users where user_id in (select owner_id from users where MSISDN=? and status<>'N')");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, childmsisdn);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching owner user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String getSerialNumberFromStatus(String status) {
		final String methodName = "getVoucherStatus";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIAL_NO FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE CURRENT_STATUS = ? AND EXPIRY_DATE > CURRENT_DATE  AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				serialNumber = QueryResult.getString(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String getSerialNumberFromStatusAndVoucherType(String status, String voucherType) {
		final String methodName = "getVoucherStatus";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIAL_NO FROM ");
		sqlSelectBuff.append(
				"VOMS_VOUCHERS WHERE CURRENT_STATUS = ? AND VOUCHER_TYPE =? AND EXPIRY_DATE > CURRENT_DATE  AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			pstmt.setString(2, voucherType);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				serialNumber = QueryResult.getString(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String getSerialNumberFromStatusAndUserId(String status, String userid) {
		final String methodName = "getVoucherStatus";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIAL_NO FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE CURRENT_STATUS = ? AND USER_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			pstmt.setString(2, userid);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				serialNumber = QueryResult.getString(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String getSerialNumberForExpiredDate(String status) {
		final String methodName = "getSerialNumberForExpiredDate";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIAL_NO FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE CURRENT_STATUS = ? AND EXPIRY_DATE < CURRENT_DATE  AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				serialNumber = QueryResult.getString(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	/* @Author Shubham Yadav */
	public String getVoucherCurrentExpiryDate(String Voucher) {
		String date = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		Log.info("Fetching Current expiry date for given voucher : " + Voucher);

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT EXPIRY_DATE FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Voucher);

			QueryResult = pstmt.executeQuery();

			date = QueryResult.getString(1);

		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return date;
	}

	public String getApplicableDualCommissioningType(String msisdn) {
		final String methodName = "getApplicableDualCommissioningType";
		Log.info("Entered " + methodName + "()");
		String commissioningType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select * from (SELECT dual_comm_type FROM commission_profile_set_version ");
		sqlSelectBuff
				.append("WHERE comm_profile_set_id = (SELECT comm_profile_set_id FROM channel_users WHERE user_id = ");
		sqlSelectBuff.append("(SELECT user_id FROM users WHERE msisdn = ?)) ");
		sqlSelectBuff.append(
				"AND TO_DATE (SYSDATE, 'DD-MM-YYYY HH24:MI:SS') >= TO_DATE (applicable_from, 'DD-MM-YYYY HH24:MI:SS') ");
		sqlSelectBuff.append("ORDER BY applicable_from DESC) where rownum=1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			commissioningType = QueryResult.getString("dual_comm_type");
		} catch (Exception e) {
			Log.info("Exception while fetching Applicable Dual Commissioning Type: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Dual_Comm_Type (" + commissioningType + ")");
		Log.info("Exiting " + methodName + "()");
		return commissioningType;
	}

	public String getSubscriberMSISDN(String ProductCode) {
		final String methodName = "getSubscriberMSISDN";
		Log.info("Entered " + methodName + "()");
		String subscriberMSIDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT RECEIVER_MSISDN FROM ");
		sqlSelectBuff.append(
				"C2S_TRANSFERS WHERE TRANSFER_STATUS = '200' and PRODUCT_CODE = ? ORDER BY TRANSFER_DATE DESC ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, ProductCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			subscriberMSIDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN  (" + subscriberMSIDN + ")");
		Log.info("Exiting " + methodName + "()");
		return subscriberMSIDN;

	}

	public String getSubscriberMSISDN() {
		final String methodName = "getSubscriberMSISDN";
		Log.info("Entered " + methodName + "()");
		String subscriberMSIDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SUBSCRIBER_ID FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE SUBSCRIBER_ID IS NOT NULL");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			// pstmt.setString(1, ProductCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			subscriberMSIDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN  (" + subscriberMSIDN + ")");
		Log.info("Exiting " + methodName + "()");
		return subscriberMSIDN;

	}

	public String checkSubscriberMSISDNexist(String MSISDN) {
		final String methodName = "checkSubscriberMSISDNexist";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String REC_EXIST = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from P2P_SUBSCRIBERS where MSISDN= ?)");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			REC_EXIST = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN exists (" + REC_EXIST + ")");
		Log.info("Exiting " + methodName + "()");
		return REC_EXIST;
	}

	public String getSubscriberP2PPin(String MSISDN) {
		final String methodName = "getSubscriberP2PPin";
		Log.info("Entered " + methodName + "()");
		String subscriberPIN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT PIN FROM ");
		sqlSelectBuff.append("P2P_SUBSCRIBERS WHERE MSISDN =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			subscriberPIN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN Pin  (" + subscriberPIN + ")");
		Log.info("Exiting " + methodName + "()");
		return subscriberPIN;

	}

	public String getP2PSubscriberMSISDN(String SubType, String status) {
		final String methodName = "getP2PSubscriberMSISDN";
		Log.info("Entered " + methodName + "()");
		String MSISDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MSISDN FROM ");
		sqlSelectBuff.append("P2P_SUBSCRIBERS WHERE SUBSCRIBER_TYPE =? and STATUS =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, SubType);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN   (" + MSISDN + ")");
		Log.info("Exiting " + methodName + "()");
		return MSISDN;

	}

	public boolean checkAmbiguousTransactionsforP2P(String fromDate, String toDate, String service) {
		final String methodname = "checkAmbiguousTransactionsforP2P";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + service + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		int result = 0;

		String DATE_FORMAT = "dd/mm/yy";
		if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
			DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA;
		}

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT count(*) FROM subscriber_transfers WHERE transfer_status IN ('205', '250') ");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?) ");
		sqlSelectBuff.append("and transfer_Date>=TO_DATE(?, ?) and service_type = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			pstmt.setString(5, service);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			Log.info("actual Count: " + QueryResult.getString(1));
			result = Integer.parseInt(QueryResult.getString(1));
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status(" + result + ")");
		Log.info("Exited " + methodname + "()");
		if (result == 0)
			return true;
		else
			return false;
	}

	public String fetchAmbiguousTransactionsforP2P(String fromDate, String toDate, String selectorType) {
		final String methodname = "fetchAmbiguousTransactionsforP2P";
		Log.info("Entered " + methodname + "(" + fromDate + ", " + toDate + ", " + selectorType + ")");
		String transactionID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		String DATE_FORMAT = "dd/mm/yy";
		if (!BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA)) {
			DATE_FORMAT = SystemPreferences.DATE_FORMAT_CAL_JAVA;
		}

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select transfer_id from subscriber_transfers where transfer_status IN ('205', '250') ");
		sqlSelectBuff.append("and transfer_Date<=TO_DATE(?, ?) ");
		sqlSelectBuff.append("and transfer_Date>=TO_DATE(?, ?) and service_type = ?  and rownum='1' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, toDate);
			pstmt.setString(2, DATE_FORMAT);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, DATE_FORMAT);
			pstmt.setString(5, selectorType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transactionID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransactionID(" + transactionID + ")");
		Log.info("Exited " + methodname + "()");
		return transactionID;
	}

	public String fetchTransferStatusforP2P(String transactionID) {
		final String methodname = "fetchTransferStatusforP2P";
		Log.info("Entered " + methodname + "(" + transactionID + ")");
		String transferStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select transfer_status from subscriber_transfers where transfer_id = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transferStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransferStatus(" + transferStatus + ")");
		Log.info("Exited " + methodname + "()");
		return transferStatus;
	}

	public String[] getP2PSubscriber(String... columnNames) {
		final String methodname = "getP2PSubscriber";
		String returnVals[] = new String[2];
		Log.info("Entered " + methodname);
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select MSISDN, PIN from (SELECT * FROM P2P_SUBSCRIBERS ORDER BY CREATED_ON DESC) WHERE ROWNUM = '1' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching subscriber details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String[] getP2PSubscriberWithStatusY(String... columnNames) {
		final String methodname = "getP2PSubscriber";
		String returnVals[] = new String[2];
		Log.info("Entered " + methodname);
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select MSISDN, PIN from (SELECT * FROM P2P_SUBSCRIBERS WHERE STATUS = 'Y' ORDER BY CREATED_ON DESC) WHERE ROWNUM = '1' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching subscriber details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String[] getP2PSubscriberWithStatusS(String... columnNames) {
		final String methodname = "getP2PSubscriber";
		String returnVals[] = new String[2];
		Log.info("Entered " + methodname);
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select MSISDN, PIN from (SELECT * FROM P2P_SUBSCRIBERS WHERE STATUS = 'S' ORDER BY CREATED_ON DESC) WHERE ROWNUM = '1' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching subscriber details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String getSubscriberMSISDNFrombarredlist(String module) {
		final String methodname = "getSubscriberMSISDNfrombarredMSISDN";
		Log.info("Entered " + methodname + "(" + module + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String MSISDN = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select MSISDN from BARRED_MSISDNS where USER_TYPE= 'RECEIVER' and MODULE =? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, module);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Barred MSISDN (" + MSISDN + ")");
		Log.info("Exited " + methodname + "()");
		return MSISDN;
	}

	public String getSerialNumber(String productID) {
		final String methodName = "getSerialNumber";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MIN(SERIAL_NO) FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Min Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public String[] getVomsVoucherDetailsFromSerialNumber(String serialNumber, String... columnNames) {
		final String methodname = "getVomsVoucherDetailsFromSerialNumber";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname);
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT * FROM VOMS_VOUCHERS WHERE SERIAL_NO = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, serialNumber);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching voucher details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String getProductNameFromVOMSProduct(String productID) {
		final String methodName = "getProductNameFromVOMSProduct";
		Log.info("Entered " + methodName + "()");
		String productName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT PRODUCT_NAME FROM ");
		sqlSelectBuff.append("VOMS_PRODUCTS WHERE PRODUCT_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			productName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Product Name (" + productName + ")");
		Log.info("Exiting " + methodName + "()");
		return productName;
	}

	public String getCategoryIDFromVOMSProduct(String productName) {
		final String methodName = "getCategoryIDFromVOMSProduct";
		Log.info("Entered " + methodName + "()");
		String categoryID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT CATEGORY_ID FROM ");
		sqlSelectBuff.append("VOMS_PRODUCTS WHERE PRODUCT_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			categoryID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Product Name (" + categoryID + ")");
		Log.info("Exiting " + methodName + "()");
		return categoryID;
	}

	public String getProducTypeFromVOMSCategory(String categoryID) {
		final String methodName = "getProductTypeFromVOMSCategory";
		Log.info("Entered " + methodName + "()");
		String productType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT VOUCHER_TYPE FROM ");
		sqlSelectBuff.append("VOMS_CATEGORIES WHERE CATEGORY_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, categoryID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			productType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Product Name (" + productType + ")");
		Log.info("Exiting " + methodName + "()");
		return productType;
	}

	public String getBatchNumber(String productID, String status) {
		final String methodName = "getBatchNumber";
		Log.info("Entered " + methodName + "()");
		String batchNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT BATCH_NO FROM (SELECT * FROM VOMS_BATCHES WHERE PRODUCT_ID = ? AND STATUS = ?");
		sqlSelectBuff.append("ORDER BY CREATED_DATE DESC) where rownum = '1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			batchNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while Batch Number: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Batch Number (" + batchNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return batchNumber;
	}

	public String[] getUserDetails_combined(String loginID, String MSISDN, String... columnNames) {
		final String methodname = "getUserDetails_combined";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + loginID + ", " + MSISDN + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT cu.user_id, cu.user_grade, cg.grade_name, cu.comm_profile_set_id, cps.comm_profile_set_name, cu.transfer_profile_id, tp.profile_name, ugeo.grph_domain_code, geo.grph_domain_name ");
		sqlSelectBuff.append(
				"FROM channel_users cu LEFT JOIN commission_profile_set cps ON cu.comm_profile_set_id = cps.comm_profile_set_id LEFT JOIN transfer_profile tp ON cu.transfer_profile_id = tp.profile_id ");
		sqlSelectBuff.append(
				"LEFT JOIN user_geographies ugeo ON cu.user_id = ugeo.user_id LEFT JOIN geographical_domains geo ON ugeo.grph_domain_code = geo.grph_domain_code LEFT JOIN channel_grades cg ON cu.user_grade = cg.grade_code ");
		sqlSelectBuff.append("WHERE cu.user_id = (SELECT user_id FROM users WHERE login_id = ? AND msisdn = ?)");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, loginID);
			statement.setString(2, MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details combined: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String getVoucherType(String typeCode) {
		final String methodName = "getVoucherType";
		Log.info("Entered " + methodName + "()");
		String voucherType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT VOUCHER_TYPE FROM ");
		sqlSelectBuff.append("VOMS_TYPES WHERE TYPE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, typeCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			voucherType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Type (" + voucherType + ")");
		Log.info("Exiting " + methodName + "()");
		return voucherType;
	}

	public Object[][] getVOMSDetailsBasedOnVoucherType(String voucherType) {
		final String methodname = "getVOMSDetailsBasedOnVoucherType";
		Log.info("Entered " + methodname + "()");
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		Object[][] resultObj = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT vt.voucher_type,vt.type, vvsm.service_type, ");
		sqlSelectBuff.append(
				"stsm.selector_name FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE_SELECTOR_MAPPING stsm ");
		sqlSelectBuff.append(
				"where vvsm.voucher_type=vt.voucher_type and vvsm.service_type=stsm.service_type and vvsm.sub_service=stsm.selector_code and vvsm.voucher_type = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, voucherType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			ResultSetMetaData meta = QueryResult.getMetaData();
			int columnCount = meta.getColumnCount();
			resultObj = new Object[rowCount][columnCount];
			int i = 0;
			while (QueryResult.next()) {
				resultObj[i][0] = QueryResult.getString("voucher_type");
				resultObj[i][1] = QueryResult.getString("type");
				resultObj[i][2] = QueryResult.getString("service_type");
				resultObj[i][3] = QueryResult.getString("selector_name");
				i++;
			}
		} catch (Exception e) {
			Log.info("Error while fetching preparing Result Object: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return resultObj;
	}

	public String getOTFValue(String username, String profileDetailsID) {
		final String methodName = "getOTFValue";
		Log.info("Entered " + methodName + "(" + profileDetailsID + ")");
		String OTFValue = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select OTF_VALUE from USER_TRANSFER_OTF_COUNT where USER_ID = (select USER_ID from USERS where USER_NAME = ?) and PRFLE_OTF_DETAIL_ID = (select min(PRFLE_OTF_DETAIL_ID) from PROFILE_OTF_DETAILS where PROFILE_DETAIL_ID = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, username);
			pstmt.setString(2, profileDetailsID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			OTFValue = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching OTF Value: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: OTFValue (" + OTFValue + ")");
		Log.info("Exiting " + methodName + "()");
		return OTFValue;
	}

	public String getVoucherTypeForUser(String msisdn) {
		final String methodName = "getVoucherTypeForUser";
		Log.info("Entered " + methodName + "(" + msisdn + ")");
		String voucherType = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		String userID = getUserIdFromMsisdn(msisdn);

		Log.info("User ID is " + userID);

		String sqlSelect = "select voucher_type from user_voucherTypes where user_id = ?";

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			voucherType = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching voucher type: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: voucherType (" + voucherType + ")");
		Log.info("Exiting " + methodName + "()");
		return voucherType;
	}

	public String getCommProfileDetailsID(String profileName, String productCode, String version) {
		final String methodName = "getCommProfileDetailsID";
		Log.info("Entered " + methodName + "(" + profileName + ")");
		String profileDetailID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT min(comm_profile_detail_id) FROM commission_profile_details WHERE comm_profile_products_id = ");
		sqlSelectBuff.append(
				"(SELECT comm_profile_products_id FROM commission_profile_products WHERE comm_profile_set_id =");
		sqlSelectBuff.append(
				"(SELECT comm_profile_set_id FROM commission_profile_set WHERE comm_profile_set_name = ?) AND product_code = ? AND comm_profile_set_version = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, profileName);
			pstmt.setString(2, productCode);
			pstmt.setString(3, version);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			profileDetailID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching profileDetailID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: profileDetailID (" + profileDetailID + ")");
		Log.info("Exiting " + methodName + "()");
		return profileDetailID;
	}

	public String[] preferenceModifyAllowed(String Preference_Code) {
		String methodname = "preferenceModifyAllowed";
		Log.info("Entered (" + methodname + ", " + Preference_Code + ")");

		String[] prefVal = new String[4];
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();

			StringBuffer QueryBuffer = new StringBuffer(
					"select NAME,DEFAULT_VALUE, MODIFIED_ALLOWED, DISPLAY from SYSTEM_PREFERENCES where ");
			QueryBuffer.append("PREFERENCE_CODE = '" + Preference_Code + "'");
			QueryResult = statement.executeQuery(QueryBuffer.toString());

			QueryResult.next();
			prefVal[0] = QueryResult.getString(1).toString();
			prefVal[1] = QueryResult.getString(2).toString();
			prefVal[2] = QueryResult.getString(3).toString();
			prefVal[3] = QueryResult.getString(4).toString();
		} catch (Exception e) {
			Log.info("Error while fetching Preference");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: VALUES (" + Arrays.toString(prefVal) + ")");
		return prefVal;
	}

	public String[] defaultTCP(String categoryCode) {
		String methodname = "defaultTCP";
		Log.info("Entered (" + methodname + ", " + categoryCode + ")");

		String[] tcp = new String[2];
		Connection connection = null;
		PreparedStatement statement = null, statement1 = null, statement2 = null;
		ResultSet QueryResult = null;

		StringBuffer QueryBuffer = new StringBuffer(
				"Select profile_id,profile_name from transfer_profile where category_code=? and is_default='Y'");
		String sqlSelect = QueryBuffer.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, categoryCode);
			Log.info("Selecting TCP with IS_DEFAULT from DB.");
			QueryResult = statement.executeQuery();
			QueryResult.next();
			tcp[0] = QueryResult.getString(1).toString();
			tcp[1] = QueryResult.getString(2).toString();
		} catch (Exception e) {
			Log.info("Error while fetching deafult TCP");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: VALUES (" + Arrays.toString(tcp) + ")");
		return tcp;
	}

	public void updateAnyColumnValue(String tableName, String columntomodify, String valueColumntomodify,
			String columntorefer, String valueofcolumntorefer) {
		Log.methodEntry("updateAnyColumnValue", tableName, columntomodify, valueColumntomodify, columntorefer,
				valueofcolumntorefer);

		Connection connection = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;

		StringBuffer QueryBuffer1 = new StringBuffer(
				"Update " + tableName + " set " + columntomodify + " = ? where " + columntorefer + "=?");
		String sqlSelect1 = QueryBuffer1.toString();
		StringBuilder QueryBuffer2 = new StringBuilder("COMMIT");
		String sqlSelect2 = QueryBuffer2.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement1 = connection.prepareStatement(sqlSelect1);
			statement1.setString(1, valueColumntomodify);
			statement1.setString(2, valueofcolumntorefer);
			Log.info("Updating " + columntorefer + "[" + valueofcolumntorefer + "] " + columntomodify + " to: "
					+ valueColumntomodify);
			statement1.executeQuery();
			statement2 = connection.prepareStatement(sqlSelect2);
			Log.info("Commit the changes in DB.");
			statement2.executeQuery();

		} catch (Exception e) {
			Log.info("Error while updating and commiting.");
			Log.writeStackTrace(e);
		} finally {
			if (statement1 != null)
				try {
					statement1.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement2 != null)
				try {
					statement2.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
	}

	public String[] defaultCommission(String categoryCode, String networkCode) {
		String methodname = "defaultCommission";
		Log.info("Entered (" + methodname + ", " + categoryCode + "," + networkCode + ")");

		String[] commission = new String[2];
		Connection connection = null;
		PreparedStatement statement = null, statement1 = null, statement2 = null;
		ResultSet QueryResult = null;

		StringBuffer QueryBuffer = new StringBuffer(
				"Select comm_profile_set_id,comm_profile_set_name from commission_profile_set where category_code=? and network_code=? and is_default='Y'");
		String sqlSelect = QueryBuffer.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, categoryCode);
			statement.setString(2, networkCode);
			Log.info("Selecting commission_profile with IS_DEFAULT from DB.");
			QueryResult = statement.executeQuery();
			QueryResult.next();
			commission[0] = QueryResult.getString(1).toString();
			commission[1] = QueryResult.getString(2).toString();
		} catch (Exception e) {
			Log.info("Error while fetching deafult commission");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: VALUES (" + Arrays.toString(commission) + ")");
		return commission;
	}

	public String[] defaultGrade(String categoryCode) {
		String methodname = "defaultCommission";
		Log.info("Entered (" + methodname + ", " + categoryCode + ")");

		String[] grade = new String[2];
		Connection connection = null;
		PreparedStatement statement = null, statement1 = null, statement2 = null;
		ResultSet QueryResult = null;

		StringBuffer QueryBuffer = new StringBuffer(
				"Select grade_code,grade_name from channel_grades where category_code=? and is_default_grade='Y'");
		String sqlSelect = QueryBuffer.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, categoryCode);
			Log.info("Selecting grade with IS_DEFAULT from DB.");
			QueryResult = statement.executeQuery();
			QueryResult.next();
			grade[0] = QueryResult.getString(1).toString();
			grade[1] = QueryResult.getString(2).toString();
		} catch (Exception e) {
			Log.info("Error while fetching deafult grade");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: VALUES (" + Arrays.toString(grade) + ")");
		return grade;
	}

	public String existingEXTCODE() {
		final String methodName = "existingEXTCODE";
		Log.info("Entered " + methodName + "()");
		String extCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EXTERNAL_CODE from ");
		sqlSelectBuff.append("(select * from users where user_type <> 'OPERATOR' and status='Y' and network_code='"
				+ _masterVO.getMasterValue(MasterI.NETWORK_CODE) + "' order by dbms_random.value()) ");
		sqlSelectBuff.append("where rownum='1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			extCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching ExternalCode: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: ExternalCode (" + extCode + ")");
		Log.info("Exiting " + methodName + "()");
		return extCode;
	}

	public String SubscriberStatus(String subMSISDN) {
		final String methodName = "getSubscriberStatus";
		Log.info("Entered " + methodName + "()");
		String SubStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT STATUS FROM ");
		sqlSelectBuff.append("RESTRICTED_MSISDNS WHERE MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, subMSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			SubStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching SubscriberStatus: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber Status (" + SubStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return SubStatus;

	}

	public String SubscriberBlacklistStatus(String subMSISDN) {
		final String methodName = "getSubscriberStatus";
		Log.info("Entered " + methodName + "()");
		String SubBlacklistStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT BLACK_LIST_STATUS FROM ");
		sqlSelectBuff.append("RESTRICTED_MSISDNS WHERE MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, subMSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			SubBlacklistStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching SubBlacklistStatus: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber Blacklist Status (" + SubBlacklistStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return SubBlacklistStatus;

	}

	public String checkForSubscriberExistence(String subMSISDN) {
		final String methodName = "checkSubscriberMSISDNexist";
		Log.info("Entered " + methodName + "(" + subMSISDN + ")");
		String REC_EXIST = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from RESTRICTED_MSISDNS where MSISDN= ?)");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, subMSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			REC_EXIST = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching MSISDN Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN exists (" + REC_EXIST + ")");
		Log.info("Exiting " + methodName + "()");
		return REC_EXIST;
	}

	public String[] fetchRoleName(String pageCode) {
		final String methodName = "fetchRoleName";
		Log.info("Entered " + methodName + "(" + pageCode + ")");
		String[] role_name_code = new String[2];
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select role_code,role_name from roles ");
		sqlSelectBuff.append("where role_code=(Select role_code from page_roles where page_code=?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, pageCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			role_name_code[0] = QueryResult.getString(1);
			role_name_code[1] = QueryResult.getString(2);
		} catch (Exception e) {
			Log.info("Error while fetching RoleName Details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Role Code and Name  (" + Arrays.toString(role_name_code) + ")");
		Log.info("Exiting " + methodName + "()");
		return role_name_code;
	}

	public String check_PIN_REQUIRED(String prefCode) {
		final String methodName = "check_PIN_REQUIRED_C2S";
		Log.info("Entered " + methodName + "()");
		String DEFAULTVALUE = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DEFAULT_VALUE FROM ");
		sqlSelectBuff.append("SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, prefCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			DEFAULTVALUE = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching DEFAULTVALUE: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULTVALUE (" + DEFAULTVALUE + ")");
		Log.info("Exiting " + methodName + "()");
		return DEFAULTVALUE;

	}

	public String get_post_balance(String TXNID) {
		final String methodName = "get_post_balance";
		Log.info("Entered " + methodName + "()");
		String balance = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SENDER_POST_BALANCE FROM ");
		sqlSelectBuff.append("C2S_TRANSFERS WHERE TRANSFER_ID= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TXNID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			balance = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Balance: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: balance (" + balance + ")");
		Log.info("Exiting " + methodName + "()");
		return balance;

	}

	public String getNetworkPrefix(String seriesType, String status) {
		final String methodName = "getNetworkPrefix";
		Log.info("Entered " + methodName + "()");
		String networkPrefix = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIES FROM ");
		sqlSelectBuff.append("NETWORK_PREFIXES WHERE SERIES_TYPE= ? AND STATUS = ? AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, seriesType);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			networkPrefix = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Balance: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: networkPrefix (" + networkPrefix + ")");
		Log.info("Exiting " + methodName + "()");
		return networkPrefix;

	}

	public String getNetworkPrefixFromNetwork(String seriesType, String status) {
		final String methodName = "getNetworkPrefixFromNetwork";
		Log.info("Entered " + methodName + "()");
		String networkPrefix = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIES FROM ");
		sqlSelectBuff.append("NETWORK_PREFIXES WHERE NETWORK_CODE <> ? AND STATUS = ? AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, seriesType);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			networkPrefix = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Balance: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: networkPrefix (" + networkPrefix + ")");
		Log.info("Exiting " + methodName + "()");
		return networkPrefix;

	}

	public String getOtherNetworkPrefix(String operator, String status) {
		final String methodName = "getOtherNetworkPrefix";
		Log.info("Entered " + methodName + "()");
		String networkPrefix = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT * FROM ");
		sqlSelectBuff.append("NETWORK_PREFIXES WHERE OPERATOR= ? AND STATUS = ?) WHERE ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, operator);
			pstmt.setString(2, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			networkPrefix = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Balance: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: networkPrefix (" + networkPrefix + ")");
		Log.info("Exiting " + methodName + "()");
		return networkPrefix;
	}

	public String get_department_name(String loginID) {
		final String methodName = "get_department_name";
		Log.info("Entered " + methodName + "()");
		String name = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DEPARTMENT FROM ");
		sqlSelectBuff.append("USERS WHERE LOGIN_ID= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			name = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching department name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Department (" + name + ")");
		Log.info("Exiting " + methodName + "()");
		return name;

	}

	public String get_division_name(String loginID) {
		final String methodName = "get_division_name";
		Log.info("Entered " + methodName + "()");
		String name = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DIVISION FROM ");
		sqlSelectBuff.append("USERS WHERE LOGIN_ID= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			name = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching division name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Division (" + name + ")");
		Log.info("Exiting " + methodName + "()");
		return name;

	}

	/**
	 * Query for fetching user balance
	 *
	 * @return: balance
	 * @author simarnoor.bains
	 */
	public String getUserSumBalance(String LoginID_OR_MSISDN) {
		final String methodname = "getUserSumBalance";
		Log.info("Entered " + methodname + "(" + LoginID_OR_MSISDN + ")");
		String usrBalance = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT sum(balance) as balance FROM user_balances WHERE ");
		sqlSelectBuff.append("user_id = (SELECT user_id FROM users WHERE login_id = ? OR MSISDN = ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LoginID_OR_MSISDN);
			pstmt.setString(2, LoginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			usrBalance = QueryResult.getString("balance");
		} catch (Exception e) {
			Log.info("Error while fetching balance");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: User Balance(" + usrBalance + ")");
		Log.info("Exited " + methodname + "()");
		return usrBalance;
	}

	public String getdepartmentCode(String dept_name) {
		final String methodName = "getdepartmentCode";
		Log.info("Entered " + methodName + "(" + dept_name + ")");
		String dept_code = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT divdept_id FROM division_department ");
		sqlSelectBuff.append("WHERE divdept_name=? AND divdept='DEPARTMENT'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, dept_name);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			dept_code = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching departmentCode: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Department Code (" + dept_code + ")");
		Log.info("Exiting " + methodName + "()");
		return dept_code;
	}

	public String getdivisionCode(String div_name) {
		final String methodName = "getdivisionCode";
		Log.info("Entered " + methodName + "(" + div_name + ")");
		String div_code = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT divdept_id FROM division_department ");
		sqlSelectBuff.append("WHERE divdept_name=? AND divdept='DIVISION'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, div_name);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			div_code = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching divisionCode: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Division Code (" + div_code + ")");
		Log.info("Exiting " + methodName + "()");
		return div_code;
	}

	/**
	 * @author: Shallu
	 * @see com.dbrepository.DBInterface#getCardGroupStatus(java.lang.String, int,
	 *      int)
	 */
	public String getCardGroupVersion(String CardGroupName) {
		final String methodName = "getCardGroupVersion";
		Log.info("Entered " + methodName + "(" + CardGroupName + ")");
		String version = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT version FROM card_group_details ");
		sqlSelectBuff.append(
				"WHERE card_group_set_id = (select CARD_GROUP_SET_ID from CARD_GROUP_SET where CARD_GROUP_SET_NAME= ?)");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			version = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Version (" + version + ")");
		Log.info("Exiting " + methodName + "()");
		return version;
	}

	public String getCardGroupVersionActive(String CardGroupName) {
		final String methodName = "getCardGroupVersion";
		Log.info("Entered " + methodName + "(" + CardGroupName + ")");
		String version = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT LAST_VERSION FROM CARD_GROUP_SET ");
		sqlSelectBuff.append("WHERE STATUS= 'Y' and CARD_GROUP_SET_NAME= ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, CardGroupName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			version = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Version (" + version + ")");
		Log.info("Exiting " + methodName + "()");
		return version;
	}

	public String getP2PSubscriberMSISDNSeq(String SubType, String status, String seq) {
		final String methodName = "getP2PSubscriberMSISDN";
		Log.info("Entered " + methodName + "()");
		String MSISDN = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MSISDN FROM ");
		sqlSelectBuff.append(
				"(SELECT MSISDN, ROWNUM RN FROM P2P_SUBSCRIBERS WHERE SUBSCRIBER_TYPE =? and STATUS =?) where RN =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, SubType);
			pstmt.setString(2, status);
			pstmt.setString(3, seq);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			MSISDN = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN   (" + MSISDN + ")");
		Log.info("Exiting " + methodName + "()");
		return MSISDN;

	}

	public String getCellGroupCode() {
		final String methodName = "getcellGroupCode";
		Log.info("Entered " + methodName + "()");
		String code = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select group_name from (Select group_name from cell_groups where group_id in ");
		sqlSelectBuff.append("(select group_id from cell_ids)  order by dbms_random.value()) where rownum='1'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			code = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: cell group code   (" + code + ")");
		Log.info("Exiting " + methodName + "()");
		return code;

	}

	public String[] getdetailsfromUsersTable(String loginID_OR_MSISDN, String... columnNames) {
		final String methodname = "getdetailsfromUsersTable";
		String returnVals[] = new String[columnNames.length];
		Log.info("Entered " + methodname + "(" + loginID_OR_MSISDN + ", " + Arrays.toString(columnNames) + ")");
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select * from users where ");
		sqlSelectBuff.append("users.login_id = ? OR users.MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, loginID_OR_MSISDN);
			statement.setString(2, loginID_OR_MSISDN);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching user details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	@Override
	public String getPinBlockCount(String msisdn) {
		final String methodName = "getSubscriberP2PPin";
		Log.info("Entered " + methodName + "()");
		String subscriberPINCount = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT PIN_BLOCK_COUNT FROM ");
		sqlSelectBuff.append("P2P_SUBSCRIBERS WHERE MSISDN =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			subscriberPINCount = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Subscriber MSISDN Pin count  (" + subscriberPINCount + ")");
		Log.info("Exiting " + methodName + "()");
		if (subscriberPINCount == null)
			return "0";
		else
			return subscriberPINCount;

	}

	@Override
	public String[] checkForOTFApplicable(String TRANSFER_ID) {
		final String methodName = "checkForOTFApplicable";
		Log.info("Entered " + methodName + "()");
		String otfDetails[] = new String[2];
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT OTF_APPLICABLE,OTF_AMOUNT FROM ");
		sqlSelectBuff.append("CHANNEL_TRANSFERS_ITEMS WHERE TRANSFER_ID =?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, TRANSFER_ID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			otfDetails[0] = QueryResult.getString(1);
			otfDetails[1] = QueryResult.getString(2);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Exiting " + methodName + "()");
		return otfDetails;
	}

	public String checkForUniqueUserIDPrefix(String userIDPrefix) {
		final String methodname = "checkForUniqueUserIDPrefix";
		Log.info("Entered " + methodname + "(" + userIDPrefix + ")");
		String UserIDPrefixStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from categories where user_id_prefix = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, userIDPrefix);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			UserIDPrefixStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching user_ID_prefix status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_id_prefix Status (" + UserIDPrefixStatus + ")");
		Log.info("Exited " + methodname + "()");
		return UserIDPrefixStatus;
	}

	public String checkForUniqueVoucherSNO(String voucherSNO) {
		final String methodname = "checkForUniqueVoucherSNO";
		Log.info("Entered " + methodname + "(" + voucherSNO + ")");
		String VoucherSNOStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from voms_vouchers where serial_no = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, voucherSNO);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			VoucherSNOStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching voucher serial number status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: voucherSNO Status (" + VoucherSNOStatus + ")");
		Log.info("Exited " + methodname + "()");
		return VoucherSNOStatus;
	}

	public String checkForUniqueSNOForVoucherGen(String voucherSNO) {
		final String methodname = "checkForUniqueSNOForVoucherGen";
		Log.info("Entered " + methodname + "(" + voucherSNO + ")");
		String snoStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append(
				"(select 1 from (Select Unique SUBSTR(serial_no,1,5) as Serial from voms_vouchers) where serial = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, voucherSNO);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			snoStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching serial number status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SNO Status (" + snoStatus + ")");
		Log.info("Exited " + methodname + "()");
		return snoStatus;
	}

	// Created by Siddharth Tomer : Used to extract OTP fro C2C Consent
	public String getConsentOTP(String MSISDN1, String MSISN2, String TXNID) {
		final String methodname = "getConsentOTP";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;
		String OTP = null;
		StringBuilder sqlSelectBuff = new StringBuilder("Select OTP from reversal_consent WHERE ");
		sqlSelectBuff.append("sender_msisdn =? and receiver_msisdn = ? and transfer_id =?");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, MSISDN1);
			statement.setString(2, MSISN2);
			statement.setString(3, TXNID);
			QueryResult = statement.executeQuery();
			OTP = QueryResult.getString(1);
		} catch (IOException e) {
			Log.info("Error while fetching OTP");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		return OTP;
	}

	public String getUserId(String userName) {
		final String methodName = "getUserId";
		Log.info("Entered " + methodName + "(" + userName + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select USER_ID from USERS where USER_NAME = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			userName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_Name (" + userName + ")");
		Log.info("Exiting " + methodName + "()");
		return userName;
	}

	public String getUserIdFromMsisdn(String msisdn) {
		final String methodName = "getUserId";
		Log.info("Entered " + methodName + "(" + msisdn + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select USER_ID from USERS where MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			msisdn = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_Name (" + msisdn + ")");
		Log.info("Exiting " + methodName + "()");
		return msisdn;
	}

	public String getLoginidFromMsisdn(String msisdn) {
		final String methodName = "getLoginidFromMsisdn";
		Log.info("Entered " + methodName + "(" + msisdn + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select LOGIN_ID from USERS where MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();
		String loginid = null;

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			loginid = QueryResult.getString(1);

		} catch (Exception e) {
			Log.info("Exception while fetching LOGINID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_Name (" + msisdn + ")");
		Log.info("Exiting " + methodName + "()");
		return loginid;
	}

	public String getExternalCodeFromMsisdn(String msisdn) {
		final String methodName = "getExternalCodeFromMsisdn";
		Log.info("Entered " + methodName + "(" + msisdn + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EXTERNAL_CODE from USERS where MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();
		String EXTERNAL_CODE = null;

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			EXTERNAL_CODE = QueryResult.getString(1);

		} catch (Exception e) {
			Log.info("Exception while fetching EXTERNAL CODE: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: EXTERNAL_CODE (" + EXTERNAL_CODE + ")");
		Log.info("Exiting " + methodName + "()");
		return EXTERNAL_CODE;
	}

	/* @author : ashmeet.saggu */
	public String getUsernameFromMsisdn(String msisdn) {
		final String methodName = "getUsernameFromMsisdn";
		Log.info("Entered " + methodName + "(" + msisdn + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select USER_NAME from USERS where MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();
		String username = null;

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, msisdn);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			username = QueryResult.getString(1);

		} catch (Exception e) {
			Log.info("Exception while fetching LOGINID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_Name (" + msisdn + ")");
		Log.info("Exiting " + methodName + "()");
		return username;
	}

	public String getType(String vouchertType) {
		final String methodName = "getVoucherType";
		Log.info("Entered " + methodName + "()");
		String type = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT TYPE FROM ");
		sqlSelectBuff.append("VOMS_TYPES WHERE VOUCHER_TYPE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, vouchertType);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			type = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Type (" + type + ")");
		Log.info("Exiting " + methodName + "()");
		return type;
	}

	@Override
	public String[] getP2PSubscriberWithRequestStatusU(String... columnNames) {
		final String methodname = "getP2PSubscriber";
		String returnVals[] = new String[2];
		Log.info("Entered " + methodname);
		String interfaceExtIDStatus = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select MSISDN, PIN from (SELECT * FROM P2P_SUBSCRIBERS WHERE REQUEST_STATUS = 'U' ORDER BY CREATED_ON DESC) WHERE ROWNUM = '1' ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();

			for (int i = 0; i < columnNames.length; i++) {
				returnVals[i] = QueryResult.getString(columnNames[i]);
			}

		} catch (Exception e) {
			Log.info("Error while fetching subscriber details: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + Arrays.toString(returnVals));
		Log.info("Exited " + methodname + "()");
		return returnVals;
	}

	public String getSerialNumberAssignedToUser(String status, String networkCode) {
		final String methodName = "getSerialNumberAssignedToUser";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERIAL_NO FROM ");
		sqlSelectBuff.append(
				"VOMS_VOUCHERS WHERE CURRENT_STATUS = ? AND USER_NETWORK_CODE = ? AND EXPIRY_DATE > CURRENT_DATE AND USER_ID IS NOT NULL  AND ROWNUM = 1");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			pstmt.setString(2, networkCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				serialNumber = QueryResult.getString(1);
			}
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Serial Number (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public ResultSet fetchVouchersFromTxnId(String txnId) {
		final String methodName = "fetchVouchersFromTxnId";
		Log.debug("Entered " + methodName + "()");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select serial_no,master_serial_no");
		sqlSelectBuff.append(" from voms_vouchers");
		sqlSelectBuff.append(" where last_transaction_id = ?");
		sqlSelectBuff.append(" order by serial_no asc");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.debug(methodName + "() :: select query: " + sqlSelect);
			pstmt.setString(1, txnId);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.debug("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(ReturnCachedResult);
		Log.debug("Exiting " + methodName + "()");
		return ReturnCachedResult;
	}

	public int rollbackDateForProcess(String processId) {
		final String methodName = "rollbackDateForProcess";
		Log.info("Entered " + methodName + "(" + processId + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		int count = 0;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"update process_status set executed_upto = sysdate - 2 where process_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, processId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			Log.info("Exception while updating process executed upto ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Exiting " + methodName + "() : updateCount = " + count);
		return count;
	}

	public Boolean isVomsBatchWithStatusPresent(String processId, String batchType, String status) {
		final String methodName = "isVomsBatchWithStatusPresent";
		Log.info("Entered " + methodName + " : processId-" + processId + " : batchType-" + batchType + " : status-"
				+ status);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		boolean isRowFetched = false;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select * from voms_batches where product_id = ? and batch_type = ? and status = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, processId);
			pstmt.setString(2, batchType);
			pstmt.setString(3, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			if (QueryResult.next())
				isRowFetched = true;
		} catch (Exception e) {
			Log.info("Exception while selecting from voms_batches ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Exiting " + methodName + "() : isRowFetched = " + isRowFetched);
		return isRowFetched;
	}

	public String getCommProfileID(String profileName) {
		final String methodname = "getCommProfileVersion";
		Log.info("Entered " + methodname + "(" + profileName + ")");
		String id = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("");
		sqlSelectBuff.append("Select COMM_PROFILE_SET_ID from COMMISSION_PROFILE_SET where COMM_PROFILE_SET_NAME= ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, profileName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			id = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + id + ")");
		Log.info("Exited " + methodname + "()");
		return id;
	}

	@Override
	public void updateAnyColumnDateValue(String tableName, String columntomodify, java.util.Date valueColumntomodify,
			String columntorefer, String valueofcolumntorefer) {

		Log.methodEntry("updateAnyColumnDateValue", tableName, columntomodify, valueColumntomodify, columntorefer,
				valueofcolumntorefer);

		Connection connection = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;

		StringBuffer QueryBuffer1 = new StringBuffer(
				"Update " + tableName + " set " + columntomodify + " = ? where " + columntorefer + "=?");
		String sqlSelect1 = QueryBuffer1.toString();
		StringBuilder QueryBuffer2 = new StringBuilder("COMMIT");
		String sqlSelect2 = QueryBuffer2.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement1 = connection.prepareStatement(sqlSelect1);
			statement1.setDate(1, BTSLDateUtil.getSQLDateFromUtilDate(valueColumntomodify));
			statement1.setString(2, valueofcolumntorefer);
			Log.info("Updating " + columntorefer + "[" + valueofcolumntorefer + "] " + columntomodify + " to: "
					+ valueColumntomodify);
			statement1.executeQuery();
			statement2 = connection.prepareStatement(sqlSelect2);
			Log.info("Commit the changes in DB.");
			statement2.executeQuery();

		} catch (Exception e) {
			Log.info("Error while updating and commiting.");
			Log.writeStackTrace(e);
		} finally {
			if (statement1 != null)
				try {
					statement1.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement2 != null)
				try {
					statement2.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

	}

	public String getTransactionIDO2C(String status) {
		final String methodName = "getTransactionIDO2C";
		Log.info("Entered " + methodName + "(" + status + ")");
		String Transaction_ID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select TRANSFER_ID from CHANNEL_TRANSFERS where status = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, status);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			Transaction_ID = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + Transaction_ID + ")");
		Log.info("Exiting " + methodName + "()");
		return Transaction_ID;
	}

	public String getTransferProfileID(String networkCode, String categoryCOde, String parent_id) {
		final String methodName = "getTransferProfileID";
		Log.info("Entered " + methodName + "(" + networkCode + ")");
		String Transaction_ID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT profile_id FROM transfer_profile where network_code = ? AND category_code = ? AND parent_profile_id = ? and status<>?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, networkCode);
			pstmt.setString(2, categoryCOde);
			pstmt.setString(3, parent_id);
			pstmt.setString(4, "N");
			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			Transaction_ID = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Network Name: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Network Name (" + Transaction_ID + ")");
		Log.info("Exiting " + methodName + "()");
		return Transaction_ID;
	}

	@Override
	public String getC2CTransactionID(Boolean isFileRequired) {

		final String methodName = "getC2CTransactionID";
		Log.info("Entered " + methodName + ". Is transaction required with file: " + isFileRequired);
		String transfer_id = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		StringBuilder sqlSelectBuff = new StringBuilder("");

		if (isFileRequired) {
			sqlSelectBuff.append("SELECT transfer_id FROM CHANNEL_TRANSFERS WHERE APPROVAL_DOC IS NOT NULL");
		} else {
			sqlSelectBuff.append("SELECT transfer_id FROM CHANNEL_TRANSFERS WHERE APPROVAL_DOC IS NULL");
		}

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);

			Log.info(methodName + "() :: select query: " + sqlSelect);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			transfer_id = rs.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id: ");
			Log.writeStackTrace(e);
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: transfer_id (" + transfer_id + ")");
		Log.info("Exiting " + methodName + "()");
		return transfer_id;

	}

	public Boolean checkForUniqueValueByColumn(String tableName, String columnName, String value) {
		final String methodName = "checkForUniqueValueByColumn";
		Log.info("Entered " + methodName + " tableName: " + tableName + " columnName: " + columnName + " value: "
				+ value);
		String status = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from ").append(tableName).append(" where ").append(columnName)
				.append(" = ? ) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, tableName);
			pstmt.setString(2, columnName);
			pstmt.setString(3, value);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			status = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching CommissionProfileName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns Status (" + status + ")");
		Log.info("Exiting " + methodName + "()");
		return ("Y".equals(status)) ? false : true;
	}

	public ResultSet getLookupByType(String lookupType) {
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();
			Log.info("Entered :: " + Thread.currentThread().getStackTrace()[1].getMethodName() + "()");
			StringBuffer QueryBuffer = new StringBuffer(
					"select LOOKUP_NAME , LOOKUP_CODE from LOOKUPS where LOOKUP_TYPE='" + lookupType + "' and status ='"
							+ PretupsI.YES + "'");
			Log.info("Query: " + QueryBuffer.toString());
			QueryResult = statement.executeQuery(QueryBuffer.toString());
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ReturnCachedResult;
	}

	public ResultSet getOtherCommissionProfileDetails(String MSISDN, String ProductCode, String requestedQuantity) {
		final String methodname = "getOtherCommissionProfileDetails";
		Log.debug("Entered " + methodname + "(" + MSISDN + ", " + ProductCode + ", " + requestedQuantity + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		boolean otherCommissionApplicable = false;

		if ("true".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL)))
			otherCommissionApplicable = true;

		final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder();
		strBuffSelectCProfileProdDetail
				.append("SELECT  ocpd.oth_commission_type, ocpd.oth_commission_rate, ocps.oth_comm_prf_type ");
		strBuffSelectCProfileProdDetail
				.append(" FROM commission_profile_details cpd LEFT JOIN commission_profile_products cpp ");
		strBuffSelectCProfileProdDetail.append(
				" ON cpd.comm_profile_products_id = cpp.comm_profile_products_id INNER JOIN commission_profile_set cps ");
		strBuffSelectCProfileProdDetail.append(
				" ON cpp.comm_profile_set_id = cps.comm_profile_set_id AND cpp.comm_profile_set_version = cps.comm_last_version ");
		strBuffSelectCProfileProdDetail
				.append(" RIGHT JOIN channel_users cu ON cpp.comm_profile_set_id = cu.comm_profile_set_id ");
		strBuffSelectCProfileProdDetail.append(
				" LEFT JOIN commission_profile_set_version cpsv ON ( cpsv.comm_profile_set_version = cps.comm_last_version AND cpsv.comm_profile_set_id = cps.comm_profile_set_id) ");
		strBuffSelectCProfileProdDetail
				.append(" LEFT JOIN other_comm_prf_set ocps ON ocps.oth_comm_prf_set_id = cpsv.oth_comm_prf_set_id ");
		strBuffSelectCProfileProdDetail.append(
				" LEFT JOIN other_comm_prf_details ocpd ON ( ocpd.oth_comm_prf_set_id = ocps.oth_comm_prf_set_id ");
		strBuffSelectCProfileProdDetail.append(" AND ocpd.start_range <= ? AND ocpd.end_range >= ? ) ");
		strBuffSelectCProfileProdDetail.append(" RIGHT JOIN users u ON cu.user_id = u.user_id WHERE u.msisdn = ? ");
		strBuffSelectCProfileProdDetail
				.append(" AND u.status != ? AND cpp.product_code = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		String sqlSelect = strBuffSelectCProfileProdDetail.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			int i = 1;
			pstmt.setLong(i++, _parser.getSystemAmount(requestedQuantity));
			pstmt.setLong(i++, _parser.getSystemAmount(requestedQuantity));
			pstmt.setString(i++, MSISDN);
			pstmt.setString(i++, "N");
			pstmt.setString(i++, ProductCode);
			pstmt.setLong(i++, _parser.getSystemAmount(requestedQuantity));
			pstmt.setLong(i++, _parser.getSystemAmount(requestedQuantity));
			Log.debug(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			ReturnCachedResult = new CachedRowSetImpl();
			ReturnCachedResult.populate(QueryResult);
		} catch (Exception e) {
			Log.info("Exception while populating Query Result to ReturnCachedResult: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.debug(ReturnCachedResult);
		Log.debug("Exited " + methodname + "()");
		return ReturnCachedResult;
	}

	public HashMap<String, String> getOptChannelTransferRule(String loginORmsisdn) {
		final String methodname = "getOptChannelTransferRule";
		Log.info("Entered " + methodname + "(" + loginORmsisdn + ")");
		HashMap<String, String> transferRules = new HashMap<String, String>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select ctr.foc_allowed from users u left outer join chnl_transfer_rules ctr ");
		sqlSelectBuff
				.append(" on (u.category_code = ctr.to_category and ctr.from_category = 'OPT') where u.msisdn = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, loginORmsisdn);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				transferRules.put("foc_allowed", QueryResult.getString("foc_allowed"));
			}
		} catch (Exception e) {
			Log.info("Error while fetching balances");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Transfer Rules(" + Arrays.asList(transferRules) + ")");
		Log.info("Exited " + methodname + "()");
		return transferRules;
	}

	public String getGeoCode(String geography) {
		final String methodname = "getGeoCode";
		Log.info("Entered " + methodname + "(" + geography + ")");
		String geocode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("");
		sqlSelectBuff.append("Select GRPH_DOMAIN_CODE from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_NAME= ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, geography);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			geocode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status (" + geocode + ")");
		Log.info("Exited " + methodname + "()");
		return geocode;
	}

	/**
	 * Query for fetching Schedule Status for Batch
	 *
	 * @author yash.gupta Scope: C2SBulkTransferRevamp.java
	 **/

	public String fetchScheduleStatus(String batchID) {
		final String methodname = "fetchScheduleStatus";
		Log.info("Entered " + methodname + "(" + batchID + ")");
		String scheduleStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select status from scheduled_batch_detail where batch_id = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, batchID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			scheduleStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching batch_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status(" + scheduleStatus + ")");
		Log.info("Exited " + methodname + "()");
		return scheduleStatus;
	}

	/**
	 * Query for fetching Sold Status
	 *
	 * @author yash.gupta Scope: DVDRechargeRevamp.java
	 **/

	public String fetchSoldStatus(String transactionID) {
		final String methodName = "fetchSoldStatus";
		Log.info("Entered " + methodName + "()");
		String soldStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SOLD_STATUS FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE C2S_TRANSACTION_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			soldStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching SOLD_STATUS: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Sold Status (" + soldStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return soldStatus;
	}

	/**
	 * Query for fetching DVD Sold Status for Bulk
	 *
	 * @author yash.gupta Scope: DVDBulkRechargeRevamp.java
	 **/

	public String fetchBulkSoldStatus(String batchID) {
		final String methodname = "fetchBulkSoldStatus";
		Log.info("Entered " + methodname + "(" + batchID + ")");
		String soldStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select sold_status from voms_vouchers where sale_batch_no = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, batchID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			soldStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching sale_batch_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Status(" + soldStatus + ")");
		Log.info("Exited " + methodname + "()");
		return soldStatus;
	}

	/**
	 * Query for fetching LookupName from Code
	 *
	 * @author yash.gupta Scope: DVDRechargeRevamp.java
	 **/

	public String getLookUpNameByCode(String LookUpCode) {
		final String methodname = "getLookUpCode";
		Log.info("Entered " + methodname + "(" + LookUpCode + ")");
		String LookUpName = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select LOOKUP_NAME from LOOKUPS where LOOKUP_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, LookUpCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = statement.executeQuery();
			QueryResult.next();
			LookUpName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Look Up Code: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: LOOKUP_NAME (" + LookUpName + ")");
		Log.info("Exited " + methodname + "()");
		return LookUpName;
	}

	/**
	 * Query for fetching ExpiryDate from Voucher Profile COde
	 *
	 * @author yash.gupta Scope: MVDRechargeRevamp.java
	 **/

	public String getVomsProductExpiry(String productName) {
		final String methodName = "getVomsProductExpiry";
		Log.info("Entered " + methodName + "()");
		String expiryDate = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EXPIRY_DATE from ");
		sqlSelectBuff.append("VOMS_PRODUCTS WHERE PRODUCT_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			expiryDate = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Product ID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Expiry Date (" + expiryDate + ")");
		Log.info("Exiting " + methodName + "()");
		return expiryDate;
	}

	/**
	 * Query for fetching Parent Geographical Domain from Voucher Profile COde
	 *
	 * @author yash.gupta Scope: O2CTransferRevampChannelAdmin.java
	 **/

	public String getParentGeoDomCode(String geoDomName) {
		final String methodName = "getParentGeoDomCode";
		Log.info("Entered " + methodName + "()");
		String GeoDomCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select PARENT_GRPH_DOMAIN_CODE from ");
		sqlSelectBuff.append("GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, geoDomName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			GeoDomCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Geographical Domain: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Geographical Domain Code (" + GeoDomCode + ")");
		Log.info("Exiting " + methodName + "()");
		return GeoDomCode;
	}

	/**
	 * Query for fetching maximum and minimum quantity of vouchers
	 *
	 * @author ashmeet.saggu Scope: MVDRechargeRevamp.java
	 **/

	public String getSystemPreferenceMAXValue(String Preference_Code) {
		final String methodName = "getSystemPreference";
		Log.info("Entered " + methodName + "(" + Preference_Code + ")");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select MAX_VALUE from system_preferences where PREFERENCE_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Preference_Code);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			prefVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULT_VALUE (" + prefVal + ")");
		Log.info("Exiting " + methodName + "()");
		return prefVal;
	}

	public String getSystemPreferenceMINValue(String Preference_Code) {
		final String methodName = "getSystemPreference";
		Log.info("Entered " + methodName + "(" + Preference_Code + ")");
		String prefVal = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select MIN_VALUE from system_preferences where PREFERENCE_CODE = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, Preference_Code);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			prefVal = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DEFAULT_VALUE (" + prefVal + ")");
		Log.info("Exiting " + methodName + "()");
		return prefVal;
	}

	public String getVoucherSegment(String productID) {
		final String methodName = "getVoucherSegment";
		Log.info("Entered " + methodName + "()");
		String serialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT VOUCHER_SEGMENT FROM ");
		sqlSelectBuff.append("VOMS_VOUCHERS WHERE PRODUCT_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, productID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			serialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching Segment: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VoucherSegment (" + serialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		return serialNumber;
	}

	public int getNetPayableAmt(String transferId) {
		int netPayableAmt = 0;
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();
			QueryResult = statement.executeQuery(
					"Select NET_PAYABLE_AMOUNT from CHANNEL_TRANSFERS where TRANSFER_ID='" + transferId + "'");
			QueryResult.next();
			netPayableAmt = QueryResult.getInt(1);
		} catch (Exception e) {
			Log.info("Error while fetching CommissionProfileName status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returns: NetPayableAmount (" + netPayableAmt + ")");
		return netPayableAmt;
	}

	/**
	 * @author ashmeet.saggu : MVDRechargeRevamp
	 */
	public boolean checkEnabledElectronicVoucherAvailable(String mrp) {
		final String methodName = "checkEnabledElectronicVoucherAvailable";
		Log.info("Entered " + methodName + "(" + mrp + ")");
		String EVDVoucherSerialNumber = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select serial_no from voms_vouchers where mrp = ? and status = 'EN' and ext_transaction_id is null");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, mrp);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			EVDVoucherSerialNumber = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: EVDVoucherSerialNumber (" + EVDVoucherSerialNumber + ")");
		Log.info("Exiting " + methodName + "()");
		if (EVDVoucherSerialNumber != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @author ashmeet.saggu : MVDRechargeRevamp
	 */
	public List<String> getMultipleEnabledVoucherSerialNumber(String MRP, int numberOfVoucher) {
		final String methodname = "getMultipleVoucherSerialNumber";
		Log.info("Entered :: " + methodname + "(" + MRP + ")");
		List<String> voucherSerialNumber = new ArrayList<>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		int countSerialNumber = 0;
		StringBuilder sqlSelectBuff = new StringBuilder(
				"select SERIAL_NO from voms_vouchers where MRP = ? and CURRENT_STATUS = ? ");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MRP);
			pstmt.setString(2, "EN");
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				voucherSerialNumber.add(QueryResult.getString(1).toString());
				if (countSerialNumber == numberOfVoucher) {
					break;
				}
				countSerialNumber += 1;
			}

		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returned Number of vouchers :" + countSerialNumber);
		return voucherSerialNumber;
	}

	/* Author Shubham Yadav */
	public List<String> getMultipleVoucherSerialNumber(String vouchCat, String Vouchstatus, String productId,
			int numberOfVoucher) {
		final String methodname = "getMultipleVoucherSerialNumber";
		Log.info("Entered :: " + methodname + " " + vouchCat + " " + Vouchstatus + " " + numberOfVoucher + " "
				+ productId);
		List<String> voucherSerialNumber = new ArrayList<>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		int countSerialNumber = 0;
		StringBuilder sqlSelectBuff = new StringBuilder(
				"select SERIAL_NO from voms_vouchers where VOUCHER_TYPE = ? and CURRENT_STATUS = ? and PRODUCT_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, vouchCat);
			pstmt.setString(2, Vouchstatus);
			pstmt.setString(3, productId);
			QueryResult = pstmt.executeQuery();
			while (QueryResult.next()) {
				voucherSerialNumber.add(QueryResult.getString(1).toString());
				if (countSerialNumber == numberOfVoucher) {
					break;
				}
				countSerialNumber += 1;
			}

		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returned Number of vouchers :" + countSerialNumber);
		return voucherSerialNumber;
	}

	/* @Author Shubham Yadav */
	public List<String> getBatchViewDetails(String batchId) {
		List<String> value = new ArrayList<String>();
		final String methodname = "gettingDetailsForViewBatchList";
		Log.info("Entered :: " + methodname + " " + batchId);
		Connection connection = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtuser = null;
		ResultSet QueryResult = null;
		int countSerialNumber = 0;
		StringBuilder sqlSelectBuff = new StringBuilder("select ? from voms_batches where batch_no = ?");
		StringBuilder sqlSelectBuffuser = new StringBuilder("select ? from voms_batches where user_id = ?");

		String sqlSelect = sqlSelectBuff.toString();
		String sqlSelectUser = sqlSelectBuffuser.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);

			// Get Batch Number
			pstmt.setString(1, "batch_no");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			value.add(QueryResult.getString(1).toString());

			// Get Batch Type
			pstmt.setString(1, "batch_type");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get Total Number Of Voucher
			pstmt.setString(1, "total_no_of_vouchers");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get From Serial Number
			pstmt.setString(1, "from_serial_no");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get To Serial Number
			pstmt.setString(1, "to_serial_no");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get Total Number Of Success
			pstmt.setString(1, "total_no_of_success");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get Total Number Of Failure
			pstmt.setString(1, "total_no_of_failure");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get Created Date
			pstmt.setString(1, "created_date");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			pstmt.setString(1, "user_id");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			String userid = QueryResult.toString();

			// Get User Name
			pstmtuser = connection.prepareStatement(sqlSelectUser);
			pstmtuser.setString(1, "user_name");
			pstmtuser.setString(2, userid);
			QueryResult = pstmtuser.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

			// Get MSISDN
			pstmtuser.setString(1, "msisdn");
			pstmtuser.setString(2, userid);
			QueryResult = pstmtuser.executeQuery();
			value.add(QueryResult.getString(1).toString());

			// Get Message for batch
			pstmt.setString(1, "message");
			pstmt.setString(2, batchId);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value.add(QueryResult.getString(1).toString());

		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returned Batch Details");
		return value;
	}

	/* @Author Shubham Yadav */
	public String getPreferenceModificationStatus(String fieldName) {
		String value = null;
		final String methodname = "getPreferencesDetails";
		Log.info("Entered :: " + methodname + " " + fieldName);
		String result = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet QueryResult = null;
		PreparedStatement pstmt = null;
		StringBuilder sqlSelectBuff = new StringBuilder(
				"select ? from system_preferences where name = '" + fieldName + "'");

		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();
			pstmt = connection.prepareStatement(sqlSelect);
			// Get Batch Number

			pstmt.setString(1, "modified_allowed");
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			result = QueryResult.getString(1);
			Log.info("Result : " + result);
			value = result;

		} catch (Exception e) {
			Log.info("Exception while fetching System Preference: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Query Repository Returned Batch Details");
		return value;
	}

	public String getVoucherName(String typeCode) {

		final String methodName = "getVoucherName";
		Log.info("Entered " + methodName + "()");
		String voucherName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT name FROM VOMS_TYPES");
		sqlSelectBuff.append("where voucher_type = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, typeCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			voucherName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching LoginID: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Voucher Name (" + voucherName + ")");
		Log.info("Exiting " + methodName + "()");
		return voucherName;

	}

//	method to fetch currency codes for Multi Currency tests

	public Object[][] fetchCurrencyCodes() {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		Object[][] currencyCodes = null;
		final StringBuilder strBuffCurrencyCodes = new StringBuilder();
		strBuffCurrencyCodes.append("select * from lookups where lookup_type='CURRLST';");

		String sqlSelect = strBuffCurrencyCodes.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			QueryResult = pstmt.executeQuery();

			int rowCount = 0;
			QueryResult.last();
			rowCount = QueryResult.getRow();
			QueryResult.beforeFirst();

			currencyCodes = new Object[rowCount][1];
			int counter = 0;
			while (QueryResult.next()) {

				currencyCodes[counter][0] = QueryResult.getString("lookup_code");
				counter++;
			}

		} catch (Exception e) {
			Log.info("Error while fetching Currency Codes");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}

		return currencyCodes;
	}

	public String fetchTransferStatusFOC(String transactionID) {
		final String methodname = "fetchTransferStatus";
		Log.info("Entered " + methodname + "(" + transactionID + ")");
		String transferStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("Select STATUS from CHANNEL_TRANSFERS where TRANSFER_ID = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, transactionID);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transferStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching transfer_id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: TransferStatus(" + transferStatus + ")");
		Log.info("Exited " + methodname + "()");
		return transferStatus;
	}

	public String getParentGeographicDomainCode(String geographicalDomainName) {
		final String methodname = "getParentGeographicDomainCode";
		Log.info("Entered " + methodname + "(" + geographicalDomainName + ")");
		String parentGeographicalDomainCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select parent_grph_domain_code from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_NAME = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, geographicalDomainName);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			parentGeographicalDomainCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching parent_grph_domain_code");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Parent Geographic domain code (" + parentGeographicalDomainCode + ")");
		Log.info("Exited " + methodname + "()");
		return parentGeographicalDomainCode;
	}

	public String getGeographicDomainName(String geographicalDomainCode) {
		final String methodname = "getGeographicDomainName";
		Log.info("Entered " + methodname + "(" + geographicalDomainCode + ")");
		String geographicalDomainName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"Select grph_domain_name from GEOGRAPHICAL_DOMAINS where GRPH_DOMAIN_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, geographicalDomainCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			geographicalDomainName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching grph_domain_name");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: geographicalDomainName (" + geographicalDomainName + ")");
		Log.info("Exited " + methodname + "()");
		return geographicalDomainName;
	}

	/**
	 * Query for fetching the latest Report Task ID of downloaded report.
	 *
	 * @author gourish.mahale Scope: DownloadOfflineReport.java
	 **/

	public String getOfflineDownloadedReportTaskID() {
		final String methodname = "getOfflineDownloadedReportTaskID";
		Log.info("Entered " + methodname);
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String reportTaskId = null;

		StringBuilder sqlSelect = new StringBuilder("SELECT * FROM ");
		sqlSelect.append("OFFLINE_REPORT_PROCESS WHERE STATUS = 'DOWNLOADED' ");
		sqlSelect.append("ORDER BY CREATED_ON DESC FETCH NEXT 1 ROWS ONLY");
		String query = sqlSelect.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(query);
			Log.info(methodname + "() :: select query: " + query);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			reportTaskId = QueryResult.getString("REPORT_PROCESS_ID");
		} catch (Exception e) {
			Log.info("Error while fetching Report Task Id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Report Task ID(" + reportTaskId + ")");
		Log.info("Exited " + methodname + "()");
		return reportTaskId;
	}

	/**
	 * Query for fetching the latest Report Task ID of downloaded report.
	 *
	 * @author gourish.mahale Scope: O2cAcknowledgePdfDownload.java
	 **/
	@Override
	public String getChannelTransfersTxnId(String senderCategoryCode, String msisdn) {
		final String methodname = "getChannelTransfersTxnId";
		Log.info("Entered " + methodname);
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String transactionId = null;

		StringBuilder sqlSelect = new StringBuilder("SELECT * FROM ");
		sqlSelect.append("CHANNEL_TRANSFERS WHERE SENDER_CATEGORY_CODE = '" + senderCategoryCode
				+ "' AND STATUS = 'CLOSE' AND TO_MSISDN = '" + msisdn + "' ");
		sqlSelect.append("ORDER BY CREATED_ON DESC FETCH NEXT 1 ROWS ONLY");
		String query = sqlSelect.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(query);
			Log.info(methodname + "() :: select query: " + query);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			transactionId = QueryResult.getString("TRANSFER_ID");
		} catch (Exception e) {
			Log.info("Error while fetching Transaction Id");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returned: Report Task ID(" + transactionId + ")");
		Log.info("Exited " + methodname + "()");
		return transactionId;
	}

	/**
	 * Unique Loan Profile name
	 * 
	 * @author yash.gupta
	 * @param LoanName
	 * @return
	 */

	public String checkForUniqueLoanProfileName(String LoanName) {
		final String methodName = "checkForUniqueLoanProfileName";
		Log.info("Entered " + methodName + "(" + LoanName + ")");
		String LOANNAMEStatus = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select case when exists ");
		sqlSelectBuff.append("(select 1 from LOAN_PROFILES where PROFILE_NAME = ?) ");
		sqlSelectBuff.append("then 'Y' else 'N' end as rec_exists from dual");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, LoanName);
			Log.info(methodName + "() :: select query: " + sqlSelect);

			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			LOANNAMEStatus = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Loan Profile Name status: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: CPName Status (" + LOANNAMEStatus + ")");
		Log.info("Exiting " + methodName + "()");
		return LOANNAMEStatus;
	}

	/**
	 * @author: yash.gupta
	 * @param: userID
	 * @return: loan_given
	 */
	public String getLoanGiven(String userID) {
		final String methodName = "getLoanGiven";
		Log.info("Entered " + methodName + "(" + userID + ")");
		String loan_given = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT loan_given FROM channel_user_loan_info ");
		sqlSelectBuff.append("WHERE user_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			loan_given = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Error while fetching Version: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Version (" + loan_given + ")");
		Log.info("Exiting " + methodName + "()");
		return loan_given;
	}

	public String getUserIDFromMSISDN(String MSISDN) {
		final String methodName = "getUserIDFromMSISDN";
		Log.info("Entered " + methodName + "(" + MSISDN + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select USER_ID from USERS where MSISDN = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, MSISDN);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			userID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: user_Name (" + userID + ")");
		Log.info("Exiting " + methodName + "()");
		return userID;
	}

	/**
	 * To get Value against Control Code from controlPreference
	 * 
	 * @param preferenceCode, Control_Code
	 * @return
	 */
	public String getValuefromControlCodeControlPreference(String preferenceCode, String ControlCode) {
		final String methodname = "getValuefromControlCodeControlPreference";
		Log.info("Entered " + methodname + "(" + preferenceCode + ", " + ControlCode + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select VALUE from CONTROL_PREFERENCES where PREFERENCE_CODE = ? ");
		sqlSelectBuff.append("AND CONTROL_CODE = ? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, preferenceCode);
			pstmt.setString(2, ControlCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			preferenceName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching NAME: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PREFERENCE_NAME (" + preferenceName + ")");
		Log.info("Exited " + methodname + "()");
		return preferenceName;
	}

	public List<String> getUserRoles(String userId, String catCode) {
		List<String> rolesCodes = new ArrayList<String>();

		final String methodName = "getUserRoles";
		Log.info("Entered " + methodName + "(" + userId + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT r.role_code FROM category_roles cr,roles r ,categories c,domains d,user_roles UR ");
		sqlSelectBuff.append("WHERE r.status != 'N' AND UR.user_id = ? ");
		sqlSelectBuff.append("AND C.category_code = ? AND r.view_roles = 'Y' ");
		sqlSelectBuff.append("AND UR.role_code=r.role_code AND cr.category_code=c.category_code ");
		sqlSelectBuff.append("AND c.domain_code=d.domain_code AND d.domain_type_code=r.domain_type ");
		sqlSelectBuff.append("AND r.group_role = 'N' AND cr.role_code = r.role_code ORDER BY r.group_name,role_name");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userId);
			pstmt.setString(2, catCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				rolesCodes.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while role codes: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: ROLE CODES" + rolesCodes);
		Log.info("Exiting " + methodName + "()");
		return rolesCodes;
	}

	@Override
	public List<String> fetchUserServicesTypes(String userId) {
		List<String> servicesTypes = new ArrayList<String>();

		final String methodName = "fetchUserServicesTypes";
		Log.info("Entered " + methodName + "(" + userId + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT SERVICE_TYPE FROM USER_SERVICES WHERE USER_ID =  ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, userId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				servicesTypes.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching services types: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SERVICES TYPES" + servicesTypes);
		Log.info("Exiting " + methodName + "()");
		return servicesTypes;
	}

	@Override
	public String getFirstNameByLoginId(String loginId) {
		final String methodName = "getFirstNameByLoginId";
		Log.info("Entered " + methodName + "(" + loginId + ")");
		String firstName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select FIRSTNAME from users where LOGIN_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			firstName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching first name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: FIRSTNAME (" + firstName + ")");
		Log.info("Exiting " + methodName + "()");
		return firstName;
	}

	@Override
	public String getShortNameByLoginId(String loginId) {
		final String methodName = "getShortNameByLoginId";
		Log.info("Entered " + methodName + "(" + loginId + ")");
		String shortName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select SHORT_NAME from users where LOGIN_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			shortName = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching short name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SHORT_NAME  (" + shortName + ")");
		Log.info("Exiting " + methodName + "()");
		return shortName;
	}

	@Override
	public String getEmailIdByLoginId(String loginId) {
		final String methodName = "getEmailIdByLoginId";
		Log.info("Entered " + methodName + "(" + loginId + ")");
		String emailId = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select EMAIL from users where LOGIN_ID = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			emailId = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching short name: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SHORT_NAME  (" + emailId + ")");
		Log.info("Exiting " + methodName + "()");
		return emailId;
	}

// This query returns the list of login id's of those channel users who have active child users.
	@Override
	public List<String> getParentLoginIdsHavingActiveChildUsers() {
		List<String> parentUsers = new ArrayList<String>();

		final String methodName = "getParentLoginIdsHavingActiveChildUsers";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT LOGIN_ID FROM USERS WHERE USER_ID IN ");
		sqlSelectBuff.append("(SELECT DISTINCT OWNER_ID FROM USERS ");
		sqlSelectBuff.append("WHERE OWNER_ID IN (PARENT_ID ) ");
		sqlSelectBuff.append("AND CATEGORY_CODE IN ('SE','RET','AG') ");
		sqlSelectBuff.append("AND STATUS = 'Y' AND USER_ID NOT IN (LOGIN_ID ))");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				parentUsers.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching parent users: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: PARENT USERS" + parentUsers);
		Log.info("Exiting " + methodName + "()");
		return parentUsers;
	}

	@Override
	public List<String> getLoginIdsHavingPendingTransactions() {
		List<String> loginIds = new ArrayList<String>();

		final String methodName = "getLoginIdsHavingPendingTransactions";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT LOGIN_ID FROM USERS WHERE USER_ID IN ");
		sqlSelectBuff.append("(SELECT FROM_USER_ID FROM CHANNEL_TRANSFERS ct ");
		sqlSelectBuff.append("WHERE STATUS = 'NEW' AND SENDER_CATEGORY_CODE NOT IN ('OPT')) ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				loginIds.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching login ids: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LOGIN_IDS" + loginIds);
		Log.info("Exiting " + methodName + "()");
		return loginIds;
	}

	@Override
	public List<String> getLoginIdsHavingAssociatedRestrictedMsisdnList() {
		List<String> loginIds = new ArrayList<String>();

		final String methodName = "getLoginIdsHavingAssociatedRestrictedMsisdnList";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT DISTINCT CHANNEL_USER_ID FROM RESTRICTED_MSISDNS");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				loginIds.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching login ids: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LOGIN_IDS" + loginIds);
		Log.info("Exiting " + methodName + "()");
		return loginIds;
	}

	@Override
	public List<String> getLoginIdsHavingPendingFOCtransactions() {
		List<String> loginIds = new ArrayList<String>();

		final String methodName = "getLoginIdsHavingPendingFOCtransactions";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT LOGIN_ID FROM USERS WHERE USER_ID IN ");
		sqlSelectBuff.append("(SELECT TO_USER_ID FROM CHANNEL_TRANSFERS ct WHERE TO_USER_ID IN ");
		sqlSelectBuff.append(" (SELECT USER_ID FROM USERS u1 WHERE ");
		sqlSelectBuff.append("(SELECT COUNT(*) FROM USERS u WHERE u.PARENT_ID = u1.USER_ID) ");
		sqlSelectBuff.append("< 1 AND USER_TYPE ='CHANNEL') AND STATUS = 'NEW' AND TYPE = 'O2C') ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				loginIds.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching login ids: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: LOGIN_IDS" + loginIds);
		Log.info("Exiting " + methodName + "()");
		return loginIds;
	}

	@Override
	public List<String> getMsisdnHavingOnGoingBatchRechargeScheduled() {
		List<String> msisdnList = new ArrayList<String>();

		final String methodName = "getMsisdnHavingOnGoingBatchRechargeScheduled";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT DISTINCT MSISDN FROM SCHEDULED_BATCH_DETAIL WHERE STATUS = 'S' ");
		sqlSelectBuff.append(
				"AND TRANSFER_STATUS = '200' AND MSISDN IN (SELECT MSISDN FROM USERS WHERE USER_TYPE = 'CHANNEL')");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				msisdnList.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDN" + msisdnList);
		Log.info("Exiting " + methodName + "()");
		return msisdnList;
	}

	@Override
	public List<String> getMsisdnWithPendingBatchFOCApproval() {
		List<String> msisdnList = new ArrayList<String>();

		final String methodName = "getMsisdnWithPendingBatchFOCApproval";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT MSISDN FROM FOC_BATCH_ITEMS fbi WHERE STATUS = 'NEW' ");
		sqlSelectBuff.append("AND EXTERNAL_CODE NOT IN ('ss') AND MSISDN IN (SELECT MSISDN FROM USERS u1 WHERE ");
		sqlSelectBuff.append(
				"(SELECT COUNT(*) FROM USERS u WHERE u.PARENT_ID = u1.USER_ID) < 1 AND USER_TYPE ='CHANNEL') FETCH FIRST 10 ROWS ONLY");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				msisdnList.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: MSISDN" + msisdnList);
		Log.info("Exiting " + methodName + "()");
		return msisdnList;
	}

	@Override
	public String getUserInfo(String columnName, String categoryCode, String userType) {
		final String methodname = "getUserInfo";
		Log.info("Entered " + methodname);
		String value = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT " + columnName + " FROM USERS WHERE CATEGORY_CODE = ? ");
		sqlSelectBuff.append("AND USER_TYPE = ? AND STATUS = 'Y' AND " + columnName + " IS NOT NULL");
		String sqlSelect = sqlSelectBuff.toString();
		Log.info("SELECT " + columnName + " FROM USERS WHERE CATEGORY_CODE = '" + categoryCode + "' AND USER_TYPE = '"
				+ userType + "' AND STATUS = 'Y'");
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			// pstmt.setString(1, columnName);
			pstmt.setString(1, categoryCode);
			pstmt.setString(2, userType);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching " + columnName + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + value + ")");
		Log.info("Exited " + methodname + "()");
		return value;

	}

	@Override
	public void deleteChannelUser(String userId) {
		final String methodname = "deleteChannelUser";
		Log.info("Entered " + methodname);
		String value = null;
		Connection connection = null;
		Statement statement = null;

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			statement = connection.createStatement();
			statement.execute("DELETE FROM USER_GEOGRAPHIES  WHERE USER_ID = '" + userId + "'");
			statement.execute("DELETE FROM USER_ROLES WHERE USER_ID ='" + userId + "'");
			statement.execute("DELETE FROM CHANNEL_USERS WHERE USER_ID ='" + userId + "'");
			statement.execute("DELETE FROM USER_SERVICES WHERE USER_ID='" + userId + "'");
			statement.execute(" DELETE FROM USERS WHERE USER_ID ='" + userId + "'");
		} catch (Exception e) {
			Log.info("Exception while deleting user with user id " + userId + "");
			Log.writeStackTrace(e);
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Successfully deleted the user with USER ID : (" + value + ")");
		Log.info("Exited " + methodname + "()");

	}

	@Override
	public List<String> fetchParentServicesTypes(String parentUserId) {
		List<String> parentServices = new ArrayList<String>();

		final String methodName = "fetchParentServicesTypes";
		Log.info("Entered " + methodName + "(" + parentUserId + ")");
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT US.service_type FROM user_services US, service_type ST, users U, category_service_type CST ");
		sqlSelectBuff.append("WHERE US.user_id = ? AND US.service_type = ST.service_type ");
		sqlSelectBuff.append("AND CST.network_code = U.network_code AND U.user_id = US.user_id ");
		sqlSelectBuff.append("AND U.category_code = CST.category_code AND CST.service_type = US.service_type");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, parentUserId);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				parentServices.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching parent services: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: SERVICES TYPE" + parentServices);
		Log.info("Exiting " + methodName + "()");
		return parentServices;
	}

	@Override
	public List<String> getOperatorRoles(String categoryCode) {
		List<String> optRoles = new ArrayList<String>();

		final String methodName = "getOperatorRoles";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT r.role_code FROM category_roles cr,roles r ,categories c,domains d ");
		sqlSelectBuff.append("WHERE r.status != 'N' AND cr.category_code = ? ");
		sqlSelectBuff.append("AND cr.category_code=c.category_code AND c.domain_code=d.domain_code ");
		sqlSelectBuff.append("AND d.domain_type_code=r.domain_type AND r.group_role = 'N' ");
		sqlSelectBuff.append("AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, categoryCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				optRoles.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching operator roles: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: " + categoryCode + " Cateogry Roles" + optRoles);
		Log.info("Exiting " + methodName + "()");
		return optRoles;
	}

	@Override
	public List<String> fetchOperatorServices(String senderNtwCode, String receiverNtwCode) {
		List<String> optServices = new ArrayList<String>();

		final String methodName = "fetchOperatorServices";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT st.service_type,st.name FROM service_type st,network_services ns ");
		sqlSelectBuff.append(
				"WHERE (st.external_interface = 'Y' or st.external_interface = 'A') AND st.service_type = ns.service_type ");
		sqlSelectBuff
				.append("AND ns.sender_network = ? AND ns.receiver_network = ? AND st.status = 'Y' ORDER BY st.name");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, senderNtwCode);
			pstmt.setString(2, receiverNtwCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				optServices.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching operator services: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Operator Services: " + optServices);
		Log.info("Exiting " + methodName + "()");
		return optServices;
	}

	@Override
	public List<String> fetchSuperChannelAdminServices(String moduleCode, String senderNtwCode, String receiverNtwCode,
			String catCode) {
		List<String> optServices = new ArrayList<String>();

		final String methodName = "fetchSuperChannelAdminServices";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT distinct(st.service_type),st.name FROM service_type st,network_services ns, category_service_type cat_serv  ");
		sqlSelectBuff.append("WHERE st.module= ? AND st.service_type = ns.service_type AND ns.sender_network = ? ");
		sqlSelectBuff
				.append("AND ns.receiver_network = ? AND st.status = 'Y' AND cat_serv.service_type = st.service_type ");
		sqlSelectBuff.append("AND cat_serv.category_code = ? and cat_serv.network_code='NG' ORDER BY st.name");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, moduleCode);
			pstmt.setString(2, senderNtwCode);
			pstmt.setString(3, receiverNtwCode);
			pstmt.setString(4, catCode);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				optServices.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching operator services: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Operator Services: " + optServices);
		Log.info("Exiting " + methodName + "()");
		return optServices;
	}

	@Override
	public List<String> getGeographicalDomainCodeListBasedOnGeoType(String geoDomainType) {
		List<String> geoDomainCodeList = new ArrayList<String>();

		final String methodName = "getGeographicalDomainCodeListBasedOnGeoType(" + geoDomainType + ")";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS ");
		sqlSelectBuff.append("WHERE GRPH_DOMAIN_TYPE = ? AND STATUS = 'Y'");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, geoDomainType);
			Log.info(methodName + "() :: select query: " + "SELECT GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS "
					+ "WHERE GRPH_DOMAIN_TYPE = " + geoDomainType + " AND STATUS = 'Y'");
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				geoDomainCodeList.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching geographical domain code list: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: GEOGRAPHICAL DOMAIN CODE LIST" + geoDomainCodeList);
		Log.info("Exiting " + methodName + "()");
		return geoDomainCodeList;

	}

	@Override
	public List<String> getDomainCodes(String userType) {
		List<String> domainCodeList = new ArrayList<String>();

		final String methodName = "getDomainCodes(" + userType + ")";
		Log.info("Entered " + methodName);
		String preferenceName = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		String userID = null;

		StringBuilder sqlSelectBuff = new StringBuilder("SELECT D.domain_code FROM domains D,domain_types DT ");
		sqlSelectBuff.append("WHERE D.status <> 'N' AND D.domain_type_code =DT.domain_type_code ");
		sqlSelectBuff.append("AND DT.domain_type_code <> '" + userType + "' ORDER BY domain_name");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			// pstmt.setString(1, userType);
			Log.info(methodName + "() :: select query: " + "SELECT D.domain_code FROM domains D,domain_types DT "
					+ "WHERE D.status <> 'N' AND D.domain_type_code =DT.domain_type_code "
					+ "AND DT.domain_type_code <> '" + userType + "' ORDER BY domain_name");
			QueryResult = pstmt.executeQuery();

			while (QueryResult.next()) {
				domainCodeList.add(QueryResult.getString(1));
			}

		} catch (Exception e) {
			Log.info("Exception while fetching domain code list: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: DOMAIN CODE LIST" + domainCodeList);
		Log.info("Exiting " + methodName + "()");
		return domainCodeList;
	}

	@Override
	public String getColumnValueFromTable(String columnName, String tableName, String columnToRefer,
			String referrercolumnValue) {
		final String methodname = "getColumnValueFromTable";
		Log.info("Entered " + methodname);
		String value = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"SELECT " + columnName + " FROM " + tableName + " WHERE " + columnToRefer + " = ? ");
		String sqlSelect = sqlSelectBuff.toString();
		Log.info("SELECT " + columnName + " FROM " + tableName + " WHERE " + columnToRefer + " = ? ");
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
//			pstmt.setString(1, columnName);
//			pstmt.setString(2, tableName);
//			pstmt.setString(3, columnToRefer);
			pstmt.setString(1, referrercolumnValue);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			value = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching " + columnName + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + value + ")");
		Log.info("Exited " + methodname + "()");
		return value;

	}

	@Override
	public boolean insertDomain(String domainCode, String domainName) {

		final String methodname = "insertDomain";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		int QueryResult = -1;
		boolean insertSuccess = false;
		StringBuilder sqlSelectBuff = new StringBuilder();

		sqlSelectBuff.append("insert INTO DOMAINS  ");
		sqlSelectBuff.append(
				"(DOMAIN_CODE, DOMAIN_NAME, DOMAIN_TYPE_CODE, OWNER_CATEGORY, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NUM_OF_CATEGORIES, APPLICATION_CODE, CAT_PROFILE_ALLOWED)");
		sqlSelectBuff.append(" VALUES(?, ?, 'COMP_SHOP', '8788', 'Y', ?, 'SU0001', ?, 'SU0001', 10, '2', NULL) ");

		String sqlinsert = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlinsert);
			pstmt.setString(1, domainCode);
			pstmt.setString(2, domainName);
			pstmt.setTimestamp(3, BTSLDateUtil.getTimestampFromUtilDate(new java.util.Date()));
			pstmt.setTimestamp(4, BTSLDateUtil.getTimestampFromUtilDate(new java.util.Date()));
			Log.info(methodname + "() :: select query: " + sqlinsert);
			QueryResult = pstmt.executeUpdate();
			if (QueryResult >= 1) {
				insertSuccess = true;
			}

		} catch (Exception e) {
			Log.info("Exception while inserting  domain " + domainName + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + insertSuccess + ")");
		Log.info("Exited " + methodname + "()");
		return insertSuccess;

	}

	@Override
	public boolean insertCategory(String domainCode, String categoryCode, String categoryName) {
		final String methodname = "insertCategory";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		int QueryResult = -1;
		boolean insertSuccess = false;
		StringBuilder sqlSelectBuff = new StringBuilder();

		sqlSelectBuff.append("  INSERT INTO CATEGORIES ");
		sqlSelectBuff.append(
				" (CATEGORY_CODE, CATEGORY_NAME, DOMAIN_CODE, SEQUENCE_NO, GRPH_DOMAIN_TYPE, MULTIPLE_GRPH_DOMAINS, WEB_INTERFACE_ALLOWED, SMS_INTERFACE_ALLOWED, FIXED_ROLES, MULTIPLE_LOGIN_ALLOWED, VIEW_ON_NETWORK_BLOCK, MAX_LOGIN_COUNT, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, DISPLAY_ALLOWED, MODIFY_ALLOWED, MAX_TXN_MSISDN, UNCNTRL_TRANSFER_ALLOWED, SCHEDULED_TRANSFER_ALLOWED, RESTRICTED_MSISDNS, PARENT_CATEGORY_CODE, USER_ID_PREFIX, SERVICES_ALLOWED, PRODUCT_TYPES_ALLOWED, DOMAIN_ALLOWED, FIXED_DOMAINS, OUTLETS_ALLOWED, AGENT_ALLOWED, HIERARCHY_ALLOWED, CATEGORY_TYPE, TRANSFERTOLISTONLY, LOW_BAL_ALERT_ALLOW, CP2P_PAYEE_STATUS, CP2P_PAYER_STATUS, C2S_PAYEE_STATUS, CP2P_WITHIN_LIST, CP2P_WITHIN_LIST_LEVEL, FIXED_PROFILE_ALLOWED, REG_CHARGES_APPLICABLE, AUTHENTICATION_TYPE) ");
		sqlSelectBuff.append(
				" VALUES(?, ?, ?, 1, 'SA', 'N', 'Y', 'Y', 'Y', 'Y', 'Y', 1, 'Y', ?, 'SU0001', ?, 'SU0001', 'Y', 'Y', NULL, 'Y', 'Y', 'Y', ?, 'MJ', 'Y', 'N', 'N', 'N', 'Y', 'Y', 'Y', 'CHUSR', 'Y', 'Y', 'N', 'N', 'Y', 'N', 'D', 'N', NULL, 'NA') ");

		String sqlinsert = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlinsert);
			pstmt.setString(1, categoryCode);
			pstmt.setString(2, categoryName);
			pstmt.setString(3, domainCode);
			pstmt.setTimestamp(4, BTSLDateUtil.getTimestampFromUtilDate(new java.util.Date()));
			pstmt.setTimestamp(5, BTSLDateUtil.getTimestampFromUtilDate(new java.util.Date()));
			pstmt.setString(6, categoryCode);
			Log.info(methodname + "() :: select query: " + sqlinsert);
			QueryResult = pstmt.executeUpdate();
			if (QueryResult >= 1) {
				insertSuccess = true;
			}

		} catch (Exception e) {
			Log.info("Exception while inserting  category " + categoryName + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + insertSuccess + ")");
		Log.info("Exited " + methodname + "()");
		return insertSuccess;
	}

	@Override
	public boolean deleteCategory(String categoryCode) {
		final String methodname = "deleteCategory";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		int QueryResult = -1;
		boolean deleteSuccess = false;
		StringBuilder sqlSelectBuff = new StringBuilder();

		sqlSelectBuff.append("  delete from  CATEGORIES where category_code =?");

		String sqldelete = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqldelete);
			pstmt.setString(1, categoryCode);
			Log.info(methodname + "() :: select query: " + sqldelete);
			QueryResult = pstmt.executeUpdate();
			if (QueryResult >= 1) {
				deleteSuccess = true;
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  category " + sqldelete + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + deleteSuccess + ")");
		Log.info("Exited " + methodname + "()");
		return deleteSuccess;
	}

	@Override
	public boolean deleteDomain(String domainCode) {
		final String methodname = "deleteDomain";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		int QueryResult = -1;
		boolean deleteSuccess = false;
		StringBuilder sqlSelectBuff = new StringBuilder();

		sqlSelectBuff.append("  delete from  domains where domain_code =?");

		String sqldelete = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqldelete);
			pstmt.setString(1, domainCode);
			Log.info(methodname + "() :: select query: " + sqldelete);
			QueryResult = pstmt.executeUpdate();
			if (QueryResult >= 1) {
				deleteSuccess = true;
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  domain " + sqldelete + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + deleteSuccess + ")");
		Log.info("Exited " + methodname + "()");
		return deleteSuccess;

	}

	public String getUserNetworkByLoginID(String loginID) {
		final String methodName = "getUserNetworkByLoginID";
		Log.info("Entered " + methodName + "(" + loginID + ")");
		String userNetworkCode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder("select network_Code from users where login_id = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, loginID);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			userNetworkCode = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching user id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: User networkCode (" + userNetworkCode + ")");
		Log.info("Exiting " + methodName + "()");
		return userNetworkCode;
	}

	@Override
	public String getTProfileIDbyProfileName(String profieName) {
		final String methodName = "getProfileIDbyProfileName";
		Log.info("Entered " + methodName + "(" + profieName + ")");
		String profileID = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"select profile_id from transfer_profile where profile_name = ?");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, profieName);
			Log.info(methodName + "() :: select query: " + sqlSelect);
			QueryResult = pstmt.executeQuery();
			QueryResult.next();
			profileID = QueryResult.getString(1);
		} catch (Exception e) {
			Log.info("Exception while fetching user id: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: Transfer profile ID  (" + profileID + ")");
		Log.info("Exiting " + methodName + "()");

		return profileID;
	}

	public CategoryVO loadCategoryGeographicalSequenceVO(Connection p_con, String p_domainCode) {
		final String methodName = "loadCategoryGeographicalSequenceVO";

		Log.info("Entered loadCategoryGeographicalSequenceVO");

		StringBuilder loggerValue = new StringBuilder();
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		CategoryVO categoryVO = null;
		final StringBuffer strBuff = new StringBuffer(
				"SELECT C.sequence_no CAT_SEQ,G.sequence_no GRPH_SEQ, D.num_of_categories, C.AGENT_ALLOWED agent_al,C.AUTHENTICATION_TYPE ");
		strBuff.append(
				"FROM categories C,geographical_domain_types G, domains D WHERE C.domain_code=? AND C.domain_code=D.domain_code AND ");
		strBuff.append("C.grph_domain_type=G.grph_domain_type AND C.status <> 'N' AND ");
		strBuff.append("C.sequence_no = (SELECT max(sequence_no) FROM categories WHERE ");
		strBuff.append("domain_code=C.domain_code AND status<>'N')");
		final String sqlSelect = strBuff.toString();
		Log.info("Query" + sqlSelect);

		try {
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			pstmtSelect.setString(1, p_domainCode);
			rs = pstmtSelect.executeQuery();

			if (rs.next()) {
				categoryVO = new CategoryVO();
				categoryVO.setAgentAllowed(rs.getString("agent_al")); // Added
				// by
				// deepika
				// aggarwal
				categoryVO.setCategorySequenceNumber(rs.getInt("CAT_SEQ"));
				categoryVO.setGeographicalDomainSeqNo(rs.getInt("GRPH_SEQ"));
				categoryVO.setNumberOfCategoryForDomain(rs.getInt("num_of_categories"));
				categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
			} else {
				categoryVO = new CategoryVO();
				categoryVO.setCategorySequenceNumber(0);
				categoryVO.setGeographicalDomainSeqNo(2);
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception : ");
			loggerValue.append(sqe.getMessage());
		} catch (Exception e) {

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}

			StringBuffer msg = new StringBuffer("");
			msg.append("Exiting: CategorySequenceNumber : " + categoryVO.getCategorySequenceNumber());
			msg.append(", GeographicalDomainSeqNo : " + categoryVO.getGeographicalDomainSeqNo());
			Log.info(msg.toString());
		}

		return categoryVO;
	}

	public UserVO loadValidUerIDs(String geographicalDomainCode, String networkCode, String categoryCode) {
		final String methodName = "loadValidUerIDs";
		Log.info("Entered loadValidUerIDs");
		StringBuilder loggerValue = new StringBuilder();
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		UserVO userVO = null;
		final StringBuffer strBuff = new StringBuffer(
				"SELECT U.user_id, U.owner_id, U.user_name, U.login_id  FROM users U, user_geographies UG, categories CAT, ");
		strBuff.append("  user_phones UP WHERE U.category_code = CAT.category_code ");
		strBuff.append(
				"                AND U.user_id=UG.user_id AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
		strBuff.append("                    geographical_domains GD1 WHERE status IN ('Y','S')  ");
		strBuff.append("                   CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append(
				"                START WITH grph_domain_code =? ) AND U.user_type= 'CHANNEL' AND u.status IN ('Y','S','SR') ");
		strBuff.append("                     AND U.network_code = ? AND U.category_code = ? ");
		strBuff.append("                AND U.user_id=UP.user_id AND UP.primary_number='Y' 	ORDER BY U.user_name ");

		final String sqlSelect = strBuff.toString();
		Log.info("Query" + sqlSelect);

		Connection p_con = null;
		try {
			p_con = DBConnectionPool.getInstance().getConnection();
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			pstmtSelect.setString(1, geographicalDomainCode);
			pstmtSelect.setString(2, networkCode);
			pstmtSelect.setString(3, categoryCode);
			rs = pstmtSelect.executeQuery();

			if (rs.next()) {
				userVO = new UserVO();
				userVO.setUserID(rs.getString("user_id"));
				userVO.setOwnerID(rs.getString("owner_id"));
				userVO.setUserName(rs.getString("user_name"));
				userVO.setLoginID(rs.getString("login_id"));

			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception : ");
			loggerValue.append(sqe.getMessage());
		} catch (Exception e) {

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}

		}

		return userVO;
	}

	public CardGroupVO getCardGroupSetVO(String networkCode, String moduleCode, String setType) {
		final String methodName = "loadValidUerIDs";
		Log.info("Entered loadValidUerIDs");
		StringBuilder loggerValue = new StringBuilder();
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		CardGroupVO cardGroupVO = null;
		final StringBuffer strBuff = new StringBuffer();
		strBuff.append("SELECT card_group_set_id,card_group_set_name,sub_service, service_type, set_type ");
		strBuff.append("FROM card_group_set ");
		strBuff.append("WHERE network_code =? AND module_code = ? AND status <> ? AND set_type = ? ");
		final String sqlSelect = strBuff.toString();
		Log.info("Query" + sqlSelect);
		Connection connection = null;

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmtSelect = connection.prepareStatement(sqlSelect);
			pstmtSelect.setString(1, networkCode);
			pstmtSelect.setString(2, moduleCode);
			pstmtSelect.setString(3, "N");
			pstmtSelect.setString(4, setType);
			rs = pstmtSelect.executeQuery();

			if (rs.next()) {
				cardGroupVO = new CardGroupVO();
				cardGroupVO.setCardgroupsetid(rs.getString("card_group_set_id"));
				cardGroupVO.setCardgroupsetname(rs.getString("card_group_set_name"));
				cardGroupVO.setSubservice("sub_service");
				cardGroupVO.setServicetype(rs.getString("service_type"));
				cardGroupVO.setSettype(rs.getString("set_type"));

			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception : ");
			loggerValue.append(sqe.getMessage());
		} catch (Exception e) {

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception ex) {
				Log.writeStackTrace(ex);
			}

		}

		return cardGroupVO;
	}

	public boolean deleteTransferRule(String networkCode, String sendSubcType, String receiverSubcType,
			String senderServiceClassID, String receiverServiceClassID, String subservice, String serviceType,
			String ruleLevel) {
		final String methodname = "deleteCategory";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		int QueryResult = -1;
		boolean deleteSuccess = false;
		StringBuilder sqlSelectBuff = new StringBuilder();

		sqlSelectBuff.append(
				" delete  FROM transfer_rules WHERE module='C2S' AND network_code=? AND sender_subscriber_type = ? ");
		sqlSelectBuff.append(" AND receiver_subscriber_type=?  AND sender_service_class_id=? ");
		sqlSelectBuff
				.append("  AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=? ");

		String sqldelete = sqlSelectBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqldelete);
			pstmt.setString(1, networkCode);
			pstmt.setString(2, sendSubcType);
			pstmt.setString(3, receiverSubcType);
			pstmt.setString(4, senderServiceClassID);
			pstmt.setString(5, receiverServiceClassID);
			pstmt.setString(6, subservice);
			pstmt.setString(7, serviceType);
			pstmt.setString(8, ruleLevel);
			Log.info(methodname + "() :: select query: " + sqldelete);
			QueryResult = pstmt.executeUpdate();
			if (QueryResult >= 1) {
				deleteSuccess = true;
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  category " + sqldelete + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + deleteSuccess + ")");
		Log.info("Exited " + methodname + "()");
		return deleteSuccess;
	}

	public String getServiceCardGroupid(String networkCode) {
		final String methodname = "deleteCategory";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String servicegroupID = null;
		StringBuilder selectQuery = new StringBuilder();

		selectQuery.append("SELECT group_id,group_name, group_code,status,");
		selectQuery.append("created_on,created_by,modified_on,modified_by,network_code");
		selectQuery.append(" FROM SERVICE_PROVIDER_GROUPS");
		selectQuery.append(" WHERE status <> 'N' AND network_code=? ");

		String sqlQry = selectQuery.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlQry);
			pstmt.setString(1, networkCode);

			Log.info(methodname + "() :: select query: " + sqlQry);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				servicegroupID = rs.getString("group_id");
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  category " + sqlQry + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + servicegroupID + ")");
		Log.info("Exited " + methodname + "()");
		return servicegroupID;
	}

	public String getServiceClassIDByInterface(String p_interfaceCategory) {
		final String methodname = "getServiceClassID";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String serviceClassID = null;
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append("SELECT service_class_code,service_class_name,interface_category,service_class_id ");
		selectQuery.append("FROM service_classes S,interfaces I,interface_types IT ");
		selectQuery.append("WHERE S.interface_id = I.interface_id AND ");
		selectQuery.append("I.interface_type_id = IT.interface_type_id AND interface_category IN(" + p_interfaceCategory
				+ ") AND S.status <> 'N'  ");
		selectQuery.append(" ORDER BY interface_category ");

		String sqlQry = selectQuery.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlQry);
			Log.info(methodname + "() :: select query: " + sqlQry);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				serviceClassID = rs.getString("service_class_id");
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  category " + sqlQry + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + serviceClassID + ")");
		Log.info("Exited " + methodname + "()");
		return serviceClassID;
	}

	public String getServiceType(String networkCode) {
		final String methodname = "getServiceType";
		Log.info("Entered " + methodname);

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String serviceType = null;

		final StringBuilder selectQueryBuff = new StringBuilder(
				"SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS ");
		selectQueryBuff.append(
				" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module='C2S' AND ST.external_interface='Y'   ORDER BY ST.name");

		String sqlQry = selectQueryBuff.toString();
		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlQry);
			pstmt.setString(1, networkCode);
			Log.info(methodname + "() :: select query: " + sqlQry);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				serviceType = rs.getString("service_type");
			}

		} catch (Exception e) {
			Log.info("Exception while deleting  category " + sqlQry + ": ");
			Log.writeStackTrace(e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: VALUE (" + serviceType + ")");
		Log.info("Exited " + methodname + "()");
		return serviceType;
	}

	@Override
	public boolean deleteServicePrdMappingData(String serviceType, String selectorCode) {
		final String methodname = "deletionfrombarredMSISDN";
		Log.info("Entered " + methodname + "(" + serviceType + "," + selectorCode + ")");
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet QueryResult = null;
		boolean deleteExecuted = false;

		StringBuilder sqlSelectBuff = new StringBuilder(
				"DELETE from selector_amount_mapping where service_type=? and selector_code=? ");
		String sqlSelect = sqlSelectBuff.toString();

		try {
			connection = DBConnectionPool.getInstance().getConnection();
			pstmt = connection.prepareStatement(sqlSelect);
			pstmt.setString(1, serviceType);
			pstmt.setString(2, selectorCode);
			Log.info(methodname + "() :: select query: " + sqlSelect);
			pstmt.executeUpdate();
			deleteExecuted = true;
		} catch (Exception e) {
			Log.info("Exception while deleting from barred MSISDN: ");
			Log.writeStackTrace(e);
		} finally {
			if (QueryResult != null)
				try {
					QueryResult.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Log.writeStackTrace(e);
				}
		}
		Log.info("Database Returns: deleteExecuted (" + deleteExecuted + ")");
		Log.info("Exited " + methodname + "()");
		return deleteExecuted;

	}

}
