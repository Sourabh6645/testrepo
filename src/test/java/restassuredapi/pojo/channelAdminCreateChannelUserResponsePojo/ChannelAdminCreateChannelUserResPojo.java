package restassuredapi.pojo.channelAdminCreateChannelUserResponsePojo;

public class ChannelAdminCreateChannelUserResPojo {

	/*
	 * public String signature;
	 * 
	 * public Response response;
	 * 
	 * public String getSignature() { return signature; } public void
	 * setSignature(String signature) { this.signature = signature; } public
	 * Response getResponse() { return response; } public void setResponse(Response
	 * response) { this.response = response; }
	 */
	
	public int status;
	public String messageCode;
    public String message;
    public Object errorMap;
    
    
    public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(Object errorMap) {
		this.errorMap = errorMap;
	}

}
