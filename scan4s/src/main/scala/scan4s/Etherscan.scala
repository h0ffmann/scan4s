package scan4s

import cats.effect.kernel.Concurrent
import scan4s.algebras.{AccessApiKey, Account, EtherscanAPIs}
import scan4s.interpreters.StaticApiKey
import scan4s.modules.EtherscanAPIv1
import org.http4s.client.Client

class Etherscan[F[_]: Concurrent](
    client: Client[F],
    accessKey: AccessApiKey[F]
)(implicit config: EtherscanConfig)
    extends EtherscanAPIs[F] {

  private lazy val module: EtherscanAPIs[F] = new EtherscanAPIv1[F](client, config, accessKey)

  lazy val accounts: Account[F] = module.accounts
//  lazy val repos: Repositories[F]          = module.repos
//  lazy val auth: Auth[F]                   = module.auth
//  lazy val gists: Gists[F]                 = module.gists
//  lazy val issues: Issues[F]               = module.issues
//  lazy val activities: Activities[F]       = module.activities
//  lazy val gitData: GitData[F]             = module.gitData
//  lazy val pullRequests: PullRequests[F]   = module.pullRequests
//  lazy val organizations: Organizations[F] = module.organizations
//  lazy val teams: Teams[F]                 = module.teams
//  lazy val projects: Projects[F]           = module.projects
}

object Etherscan {

  def apply[F[_]: Concurrent](
      client: Client[F]
  )(implicit config: EtherscanConfig): Etherscan[F] =
    new Etherscan[F](client, new StaticApiKey(config.apiKey))
}
