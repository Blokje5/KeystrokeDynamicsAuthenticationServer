package shared.session

import org.apache.spark.sql.SparkSession

object Session {

  def getSession(): SparkSession = {
    SparkSession
      .builder()
      .appName("KeystrokeAnalytics")
      .master("local[*]")
      .getOrCreate()
  }
}
