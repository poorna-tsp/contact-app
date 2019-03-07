package com.plivo.contact.model;

import java.util.List;

public class ListResponse {
    private List data;
    private String pageLink ;

    public ListResponse(List data) {
        this.data = data;
    }

    public List getData() {
        return data;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }
}
