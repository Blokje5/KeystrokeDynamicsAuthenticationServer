package shared.domain

case class User(username: String, password: String)
case class UserSession(sessionId: String, username: String)

case class KeystrokeData(eventtime: String, eventtype: String, sessionid: String, keyname: String, inputtype: String, id: String)
case class UserKeystrokeData(eventtime: String, eventtype: String, username: String, password: String, keyname: String)
