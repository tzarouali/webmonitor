module Pages.LoginPage exposing (view, update)

import Msg exposing (..)
import Model exposing (..)
import RestApiSettings as S exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import Http
import Task
import HttpBuilder as HT exposing (..)
import Json.Encode as E exposing (string, object)
import Json.Decode as D exposing (string, Decoder)
import Json.Decode.Pipeline as D exposing (decode, required)
import Uuid as U exposing (..)


update : LoginPageMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    Email e ->
      (model |> updateUserEmail e, Cmd.none)
    Password p ->
      (model |> updateUserPassword p, Cmd.none)
    LoginBtnClick ->
      login model
    HttpPostLogin (Ok userData) ->
      let
        modelWithUserIdAndToken =
          model
            |> updateUserId userData.userId
            |> updateUserToken userData.token
            |> updateLoginPageError Nothing
        commandLoginOkMsg =
          genUrlMsgCommand ShowHome
      in
        (modelWithUserIdAndToken, commandLoginOkMsg)
    HttpPostLogin (Err e) ->
      (model |> updateLoginPageError (Just (LoginPageError FailedLogin)), Cmd.none)

view : Model -> Html Msg
view model =
  let
    html =
      div [class "container"]
      [ div [class "col-md-4 col-md-offset-4", style [("margin-top", "10%")]]
        [ div [class "form-group"]
          [ label [for "email"] [text "Email"]
          , input [ id "email", type_ "text", class "form-control", placeholder "Email",  onInput Email] []
          ]
        , div [class "form-group"]
          [ label [for "password"] [text "Password"]
          , input [ id "password", type_ "password", class "form-control", placeholder "Password", onInput Password] []
          ]
        , div [style [("text-align", "center")]]
          [ button [ class "btn btn-default", onClick LoginBtnClick] [ text "Login" ]
          ]
        , br [] []
        , generateErrorMessageLabel model
        ]
      ]
  in Html.map LoginPageMsg html

generateErrorMessageLabel : Model -> Html LoginPageMsgType
generateErrorMessageLabel model =
  let
    visibilityAndError =
      case model.loginPageModel.loginPageError of
        Nothing -> ("hidden", "")
        Just (LoginPageError e) ->
          case e of
            EmailOrPasswordEmpty -> ("visible", "Email and password fields are required")
            FailedLogin -> ("visible", "Login error!")
  in
    div [style [("visibility", (Tuple.first visibilityAndError)), ("color", "red"), ("text-align", "center")]]
    [ text (Tuple.second visibilityAndError)
    ]

login : Model -> (Model, Cmd Msg)
login model =
  case (model.loginPageModel.email, model.loginPageModel.password) of
    (Just e, Just p) ->
      let
        userEmpty = String.isEmpty (String.trim e)
        passEmpty = String.isEmpty (String.trim p)
      in
        if (userEmpty || passEmpty) then
          (model |> updateLoginPageError (Just (LoginPageError EmailOrPasswordEmpty)), Cmd.none)
        else
          makeLoginHttpRequest model e p
    _ ->
      (model |> updateLoginPageError (Just (LoginPageError EmailOrPasswordEmpty)), Cmd.none)

makeLoginHttpRequest : Model -> Email -> Password -> (Model, Cmd Msg)
makeLoginHttpRequest m e p =
  let
    jsonBody = E.object [("email", E.string e), ("password", E.string p)]
    req = HT.post S.loginUri
          |> HT.withExpect (Http.expectJson loginDecoder)
          |> HT.withJsonBody jsonBody
    cmd = Http.send HttpPostLogin (HT.toRequest req)
  in
    (m, Cmd.map LoginPageMsg cmd)

loginDecoder : D.Decoder UserLoginData
loginDecoder =
  D.decode UserLoginData
    |> D.required "userId" U.decoder
    |> D.required "token" D.string
