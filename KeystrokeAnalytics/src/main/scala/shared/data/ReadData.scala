package shared.data

import org.apache.spark.sql.{Dataset, SparkSession}
import shared.domain.{KeystrokeData, User, UserKeystrokeData, UserSession}

object ReadData {

  def readUserData()(implicit sc: SparkSession): Dataset[User] = {
    import sc.implicits._
    sc
      .read
      .option("header", "true")
      .csv("../keystroke-data/user.csv")
      .as[User]
  }

  def readUserSessionData()(implicit sc: SparkSession): Dataset[UserSession] = {
    import sc.implicits._
    sc
      .read
      .option("header", "true")
      .csv("../keystroke-data/user_session.csv")
      .as[UserSession]
  }

  def readKeystrokeData()(implicit sc: SparkSession): Dataset[KeystrokeData] = {
    import sc.implicits._
    sc
      .read
      .json("../keystroke-data/data.json")
      .as[KeystrokeData]
  }
}
