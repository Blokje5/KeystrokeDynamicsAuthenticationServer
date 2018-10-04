package data.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import data.CalculateFeatureAlgorithms;
import data.domain.Sample;
import data.domain.SampleAggregate;
import data.domain.UserEnrollmentAggregate;
import data.service.TemplateCacheService;
import spark.Route;

public class UserTemplateController {

    private TemplateCacheService templateCacheService;

    @Inject public UserTemplateController(TemplateCacheService templateCacheService) {
        this.templateCacheService = templateCacheService;
    }

    public Route userTemplateRoute = (request, response) -> {
       final UserEnrollmentAggregate userEnrollmentAggregate = new Gson().fromJson(request.body(), UserEnrollmentAggregate.class);
       final List<Sample> sampleList = userEnrollmentAggregate.getSamples();
       final List<SampleAggregate> sampleAggregates = sampleList.stream()
               .map(Sample::getKeystrokeEvents)
               .map(CalculateFeatureAlgorithms::calculateFeaturesForSample)
               .collect(Collectors.toList());

       final SampleAggregate userTemplate = CalculateFeatureAlgorithms.calculateUserTemplate(sampleAggregates);

       templateCacheService.add(userEnrollmentAggregate.getUsername(), userTemplate);

       return new Gson().toJson(userTemplate);
    };
}
