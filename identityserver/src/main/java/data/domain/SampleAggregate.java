package data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleAggregate {
    private double dwellTimeMean;
    private double dwellTimeStd;

    private double flightTimeMean;
    private double flightTimeStd;

    private double keyReleaseTimeMean;
    private double keyReleaseTimeStd;

    private double keyPressTimeMean;
    private double keyPressTimeStd;

    private double diGraphTimeMean;
    private double diGraphTimeStd;

    public SampleAggregate combine(final SampleAggregate other) {
        final double newDwellTimeMean = this.dwellTimeMean + other.dwellTimeMean / 2;
        final double newDwellTimeStd = this.dwellTimeStd + other.dwellTimeStd / 2;

        final double newFlightTimeMean = this.flightTimeMean + other.flightTimeMean / 2;
        final double newFlightTimeStd = this.flightTimeStd + other.flightTimeStd / 2;

        final double newKeyReleaseTimeMean = this.keyReleaseTimeMean + other.keyReleaseTimeMean / 2;
        final double newKeyReleaseTimeStd = this.keyReleaseTimeStd + other.keyReleaseTimeStd / 2;

        final double newKeyPressTimeMean = this.keyPressTimeMean + other.keyPressTimeMean / 2;
        final double newKeyPressTimeStd = this.keyPressTimeStd + other.keyPressTimeStd / 2;

        final double newDiGraphTimeMean = this.diGraphTimeMean + other.diGraphTimeMean / 2;
        final double newDiGraphTimeStd = this.diGraphTimeStd + other.diGraphTimeStd / 2;

        return new SampleAggregate(newDwellTimeMean, newDwellTimeStd, newFlightTimeMean, newFlightTimeStd,
                newKeyReleaseTimeMean, newKeyReleaseTimeStd, newKeyPressTimeMean, newKeyPressTimeStd,
                newDiGraphTimeMean, newDiGraphTimeStd);
    }
}
