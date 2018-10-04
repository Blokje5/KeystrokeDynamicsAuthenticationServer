package shared.data

import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}
import shared.domain.UserKeystrokeData

class KeystrokeFeatureExtraction(val sc: SparkSession, val normalizedStartTimeDf: DataFrame) {
  import sc.implicits._

  lazy val lagLeadDf = addLagAndLeadColumns(normalizedStartTimeDf)

  val partitonBySessionAndKeynameOrderByTimestamp = Window
    .partitionBy("username", "keyname")
    .orderBy($"eventtime".asc)

  // Window by username, order by time -> recreation of typing session.
  val partitonBySessionOrderByTimestamp = Window
    .partitionBy("username")
    .orderBy($"eventtime".asc)

  val lagOnePartitioned = lag("normalizedTime", 1, 0)
    .over(partitonBySessionAndKeynameOrderByTimestamp)

  def dwellTime() = {
    // Dwell time = Keydown -> Keyup time = Keyup - Keydown
    lagLeadDf
      .withColumn("dwellTime", $"normalizedTime" - $"lag_one_partitioned")
      .filter("eventtype = 'keyup'")
      .select($"username", $"dwellTime")
      .groupBy($"username")
      .agg(
        mean($"dwellTime").alias("mean_dwell"),
        stddev_pop($"dwellTime").alias("std_dwell")
      )
      .cache()
  }

  def flightTime() = {
    // Key Interval (Flight Time) Key up KeyDown = Keydown - Keyup
    lagLeadDf
      //.withColumn("flightTime", $"lead_one" - $"normalizedTime")
      .withColumn("flightTime", leadMultiple())
      .filter("eventtype = 'keyup'")
      .select($"username", $"flightTime")
      .groupBy($"username")
      .agg(
        mean($"flightTime").alias("mean_flight"),
        stddev_pop($"flightTime").alias("std_flight")
      )
      .cache()
  }

  def keyPressLatency() = {
    // key press latency: Keydown -> Keydown
    lagLeadDf
      .filter("eventtype = 'keydown'")
      .withColumn("keyPressLatency", $"normalizedTime" - $"lag_one")
      .select($"username", $"keyPressLatency")
      .groupBy($"username")
      .agg(
        mean($"keyPressLatency").alias("mean_press"),
        stddev_pop($"keyPressLatency").alias("std_press")
      )
      .cache()
  }

  def keyReleaseLatency() = {
    // Key Release latency: Keyup -> Keyup.
    lagLeadDf
      .filter("eventtype = 'keyup'")
      .withColumn("keyReleaseLatency",  $"lead_one" - $"normalizedTime")
      .select($"username", $"keyReleaseLatency")
      .groupBy($"username")
      .agg(
        mean($"keyReleaseLatency").alias("mean_release"),
        stddev_pop($"keyReleaseLatency").alias("std_release")
      )
      .cache()
  }

  // TODO account for KD KD KD or KD KD KD KD
  def diGraph() = {
    // Di graph: Keydown Keyup Keydown Keyup or Keydown Keydown Keyup Keyup
    lagLeadDf
      .withColumn("diGraph", $"normalizedTime" - $"lag_three")
      .filter("eventtype = 'keyup'")
      .select($"username", $"diGraph")
      .groupBy($"username")
      .agg(
        mean($"diGraph").alias("mean_di"),
        stddev_pop($"diGraph").alias("std_di")
      )
      .cache()
  }

  private def addLagAndLeadColumns(normalizedStartTimeDf: DataFrame) = {
    val lagX = lagXOver(partitonBySessionOrderByTimestamp, "normalizedTime") _

    val lagOne = lagX(1)
    val lagTwo = lagX(2)
    val lagThree = lagX(3)


    val leadOne = lead("normalizedTime", 1, 0)
      .over(partitonBySessionOrderByTimestamp)

    normalizedStartTimeDf
      .withColumn("lag_one", lagOne)
      .withColumn("lag_one_partitioned", lagOnePartitioned)
      .withColumn("lag_two", lagTwo)
      .withColumn("lag_three", lagThree)
      .withColumn("lead_one", leadOne)
      .cache()
  }

  def lagXOver(windowSpec: WindowSpec, column: String)(x: Int) = lag(column, x, 0).over(windowSpec)

  // TODO This only checks ahead once, a better solution requires the time series format... See blog
  def leadMultiple(): Column = {
    when($"eventtype" === lead($"eventtype", 1).over(partitonBySessionOrderByTimestamp),
      lead($"normalizedTime", 2).over(partitonBySessionOrderByTimestamp))
      .otherwise(lead($"normalizedTime", 1).over(partitonBySessionOrderByTimestamp))
  }
}

object KeystrokeFeatureExtraction {
  def apply(normalizedStartTimeDf: DataFrame)(implicit sc: SparkSession): KeystrokeFeatureExtraction = new KeystrokeFeatureExtraction(sc, normalizedStartTimeDf)
}
