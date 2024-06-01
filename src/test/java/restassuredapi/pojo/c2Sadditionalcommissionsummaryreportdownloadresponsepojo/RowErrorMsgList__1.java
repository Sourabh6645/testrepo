
package restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "masterErrorList",
    "rowErrorMsgList",
    "rowName",
    "rowValue"
})
@Generated("jsonschema2pojo")
public class RowErrorMsgList__1 {

    @JsonProperty("masterErrorList")
    private List<MasterError__3> masterErrorList = null;
    @JsonProperty("rowErrorMsgList")
    private List<RowErrorMsg__1> rowErrorMsgList = null;
    @JsonProperty("rowName")
    private String rowName;
    @JsonProperty("rowValue")
    private String rowValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("masterErrorList")
    public List<MasterError__3> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterError__3> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgList")
    public List<RowErrorMsg__1> getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    @JsonProperty("rowErrorMsgList")
    public void setRowErrorMsgList(List<RowErrorMsg__1> rowErrorMsgList) {
        this.rowErrorMsgList = rowErrorMsgList;
    }

    @JsonProperty("rowName")
    public String getRowName() {
        return rowName;
    }

    @JsonProperty("rowName")
    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    @JsonProperty("rowValue")
    public String getRowValue() {
        return rowValue;
    }

    @JsonProperty("rowValue")
    public void setRowValue(String rowValue) {
        this.rowValue = rowValue;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
