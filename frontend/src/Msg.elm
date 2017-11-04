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
  | Password String
  | LoginBtnClick
  | DoLogin (Result Http.Error UserLoginData)

type HomePageMsgType =
  Hello

genUrlMsgCommand : UrlMsgType -> Cmd Msg
genUrlMsgCommand msgType =
  Task.succeed (UrlMsg msgType) |> Task.perform identity