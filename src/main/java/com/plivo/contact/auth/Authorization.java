package com.plivo.contact.auth;

import com.plivo.contact.handler.database.DatabaseHandler;

public interface Authorization {
    public Session authenticate(DatabaseHandler dbHandler) ;
    public String getFailureReason() ;
}
