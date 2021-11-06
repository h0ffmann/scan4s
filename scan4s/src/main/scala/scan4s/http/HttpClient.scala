package scan4s.http

import cats.data.EitherT
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.syntax.all._
import scan4s.algebras.AccessApiKey
import scan4s.http.Http4sSyntax._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.CirceEntityDecoder._
//import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s._
import scan4s.ETHError.{JsonParsingError, UnhandledResponseError}
import scan4s.domain.Pagination
import scan4s.{ETHError, ETHResponse, EtherscanConfig}

class HttpClient[F[_]: Concurrent](
    client: Client[F],
    val config: EtherscanConfig,
    accessTokens: AccessApiKey[F]
) {
  import HttpClient._
  import accessTokens._

  def get[Res: Decoder](
      method: String,
      //headers: Map[String, String] = Map.empty,
      params: Map[String, String] = Map.empty,
      pagination: Option[Pagination] = None
  ): F[ETHResponse[Res]] =
    withAccessApiKey { accessToken =>
      run[Unit, Res](
        RequestBuilder(url = buildURL(method))
          .withAuth(accessToken)
          .withParams(
            params ++ pagination.fold(Map.empty[String, String])(p =>
              Map("page" -> p.page.toString, "per_page" -> p.per_page.toString)
            )
          )
      )
    }

  def getWithoutResponse(
      url: String,
      headers: Map[String, String] = Map.empty
  ): F[ETHResponse[Unit]] =
    withAccessApiKey(accessToken =>
      runWithoutResponse[Unit](
        RequestBuilder(buildURL(url)).withHeaders(headers).withAuth(accessToken)
      )
    )

  def patch[Req: Encoder, Res: Decoder](
      method: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[ETHResponse[Res]] =
    withAccessApiKey(accessToken =>
      run[Req, Res](
        RequestBuilder(buildURL(method)).patchMethod
          .withAuth(accessToken)
          .withHeaders(headers)
          .withData(data)
      )
    )

  def put[Req: Encoder, Res: Decoder](
      url: String,
      headers: Map[String, String] = Map(),
      data: Req
  ): F[ETHResponse[Res]] =
    withAccessApiKey(accessToken =>
      run[Req, Res](
        RequestBuilder(buildURL(url)).putMethod
          .withAuth(accessToken)
          .withHeaders(headers)
          .withData(data)
      )
    )

  def post[Req: Encoder, Res: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[ETHResponse[Res]] =
    withAccessApiKey(accessToken =>
      run[Req, Res](
        RequestBuilder(buildURL(url)).postMethod
          .withAuth(accessToken)
          .withHeaders(headers)
          .withData(data)
      )
    )

  def postAuth[Req: Encoder, Res: Decoder](
      method: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[ETHResponse[Res]] =
    run[Req, Res](RequestBuilder(buildURL(method)).postMethod.withHeaders(headers).withData(data))

  def postOAuth[Res: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty,
      params: Map[String, String] = Map.empty
  ): F[ETHResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(url).postMethod
        .withHeaders(Map("Accept" -> "application/json") ++ headers)
        .withParams(params)
    )

  def delete(
      url: String,
      headers: Map[String, String] = Map.empty
  ): F[ETHResponse[Unit]] =
    withAccessApiKey(accessToken =>
      run[Unit, Unit](
        RequestBuilder(buildURL(url)).deleteMethod.withHeaders(headers).withAuth(accessToken)
      )
    )

  def deleteWithResponse[Res: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty
  ): F[ETHResponse[Res]] =
    withAccessApiKey(accessToken =>
      run[Unit, Res](
        RequestBuilder(buildURL(url)).deleteMethod
          .withAuth(accessToken)
          .withHeaders(headers)
      )
    )

  def deleteWithBody[Req: Encoder, Res: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[ETHResponse[Res]] =
    withAccessApiKey(accessToken =>
      run[Req, Res](
        RequestBuilder(buildURL(url)).deleteMethod
          .withAuth(accessToken)
          .withHeaders(headers)
          .withData(data)
      )
    )

  private def buildURL(method: String): String = {
    val url = s"${config.url.entryName}$method"
    println(url)
    url
  }

  private def run[Req: Encoder, Res: Decoder](request: RequestBuilder[Req]): F[ETHResponse[Res]] =
    runRequest(request)
      .use { response =>
        buildResponse(response).map(ETHResponse(_, response.status.code, response.headers.toMap))
      }

  private def runWithoutResponse[Req: Encoder](request: RequestBuilder[Req]): F[ETHResponse[Unit]] =
    runRequest(
      request
    ).use { response =>
      buildResponseFromEmpty(response).map(
        ETHResponse(_, response.status.code, response.headers.toMap)
      )
    }

  private def runRequest[Req: Encoder](request: RequestBuilder[Req]): Resource[F, Response[F]] =
    client
      .run(
        Request[F]()
          .withMethod(request.httpVerb)
          .withUri(request.toUri(config))
          .withHeaders(Headers(config.toHeaderList) ++ Headers(request.toHeaderList))
          .withJsonBody(request.data)
      )
}

object HttpClient {
  // the GitHub API sometimes returns [[BasicError]] when 404.
//  private[scan4s] val notFoundDecoder: Decoder[EtherscanError] =
//    implicitly[Decoder[NotFoundError]].widen.or(BasicError.basicErrorDecoder.widen)
//  private def notFoundEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, GHError] =
//    jsonOf(implicitly, notFoundDecoder)

  private[scan4s] def buildResponse[F[_]: Concurrent, A: Decoder](
      response: Response[F]
  ): F[Either[ETHError, A]] =
    (response.status.code match {
      case i if Status.fromInt(i).exists(_.isSuccess) => response.attemptAs[A].map(_.asRight)
//      case 400 => response.attemptAs[BadRequestError].map(_.asLeft)
//      case 401 => response.attemptAs[UnauthorizedError].map(_.asLeft)
//      case 403 => response.attemptAs[ForbiddenError].map(_.asLeft)
//      case 404 => response.attemptAs[GHError](notFoundEntityDecoder).map(_.asLeft)
//      case 422 => response.attemptAs[UnprocessableEntityError].map(_.asLeft)
//      case 423 => response.attemptAs[RateLimitExceededError].map(_.asLeft)
      case _ =>
        EitherT
          .right[DecodeFailure](responseBody(response))
          .map(s =>
            UnhandledResponseError(s"Unhandled status code ${response.status.code}", s).asLeft
          )
    }).fold(
      e => (JsonParsingError(e): ETHError).asLeft,
      _.leftMap[ETHError](identity)
    )

  private[scan4s] def buildResponseFromEmpty[F[_]: Concurrent](
      response: Response[F]
  ): F[Either[ETHError, Unit]] =
    if (response.status.isSuccess)
      Either.unit[ETHError].pure[F]
    else
      buildResponse[F, Unit](response)

  private def responseBody[F[_]: Concurrent](response: Response[F]): F[String] =
    response.bodyText.compile.foldMonoid
}
