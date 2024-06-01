package restassuredapi.pojo.saveNetworkRespPojo;

import restassuredapi.pojo.BaseResponsePojo;

public class NetworkPrefixRespVO extends BaseResponsePojo {
	private String prepaidSeries;
	private String postpaidSeries;
	private String otherSeries;
	private String portSeries;
	
	public String getPrepaidSeries() {
		return prepaidSeries;
	}
	public void setPrepaidSeries(String prepaidSeries) {
		this.prepaidSeries = prepaidSeries;
	}
	public String getPostpaidSeries() {
		return postpaidSeries;
	}
	public void setPostpaidSeries(String postpaidSeries) {
		this.postpaidSeries = postpaidSeries;
	}
	public String getOtherSeries() {
		return otherSeries;
	}
	public void setOtherSeries(String otherSeries) {
		this.otherSeries = otherSeries;
	}
	public String getPortSeries() {
		return portSeries;
	}
	public void setPortSeries(String portSeries) {
		this.portSeries = portSeries;
	}

}