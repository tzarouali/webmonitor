module Pages.HomePage exposing (view, update, subscriptions)

import Msg exposing (..)
import Model exposing (..)
import RestApiSettings as S exposing (..)
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
  case model.homePageModel.subscriptions of
    [] ->
      div [style [("font-size", "20px"), ("background", "red"), ("text-align", "center")]]
      [text "No subscriptions available!"]
    ss ->
      table [class "table", style [("width", "100%")]]
      [ thead []
        [ th [] [text "Subscription Name"]
        , th [] [text "Last Value"]
        , th [] [text "Last Updated"]
        ]
      , tbody [] <| List.map renderSubscription ss
      ]

renderSubscription : UserSubscription -> Html HomePageMsgType
renderSubscription s =
  let
    colorClass =
      Maybe.map (\ d -> if d.changed then "backgroundAnimated" else "") s.data |> Maybe.withDefault ""
  in
    tr []
    [ td [] [text s.name]
    , td [class colorClass] [text (Maybe.withDefault "" (s.data |> Maybe.andThen (\ d -> Just d.value)))]
    , td [class colorClass] [text (Maybe.withDefault "" (s.data |> Maybe.andThen (\ d -> Just (DF.format "%Y-%m-%d %H:%M:%S" d.lastUpdated))))]
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
      (model |> updateUserSubscriptions subscriptions, Cmd.none)
    HttpGetSubscriptions (Err e) ->
      (model |> updateHomePageError (Just (HomePageError LoadingSubscriptionsError)), Cmd.none)

updateSubscription : Model -> String -> Model
updateSubscription model newSocketString =
  case parseSubscriptionIdFromSocketMsg newSocketString of
    Just subId ->
      let
        parts = List.partition (\ s -> s.id == subId) model.homePageModel.subscriptions
        subToUpdate = List.head (Tuple.first parts)
      in
        case subToUpdate of
          Just s ->
            let
              newS = {s | data = parseSubscriptionValueFromString s.data newSocketString}
              subsUpdated = [newS] ++ (Tuple.second parts)
              sortedSubs = List.sortBy .name subsUpdated
            in
              model |> updateUserSubscriptions sortedSubs
          _ ->
            model
    _ ->
      model

parseSubscriptionIdFromSocketMsg : String -> Maybe Uuid
parseSubscriptionIdFromSocketMsg socketMessage =
  D.decodeString (D.field "subscriptionId" U.decoder) socketMessage |> Result.toMaybe

parseSubscriptionValueFromString : Maybe SubscriptionValue -> String -> Maybe SubscriptionValue
parseSubscriptionValueFromString prevData socketMessage =
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
        let
          changed = ME.unwrap True (\ d -> d.value /= value) prevData
        in
          Just ({subId = subId
                , value = value
                , lastUpdated = date
                , changed = changed
               })
      _ ->
        Nothing

retrieveSubscriptions : Model -> (Model, Cmd Msg)
retrieveSubscriptions model =
  let
    maybeToken = model.commonModel.userSession.token
    maybeUserId = Maybe.map U.toString model.commonModel.userSession.userId
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
        |> D.required "cssSelector" D.string
        |> D.required "userId" U.decoder
        |> D.required "name" D.string
        |> D.hardcoded Nothing
  in
    D.list subDecoder

subscriptions : Model -> List (Sub Msg)
subscriptions model =
  case model.homePageModel.subscriptions of
    [] ->
      [Sub.map HomePageMsg Sub.none]
    ss ->
      case (model.commonModel.userSession.token, model.commonModel.userSession.userId) of
        (Just token, Just userId) ->
          List.map (\ s ->
            let
              socketUri = S.subscriptionSocketUri s.id userId token
              wsResult = WebSocket.listen socketUri NewSubscriptionValue
            in
              Sub.map HomePageMsg wsResult
            ) ss
        _ ->
          []
