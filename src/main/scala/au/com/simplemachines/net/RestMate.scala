package au.com.simplemachines.net

import java.io._
import java.net.{URL, HttpURLConnection}

import au.com.simplemachines.net.RestMate.HttpMethod

import scala.annotation.tailrec
import scala.collection.mutable

/**
 * A simple RESTful request utility. Uses standard <code>HttpURLConnection</code>.
 * <p/>
 * Similar in concept to Spring's <code>RestTemplate</code> but simpler and without the dep.
 */
object RestMate {

  protected object HttpMethod extends Enumeration {
    type HttpMethod = Value
    val GET, PUT, POST, DELETE, HEAD = Value
  }

}

class RestMate extends RestOps {

  import RestMate.HttpMethod._

  def get(url: String, requestOptions: RestMateRequestOptions): RestMateResponse =
    makeRequest(url, RestMate.HttpMethod.GET, requestOptions)

  def put(url: String, requestOptions: RestMateRequestOptions): RestMateResponse =
    makeRequest(url, RestMate.HttpMethod.PUT, requestOptions)

  def post(url: String, requestOptions: RestMateRequestOptions): RestMateResponse =
    makeRequest(url, RestMate.HttpMethod.POST, requestOptions)

  def delete(url: String, requestOptions: RestMateRequestOptions): RestMateResponse =
    makeRequest(url, RestMate.HttpMethod.DELETE, requestOptions)

  def head(url: String, requestOptions: RestMateRequestOptions): RestMateResponse =
    makeRequest(url, RestMate.HttpMethod.HEAD, requestOptions)

  /**
   * Template. Override in subclasses.
   *
   * @param urlStr the url to request.
   * @param method the HTTP method type.
   * @param opts   the options to use.
   * @return RestMateResponse the response. Never null.
   */
  protected def makeRequest(urlStr: String, method: HttpMethod, opts: RestMateRequestOptions): RestMateResponse = {
    var connection: HttpURLConnection = null
    try {
      connection = prepareRequest(openConnection(urlStr), method, opts)
      connection.connect()
      opts.body foreach { inputStream =>
        copyAndClose(inputStream, connection.getOutputStream)
      }
      prepareResponse(connection)
    }
    catch {
      case ex: IOException => {
        throw new ConnectionException("While connecting to " + urlStr, ex)
      }
    } finally {
      if (connection != null) {
        connection.disconnect()
      }
    }
  }

  @throws(classOf[IOException])
  private[net] def openConnection(urlStr: String): HttpURLConnection = {
    new URL(urlStr).openConnection.asInstanceOf[HttpURLConnection]
  }

  @throws(classOf[IOException])
  private def prepareRequest(connection: HttpURLConnection, method: HttpMethod, req: RestMateRequestOptions): HttpURLConnection = {
    connection.setDoInput(true)
    if (method eq RestMate.HttpMethod.GET) {
      connection.setInstanceFollowRedirects(req.followingRedirects)
    }
    else {
      connection.setInstanceFollowRedirects(false)
    }
    if (method == HttpMethod.PUT || method == HttpMethod.POST) {
      connection.setDoOutput(true)
    }
    else {
      connection.setDoOutput(false)
    }
    connection.setRequestMethod(method.toString)
    for ((k, v) <- req.headers) {
      connection.addRequestProperty(k, v)
    }
    connection.setConnectTimeout(req.connectionTimeout)
    connection
  }

  @tailrec
  private def getHeadersAsMap(connection: HttpURLConnection,
                              headers: mutable.Map[String, String] = mutable.Map.empty[String, String],
                              n: Int = 1): Map[String, String] = {
    Option(connection.getHeaderFieldKey(n)) match {
      case None => headers.toMap
      case Some(name) => getHeadersAsMap(connection, headers += (name -> connection.getHeaderField(n)), n + 1)
    }
  }

  @throws(classOf[IOException])
  private def prepareResponse(connection: HttpURLConnection): RestMateResponse = {
    // Header field 0 is the status line for most but not all HttpURLConnection impls (see the JavaDoc for
    // getHeaderField and getHeaderFieldKey). In the case that header field 0 is the status code the key will be
    // null.
    val responseHeaders = Option(connection.getHeaderFieldKey(0)) match {
      case Some(name) => getHeadersAsMap(connection, mutable.Map(name -> connection.getHeaderField(0)))
      case _ => getHeadersAsMap(connection)
    }

    val out = new ByteArrayOutputStream
    val in = Option(connection.getErrorStream) match {
      case Some(errorStream) => errorStream
      case _ => connection.getInputStream
    }
    copyAndClose(in, out)
    new RestMateResponse(connection.getResponseCode, connection.getResponseMessage, responseHeaders, Option(out.toByteArray))
  }

  def copyAndClose(inputStream: InputStream, outputStream: OutputStream) = {
    try {
      copy(inputStream, outputStream)
    } finally {
      close(inputStream, outputStream)
    }
  }

  @tailrec
  private final def copy(inputStream: InputStream, outputStream: OutputStream, bufferSize: Int = 1024) {
    val buf = new Array[Byte](bufferSize)
    inputStream.read(buf, 0, buf.length) match {
      case -1 => ()
      case n =>
        outputStream.write(buf, 0, n)
        copy(inputStream, outputStream, bufferSize)
    }
  }

  @throws(classOf[IOException])
  private def close(closables: Closeable*) = {
    closables foreach { c =>
      try {
        c.close()
      } catch {
        case e: Exception => ()
      }
    }
  }
}
