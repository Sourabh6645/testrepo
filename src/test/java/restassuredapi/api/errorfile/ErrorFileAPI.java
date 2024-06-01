package restassuredapi.api.errorfile;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.errorfilerequestpojo.ErrorFileRequestPojo;


public class ErrorFileAPI extends BaseAPI{
		
		String apiPath="v1/c2cFileServices/bulkerrorfile";
		String contentType;
		String accessToken;
		ErrorFileRequestPojo errorFileRequestPojo = new ErrorFileRequestPojo();
		
		EncoderConfig encoderconfig = new EncoderConfig();
		public ErrorFileAPI(String baseURI,String accessToken) {
			super(baseURI);
			this.accessToken=accessToken;
			
		}
		 public void setContentType(String contentType) {
		        this.contentType = contentType;
		    }
		 
		 public void addBodyParam(ErrorFileRequestPojo errorFileRequestPojo) {
				this.errorFileRequestPojo =errorFileRequestPojo;
				}
		
		protected void createRequest() {
			requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setBasePath(apiPath);
			requestSpecBuilder.setConfig(RestAssured.config()
	                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(contentType);
			requestSpecBuilder.setBody(errorFileRequestPojo);
			requestSpecification = requestSpecBuilder.build();
			HashMap<String, String> queryParams = new HashMap<String, String>();
			HashMap<String, String> pathParams = new HashMap<String, String>();
			logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		}

		@Override
		protected void executeRequest() {
			apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
			String s=apiResponse.asString();
			Log.info(s);
			System.out.println(s);
		}

		@Override
		protected void validateResponse() {
			responseSpecBuilder.expectStatusCode(expectedStatusCode);
			responseSpecification = responseSpecBuilder.build();
			
		}


	}


