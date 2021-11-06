package scan4s.algebras

import scan4s.ETHResponse

trait AccessApiKey[F[_]] {

  def withAccessApiKey[T](f: String => F[ETHResponse[T]]): F[ETHResponse[T]]
}
