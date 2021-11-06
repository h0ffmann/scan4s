package scan4s.algebras

import scan4s.algebras.Account.{Balance, MultiBalance}
import scan4s.{BlockTag, ETHResponse}

//TODO balance is given in wei, create converters

object Account {
  final case class Balance(status: String, message: String, result: String)
  final case class SingleBalance(account: String, balance: String)
  final case class MultiBalance(status: String, message: String, result: List[SingleBalance])
}

trait Account[F[_]] {

  protected val module              = "account"
  protected val singleBalanceAction = "balance"
  protected val multiBalanceAction  = "balancemulti"

  def getBalance(
      address: String,
      tag: BlockTag = BlockTag.Latest,
      params: Map[String, String] = Map()
  ): F[ETHResponse[Balance]]

  def getMultiBalance(
      addresses: List[String],
      tag: BlockTag = BlockTag.Latest,
      params: Map[String, String] = Map()
  ): F[ETHResponse[MultiBalance]]
}
