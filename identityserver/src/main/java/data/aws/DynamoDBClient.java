package data.aws;


import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.apache.log4j.Logger;

public class DynamoDBClient {
    private final  Logger logger = Logger.getLogger(DynamoDBClient.class);
    public static final String KEYSTROKE_TABLE_NAME = "keystroke_events";
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_SESSION_TABLE_NAME = "user_session";

    // Add your own credentials here or use environment variables
    final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder 
            .standard()
            .build();

    @Inject public void initClient() {
        setupTablesIfNotExisting();
    }

    public void setupTablesIfNotExisting() {
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(KEYSTROKE_TABLE_NAME)
                .withProvisionedThroughput(new ProvisionedThroughput(5l, 5l))
                .withKeySchema(
                        new KeySchemaElement("sessionId", KeyType.HASH),
                        new KeySchemaElement("eventTime", KeyType.RANGE)
                )
                .withAttributeDefinitions(
                        new AttributeDefinition("sessionId", ScalarAttributeType.S),
                        new AttributeDefinition("eventTime", ScalarAttributeType.S)
                );

        saveDdbOperation(ddb -> ddb.createTable(request));

        CreateTableRequest requestForUserTable = new CreateTableRequest()
                .withTableName(USER_TABLE_NAME)
                .withProvisionedThroughput(new ProvisionedThroughput(5l, 5l))
                .withKeySchema(
                        new KeySchemaElement("username", KeyType.HASH)
                )
                .withAttributeDefinitions(
                        new AttributeDefinition("username", ScalarAttributeType.S)
                );
        saveDdbOperation(ddb -> ddb.createTable(requestForUserTable));

        CreateTableRequest requestForUserSessionTable = new CreateTableRequest()
                .withTableName(USER_SESSION_TABLE_NAME)
                .withProvisionedThroughput(new ProvisionedThroughput(5l, 5l))
                .withKeySchema(
                        new KeySchemaElement("username", KeyType.HASH)
                )
                .withAttributeDefinitions(
                        new AttributeDefinition("username", ScalarAttributeType.S)
                );

        saveDdbOperation(ddb -> ddb.createTable(requestForUserSessionTable));
    }

    public void putItem(Map<String, AttributeValue> valueMap, String tableName) {
        saveDdbOperation(ddb -> ddb.putItem(tableName, valueMap));
    }

    public GetItemResult getItem(String key, String value, String tableName) {
        return saveDdbOperation(ddb -> ddb.getItem(
                new GetItemRequest()
                        .withKey(Collections.singletonMap(key, new AttributeValue(value)))
                        .withTableName(tableName)
        ));
    }

    private <T> T saveDdbOperation(Function<AmazonDynamoDB, T> function) {
        try {
            return function.apply(ddb);
        } catch (ResourceNotFoundException e) {
            logger.error(String.format("Error: The table \"%s\" can't be found.\n"));
        } catch (AmazonServiceException e) {
            logger.error(e.getMessage());
        } catch (AmazonClientException e) {
            // Most likely the table already exists
            logger.error(e.getMessage());
        }

        return null;
    }
}
