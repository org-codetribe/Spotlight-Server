package com.yappyapps.spotlight.domain.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WowzaEventDeleted {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("request_id")
    @Expose
    private String requestId;
    @SerializedName("request_timestamp")
    @Expose
    private String requestTimestamp;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(String requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

}