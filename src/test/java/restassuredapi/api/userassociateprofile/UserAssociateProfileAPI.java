package restassuredapi.api.userassociateprofile;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userassociateprofileresponsepojo.UserAssociateProfileResponsePojo;

public class UserAssociateProfileAPI extends BaseAPI {

	String apiPath = "/v1/channelUsers/associateProfile";

	String contentType;
	String category;
	String geography;
	String accessToken;
	UserAssociateProfileResponsePojo userAssociateProfileResponsePojo = new UserAssociateProfileResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserAssociateProfileAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("category", category);
		requestSpecBuilder.addQueryParam("geography", geography);;
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("category", category);
		queryParams.put("geography", geography);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
