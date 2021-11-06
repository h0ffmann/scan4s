package scan4s.serde

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import scan4s.algebras.Account.{Balance, MultiBalance, SingleBalance}
//import cats.syntax.all._
//import io.circe._

object Decoders {
  implicit val decodeBalance: Decoder[Balance]             = deriveDecoder[Balance]
  implicit val decodeSingleBalance: Decoder[SingleBalance] = deriveDecoder[SingleBalance]
  implicit val decodeMultiBalance: Decoder[MultiBalance]   = deriveDecoder[MultiBalance]
}
