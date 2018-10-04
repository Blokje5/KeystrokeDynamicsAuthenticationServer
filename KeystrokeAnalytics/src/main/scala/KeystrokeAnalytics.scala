import org.apache.log4j._
import shared.data.{CleanData, KeystrokeAlgortihm, KeystrokeFeatureExtraction}
import shared.session.Session



object KeystrokeAnalytics {


  def main(args: Array[String]): Unit = {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    implicit val sc = Session.getSession()

    print(KeystrokeAlgortihm.keystrokeExample() mkString "\n")
//
//    val keystrokeDataDf = CleanData.prepareKeystrokeData()
//
//    // normalize start time
//    val normalizedStartTimeDf = CleanData.normalizeEventTime(keystrokeDataDf)
//
//    val keystrokeFeatures = KeystrokeFeatureExtraction(normalizedStartTimeDf)
//
//    // Dwell time = Keydown -> Keyup time = Keyup - Keydown
//    val dwellTimeDF = keystrokeFeatures.dwellTime()
//    // Key Interval (Flight Time) Key up KeyDown = Keydown - Keyup
//    val flightTimeDf = keystrokeFeatures.flightTime()
//
//    // key press latency: Keydown -> Keydown
//    val keyPressLatency = keystrokeFeatures.keyPressLatency()
//
//    // Key Release latency: Keyup -> Keyup.
//    val keyReleaseLatency = keystrokeFeatures.keyReleaseLatency()
//
//    // Di graph: Keydown Keyup Keydown Keyup or Keydown Keydown Keyup Keyup
//    val diGraphDF = keystrokeFeatures.diGraph()

  }
}

