module Pages.HomePage exposing (view, update, subscriptions)

import Msg exposing (..)
import Model exposing (..)
import Settings as S exposing (..)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Decode as D exposing (string, Decoder)
import Json.Decode.Pipeline as D exposing (decode, required)
import HttpBuilder as HT exposing (..)
import WebSocket
import Date exposing (..)
import Date.Format as DF exposing (..)
import Uuid as U exposing (..)
import Maybe.Extra as ME exposing (..)


view : Model -> Html Msg
view model =
  let
    html =
      div [class "col-md-12"]
      [
        generateSubscriptionHtml model
      ]
  in
    Html.map HomePageMsg html

generateSubscriptionHtml : Model -> Html HomePageMsgType
generateSubscriptionHtml model =
  case model.subscriptions of
    [] ->
      div [style [("font-size", "20px"), ("background", "red")]]
      [text "No subscriptions available!"]
    ss ->
      div [class "col-md-12"] <| List.map renderSubscription ss

renderSubscription : UserSubscription -> Html HomePageMsgType
renderSubscription s =
  div [class "col-md-3", style [("background", "#aabbcc"), ("text-align", "center"), ("margin", "3px")]]
  [ text s.name
  , br [] []
  , text "Last Value:"
  , br [] []
  , text (Maybe.withDefault "" (s.data |> Maybe.andThen (\d -> Just d.value)))
  , br [] []
  , text "Last updated:"
  , br [] []
  , text (Maybe.withDefault "" (s.data |> Maybe.andThen (\d -> Just (DF.format "%Y-%m-%d %H:%M:%S" d.lastUpdated))))
  , br [] []
  , div [class "form-horizontal,row"]
    [ button [ class "btn btn-default", onClick RefreshSingleSubscription] [ text "Refresh" ]
    , button [ class "btn btn-default", onClick (RetrieveSubscriptionDetail s.id)] [ text "Details" ]
    ]
  ]

update : HomePageMsgType -> Model -> (Model, Cmd Msg)
update msg model =
  case msg of
    NewSubscriptionValue value ->
      (updateSubscription model value, Cmd.none)
    LoadSubscriptions ->
      retrieveSubscriptions model
    RefreshSingleSubscription ->
      (model, Cmd.none)
    RetrieveSubscriptionDetail subId ->
      (model, Cmd.none)
    HttpGetSubscriptions (Ok subscriptions) ->
      ({model | subscriptions = subscriptions}, Cmd.none)
    HttpGetSubscriptions (Err e) ->
      ({model | homePageError = (Just (HomePageError LoadingSubscriptionsError))}, Cmd.none)

updateSubscription : Model -> String -> Model
updateSubscription model newSocketString =
  case parseSubscriptionIdFromSocketMsg newSocketString of
    Just subId ->
      let
        parts = List.partition (\s -> s.id == subId) model.subscriptions
        subToUpdate = List.head (Tuple.first parts)
      in
        case subToUpdate of
          Just s ->
            let
              newS = {s | data = parseSubscriptionValueFromString newSocketString}
              subsUpdated = [newS] ++ (Tuple.second parts)
              sortedSubs = List.sortBy .name subsUpdated
            in
              {model | subscriptions = sortedSubs}
          _ ->
            model
    _ ->
      model

parseSubscriptionIdFromSocketMsg : String -> Maybe Uuid
parseSubscriptionIdFromSocketMsg socketMessage =
  D.decodeString (D.field "subscriptionId" U.decoder) socketMessage |> Result.toMaybe

parseSubscriptionValueFromString : String -> Maybe SubscriptionValue
parseSubscriptionValueFromString socketMessage =
  let
    subId =
      D.decodeString (D.field "subscriptionId" U.decoder) socketMessage
    subValue =
      D.decodeString (D.field "value" D.string) socketMessage
    subUpdatedDate =
      Result.andThen Date.fromString (D.decodeString (D.field "lastUpdated" D.string) socketMessage)
  in
    case (subId, subValue, subUpdatedDate) of
      (Ok subId, Ok value, Ok date) ->
        Just ({subId = subId, value = value, lastUpdated = date})
      _ ->
        Nothing

retrieveSubscriptions : Model -> (Model, Cmd Msg)
retrieveSubscriptions model =
  let
    maybeToken = model.userDetails.token
    maybeUserId = Maybe.map U.toString model.userDetails.userId
    req = HT.get S.subscriptionsUri
          |> HT.withExpect (Http.expectJson subscriptionsDecoder)
          |> HT.withHeader S.tokenHeaderName (Maybe.withDefault "" maybeToken)
          |> HT.withHeader S.userIdHeaderName (Maybe.withDefault "" maybeUserId)
    cmd = Http.send HttpGetSubscriptions (HT.toRequest req)
  in
    (model, Cmd.map HomePageMsg cmd)

subscriptionsDecoder : D.Decoder (List UserSubscription)
subscriptionsDecoder =
  let
    subDecoder =
      D.decode UserSubscription
        |> D.required "id" U.decoder
        |> D.required "url" D.string
        |> D.required "jqueryExtractor" D.string
        |> D.required "userId" U.decoder
        |> D.required "name" D.string
        |> D.hardcoded Nothing
  in
    D.list subDecoder

subscriptions : Model -> List (Sub Msg)
subscriptions model =
  case model.subscriptions of
    [] ->
      [Sub.map HomePageMsg Sub.none]
    ss ->
      case (model.userDetails.token, model.userDetails.userId) of
        (Just token, Just userId) ->
          List.map (\s ->
            let
              socketUri = S.subscriptionSocketUri s.id userId token
              wsResult = WebSocket.listen socketUri NewSubscriptionValue
            in
              Sub.map HomePageMsg wsResult
            ) ss
        _ ->
          []