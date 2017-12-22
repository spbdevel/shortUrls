package org.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UrlResponse {

    private String shortcode;
    private String description;
    private boolean badRequest;
    private boolean found;

    public String getShortcode() {
        return shortcode;
    }
    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getDescription() {
        return description;

    }
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public boolean isBadRequest() {
        return badRequest;
    }

    public void setBadRequest(boolean badRequest) {
        this.badRequest = badRequest;
    }

    @JsonIgnore
    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
