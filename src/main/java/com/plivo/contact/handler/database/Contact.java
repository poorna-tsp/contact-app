package com.plivo.contact.handler.database;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.plivo.contact.model.Constants;

@DynamoDBTable(tableName = Constants.TABLE_CONTACT)
public class Contact {
    private String account ;
    private String email;
    private String name;
    private String phone;
    private String relation;

    @DynamoDBHashKey(attributeName = Constants.ATTRIB_ACCOUNT)
    public String getAccount() {
        return account;
    }

    @DynamoDBRangeKey(attributeName = Constants.ATTRIB_EMAIL)
    public String getEmail() {
        return email;
    }

    @DynamoDBIndexRangeKey(attributeName = Constants.ATTRIB_NAME, localSecondaryIndexName = Constants.INDEX_NAME)
    public String getName() {
        return name;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIB_PHONE)
    public String getPhone() {
        return phone;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIB_RELATION)
    public String getRelation() {
        return relation;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
