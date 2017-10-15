package webmonitor.global

import play.api.http.HttpErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

object CustomErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  override def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}
