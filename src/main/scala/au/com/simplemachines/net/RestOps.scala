package au.com.simplemachines.net

/** RESTful operations. */
trait RestOps {
  /** HTTP GET request for url. */
  def get(url: String, requestOptions: RestMateRequestOptions = RestMateRequestOptions.Default): RestMateResponse

  /** HTTP PUT request for url. */
  def put(url: String, requestOptions: RestMateRequestOptions = RestMateRequestOptions.Default): RestMateResponse

  /** HTTP POST request for url. */
  def post(url: String, requestOptions: RestMateRequestOptions = RestMateRequestOptions.Default): RestMateResponse

  /** HTTP DELETE request for url. */
  def delete(url: String, requestOptions: RestMateRequestOptions = RestMateRequestOptions.Default): RestMateResponse

  /** HTTP HEAD request for url. */
  def head(url: String, requestOptions: RestMateRequestOptions = RestMateRequestOptions.Default): RestMateResponse
}
