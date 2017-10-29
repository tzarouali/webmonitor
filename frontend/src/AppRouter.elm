module AppRouter exposing(..)

import UrlParser as Url exposing (top)
import Model exposing (..)
import Msg exposing (..)
import Navigation exposing (..)
import Pages.LoginPage as LoginPage
import Html exposing(..)
import Routes exposing (..)

route : Url.Parser (Route -> a) a
route =
  Url.oneOf
    [ Url.map Home top
    ]

update : UrlMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange newUrl ->
      ({model | history = Url.parsePath route newUrl :: model.history}
      , Cmd.none)
    LoginOk ->
      (model, Navigation.newUrl "#/home")

view : Model -> Html Msg
view model =
  case model.history of
    currentLocation :: _ ->
      case currentLocation of
        Just r ->
          case r of
            Home ->
              div [] [text "aaaaaaaaaaaaaaaaaaaaaaaaaa"]
            Login ->
              LoginPage.view model
        Nothing ->
          LoginPage.view model
    _ ->
      LoginPage.view model
