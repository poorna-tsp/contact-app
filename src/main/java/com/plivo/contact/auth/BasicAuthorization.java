package com.plivo.contact.auth;

import com.plivo.contact.handler.database.DatabaseHandler;
import com.plivo.contact.handler.database.Account;
import com.plivo.contact.util.StringUtil;

import java.util.Base64;

public class BasicAuthorization implements Authorization {

    private String message;
    private String user;
    private String pwd;

    public BasicAuthorization(String token) {
        message = null;
        String decodedToken = new String(Base64.getDecoder().decode(token));
        int index = decodedToken.indexOf(":");
        if (index == -1 || index == decodedToken.length()-1) {
            message = "Invalid token. token not formatted properly.";
        }

        user = decodedToken.substring(0, index);
        pwd = decodedToken.substring(index+1);

        if (StringUtil.isNullOrEmpty(user) || StringUtil.isNullOrEmpty(pwd)) {
            message = "Invalid token. Invalid user credentials";
        }
    }

    @Override public Session authenticate(DatabaseHandler dbHandler) {
        message = null;
        Account account = dbHandler.getAccount(user);
        if (account == null) {
            message = "Invalid user. Account does not exists.";
            return null;
        }

        if (!pwd.equals(account.getPassword())) {
            message = "Invalid password. Password does not match";
            return null;
        }

        return new Session(account.getUser());
    }

    @Override public String getFailureReason() {
        return message;
    }
}
