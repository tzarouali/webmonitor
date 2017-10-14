import Html exposing (Html, button, div, text)
import Html.Attributes exposing (..)
main =
  Html.program
    { init = init
    , update = update
    , subscriptions = subscriptions
    , view = view
    }


type Model =
    InitModel String

type Msg =
    TestMsg

init : (Model, Cmd Msg)
init = (InitModel "", Cmd.none)

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    case msg of
        TestMsg -> (model, Cmd.none)

subscriptions : Model -> Sub Msg
subscriptions model = Sub.none

view : Model -> Html Msg
view model =
    div [class "container"]
    [ text "hello!"
    ]