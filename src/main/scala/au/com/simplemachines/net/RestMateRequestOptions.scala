package au.com.simplemachines.net

import java.io.{ByteArrayInputStream, InputStream}

/** Options for an HTTP request. */
object RestMateRequestOptions {
  def Default = new RestMateRequestOptions()
}

case class RestMateRequestOptions(headers: List[(String, String)] = Nil,
                                  followingRedirects: Boolean = false,
                                  connectionTimeout: Int = 5000,
                                  readTimeout: Int = 10000,
                                  body: Option[InputStream] = None) {

  @deprecated("Use connectionTimeoutMs", "1.0.1")
  def timeoutMs(ms: Int) = connectionTimeoutMs(ms)

  def connectionTimeoutMs(ms: Int) =
    copy(connectionTimeout = ms)

  def readTimeoutMs(ms: Int) =
    copy(readTimeout = ms)

  def contentType(contentType: String) =
    copy(headers = ("Content-Type" -> contentType) :: headers)

  def withHeader(key: String, value: String) =
    copy(headers = (key -> value) :: headers)

  def withBody(bodyStr: String) =
    copy(body = Some(new ByteArrayInputStream(bodyStr.getBytes)))

  def withBody(in: InputStream) =
    copy(body = Some(in))
}