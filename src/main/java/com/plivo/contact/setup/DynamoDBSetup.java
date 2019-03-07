package com.plivo.contact.setup;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.plivo.contact.model.Constants;

public class DynamoDBSetup {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
            .build();

    public static void createAccountTable() {
        CreateTableRequest request = new CreateTableRequest().withTableName(Constants.TABLE_ACCOUNT)
                .withKeySchema(new KeySchemaElement(Constants.ATTRIB_USER, KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition(Constants.ATTRIB_USER, ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
        client.createTable(request);
    }

    public static void createContactTable() {
        CreateTableRequest request = new CreateTableRequest().withTableName(Constants.TABLE_CONTACT)
                .withKeySchema(new KeySchemaElement(Constants.ATTRIB_ACCOUNT, KeyType.HASH),
                        new KeySchemaElement(Constants.ATTRIB_EMAIL, KeyType.RANGE))
                .withAttributeDefinitions(new AttributeDefinition(Constants.ATTRIB_ACCOUNT, ScalarAttributeType.S),
                        new AttributeDefinition(Constants.ATTRIB_EMAIL, ScalarAttributeType.S),
                        new AttributeDefinition(Constants.ATTRIB_NAME, ScalarAttributeType.S))
                .withLocalSecondaryIndexes(new LocalSecondaryIndex().withIndexName(Constants.INDEX_NAME)
                        .withKeySchema(new KeySchemaElement(Constants.ATTRIB_ACCOUNT, KeyType.HASH),
                                new KeySchemaElement(Constants.ATTRIB_NAME, KeyType.RANGE))
                        .withProjection(new Projection().withProjectionType(ProjectionType.ALL)))
                .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
        client.createTable(request);
    }

    public static void deleteTable(String table) {
        client.deleteTable(table);
    }

    public static void main(String[] args) {
        deleteTable(Constants.TABLE_ACCOUNT);
        createAccountTable();
        deleteTable(Constants.TABLE_CONTACT);
        createContactTable();
    }
}
