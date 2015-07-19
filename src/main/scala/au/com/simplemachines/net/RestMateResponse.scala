package au.com.simplemachines.net

case class RestMateResponse(statusCode: Int,
                            statusMessage: String,
                            headers: Map[String, String],
                            responseBody: Option[Array[Byte]] = None) {

  /** Return responseBody in default charset. */
  def getResponseBodyAsString: Option[String] = {
    responseBody.map(new String(_))
  }

  /** Is the status code in the 200 range? */
  def isSuccessful: Boolean = {
    statusCode >= 200 && statusCode <= 300
  }

}
