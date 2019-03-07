package com.plivo.contact.handler.database;

import java.util.List;

public class Result {
    private List<Contact> contacts ;
    private String pageLink ;

    public Result(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }
}
