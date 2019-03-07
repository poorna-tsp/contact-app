package com.plivo.contact.handler.database;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.plivo.contact.model.Constants;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    private AmazonDynamoDB client;

    public DatabaseHandler() {
        if ("true".equals(System.getenv("dev"))) {
            client = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
                    .build();
        } else {
            client =  AmazonDynamoDBClientBuilder.standard().build();
        }
    }

    public Account getAccount(String accountName) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper.load(Account.class, accountName);
    }

    public boolean createAccount(Account account) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Map expected = new HashMap();
        expected.put(Constants.ATTRIB_USER, new ExpectedAttributeValue(false));

        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expected);
        try {
            mapper.save(account, saveExpression);
        } catch (ConditionalCheckFailedException e) {
            return false;
        }

        return true;
    }

    public boolean createContact(Contact contact) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Map expected = new HashMap();
        expected.put(Constants.ATTRIB_EMAIL, new ExpectedAttributeValue(false));

        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expected);
        try {
            mapper.save(contact, saveExpression);
        } catch (ConditionalCheckFailedException e) {
            return false;
        }

        return true;
    }

    public Result getContacts(String account, PageCondition pageCondition, SearchCondition searchCondition) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        DynamoDBQueryExpression<Contact> queryExpression =
                new DynamoDBQueryExpression<Contact>();

        HashMap<String, AttributeValue> value = new HashMap();
        value.put(":v1", new AttributeValue().withS(account));

        HashMap<String, String> name = new HashMap();
        name.put("#n1", Constants.ATTRIB_ACCOUNT);

        if (searchCondition == null) {
            queryExpression.withKeyConditionExpression("#n1 = :v1");
        } else {
            queryExpression.withKeyConditionExpression("#n1 = :v1 and #n2 between :v2 and :v3");
            value.put(":v2", new AttributeValue().withS(searchCondition.getFromValue()));
            value.put(":v3", new AttributeValue().withS(searchCondition.getToValue()));

            if (searchCondition.getField() == Field.EMAIL) {
                name.put("#n2", Constants.ATTRIB_EMAIL);

            } else {
                queryExpression.withIndexName(Constants.INDEX_NAME);
                name.put("#n2", Constants.ATTRIB_NAME);
            }
        }

        queryExpression.withExpressionAttributeValues(value);
        queryExpression.withExpressionAttributeNames(name);

        if (pageCondition != null) {
            queryExpression.withLimit(pageCondition.getSize()).withExclusiveStartKey(getExlusiveStartKey(pageCondition.getFrom()));
        }

        QueryResultPage<Contact> queryResult = mapper.queryPage(Contact.class, queryExpression);
        Result result = new Result(queryResult.getResults());
        result.setPageLink(getPageLink(queryResult.getLastEvaluatedKey()));
        return result;
    }

    public Contact getContact(String account, String email) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper.load(Contact.class, account, email);
    }

    public boolean updateContact(String account, Contact contact) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        Map expected = new HashMap();
        expected.put(Constants.ATTRIB_EMAIL, new ExpectedAttributeValue(true).withValue(new AttributeValue().withS(contact.getEmail())));

        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expected);
        try {
            mapper.save(contact, saveExpression);
        } catch (ConditionalCheckFailedException e) {
            return false;
        }
        return true;
    }

    public void deleteContact(String account, String email) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        Contact contact = new Contact();
        contact.setAccount(account);
        contact.setEmail(email);
        mapper.delete(contact);
    }

    private String getPageLink(Map<String, AttributeValue> map) {
        String value = null;
        if (map != null) {
            StringBuffer buf = new StringBuffer();
            for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                buf.append(entry.getKey()).append("$").append(entry.getValue().getS()).append("#");
            }
            value = Base64.getEncoder().encodeToString(buf.toString().getBytes());
        }

        return value;
    }

    private Map<String, AttributeValue> getExlusiveStartKey(String pageLink) {
        if (pageLink == null) {
            return null;
        }

        Map<String, AttributeValue> returnMap = new HashMap<String, AttributeValue>();
        String data = new String(Base64.getDecoder().decode(pageLink));
        String[] list = data.split("#");
        for (String row : list) {
            String[] keyValue = row.split("\\$", 2);
            if (keyValue.length == 2) {
                returnMap.put(keyValue[0], new AttributeValue().withS(keyValue[1]));
            }
        }

        return returnMap;
    }
}

