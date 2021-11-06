package scan4s

import org.http4s.DecodeFailure

sealed abstract class ETHError(
    message: String
) extends Exception {
  final override def fillInStackTrace(): Throwable = this
  final override def getMessage(): String          = message
}

object ETHError {
  final case class UnhandledResponseError(
      message: String,
      body: String
  ) extends ETHError(message) {
    override def toString(): String = s"UnhandledResponseError($message, $body)"
  }

  final case class JsonParsingError(
      message: String,
      cause: Option[Throwable]
  ) extends ETHError(message) {
    override def toString(): String    = s"JsonParsingError($message, $cause)"
    override def getCause(): Throwable = cause.orNull
  }
  object JsonParsingError {
    def apply(df: DecodeFailure): JsonParsingError = JsonParsingError(df.message, df.cause)
  }
}
