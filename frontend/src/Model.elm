module Model exposing (..)

import Navigation

type alias Model =
  { history : List Navigation.Location
  , userDetails : UserDetails
  , error : Maybe ApplicationError
  }

type alias Email = String
type alias Password = String

type alias UserDetails =
  { email : Maybe Email
  , password : Maybe Password
  , token : Maybe String
  , userId : Maybe String
  }

type ApplicationError =
  LoginError LoginErrorType

type LoginErrorType =
    EmailOrPasswordEmpty
  | FailedLogin

type alias UserLoginData =
  { userId : String
  , token: String
  }