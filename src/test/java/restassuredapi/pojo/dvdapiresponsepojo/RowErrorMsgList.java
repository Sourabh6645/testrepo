package restassuredapi.pojo.dvdapiresponsepojo;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "masterErrorList", "rowErrorMsgList" })
public class RowErrorMsgList {

	private List<RowErrorMsgLists> rowErrorMsgLists;

	public List<RowErrorMsgLists> getRowErrorMsgLists() {
		return rowErrorMsgLists;
	}

	public void setRowErrorMsgLists(List<RowErrorMsgLists> rowErrorMsgLists) {
		this.rowErrorMsgLists = rowErrorMsgLists;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return (sb.append("rowErrorMsgLists = ").append(rowErrorMsgLists)).toString();
	}
}
