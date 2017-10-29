module Main exposing (..)

import Pages.LoginPage as LoginPage
import AppRouter exposing(..)
import Model exposing (..)
import Msg exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Navigation


main =
    Navigation.program (\ location -> UrlMsg (UrlChange location))
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }

init : Navigation.Location -> (Model, Cmd Msg)
init location =
  (
    { userDetails = {email = Nothing, password = Nothing, token = Nothing, userId = Nothing}
    , error = Nothing
    }
  , Cmd.none
  )

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlMsg msgType ->
      AppRouter.update msgType model

    LoginPageMsg msgType ->
      LoginPage.update msgType model

subscriptions : Model -> Sub Msg
subscriptions model = Sub.none

view : Model -> Html Msg
view model =
  LoginPage.view model
