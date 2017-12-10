module Msg exposing (..)

import Model exposing (..)
import Http
import Navigation
import Task


type Msg =
    UrlMsg UrlMsgType
  | LoginPageMsg LoginPageMsgType
  | HomePageMsg HomePageMsgType

type UrlMsgType =
    UrlChange Navigation.Location
  | ShowHome
  | ShowLogin

type LoginPageMsgType =
    Email Email
  | Password Password
  | LoginBtnClick
  | HttpPostLogin (Result Http.Error UserLoginData)

type HomePageMsgType =
    LoadSubscriptions
  | RefreshSingleSubscription
  | NewSubscriptionValue String
  | RetrieveSubscriptionDetail SubscriptionId
  | HttpGetSubscriptions (Result Http.Error (List UserSubscription))

genUrlMsgCommand : UrlMsgType -> Cmd Msg
genUrlMsgCommand msgType =
  Task.succeed (UrlMsg msgType) |> Task.perform identity

genHomeMsgCommand : HomePageMsgType -> Cmd Msg
genHomeMsgCommand msgType =
  Task.succeed (HomePageMsg msgType) |> Task.perform identity
