module Main exposing (..)

import Pages.HomePage as HomePage
import Pages.LoginPage as LoginPage
import AppRouter exposing (..)
import Routes exposing (..)
import Model exposing (..)
import Msg exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Task
import Navigation


main =
  Navigation.program (\ location -> UrlMsg (UrlChange location))
  { init = init
  , view = view
  , update = update
  , subscriptions = subscriptions
  }

init : Navigation.Location -> (Model, Cmd Msg)
init _ =
  ( initModel
  , genUrlMsgCommand ShowLogin
  )

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlMsg msgType ->
      AppRouter.update msgType model

    LoginPageMsg msgType ->
      LoginPage.update msgType model

    HomePageMsg msgType ->
      HomePage.update msgType model

subscriptions : Model -> Sub Msg
subscriptions model =
  Sub.batch (HomePage.subscriptions model)

view : Model -> Html Msg
view model =
  AppRouter.view model
