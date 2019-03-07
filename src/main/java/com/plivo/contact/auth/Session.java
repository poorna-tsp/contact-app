package com.plivo.contact.auth;

public class Session {
    private String account ;

    public Session(String account) {
        this.account = account ;
    }

    public String getAccount() {
        return this.account ;
    }
}
