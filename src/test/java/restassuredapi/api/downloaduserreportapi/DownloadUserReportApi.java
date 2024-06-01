package restassuredapi.api.downloaduserreportapi;

import com.utils.Log;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sreversalrequestpojo.C2SReversalRequestPojo;
import restassuredapi.pojo.downloaduserlistrequestpojo.DownloadUserListRequestPojo;

import java.util.ArrayList;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class DownloadUserReportApi extends BaseAPI {

    String apiPath = "/v1/c2cFileServices/downloadUserReport";
    String contentType;
    String category;
    String domain;
    String networkCode;
    String geography;
    String status;
    String accessToken;
    EncoderConfig encoderconfig = new EncoderConfig();

    ArrayList<DownloadUserListRequestPojo> downloadUserListRequestPojo = new ArrayList<DownloadUserListRequestPojo>();
    
    public DownloadUserReportApi (String baseURI, String accessToken){
        super(baseURI);
        this.accessToken=accessToken;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void addBodyParam(ArrayList<DownloadUserListRequestPojo> downloadUserListRequestPojo) {
		this.downloadUserListRequestPojo = downloadUserListRequestPojo;
	}

	@Override
    protected void createRequest() {
        requestSpecBuilder.setBaseUri(baseURI);
        requestSpecBuilder.setBasePath(apiPath);
        requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
        requestSpecBuilder.setContentType(contentType);
        requestSpecBuilder.setBody(downloadUserListRequestPojo);
        requestSpecBuilder.addQueryParam("category", category);
        requestSpecBuilder.addQueryParam("domain", domain);
        requestSpecBuilder.addQueryParam("networkCode", networkCode);
        requestSpecBuilder.addQueryParam("geography", geography);
        requestSpecBuilder.addQueryParam("status", status);
        requestSpecification = requestSpecBuilder.build();
        HashMap<String, String> queryParams = new HashMap<String, String>();
        HashMap<String, String> pathParams = new HashMap<String, String>();
        queryParams.put("category", category);
        queryParams.put("domain", domain);	
        queryParams.put("networkCode", networkCode);
        queryParams.put("geography", geography);
        queryParams.put("status", status);
        logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
    }

    @Override
    protected void executeRequest() {
        apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
        String s = apiResponse.asString();
        System.out.println(s);
        Log.info(s);
    }

    @Override
    protected void validateResponse() {
        responseSpecBuilder.expectStatusCode(expectedStatusCode);
        responseSpecification = responseSpecBuilder.build();
        apiResponse.then().spec(responseSpecification);
    }
}