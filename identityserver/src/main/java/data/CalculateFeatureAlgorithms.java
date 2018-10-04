package data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import data.domain.FeatureAggregate;
import data.domain.KeystrokeEvent;
import data.domain.SampleAggregate;

public class CalculateFeatureAlgorithms {

    public static boolean distanceMeasureWithinThreshold(final List<KeystrokeEvent> keystrokeEvents, final SampleAggregate userTemplate) {
        final FeatureAggregate featureAggregate = calculateFeatures(calculateOverKeystrokeArray(keystrokeEvents));

        final double dwellTimeDistance = calculateDistance(featureAggregate.getDwellTimes(), userTemplate.getDwellTimeMean(), userTemplate.getDwellTimeStd());
        final double flightTimeDistance = calculateDistance(featureAggregate.getFlightTimes(), userTemplate.getFlightTimeMean(), userTemplate.getFlightTimeStd());
        final double keyReleaseTimeDistance = calculateDistance(featureAggregate.getKeyReleaseTimes(), userTemplate.getKeyReleaseTimeMean(), userTemplate.getKeyReleaseTimeStd());
        final double keyPressTimeDistance = calculateDistance(featureAggregate.getKeyPressTimes(), userTemplate.getKeyPressTimeMean(), userTemplate.getKeyPressTimeStd());
        final double diGraphTimeDistance = calculateDistance(featureAggregate.getDiGraphTimes(), userTemplate.getDiGraphTimeMean(), userTemplate.getDiGraphTimeStd());

        return distanceWithinThreshold(dwellTimeDistance, userTemplate.getDwellTimeStd(), userTemplate.getDwellTimeMean()) &&
               distanceWithinThreshold(flightTimeDistance, userTemplate.getFlightTimeStd(), userTemplate.getFlightTimeMean()) &&
               distanceWithinThreshold(keyReleaseTimeDistance, userTemplate.getKeyReleaseTimeStd(), userTemplate.getKeyReleaseTimeMean()) &&
               distanceWithinThreshold(keyPressTimeDistance, userTemplate.getKeyPressTimeStd(), userTemplate.getKeyPressTimeMean()) &&
               distanceWithinThreshold(diGraphTimeDistance, userTemplate.getDiGraphTimeStd(), userTemplate.getDiGraphTimeMean());
    }

    private static double calculateDistance(final List<Double> measures, final double mean, final double std) {
        final List<Double> distances = measures.stream()
                .map(measure -> Math.pow(measure - mean, 2))
                .collect(Collectors.toList());

        final double sum = distances.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return Math.sqrt(sum / (measures.size() - 1));
    }

    private static boolean distanceWithinThreshold(double distance, double std, double mean) {
        final double threshold = 2*std;
        return distance < threshold;
    }

    public static SampleAggregate calculateUserTemplate(final List<SampleAggregate> sampleAggregates) {
        return sampleAggregates.stream().reduce(SampleAggregate::combine).get();
    }

    public static SampleAggregate calculateFeaturesForSample(final List<KeystrokeEvent> keystrokeEvents) {
        return calculateSampleAggregate(calculateFeatures(calculateOverKeystrokeArray(keystrokeEvents)));
    }

    private static List<Double[]> calculateOverKeystrokeArray(final List<KeystrokeEvent> keystrokeEvents) {
        // Map to store keyname -> time pairs
        final Map<String, Double> keystrokeDataMap = new HashMap<>();
        // Array of double pair to hold keydown, keyup time
        final List<Double[]> timePairs = new LinkedList<>();

        // for now make the assumption that events always start with keydown
        keystrokeEvents.forEach(keystrokeEvent -> {
            final Double time = Double.parseDouble(keystrokeEvent.getEventTime());
            final String keyname = keystrokeEvent.getKeyName();

            if (!keystrokeDataMap.containsKey(keyname)) {
                // add to map if key hasn't been entered (assuming keydown always arrives first)
                keystrokeDataMap.put(keyname, time);
            } else {
                // retrieve previous value (keydown) and add to pairs list
                final Double previousTime = keystrokeDataMap.get(keyname);
                timePairs.add(new Double[]{previousTime, time});
                // remove previous entry
                keystrokeDataMap.remove(keyname);
            }
        });

        return timePairs;
    }

    private static FeatureAggregate calculateFeatures(final List<Double[]> timePairs) {
        final List<Double> dwellTimes = new LinkedList();
        final List<Double> flightTimes = new LinkedList();
        final List<Double> keyReleaseTimes = new LinkedList();
        final List<Double> keyPressTimes = new LinkedList();
        final List<Double> diGraphTimes = new LinkedList();

        for (int i = 0; i < timePairs.size(); i++) {
            final Double[] timePair = timePairs.get(i);
            // Keyup - keydown
            final double dwellTime = timePair[1] - timePair[0];
            dwellTimes.add(dwellTime);

            // need to make sure we don't go out of array bounds
            if ((i + 1) < timePairs.size()) {
                final Double[] nextPair = timePairs.get(i + 1);
                // Keydown - keyup of previous key
                final double flightTime = nextPair[0] - timePair[1];
                flightTimes.add(flightTime);
                // Keyup - keyup
                final double keyReleaseTime = nextPair[1] - timePair[1];
                keyReleaseTimes.add(keyReleaseTime);
                // Keydown - keydown
                final double keyPressTime = nextPair[0] - timePair[0];
                keyPressTimes.add(keyPressTime);
                // Keyup - keydown
                final double diGraph = nextPair[1] - timePair[0];
                diGraphTimes.add(diGraph);
            }
        }

        return new FeatureAggregate(dwellTimes, flightTimes, keyReleaseTimes, keyPressTimes, diGraphTimes);
    }

    private static SampleAggregate calculateSampleAggregate(final FeatureAggregate featureAggregate) {
        final SampleAggregate aggregate = new SampleAggregate();
        final double dwellTimeMean = calculateMeanOverList(featureAggregate.getDwellTimes());
        final double dwellTimeStd = calculateStandardDeviationOverList(featureAggregate.getDwellTimes(), dwellTimeMean);
        aggregate.setDwellTimeMean(dwellTimeMean);
        aggregate.setDwellTimeStd(dwellTimeStd);

        final double flightTimeMean = calculateMeanOverList(featureAggregate.getFlightTimes());
        final double flightTimeStd = calculateStandardDeviationOverList(featureAggregate.getFlightTimes(), flightTimeMean);
        aggregate.setFlightTimeMean(dwellTimeMean);
        aggregate.setFlightTimeStd(flightTimeStd);

        final double keyReleaseTimeMean = calculateMeanOverList(featureAggregate.getKeyReleaseTimes());
        final double keyReleaseTimeStd = calculateStandardDeviationOverList(featureAggregate.getKeyReleaseTimes(), keyReleaseTimeMean);
        aggregate.setKeyReleaseTimeMean(keyReleaseTimeMean);
        aggregate.setKeyReleaseTimeStd(keyReleaseTimeStd);

        final double keyPressTimeMean = calculateMeanOverList(featureAggregate.getKeyPressTimes());
        final double keyPressTimeStd = calculateStandardDeviationOverList(featureAggregate.getKeyPressTimes(), keyPressTimeMean);
        aggregate.setKeyPressTimeMean(keyPressTimeMean);
        aggregate.setKeyPressTimeStd(keyPressTimeStd);

        final double diGraphTimeMean = calculateMeanOverList(featureAggregate.getDiGraphTimes());
        final double diGraphTimeStd = calculateStandardDeviationOverList(featureAggregate.getDiGraphTimes(), diGraphTimeMean);
        aggregate.setDiGraphTimeMean(diGraphTimeMean);
        aggregate.setDiGraphTimeStd(diGraphTimeStd);

        return aggregate;
    }

    private static double calculateMeanOverList(final List<Double> doubles) {
        // Type erasure ...
        return doubles.stream().mapToDouble(Double::doubleValue).sum() / doubles.size();
    }

    private static double calculateStandardDeviationOverList(final List<Double> doubles, final double mean) {
        // Type erasure ...
        return Math.sqrt(doubles.stream().map(value -> Math.pow(value - mean, 2)).mapToDouble(Double::doubleValue).sum() / (doubles.size() - 1));
    }
}
