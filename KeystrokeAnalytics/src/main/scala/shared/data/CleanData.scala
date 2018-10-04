package shared.data

import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.functions.first
import org.apache.spark.sql.{Dataset, SparkSession}
import shared.domain.UserKeystrokeData

object CleanData {
  def prepareKeystrokeData()(implicit sc: SparkSession): Dataset[UserKeystrokeData] = {
    import sc.implicits._
    val userDf = ReadData.readUserData()

    val userSessionDf = ReadData.readUserSessionData()

    val rawDf = ReadData.readKeystrokeData()

    rawDf
      // only keyup or keydown events
      .filter("eventtype IN ('keyup', 'keydown')")
      // make sure lastpass data is filtered ...
      .filter("LENGTH(keyname) = 1 OR keyname IN ('Backspace','Shift', 'Tab','Enter')")
      // filter on password fields
      .filter("inputtype = 'password'")
      // join username
      .join(userSessionDf, "sessionId")
      // join password
      .join(userDf, "username")
      .select("eventtime", "eventtype", "username", "password", "keyname")
      .orderBy("eventtime")
      .as[UserKeystrokeData]
      .cache()
  }

  def normalizeEventTime(keystrokeDataDf: Dataset[UserKeystrokeData])(implicit sc: SparkSession) = {
    import sc.implicits._
    val partitonBySessionOrderByTimestamp = Window
      .partitionBy("username")
      .orderBy($"eventtime".asc)

    // first value within a shared.session indicates the time a user started typing
    val startTime = first($"eventtime")
      .over(partitonBySessionOrderByTimestamp)

    // normalize start time
    keystrokeDataDf
      .withColumn("startTime", startTime)
      .select($"*", ($"eventtime" - $"startTime") as "normalizedTime")
      .cache()
  }
}
