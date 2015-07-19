package au.com.simplemachines.net

import java.io.{IOException, ByteArrayInputStream, ByteArrayOutputStream}
import java.net.HttpURLConnection

import org.specs2.mock.Mockito
import org.specs2.specification.Scope

class RestMateSpec extends org.specs2.mutable.Specification with Mockito {

  trait Fixtures extends Scope {
    val connection = mock[HttpURLConnection]

    val restMate = new RestMate {
      @throws(classOf[IOException])
      override private[net] def openConnection(urlStr: String) = connection
    }
  }

  "RestMate" >> {
    "should setup connection for successful post request and prepare expected response" >> new Fixtures {
      val requestOut = new ByteArrayOutputStream()
      val responseIn = new ByteArrayInputStream("Baz".getBytes)
      connection.getOutputStream returns requestOut
      connection.getInputStream returns responseIn
      connection.getResponseCode returns 200
      connection.getHeaderFieldKey(1) returns "Content-Type"
      connection.getHeaderField(1) returns "text/plain"

      val response = restMate.post("http://simplemachines.com.au",
        RestMateRequestOptions.Default
          .contentType("text/html")
          .timeoutMs(2000)
          .withHeader("Jack", "Foo")
          .withBody("Foo Bar"))

      there was one(connection).setRequestMethod("POST")
      there was one(connection).setConnectTimeout(2000)
      there was one(connection).addRequestProperty("Content-Type", "text/html")
      there was one(connection).addRequestProperty("Jack", "Foo")

      new String(requestOut.toByteArray) must_=== "Foo Bar"
      response.getResponseBodyAsString must_== Some("Baz")
      response.statusCode must_== 200
      response.headers must_== Map("Content-Type" -> "text/plain")
    }
    "should get error stream if unsuccessful" >> new Fixtures {
      val requestOut = new ByteArrayOutputStream()
      val errorIn = new ByteArrayInputStream("Some Error".getBytes)

      connection.getOutputStream returns requestOut
      connection.getErrorStream returns errorIn

      val response = restMate.get("http://simplemachines.com.au")

      response.getResponseBodyAsString must_== Some("Some Error")
    }
  }
}
