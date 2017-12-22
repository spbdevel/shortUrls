package org.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class AccountResp implements Serializable {
    private Boolean success;
    private String password;
    private String description;
    private Boolean found;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }
}
