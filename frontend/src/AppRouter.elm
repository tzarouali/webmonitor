module AppRouter exposing(..)

import UrlParser as P exposing (s, top)
import Model exposing (..)
import Msg exposing (..)
import Navigation exposing (..)
import Pages.LoginPage as LoginPage
import Pages.HomePage as HomePage
import Html exposing(..)
import Routes as R exposing (..)


route : P.Parser (Route -> a) a
route =
  P.oneOf
    [ P.map R.Home (P.s "home")
    , P.map R.Login (P.s "login")
    ]

update : UrlMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange newUrl ->
      ({model | history = P.parseHash route newUrl :: model.history}
      , Cmd.none)
    ShowHome ->
      let
        comm1 = Navigation.newUrl "#/home"
        comm2 = genHomeMsgCommand LoadSubscriptions
      in
      (model, Cmd.batch [comm1, comm2])
    ShowLogin ->
      (model, Navigation.newUrl "#/login")

view : Model -> Html Msg
view model =
  case model.history of
    currentLocation :: _ ->
      case currentLocation of
        Just route ->
          case route of
            R.Home ->
              HomePage.view model
            R.Login ->
              LoginPage.view model
        Nothing ->
          LoginPage.view model
    _ ->
      LoginPage.view model
