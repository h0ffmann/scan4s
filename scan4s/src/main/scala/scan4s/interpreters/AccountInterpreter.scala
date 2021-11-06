package scan4s.interpreters

import scan4s.algebras.Account
import scan4s.http.HttpClient
import scan4s.{BlockTag, ETHResponse}
import scan4s.serde.Decoders._
class AccountInterpreter[F[_]](implicit client: HttpClient[F]) extends Account[F] {

  override def getBalance(
      address: String,
      tag: BlockTag,
      params: Map[String, String]
  ): F[ETHResponse[Account.Balance]] = {
    val pars = Map(
      "module"  -> module,
      "action"  -> singleBalanceAction,
      "address" -> address,
      "tag"     -> tag.entryName
    )
    client.get[Account.Balance]("/api", params = pars ++ params, pagination = None)
  }

  override def getMultiBalance(
      addresses: List[String],
      tag: BlockTag,
      params: Map[String, String]
  ): F[ETHResponse[Account.MultiBalance]] = {
    val pars = Map(
      "module"  -> module,
      "action"  -> multiBalanceAction,
      "address" -> addresses.mkString(","),
      "tag"     -> tag.entryName
    )
    client.get[Account.MultiBalance]("/api", params = pars ++ params, pagination = None)
  }
}
