package model

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._

final case class WsAuthRequest(userId: UUID, token: String)

object WsAuthRequest {

  implicit lazy val wsAuthRequestEncoder : Encoder[WsAuthRequest] = deriveEncoder
  implicit lazy val wsAuthRequestDecoder : Decoder[WsAuthRequest] = deriveDecoder

}
