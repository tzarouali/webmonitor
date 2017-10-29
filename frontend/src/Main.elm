module Main exposing (..)

import Pages.LoginPage as LoginPage
import Model exposing (..)
import Msg exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Navigation


main =
  Navigation.program UrlChange
  { init = init
  , view = view
  , update = update
  , subscriptions = subscriptions
  }

init : Navigation.Location -> (Model, Cmd Msg)
init location =
  (
    { history = [location]
    , userDetails = {email = Nothing, password = Nothing, token = Nothing, userId = Nothing}
    , error = Nothing
    }
  , Cmd.none
  )

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    UrlChange location ->
      ( {model | history = location :: model.history}
      , Cmd.none
      )
    LoginPageMsg msgType ->
      LoginPage.update msgType model

subscriptions : Model -> Sub Msg
subscriptions model = Sub.none

view : Model -> Html Msg
view model =
  LoginPage.view model
