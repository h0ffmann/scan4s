package scan4s

import cats.implicits.catsSyntaxOptionId
//import ciris._
//import eu.timepit.refined.api.Refined
//import eu.timepit.refined.string.{MatchesRegex, Url}
//import eu.timepit.refined.types.string.NonEmptyString
//import eu.timepit.refined._
//import eu.timepit.refined.collection._
//import eu.timepit.refined._
//import eu.timepit.refined.auto._
//import eu.timepit.refined.numeric._
//import eu.timepit.refined.api.{RefType, Refined}
//import eu.timepit.refined.boolean._
//import eu.timepit.refined.char._
//import eu.timepit.refined.collection._
//import eu.timepit.refined.generic._
//import eu.timepit.refined.string._
import org.http4s.Header
import org.typelevel.ci.CIString
//import scala.concurrent.duration._

//final case class EtherscanConfig(
//    url: String Refined Url,
//    apiKey: Secret[ApiKey],
//    headers: Map[NonEmptyString, NonEmptyString],
//    rateLimit: Option[Duration]
//                                )
final case class EtherscanConfig(
    url: NetEndpoint,
    apiKey: String,
    headers: Map[String, String] = Map(),
    rateLimitPerSec: Option[Int] = None
) {
  def toHeaderList: List[Header.Raw] = headers.map { case (k, v) =>
    Header.Raw(CIString(k), v)
  }.toList
}

object EtherscanConfig {
  //type ApiKey = String Refined MatchesRegex["[a-zA-Z0-9]{25,40}"]
  implicit val default: EtherscanConfig =
    EtherscanConfig(
      NetEndpoint.MainNet,
      apiKey =
        "https://docs.etherscan.io/getting-started/viewing-api-usage-statistics#creating-an-api-key",
      headers = Map("User-Agent" -> "scan4s"),
      rateLimitPerSec = 5.some
    )
}
