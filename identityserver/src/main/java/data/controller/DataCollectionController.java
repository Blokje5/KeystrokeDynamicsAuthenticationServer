package data.controller;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import data.aws.DynamoDBClient;
import com.google.inject.Inject;
import data.domain.KeystrokeEvent;
import data.domain.SessionEvent;
import spark.Route;
import spark.utils.StringUtils;

public class DataCollectionController {
    private DynamoDBClient dynamoDBClient;

    @Inject public DataCollectionController(DynamoDBClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    public Route dataCollectionRoute = (request, response) -> {
        KeystrokeEvent keystrokeEvent = new Gson().fromJson(request.body(), KeystrokeEvent.class);
        if (keystrokeEvent != null) {
            dynamoDBClient.putItem(keystrokeEventToMap(keystrokeEvent), DynamoDBClient.KEYSTROKE_TABLE_NAME);
        }

        return "{ \"message\": \"OK\" }";
    };

    public Route sessionCollectionRoute = (request, response) -> {
        final SessionEvent sessionEvent = new Gson().fromJson(request.body(), SessionEvent.class);
        if (sessionEvent != null) {
            dynamoDBClient.putItem(sessionEventToMap(sessionEvent), DynamoDBClient.USER_SESSION_TABLE_NAME);
        }

        return  "{ \"message\": \"OK\" }";
    };

    private Map<String, AttributeValue> keystrokeEventToMap(final KeystrokeEvent keystrokeEvent) {
        final HashMap<String, AttributeValue> map = new HashMap<>();

        map.put("sessionId", new AttributeValue(keystrokeEvent.getSessionId()));
        map.put("eventTime", new AttributeValue(keystrokeEvent.getEventTime()));
        map.put("eventType", new AttributeValue(keystrokeEvent.getEventType()));
        if (StringUtils.isNotEmpty(keystrokeEvent.getKeyName())) {
            map.put("keyName", new AttributeValue(keystrokeEvent.getKeyName()));
        }

        if (StringUtils.isNotEmpty(keystrokeEvent.getInputType())) {
            map.put("inputType", new AttributeValue(keystrokeEvent.getInputType()));
        }

        if (StringUtils.isNotEmpty(keystrokeEvent.getId())) {
            map.put("id", new AttributeValue(keystrokeEvent.getId()));
        }

        return map;
    }

    private Map<String, AttributeValue> sessionEventToMap(final SessionEvent sessionEvent) {
        final HashMap<String, AttributeValue> map = new HashMap<>();

        map.put("username", new AttributeValue(sessionEvent.getUsername()));
        map.put("session", new AttributeValue(sessionEvent.getSession()));

        return map;
    };
}
