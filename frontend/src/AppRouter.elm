module AppRouter exposing(..)

import UrlParser as P exposing (s)
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
    [ P.map R.Home (P.s R.homePathEnd)
    , P.map R.Login (P.s R.loginPathEnd)
    ]

update : UrlMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange newUrl ->
      let
        routeHist = model.commonModel.routeHistory
      in
        (model |> updateRouteHistory (P.parseHash route newUrl :: routeHist), Cmd.none)

    ShowHome ->
      let
        comm1 = Navigation.newUrl R.homePathHash
        comm2 = genHomeMsgCommand LoadSubscriptions
      in
        (model, Cmd.batch [comm1, comm2])

    ShowLogin ->
      (model, Navigation.newUrl R.loginPathHash)

view : Model -> Html Msg
view model =
  case model.commonModel.routeHistory of
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
