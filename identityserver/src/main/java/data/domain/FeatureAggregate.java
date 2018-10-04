package data.domain;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureAggregate {
    private List<Double> dwellTimes;
    private List<Double> flightTimes;
    private List<Double> keyReleaseTimes;
    private List<Double> keyPressTimes;
    private List<Double> diGraphTimes;
}
