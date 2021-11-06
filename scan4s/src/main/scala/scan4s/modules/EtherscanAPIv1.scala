package scan4s.modules

import cats.effect.kernel.Concurrent
import org.http4s.client.Client
import scan4s.EtherscanConfig
import scan4s.algebras.AccessApiKey
//import org.http4s.client.Client
import scan4s.http.HttpClient
import scan4s.interpreters._
import scan4s.algebras._

class EtherscanAPIv1[F[_]: Concurrent](
    client: Client[F],
    config: EtherscanConfig,
    apiKey: AccessApiKey[F]
) extends EtherscanAPIs[F] {

  implicit val httpClient: HttpClient[F] = new HttpClient[F](client, config, apiKey)

  override val accounts: Account[F] = new AccountInterpreter[F]
//  override val repos: Repositories[F]          = new RepositoriesInterpreter[F]
//  override val auth: Auth[F]                   = new AuthInterpreter[F]
//  override val gists: Gists[F]                 = new GistsInterpreter[F]
//  override val issues: Issues[F]               = new IssuesInterpreter[F]
//  override val activities: Activities[F]       = new ActivitiesInterpreter[F]
//  override val gitData: GitData[F]             = new GitDataInterpreter[F]
//  override val pullRequests: PullRequests[F]   = new PullRequestsInterpreter[F]
//  override val organizations: Organizations[F] = new OrganizationsInterpreter[F]
//  override val teams: Teams[F]                 = new TeamsInterpreter[F]
//  override val projects: Projects[F]           = new ProjectsInterpreter[F]

}
