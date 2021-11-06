package scan4s

import org.http4s.blaze.client.BlazeClientBuilder
import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s.client.Client
import org.http4s.client.middleware._

object Main extends IOApp {
  private val apiKey = sys.env.getOrElse("API_KEY", "potato")

  def logActions: Option[String => IO[Unit]] = Some(s => IO.println(s))
  val middlewares: Client[IO] => Client[IO] = //Seq(
    RequestLogger.colored[IO](
      logHeaders = true,
      logBody = true,
      redactHeadersWhen = _ => false,
      logAction = logActions
    )(_) //,
//    FollowRedirect[IO](maxRedirects = 5)(_)
//  )

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val cfg: EtherscanConfig            = EtherscanConfig(NetEndpoint.MainNet, apiKey)
    val clientResource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO].resource
    clientResource
      .use { c =>
        Etherscan[IO](middlewares(c)).accounts
          .getBalance("0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae")
      }
      .map(res => println(res)) *> IO(ExitCode.Success)
  }
}
