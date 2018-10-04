package identity.registration.service;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import data.aws.DynamoDBClient;
import exceptions.AlreadyExistsException;
import identity.registration.domain.User;

public class UserService {

    private DynamoDBClient dynamoDBClient;

    @Inject public UserService(DynamoDBClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    public void add(User user) {
        if(user.getUsername() != null) {
            GetItemResult getItemResult = dynamoDBClient.getItem("username", user.getUsername(), DynamoDBClient.USER_TABLE_NAME);
            if (getItemResult.getItem() != null) {
                throw new AlreadyExistsException("User is already registered");
            }
        }
        final HashMap<String, AttributeValue> map = new HashMap<>();

        map.put("username", new AttributeValue(user.getUsername()));
        map.put("password", new AttributeValue(user.getPassword()));
        dynamoDBClient.putItem(map, DynamoDBClient.USER_TABLE_NAME);
    }

    public User findByUsername(String username) {
        GetItemResult getItemResult = dynamoDBClient.getItem("username", username, DynamoDBClient.USER_TABLE_NAME);
        if (getItemResult != null) {
            Map<String,AttributeValue> item = getItemResult.getItem();
            String password = item.get("password").getS();
            return new User(username, password);
        }
        return null;
    }
}
