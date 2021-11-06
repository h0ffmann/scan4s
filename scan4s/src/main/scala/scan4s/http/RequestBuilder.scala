package scan4s.http

import org.http4s.Method

case class RequestBuilder[Res](
    url: String,
    httpVerb: Method = Method.GET,
    authHeader: Map[String, String] = Map.empty[String, String],
    data: Option[Res] = None,
    params: Map[String, String] = Map.empty[String, String],
    headers: Map[String, String] = Map.empty[String, String]
) {

  def postMethod: RequestBuilder[Res] = this.copy(httpVerb = Method.POST)

  def patchMethod: RequestBuilder[Res] = this.copy(httpVerb = Method.PATCH)

  def putMethod: RequestBuilder[Res] = this.copy(httpVerb = Method.PUT)

  def deleteMethod: RequestBuilder[Res] = this.copy(httpVerb = Method.DELETE)

  def withHeaders(headers: Map[String, String]): RequestBuilder[Res] = this.copy(headers = headers)

  def withParams(params: Map[String, String]): RequestBuilder[Res] = this.copy(params = params)

  def withData(data: Res): RequestBuilder[Res] = this.copy(data = Some(data))

  def withAuth(accessApiKey: String): RequestBuilder[Res] = {
    println(accessApiKey)
    this.copy(params = this.params + ("apikey" -> accessApiKey))
  }
}
