package scan4s.interpreters

import scan4s.ETHResponse
import scan4s.algebras.AccessApiKey

class StaticApiKey[F[_]](accessToken: String) extends AccessApiKey[F] {

  override def withAccessApiKey[T](f: String => F[ETHResponse[T]]): F[ETHResponse[T]] =
    f(accessToken)
}
