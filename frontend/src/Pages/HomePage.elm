module Pages.HomePage exposing (view, update)

import Msg exposing (..)
import Model exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)


view : Model -> Html Msg
view model =
  let
    html =
      div []
      [text "This is home!"
      ]
  in
    Html.map HomePageMsg html

update : HomePageMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  (model, Cmd.none)
