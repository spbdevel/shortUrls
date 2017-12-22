package org.app.dto;

public class UrlReq {

    private String url;
    private Integer redirectType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Integer redirectType) {
        this.redirectType = redirectType;
    }

    @Override
    public String toString() {
        return "UrlReq{" +
                "url='" + url + '\'' +
                ", redirectType='" + redirectType + '\'' +
                '}';
    }
}
