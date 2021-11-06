package scan4s.algebras

trait EtherscanAPIs[F[_]] {

  def accounts: Account[F]
}
