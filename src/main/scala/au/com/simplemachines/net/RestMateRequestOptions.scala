package au.com.simplemachines.net

import java.io.{ByteArrayInputStream, InputStream}

/** Options for an HTTP request. */
object RestMateRequestOptions {
  def Default = new RestMateRequestOptions()
}

case class RestMateRequestOptions(headers: List[(String, String)] = Nil,
                                  followingRedirects: Boolean = false,
                                  connectionTimeout: Int = 5000,
                                  body: Option[InputStream] = None) {

  def timeoutMs(ms: Int) =
    copy(connectionTimeout = ms)

  def contentType(contentType: String) =
    copy(headers = ("Content-Type" -> contentType) :: headers)

  def withHeader(key: String, value: String) =
    copy(headers = (key -> value) :: headers)

  def withBody(bodyStr: String) =
    copy(body = Some(new ByteArrayInputStream(bodyStr.getBytes)))

  def withBody(in: InputStream) =
    copy(body = Some(in))
}