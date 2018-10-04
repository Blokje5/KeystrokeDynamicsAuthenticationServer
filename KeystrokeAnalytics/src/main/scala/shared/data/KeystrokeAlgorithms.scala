package shared.data


import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable.{HashMap, Map}
import scala.collection.mutable.ListBuffer

object KeystrokeAlgortihm {
  def keystrokeExample()(implicit sc: SparkSession): List[(String, Long, Long)] = {
    val rawJson = List("""{"sample":0,"events":[{"eventType":"keydown","eventTime":8781416,"keyName":"h"},{"eventType":"keydown","eventTime":8781476,"keyName":"u"},{"eventType":"keyup","eventTime":8781526,"keyName":"h"},{"eventType":"keyup","eventTime":8781558,"keyName":"u"},{"eventType":"keydown","eventTime":8781676,"keyName":"n"},{"eventType":"keyup","eventTime":8781733,"keyName":"n"},{"eventType":"keydown","eventTime":8781756,"keyName":"t"},{"eventType":"keyup","eventTime":8781813,"keyName":"t"},{"eventType":"keydown","eventTime":8781883,"keyName":"e"},{"eventType":"keydown","eventTime":8781910,"keyName":"r"},{"eventType":"keyup","eventTime":8781950,"keyName":"e"},{"eventType":"keyup","eventTime":8781990,"keyName":"r"},{"eventType":"keydown","eventTime":8782050,"keyName":"2"},{"eventType":"keyup","eventTime":8782133,"keyName":"2"}]}""")

    import sc.implicits._

    case class SampleData (sample: Long, events: Array[String])
    val reader = sc.read
    val jsonDF = reader.json(sc.sparkContext.parallelize(rawJson).toDS())

    val sampleData = jsonDF
      .select(explode($"events") as "event") // converts event to struct
      .select("event.*") // select all subfields in event, creating three columns


   mapOverEventTime(sampleData)
  }

  /**
    * Convert an event array into an array of the format [keyName, keyDownTime, keyUpTime]
    * assume we start with an keydown
    */
  private def mapOverEventTime(sampleData: DataFrame): List[(String, Long, Long)] = {
    val map: Map[String, (String, Long)] = new HashMap().empty
    val result: ListBuffer[(String, Long, Long)] = new ListBuffer()

    sampleData
      .foreach(row => {
        val eventTime = row.getAs[Long](0)
        val eventType = row.getAs[String](1)
        val keyName = row.getAs[String](2)

        if (map contains keyName) {
          val (_, oldTime) = map get keyName get
          val tuple = (keyName, oldTime, eventTime)
          map remove keyName
          result += tuple
        } else {
          map + keyName -> (eventType, eventTime)
        }
      })

    result.toList
  }

  /**
    * After we mapped the data calculating the different features should be really easy
    */
  private def calculateKeystrokeFeatures(mappedData: List[(String, Long, Long)]): Unit = {
    // dwellTime = keyup - keydown
    val dwellTimeList = mappedData
      .map(tuple => tuple._3 - tuple._2)

    // zipped to reproduce the effects of lagging
    val zipped = mappedData zip mappedData.tail

    // flightTime = keydown - keyup
    val flightTimeList = zipped.map { case (tuple1, tuple2) => tuple2._2 - tuple1._3}
  }
}
