package org.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AccountReq implements Serializable {
    private String accId;

    public String getAccId() {
        return accId;
    }

    @JsonProperty("AccountId")
    public void setAccId(String accId) {
        this.accId = accId;
    }

}
