package data;

import data.aws.DynamoDBClient;
import com.google.inject.AbstractModule;

public class DataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DynamoDBClient.class);
    }
}
