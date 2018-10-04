package data.service;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

import data.domain.SampleAggregate;

@Singleton
public class TemplateCacheService {
    public static Map<String, SampleAggregate> userTemplateCache = new HashMap<>();

    public void add(final String username, final SampleAggregate sampleAggregate) {
        userTemplateCache.put(username, sampleAggregate);
    }

    public SampleAggregate get(final String username) {
        return userTemplateCache.get(username);
    }
}
