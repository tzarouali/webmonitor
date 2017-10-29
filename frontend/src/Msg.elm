module Msg exposing (..)

import Model exposing (..)
import Http
import Navigation


type Msg =
    UrlMsg UrlMsgType
  | LoginPageMsg LoginMsgType

type UrlMsgType =
    UrlChange Navigation.Location
  | LoginOk

type LoginMsgType =
    Email Email
  | Password String
  | LoginBtnClick
  | DoLogin (Result Http.Error UserLoginData)
