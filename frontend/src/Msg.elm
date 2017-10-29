module Msg exposing (..)

import Model exposing (..)
import Http
import Navigation


type Msg =
    UrlChange Navigation.Location
  | LoginPageMsg LoginMsgType

type LoginMsgType =
    Email Email
  | Password String
  | LoginBtnClick
  | DoLogin (Result Http.Error UserLoginData)
