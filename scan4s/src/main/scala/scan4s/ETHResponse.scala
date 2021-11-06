package scan4s

final case class ETHResponse[A](
    result: Either[ETHError, A],
    statusCode: Int,
    headers: Map[String, String]
)
