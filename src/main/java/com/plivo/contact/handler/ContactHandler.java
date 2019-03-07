package com.plivo.contact.handler;

import com.plivo.contact.handler.database.*;
import com.plivo.contact.model.ListResponse;
import com.plivo.contact.model.UserContact;
import com.plivo.contact.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactHandler {

    public static void createContact(DatabaseHandler databaseHandler, String account, UserContact userContact) throws IllegalArgumentException {
        if (StringUtil.isNullOrEmpty(userContact.getEmail())) {
            throw new IllegalArgumentException("Invalid email. Email can not be empty");
        }

        if (StringUtil.isNullOrEmpty(userContact.getName())) {
            throw new IllegalArgumentException("Invalid name. Name can not be empty");
        }

        Contact contact = new Contact();
        contact.setAccount(account);
        contact.setEmail(userContact.getEmail());
        contact.setName(userContact.getName());
        contact.setPhone(userContact.getPhone());
        contact.setRelation(userContact.getRelation());
        if (!databaseHandler.createContact(contact)) {
            throw new IllegalArgumentException("Invalid entry. Contact already exists with same email id");
        }
    }

    public static ListResponse getContacts(DatabaseHandler databaseHandler, String account, String email, String name, Integer pageSize,
                                           String pageLink) {
        if (pageSize == null) {
            pageSize = 10;
        }

        PageCondition pageCondition = new PageCondition(pageSize, pageLink);
        SearchCondition searchCondition = null;
        if (!StringUtil.isNullOrEmpty(email)) {
            searchCondition = new SearchCondition(Field.EMAIL, email, getRange(email));

        } else if (!StringUtil.isNullOrEmpty(name)) {
            searchCondition = new SearchCondition(Field.NAME, name, getRange(name));
        }

        Result result = databaseHandler.getContacts(account, pageCondition, searchCondition);
        ArrayList<UserContact> userList = new ArrayList<>();
        if (result == null || result.getContacts() == null) {
            return new ListResponse(userList);
        }

        List<Contact> contacts = result.getContacts();
        for (Contact contact : contacts) {
            UserContact userContact = new UserContact();
            userContact.setEmail(contact.getEmail());
            userContact.setName(contact.getName());
            userContact.setPhone(contact.getPhone());
            userContact.setRelation(contact.getRelation());
            userList.add(userContact);
        }
        ListResponse response = new ListResponse(userList);
        response.setPageLink(result.getPageLink());
        return response;
    }

    public static UserContact getContact(DatabaseHandler databaseHandler, String account, String email) {
        Contact contact = databaseHandler.getContact(account, email);
        if (contact == null) {
            return null;
        }

        UserContact userContact = new UserContact();
        userContact.setEmail(contact.getEmail());
        userContact.setName(contact.getName());
        userContact.setPhone(contact.getPhone());
        userContact.setRelation(contact.getRelation());
        return userContact;
    }

    public static void updateContact(DatabaseHandler databaseHandler, String account, String email, UserContact contact, boolean isPartial)
            throws IllegalArgumentException {

        Contact updatedContact;
        if (isPartial) {
            updatedContact = databaseHandler.getContact(account, email);
            if (updatedContact == null) {
                throw new IllegalArgumentException("Invalid email. Contact does not exists");
            }
            if (contact.getName() != null) {
                updatedContact.setName(contact.getName());
            }
            if (contact.getPhone() != null) {
                updatedContact.setPhone(contact.getPhone());
            }
            if (contact.getRelation() != null) {
                updatedContact.setRelation(contact.getRelation());
            }
        } else {
            updatedContact = new Contact();
            updatedContact.setAccount(account);
            updatedContact.setEmail(email);
            updatedContact.setName(contact.getName());
            updatedContact.setPhone(contact.getPhone());
            updatedContact.setRelation(contact.getRelation());
        }
        if (!databaseHandler.updateContact(account, updatedContact)) {
            throw new IllegalArgumentException("Invalid email. Contact does not exists");
        }
    }

    public static void deleteContact(DatabaseHandler databaseHandler, String account, String email) {
        databaseHandler.deleteContact(account, email);
    }

    private static String getRange(String data) {
        return data.substring(0, data.length() - 1) + (char) (data.charAt(data.length() - 1) + 1);
    }
}
