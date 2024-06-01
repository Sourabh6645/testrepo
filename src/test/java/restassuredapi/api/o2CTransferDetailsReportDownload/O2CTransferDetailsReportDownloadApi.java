package restassuredapi.api.o2CTransferDetailsReportDownload;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2Ctransferdetailsreportdownloadrequestpojo.O2CTransferDetailsReportDownloadRequestPojo;

public class O2CTransferDetailsReportDownloadApi extends BaseAPI {

	String apiPath = "/v1/pretupsUIReports/downloadO2ctransferdetailsRpt";
	String contentType;
	O2CTransferDetailsReportDownloadRequestPojo o2CTransferDetailsReportDownloadRequestPojo  = new O2CTransferDetailsReportDownloadRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2CTransferDetailsReportDownloadApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(O2CTransferDetailsReportDownloadRequestPojo o2CTransferDetailsReportDownloadRequestPojo) {
		this.o2CTransferDetailsReportDownloadRequestPojo = o2CTransferDetailsReportDownloadRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2CTransferDetailsReportDownloadRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}

}