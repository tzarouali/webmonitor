module AppRouter exposing(..)

import UrlParser as Url exposing (top)
import Model exposing (..)
import Msg exposing (..)

type Route =
    Login
  | Home

route : Url.Parser (Route -> a) a
route =
  Url.oneOf
    [ Url.map Home top
    ]

update : UrlMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange newUrl ->
      (model, Cmd.none)
