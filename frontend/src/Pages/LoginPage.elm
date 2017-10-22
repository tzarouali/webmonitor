module Pages.LoginPage exposing (view, update)

import Msg exposing (..)
import Model exposing (..)
import Settings as S exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import Http
import HttpBuilder as HT exposing (..)
import Json.Encode as E exposing (string, object)
import Json.Decode as D exposing (int, string, float, Decoder)
import Json.Decode.Pipeline as D exposing (decode, required)



update : LoginMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    Email e ->
      let
        userDetails = model.userDetails
        newUser = {userDetails | email = Just e}
      in
        ({model | userDetails = newUser}, Cmd.none)
    Password p ->
      let
        userDetails = model.userDetails
        newPass = {userDetails | password = Just p}
      in
        ({model | userDetails = newPass}, Cmd.none)
    LoginBtnClick ->
      login model
    DoLogin (Ok userData) ->
      let
        userDetails = model.userDetails
        withUserID = {userDetails | userId = Just userData.userId}
      in
        let
          withToken = {withUserID | token = Just userData.token}
        in
          ({model | userDetails = withToken}, Cmd.none)
    DoLogin (Err e) ->
      ({model | error = (Just (LoginError FailedLogin))}, Cmd.none)


view : Model -> Html Msg
view model =
  let
    html =
      div [class "container"]
      [ div [style [("margin-top", "10%"), ("text-align", "center")]]
        [ div [class "form-group"]
          [ label [for "email"] [text "Email"]
          , input [ id "email", type_ "text", placeholder "Email",  onInput Email] []
          ]
        , div [class "form-group"]
          [ label [for "password"] [text "Password"]
          , input [ id "password", type_ "password", placeholder "Password", onInput Password] []
          ]
        , button [ class "button", onClick LoginBtnClick] [ text "Login" ]
        , generateErrorMessageLabel model
        ]
      ]
  in Html.map LoginPageMsg html


generateErrorMessageLabel : Model -> Html LoginMsgType
generateErrorMessageLabel model =
  let
    visibilityAndError =
      case model.error of
        Nothing -> ("hidden", "")
        Just (LoginError e) ->
          case e of
            EmailOrPasswordEmpty -> ("visible", "Email and password fields are required")
            FailedLogin -> ("visible", "Login error!")
  in
    label [id "loginErrorMessage", style [("visibility", (Tuple.first visibilityAndError))]] [text (Tuple.second visibilityAndError)]


login : Model -> (Model, Cmd Msg)
login model =
  case (model.userDetails.email, model.userDetails.password) of
    (Just u, Just p) ->
      let
        trimmedUser = String.trim u
        trimmedPass = String.trim p
      in
        if (String.isEmpty trimmedUser) || (String.isEmpty trimmedPass) then
          ({model | error = (Just (LoginError EmailOrPasswordEmpty))}, Cmd.none)
        else
          makeLoginHttpRequest model
    _ ->
      ({model | error = (Just (LoginError EmailOrPasswordEmpty))}, Cmd.none)


makeLoginHttpRequest : Model -> (Model, Cmd Msg)
makeLoginHttpRequest model =
  let
    theEmail = Maybe.withDefault "" model.userDetails.email
    thePass = Maybe.withDefault "" model.userDetails.password
    jsonBody = E.object [("email", E.string theEmail), ("password", E.string thePass)]
    req = HT.post S.loginUri
          |> HT.withExpect (Http.expectJson loginDecoder)
          |> HT.withJsonBody jsonBody
    cmd = Http.send DoLogin (HT.toRequest req)
  in
    (model, Cmd.map LoginPageMsg cmd)


loginDecoder : Decoder UserLoginData
loginDecoder =
  decode UserLoginData
    |> D.required "userId" D.string
    |> D.required "token" D.string
