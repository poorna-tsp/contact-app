package com.plivo.contact.handler.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.plivo.contact.model.Constants;

@DynamoDBTable(tableName = Constants.TABLE_ACCOUNT)
public class Account {
    private String user ;
    private String password ;

    @DynamoDBHashKey(attributeName = Constants.ATTRIB_USER)
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIB_PWD)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
