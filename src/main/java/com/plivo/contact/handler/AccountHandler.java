package com.plivo.contact.handler;

import com.plivo.contact.handler.database.Account;
import com.plivo.contact.handler.database.DatabaseHandler;
import com.plivo.contact.model.UserAccount;
import com.plivo.contact.util.StringUtil;

public class AccountHandler {
    public static void createAccount(DatabaseHandler databaseHandler, UserAccount userAccount) throws IllegalArgumentException {
        if (StringUtil.isNullOrEmpty(userAccount.getUser())) {
            throw new IllegalArgumentException("Invalid user. User can not be empty");
        }

        if (StringUtil.isNullOrEmpty(userAccount.getPassword())) {
            throw new IllegalArgumentException("Invalid password. Password can not be empty");
        }

        Account account = new Account() ;
        account.setUser(userAccount.getUser());
        account.setPassword(userAccount.getPassword());
        if (!databaseHandler.createAccount(account)) {
            throw new IllegalArgumentException("Invalid user. User account already exist");
        }
    }
}
