module AppRouter exposing(..)

import UrlParser as P exposing (s, top)
import Model exposing (..)
import Msg exposing (..)
import Navigation exposing (..)
import Pages.LoginPage as LoginPage
import Pages.HomePage as HomePage
import Html exposing(..)
import Routes exposing (..)

route : P.Parser (Route -> a) a
route =
  P.oneOf
    [ P.map Home (P.s "home")
    , P.map Login (P.s "login")
    ]

update : UrlMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange newUrl ->
      ({model | history = P.parseHash route newUrl :: model.history}
      , Cmd.none)
    ShowHome ->
      (model, Navigation.newUrl "#/home")
    ShowLogin ->
      (model, Navigation.newUrl "#/login")

view : Model -> Html Msg
view model =
  case model.history of
    currentLocation :: _ ->
      case currentLocation of
        Just r ->
          case r of
            Home ->
              HomePage.view model
            Login ->
              LoginPage.view model
        Nothing ->
          LoginPage.view model
    _ ->
      LoginPage.view model

